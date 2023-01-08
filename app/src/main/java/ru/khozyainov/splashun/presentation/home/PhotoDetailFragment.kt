package ru.khozyainov.splashun.presentation.home

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import androidx.core.view.forEach
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.khozyainov.splashun.R
import ru.khozyainov.splashun.databinding.FragmentPhotoDetailBinding
import ru.khozyainov.splashun.models.PhotoDetails
import ru.khozyainov.splashun.utils.*

@AndroidEntryPoint
class PhotoDetailFragment :
    ViewBindingFragment<FragmentPhotoDetailBinding>(FragmentPhotoDetailBinding::inflate) {

    private val viewModel: HomeViewModel by viewModels()

    private val args: PhotoDetailFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbarSettings()
        observePhotoFlow()
        getPhoto()
        setListeners()
    }

    private fun setListeners()= with(binding){
        likeLayout.setOnClickListener {
            viewModel.setLikeForDetailPhoto()
        }

        locationImageView.setOnClickListener { }
        locationTextView.setOnClickListener { }

        downloadImageView.setOnClickListener { }
        downloadTextView.setOnClickListener { }

        getShareButton().setOnClickListener { }
    }

    private fun observePhotoFlow() =
        viewModel.uiState.launchAndCollectLatest(viewLifecycleOwner) { state ->
            when (state) {
                is HomeViewModel.UiState.Success -> showPhotoDetails(state.photo)
                is HomeViewModel.UiState.Loading -> updateIsLoading(state.loading)
                is HomeViewModel.UiState.Error -> updateErrorState(state.exceptionMessage)
            }
        }

    private fun updateIsLoading(isLoading: Boolean) =
        with(binding) {
            progressBar.isVisible = isLoading
            exceptionTextView.isVisible = !isLoading
            downloadTextView.isVisible = !isLoading
            downloadImageView.isVisible = !isLoading
            aboutTextView.isVisible = !isLoading
            exifTextView.isVisible = !isLoading
            tagsTextView.isVisible = !isLoading
            locationTextView.isVisible = !isLoading
            locationImageView.isVisible = !isLoading
            likeLayout.isVisible = !isLoading
            authorNameTextView.isVisible = !isLoading
            authorFullNameTextView.isVisible = !isLoading
            authorAvatarImageView.isVisible = !isLoading
            photoImageView.isVisible = !isLoading
        }


    private fun updateErrorState(errorMessage: String) = with(binding) {
        updateIsLoading(true)
        progressBar.isVisible = false
        exceptionTextView.isVisible = true
        exceptionTextView.text = errorMessage
    }

    private fun getPhoto() =
        viewLifecycleOwner.lifecycleScope.launch { viewModel.getPhoto(args.photoId) }

    private fun setToolbarSettings() {
        val toolbar = getToolbar()
        toolbar.title = getString(R.string.photo)
        toolbar.menu.forEach { manuItems ->
            manuItems.isVisible = manuItems.itemId == R.id.toolbar_menu_share
        }
    }

    private fun showPhotoDetails(photo: PhotoDetails) = with(binding) {
        updateIsLoading(false)
        showImage(photo)
        showAuthorAvatar(photo)
        authorFullNameTextView.text = photo.author.fullName
        authorNameTextView.text = photo.author.name
        photo.showLikeCount(likeCountTextView)
        showLike(photo)
        locationTextView.text = getLocationString(photo)
        tagsTextView.text = photo.tags.joinToString(separator = " ") { "#${it} " }.trim()
        val exif = photo.exif
        exifTextView.text = getString(
            R.string.exif,
            exif.made,
            exif.model,
            exif.exposure,
            exif.aperture.toString(),
            exif.focalLength.toString(),
            exif.iso.toString()
        )
        aboutTextView.text = getString(R.string.about, photo.author.name, photo.author.about)
        downloadTextView.text =
            getString(R.string.downloads, photo.downloads.toString()).underLine()
    }

    private fun getLocationString(photo: PhotoDetails): String =
        if (photo.location.city.isEmpty() && photo.location.country.isEmpty()) getString(R.string.no_location)
        else {
            when {
                photo.location.city.isEmpty() && photo.location.country.isNotEmpty() -> photo.location.country
                photo.location.country.isEmpty() && photo.location.city.isNotEmpty() -> photo.location.city
                else -> getString(R.string.location, photo.location.city, photo.location.country)
            }
        }

    private fun showLike(photo: PhotoDetails) =
        Glide.with(requireContext())
            .load(
                if (photo.like) {
                    R.drawable.ic_like
                } else {
                    R.drawable.ic_like_empty
                }
            )
            .into(binding.likeImageView)

    private fun showAuthorAvatar(itemPhoto: PhotoDetails) = Glide.with(requireContext())
        .load(itemPhoto.author.image)
        .error(
            AppCompatResources.getDrawable(
                requireContext(),
                R.drawable.ic_error
            )
        )
        .circleCrop()
        .into(binding.authorAvatarImageView)

    private fun showImage(photo: PhotoDetails) {

        val context = requireContext()

        val width = if (!context.orientationIsPortrait()) context.getDisplayWidthHeight().second
        else context.getDisplayWidthHeight().first

        val height = (photo.height.toDouble() / photo.width.toDouble() * width).toInt()

        Glide.with(requireContext())
            .load(photo.image + "&fm=pjpg&w=${width}&h=${height}") //todo  в константы завернуть
            .override(width, height)

            .error(
                AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.ic_error
                )
            ) //todo Добавить информацию под изображение
            .thumbnail(
                Glide.with(requireContext())
                    .load(BlurHashDecoder.decode(photo.placeholder, width, height))
                    .placeholder(requireContext().getCircularProgressDrawable())
            )
            .into(binding.photoImageView)
    }

    private fun getToolbar(): Toolbar =
        activity?.findViewById(R.id.toolbar) ?: throw Exception("")//TODO

    private fun getShareButton(): ImageButton =
        getToolbar().menu?.findItem(R.id.toolbar_menu_share)?.actionView as ImageButton

}