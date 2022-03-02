package com.example.socialapp

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.socialapp.Model.Post
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class PostAdapter(options: FirestoreRecyclerOptions<Post>, val Listener: IpostAdapter) : FirestoreRecyclerAdapter<Post, PostViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
         val viewholder= PostViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_view,parent,false))
         viewholder.likeButton.setOnClickListener {
             val postid = snapshots.getSnapshot(viewholder.adapterPosition).id
             Listener.onLikeClicked(postid)
             Log.d("msg", "postid = $postid")
             Log.d("msg","like button clicked")
         }
        return viewholder
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int, model: Post) {
        holder.userName.text = model.createdBy.name
        holder.userText.text = model.text
        //with(context) load(url) into(view)
        Glide.with(holder.userImage.context).load(model.createdBy.imageUrl).circleCrop().into(holder.userImage)
        holder.Likecount.text = model.Likes.size.toString()
        holder.userTime.text = Utils.getTimeAgo(model.createdAt)

        val auth = FirebaseAuth.getInstance()
        val currentuserId = auth.currentUser!!.uid

        val isliked = model.Likes.contains(currentuserId)
        if(isliked){
            holder.likeButton.setImageDrawable(ContextCompat.getDrawable(holder.likeButton.context,R.drawable.ic_baseline_favorite_24))
            }
            else{
                holder.likeButton.setImageDrawable(ContextCompat.getDrawable(holder.likeButton.context,R.drawable.unliked))
            }
        }

}


class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val userImage = itemView.findViewById<ImageView>(R.id.userImage)
    val userName = itemView.findViewById<TextView>(R.id.userName)
    val userTime = itemView.findViewById<TextView>(R.id.userTime)
    val userText = itemView.findViewById<TextView>(R.id.userText)
    val Likecount = itemView.findViewById<TextView>(R.id.LikesCount)
    val likeButton = itemView.findViewById<ImageView>(R.id.LikeBtn)
}
interface IpostAdapter{
    fun onLikeClicked(postid : String)
}
