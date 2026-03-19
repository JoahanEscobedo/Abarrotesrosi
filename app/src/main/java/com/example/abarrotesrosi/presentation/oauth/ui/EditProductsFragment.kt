package com.example.abarrotesrosi.presentation.oauth.ui

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.abarrotesrosi.R
import com.google.firebase.firestore.FirebaseFirestore

class EditProductsFragment : Fragment(R.layout.fragment_editproduc) {

    private lateinit var db: FirebaseFirestore
    private var productId: String? = null

    // 1. Define las mismas categorías que usas en la pantalla de "Agregar"
    val categorias = listOf("Abarrotes", "Bebidas", "Limpieza", "Snacks", "Otros")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = FirebaseFirestore.getInstance()

        val etNombre = view.findViewById<EditText>(R.id.etNombre)
        val etDescripcion = view.findViewById<EditText>(R.id.etDescripcion)
        val etPrecio = view.findViewById<EditText>(R.id.etPrecio)
        val etPiezas = view.findViewById<EditText>(R.id.etPiezas)
        val spCategoria = view.findViewById<Spinner>(R.id.spCategoria) // Tu Spinner
        val etCodigo = view.findViewById<EditText>(R.id.etCodigo)
        val btnGuardar = view.findViewById<Button>(R.id.btnGuardar)

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categorias)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spCategoria.adapter = adapter

        arguments?.let { bundle ->
            productId = bundle.getString("id")
            etNombre.setText(bundle.getString("nombre"))
            etDescripcion.setText(bundle.getString("descripcion"))
            etPrecio.setText(bundle.getDouble("precio").toString())
            etPiezas.setText(bundle.getInt("piezas").toString())
            etCodigo.setText(bundle.getString("codigo"))

            // --- LÓGICA PARA QUE EL SPINNER SE QUEDE EN LA SELECCIÓN ACTUAL ---
            val categoriaDelProducto = bundle.getString("categoria")
            if (categoriaDelProducto != null) {
                // Buscamos la posición del texto en nuestro array
                val posicion = categorias.indexOf(categoriaDelProducto)
                if (posicion >= 0) {
                    spCategoria.setSelection(posicion) // Esto marca la opción actual
                }
            }
        }

        // 4. Botón para guardar los cambios en Firebase
        btnGuardar.setOnClickListener {
            val nombre = etNombre.text.toString()
            val descripcion = etDescripcion.text.toString()
            val precio = etPrecio.text.toString().toDoubleOrNull() ?: 0.0
            val piezas = etPiezas.text.toString().toIntOrNull() ?: 0
            val categoriaSeleccionada = spCategoria.selectedItem.toString() // Nueva categoría
            val codigo = etCodigo.text.toString()

            if (productId != null) {
                val productoActualizado = mapOf(
                    "nombre" to nombre,
                    "descripcion" to descripcion,
                    "precio" to precio,
                    "piezas" to piezas,
                    "categoria" to categoriaSeleccionada,
                    "codigo" to codigo
                )

                db.collection("products").document(productId!!)
                    .update(productoActualizado)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Producto actualizado", Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack() // Regresa a la lista
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "Error al actualizar", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }
}