package com.bangkitacademy.nutrace.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import com.bangkitacademy.nutrace.MainActivity
import com.bangkitacademy.nutrace.R
import com.bangkitacademy.nutrace.customview.MyButton
import com.bangkitacademy.nutrace.customview.MyEmailEditText
import com.bangkitacademy.nutrace.customview.PasswordEditText
import com.bangkitacademy.nutrace.databinding.ActivityLoginBinding
import com.bangkitacademy.nutrace.ui.register.RegisterActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLoginBinding
    private lateinit var auth : FirebaseAuth

    private lateinit var myButton: MyButton
    private lateinit var myPasswordEditText: PasswordEditText
    private lateinit var myEmailEditText: MyEmailEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        auth = FirebaseAuth.getInstance()

        myButton = binding.loginButton
        myPasswordEditText = binding.passwordEditText
        myEmailEditText = binding.emailEditText
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

        setupAction()
        playAnimation()
    }

    private fun setupAction() {
        myButton.setOnClickListener {
            if (valid()) {
                val email = binding.emailEditText.text.toString()
                val password = binding.passwordEditText.text.toString()
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        startActivity(Intent(this, MainActivity::class.java))
                        finishAffinity()
                    } else {
                        Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }else {
                Toast.makeText(
                    this,
                    resources.getString(R.string.check_input),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.tvSignup.setOnClickListener{
            startActivity(Intent(this, RegisterActivity::class.java))
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
            binding.messageTextView,
            binding.emailTextView,
            binding.emailEditTextLayout,
            binding.passwordTextView,
            binding.passwordEditTextLayout,
            binding.loginButton,
            binding.tvDontHaveAcc,
            binding.tvSignup
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


    private fun valid(): Boolean {
        return binding.emailEditText.error == null &&
                binding.passwordEditText.error == null &&
                !binding.emailEditText.text.isNullOrEmpty() &&
                !binding.passwordEditText.text.isNullOrEmpty()
    }

    private fun setMyButtonEnable() {
        myButton.isEnabled = valid()
    }

}