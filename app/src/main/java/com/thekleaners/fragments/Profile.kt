package com.thekleaners.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.thekleaners.BuildConfig
import com.thekleaners.R
import com.thekleaners.activity.NavigationDrawer
import com.thekleaners.baseClasses.BaseNavigationFragment
import kotlinx.android.synthetic.main.app_bar_navigation_drawer.*
import kotlinx.android.synthetic.main.dialog_logout.*
import kotlinx.android.synthetic.main.fragment_profile.*


class Profile : BaseNavigationFragment() {


    private val displayRectangle = Rect()
    private var width = 0
    private lateinit var dialog: Dialog
    private lateinit var metrics: DisplayMetrics

    private var user_id: String? = null
    private var storageReference: StorageReference? = null
    private var firebaseAuth: FirebaseAuth? = null
    private var firebaseFirestore: FirebaseFirestore? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        firebaseAuth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()
        user_id = FirebaseAuth.getInstance().uid
        storageReference = FirebaseStorage.getInstance().reference


        mainActivity = activity as NavigationDrawer
        mainActivity.toolbar.visibility = View.GONE
        mainActivity.tabLayout.visibility = View.GONE
        (activity as NavigationDrawer).setDrawerLocked(true)

        // ViewCompat.setNestedScrollingEnabled(mNestedScrollView, true)

        metrics = DisplayMetrics()
        mainActivity.window.decorView.getWindowVisibleDisplayFrame(displayRectangle)
        width = (displayRectangle.width() * 0.9f).toInt()
        mainActivity.windowManager.defaultDisplay.getMetrics(metrics)
        dialog = Dialog(mainActivity)
        mRelativeLayoutMyAddress.setOnClickListener { mRelativeLayoutMyAddressFunction() }
        mRelativeLayoutMyService.setOnClickListener { mRelativeLayoutMyServiceFunction() }
        mEditProfile.setOnClickListener { mEditProfileFunction() }
        mLogout.setOnClickListener { logoutDialog() }
        mProfileBackArrow.setOnClickListener { mProfileBackArrowFunction() }
        mRelativeLayoutQuery.setOnClickListener { mRelativeLayoutQueryFunction() }
        mRelativeLayoutMyInvoice.setOnClickListener { mRelativeLayoutMyInvoiceFunction() }
        mUsername.setOnClickListener { mEditProfileFunction() }
        mInvite.setOnClickListener {
            try {
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "TheKleaners")
                var shareMessage = "\nTheKleaners,where hygiene matters\n"
                shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" +
                        BuildConfig.APPLICATION_ID + "\n"
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                startActivity(Intent.createChooser(shareIntent, "choose one"))
            } catch (e: Exception) {
                //e.toString();
            }

        }



        profile_progress.visibility = View.VISIBLE
        mRelativeLayoutMyAddress.isEnabled = false
        mRelativeLayoutMyService.isEnabled = false
        mRelativeLayoutMyInvoice.isEnabled = false
        mRelativeLayoutQuery.isEnabled = false


        firebaseFirestore!!.collection("Users").document(user_id!!).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {

                if (task.result!!.exists()) {

                    val name = task.result!!.getString("name")
                    val surname = task.result!!.getString("email")
                    val number = task.result!!.getString("number")

                    when {
                        mUsername == null -> return@addOnCompleteListener
                        mUserEmail == null -> return@addOnCompleteListener
                        mUserNumber == null -> return@addOnCompleteListener
                        else -> {

                            mUsername.text = name
                            mUserEmail.text = surname
                            mUserNumber.text = number
                        }
                    }
                }

            } else {

                val error = task.exception!!.message
                Toast.makeText(context, "$error", Toast.LENGTH_LONG).show()

            }
            profile_progress.visibility = View.INVISIBLE
            mRelativeLayoutMyAddress.isEnabled = true
            mRelativeLayoutMyService.isEnabled = true
            mRelativeLayoutMyInvoice.isEnabled = true
            mRelativeLayoutQuery.isEnabled = true

            //setup_btn.isEnabled = true
        }

    }


    private fun mRelativeLayoutMyAddressFunction() {
        fragmentManager!!.beginTransaction().addToBackStack(null).replace(R.id.containerView, SavedAddress()).commit()
    }


    private fun mProfileBackArrowFunction() {
        val intent = Intent(mainActivity, NavigationDrawer::class.java)
        startActivity(intent)
    }

    private fun mRelativeLayoutMyServiceFunction() {

        if (pref.homeAndFlat) {
            fragmentManager!!.beginTransaction().addToBackStack(null).replace(R.id.containerView, SavedServices())
                .commit()
        } else {
            fragmentManager!!.beginTransaction().addToBackStack(null)
                .replace(R.id.containerView, SavedServiceForFarms()).commit()
        }

    }

    private fun mEditProfileFunction() {
        fragmentManager!!.beginTransaction().addToBackStack(null).replace(R.id.containerView, UserEditProfile())
            .commit()
    }

    private fun mRelativeLayoutMyInvoiceFunction() {
        fragmentManager!!.beginTransaction().addToBackStack(null).replace(R.id.containerView, MyInvoice()).commit()
    }

    private fun mRelativeLayoutQueryFunction() {
        fragmentManager!!.beginTransaction().addToBackStack(null).replace(R.id.containerView, MyQueries()).commit()
    }

    private fun mDialogLogoutFunction() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(mainActivity, NavigationDrawer::class.java)
        startActivity(intent)
        dialog.dismiss()
    }

    @SuppressLint("InflateParams")
    private fun logoutDialog() {
        val layout = LayoutInflater.from(mainActivity).inflate(R.layout.dialog_logout, null, false)
        layout.minimumWidth = width
        dialog.setContentView(layout)
        dialog.buttonLogout.setOnClickListener { mDialogLogoutFunction() }
        dialog.mCancelDialog.setOnClickListener { dialog.dismiss() }
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()

    }

}
