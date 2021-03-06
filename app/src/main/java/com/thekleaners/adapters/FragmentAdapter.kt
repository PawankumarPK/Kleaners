package com.thekleaners.adapters


import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.thekleaners.fragments.Home
import com.thekleaners.fragments.CommercialServices


 class FragmentAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment? {
        when (position) {
            0 -> return Home()
            1 -> return CommercialServices()
        }
        return null
    }

    override fun getCount(): Int {
        return 2
    }


    override fun getPageTitle(position: Int): CharSequence? {
        when (position) {
        //
        //Your tab titles
        //
            0 -> return "Residential"
            1 -> return "Commercial"
            else -> return null
        }
    }
}