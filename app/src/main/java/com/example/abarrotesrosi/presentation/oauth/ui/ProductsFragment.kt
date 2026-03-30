package com.example.abarrotesrosi.presentation.oauth.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.abarrotesrosi.R
import com.google.firebase.firestore.FirebaseFirestore

class ProductsFragment : Fragment() {

    private lateinit var db: FirebaseFirestore
    private var imagenSeleccionadaUri: Uri? = null

    private val seleccionarImagenLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                imagenSeleccionadaUri = data?.data

                val imageView = view?.findViewById<ImageView>(R.id.imgProducto)
                imageView?.setImageURI(imagenSeleccionadaUri)
            }
        }

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
        val btnviewproducs = view.findViewById<Button>(R.id.btnviewproducs)
        val btnSeleccionarImagen = view.findViewById<Button>(R.id.btnSeleccionarImagen)
        val imgProducto = view.findViewById<ImageView>(R.id.imgProducto)

        val categorias = listOf("Abarrotes", "Bebidas", "Limpieza", "Snacks", "Otros")

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            categorias
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categoria.adapter = adapter

        btnSeleccionarImagen.setOnClickListener {
            abrirGaleria()
        }

        btnGuardar.setOnClickListener {

            val precioValue = precio.text.toString().toDoubleOrNull() ?: 0.0
            val piezasValue = piezas.text.toString().toIntOrNull() ?: 0

            val bitmap = (imgProducto.drawable as? BitmapDrawable)?.bitmap
            val imagenBase64 = if (bitmap != null) {
                ImageUtils.bitmapToBase64(bitmap)
            } else {
                ""
            }

            val product = hashMapOf(
                "nombre" to nombre.text.toString(),
                "descripcion" to descripcion.text.toString(),
                "precio" to precioValue,
                "piezas" to piezasValue,
                "categoria" to categoria.selectedItem.toString(),
                "codigo" to codigo.text.toString(),
                "imagenBase64" to imagenBase64
            )

            db.collection("products")
                .add(product)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Producto guardado", Toast.LENGTH_SHORT).show()
                    limpiarCampos(nombre, descripcion, precio, piezas, codigo, imgProducto)
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Error al guardar", Toast.LENGTH_SHORT).show()
                }
        }

        btnviewproducs.setOnClickListener {
            findNavController().navigate(R.id.action_ProductsFragment_to_ViewProductsFragment)
        }
    }

    private fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        seleccionarImagenLauncher.launch(intent)
    }

    private fun limpiarCampos(
        nombre: EditText,
        descripcion: EditText,
        precio: EditText,
        piezas: EditText,
        codigo: EditText,
        imgProducto: ImageView
    ) {
        nombre.text.clear()
        descripcion.text.clear()
        precio.text.clear()
        piezas.text.clear()
        codigo.text.clear()
        imgProducto.setImageResource(R.drawable.logorosi)
        imagenSeleccionadaUri = null
    }
}