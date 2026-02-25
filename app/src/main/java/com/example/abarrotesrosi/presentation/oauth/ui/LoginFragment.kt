package com.example.abarrotesrosi.presentation.oauth.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.abarrotesrosi.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth

enum class ProviderType {
    BASIC,
}
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
    }

    private fun setup() {
        binding.loginButton.setOnClickListener {
            val email = binding.inputEmail.text.toString()
            val password = binding.inputPass.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            navigateToHome()
                        } else {
                            showAlert()
                        }
                    }
            } else {
                binding.inputEmail.error = "Campo obligatorio"
                binding.inputPass.error = "Campo obligatorio"
            }
        }

        //nos manda al framneto de resguistrarse
        binding.reguistroButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(com.example.abarrotesrosi.R.id.nav_host_fragment, RegisterFragment()) // Asegúrate de que el ID coincida con tu contenedor
                .addToBackStack(null)
                .commit()
        }
    }

    private fun showAlert() {
        // En un Fragment usamos 'requireContext()' en lugar de 'this'
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error autenticando al usuario. Verifica tus credenciales.")
        builder.setPositiveButton("Aceptar", null)
        val dialog = builder.create()
        dialog.show()
    }

    private fun navigateToHome() {
        parentFragmentManager.beginTransaction()
            .replace(com.example.abarrotesrosi.R.id.nav_host_fragment, homefragment())
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}