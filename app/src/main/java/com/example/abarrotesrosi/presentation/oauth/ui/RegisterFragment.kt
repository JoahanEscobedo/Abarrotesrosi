package com.example.abarrotesrosi.presentation.oauth.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.abarrotesrosi.databinding.FragmentRegisterBinding
import com.example.abarrotesrosi.presentation.main.MainActivity
import com.google.firebase.auth.FirebaseAuth

enum class ProviderType {
    BASIC, GOOGLE, FACEBOOK
}

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    private fun setup() {
        binding.reguistrarButton.setOnClickListener {
            val email = binding.inputEmail.text.toString()
            val password = binding.inputPass.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val userEmail = task.result?.user?.email ?: ""
                            showHome(userEmail, ProviderType.BASIC)
                        } else {
                            showAlert()
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

    private fun showHome(email: String, provider: ProviderType) {
        // Intent para ir a MainActivity
        val homeIntent = Intent(requireContext(), MainActivity::class.java).apply {
            // Pasamos los datos necesarios
            putExtra("email", email)
            putExtra("provider", provider.name)
            // Flags para evitar que el usuario regrese a la pantalla de registro
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        startActivity(homeIntent)

        // Finalizamos la actividad contenedora del fragment
        activity?.finish()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}