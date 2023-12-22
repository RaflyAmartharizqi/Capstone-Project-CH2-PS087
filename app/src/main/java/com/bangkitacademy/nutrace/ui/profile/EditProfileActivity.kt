package com.bangkitacademy.nutrace.ui.profile

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bangkitacademy.nutrace.R
import com.bangkitacademy.nutrace.databinding.ActivityEditProfileBinding
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage

class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var imgUri: Uri
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var profileFragment: ProfileFragment

    private var permissionsGranted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        binding.tvEditFoto.setOnClickListener {
            openGallery()
        }

        val savedUserName = sharedPreferences.getString("userName", "")
        binding.editName.setText(savedUserName)

        val saveButton: Button = findViewById(R.id.saveButton)
        saveButton.setOnClickListener {

            if (::imgUri.isInitialized) {
                uploadImgToFirebase(imgUri)
            } else {
                Log.e("EditProfileActivity", "No image selected")
            }

            val editedName = binding.editName.text.toString()
            updateUserName(editedName)
            if (::imgUri.isInitialized) {
                updateProfileImage(imgUri)
            }

            Toast.makeText(this, "Profile saved successfully", Toast.LENGTH_SHORT).show()
        }

        val savedImageURL = sharedPreferences.getString("profileImageURL", null)
        savedImageURL?.let {
            with(binding) {
                Glide.with(this@EditProfileActivity)
                    .load(it)
                    .into(ivProfile)
            }
        }

        val sharedPrefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val defaultName = sharedPrefs.getString("userName", "")
        binding.editName.setText(defaultName)

        profileFragment = ProfileFragment()

    }

    private fun openGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            val selectedImageUri = data.data
            val editedName = data.getStringExtra("editedName")

            selectedImageUri?.let {
                with(binding) {
                    Glide.with(this@EditProfileActivity)
                        .load(it)
                        .into(ivProfile)
                }
                imgUri = it
            }

            if (!editedName.isNullOrBlank()) {
                updateUserName(editedName)
            }

        }

    }

    private fun uploadImgToFirebase(selectedImageUri: Uri?) {
        if (selectedImageUri == null) {
            Log.e("uploadImgToFirebase", "No image selected")
            return
        }

        val ref =
            FirebaseStorage.getInstance().reference.child("img_user/${FirebaseAuth.getInstance().currentUser?.email}")

        ref.putFile(selectedImageUri)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    ref.downloadUrl.addOnCompleteListener { Task ->
                        Task.result.let { Uri ->
                            imgUri = Uri
                            binding.ivProfile.setImageURI(selectedImageUri)
                        }
                    }
                }
            }
            .addOnFailureListener {
                Log.e("uploadImgToFirebase", "Error uploading image: ${it.message}")
            }
    }

    private fun updateUserName(editedName: String) {
        sharedPreferences.edit {
            putString("userName", editedName)
            apply()
        }

        val intent = Intent("UPDATE_PROFILE_NAME")
        intent.putExtra("editedName", editedName)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    private fun updateProfileImage(imgUri: Uri) {
        sharedPreferences.edit {
            putString("profileImageURL", imgUri.toString())
            apply()
        }

        val intent = Intent("UPDATE_PROFILE_IMAGE")
        intent.putExtra("editedImageUri", imgUri.toString())
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PICK_IMAGE_REQUEST) {
            if (allPermissionsGranted()) {
                openGallery()
            } else {
                Toast.makeText(
                    this,
                    getString(R.string.didnt_get_permission),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun requestPermissions() {
        if (!permissionsGranted) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                PICK_IMAGE_REQUEST
            )
        }
    }

    private fun allPermissionsGranted(): Boolean {
        val result = REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
        }
        permissionsGranted = result
        return result
    }

    companion object {
        private const val PICK_IMAGE_REQUEST = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

}