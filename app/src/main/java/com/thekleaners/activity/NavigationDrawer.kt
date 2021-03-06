package com.thekleaners.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.util.DisplayMetrics
import android.view.*
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.thekleaners.BuildConfig
import com.thekleaners.R
import com.thekleaners.adapters.FragmentAdapter
import com.thekleaners.baseClasses.BaseActivity
import com.thekleaners.fragments.*
import kotlinx.android.synthetic.main.activity_navigation_drawer.*
import kotlinx.android.synthetic.main.app_bar_navigation_drawer.*
import kotlinx.android.synthetic.main.dialog_privacy.*
import kotlinx.android.synthetic.main.dialog_version.view.*
import kotlinx.android.synthetic.main.nav_header_navigation_drawer.*


class NavigationDrawer : BaseActivity(), NavigationView.OnNavigationItemSelectedListener, DrawerLocker {

    private var user_id: String? = null
    private var storageReference: StorageReference? = null
    private var firebaseAuth: FirebaseAuth? = null
    private var firebaseFirestore: FirebaseFirestore? = null

    private val displayRectangle = Rect()
    private var width = 0
    private lateinit var dialog: Dialog
    private lateinit var metrics: DisplayMetrics


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation_drawer)
        setSupportActionBar(toolbar)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }

        metrics = DisplayMetrics()
        this.window.decorView.getWindowVisibleDisplayFrame(displayRectangle)
        width = (displayRectangle.width() * 0.9f).toInt()
        dialog = Dialog(this)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()
        user_id = FirebaseAuth.getInstance().uid
        storageReference = FirebaseStorage.getInstance().reference

        //  supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        //  toolbar.visibility = View.VISIBLE
        // belowlayout()


        val navigationView = findViewById<View>(R.id.nav_view) as NavigationView
        val headerview = navigationView.getHeaderView(0)
        val header = headerview.findViewById(R.id.mLinearLayout) as RelativeLayout

        val pagerAdapter = FragmentAdapter(supportFragmentManager)
        mViewPager.adapter = pagerAdapter
        tabLayout.setupWithViewPager(mViewPager)

        /// setupTabIcons()

        header.setOnClickListener { signInListener() }
        getUserNameData()

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

    }

    override fun setDrawerLocked(shouldLock: Boolean) {
        if (shouldLock) {
            drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        } else {
            drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        }

    }


    override fun onBackPressed() {

        if (drawer_layout.isDrawerOpen(nav_view)) {
            drawer_layout.closeDrawer(GravityCompat.START)
            return
        } else if (fragmentManager.backStackEntryCount == 0) {
            tabLayout.visibility = View.VISIBLE
            toolbar.visibility = View.VISIBLE
            setDrawerLocked(false)
            super.onBackPressed()
        }
    }


    private fun exitApp(exit: Boolean) {
        AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle(resources.getString(R.string.exit))
            .setMessage(resources.getString(R.string.areyousureyouwanttoexit))
            .setPositiveButton(resources.getString(R.string.yes))
            { _, _ ->
                if (exit) finish() else super.onBackPressed()
            }
            .setNegativeButton(resources.getString(R.string.no), null).show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.navigation_drawer, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_feedback -> supportFragmentManager.beginTransaction().addToBackStack(null).replace(
                R.id.containerView,
                Feedback()
            ).commit()

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.mServices -> {
                if (FirebaseAuth.getInstance().currentUser == null)
                    supportFragmentManager.beginTransaction().replace(R.id.containerView, SignUpKleaners()).commit()
                else
                    supportFragmentManager.beginTransaction().replace(R.id.containerView, SelectServices()).commit()
            }
            R.id.mVisitSite -> {
                supportFragmentManager.beginTransaction().replace(R.id.containerView, VisitWebsite())
                    .addToBackStack(null).commit()
            }
            R.id.mAboutUs -> {
                supportFragmentManager.beginTransaction().replace(R.id.containerView, AboutUs()).commit()
            }
            R.id.mPolicy -> {
                policyDialog()
            }
            R.id.mHelp -> {
                supportFragmentManager.beginTransaction().replace(R.id.containerView, HelpCenter()).commit()
            }
            R.id.mVersion -> {
                versionDialog()
            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun signInListener(): Boolean {
        supportFragmentManager.beginTransaction().replace(R.id.containerView, SignUpKleaners())
            .addToBackStack(null).commit()

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    /*private fun belowlayout() {
        val params = containerView.layoutParams as CoordinatorLayout.LayoutParams
        params.behavior = AppBarLayout.ScrollingViewBehavior()
        containerView.requestLayout()
    }
*/
    private fun getUserNameData() {

        if (FirebaseAuth.getInstance().currentUser == null) {
            //fragmentManager!!.beginTransaction().replace(R.id.containerView, Profile()).commit()

            return

        } else {

            firebaseFirestore!!.collection("Users").document(user_id!!).get().addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    if (task.result!!.exists()) {

                        val name = task.result!!.getString("name")
                        val number = task.result!!.getString("number")

                        when {
                            mSignInTheKleaner == null -> {
                                return@addOnCompleteListener
                            }
                            mWhereHygieneMatters == null -> {
                                return@addOnCompleteListener
                            }

                            else -> {
                                mSignInTheKleaner.text = name
                                mWhereHygieneMatters.text = number
                            }
                        }
                    }

                } else {

                    val error = task.exception!!.message
                    Toast.makeText(this, "$error", Toast.LENGTH_LONG).show()

                }
                // profile_progress.visibility = View.INVISIBLE


                //setup_btn.isEnabled = true
            }
        }

    }

    @SuppressLint("InflateParams")
    private fun versionDialog() {
        val layout = LayoutInflater.from(this).inflate(R.layout.dialog_version, null)
        layout.minimumWidth = width
        val lp =
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        lp.setMargins(0, 20, 0, 0)
        layout.mVersion.text = "v.${BuildConfig.VERSION_NAME}"
        dialog.setContentView(layout)
        dialog.setCanceledOnTouchOutside(true)
        dialog.show()

    }

    @SuppressLint("InflateParams")
    private fun policyDialog() {
        val layout = LayoutInflater.from(this).inflate(R.layout.dialog_privacy, null)
        layout.minimumWidth = width
        val lp =
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        lp.setMargins(0, 20, 0, 0)
        dialog.setContentView(layout)
        dialog.mPrivacyPolicy.setOnClickListener {
            supportFragmentManager.beginTransaction().replace(R.id.containerView, PrivacyPoilcy()).addToBackStack(null)
                .commit()
            dialog.dismiss()
        }
        dialog.mTermsOfUses.setOnClickListener {
            supportFragmentManager.beginTransaction().replace(R.id.containerView, TermsOfUses()).addToBackStack(null)
                .commit()
            dialog.dismiss()
        }
        dialog.setCanceledOnTouchOutside(true)
        dialog.show()

    }

}

