package com.example.abarrotesrosi.presentation.oauth.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.abarrotesrosi.R
import com.example.abarrotesrosi.databinding.FragmentVewbuyBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.WriteBatch

class VeiwBuyFragment : Fragment() {

    private var _binding: FragmentVewbuyBinding? = null
    private val binding get() = _binding!!

    private val db = FirebaseFirestore.getInstance()

    private var totalCompra: Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVewbuyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        @Suppress("DEPRECATION")
        val productos = arguments?.getSerializable("productos") as? ArrayList<ProductBuy> ?: arrayListOf()
        totalCompra = arguments?.getDouble("total", 0.0) ?: 0.0

        binding.tvResumenCompra.layoutManager = LinearLayoutManager(requireContext())
        binding.tvResumenCompra.adapter = VewiBuyAdapter(productos)

        binding.tvTotal.text = "$${String.format("%.2f", totalCompra)}"

        binding.btnComprar.setOnClickListener {
            if (productos.isEmpty()) {
                Toast.makeText(requireContext(), "No hay productos para comprar", Toast.LENGTH_SHORT).show()
            } else {
                actualizarStockYFinalizarCompra(productos)
            }
        }
    }

    private fun actualizarStockYFinalizarCompra(productos: ArrayList<ProductBuy>) {
        binding.btnComprar.isEnabled = false
        binding.btnComprar.text = "Procesando..."

        val batch = db.batch()
        var productosProcesados = 0
        var huboError = false

        for (producto in productos) {
            val docRef = db.collection("products").document(producto.id)

            docRef.get()
                .addOnSuccessListener { document ->
                    if (!document.exists()) {
                        huboError = true
                        Toast.makeText(
                            requireContext(),
                            "El producto ${producto.nombre} no existe en la base de datos",
                            Toast.LENGTH_LONG
                        ).show()
                        restaurarBoton()
                        return@addOnSuccessListener
                    }

                    val stockActual = document.getLong("piezas")?.toInt() ?: 0
                    val cantidadComprada = producto.cantidad

                    if (stockActual < cantidadComprada) {
                        huboError = true
                        Toast.makeText(
                            requireContext(),
                            "No hay suficiente stock de ${producto.nombre}",
                            Toast.LENGTH_LONG
                        ).show()
                        restaurarBoton()
                        return@addOnSuccessListener
                    }

                    val nuevoStock = stockActual - cantidadComprada
                    batch.update(docRef, "piezas", nuevoStock)

                    productosProcesados++

                    if (productosProcesados == productos.size && !huboError) {
                        guardarCambios(batch)
                    }
                }
                .addOnFailureListener {
                    huboError = true
                    Toast.makeText(
                        requireContext(),
                        "Error al consultar productos",
                        Toast.LENGTH_SHORT
                    ).show()
                    restaurarBoton()
                }
        }
    }

    private fun guardarCambios(batch: WriteBatch) {
        batch.commit()
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Compra realizada con éxito", Toast.LENGTH_SHORT).show()

                val bundle = bundleOf(
                    "total" to totalCompra
                )

                findNavController().navigate(R.id.successBuyFragment, bundle)
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error al actualizar stock", Toast.LENGTH_SHORT).show()
                restaurarBoton()
            }
    }

    private fun restaurarBoton() {
        binding.btnComprar.isEnabled = true
        binding.btnComprar.text = "Comprar"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}