package com.thekleaners.baseClasses



import android.os.Bundle

import android.support.v4.app.Fragment
import com.thekleaners.utils.Preferences
import com.thekleaners.activity.NavigationDrawer

abstract class BaseNavigationFragment : Fragment() {

    lateinit var mainActivity: NavigationDrawer
    lateinit var pref: Preferences
    // lateinit var homeActivity: MainActivity


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainActivity = activity as NavigationDrawer
        pref = Preferences().getInstance(activity!!)
//        homeActivity = activity as MainActivity

    }

}