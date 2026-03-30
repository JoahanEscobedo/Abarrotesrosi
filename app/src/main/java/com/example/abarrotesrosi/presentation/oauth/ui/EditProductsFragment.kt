package com.example.abarrotesrosi.presentation.oauth.ui

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.abarrotesrosi.R
import com.google.firebase.firestore.FirebaseFirestore

class EditProductsFragment : Fragment(R.layout.fragment_editproduc) {

    private lateinit var db: FirebaseFirestore
    private var productId: String? = null
    private var imagenBase64Actual: String = ""
    private var imagenSeleccionadaUri: Uri? = null

    val categorias = listOf("Abarrotes", "Bebidas", "Limpieza", "Snacks", "Otros")

    private val seleccionarImagenLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                imagenSeleccionadaUri = data?.data

                val imageView = view?.findViewById<ImageView>(R.id.imgProducto)
                imageView?.setImageURI(imagenSeleccionadaUri)
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = FirebaseFirestore.getInstance()

        val etNombre = view.findViewById<EditText>(R.id.etNombre)
        val etDescripcion = view.findViewById<EditText>(R.id.etDescripcion)
        val etPrecio = view.findViewById<EditText>(R.id.etPrecio)
        val etPiezas = view.findViewById<EditText>(R.id.etPiezas)
        val spCategoria = view.findViewById<Spinner>(R.id.spCategoria)
        val etCodigo = view.findViewById<EditText>(R.id.etCodigo)
        val btnGuardar = view.findViewById<Button>(R.id.btnGuardar)
        val imgProducto = view.findViewById<ImageView>(R.id.imgProducto)
        val btnSeleccionarImagen = view.findViewById<Button>(R.id.btnSeleccionarImagen)

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categorias)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spCategoria.adapter = adapter

        btnSeleccionarImagen.setOnClickListener {
            abrirGaleria()
        }

        arguments?.let { bundle ->
            productId = bundle.getString("id")
            etNombre.setText(bundle.getString("nombre"))
            etDescripcion.setText(bundle.getString("descripcion"))
            etPrecio.setText(bundle.getDouble("precio").toString())
            etPiezas.setText(bundle.getInt("piezas").toString())
            etCodigo.setText(bundle.getString("codigo"))

            imagenBase64Actual = bundle.getString("imagenBase64", "")

            if (imagenBase64Actual.isNotEmpty()) {
                val bitmap = ImageUtils.base64ToBitmap(imagenBase64Actual)
                Glide.with(requireContext())
                    .load(bitmap)
                    .into(imgProducto)
            }

            val categoriaDelProducto = bundle.getString("categoria")
            if (categoriaDelProducto != null) {
                val posicion = categorias.indexOf(categoriaDelProducto)
                if (posicion >= 0) {
                    spCategoria.setSelection(posicion)
                }
            }
        }

        btnGuardar.setOnClickListener {
            val nombre = etNombre.text.toString()
            val descripcion = etDescripcion.text.toString()
            val precio = etPrecio.text.toString().toDoubleOrNull() ?: 0.0
            val piezas = etPiezas.text.toString().toIntOrNull() ?: 0
            val categoriaSeleccionada = spCategoria.selectedItem.toString()
            val codigo = etCodigo.text.toString()

            if (productId != null) {

                val bitmap = (imgProducto.drawable as? BitmapDrawable)?.bitmap
                val nuevaImagenBase64 = if (bitmap != null) {
                    ImageUtils.bitmapToBase64(bitmap)
                } else {
                    imagenBase64Actual
                }

                val productoActualizado = mapOf(
                    "nombre" to nombre,
                    "descripcion" to descripcion,
                    "precio" to precio,
                    "piezas" to piezas,
                    "categoria" to categoriaSeleccionada,
                    "codigo" to codigo,
                    "imagenBase64" to nuevaImagenBase64
                )

                db.collection("products").document(productId!!)
                    .update(productoActualizado)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Producto actualizado", Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack()
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "Error al actualizar", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    private fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        seleccionarImagenLauncher.launch(intent)
    }
}