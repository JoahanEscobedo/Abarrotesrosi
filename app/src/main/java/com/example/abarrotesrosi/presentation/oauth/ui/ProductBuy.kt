package com.example.abarrotesrosi.presentation.oauth.ui

import java.io.Serializable

data class ProductBuy(
    var id: String = "",
    var nombre: String = "",
    var precio: Double = 0.0,
    var piezas: Int = 0,
    var cantidad: Int = 0,
    var stockOriginal: Int = 0
) : Serializable