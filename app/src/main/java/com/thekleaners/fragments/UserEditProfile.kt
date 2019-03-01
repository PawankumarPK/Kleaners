package com.thekleaners.fragments

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.thekleaners.R
import com.thekleaners.activity.NavigationDrawer
import com.thekleaners.baseClasses.BaseNavigationFragment
import kotlinx.android.synthetic.main.app_bar_navigation_drawer.*
import kotlinx.android.synthetic.main.fragment_user_edit_profile.*
import java.util.*
import java.util.regex.Pattern

class UserEditProfile : BaseNavigationFragment() {

    private var user_id: String? = null
    private var storageReference: StorageReference? = null
    private var firebaseAuth: FirebaseAuth? = null
    private var firebaseFirestore: FirebaseFirestore? = null

     private val EMAIL_REGEX = "^[\\w-+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-z]{2,})$"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_user_edit_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mEditProfileBackArrow.setOnClickListener { mEditProfileBackArrowFunction() }

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()
        user_id = FirebaseAuth.getInstance().uid
        storageReference = FirebaseStorage.getInstance().reference


        mainActivity = activity as NavigationDrawer
        mainActivity.toolbar.visibility = View.GONE
        mainActivity.tabLayout.visibility = View.GONE
        (activity as NavigationDrawer).setDrawerLocked(true)

        if (firebaseFirestore == null)
            setup_progress.visibility = View.INVISIBLE
        else
            setup_progress.visibility = View.VISIBLE
        setup_btn.isEnabled = false


        firebaseFirestore!!.collection("Users").document(user_id!!).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {

                if (task.result!!.exists()) {

                    val name = task.result!!.getString("name")
                    val surname = task.result!!.getString("email")
                    val number = task.result!!.getString("number")

                    when {
                        setup_name == null -> return@addOnCompleteListener
                        setup_surname == null -> return@addOnCompleteListener
                        setup_number == null -> return@addOnCompleteListener
                        else -> {
                            setup_name.setText(name)
                            setup_surname.setText(surname)
                            setup_number.setText(number)
                        }
                    }

                }

            } else {

                val error = task.exception!!.message
                Toast.makeText(context, "$error", Toast.LENGTH_LONG).show()

            }
            setup_progress.visibility = View.INVISIBLE
            setup_btn.isEnabled = true
        }


        setup_btn.setOnClickListener {
            val user_name = setup_name.text.toString()
            val surname_name = setup_surname.text.toString()
            val number_name = setup_number.text.toString()

            if (!checkValidField())
            return@setOnClickListener

            when {
                setup_name.text.toString().isEmpty() -> {
                    setup_name.error = "Empty"
                    return@setOnClickListener
                }
                setup_surname.text.toString().isEmpty() -> {
                    setup_surname.error = "Empty"
                    return@setOnClickListener

                }
                setup_number.text.toString().isEmpty() -> {
                    setup_number.error = "Empty"
                    return@setOnClickListener
                }
                else -> {

                    setup_progress.visibility = View.VISIBLE

                    storeFirestore(null, user_name, surname_name, number_name)
                }

            }
        }


    }

    private fun checkValidField(): Boolean {
        val isValid: Boolean
        val p = Pattern.compile(EMAIL_REGEX)
        val m = p.matcher(setup_surname.text.toString())
        isValid = m.find()
        if (!isValid) {
            Toast.makeText(context, "Invalid Email", Toast.LENGTH_LONG).show()
            return isValid
        }
        return isValid
    }


    private fun storeFirestore(
        task: Task<UploadTask.TaskSnapshot>?,
        user_name: String,
        surname: String,
        number: String
    ) {

        val userMap = HashMap<String, String>()
        userMap["name"] = user_name
        userMap["email"] = surname
        userMap["number"] = number

        firebaseFirestore!!.collection("Users").document(user_id!!).set(userMap as Map<String, Any>)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "User Settings are updated", Toast.LENGTH_SHORT).show()

                    fragmentManager!!.beginTransaction().addToBackStack(null).replace(R.id.containerView, Profile())
                        .commit()


                }
            }
    }


    private fun mEditProfileBackArrowFunction() {
        fragmentManager!!.beginTransaction().addToBackStack(null).replace(R.id.containerView, Profile()).commit()
    }
}
