package com.example.myplaylistmaker.settings.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.myplaylistmaker.R
import com.example.myplaylistmaker.settings.viewmodels.ThemeViewModel
import com.example.myplaylistmaker.sharing.viewmodels.ShareViewModel
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {

    private val viewModel: ShareViewModel by viewModels {
        ShareViewModel.Factory(resources)
    }

    private val themeViewModel by lazy {
        ViewModelProvider(this)[ThemeViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)

        val arrow1Button: ImageView = findViewById(R.id.arrow1)
        arrow1Button.setOnClickListener {
            finish()
        }

        val themeSwitcher: SwitchMaterial = findViewById(R.id.themeSwitcher)

        themeViewModel.isThemeChecked.observe(this) { isChecked ->
            themeSwitcher.isChecked = isChecked
        }

        themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            themeViewModel.onThemeSwitchChanged(isChecked)
        }

        val buttonShare = findViewById<ImageView>(R.id.set_share)
        val buttonSup = findViewById<ImageView>(R.id.set_support)
        val buttonOffer = findViewById<ImageView>(R.id.set_offer)

        viewModel.shareIntentData.observe(this) { message ->
            message?.let {
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, it)
                    type = "text/plain"
                }
                startActivity(Intent.createChooser(shareIntent, getString(R.string.share_string)))
            }
        }

        viewModel.supportIntentData.observe(this) { supportData ->
            supportData?.let {
                val mailIntent = Intent().apply {
                    action = Intent.ACTION_SENDTO
                    data = Uri.parse("mailto:")
                    putExtra(Intent.EXTRA_EMAIL, arrayOf(it.email))
                    putExtra(Intent.EXTRA_SUBJECT, it.subject)
                    putExtra(Intent.EXTRA_TEXT, it.text)
                }
                startActivity(mailIntent)
            }
        }

        viewModel.offerIntentData.observe(this) { url ->
            url?.let {
                val offerIntent = Intent(Intent.ACTION_VIEW)
                offerIntent.data = Uri.parse(it)
                startActivity(offerIntent)
            }
        }

        buttonShare.setOnClickListener {
            viewModel.prepareShareIntent()
        }

        buttonSup.setOnClickListener {
            viewModel.prepareSupportIntent()
        }

        buttonOffer.setOnClickListener {
            viewModel.prepareOfferIntent()
        }
    }
}


