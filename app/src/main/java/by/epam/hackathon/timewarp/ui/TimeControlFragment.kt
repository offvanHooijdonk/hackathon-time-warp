package by.epam.hackathon.timewarp.ui

import android.animation.LayoutTransition
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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
import kotlinx.android.synthetic.main.fr_time_control.*
import java.util.*

class TimeControlFragment : Fragment(), MainActivity.FABClickListener {
    companion object {
        const val BALANCE_AUTO_TOGGLE_DELAY = 5000L

        fun newInstance(): TimeControlFragment {
            val fragment = TimeControlFragment()
            return fragment
        }
    }

    private lateinit var receiverMin: BroadcastReceiver

    private var elapsedWork: Int = 0
    private var elapsedRest: Int = 0
    private var isWorking = false
    private var isActive = false
    private var isBalanceVisible = false
    private var lastBalanceShowTime: Long = 0

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater?.inflate(R.layout.fr_time_control, container, false)

    override fun onViewCreated(v: View?, savedInstanceState: Bundle?) {
        receiverMin = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (isActive)
                    if (isWorking) elapsedWork++ else elapsedRest++
                drawCurrentTime(true)
                drawBalance()
            }
        }
        /*val rand = Random()
        elapsedWork = rand.nextInt(6) * 60 + rand.nextInt(60)
        elapsedRest = rand.nextInt(4) * 60 + rand.nextInt(60)*/
        root.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        root.layoutTransition.enableTransitionType(LayoutTransition.APPEARING)

        drawBalance()
        blockTimeWork.setOnClickListener { showAndHideBalanceBar() }
        blockTimeRest.setOnClickListener { showAndHideBalanceBar() }

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

    override fun onFabClicked(fab: FloatingActionButton) {
        if (isActive) {
            if (isWorking) {
                fab.setImageResource(R.drawable.ic_build_24)
                isWorking = false
            } else {
                fab.setImageResource(R.drawable.ic_weekend_24)
                isWorking = true
            }
        } else {
            fab.setImageResource(R.drawable.ic_weekend_24)
            isActive = true
            isWorking = true
        }
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

}