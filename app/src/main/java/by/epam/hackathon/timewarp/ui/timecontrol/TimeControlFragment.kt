package by.epam.hackathon.timewarp.ui.timecontrol

import android.animation.LayoutTransition
import android.animation.ObjectAnimator
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Handler
import android.support.constraint.ConstraintLayout
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import by.epam.hackathon.timewarp.R
import by.epam.hackathon.timewarp.ui.main.MainActivity
import kotlinx.android.synthetic.main.fr_time_control.*
import java.util.*

class TimeControlFragment : Fragment(), MainActivity.FABClickListener {
    companion object {
        const val BALANCE_AUTO_TOGGLE_DELAY = 5000L
        const val DOUBLE_TAP_DELAY = 500L

        fun newInstance(fab: FloatingActionButton): TimeControlFragment {
            val fragment = TimeControlFragment()
            fragment.fabMain = fab
            return fragment
        }
    }

    private lateinit var receiverMin: BroadcastReceiver

    private lateinit var fabMain: FloatingActionButton
    private var elapsedWork: Int = 0
    private var elapsedRest: Int = 0
    private var workStatus: WorkStatus = WorkStatus.NOT_STARTED
    private var isPaused = false
    private var isBalanceVisible = false
    private var lastBalanceShowTime: Long = 0
    private var lastClockClickedTime: Long = 0

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater?.inflate(R.layout.fr_time_control, container, false)

    override fun onViewCreated(v: View?, savedInstanceState: Bundle?) {
        receiverMin = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (workStatus == WorkStatus.WORKING && !isPaused)
                    elapsedWork++
                else if (workStatus == WorkStatus.RESTING)
                    elapsedRest++
                drawCurrentTime(true)
                drawBalance()
            }
        }

        txtWorkStatus.text = getString(R.string.status_not_started)
        root.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        root.layoutTransition.enableTransitionType(LayoutTransition.APPEARING)

        drawBalance()
        blockTimeWork.setOnClickListener { showAndHideBalanceBar() }
        blockTimeRest.setOnClickListener { showAndHideBalanceBar() }

        clock.setOnClickListener { // TODO move to a method
            val time: Long = System.currentTimeMillis()
            if (time - lastClockClickedTime < DOUBLE_TAP_DELAY) {
                if (workStatus == WorkStatus.WORKING) {
                    if (isPaused) {
                        showPauseOverlay(false)
                        changeStatus(workStatus)
                    } else {
                        showPauseOverlay(true)
                        setupStatusBar(fabMain, R.drawable.ic_play_24, R.color.fab_paused, R.color.status_text_paused, R.string.status_paused)
                    }
                    isPaused = !isPaused
                }
            }
            lastClockClickedTime = time
        }

        registerMinReceiver()
    }

    override fun onResume() {
        super.onResume()

        drawCurrentTime(false)
    }

    override fun onDestroy() {
        super.onDestroy()

        unregisterMinReceiver()
    }

    override fun onFabClicked() {
        workStatus = if (!isPaused) {
            if (workStatus == WorkStatus.WORKING) {
                WorkStatus.RESTING
            } else {
                WorkStatus.WORKING
            }
        } else {
            showPauseOverlay(false)
            isPaused = false
            workStatus
        }

        changeStatus(workStatus)
    }

    fun getStatusViewStartPosition() = blockStatus.left

    private fun changeStatus(newStatus: WorkStatus) {
        val iconRes: Int
        val fabColorRes: Int
        val textColorRes: Int
        val statusTextRes: Int

        when (newStatus) {
            WorkStatus.RESTING -> {
                iconRes = R.drawable.ic_build_24
                fabColorRes = R.color.fab_resting
                textColorRes = R.color.status_text_resting
                statusTextRes = R.string.status_rest
            }
            WorkStatus.WORKING -> {
                iconRes = R.drawable.ic_weekend_24
                fabColorRes = R.color.fab_working
                textColorRes = R.color.status_text_working
                statusTextRes = R.string.status_working
            }
            WorkStatus.NOT_STARTED, WorkStatus.FINISHED -> {
                iconRes = R.drawable.ic_weekend_24
                fabColorRes = R.color.fab_working
                textColorRes = R.color.status_text_working
                statusTextRes = R.string.status_working
            }
        }

        setupStatusBar(fabMain, iconRes, fabColorRes, textColorRes, statusTextRes)
    }

    private fun setupStatusBar(fab: FloatingActionButton, iconRes: Int, fabColorRes: Int, textColorRes: Int, statusTextRes: Int) {
        fab.setImageResource(iconRes)
        fab.backgroundTintList = ColorStateList.valueOf(resources.getColor(fabColorRes))
        txtWorkStatus.text = getString(statusTextRes)
        txtWorkStatus.setTextColor(resources.getColor(textColorRes))
    }

    private fun drawBalance() { // TODO use constants and resources
        val calendar = Calendar.getInstance()
        calendar.timeZone = TimeZone.getTimeZone(TimeZone.getAvailableIDs(0)[0]) // TODO find better way to get 0
        calendar.timeInMillis = 60L * 1000 * (elapsedRest + elapsedWork)
        txtChipTimePassed.text = DateFormat.format("H:mm", calendar)
        val balancePerCent: Float = if (elapsedWork == 0 && elapsedRest == 0) 0.5f else elapsedWork.toFloat() / (elapsedWork + elapsedRest)
        (glBalance.layoutParams as ConstraintLayout.LayoutParams).guidePercent = balancePerCent

        var balanceTime: Float
        when {
            balancePerCent < 0.1f -> {
                balanceTime = 0.1f
                txtBalanceWork.text = null
                txtBalanceRest.text = toPerCentString(1 - balancePerCent)
            }
            balancePerCent > 0.9f -> {
                balanceTime = 0.9f
                txtBalanceWork.text = toPerCentString(balancePerCent)
                txtBalanceRest.text = null
            }
            else -> {
                balanceTime = balancePerCent
                txtBalanceWork.text = toPerCentString(balancePerCent)
                txtBalanceRest.text = toPerCentString(1 - balancePerCent)
            }
        }

        (glTimeElapsed.layoutParams as ConstraintLayout.LayoutParams).guidePercent = balanceTime
    }

    private fun showPauseOverlay(isShow: Boolean) {
        if (isShow){
            ObjectAnimator.ofFloat(clock, View.ALPHA, 1.0f, 0.5f).setDuration(300).start()
            ObjectAnimator.ofFloat(imgPlay, View.ALPHA, 0.0f, 0.5f).setDuration(300).start()
            //imgPlay.visibility = View.VISIBLE
        } else {
            ObjectAnimator.ofFloat(clock, View.ALPHA, 0.5f, 1.0f).setDuration(300).start()
            ObjectAnimator.ofFloat(imgPlay, View.ALPHA, 0.5f, 0.0f).setDuration(300).start()
            //imgPlay.visibility = View.GONE
        }
    }

    private fun toPerCentString(number: Float) = getString(R.string.perCentTemplate, Math.round(100 * number))

    private fun drawCurrentTime(isAnimate: Boolean) {
        val cal: Calendar = Calendar.getInstance()
        if (isAnimate) {
            clock.animateToTime(cal.get(Calendar.HOUR), cal.get(Calendar.MINUTE))
        } else {
            clock.hours = cal.get(Calendar.HOUR)
            clock.minutes = cal.get(Calendar.MINUTE)
        }
    }

    private fun registerMinReceiver() {
        activity.registerReceiver(receiverMin, IntentFilter(Intent.ACTION_TIME_TICK))
    }

    private fun unregisterMinReceiver() {
        activity.unregisterReceiver(receiverMin)
    }

    private fun showAndHideBalanceBar() {
        toggleBalanceBar()
        val showTime = Date().time
        lastBalanceShowTime = showTime
        Handler().postDelayed({
            if (lastBalanceShowTime == showTime && isBalanceVisible) {
                toggleBalanceBar()
            }
        }, BALANCE_AUTO_TOGGLE_DELAY)
    }

    private fun toggleBalanceBar() {
        val height: Int
        val visibility: Int
        if (isBalanceVisible) {
            height = resources.getDimensionPixelSize(R.dimen.balance_tab_height_min)
            visibility = View.GONE
        } else {
            height = resources.getDimensionPixelSize(R.dimen.balance_tab_height)
            visibility = View.VISIBLE
        }

        setupViewHeight(blockTimeWork, height)
        setupViewHeight(blockTimeRest, height)

        txtChipTimePassed.visibility = visibility
        txtBalanceWork.visibility = visibility
        txtBalanceRest.visibility = visibility

        isBalanceVisible = !isBalanceVisible
    }

    private fun setupViewHeight(view: View, height: Int) {
        val lParamsWork = view.layoutParams as ConstraintLayout.LayoutParams
        lParamsWork.height = height
        view.layoutParams = lParamsWork

    }

    private enum class WorkStatus {
        NOT_STARTED, WORKING, RESTING, FINISHED
    }

}