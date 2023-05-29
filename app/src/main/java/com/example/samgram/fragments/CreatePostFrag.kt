@file:Suppress("DEPRECATION")

package com.example.samgram.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.samgram.R
import com.example.samgram.Utils
import androidx.lifecycle.Observer
import com.example.samgram.adapters.MyPostAdapter
import com.example.samgram.databinding.FragmentCreatePostBinding
import com.example.samgram.mvvm.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.util.*


class CreatePostFrag : Fragment() {


    private lateinit var binding : FragmentCreatePostBinding
    private lateinit var pd: ProgressDialog
    private lateinit var vm : ViewModel
    private lateinit var storageRef: StorageReference
    private lateinit var storage: FirebaseStorage
    private var uri: Uri? = null
    private lateinit var firestore : FirebaseFirestore

    private lateinit var bitmap: Bitmap
    var postid : String =""
    var imageUserPoster: String = ""
    var nameUserPoster: String = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_post, container, false)

       return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        vm = ViewModelProvider(this).get(ViewModel::class.java)


        postid = UUID.randomUUID().toString()


        binding.lifecycleOwner = viewLifecycleOwner

        pd = ProgressDialog(requireContext())

        firestore = FirebaseFirestore.getInstance()


        storage = FirebaseStorage.getInstance()
        storageRef = storage.reference




        vm.name.observe(viewLifecycleOwner, Observer { it->

            nameUserPoster = it!!


        })

        vm.image.observe(viewLifecycleOwner, Observer { it->

            imageUserPoster = it!!


        })





        binding.imageToPost.setOnClickListener {


            addPostDialog()



        }

        binding.postBtn.setOnClickListener {

            var capti = binding.addCaption.text.toString()

            firestore.collection("Posts").document(postid).update("caption", capti)

            view.findNavController().navigate(R.id.action_createPostFrag_to_profileFragment)





        }





    }

    private fun addPostDialog() {
        val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Choose your profile picture")
        builder.setItems(options) { dialog, item ->
            when {
                options[item] == "Take Photo" -> {

                    takePhotoWithCamera()


                }
                options[item] == "Choose from Gallery" -> {
                    pickImageFromGallery()
                }
                options[item] == "Cancel" -> dialog.dismiss()
            }
        }
        builder.show()


    }



    @SuppressLint("QueryPermissionsNeeded")
    private fun pickImageFromGallery() {

        val pickPictureIntent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (pickPictureIntent.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForResult(pickPictureIntent, Utils.REQUEST_IMAGE_PICK)
        }
    }

    // To take a photo with the camera, you can use this code
    @SuppressLint("QueryPermissionsNeeded")
    private fun takePhotoWithCamera() {

        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(takePictureIntent, Utils.REQUEST_IMAGE_CAPTURE)


    }


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)



        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                Utils.REQUEST_IMAGE_CAPTURE -> {
                    val imageBitmap = data?.extras?.get("data") as Bitmap

                    uploadImageToFirebaseStorage(imageBitmap)
                }
                Utils.REQUEST_IMAGE_PICK -> {
                    val imageUri = data?.data
                    val imageBitmap =
                        MediaStore.Images.Media.getBitmap(context?.contentResolver, imageUri)
                    uploadImageToFirebaseStorage(imageBitmap)
                }
            }
        }

    }


    private fun uploadImageToFirebaseStorage(imageBitmap: Bitmap?) {


        val baos = ByteArrayOutputStream()
        imageBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()


        bitmap = imageBitmap!!

        binding.imageToPost.setImageBitmap(imageBitmap)

        val storagePath = storageRef.child("Photos/${UUID.randomUUID()}.jpg")
        val uploadTask = storagePath.putBytes(data)


        uploadTask.addOnSuccessListener {


            val task = it.metadata?.reference?.downloadUrl

            task?.addOnSuccessListener {

                uri = it



                postImage(uri)








            }
            Toast.makeText(context, "Image uploaded successfully!", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(context, "Failed to upload image!", Toast.LENGTH_SHORT).show()
        }
    }



    private fun postImage(uri: Uri?) {



        val likers = ArrayList<String>() // Create an empty ArrayList as the initial value

        val hashMap = hashMapOf<Any, Any>("image" to uri.toString(), "postid" to postid, "userid" to Utils.getUiLoggedIn(), "likers" to likers,
            "time" to Utils.getTime(), "caption" to "default", "likes" to 0,
            "username" to nameUserPoster, "imageposter" to imageUserPoster)

        firestore.collection("Posts").document(postid).set(hashMap)


    }


}