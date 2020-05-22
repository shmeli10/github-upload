package com.ostin.qrreader.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ostin.qrreader.home.qr_code.list.CodeListFragment
import com.ostin.qrreader.home.qr_code.reader.CodeReaderFragment

const val CODE_READER_PAGE_INDEX = 0
const val CODE_LIST_PAGE_INDEX = 1

class ViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    /**
     * Mapping of the ViewPager page indexes to their respective Fragments
     */
    private val tabFragmentsCreators: Map<Int, () -> Fragment> = mapOf(
        CODE_READER_PAGE_INDEX to { CodeReaderFragment() },
        CODE_LIST_PAGE_INDEX to { CodeListFragment() }
    )

    override fun getItemCount() = tabFragmentsCreators.size

    override fun createFragment(position: Int): Fragment {
        return tabFragmentsCreators[position]?.invoke() ?: throw IndexOutOfBoundsException()
    }
}