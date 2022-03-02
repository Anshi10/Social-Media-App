package com.example.socialapp.Daos

import android.util.Log
import com.example.socialapp.Model.Post
import com.example.socialapp.Model.user
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class PostDao {
    val db = FirebaseFirestore.getInstance()
    val PostCollection = db.collection("Post")
    val auth = Firebase.auth

    fun addPost(text : String){
        //!! is to ensure that post is created only when user is logged in otherwise it will count as illegal exception
        val currentUserId = auth.currentUser!!.uid
        val userdao = userDao()
        GlobalScope.launch{
            //getuserbyid return task which will turn into object of user data class
            val Postuser = userdao.getuserbyId(currentUserId).await().toObject(user::class.java)!!
            //this will give the time when the post is created
            val currentTime = System.currentTimeMillis()

            val post = Post(text,Postuser,currentTime)
            PostCollection.document().set(post)
        }
    }
    fun getPostById(postid : String) : Task<DocumentSnapshot>{
        return PostCollection.document(postid).get()
    }
    fun updateLikes(postid: String) {
        GlobalScope.launch {
            val currentUserid = auth.currentUser!!.uid
            val post = getPostById(postid).await().toObject(Post::class.java)!!
            val isliked  = post.Likes.contains(currentUserid)
            if (isliked) {
                post.Likes.remove(currentUserid)
            } else {
                post.Likes.add(currentUserid)
            }
           PostCollection.document(postid).set(post)
        }
        Log.d("msg","updateLikes called")
    }

}
//in the conversion of documentsnapsot to object
//In Kotlin, the data classes don't provide a default no-arg constructor.
// So you need somehow ensure the compiler that all the properties have an initial value.
// You can provide to all of the properties an initial value of null or any other value you find more appropriate.
