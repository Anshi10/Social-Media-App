package com.example.socialapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.socialapp.Daos.PostDao
import kotlinx.android.synthetic.main.activity_new_post.*

class NewPostActivity : AppCompatActivity() {

    private lateinit var postDaoobject : PostDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_post)

        postDaoobject = PostDao()

        submitbtn.setOnClickListener{
            val InputText = inputText.text.toString()
            if(InputText.isNotEmpty() ){
                postDaoobject.addPost(InputText)
                finish()
            }
           else{
               Toast.makeText(this,"Please text the post",Toast.LENGTH_SHORT).show()
            }
        }
    }
}