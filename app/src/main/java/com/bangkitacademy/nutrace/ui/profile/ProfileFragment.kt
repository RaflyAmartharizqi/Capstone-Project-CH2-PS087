package com.bangkitacademy.nutrace.ui.profile

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bangkitacademy.nutrace.R
import com.bangkitacademy.nutrace.ui.login.LoginActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import de.hdodenhof.circleimageview.CircleImageView

class ProfileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth

    private lateinit var nameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var profileImageView: CircleImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        nameTextView = view.findViewById(R.id.tv_name)
        emailTextView = view.findViewById(R.id.tv_username)
        profileImageView = view.findViewById(R.id.iv_profile)

        val sharedPrefs = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        val userName = sharedPrefs.getString("userName", null)
        nameTextView.text = "$userName"

        val user = FirebaseAuth.getInstance().currentUser
        emailTextView.text = "${user?.email}"

        val userProfileImageUrl = sharedPrefs.getString("userProfileImageUrl", null)
        if (userProfileImageUrl != null) {
            Glide.with(this)
                .load(userProfileImageUrl)
                .placeholder(R.drawable.profile_default)
                .error(R.drawable.profile_default)
                .into(profileImageView)
        } else {
            Glide.with(this)
                .load(R.drawable.profile_default)
                .into(profileImageView)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            profileImageReceiver,
            IntentFilter("UPDATE_PROFILE_IMAGE")
        )

        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            profileNameReceiver,
            IntentFilter("UPDATE_PROFILE_NAME")
        )

        auth = FirebaseAuth.getInstance()

        val logoutButton: Button = view.findViewById(R.id.logoutButton)
        logoutButton.setOnClickListener {
            signOut()
        }

        val editProfileButton: Button = view.findViewById(R.id.editProfilButton)
        editProfileButton.setOnClickListener {
            editProfile()
        }

    }

    private val profileImageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                if (it.action == "UPDATE_PROFILE_IMAGE") {
                    val editedImageUri = it.getStringExtra("editedImageUri")
                    updateProfileImage(editedImageUri)
                }
            }
        }
    }

    private val profileNameReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                if (it.action == "UPDATE_PROFILE_NAME") {
                    val editedName = it.getStringExtra("editedName")
                    updateProfileName(editedName)
                }
            }
        }
    }

    private fun updateProfileName(editedName: String?) {
        editedName?.let {
            nameTextView.text = it

            val sharedPrefs = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            sharedPrefs.edit {
                putString("userName", it)
                apply()
            }
        }
    }

    fun updateProfileImage(imageUri: String?) {
        imageUri?.let {
            Glide.with(this)
                .load(it)
                .into(profileImageView)

            val sharedPrefs = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            sharedPrefs.edit {
                putString("userProfileImageUrl", it)
                apply()
            }
        }
    }

    override fun onDestroyView() {
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(profileImageReceiver)
        super.onDestroyView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == EDIT_PROFILE_REQUEST && resultCode == Activity.RESULT_OK) {
            val editedImageUri = data?.getStringExtra("editedImageUri")

            Glide.with(this)
                .load(editedImageUri)
                .placeholder(R.drawable.profile_default)
                .error(R.drawable.profile_default)
                .into(profileImageView)

            val editedName = data?.getStringExtra("editedName")
            updateProfileName(editedName)
        }
    }

    private fun signOut() {
        auth.signOut()

        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent)
        requireActivity().finishAffinity()
    }

    private fun editProfile() {

        val intent = Intent(requireContext(), EditProfileActivity::class.java)

        val userName = nameTextView.text.toString()
        intent.putExtra("currentUserName", userName)

        startActivityForResult(intent, EDIT_PROFILE_REQUEST)
    }

    companion object {
        private const val EDIT_PROFILE_REQUEST = 100
    }

}
