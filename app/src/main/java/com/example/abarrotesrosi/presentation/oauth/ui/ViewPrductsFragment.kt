package com.example.abarrotesrosi.presentation.oauth.ui

import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.example.abarrotesrosi.R

data class Product(
    var id: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val precio: Double = 0.0,
    val piezas: Int = 0,
    val categoria: String = "",
    val codigo: String = "",
    val imagenBase64: String = ""
)

class ViewProductsFragment : Fragment() {
    private lateinit var recycler: RecyclerView
    private lateinit var db: FirebaseFirestore
    private val listaProductos = mutableListOf<Product>()
    private lateinit var adapter: ProductAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_viewproducts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recycler = view.findViewById(R.id.recyclerProducts)
        recycler.layoutManager = LinearLayoutManager(requireContext())

        db = FirebaseFirestore.getInstance()

        adapter = ProductAdapter(
            listaProductos,
            onDelete = { eliminarProducto(it) },
            onEdit = { editarProducto(it) }
        )

        recycler.adapter = adapter

        obtenerProductos()

        view.findViewById<Button>(R.id.btnbew).setOnClickListener {
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
                    product.id = document.id
                    listaProductos.add(product)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error al cargar productos", Toast.LENGTH_SHORT).show()
            }
    }

    private fun eliminarProducto(product: Product) {
        db.collection("products")
            .document(product.id)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Eliminado", Toast.LENGTH_SHORT).show()
                obtenerProductos()
            }
    }

    private fun editarProducto(product: Product) {
        val bundle = Bundle().apply {
            putString("id", product.id)
            putString("nombre", product.nombre)
            putString("descripcion", product.descripcion)
            putDouble("precio", product.precio)
            putInt("piezas", product.piezas)
            putString("categoria", product.categoria)
            putString("codigo", product.codigo)
            putString("imagenBase64", product.imagenBase64)
        }

        findNavController().navigate(
            R.id.action_ViewProductsFragment_to_editProductsFragment,
            bundle
        )
    }
}

class ProductAdapter(
    private val lista: List<Product>,
    private val onDelete: (Product) -> Unit,
    private val onEdit: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgProducto: ImageView = view.findViewById(R.id.imgProductoItem)
        val nombre: TextView = view.findViewById(R.id.tvNombre)
        val descripcion: TextView = view.findViewById(R.id.tvDescripcion)
        val precio: TextView = view.findViewById(R.id.tvPrecio)
        val piezas: TextView = view.findViewById(R.id.tvPiezas)
        val categoria: TextView = view.findViewById(R.id.tvCategoria)
        val btnEditar: ImageButton = view.findViewById(R.id.btnEditar)
        val btnEliminar: ImageButton = view.findViewById(R.id.btnEliminar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_itemproducts, parent, false)
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

        if (product.imagenBase64.isNotEmpty()) {
            val bitmap = ImageUtils.base64ToBitmap(product.imagenBase64)
            Glide.with(holder.itemView.context)
                .load(bitmap)
                .into(holder.imgProducto)
        } else {
            holder.imgProducto.setImageResource(R.drawable.logorosi)
        }

        holder.btnEliminar.setOnClickListener {
            onDelete(product)
        }

        holder.btnEditar.setOnClickListener {
            onEdit(product)
        }
    }
}