
package com.example.javier_parada_herpm1305_23

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EquipoActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var ttsReady = false

    // URI de destino de la fotografía
    private var fotoUri: Uri? = null

    // Lanzador de cámara
    private val lanzadorCamara = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { resultado ->
        val uri = fotoUri

        if (resultado && uri != null) {

            // Publicar la fotografía en la galería
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val valores = ContentValues().apply {
                    put(MediaStore.Images.Media.IS_PENDING, 0)
                }

                contentResolver.update(uri, valores, null, null)
            }

            Toast.makeText(
                this,
                "¡Fotografía guardada correctamente! 📸",
                Toast.LENGTH_LONG
            ).show()

        } else {

            // Eliminar archivo si se canceló la captura
            uri?.let {
                contentResolver.delete(it, null, null)
            }

            Toast.makeText(
                this,
                "Captura cancelada.",
                Toast.LENGTH_SHORT
            ).show()
        }

        fotoUri = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_equipo)

        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.main)
        ) { v, insets ->
            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )

            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }

        // Inicializar el motor de voz
        tts = TextToSpeech(this, this)

        // Interacción con Valentina
        val cardValentina = findViewById<android.view.View>(
            R.id.cardValentina
        )

        cardValentina.setOnClickListener {
            hablarValentina()
        }

        // Botón de captura fotográfica
        val btnCapturarFoto = findViewById<android.view.View>(
            R.id.btnCapturarFoto
        )

        btnCapturarFoto.setOnClickListener {
            capturarFotografia()
        }

        // Botón para abrir las estadísticas de ventas
        val btnEstadisticasVentas = findViewById<android.view.View>(
            R.id.btnEstadisticasVentas
        )

        btnEstadisticasVentas.setOnClickListener {
            val intent = Intent(this, VentasActivity::class.java)
            startActivity(intent)
        }
    }

    // Crear ubicación de almacenamiento de la fotografía
    private fun crearUriFoto(): Uri? {

        val fecha = SimpleDateFormat(
            "yyyyMMdd_HHmmss",
            Locale.getDefault()
        ).format(Date())

        val nombreArchivo = "MyRetail_$fecha.jpg"

        val valores = ContentValues().apply {
            put(
                MediaStore.Images.Media.DISPLAY_NAME,
                nombreArchivo
            )

            put(
                MediaStore.Images.Media.MIME_TYPE,
                "image/jpeg"
            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(
                    MediaStore.Images.Media.RELATIVE_PATH,
                    Environment.DIRECTORY_PICTURES + "/MyRetail"
                )

                put(
                    MediaStore.Images.Media.IS_PENDING,
                    1
                )
            }
        }

        return contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            valores
        )
    }

    // Abrir la cámara y capturar fotografía
    private fun capturarFotografia() {

        val uri = crearUriFoto()

        if (uri != null) {

            fotoUri = uri

            try {
                lanzadorCamara.launch(uri)

            } catch (e: Exception) {

                contentResolver.delete(uri, null, null)
                fotoUri = null

                Toast.makeText(
                    this,
                    "No fue posible abrir la cámara.",
                    Toast.LENGTH_LONG
                ).show()
            }

        } else {

            Toast.makeText(
                this,
                "No fue posible preparar el almacenamiento de la fotografía.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // Configuración del idioma del motor de voz
    override fun onInit(status: Int) {

        if (status == TextToSpeech.SUCCESS) {

            val resultado = tts?.setLanguage(
                Locale("es", "CL")
            )

            if (resultado != TextToSpeech.LANG_MISSING_DATA &&
                resultado != TextToSpeech.LANG_NOT_SUPPORTED
            ) {

                ttsReady = true

            } else {

                val idiomaAlternativo = tts?.setLanguage(
                    Locale("es", "ES")
                )

                ttsReady =
                    idiomaAlternativo != TextToSpeech.LANG_MISSING_DATA &&
                            idiomaAlternativo != TextToSpeech.LANG_NOT_SUPPORTED
            }
        }
    }

    // Mensaje de presentación de Valentina
    private fun hablarValentina() {

        if (ttsReady) {

            val mensaje =
                "¡Hola! Soy Valentina Díaz, la vendedora del área de deportes. " +
                        "¡Estoy aquí para ayudarte a encontrar lo que necesitas!"

            tts?.speak(
                mensaje,
                TextToSpeech.QUEUE_FLUSH,
                null,
                "saludo_valentina"
            )

        } else {

            Toast.makeText(
                this,
                "El motor de voz todavía no está disponible.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Liberar recursos al cerrar la pantalla
    override fun onDestroy() {

        tts?.stop()
        tts?.shutdown()
        tts = null

        super.onDestroy()
    }
}
