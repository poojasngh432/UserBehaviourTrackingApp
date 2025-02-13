package com.poojasinghandroid.userbehaviourtrackingapp.ui

import android.content.Context
import android.util.Log
import com.poojasinghandroid.userbehaviourtrackingapp.data.UserBehavior
import org.tensorflow.lite.Interpreter
import java.io.File
import java.io.FileOutputStream

class TFLiteModel(context: Context) {
    private var interpreter: Interpreter? = null

    init {
        try {
            val modelFile = File(context.filesDir, "model.tflite")
            if (!modelFile.exists() || !isValidTFLiteModel(modelFile)) {
                copyModelFromAssets(context, modelFile)
            }
            interpreter = Interpreter(modelFile)
        } catch (e: Exception) {
            Log.e("TFLite", "Error loading model", e)
        }
    }

    private fun isValidTFLiteModel(modelFile: File): Boolean {
        return try {
            val model = Interpreter(modelFile)
            model.close()
            true
        } catch (e: Exception) {
            Log.e("TFLite", "Invalid model: ${modelFile.absolutePath}")
            false
        }
    }

    // Copy the model from assets to internal storage
    private fun copyModelFromAssets(context: Context, modelFile: File) {
        try {
            val assetManager = context.assets
            val inputStream = assetManager.open("model.tflite")
            val outputStream = FileOutputStream(modelFile)

            // Read from the input stream and write to the output stream
            val buffer = ByteArray(1024)
            var bytesRead: Int
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
            }

            inputStream.close()
            outputStream.close()

            Log.d("TFLite", "Model copied from assets to: ${modelFile.absolutePath}")
        } catch (e: Exception) {
            Log.e("TFLite", "Error copying model from assets to internal storage", e)
        }
    }

    fun predictBehavior(sessions: List<UserBehavior>): Float {
        val features = mutableListOf<FloatArray>()
        for (session in sessions) {
            features.add(extractFeatures(session.inputText))
        }
        return runModel(features)
    }

    private fun extractFeatures(text: String): FloatArray {
        val length = text.length.toFloat()

        val asciiSum = text.sumOf { it.code.toLong() }
        val asciiAvg = if (text.isNotEmpty()) asciiSum / text.length else 0f

        return floatArrayOf(asciiAvg.toFloat(), length)
    }

    fun runModel(features: List<FloatArray>): Float {
        try {
            if (features.isEmpty() || features[0].size != 2) {
                Log.e("TFLiteModel", "Invalid input shape. Expected [1,2] but got [${features.size}, ${features[0].size}]")
                return 0f
            }

            if (interpreter == null) {
                Log.e("TFLiteModel", "ERROR: Model interpreter is null!")
                return 0f
            }

            val inputArray = Array(features.size) { i -> features[i] }
            val outputArray = Array(inputArray[0].size) { FloatArray(1) }

            Log.d("TFLiteModel", "Input shape: ${inputArray.size}x${inputArray[0].size}")
            Log.d("TFLiteModel", "Expected Output shape: ${outputArray.size}x${outputArray[0].size}")

            Log.d("TFLiteModel", "Running model with input: ${inputArray[0].contentToString()}")
            interpreter?.run(inputArray, outputArray)
            Log.d("TFLiteModel", "Model prediction output: ${outputArray[0][0]}")

            Log.d("TFLiteModel", "Model output: ${outputArray[0][0]}")

            return if (outputArray.size > 1) {
                outputArray.map { it[0] }.average().toFloat()  // Average all outputs
            } else {
                outputArray[0][0]  // Single output
            }
        } catch (e: Exception) {
            Log.e("TFLiteModel", "Error running TensorFlow Lite model: ${e.message}")
            return 0f
        }
    }
}