package com.duoc

// Clase sellada para manejar los diferentes estados de préstamo
sealed class EstadoPrestamo {
    object Pendiente : EstadoPrestamo()
    object EnPrestamo : EstadoPrestamo()
    object Devuelto : EstadoPrestamo()
    data class Error(val mensaje: String) : EstadoPrestamo()
}
