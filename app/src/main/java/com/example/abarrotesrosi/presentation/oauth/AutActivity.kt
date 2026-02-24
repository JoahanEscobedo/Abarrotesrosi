package com.example.abarrotesrosi.presentation.oauth

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.abarrotesrosi.presentation.main.MainActivity
import com.example.abarrotesrosi.R
import com.google.firebase.auth.FirebaseAuth

enum class ProviderType {
    BASIC
}

//TODO: Cuando pasemos la logica al fragmento, borremo este.
class AutActivity : AppCompatActivity() {

    lateinit var EmailImput: EditText //TODO: ESto ya no se utilizara por que usaremos binding, es muy viejo
    lateinit var PasswordImput: EditText
    lateinit var LoginBtn: Button
    lateinit var ReguistreBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aut)

        EmailImput = findViewById(R.id.input_email)//TODO: ESto ya no se utilizara por que usaremos binding, es muy viejo
        PasswordImput = findViewById(R.id.input_pass)
        LoginBtn = findViewById(R.id.login_button)
        ReguistreBtn = findViewById(R.id.reguistro_button)

        setup()
    }

    //TODO: Login es para entrar y registro para registrar, son dos pantalla independientes.
    private fun setup() {
        title = "Autenticacion" //TODO: Agregar en un strings

        // REGISTRO TODO: Agregar esto en el registro fragment
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

        // LOGIN (Corregido: signIn en lugar de createUser)TODO: Agregar esto en el login fragment
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

    private fun showalert() { //TODO: Las funciones tienen que ser tipo camello, ejemplo: showAlert()
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error autenticando al usuario")
        builder.setPositiveButton("aceptar", null)
        builder.create().show()
    }

    private fun showhome(email: String, provider: ProviderType) {
        val homeIntent = Intent(this, MainActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)
        }
        startActivity(homeIntent)
    }
}