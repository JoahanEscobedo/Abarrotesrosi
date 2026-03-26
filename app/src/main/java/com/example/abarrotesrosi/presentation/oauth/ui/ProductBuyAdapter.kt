package com.example.abarrotesrosi.presentation.oauth.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.abarrotesrosi.R

class ProductBuyAdapter(
    private val lista: MutableList<ProductBuy>
) : RecyclerView.Adapter<ProductBuyAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgProducto: ImageView = view.findViewById(R.id.imgProductoBuy)
        val tvNombre: TextView = view.findViewById(R.id.tvNombre)
        val tvPrecio: TextView = view.findViewById(R.id.tvPrecio)
        val tvPiezas: TextView = view.findViewById(R.id.tvPiezas)
        val btnMas: Button = view.findViewById(R.id.btnMas)
        val btnMenos: Button = view.findViewById(R.id.btnMenos)
        val tvCantidad: TextView = view.findViewById(R.id.tvCantidad)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_buyproducts, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = lista.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val producto = lista[position]

        holder.tvNombre.text = producto.nombre
        holder.tvPrecio.text = "Precio: $${producto.precio}"
        holder.tvPiezas.text = "Stock: ${producto.piezas}"
        holder.tvCantidad.text = producto.cantidad.toString()

        if (producto.imagenBase64.isNotEmpty()) {
            val bitmap = ImageUtils.base64ToBitmap(producto.imagenBase64)
            Glide.with(holder.itemView.context)
                .load(bitmap)
                .into(holder.imgProducto)
        } else {
            holder.imgProducto.setImageResource(R.drawable.logorosi)
        }

        holder.btnMas.setOnClickListener {
            if (producto.piezas > 0) {
                producto.piezas--
                producto.cantidad++
                notifyItemChanged(position)
            }
        }

        holder.btnMenos.setOnClickListener {
            if (producto.cantidad > 0 && producto.piezas < producto.stockOriginal) {
                producto.piezas++
                producto.cantidad--
                notifyItemChanged(position)
            }
        }
    }
}