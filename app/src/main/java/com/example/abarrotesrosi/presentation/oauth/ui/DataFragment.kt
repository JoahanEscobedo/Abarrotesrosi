package com.example.abarrotesrosi.presentation.oauth.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.abarrotesrosi.R
import com.google.firebase.auth.FirebaseAuth
import com.example.abarrotesrosi.databinding.FragmentDataBinding
import com.google.firebase.firestore.FirebaseFirestore
import android.app.AlertDialog

class DataFragment : Fragment() {

    private var _binding: FragmentDataBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: FirebaseFirestore
    private var uid: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDataBinding.inflate(inflater, container, false)

        db = FirebaseFirestore.getInstance()
        uid = FirebaseAuth.getInstance().currentUser?.uid
        closeSession()
        botones()


        return binding.root
    }

    private fun botones(){
        binding.btnuserdata.setOnClickListener {
            consultarDatos()
        }
        binding.btnuseredit.setOnClickListener {
            editarNombre()
        }
        binding.btnuserdelete.setOnClickListener {
            mostrarAlertaEliminar()
        }
    }

    private fun consultarDatos(){

        uid?.let {

            db.collection("users")
                .document(it)
                .get()
                .addOnSuccessListener { document ->

                    if (document.exists()) {

                        val nombre = document.getString("name")
                        val email = document.getString("email")

                        binding.txtNombre.setText(nombre)
                        binding.txtEmail.text = email
                    }
                }
        }
    }

    private fun editarNombre(){

        val nuevoNombre = binding.txtNombre.text.toString()

        uid?.let {

            db.collection("users")
                .document(it)
                .update("name", nuevoNombre)
                .addOnSuccessListener {

                    binding.txtNombre.setText(nuevoNombre)

                }
                .addOnFailureListener {

                }
        }
    }

    private fun eliminarUsuario(){

        val user = FirebaseAuth.getInstance().currentUser

        uid?.let { id ->

            db.collection("users")
                .document(id)
                .delete()
                .addOnSuccessListener {

                    user?.delete()?.addOnCompleteListener {

                        findNavController().navigate(R.id.action_DataFragment_to_loginFragment)

                    }
                }
        }
    }

    private fun mostrarAlertaEliminar(){

        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar usuario")
            .setMessage("¿Seguro que deseas eliminar tu cuenta?")
            .setPositiveButton("Sí") { _, _ ->

                eliminarUsuario()

            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun closeSession() {
        binding.btnclosest.setOnClickListener {

            FirebaseAuth.getInstance().signOut()

            findNavController().navigate(
                R.id.action_DataFragment_to_loginFragment,
                null,
                androidx.navigation.NavOptions.Builder()
                    .setPopUpTo(R.id.DataFragment, true)
                    .build()
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}