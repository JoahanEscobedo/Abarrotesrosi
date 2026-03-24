package com.example.abarrotesrosi.presentation.oauth.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.abarrotesrosi.databinding.FragmentHomeBinding
import com.google.firebase.firestore.FirebaseFirestore

data class ProductBuy(
    var id: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val precio: Double = 0.0,
    var piezas: Int = 0,
    val categoria: String = "",
    var codigo: String = "",
    var cantidad: Int = 0,
    var stockOriginal: Int = 0
)

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    val db = FirebaseFirestore.getInstance()

    private lateinit var adapter: ProductBuyAdapter
    private val lista = mutableListOf<ProductBuy>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configurarRecycler()
        obtenerProductos()
        binding.btnFinalizarVenta.setOnClickListener {
            android.widget.Toast.makeText(requireContext(), "Venta realizada con éxito", android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    private fun configurarRecycler() {
        adapter = ProductBuyAdapter(lista)

        binding.rvProductsHome.layoutManager = LinearLayoutManager(requireContext())
        binding.rvProductsHome.adapter = adapter
    }

    private fun obtenerProductos() {
        db.collection("products")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener

                lista.clear()
                for (doc in snapshot!!) {
                    val producto = doc.toObject(ProductBuy::class.java)
                    producto.id = doc.id

                    producto.stockOriginal = producto.piezas

                    lista.add(producto)
                }
                adapter.notifyDataSetChanged()
            }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}