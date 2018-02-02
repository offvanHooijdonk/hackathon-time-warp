package by.epam.hackathon.timewarp.ui

import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity
import android.view.*
import by.epam.hackathon.timewarp.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_main.view.*

class MainActivity : AppCompatActivity() {

    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    companion object {
        val TAB_INDEX_LIST = 0
        val TAB_INDEX_TIME_CONTROL = 1
        val TAB_INDEX_REPORT = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        container.adapter = mSectionsPagerAdapter

        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))

        tabs.addOnTabSelectedListener(TabSelectionListener(this))

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

    private inner class TabSelectionListener(val ctx: Context) : TabLayout.OnTabSelectedListener {

        override fun onTabSelected(tab: TabLayout.Tab?) {
            fab.rotation = 0f
            when (tab?.position) {
                TAB_INDEX_TIME_CONTROL -> {
                    fab.hide()
                }
                TAB_INDEX_LIST -> {
                    if (fab.visibility == View.VISIBLE) {
                        rotateButton(0f, -90f, R.drawable.ic_add_24)
                    } else {
                        fab.setImageResource(R.drawable.ic_add_24)
                        fab.show()
                    }
                }
                TAB_INDEX_REPORT -> {
                    if (fab.visibility == View.VISIBLE) {
                        rotateButton(-90f, 0f, R.drawable.ic_cloud_upload_24)
                    } else {
                        fab.setImageResource(R.drawable.ic_cloud_upload_24)
                        fab.show()
                    }
                }
            }
        }

        private fun rotateButton(angleFrom: Float, angleTo: Float, imgNewRes: Int) {
            ObjectAnimator.ofFloat(fab, "rotation", angleFrom, angleTo).setDuration(200).start();
            Handler().postDelayed({ fab.setImageResource(imgNewRes) }, 100);
        }

        override fun onTabReselected(tab: TabLayout.Tab?) {}

        override fun onTabUnselected(tab: TabLayout.Tab?) {}
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
