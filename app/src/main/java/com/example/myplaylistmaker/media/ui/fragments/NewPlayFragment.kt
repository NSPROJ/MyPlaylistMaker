package com.example.myplaylistmaker.media.ui.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.myplaylistmaker.R
import com.example.myplaylistmaker.databinding.FragmentNewPlayBinding
import com.example.myplaylistmaker.media.viewModels.NewPlayViewModel
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

class NewPlayFragment : Fragment() {

    private var _binding: FragmentNewPlayBinding? = null
    private val binding get() = _binding!!
    private lateinit var toolbar: Toolbar
    private lateinit var playlistImageView: ImageView
    private lateinit var playlistName: TextInputEditText
    private lateinit var playlistDescription: TextInputEditText
    private lateinit var playlistCreate: Button
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

        playlistName = binding.nameName
        playlistDescription = binding.description
        playlistCreate = binding.createPlaylist
        playlistImageView = binding.playListImage

        val playlistId = arguments?.getInt("playlistId")
        viewModel.loadPlaylist(playlistId)

        viewModel.playlist.observe(viewLifecycleOwner) { playlist ->
            playlist?.let {
                playlistName.setText(it.playlistName)
                playlistDescription.setText(it.description)
                if (it.path != null) {
                    loadImage(it.path)
                    isImageAdded = true
                }
            }
        }

        viewModel.isEditing.observe(viewLifecycleOwner) { isEditing ->
            if (isEditing) {
                toolbar.title = getString(R.string.redact_playlist)
                playlistCreate.text = getString(R.string.save)
            } else {
                toolbar.title = getString(R.string.new_playlist)
                playlistCreate.text = getString(R.string.create)
            }
        }

        viewModel.playlistName.observe(viewLifecycleOwner) { name ->
            playlistName.setText(name)
        }

        viewModel.playlistDescription.observe(viewLifecycleOwner) { description ->
            playlistDescription.setText(description)
        }

        viewModel.playlistCoverPath.observe(viewLifecycleOwner) { path ->
            if (path != null) {
                loadImage(path)
                isImageAdded = true
            }
        }

        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    loadImage(uri)
                    lifecycleScope.launch {
                        saveImageToStorage(uri)
                    }
                } else {
                    Toast.makeText(requireContext(), "Изображение не выбрано", Toast.LENGTH_SHORT)
                        .show()
                    isImageAdded = false
                }
            }

        playlistCreate.setOnClickListener {
            viewModel.setPlaylistName(playlistName.text.toString())
            viewModel.setPlaylistDescription(playlistDescription.text.toString())
            viewModel.savePlaylist()
            findNavController().navigateUp()
        }

        playlistImageView.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        playlistCreate.isEnabled = false

        playlistName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateCreateButtonState()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        playlistDescription.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateCreateButtonState()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun loadImage(imageSource: Any?) {
        if (imageSource == null) return

        val requestBuilder = Glide.with(requireContext())
            .load(imageSource)
            .placeholder(R.drawable.placeholder)
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
            .transition(DrawableTransitionOptions.withCrossFade())

        requestBuilder.into(binding.playListImage)
    }

    private suspend fun saveImageToStorage(uri: Uri) {
        val filename = System.currentTimeMillis().toString()
        if (viewModel.saveImage(uri, filename)) {
            viewModel.setPlaylistCoverPath(viewModel.getImagePath(filename))
        }
    }

    private fun updateCreateButtonState() {
        binding.createPlaylist.isEnabled =
            !playlistName.text.isNullOrBlank() || !playlistDescription.text.isNullOrBlank()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().findViewById<ScrollView>(R.id.scrollView)?.visibility = View.GONE
    }

    override fun onDetach() {
        super.onDetach()
        requireActivity().findViewById<ScrollView>(R.id.scrollView)?.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.quit_question))
            .setMessage(getString(R.string.lost_action))
            .setPositiveButton(getString(R.string.close_toast)) { _, _ ->
                findNavController().navigateUp()
            }
            .setNegativeButton("Отмена") { dialog, _ ->
                dialog.dismiss()
            }

        val alertDialog = builder.create()

        alertDialog.setOnShowListener {
            val textColor =
                if (isNightModeEnabled()) android.graphics.Color.WHITE else android.graphics.Color.BLACK
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(textColor)
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(textColor)
        }
        alertDialog.show()
    }

    private fun isNightModeEnabled(): Boolean {
        val currentNightMode =
            resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES
    }

    private fun handleBackNavigation() {
        if (viewModel.isEditing.value == true) {
            findNavController().navigateUp()
        } else {
            if (!playlistName.text.isNullOrBlank() || !playlistDescription.text.isNullOrBlank() || isImageAdded) {
                showConfirmationDialog()
            } else {
                findNavController().navigateUp()
            }
        }
    }

    private fun transformDpToPx(dp: Float = 8.0f): Int {
        return (dp * resources.displayMetrics.density + 0.5f).toInt()
    }
}


