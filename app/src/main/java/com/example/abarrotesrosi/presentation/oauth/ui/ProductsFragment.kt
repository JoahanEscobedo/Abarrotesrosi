package com.example.abarrotesrosi.presentation.oauth.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.abarrotesrosi.R
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore

class ProductsFragment: Fragment(){

    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_products, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        db = FirebaseFirestore.getInstance()

        val nombre = view.findViewById<EditText>(R.id.etNombre)
        val descripcion = view.findViewById<EditText>(R.id.etDescripcion)
        val precio = view.findViewById<EditText>(R.id.etPrecio)
        val piezas = view.findViewById<EditText>(R.id.etPiezas)
        val codigo = view.findViewById<EditText>(R.id.etCodigo)
        val categoria = view.findViewById<Spinner>(R.id.spCategoria)
        val btnGuardar = view.findViewById<Button>(R.id.btnGuardar)

        // Lista de categorías
        val categorias = listOf("Abarrotes", "Bebidas", "Limpieza", "Snacks", "Otros")

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            categorias
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categoria.adapter = adapter

        btnGuardar.setOnClickListener {

            val precioValue = precio.text.toString().toDoubleOrNull() ?: 0.0
            val piezasValue = piezas.text.toString().toIntOrNull() ?: 0

            val product = hashMapOf(
                "nombre" to nombre.text.toString(),
                "descripcion" to descripcion.text.toString(),
                "precio" to precioValue,
                "piezas" to piezasValue,
                "categoria" to categoria.selectedItem.toString(),
                "codigo" to codigo.text.toString()
            )

            db.collection("products")
                .add(product)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Producto guardado", Toast.LENGTH_SHORT).show()
                    limpiarCampos(nombre, descripcion, precio, piezas, codigo)
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Error al guardar", Toast.LENGTH_SHORT).show()
                }
        }
        val btnviewproducs = view.findViewById<Button>(R.id.btnviewproducs)

        btnviewproducs.setOnClickListener {
            findNavController().navigate(R.id.action_ProductsFragment_to_ViewProductsFragment)
        }
    }

    private fun limpiarCampos(
        nombre: EditText,
        descripcion: EditText,
        precio: EditText,
        piezas: EditText,
        codigo: EditText
    ) {
        nombre.text.clear()
        descripcion.text.clear()
        precio.text.clear()
        piezas.text.clear()
        codigo.text.clear()
    }
}