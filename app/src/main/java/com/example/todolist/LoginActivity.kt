package com.example.todolist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.todolist.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Firebase auth instance
        auth = FirebaseAuth.getInstance()

        //Change Login Activity to Register Activity
        binding.tvMoveToRegister.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
            finish()
        }

        binding.loginBtn.setOnClickListener {
            when{
                //If email is empty
                TextUtils.isEmpty(binding.etLoginEmail.text.toString().trim{it <= ' '}) ->{
                    Toast.makeText(this@LoginActivity, "Email is Empty", Toast.LENGTH_SHORT).show()
                }

                //If password is empty
                TextUtils.isEmpty(binding.etLoginPassword.text.toString().trim{it <= ' '}) ->{
                    Toast.makeText(this@LoginActivity, "Password is Empty", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    //Variables to get data from filled blanks
                    val email: String = binding.etLoginEmail.text.toString().trim{it <= ' '}
                    val password: String = binding.etLoginPassword.text.toString().trim{it <= ' '}

                    // Login using Firebase
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            //If login success
                            if (task.isSuccessful){
                                //Send user in logged in activity(ToDoListActivity)
                                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                //Set info.
                                intent.putExtra("user_id", FirebaseAuth.getInstance().currentUser!!.uid)
                                intent.putExtra("email_id", email)
                                startActivity(intent)
                                finish()
                            }else{
                                //If login wasn't successful
                                Toast.makeText(this@LoginActivity, task.exception!!.message.toString(), Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }
        }



    }
    //check if user already logged in or not
    override fun onStart() {
        super.onStart()
        val firebaseUser = auth.currentUser
        if (firebaseUser != null){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

}