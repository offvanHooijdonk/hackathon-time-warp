package by.epam.hackathon.timewarp.ui

import android.animation.ArgbEvaluator
import android.animation.LayoutTransition
import android.animation.ValueAnimator
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.*
import by.epam.hackathon.timewarp.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_main.view.*


class MainActivity : AppCompatActivity() {

    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    companion object {
        const val TAB_INDEX_LIST = 0
        const val TAB_INDEX_TIME_CONTROL = 1
        const val TAB_INDEX_REPORT = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        container.adapter = mSectionsPagerAdapter

        mainContent.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)

        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), this.resources.getColor(R.color.clock_background), 0xFFFFFF) // TODO move to a method
        container.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                //Log.i("t-race", "position: $position, offset: $positionOffset, offsetPx: $positionOffsetPixels")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                    if (position == 1) {
                        colorAnimation.setCurrentFraction(positionOffset)
                        val color: Int = Color.parseColor(String.format("#%06X", 0xFFFFFF and colorAnimation.animatedValue as Int))
                        container.setBackgroundColor(color)
                    } else if (position == 0) {
                        colorAnimation.setCurrentFraction(1 - positionOffset)
                        val color: Int = Color.parseColor(String.format("#%06X", 0xFFFFFF and colorAnimation.animatedValue as Int))
                        container.setBackgroundColor(color)
                    }
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageSelected(position: Int) {}
        })

        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))

        tabs.addOnTabSelectedListener(TabSelectionListener())

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.action_settings) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {

            return when (position) {
                TAB_INDEX_TIME_CONTROL -> {
                    TimeControlFragment.newInstance()
                }
                else -> {
                    PlaceholderFragment.newInstance(position + 1)
                }
            }
        }

        override fun getCount(): Int {
            return tabs.tabCount
        }
    }

    private inner class TabSelectionListener : TabLayout.OnTabSelectedListener {
        private var prevPosition = 0

        override fun onTabSelected(tab: TabLayout.Tab?) {
            //val direction = tab!!.position - prevPosition
            when (tab!!.position) {
                TAB_INDEX_TIME_CONTROL -> {
                    //rotateButton(R.drawable.ic_play_24, direction)
                    //fab.setImageResource(R.drawable.ic_play_24)
                    move(true,R.drawable.ic_play_24)
                }
                TAB_INDEX_LIST -> {
                    //rotateButton(R.drawable.ic_add_24, direction)
                    move(false, R.drawable.ic_add_24)
                }
                TAB_INDEX_REPORT -> {
                    //rotateButton(R.drawable.ic_cloud_upload_24, direction)
                    move(false,R.drawable.ic_cloud_upload_24)
                }
            }
        }

        private fun move(isCenter: Boolean, imgNewRes: Int) { // TODO make enum
            val lParams: CoordinatorLayout.LayoutParams = (fab.layoutParams as CoordinatorLayout.LayoutParams)
            lParams.gravity = Gravity.BOTTOM or if (isCenter) Gravity.CENTER_HORIZONTAL else Gravity.END
            fab.layoutParams = lParams
            Handler().postDelayed({fab.setImageResource(imgNewRes)}, 100)
        }

        /*private fun rotateButton(imgNewRes: Int, direction: Int) {
            val anim: Animator = ObjectAnimator.ofFloat(fab, "rotation", 0f, direction * 45f).setDuration(100)
            anim.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    fab.setImageResource(imgNewRes)
                    ObjectAnimator.ofFloat(fab, "rotation", -direction * 45f, 0f).setDuration(100).start()
                }
            })
            anim.start()
        }*/

        override fun onTabReselected(tab: TabLayout.Tab?) {}

        override fun onTabUnselected(tab: TabLayout.Tab?) {prevPosition = tab!!.position}
    }

    @Deprecated("To be removed")
    class PlaceholderFragment : Fragment() {

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                  savedInstanceState: Bundle?): View? {
            val rootView = inflater.inflate(R.layout.fragment_main, container, false)
            rootView.txtSectionLabel.text = getString(R.string.section_format, arguments.getInt(ARG_SECTION_NUMBER))
            return rootView
        }

        companion object {
            private val ARG_SECTION_NUMBER = "section_number"

            fun newInstance(sectionNumber: Int): PlaceholderFragment {
                val fragment = PlaceholderFragment()
                val args = Bundle()
                args.putInt(ARG_SECTION_NUMBER, sectionNumber)
                fragment.arguments = args
                return fragment
            }
        }
    }
}
