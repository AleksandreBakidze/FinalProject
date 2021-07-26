package com.example.todolistsweeftdigital

/*
*   @Aleksandre Bakidze
*   Created on -> 26.07.2021
*/

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todolistsweeftdigital.databinding.ActivityMainBinding
import com.example.todolistsweeftdigital.model.UserData
import com.example.todolistsweeftdigital.view.UserAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var addsBtn: FloatingActionButton
    private lateinit var recv: RecyclerView
    private lateinit var userList: ArrayList<UserData>
    private lateinit var c: Context
    private lateinit var userAdapter: UserAdapter
    private lateinit var database: DatabaseReference
    private lateinit var v: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //set list
        userList = arrayListOf<UserData>()

        //for context
        c = this
        v = View(this)

        //set ids
        addsBtn = binding.addingBtn
        recv = binding.mRecycler

        //set adapter
        userAdapter = UserAdapter(this, userList)

        //set Recycler view Adapter
        recv.layoutManager = LinearLayoutManager(this)
        recv.setHasFixedSize(true)
        recv.adapter = userAdapter

        //set dialog to upload info in firebase
        addsBtn.setOnClickListener { addInfo() }

        //Logout from app
        binding.logoutBtn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            finish()
        }

        //To get data from firebase
        getUserData()
    }

    private fun getUserData() {
        val currentUser = FirebaseAuth.getInstance().currentUser?.uid
        database = FirebaseDatabase.getInstance().getReference("$currentUser")
        database.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                //Clearing old list
                userList.clear()
                if (snapshot.exists()){
                    for (userSnapshot in snapshot.children){
                        val user = userSnapshot.getValue(UserData::class.java)
                        userList.add(user!!)
                    }
                    recv.adapter = UserAdapter(c, userList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity,"Loading failed", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun addInfo() {
        val inflater = LayoutInflater.from(this)
        val v = inflater.inflate(R.layout.add_item, null)
        val toDoTitle = v.findViewById<EditText>(R.id.toDoTitle)
        val toDoDescription = v.findViewById<EditText>(R.id.toDoDescription)
        val addDialog = AlertDialog.Builder(this)

        addDialog.setView(v)
        addDialog.setPositiveButton("OK"){
            dialog,_->
            val title = toDoTitle.text.toString()
            val description = toDoDescription.text.toString()

            //--->>>saving in database
            val userData = UserData(title, description)

            //find current user
            val currentUser = FirebaseAuth.getInstance().currentUser?.uid

            //unique id for upload data
            val uid = FirebaseDatabase.getInstance().reference.push().key
            //--->>>saving in database

            //userList.add(UserData("Name: $names", "Mobile No. : $number"))
            userAdapter.notifyDataSetChanged()
            //Toast.makeText(this,"Adding User Information Success", Toast.LENGTH_SHORT).show()

            //--->>>saving in database
            //set current user id
            database = FirebaseDatabase.getInstance().getReference("$currentUser").child(title)

            if (title.isEmpty()){
                Toast.makeText(this,"Blank is empty", Toast.LENGTH_LONG).show()
            }else{
                //set unique id for each and send in firebase realtime database
                database.setValue(userData)
                //--->>>saving in database
            }


            dialog.dismiss()
        }
        addDialog.setNegativeButton("Cancel"){
            dialog,_->
            dialog.dismiss()
        }
        addDialog.create()
        addDialog.show()
    }
}