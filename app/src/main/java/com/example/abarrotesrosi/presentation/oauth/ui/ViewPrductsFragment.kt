package com.example.abarrotesrosi.presentation.oauth.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.example.abarrotesrosi.R




class ViewProductsFragment : Fragment() {
    data class Product(
        val nombre: String = "",
        val descripcion: String = "",
        val precio: Double = 0.0,
        val piezas: Int = 0,
        val categoria: String = "",
        val codigo: String = ""
    )
    private lateinit var recycler: RecyclerView
    private lateinit var db: FirebaseFirestore
    private val listaProductos = mutableListOf<Product>()
    private lateinit var adapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_viewproducts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        recycler = view.findViewById(R.id.recyclerProducts)
        recycler.layoutManager = LinearLayoutManager(requireContext())

        adapter = ProductAdapter(listaProductos)
        recycler.adapter = adapter

        db = FirebaseFirestore.getInstance()

        obtenerProductos()

        val btnAgregar = view.findViewById<Button>(R.id.btnbew)

        btnAgregar.setOnClickListener {
            findNavController().navigate(R.id.action_ViewProductsFragment_to_ProductsFragment)
        }
    }


    private fun obtenerProductos() {
        db.collection("products")
            .get()
            .addOnSuccessListener { result ->

                listaProductos.clear()

                for (document in result) {
                    val product = document.toObject(Product::class.java)
                    listaProductos.add(product)
                }

                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error al cargar", Toast.LENGTH_SHORT).show()
            }
    }

    class ProductAdapter(private val lista: List<Product>) :
        RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val nombre: TextView = view.findViewById(R.id.tvNombre)
            val descripcion: TextView = view.findViewById(R.id.tvDescripcion)
            val precio: TextView = view.findViewById(R.id.tvPrecio)
            val piezas: TextView = view.findViewById(R.id.tvPiezas)
            val categoria: TextView = view.findViewById(R.id.tvCategoria)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_item_products, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount(): Int = lista.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val product = lista[position]

            holder.nombre.text = product.nombre
            holder.descripcion.text = product.descripcion
            holder.precio.text = "Precio: $${product.precio}"
            holder.piezas.text = "Stock: ${product.piezas}"
            holder.categoria.text = "Categoría: ${product.categoria}"
        }
    }
}