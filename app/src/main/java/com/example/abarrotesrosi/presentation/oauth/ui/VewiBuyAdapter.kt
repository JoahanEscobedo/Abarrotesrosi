package com.example.abarrotesrosi.presentation.oauth.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.abarrotesrosi.R

class VewiBuyAdapter(private val lista: List<ProductBuy>) :
    RecyclerView.Adapter<VewiBuyAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tvNombre)
        val tvCantidad: TextView = view.findViewById(R.id.tvCantidad)
        val tvSubtotal: TextView = view.findViewById(R.id.tvSubtotal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_itemvewbuy, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val producto = lista[position]

        val subtotal = producto.cantidad * producto.precio

        holder.tvNombre.text = producto.nombre
        holder.tvCantidad.text = "x${producto.cantidad}"
        holder.tvSubtotal.text = "$${String.format("%.2f", subtotal)}"
    }

    override fun getItemCount() = lista.size
}