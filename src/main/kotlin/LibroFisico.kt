package com.duoc

// Clase derivada para libros físicos
class LibroFisico(
    titulo: String,
    autor: String,
    precioBase: Double,
    diasPrestamo: Int,
    val esReferencia: Boolean = false
) : Libro(titulo, autor, precioBase, diasPrestamo) {

    // Sobrescritura del método costoFinal
    override fun costoFinal(): Double {
        return if (esReferencia) 0.0 else precioBase
    }

    // Sobrescritura del método descripción
    override fun descripcion(): String {
        val tipo = if (esReferencia) "Referencia (No prestable)" else "Físico"
        return "$titulo por $autor - $tipo - Precio: $${costoFinal()}"
    }
}