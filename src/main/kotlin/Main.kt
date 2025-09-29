package com.duoc

import kotlinx.coroutines.*

fun main() = runBlocking {
    println("=== SISTEMA BOOKSMART ===")
    println()

    val gestor = GestorPrestamos()
    val catalogo = gestor.inicializarCatalogo()

    try {
        // 1. Mostrar catálogo disponible
        println("Catálogo disponible:")
        catalogo.forEachIndexed { index, libro ->
            val disponibilidad = if (libro is LibroFisico && libro.esReferencia) {
                "(Físico • Referencia: NO SE PRESTA)"
            } else when (libro) {
                is LibroFisico -> "(Físico)"
                is LibroDigital -> if (libro.drm) "(Digital • DRM)" else "(Digital)"
                else -> ""
            }
            println("${index + 1}. ${libro.titulo} - $ ${libro.precioBase.toInt()} $disponibilidad")
        }
        println()

        // 2. Selección de libros para préstamo
        print("Seleccione libros para préstamo (números separados por coma): ")
        val input = readLine() ?: ""
        val indicesSeleccionados = input.split(",").mapNotNull {
            it.trim().toIntOrNull()?.minus(1)
        }.filter { it in 0 until catalogo.size }

        if (indicesSeleccionados.isEmpty()) {
            println("No se seleccionaron libros válidos.")
            return@runBlocking
        }

        val librosSeleccionados = indicesSeleccionados.map { catalogo[it] }
        println("Tipo de usuario (estudiante/docente/externo): ", )
        val tipoUsuarioInput = readLine()?.lowercase() ?: "externo"

        val tipoUsuario = when (tipoUsuarioInput) {
            "estudiante" -> TipoUsuario.ESTUDIANTE
            "docente" -> TipoUsuario.DOCENTE
            else -> TipoUsuario.EXTERNO
        }

        println()
        println("Validando solicitud...")

        // 3. Validar cada libro seleccionado
        val librosValidos = mutableListOf<Libro>()
        for (libro in librosSeleccionados) {
            val estado = gestor.validarPrestamo(libro)
            when (estado) {
                is EstadoPrestamo.Error -> {
                    println("- Libro #${catalogo.indexOf(libro) + 1} (Referencia) no fue seleccionado, no se puede prestar. OK")
                }
                else -> {
                    librosValidos.add(libro)
                    if (libro is LibroDigital && libro.drm) {
                        println("- Verificación DRM para libros digitales seleccionados... OK")
                    }
                }
            }
        }

        if (librosValidos.isEmpty()) {
            println("No hay libros válidos para préstamo.")
            return@runBlocking
        }

        println()
        println("Procesando préstamo...")

        // 4. Procesar préstamos de forma asíncrona
        val librosPrestados = mutableListOf<Libro>()
        for (libro in librosValidos) {
            val estado = gestor.procesarPrestamoAsincrono(libro)
            when (estado) {
                is EstadoPrestamo.EnPrestamo -> {
                    librosPrestados.add(libro)
                }
                is EstadoPrestamo.Error -> {
                    println("Error procesando ${libro.titulo}: ${estado.mensaje}")
                }
                else -> println("${libro.titulo}: ${estado::class.simpleName}")
            }
        }

        if (librosPrestados.isEmpty()) {
            println("No se pudieron procesar préstamos.")
            return@runBlocking
        }

        println("Estado: En Préstamo")
        println()

        // 5. Mostrar resumen del préstamo
        println("=== RESUMEN DEL PRÉSTAMO ===")
        librosPrestados.forEach { libro ->
            val tipo = when (libro) {
                is LibroFisico -> "Físico"
                is LibroDigital -> if (libro.drm) "Digital • DRM" else "Digital"
                else -> ""
            }
            println("- ${libro.titulo} ($tipo): $ ${libro.costoFinal().toInt()}")
        }
        println()

        // 6. Calcular totales
        val subtotal = librosPrestados.sumOf { it.costoFinal() }
        val descuento = gestor.aplicarDescuento(subtotal, tipoUsuario)
        val porcentajeDescuento = (tipoUsuario.descuento * 100).toInt()
        val multa = 0.0 // Sin retraso en este ejemplo
        val total = subtotal - descuento + multa

        println("Subtotal: $ ${subtotal.toInt()}")
        println("Descuento ${tipoUsuario.name.lowercase().replaceFirstChar { it.uppercase() }} ($porcentajeDescuento%): -$ ${descuento.toInt()}")
        println("Multa por retraso: $ ${multa.toInt()} (sin retraso)")
        println()
        println("TOTAL: $ ${total.toInt()}")
        println()
        println("Estado final: En Préstamo")
        println("Tiempo estimado para retiro/activación digital: 3 s")

    } catch (e: Exception) {
        println("❌ Error crítico en la aplicación: ${e.message}")
        e.printStackTrace()
    }
}