package com.example.samgram.mvvm

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.samgram.Utils
import com.example.samgram.modal.Feed
import com.example.samgram.modal.Posts
import com.example.samgram.modal.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class ViewModel : ViewModel(){


    val name = MutableLiveData<String>()
    val image = MutableLiveData<String>()
    val followers = MutableLiveData<String>()
    val following = MutableLiveData<String>()




    init {

        getCurrentUser()

    }


    fun getCurrentUser() = viewModelScope.launch(Dispatchers.IO) {

        val firestore = FirebaseFirestore.getInstance()

        firestore.collection("Users").document(Utils.getUiLoggedIn()).addSnapshotListener { value, error ->


            if (error!=null){

                return@addSnapshotListener

            }

            if (value!=null && value.exists()){

                val users = value.toObject(User::class.java)
                name.value  = users!!.username!!
                image.value = users.image!!
                followers.value = users.followers!!.toString()
                following.value = users.following!!.toString()











            }



        }






    }



    fun getMyPosts(): LiveData<List<Posts>> {
        val posts = MutableLiveData<List<Posts>>()
        val firestore = FirebaseFirestore.getInstance()

        viewModelScope.launch(Dispatchers.IO) {
            try {
                firestore.collection("Posts")
                    .whereEqualTo("userid", Utils.getUiLoggedIn())
                    .addSnapshotListener { snapshot, exception ->
                        if (exception != null) {
                            // Handle the exception here
                            return@addSnapshotListener
                        }

                        val postList = snapshot?.documents?.mapNotNull {
                            it.toObject(Posts::class.java)
                        }
                            ?.sortedByDescending { it.time
                            }

                        posts.postValue(postList!!) // Switch back to the main thread
                    }
            } catch (e: Exception) {
                // Handle any exceptions that occur during the Firestore operation
            }
        }



        return posts
    }


    // get all users except me
    fun getAllUsers(): LiveData<List<User>> {
        val users = MutableLiveData<List<User>>()
        val firestore = FirebaseFirestore.getInstance()

        viewModelScope.launch(Dispatchers.IO) {
            try {
                firestore.collection("Users").addSnapshotListener { snapshot, exception ->
                    if (exception != null) {
                        // Handle the exception here
                        return@addSnapshotListener
                    }

                    val usersList = mutableListOf<User>()
                    snapshot?.documents?.forEach { document ->
                        val user = document.toObject(User::class.java)
                        if (user != null && user.userid != Utils.getUiLoggedIn()) {
                            usersList.add(user)
                        }
                    }

                    users.postValue(usersList) // Switch back to the main thread
                }
            } catch (e: Exception) {
                // Handle any exceptions that occur during the Firestore operation
            }
        }



        return users
    }





    fun loadMyFeed(): LiveData<List<Feed>> {

        val firestore = FirebaseFirestore.getInstance()

        val feeds = MutableLiveData<List<Feed>>()



        viewModelScope.launch(Dispatchers.IO) {



            getThePeopleIFollow { list ->



                try{

                    firestore.collection("Posts").whereIn("userid", list)
                        .addSnapshotListener { value, error ->
                            if (error != null) {
                                return@addSnapshotListener
                            }

                            val feed = mutableListOf<Feed>()
                            value?.documents?.forEach { documentSnapshot ->
                                val pModal = documentSnapshot.toObject(Feed::class.java)
                                pModal?.let {
                                    feed.add(it)
                                }
                            }

                            val sortedFeed = feed.sortedByDescending { it.time }



                            feeds.postValue(sortedFeed)


                        }



                }catch (e:Exception){



                }

            }


        }




        return feeds
    }



    // get the ids of those who I follow

    fun getThePeopleIFollow(callback: (List<String>) -> Unit) {
        val firestore = FirebaseFirestore.getInstance()

        val ifollowlist = mutableListOf<String>()
        ifollowlist.add(Utils.getUiLoggedIn())

        firestore.collection("Follow").document(Utils.getUiLoggedIn())
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val followingIds = documentSnapshot.get("following_id") as? List<String>
                    val updatedList = followingIds?.toMutableList() ?: mutableListOf()

                    ifollowlist.addAll(updatedList)

                    Log.e("ListOfFeed", ifollowlist.toString())
                    callback(ifollowlist)
                } else {
                    callback(ifollowlist)
                }
            }
    }




}