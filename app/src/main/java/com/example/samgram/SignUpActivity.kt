@file:Suppress("DEPRECATION")

package com.example.samgram

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.samgram.databinding.ActivitySignInBinding
import com.example.samgram.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var pd: ProgressDialog
    private lateinit var firestore: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_sign_up)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        binding.signUpTexttoSignIn.setOnClickListener {

            startActivity(Intent(this, SignInActivity::class.java))

        }

        pd = ProgressDialog(this)



        binding.signUpButton.setOnClickListener {




            if (binding.signUpnetemail.text.isNotEmpty() && binding.signUpetpassword.text.isNotEmpty() && binding.signUpName.text.isNotEmpty()){

                val email = binding.signUpnetemail.text.toString()
                val password = binding.signUpetpassword.text.toString()
                val name =  binding.signUpName.text.toString()


                signInUp(name, email, password)


            }

            if (binding.signUpnetemail.text.isEmpty() || binding.signUpetpassword.text.isEmpty() || binding.signUpName.text.isEmpty()){


                Toast.makeText(this, "Fill the fields", Toast.LENGTH_SHORT).show()
            }




        }










    }

    private fun signInUp(name: String, email: String, password: String) {
        pd.show()
        pd.setMessage("Registering User")


        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task->


            if (task.isSuccessful){


                val user = auth.currentUser


                val hashMap = hashMapOf("userid" to user!!.uid,
                    "image" to "https://upload.wikimedia.org/wikipedia/en/b/bd/Doraemon_character.png",
                    "username" to name,
                    "email" to email,
                    "followers" to 0,
                    "following" to 0)



                firestore.collection("Users").document(user.uid).set(hashMap)
                pd.dismiss()
                startActivity(Intent(this, SignInActivity::class.java))











            }



    }


}
}