package com.duoc

// Clase derivada para libros digitales
class LibroDigital(
    titulo: String,
    autor: String,
    precioBase: Double,
    diasPrestamo: Int,
    val drm: Boolean = false
) : Libro(titulo, autor, precioBase, diasPrestamo) {

    // Sobrescritura del método costoFinal
    override fun costoFinal(): Double {
        return if (drm) precioBase * 1.1 else precioBase // 10% más si tiene DRM
    }

    // Sobrescritura del método descripción
    override fun descripcion(): String {
        val tipoDigital = if (drm) "Digital con DRM" else "Digital sin DRM"
        return "$titulo por $autor - $tipoDigital - Precio: $${costoFinal()}"
    }
}