package com.example.myplaylistmaker.settings.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.myplaylistmaker.R
import com.example.myplaylistmaker.settings.viewmodels.ThemeViewModel
import com.example.myplaylistmaker.sharing.viewmodels.SettingsViewModel
import com.google.android.material.switchmaterial.SwitchMaterial
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    private val themeViewModel by viewModel<ThemeViewModel>()
    private val settingsViewModel by viewModel<SettingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.settings_fragment, container, false)

        val buttonShare = view.findViewById<ImageView>(R.id.set_share)
        val themeSwitcher: SwitchMaterial = view.findViewById(R.id.themeSwitcher)

        themeViewModel.isThemeChecked.observe(viewLifecycleOwner) { isChecked ->
            themeSwitcher.isChecked = isChecked
        }

        themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            themeViewModel.onThemeSwitchChanged(isChecked)
        }

        buttonShare.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, settingsViewModel.getShareAppLink())
            }
            val chooserIntent = Intent.createChooser(shareIntent, getString(R.string.share_string))
            startActivity(chooserIntent)
        }

        val buttonSup = view.findViewById<ImageView>(R.id.set_support)
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

        val buttonOffer = view.findViewById<ImageView>(R.id.set_offer)
        buttonOffer.setOnClickListener {
            val url = Uri.parse(settingsViewModel.getTermsLink())
            val intent = Intent(Intent.ACTION_VIEW, url)
            startActivity(intent)
        }

        return view
    }
}
