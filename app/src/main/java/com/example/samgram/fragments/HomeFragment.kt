package com.example.samgram.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.samgram.R
import com.example.samgram.Utils
import com.example.samgram.adapters.MyFeedAdapter
import com.example.samgram.adapters.onDoubleTapClickListener
import com.example.samgram.databinding.FragmentHomeBinding
import com.example.samgram.modal.Feed
import com.example.samgram.mvvm.ViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class HomeFragment : Fragment(), onDoubleTapClickListener {


    private lateinit var vm : ViewModel
    private lateinit var binding : FragmentHomeBinding
    private lateinit var adapter : MyFeedAdapter

    private var postid : String = ""


    private var userwholiked : String = ""

    private var idofpostowner : String = ""



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        vm = ViewModelProvider(this).get(ViewModel::class.java)
        adapter = MyFeedAdapter()

        binding.lifecycleOwner = viewLifecycleOwner


        vm.loadMyFeed().observe(viewLifecycleOwner, Observer {


            adapter.setFeedList(it)

            binding.feedRecycler.adapter = adapter



        } )

        adapter.setListener(this)


        binding.imageViewBottom.setOnClickListener {

            view.findNavController().navigate(R.id.action_homeFragment2_to_profileFragment)

        }

        vm.image.observe(viewLifecycleOwner, Observer {


            Glide.with(requireContext()).load(it).into(binding.imageViewBottom)


        })



    }

    override  fun onDoubleTap(feed: Feed) {


        val currentUserId = Utils.getUiLoggedIn() // Replace with the current user's ID
        val postId = feed.postid

        val firestore = FirebaseFirestore.getInstance()
        val postRef = firestore.collection("Posts").document(postId!!)

        // Fetch current like count and likers from Firestore
        postRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val likes = document.getLong("likes")?.toInt() ?: 0
                    val likers = document.get("likers") as? List<String>

                    if (!likers.isNullOrEmpty() && likers.contains(currentUserId)) {
                        // User has already liked the post
                        println("You have already liked this post!")
                    } else {
                        // Increment like count and update likers
                        postRef.update(
                            "likes", likes + 1,
                            "likers", FieldValue.arrayUnion(currentUserId)
                        )
                            .addOnSuccessListener {
                                println("Post liked!")
                            }
                            .addOnFailureListener { exception ->
                                println("Failed to update like: $exception")
                            }
                    }
                }
            }




    }




}