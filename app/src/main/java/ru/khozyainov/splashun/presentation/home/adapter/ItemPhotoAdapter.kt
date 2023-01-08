package ru.khozyainov.splashun.presentation.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import ru.khozyainov.splashun.R
import ru.khozyainov.splashun.databinding.ItemPhotoBinding
import ru.khozyainov.splashun.models.ItemPhoto
import ru.khozyainov.splashun.models.Photo
import ru.khozyainov.splashun.utils.*

class ItemPhotoAdapter(
    private val listener: Listener
) : PagingDataAdapter<ItemPhoto, ItemPhotoAdapter.Holder>(DiffUtilForDataClass()),
    View.OnClickListener {

    override fun onClick(v: View?) {
        val imagePhoto = v?.tag as ItemPhoto
        when (v.id) {
            R.id.likeLayout -> listener.likePhoto(imagePhoto)
            else -> listener.navigateToDetailPhoto(imagePhoto)
        }
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val itemPhoto = getItem(position) ?: return
        with(holder.binding) {
            photoImageView.tag = itemPhoto
            likeLayout.tag = itemPhoto
        }
        holder.bind(itemPhoto, position)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemPhotoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        binding.likeLayout.setOnClickListener(this)
        binding.photoImageView.setOnClickListener(this)

        return Holder(binding)
    }

    interface Listener {
        fun navigateToDetailPhoto(itemPhoto: ItemPhoto)
        fun likePhoto(itemPhoto: ItemPhoto)
    }

    class Holder(
        val binding: ItemPhotoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(itemPhoto: ItemPhoto, position: Int) {

            var firstPosition = position == 0
            val params = itemView.layoutParams as StaggeredGridLayoutManager.LayoutParams
            params.isFullSpan = firstPosition
            val displayWidth = if (!itemView.context.orientationIsPortrait()) {
                params.isFullSpan = false
                firstPosition = false
                itemView.context.getDisplayWidthHeight().second
            } else itemView.context.getDisplayWidthHeight().first

            val heightRatio =
                (itemPhoto.height.toDouble() / itemPhoto.width.toDouble() * displayWidth).toInt()

            val width = if (firstPosition) displayWidth else displayWidth / 2
            val height = if (firstPosition) heightRatio else heightRatio / 2

            showPhoto(
                itemPhoto = itemPhoto,
                height = height,
                width = width
            )
            showAuthorAvatar(itemPhoto = itemPhoto)
            showLike(itemPhoto = itemPhoto)

            with(binding) {
                itemPhoto.showLikeCount(likeCountTextView)

                photoImageView.layoutParams = ViewGroup.LayoutParams(
                    width,
                    height
                )
                authorFullNameTextView.text = itemPhoto.author.fullName
                authorNameTextView.text = itemPhoto.author.name
            }
        }

        private fun showLike(itemPhoto: ItemPhoto) =
            Glide.with(itemView)
                .load(
                    if (itemPhoto.like) {
                        R.drawable.ic_like
                    } else {
                        R.drawable.ic_like_empty
                    }
                )
                .into(binding.likeImageView)

        private fun showAuthorAvatar(itemPhoto: ItemPhoto) = Glide.with(itemView)
            .load(itemPhoto.author.image)
            .error(
                AppCompatResources.getDrawable(
                    itemView.context,
                    R.drawable.ic_error
                )
            )
            .circleCrop()
            .into(binding.authorAvatarImageView)

        private fun showPhoto(itemPhoto: ItemPhoto, height: Int, width: Int) =
            Glide.with(itemView)
                .load(itemPhoto.image + "&fm=pjpg&w=$width&h=$height&fit=clamp") //todo  в константы завернуть
                .override(width, height)
                .placeholder(itemView.context.getCircularProgressDrawable())
                .error(
                    AppCompatResources.getDrawable(
                        itemView.context,
                        R.drawable.ic_error
                    )
                ) //todo Добавить информацию под изображение
                .into(binding.photoImageView)

    }
}