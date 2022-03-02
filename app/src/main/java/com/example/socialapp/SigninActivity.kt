package com.example.socialapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.socialapp.Daos.userDao
import com.example.socialapp.Model.user
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_signin.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class SigninActivity : AppCompatActivity() {
    val RC_SIGN_IN = 126
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)


        //firebase instance(auth) initialisation
        auth = FirebaseAuth.getInstance()

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id_auth))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        signInButton.setOnClickListener { signIn() }
    }
    override fun onStart() {
        super.onStart()
        updateUI(auth.currentUser)
    }
    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val exception = task.exception
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d("msg", "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("msg", "Google sign in failed" +exception.toString(), e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        loading.visibility = View.VISIBLE
        signInButton.visibility = View.GONE
        GlobalScope.launch(Dispatchers.IO) {
            auth.signInWithCredential(credential).await()
            val firebaseuser = auth.currentUser
            //here we are switching to main thread from IO thread
            withContext(Dispatchers.Main){
                updateUI(firebaseuser)
            }
        }

    }

    private fun updateUI(firebaseUser: FirebaseUser?) {
        if(firebaseUser != null){
            //convert firebase user to user(model/data class)
            val User = user(firebaseUser.displayName,firebaseUser.photoUrl.toString(),firebaseUser.uid)
            //create instance of user Dao class
            val userDaoObject = userDao()
            //add user into database using userDao add =User function
            userDaoObject.addUser(User)

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }else{
            loading.visibility = View.GONE
            signInButton.visibility = View.VISIBLE
        }
    }


}