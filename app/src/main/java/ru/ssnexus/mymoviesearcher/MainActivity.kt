package ru.ssnexus.mymoviesearcher

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button_menu.setOnClickListener { Toast.makeText(this, button_menu.text,Toast.LENGTH_SHORT).show() }
        button_fav.setOnClickListener { Toast.makeText(this, button_fav.text,Toast.LENGTH_SHORT).show() }
        button_later.setOnClickListener { Toast.makeText(this, button_later.text,Toast.LENGTH_SHORT).show() }
        button_compile.setOnClickListener { Toast.makeText(this, button_compile.text,Toast.LENGTH_SHORT).show() }
        button_settings.setOnClickListener { Toast.makeText(this, button_settings.text,Toast.LENGTH_SHORT).show() }

    }


}
