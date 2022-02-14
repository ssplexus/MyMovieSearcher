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

//        button_menu.setOnClickListener { Toast.makeText(this, button_menu.text,Toast.LENGTH_SHORT).show() }
//        button_fav.setOnClickListener { Toast.makeText(this, button_fav.text,Toast.LENGTH_SHORT).show() }
//        button_later.setOnClickListener { Toast.makeText(this, button_later.text,Toast.LENGTH_SHORT).show() }
//        button_compile.setOnClickListener { Toast.makeText(this, button_compile.text,Toast.LENGTH_SHORT).show() }
//        button_settings.setOnClickListener { Toast.makeText(this, button_settings.text,Toast.LENGTH_SHORT).show() }

        initNavigation()

    }

    fun initNavigation()
    {
        topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.settings -> {
                    Toast.makeText(this, "Настройки", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }

        topAppBar.setNavigationOnClickListener {
            Toast.makeText(this, "Когда-нибудь здесь будет навигация...", Toast.LENGTH_SHORT).show()
        }
        bottom_navigation.setOnNavigationItemSelectedListener {

            when (it.itemId) {
                R.id.favorites -> {
                    Toast.makeText(this, "Избранное", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.watch_later -> {
                    Toast.makeText(this, "Посмотреть похже", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.selections -> {
                    Toast.makeText(this, "Подборки", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
    }

}
