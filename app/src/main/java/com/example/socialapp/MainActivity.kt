package com.example.socialapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.socialapp.Daos.PostDao
import com.example.socialapp.Model.Post
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), IpostAdapter {

    private lateinit var adapter : PostAdapter
    private lateinit var postdao : PostDao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        addPost.setOnClickListener{
            val intent = Intent(this , NewPostActivity::class.java)
            startActivity(intent)
        }
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        postdao = PostDao()
        //accessing postcollections of postdao
        val postcollections = postdao.PostCollection
        //writing the query to show data in Rview
        val query = postcollections.orderBy("createdAt", Query.Direction.DESCENDING)
        //firestoreoptions to pass in adapter parameter
        val firestoreOption = FirestoreRecyclerOptions.Builder<Post>().setQuery(query , Post::class.java).build()

         adapter = PostAdapter(firestoreOption,this)

        Rview.adapter = adapter
        Rview.layoutManager = LinearLayoutManager(this)
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    override fun onLikeClicked(postid: String) {
        Log.d("msg","onLikeClicked function called")
         postdao.updateLikes(postid)
    }


}
//recycler view needs options which is basically a query of the way of showing data into recycler view