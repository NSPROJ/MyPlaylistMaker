package com.example.myplaylistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        val arrow1Button: Button = findViewById(R.id.arrow1)
        arrow1Button.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        val buttonShare = findViewById<ImageView>(R.id.set_share)

        buttonShare.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            val message = "https://practicum.yandex.ru/android-developer/"
            shareIntent.putExtra(Intent.EXTRA_TEXT, message)
            val chooserIntent = Intent.createChooser(shareIntent, "Поделиться приложением")
            startActivity(chooserIntent)
        }

        val buttonSup = findViewById<ImageView>(R.id.set_support)
        val mySupportEmail = "dekgold@gmail.com"
        buttonSup.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(
                    Intent.EXTRA_EMAIL,
                    arrayOf(mySupportEmail),
                )
                putExtra(
                    Intent.EXTRA_SUBJECT,
                    "Сообщение разработчикам и разработчицам приложения Playlist Maker",
                )
                putExtra(
                    Intent.EXTRA_TEXT,
                    "Спасибо разработчикам и разработчицам за крутое приложение!",
                )
            }
            startActivity(intent)
        }

        val buttonOffer = findViewById<ImageView>(R.id.set_offer)

        buttonOffer.setOnClickListener {
            val url = Uri.parse("https://yandex.ru/legal/practicum_offer/")
            val intent = Intent(Intent.ACTION_VIEW, url)
            startActivity(intent)
        }
    }
}