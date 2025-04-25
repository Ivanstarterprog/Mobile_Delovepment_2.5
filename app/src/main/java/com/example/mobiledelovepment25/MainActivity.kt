package com.example.mobiledelovepment25

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mobiledelovepment25.databinding.ActivityMainBinding
import com.github.dhaval2404.colorpicker.ColorPickerDialog

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val addedColors = mutableSetOf<Int>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setListeners()
        addColorButton(Color.BLACK)
    }

    private fun setListeners() {
        setBrushSizeListener()
        setFileButtonsListeners()
    }

    private fun setBrushSizeListener() {
        binding.apply {
            binding.brushSizeSeekBar.setOnSeekBarChangeListener(object :
                SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    binding.drawingView.setBrushSize(progress.toFloat())
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })
        }
    }

    private fun setFileButtonsListeners() {
        binding.apply {
            val pickImageLauncher =
                registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
                    uri?.let {
                        val inputStream = contentResolver.openInputStream(uri)
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        drawingView.setBackgroundBitmap(bitmap)
                    }
                }

            loadButton.setOnClickListener {
                pickImageLauncher.launch("image/*")
            }


            val saveImageLauncher =
                registerForActivityResult(ActivityResultContracts.CreateDocument("image/png")) { uri ->
                    uri?.let {
                        try {
                            val outputStream = contentResolver.openOutputStream(uri)
                            if (outputStream != null) {
                                drawingView.getBitmap()
                                    ?.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                            }
                            outputStream?.close()
                            Toast.makeText(
                                this@MainActivity,
                                "Шедевр сохранён!",
                                Toast.LENGTH_SHORT
                            ).show()
                        } catch (e: Exception) {
                            Toast.makeText(
                                this@MainActivity,
                                "Слишком круто, не могу сохранить",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

            saveButton.setOnClickListener {
                saveImageLauncher.launch("drawing_${System.currentTimeMillis()}.png")
            }

            addColorButton.setOnClickListener {
                showColorPickerDialog()
            }

            clearCanvasButton.setOnClickListener {
                drawingView.clearCanvas()
            }
        }
    }

    private fun showColorPickerDialog() {
        val colorPicker = ColorPickerDialog.Builder(this)
            .setTitle("Выберите цвет")
            .setColorListener { color, colorHex ->
                addColorButton(color)
                Toast.makeText(this@MainActivity, "Выбран цвет: $colorHex", Toast.LENGTH_SHORT)
                    .show()
            }
            .setPositiveButton("OK")
            .build()

        colorPicker.show()
    }

    private fun addColorButton(color: Int) {
        binding.apply {
            if (addedColors.contains(color)) return

            addedColors.add(color)

            val colorButton = ImageButton(applicationContext).apply {
                layoutParams = LinearLayout.LayoutParams(40.dpToPx(), 40.dpToPx()).apply {
                    marginStart = 8.dpToPx()
                }
                setBackgroundColor(color)
                setOnClickListener {
                    drawingView.setColor(color)
                }
                setOnLongClickListener { view ->
                    Toast.makeText(
                        this@MainActivity,
                        "Цвет удалён",
                        Toast.LENGTH_SHORT
                    ).show()
                    (view.parent as? ViewGroup)?.removeView(view)
                    addedColors.remove(color)

                    return@setOnLongClickListener true
                }
            }
            drawingView.setColor(color)
            colorButtonsContainer.addView(colorButton, colorButtonsContainer.childCount - 1)
        }
    }

    private fun Int.dpToPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()
}