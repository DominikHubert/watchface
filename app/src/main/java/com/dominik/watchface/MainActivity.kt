package com.dominik.watchface

import android.app.Activity
import android.os.Bundle
import android.widget.TextView
import android.view.Gravity

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val textView = TextView(this).apply {
            text = "Omega Style Watchface\n\nBitte drücke lange auf dein aktuelles Zifferblatt und wähle 'Omega Style' aus der Liste aus."
            gravity = Gravity.CENTER
            setPadding(20, 20, 20, 20)
        }
        
        setContentView(textView)
    }
}
