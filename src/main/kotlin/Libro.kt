package com.duoc

// Clase base abierta para herencia
open class Libro(
    val titulo: String,
    val autor: String,
    val precioBase: Double,
    val diasPrestamo: Int
) {
    // Método virtual que puede ser sobrescrito
    open fun costoFinal(): Double = precioBase

    // Método virtual para descripción
    open fun descripcion(): String = "$titulo por $autor - Precio: $${precioBase}"
}

