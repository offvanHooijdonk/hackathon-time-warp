package by.epam.hackathon.timewarp.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import by.epam.hackathon.timewarp.R
import kotlinx.android.synthetic.main.fr_time_control.*
import java.util.*

class TimeControlFragment:Fragment() {
    private lateinit var receiverMin: BroadcastReceiver

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