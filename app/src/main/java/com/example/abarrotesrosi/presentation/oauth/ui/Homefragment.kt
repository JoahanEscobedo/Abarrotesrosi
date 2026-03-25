package com.example.abarrotesrosi.presentation.oauth.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.abarrotesrosi.R
import com.example.abarrotesrosi.databinding.FragmentHomeBinding
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val db = FirebaseFirestore.getInstance()

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

            val productosComprados = lista.filter { it.cantidad > 0 }

            if (productosComprados.isEmpty()) {
                Toast.makeText(requireContext(), "No hay productos seleccionados", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            var total = 0.0
            productosComprados.forEach { producto ->
                total += producto.cantidad * producto.precio
            }

            val bundle = Bundle().apply {
                putSerializable("productos", ArrayList(productosComprados))
                putDouble("total", total)
            }

            try {
                findNavController().navigate(
                    R.id.action_homeFragment_to_veiwBuyFragment,
                    bundle
                )
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Error al abrir resumen: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
                e.printStackTrace()
            }
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

                if (error != null) {
                    Toast.makeText(requireContext(), "Error al cargar productos", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (snapshot == null) return@addSnapshotListener

                lista.clear()

                for (doc in snapshot.documents) {
                    val producto = doc.toObject(ProductBuy::class.java)

                    if (producto != null) {
                        producto.id = doc.id
                        producto.stockOriginal = producto.piezas
                        lista.add(producto)
                    }
                }

                adapter.notifyDataSetChanged()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}