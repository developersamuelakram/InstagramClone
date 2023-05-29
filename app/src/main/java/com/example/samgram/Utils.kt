package com.example.samgram

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.samgram.modal.Follow
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*

class Utils {


    companion object {

        private val auth = FirebaseAuth.getInstance()
        private var userid: String = ""


        const val REQUEST_IMAGE_CAPTURE = 1
        const val REQUEST_IMAGE_PICK = 2
        const val PROFILE_IMAGE_CAPTURE = 3
        const val PROFILE_IMAGE_PICK = 4

        fun getUiLoggedIn(): String {


            if (auth.currentUser != null) {

                userid = auth.currentUser!!.uid


            }




            return userid

        }



        fun getTime(): Long {


            val unixTimestamp: Long = System.currentTimeMillis() / 1000



            return unixTimestamp

        }



    }
}