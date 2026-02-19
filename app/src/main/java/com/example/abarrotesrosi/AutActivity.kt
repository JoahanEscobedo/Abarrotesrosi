package com.example.abarrotesrosi

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

enum class ProviderType {
    BASIC
}

class AutActivity : AppCompatActivity() {

    lateinit var EmailImput: EditText
    lateinit var PasswordImput: EditText
    lateinit var LoginBtn: Button
    lateinit var ReguistreBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aut)

        EmailImput = findViewById(R.id.input_email)
        PasswordImput = findViewById(R.id.input_pass)
        LoginBtn = findViewById(R.id.login_button)
        ReguistreBtn = findViewById(R.id.reguistro_button)

        setup()
    }

    private fun setup() {
        title = "Autenticacion"

        // REGISTRO
        ReguistreBtn.setOnClickListener {
            if (EmailImput.text.isNotEmpty() && PasswordImput.text.isNotEmpty()) {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                    EmailImput.text.toString(),
                    PasswordImput.text.toString()
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        showhome(it.result?.user?.email ?: "", ProviderType.BASIC)
                    } else {
                        showalert()
                    }
                }
            }
        }

        // LOGIN (Corregido: signIn en lugar de createUser)
        LoginBtn.setOnClickListener {
            if (EmailImput.text.isNotEmpty() && PasswordImput.text.isNotEmpty()) {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(
                    EmailImput.text.toString(),
                    PasswordImput.text.toString()
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        showhome(it.result?.user?.email ?: "", ProviderType.BASIC)
                    } else {
                        showalert()
                    }
                }
            }
        }
    }

    private fun showalert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error autenticando al usuario")
        builder.setPositiveButton("aceptar", null)
        builder.create().show()
    }

    private fun showhome(email: String, provider: ProviderType) {
        val homeIntent = Intent(this, Home::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)
        }
        startActivity(homeIntent)
    }
}