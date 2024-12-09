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
import com.example.myplaylistmaker.media.domain.Playlist
import com.example.myplaylistmaker.media.viewModels.NewPlayViewModel
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


@Suppress("DEPRECATION")
class NewPlayFragment : Fragment() {

    private var _binding: FragmentNewPlayBinding? = null
    private val binding get() = _binding!!
    private lateinit var toolbar: Toolbar
    private lateinit var playlistImageView: ImageView
    private lateinit var playlistName: TextInputEditText
    private lateinit var playlistDescription: TextInputEditText
    private lateinit var playlistCreate: Button

    var isPlaylistNameFilled = false
    var isPlaylistDescriptionFilled = false

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

        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
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
            val playlist = Playlist(
                0,
                playlistName.text.toString(),
                playlistDescription.text.toString(),
                getPath(),
                mutableListOf(),
                0
            )

            viewModel.insertPlaylist(playlist)
            val playlistName = playlistName.text ?: ""
            Toast.makeText(
                requireContext(),
                getString(R.string.playlist_str, playlistName),
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
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        playlistDescription.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                isPlaylistDescriptionFilled = !s.isNullOrBlank()
                updateCreateButtonState()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

    }

    private fun getPath(): String {
        return viewModel.getImagePath(binding.toString())
    }

    private suspend fun saveImageToStorage(uri: Uri) {
        viewModel.saveImage(uri, binding.toString())

    }
        fun updateCreateButtonState() {
            binding.createPlaylist.isEnabled = isPlaylistNameFilled || isPlaylistDescriptionFilled
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
            builder.setTitle(getString(R.string.quit_question))
            builder.setMessage(getString(R.string.lost_action))
            builder.setPositiveButton(getString(R.string.close_toast)) { _, _ ->
                parentFragmentManager.popBackStack()
            }
            builder.setNegativeButton("Отмена") { dialog, _ ->
                dialog.dismiss()
            }

            val alertDialog = builder.create()
            alertDialog.show()

            val textColor =
                if (isNightModeEnabled()) android.graphics.Color.WHITE else android.graphics.Color.BLACK
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(textColor)
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(textColor)
        }

        private fun isNightModeEnabled(): Boolean {
            val currentNightMode =
                resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
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


