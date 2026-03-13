package com.example.abarrotesrosi.presentation.oauth.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.abarrotesrosi.databinding.FragmentRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import android.net.Uri
import android.content.Intent
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.firestore.FirebaseFirestore
import androidx.navigation.fragment.findNavController
import com.example.abarrotesrosi.R


class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val bd = FirebaseFirestore.getInstance()
    private var selectedImageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
    }

    private fun setup() {

        binding.btnUploadPhoto.setOnClickListener {
            openGallery()
        }

        binding.reguistrarButton.setOnClickListener {

            val email = binding.inputEmail.text.toString().trim()
            val password = binding.inputPass.text.toString().trim()
            val name = binding.inputName.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty() && name.isNotEmpty()) {

                FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->

                        if (task.isSuccessful) {

                            val userId = FirebaseAuth.getInstance().currentUser!!.uid

                            uploadPhoto(userId, name, email)

                            findNavController().navigate(R.id.loginFragment)

                        } else {
                            showAlert(task.exception?.localizedMessage ?: "Error")
                        }
                    }
            }
        }
    }


    private fun showAlert(message: String = "Se ha producido un error autenticando al usuario.") {
        AlertDialog.Builder(requireContext())
            .setTitle("Error")
            .setMessage(message)
            .setPositiveButton("Aceptar", null)
            .show()
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 100)
    }

    private fun uploadPhoto(userId: String, name: String, email: String) {

        val storageRef = FirebaseStorage.getInstance().reference
            .child("users/$userId/profile.jpg")

        selectedImageUri?.let { uri ->

            storageRef.putFile(uri)
                .addOnSuccessListener {

                    storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->

                        saveData(userId, name, email, downloadUrl.toString())

                    }

                }
                .addOnFailureListener {
                    showAlert("Error subiendo la imagen")
                }

        } ?: run {
            saveData(userId, name, email, "")
        }
    }

    private fun saveData(userId: String, name: String, email: String, photo: String) {

        val userMap = hashMapOf(
            "name" to name,
            "email" to email,
            "photo" to photo
        )

        bd.collection("users")
            .document(userId)
            .set(userMap)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}