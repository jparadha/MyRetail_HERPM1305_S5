package com.example.javier_parada_herpm1305_23

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class VentasActivity : AppCompatActivity() {

    private lateinit var chartContainer: FrameLayout

    private val colorNaranja = Color.rgb(245, 130, 32)
    private val colorAzul = Color.rgb(20, 36, 59)
    private val colorGris = Color.rgb(123, 132, 145)
    private val colorFondoBarra = Color.rgb(235, 238, 242)

    private val ventas = listOf(
        Vendedor("Andrés", 28),
        Vendedor("Camila", 35),
        Vendedor("Valentina", 42),
        Vendedor("Felipe", 31),
        Vendedor("Diego", 24),
        Vendedor("Carla", 38)
    )

    data class Vendedor(
        val nombre: String,
        val unidades: Int
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ventas)

        chartContainer = findViewById(R.id.chartContainer)

        crearGrafico()
    }

    private fun crearGrafico() {

        chartContainer.removeAllViews()

        val contenedorGrafico = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(8), dp(8), dp(8), dp(8))
        }

        // Escala del gráfico
        contenedorGrafico.addView(crearEscala())

        // Filas de vendedores
        ventas.forEach { vendedor ->
            contenedorGrafico.addView(crearFilaVendedor(vendedor))
        }

        chartContainer.addView(
            contenedorGrafico,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.TOP
            }
        )
    }

    private fun crearEscala(): LinearLayout {

        val escala = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val espacioNombre = View(this)

        escala.addView(
            espacioNombre,
            LinearLayout.LayoutParams(dp(76), dp(28))
        )

        val etiquetas = listOf("0", "10", "20", "30", "40")

        val zonaEscala = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
        }

        etiquetas.forEach { numero ->

            val etiqueta = TextView(this).apply {
                text = numero
                textSize = 10f
                setTextColor(colorGris)
                gravity = Gravity.CENTER
            }

            zonaEscala.addView(
                etiqueta,
                LinearLayout.LayoutParams(0, dp(28), 1f)
            )
        }

        escala.addView(
            zonaEscala,
            LinearLayout.LayoutParams(0, dp(28), 1f)
        )

        escala.addView(
            View(this),
            LinearLayout.LayoutParams(dp(38), dp(28))
        )

        return escala
    }

    private fun crearFilaVendedor(vendedor: Vendedor): LinearLayout {

        val fila = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, dp(8), 0, dp(8))
        }

        // Nombre del vendedor
        val nombre = TextView(this).apply {
            text = vendedor.nombre
            textSize = 12f
            setTextColor(colorAzul)
            gravity = Gravity.CENTER_VERTICAL
        }

        fila.addView(
            nombre,
            LinearLayout.LayoutParams(dp(76), dp(36))
        )

        // Área de la barra
        val areaBarra = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            background = crearFondoBarra()
        }

        val barra = View(this).apply {
            background = crearBarraNaranja()
        }

        val espacioRestante = View(this)

        val maximo = 42f

        areaBarra.addView(
            barra,
            LinearLayout.LayoutParams(
                0,
                dp(16),
                vendedor.unidades.toFloat()
            )
        )

        areaBarra.addView(
            espacioRestante,
            LinearLayout.LayoutParams(
                0,
                dp(16),
                maximo - vendedor.unidades
            )
        )

        fila.addView(
            areaBarra,
            LinearLayout.LayoutParams(0, dp(36), 1f)
        )

        // Valor de ventas
        val valor = TextView(this).apply {
            text = "${vendedor.unidades} u."
            textSize = 11f
            setTextColor(colorAzul)
            gravity = Gravity.CENTER
        }

        fila.addView(
            valor,
            LinearLayout.LayoutParams(dp(38), dp(36))
        )

        return fila
    }

    private fun crearBarraNaranja(): GradientDrawable {

        return GradientDrawable().apply {
            setColor(colorNaranja)
            cornerRadius = dp(8).toFloat()
        }
    }

    private fun crearFondoBarra(): GradientDrawable {

        return GradientDrawable().apply {
            setColor(colorFondoBarra)
            cornerRadius = dp(8).toFloat()
        }
    }

    private fun dp(valor: Int): Int {
        return (valor * resources.displayMetrics.density).toInt()
    }
}