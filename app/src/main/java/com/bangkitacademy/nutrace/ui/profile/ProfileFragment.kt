package com.bangkitacademy.nutrace.ui.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
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

        val userProfileImageUrl = user?.photoUrl?.toString()
        Glide.with(this)
            .load(userProfileImageUrl)
            .placeholder(R.drawable.profile_dafault)
            .error(R.drawable.profile_dafault)
            .into(profileImageView)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        val logoutButton: Button = view.findViewById(R.id.logoutButton)
        logoutButton.setOnClickListener {
            signOut()
        }
    }

    private fun signOut() {
        auth.signOut()

        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent)
        requireActivity().finishAffinity()
    }

}
