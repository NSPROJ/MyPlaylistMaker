package com.example.myplaylistmaker
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        val arrow1Button: ImageView = findViewById(R.id.arrow1)
        arrow1Button.setOnClickListener {
            finish()
        }

        val buttonShare = findViewById<ImageView>(R.id.set_share)

        buttonShare.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            val message = getString(R.string.Y_P)
            shareIntent.putExtra(Intent.EXTRA_TEXT, message)
            val chooserIntent = Intent.createChooser(shareIntent, getString(R.string.share_string))
            startActivity(chooserIntent)
        }

        val buttonSup = findViewById<ImageView>(R.id.set_support)
        val mySupportEmail = getString(R.string.my_mail)
        buttonSup.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(
                    Intent.EXTRA_EMAIL,
                    arrayOf(mySupportEmail),
                )
                putExtra(
                    Intent.EXTRA_SUBJECT,
                    getString(R.string.congrats_str),
                )
                putExtra(
                    Intent.EXTRA_TEXT,
                    getString(R.string.thanks_str),
                )
            }
            startActivity(intent)
        }

        val buttonOffer = findViewById<ImageView>(R.id.set_offer)

        buttonOffer.setOnClickListener {
            val url = Uri.parse(getString(R.string.YP_urlOffer))
            val intent = Intent(Intent.ACTION_VIEW, url)
            startActivity(intent)
        }
    }
}