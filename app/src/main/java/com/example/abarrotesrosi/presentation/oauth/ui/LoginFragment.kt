package com.example.abarrotesrosi.presentation.oauth.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.abarrotesrosi.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import androidx.navigation.fragment.findNavController
import com.example.abarrotesrosi.R
import android.util.Log

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        login()
        register()
    }

    private fun register() {
        binding.reguistroButton.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }


    //esta es mi funcion login
    private fun login() {
        binding.loginButton.setOnClickListener {

            val email = binding.inputEmail.text.toString().trim()
            val password = binding.inputPass.text.toString().trim()

            if (email.isEmpty()) {
                binding.inputEmail.error = "Campo obligatorio"
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                binding.inputPass.error = "Campo obligatorio"
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    findNavController().navigate(
                        R.id.action_loginFragment_to_homeFragment
                    )
                }
                .addOnFailureListener { e ->
                    Log.e("LOGIN", e.message.toString())
                    showAlert()
                }
        }
    }

    private fun showAlert() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error autenticando al usuario. Verifica tus credenciales.")
        builder.setPositiveButton("Aceptar", null)
        val dialog = builder.create()
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}