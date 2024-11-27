package com.example.myplaylistmaker.media.ui.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.myplaylistmaker.media.viewModels.NewPlayViewModel
import com.example.myplaylistmaker.R
import com.example.myplaylistmaker.databinding.FragmentNewPlayBinding
import com.example.myplaylistmaker.media.domain.Playlist
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class NewPlayFragment : Fragment() {

    private var _binding: FragmentNewPlayBinding? = null
    private val binding get() = _binding!!
    private lateinit var toolbar: Toolbar
    private lateinit var playlistImageView: ImageView
    private lateinit var playlistName: EditText
    private lateinit var playlistDescription: EditText
    private lateinit var playlistCreate: Button

    private var isPlaylistNameFilled = false
    private var isPlaylistDescriptionFilled = false
    private var isImageAdded = false

    private val viewModel by viewModel<NewPlayViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewPlayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar = binding.newPlayListToolbar
        toolbar.setNavigationOnClickListener {
            handleBackNavigation()
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            handleBackNavigation()
        }



        playlistName = binding.playlistName
        playlistDescription = binding.playListDescription
        playlistCreate = binding.createPlaylist
        playlistImageView = binding.playListImage

        val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                Glide.with(requireContext())
                    .load(uri)
                    .apply(
                        RequestOptions().transform(
                            MultiTransformation(
                                CenterCrop(),
                                RoundedCorners(
                                    transformDpToPx(8f)
                                )
                            )
                        )
                    )
                    .into(binding.playListImage)

                if (viewModel.saveImage(uri, "${binding.playlistName.text}.jpg")) {
                    Toast.makeText(requireContext(), "Изображение успешно сохранено!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Ошибка при сохранении изображения", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Изображение не выбрано", Toast.LENGTH_SHORT).show()
                isImageAdded = false
            }
        }


        playlistCreate.setOnClickListener {
            val playlist = Playlist(
                0,
                playlistName.text.toString(),
                playlistDescription.text.toString(),
                getPath(),
                mutableListOf(),
                0
            )

            viewModel.insertPlaylist(playlist)
            Toast.makeText(
                requireContext(),
                "Плейлист ${playlistName.text} успешно создан!",
                Toast.LENGTH_LONG
            ).show()
            parentFragmentManager.popBackStack()
        }


        playlistImageView.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            isImageAdded = true
        }

        playlistCreate.isEnabled = false

        playlistName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                isPlaylistNameFilled = !s.isNullOrBlank()
                updateCreateButtonState()
                updateDrawables()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        playlistDescription.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                isPlaylistDescriptionFilled = !s.isNullOrBlank()
                updateCreateButtonState()
                updateDrawables()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun getPath(): String {
        val filePath = File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "playlist_images")
        val file = File(filePath, "$binding")
        return file.toString()
    }

    private fun updateCreateButtonState() {
        playlistCreate.isEnabled = isPlaylistNameFilled
    }

    private fun updateDrawables() {
        if (isPlaylistNameFilled) {
            playlistName.setBackgroundResource(R.drawable.editable_true)
        } else {
            playlistName.setBackgroundResource(R.drawable.editable_false)
        }

        if (isPlaylistDescriptionFilled) {
            playlistDescription.setBackgroundResource(R.drawable.editable_true)
        } else {
            playlistDescription.setBackgroundResource(R.drawable.editable_false)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val scrollView = requireActivity().findViewById<ScrollView>(R.id.scrollView)
        if (scrollView != null) {
            scrollView.visibility = View.GONE
        }
    }

    override fun onDetach() {
        super.onDetach()
        val scrollView = requireActivity().findViewById<ScrollView>(R.id.scrollView)
        if (scrollView != null) {
            scrollView.visibility = View.VISIBLE
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Завершить создание плейлиста?")
        builder.setMessage("Все несохраненные данные будут потеряны")
        builder.setPositiveButton("Завершить") { _, _ ->
            parentFragmentManager.popBackStack()
        }
        builder.setNegativeButton("Отмена") { dialog, _ ->
            dialog.dismiss()
        }

        val alertDialog = builder.create()
        alertDialog.show()

        val textColor = if (isNightModeEnabled()) android.graphics.Color.WHITE else android.graphics.Color.BLACK
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(textColor)
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(textColor)
    }

    private fun isNightModeEnabled(): Boolean {
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES
    }

    private fun handleBackNavigation() {
        if (isPlaylistNameFilled || isPlaylistDescriptionFilled || isImageAdded) {
            showConfirmationDialog()
        } else {
            if (parentFragmentManager.backStackEntryCount > 0) {
                parentFragmentManager.popBackStack()
            } else {
                requireActivity().finish()
            }
        }
    }

    private fun transformDpToPx(dp: Float): Int {
        return (dp * resources.displayMetrics.density + 0.5f).toInt()
    }
}

