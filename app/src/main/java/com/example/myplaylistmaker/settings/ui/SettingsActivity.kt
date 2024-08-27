package com.example.myplaylistmaker.settings.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.myplaylistmaker.R
import com.example.myplaylistmaker.settings.viewmodels.ThemeViewModel
import com.example.myplaylistmaker.sharing.viewmodels.SettingsViewModel
import com.example.myplaylistmaker.sharing.viewmodels.SettingsViewModelFactory
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {

    private val themeViewModel by lazy {
        ViewModelProvider(this)[ThemeViewModel::class.java]
    }

    private val settingsViewModel by lazy {
        ViewModelProvider(this, SettingsViewModelFactory())[SettingsViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)

        val arrow1Button: ImageView = findViewById(R.id.arrow1)
        arrow1Button.setOnClickListener {
            finish()
        }

        val buttonShare = findViewById<ImageView>(R.id.set_share)
        val themeSwitcher: SwitchMaterial = findViewById(R.id.themeSwitcher)

        themeViewModel.isThemeChecked.observe(this) { isChecked ->
            themeSwitcher.isChecked = isChecked
        }

        themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            themeViewModel.onThemeSwitchChanged(isChecked)
        }

        buttonShare.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            val message = settingsViewModel.getShareAppLink()
            shareIntent.putExtra(Intent.EXTRA_TEXT, message)
            val chooserIntent = Intent.createChooser(shareIntent, getString(R.string.share_string))
            startActivity(chooserIntent)
        }

        val buttonSup = findViewById<ImageView>(R.id.set_support)
        buttonSup.setOnClickListener {
            val supportData = settingsViewModel.getSupportEmailData()
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(supportData.email))
                putExtra(Intent.EXTRA_SUBJECT, supportData.subject)
                putExtra(Intent.EXTRA_TEXT, supportData.text)
            }
            startActivity(intent)
        }

        val buttonOffer = findViewById<ImageView>(R.id.set_offer)
        buttonOffer.setOnClickListener {
            val url = Uri.parse(settingsViewModel.getTermsLink())
            val intent = Intent(Intent.ACTION_VIEW, url)
            startActivity(intent)
        }
    }
}

