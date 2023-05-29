package com.example.samgram

import android.app.Application
import android.content.Context

class MyApplication : Application() {

    companion object{


        lateinit var instance : MyApplication




    }

    override fun onCreate(){
        super.onCreate()

        instance = this



    }


}