package com.example.todolist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.todolist.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Change Register Activity to Login Activity
        binding.tvMoveToLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        binding.registerBtn.setOnClickListener {
            when{
                //If full name is empty
                TextUtils.isEmpty(binding.etRegistrationFullname.text.toString().trim{it <= ' '}) ->{
                    Toast.makeText(this@RegisterActivity, "Full Name is Empty", Toast.LENGTH_SHORT).show()
                }

                //If username is empty
                TextUtils.isEmpty(binding.etRegistrationUsername.text.toString().trim{it <= ' '}) ->{
                    Toast.makeText(this@RegisterActivity, "Username is Empty", Toast.LENGTH_SHORT).show()
                }

                //If email is empty
                TextUtils.isEmpty(binding.etRegistrationEmail.text.toString().trim{it <= ' '}) ->{
                    Toast.makeText(this@RegisterActivity, "Email is Empty", Toast.LENGTH_SHORT).show()
                }

                //If password is empty
                TextUtils.isEmpty(binding.etRegistrationPassword.text.toString().trim{it <= ' '}) ->{
                    Toast.makeText(this@RegisterActivity, "Password is Empty", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    //Variables to get data from filled blanks
                    val fullName: String = binding.etRegistrationFullname.text.toString().trim{it <= ' '}
                    val username: String = binding.etRegistrationUsername.text.toString().trim{it <= ' '}
                    val email: String = binding.etRegistrationEmail.text.toString().trim{it <= ' '}
                    val password: String = binding.etRegistrationPassword.text.toString().trim{it <= ' '}

                    // Register with email and password
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            //If registration success
                            if (task.isSuccessful){

                                //Register user in firebase
                                val firebaseUser: FirebaseUser = task.result!!.user!!

                                //Send user in loged in activity(ToDoListActivity)
                                val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                                //Set info.
                                intent.putExtra("user_id", firebaseUser.uid)
                                intent.putExtra("email_id", email)
                                startActivity(intent)
                                finish()
                            }else{
                                //If register wasn't successful
                                Toast.makeText(this@RegisterActivity, task.exception!!.message.toString(), Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }
        }



    }
}