package by.epam.hackathon.timewarp.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import by.epam.hackathon.timewarp.R
import kotlinx.android.synthetic.main.fr_time_control.*
import java.util.*

class TimeControlFragment:Fragment() {
    private lateinit var receiverMin: BroadcastReceiver

    private var elapsedWork: Int = 0
    private var elapsedRest: Int = 0

    companion object {
        fun newInstance(): TimeControlFragment {
            val fragment = TimeControlFragment()
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater?.inflate(R.layout.fr_time_control, container, false)

    override fun onViewCreated(v: View?, savedInstanceState: Bundle?) {
        receiverMin = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                drawCurrentTime(true)
            }
        }
        val rand = Random()
        elapsedWork = rand.nextInt(6) * 60 + rand.nextInt(60)
        elapsedRest = rand.nextInt(4) * 60 + rand.nextInt(60)
        drawBalance()

    }

    override fun onResume() {
        super.onResume()

        drawCurrentTime(false)
        registerMinReceiver()
    }

    override fun onPause() {
        super.onPause()

        unregisterMinReceiver()
    }

    private fun drawBalance() { // TODO use constants and resources
        txtChipTimePassed.text = DateFormat.format("HH:mm", 60L * 1000 * (elapsedRest + elapsedWork))
        val balancePerCent = elapsedWork.toFloat() / (elapsedWork + elapsedRest)
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

}