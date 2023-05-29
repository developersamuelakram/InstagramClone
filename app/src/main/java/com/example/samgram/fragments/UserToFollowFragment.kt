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
import com.example.samgram.R
import com.example.samgram.Utils
import com.example.samgram.adapters.OnFriendClicked
import com.example.samgram.adapters.UsersAdapter
import com.example.samgram.databinding.FragmentUserToFollowBinding
import com.example.samgram.modal.User
import com.example.samgram.mvvm.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class UserToFollowFragment : Fragment(), OnFriendClicked {


    private lateinit var adapter : UsersAdapter
    private lateinit var vm : ViewModel
    private lateinit var binding: FragmentUserToFollowBinding
    var clickedOn: Boolean = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil. inflate(inflater, R.layout.fragment_user_to_follow, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vm = ViewModelProvider(this).get(ViewModel::class.java)




        binding.lifecycleOwner = viewLifecycleOwner

        adapter = UsersAdapter()

        binding.backBtn.setOnClickListener {


            view.findNavController().navigate(R.id.action_userToFollowFragment_to_profileFragment)
        }


        vm.getAllUsers().observe(viewLifecycleOwner, Observer {




            adapter.setUserLIST(it)

            binding.rvFollow.adapter = adapter




        })

        adapter.setListener(this)






    }

    override fun onfriendListener(position: Int, user: User) {


        adapter.followUser(user)



    }


}