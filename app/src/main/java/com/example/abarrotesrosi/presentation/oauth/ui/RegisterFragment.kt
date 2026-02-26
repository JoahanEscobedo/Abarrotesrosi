package com.example.abarrotesrosi.presentation.oauth.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.abarrotesrosi.databinding.FragmentRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import androidx.navigation.fragment.findNavController
import com.example.abarrotesrosi.R

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

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
        binding.reguistrarButton.setOnClickListener {
            val email = binding.inputEmail.text.toString().trim()
            val password = binding.inputPass.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            FirebaseAuth.getInstance().signOut()
                            if (isAdded) {
                                AlertDialog.Builder(requireContext())
                                    .setTitle("Registro Exitoso")
                                    .setMessage("¡Bienvenido! Tu cuenta ha sido creada correctamente.")
                                    .setPositiveButton("Ir al Login") { _, _ ->

                                        val volviste = findNavController().popBackStack()

                                        if (!volviste) {
                                            try {
                                                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                                            } catch (e: Exception) {
                                                showAlert("Error de navegación: ${e.localizedMessage}")
                                            }
                                        }
                                    }
                                    .setCancelable(false)
                                    .show()
                            }
                        } else {
                            val errorMsg = task.exception?.localizedMessage ?: "Error desconocido"
                            showAlert(errorMsg)
                        }
                    }
            } else {
                if (email.isEmpty()) binding.inputEmail.error = "Campo obligatorio"
                if (password.isEmpty()) binding.inputPass.error = "Campo obligatorio"
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}