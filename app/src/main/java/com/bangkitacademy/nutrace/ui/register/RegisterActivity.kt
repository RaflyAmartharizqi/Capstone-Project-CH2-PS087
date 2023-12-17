package com.bangkitacademy.nutrace.ui.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import com.bangkitacademy.nutrace.R
import com.bangkitacademy.nutrace.customview.InputNameEditText
import com.bangkitacademy.nutrace.customview.MyButton
import com.bangkitacademy.nutrace.customview.MyEmailEditText
import com.bangkitacademy.nutrace.customview.PasswordEditText
import com.bangkitacademy.nutrace.databinding.ActivityRegisterBinding
import com.bangkitacademy.nutrace.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth : FirebaseAuth

    private lateinit var myButton: MyButton
    private lateinit var myPasswordEditText: PasswordEditText
    private lateinit var myEmailEditText: MyEmailEditText
    private lateinit var myNameEditText: InputNameEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        auth = FirebaseAuth.getInstance()

        myButton = binding.signupButton
        myPasswordEditText = binding.passwordEditText
        myEmailEditText = binding.emailEditText
        myNameEditText= binding.nameEditText
        setMyButtonEnable()

        myPasswordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setMyButtonEnable()
            }
            override fun afterTextChanged(s: Editable) {
            }
        })

        myEmailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setMyButtonEnable()
            }
            override fun afterTextChanged(s: Editable) {
            }
        })

        myNameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setMyButtonEnable()
            }
            override fun afterTextChanged(s: Editable) {
            }
        })

        setupAction()
        playAnimation()
    }


    private fun setupAction() {
        binding.tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        myButton.setOnClickListener {
            if (valid()) {
                val name = binding.nameEditText.text.toString()
                val email = binding.emailEditText.text.toString()
                val password = binding.passwordEditText.text.toString()

                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val user = FirebaseAuth.getInstance().currentUser

                            showLoadingIndicator(false)
                            Toast.makeText(
                                this,
                                "Registration successful. Welcome, $name!",
                                Toast.LENGTH_SHORT
                            ).show()

                            val sharedPrefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                            val editor = sharedPrefs.edit()
                            editor.putString("userName", name)
                            editor.apply()

                            startActivity(Intent(this, LoginActivity::class.java))
                            finishAffinity()
                        } else {
                            Toast.makeText(
                                this,
                                "Registration failed. ${task.exception?.message}",
                                Toast.LENGTH_SHORT
                            ).show()

                            showLoadingIndicator(false)
                        }
                    }
            } else {
                Toast.makeText(
                    this,
                    resources.getString(R.string.check_input),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val elements = listOf(
            binding.titleTextView,
            binding.nameTextView,
            binding.nameEditTextLayout,
            binding.emailTextView,
            binding.emailEditTextLayout,
            binding.passwordTextView,
            binding.passwordEditTextLayout,
            binding.signupButton,
            binding.tvHaveAcc,
            binding.tvLogin
        )

        val animatorSet = AnimatorSet()
        val animations = elements.map {
            ObjectAnimator.ofFloat(it, View.ALPHA, 1f).apply {
                duration = 100
            }
        }

        animatorSet.playSequentially(animations)
        animatorSet.start()
    }


    private fun showLoadingIndicator(isLoad: Boolean) {
        binding.progressBar.visibility = if (isLoad) View.VISIBLE else View.GONE
    }

    private fun valid(): Boolean {
        val emailValid = binding.emailEditText.error == null && !binding.emailEditText.text.isNullOrEmpty()
        val passwordValid = binding.passwordEditText.error == null && !binding.passwordEditText.text.isNullOrEmpty()
        val nameValid = binding.nameEditText.error == null && !binding.nameEditText.text.isNullOrEmpty()

        return emailValid && passwordValid && nameValid
    }

    private fun setMyButtonEnable() {
        myButton.isEnabled = valid()
    }
}