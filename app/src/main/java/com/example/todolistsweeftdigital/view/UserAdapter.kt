package com.example.todolistsweeftdigital.view

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.todolistsweeftdigital.R
import com.example.todolistsweeftdigital.model.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class UserAdapter(val c:Context,val userList:ArrayList<UserData>):RecyclerView.Adapter<UserAdapter.UserViewHolder>() {
    private lateinit var database: DatabaseReference

    inner class UserViewHolder(val v: View): RecyclerView.ViewHolder(v){
        var title: TextView = v.findViewById(R.id.mTitle)
        var description: TextView = v.findViewById(R.id.mSubTitle)
        private var mMenus: ImageView = v.findViewById(R.id.mMenus)
        private lateinit var database: DatabaseReference

        init {
            mMenus.setOnClickListener { popupMenus(it) }
        }

        //Display popup menu after click three dot
        private fun popupMenus(v: View) {
            val position = userList[adapterPosition]
            val popupMenus = PopupMenu(c, v)
            popupMenus.inflate(R.menu.show_menu)
            popupMenus.setOnMenuItemClickListener {
                when(it.itemId){
                    //edit old info
                    R.id.editText -> {
                        val v = LayoutInflater.from(c).inflate(R.layout.add_item_desc, null)
                        //var title = v.findViewById<EditText>(R.id.toDoTitle)
                        val description = v.findViewById<EditText>(R.id.toDoDescription)
                        var currentTitle = position.toDoTitle
                        val currentUser = FirebaseAuth.getInstance().currentUser?.uid
                        AlertDialog.Builder(c)
                            .setView(v)
                            .setPositiveButton("OK"){
                                dialog,_->
//                                position.toDoTitle = title.text.toString()
//                                position.toDoDescription = description.text.toString()
//                                notifyDataSetChanged()
                                val user = mapOf<String, String>(
                                    "toDoTitle" to title.text.toString(),
                                    "toDoDescription" to description.text.toString()
                                )

                                if (description.text.isEmpty()){
                                    dialog.dismiss()
                                    Toast.makeText(c,"Blank is empty", Toast.LENGTH_SHORT).show()
                                }else{
                                    // database init
                                    database = FirebaseDatabase.getInstance().getReference("$currentUser").child(currentTitle)

                                    //firebase database update
                                    database.updateChildren(user).addOnSuccessListener {
                                        Toast.makeText(c,"User info is edited", Toast.LENGTH_SHORT).show()
                                    }.addOnFailureListener {
                                        Toast.makeText(c,"User info editing failed", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                notifyDataSetChanged()
                                //Toast.makeText(c,"User info is edited", Toast.LENGTH_SHORT).show()
                                dialog.dismiss()

                            }
                            .setNegativeButton("Cancel"){
                                dialog,_->
                                dialog.dismiss()
                            }
                            .create()
                            .show()
                        true
                    }
                    //delete old info
                    R.id.delete -> {
                        var currentTitle = position.toDoTitle
                        val currentUser = FirebaseAuth.getInstance().currentUser?.uid
                        AlertDialog.Builder(c)
                            .setTitle("Delete")
                            .setIcon(R.drawable.ic_warning)
                            .setMessage("Are you sure delete this info ?")
                            .setPositiveButton("Yes"){
                                dialog,_->
                                userList.removeAt(adapterPosition)
                                notifyDataSetChanged()
                                dialog.dismiss()
                                database = FirebaseDatabase.getInstance().getReference("$currentUser").child(currentTitle)
                                database.removeValue()
                                Toast.makeText(c, "$currentTitle", Toast.LENGTH_LONG).show()
                            }
                            .setNegativeButton("No"){
                                dialog,_->
                                dialog.dismiss()
                            }
                            .create()
                            .show()
                        true
                    }
                    else -> true
                }
            }
            popupMenus.show()
            val popup = PopupMenu::class.java.getDeclaredField("mPopup")
            popup.isAccessible = true
            val menu = popup.get(popupMenus)
            menu.javaClass.getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                .invoke(menu, true)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.list_item, parent, false)
        return UserViewHolder(v)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val newList = userList[position]
        holder.title.text = newList.toDoTitle
        holder.description.text = newList.toDoDescription
    }

    override fun getItemCount(): Int {
        return userList.size
    }
}