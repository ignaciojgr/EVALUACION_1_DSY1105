package com.duoc

import kotlinx.coroutines.*

// Enumeración para tipos de usuario
enum class TipoUsuario(val descuento: Double) {
    ESTUDIANTE(0.10),    // 10% descuento
    DOCENTE(0.15),       // 15% descuento
    EXTERNO(0.0)         // 0% descuento
}

// Clase para gestionar préstamos
class GestorPrestamos {

    // Inicializar catálogo con datos de prueba
    fun inicializarCatalogo(): List<Libro> {
        return listOf(
            LibroFisico("Estructuras de Datos", "Goodrich", 12990.0, 7, false),
            LibroFisico("Diccionario Enciclopédico", "Varios", 15990.0, 0, true),
            LibroDigital("Programación en Kotlin", "JetBrains", 9990.0, 10, true),
            LibroDigital("Algoritmos Básicos", "Cormen", 11990.0, 10, false)
        )
    }

    // Función para mostrar catálogo
    fun mostrarCatalogo(catalogo: List<Libro>) {
        println("=== CATÁLOGO DE LIBROS BOOKSMART ===")
        catalogo.forEachIndexed { index, libro ->
            println("${index + 1}. ${libro.descripcion()}")
            if (libro is LibroFisico && libro.esReferencia) {
                println("   ⚠️  Este libro NO está disponible para préstamo")
            }
            println("   Días de préstamo: ${libro.diasPrestamo}")
            println()
        }
    }

    // Función para calcular multa por retraso
    fun calcularMulta(diasRetraso: Int): Double {
        return if (diasRetraso > 0) diasRetraso * 500.0 else 0.0 // $500 por día de retraso
    }

    // Función para aplicar descuento según tipo de usuario
    fun aplicarDescuento(subtotal: Double, tipoUsuario: TipoUsuario): Double {
        return subtotal * tipoUsuario.descuento
    }

    // Función para validar préstamo
    fun validarPrestamo(libro: Libro): EstadoPrestamo {
        return try {
            when {
                libro.precioBase < 0 -> EstadoPrestamo.Error("Precio inválido: no puede ser negativo")
                libro.diasPrestamo < 0 -> EstadoPrestamo.Error("Días de préstamo inválidos: no pueden ser negativos")
                libro is LibroFisico && libro.esReferencia -> EstadoPrestamo.Error("Libro de referencia no disponible para préstamo")
                else -> EstadoPrestamo.Pendiente
            }
        } catch (e: Exception) {
            EstadoPrestamo.Error("Error en validación: ${e.message}")
        }
    }

    // Función asíncrona para simular procesamiento de préstamo
    suspend fun procesarPrestamoAsincrono(libro: Libro): EstadoPrestamo {
        return try {
            val estadoInicial = validarPrestamo(libro)

            if (estadoInicial is EstadoPrestamo.Error) {
                return estadoInicial
            }

            println("⏳ Procesando préstamo de '${libro.titulo}'...")
            delay(3000) // Simular 3 segundos de procesamiento

            // Simular éxito en el préstamo
            EstadoPrestamo.EnPrestamo

        } catch (e: Exception) {
            EstadoPrestamo.Error("Error durante el procesamiento: ${e.message}")
        }
    }

    // Función para generar reporte usando operaciones funcionales
    fun generarReporte(librosPrestados: List<Libro>): String {
        val totalLibros = librosPrestados.size
        val costoTotal = librosPrestados.sumOf { it.costoFinal() }
        val librosDigitales = librosPrestados.filter { it is LibroDigital }.size
        val librosFisicos = librosPrestados.filter { it is LibroFisico }.size
        val promedioPrecios = if (totalLibros > 0) costoTotal / totalLibros else 0.0

        return """
        === REPORTE DE PRÉSTAMOS ===
        Total de libros prestados: $totalLibros
        Libros físicos: $librosFisicos
        Libros digitales: $librosDigitales
        Costo total: $${costoTotal}
        Precio promedio: $${String.format("%.2f", promedioPrecios)}
        """.trimIndent()
    }

    // Función para mostrar resumen final
    fun mostrarResumenFinal(
        librosPrestados: List<Libro>,
        tipoUsuario: TipoUsuario,
        diasRetraso: Int = 0
    ) {
        val subtotal = librosPrestados.sumOf { it.costoFinal() }
        val descuento = aplicarDescuento(subtotal, tipoUsuario)
        val multa = calcularMulta(diasRetraso)
        val total = subtotal - descuento + multa

        println("\n=== RESUMEN DE PRÉSTAMO ===")
        println("Libros prestados:")
        librosPrestados.forEach { libro ->
            println("- ${libro.titulo}: $${libro.costoFinal()}")
        }

        println("\n--- CÁLCULOS ---")
        println("Subtotal: $${subtotal}")
        println("Descuento (${tipoUsuario.name}): -$${String.format("%.2f", descuento)}")

        if (multa > 0) {
            println("Multa por $diasRetraso días de retraso: +$${multa}")
        }

        println("TOTAL A PAGAR: $${String.format("%.2f", total)}")
        println()
    }
}
