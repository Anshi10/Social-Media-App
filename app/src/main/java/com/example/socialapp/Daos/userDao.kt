package com.example.socialapp.Daos

import com.example.socialapp.Model.user
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class userDao {
    //get the user database(instance of it)
    val db = FirebaseFirestore.getInstance()
    //refer users collection from user database
    val usersCollection = db.collection("users")

    fun addUser(user : user?){
        //let is a scope function which let this block to run if user is not null
        user?.let {
            GlobalScope.launch(Dispatchers.IO) {
                usersCollection.document(user.id).set(it)
            }
        }
    }

    fun getuserbyId(uId : String) : Task<DocumentSnapshot> {
         return usersCollection.document(uId).get()
    }
}