package com.thekleaners.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import com.thekleaners.R
import com.thekleaners.activity.NavigationDrawer
import com.thekleaners.baseClasses.BaseNavigationFragment
import kotlinx.android.synthetic.main.app_bar_navigation_drawer.*
import kotlinx.android.synthetic.main.fragment_car_categories.*


class CarCategories : BaseNavigationFragment() {

    var name: Boolean = true
    var value: Double = 0.toDouble()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_car_categories, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainActivity = activity as NavigationDrawer
        mainActivity.toolbar.visibility = View.GONE
        mainActivity.tabLayout.visibility = View.GONE
        (activity as NavigationDrawer).setDrawerLocked(true)

        radioShifts.setOnCheckedChangeListener(radioListener)
        mCarServiceBackArrow.setOnClickListener { mCarServiceBackArrowFunction() }
        //mDemoButton.visibility = View.GONE

    }

    @SuppressLint("SetTextI18n")
    private val radioListener = RadioGroup.OnCheckedChangeListener { _, checkedId ->

        when (checkedId) {
            R.id.mHatchback -> {
                carType.text = "HATCHBACK"
                carAmountData.text = "1999"
                carSingleAmount.text = "6.67"
                // mHatchback.startAnimation(AnimationUtils.loadAnimation(context, R.anim.image_button))
                demoFun()
            }
            R.id.mSedan -> {
                carType.text = "SEDAN"
                carAmountData.text = "2199"
                carSingleAmount.text = "10"
                // mSedan.startAnimation(AnimationUtils.loadAnimation(context, R.anim.image_button))
                demoFun()
            }
            R.id.mSuvMuv -> {
                carType.text = "SUV/MUV"
                carAmountData.text = "2499"
                carSingleAmount.text = "16.67"
                // mSuvMuv.startAnimation(AnimationUtils.loadAnimation(context, R.anim.image_button))
                demoFun()
            }
            R.id.mPrimieryLuxury -> {
                carType.text = "LUXURY"
                carAmountData.text = "3499"
                carSingleAmount.text = "23.33"
                // mPrimieryLuxury.startAnimation(AnimationUtils.loadAnimation(context, R.anim.image_button))
                demoFun()
            }

            R.id.mLuv -> {
                carType.text = "LUV"
                carAmountData.text = "4199"
                carSingleAmount.text = "13.33"
                // mLuv.startAnimation(AnimationUtils.loadAnimation(context, R.anim.image_button))
                demoFun()
            }
        }
    }

    private fun demoFun() {
        // val singleAmount = Integer.parseInt(carSingleAmount.text.toString())


        val args = Bundle()
        args.putString("doctor_id", carType.text.toString())
        args.putString("doctor_carAmount", carAmountData.text.toString())
        args.putDouble("doctor_carSingleAmount", carSingleAmount.text.toString().toDouble())
        // args.putExtra("MY_KEY", 15);

        val newFragment = CarDetails()
        newFragment.arguments = args

        fragmentManager!!.beginTransaction().replace(R.id.containerView, newFragment).commit()
    }

    private fun mCarServiceBackArrowFunction() {
        fragmentManager!!.beginTransaction().addToBackStack(null).replace(R.id.containerView, CarCleaning()).commit()
    }

}



