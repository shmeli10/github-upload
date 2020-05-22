package com.ostin.qrreader.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.ostin.qrreader.R
import com.ostin.qrreader.adapters.CODE_LIST_PAGE_INDEX
import com.ostin.qrreader.adapters.CODE_READER_PAGE_INDEX
import com.ostin.qrreader.adapters.ViewPagerAdapter
import com.ostin.qrreader.databinding.FragmentViewPagerBinding

class ViewPagerFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentViewPagerBinding.inflate(inflater, container, false)
        val tabLayout = binding.tabs
        val viewPager = binding.viewPager

        viewPager.adapter = ViewPagerAdapter(this)

        // Set the icon and text for each tab
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.setIcon(getTabIcon(position))
            tab.text = getTabTitle(position)
        }.attach()

        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)

        return binding.root
    }

    private fun getTabIcon(position: Int): Int {
        return when (position) {
            CODE_READER_PAGE_INDEX -> R.drawable.code_reader_tab_selector
            CODE_LIST_PAGE_INDEX -> R.drawable.code_list_tab_selector
            else -> throw IndexOutOfBoundsException()
        }
    }

    private fun getTabTitle(position: Int): String? {
        return when (position) {
            CODE_READER_PAGE_INDEX -> getString(R.string.code_reader_title)
            CODE_LIST_PAGE_INDEX -> getString(R.string.code_list_title)
            else -> null
        }
    }
}