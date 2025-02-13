package com.poojasinghandroid.userbehaviourtrackingapp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.poojasinghandroid.userbehaviourtrackingapp.ui.BehaviorViewModel
import com.poojasinghandroid.userbehaviourtrackingapp.ui.TFLiteModel

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: BehaviorViewModel
    private lateinit var model: TFLiteModel
    private lateinit var submitButton: Button
    private lateinit var inputText: EditText
    private var userInput: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this).get(BehaviorViewModel::class.java)
        model = TFLiteModel(this)

        submitButton = findViewById(R.id.button)
        inputText = findViewById(R.id.inputText)

        viewModel.lastSessions.observe(this) { lastInputs ->
            if (!lastInputs.isNullOrEmpty()) {
                val probability = model.predictBehavior(lastInputs)
                val percentage = (probability * 100).toInt()

                if (userInput.isNotEmpty()) {
                    Log.d("TFLite", "lastInputs - $lastInputs")
                    Toast.makeText(this, "Same user probability: $percentage%", Toast.LENGTH_SHORT).show()
                }
            }
        }

        submitButton.setOnClickListener {
            userInput = inputText.text.toString()

            if (userInput.isNotEmpty()) {
                viewModel.saveBehavior(userInput)
            } else {
                Toast.makeText(this, "Please enter some text", Toast.LENGTH_SHORT).show()
            }
        }
    }
}