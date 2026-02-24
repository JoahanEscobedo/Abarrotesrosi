package com.example.abarrotesrosi.presentation.oauth.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.abarrotesrosi.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth

//TODO: Podemos configurar de esta manera los de mas Frgaments
//TODO: Pasaremos todo lo del AutActivity aqui
//TODO: En la parte de navegacion, res -> navigation, encontraras las pantallas (grafoz)
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        binding.emailTextView.text = "email"
        binding.providerTextView.text = "provider"

        binding.logOutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}