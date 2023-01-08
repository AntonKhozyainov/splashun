package ru.khozyainov.splashun.presentation.onboarding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import ru.khozyainov.splashun.R
import ru.khozyainov.splashun.models.OnboardingItem
import ru.khozyainov.splashun.databinding.ItemOnboardingBinding

class OnboardingAdapter(
    private val listener: Listener
) : RecyclerView.Adapter<OnboardingAdapter.Holder>(), View.OnClickListener {

    private val onboardingList = listOf(
        OnboardingItem(
            image = R.drawable.ic_onboarding_image_1,
            title = R.string.onboarding_page_1
        ),
        OnboardingItem(
            image = R.drawable.ic_onboarding_image_2,
            title = R.string.onboarding_page_2
        ),
        OnboardingItem(
            image = R.drawable.ic_onboarding_image_3,
            title = R.string.onboarding_page_3
        )
    )

    override fun onClick(v: View) {
        val indext = v.tag as Int
        when (v.id) {
            R.id.nextButton -> listener.showNextPage((onboardingList.size - 1) == indext)
            else -> listener.showPrevPage()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemOnboardingBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        with(binding) {
            nextButton.setOnClickListener(this@OnboardingAdapter)
            prevButton.setOnClickListener(this@OnboardingAdapter)
        }

        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        with(holder.binding) {
            nextButton.tag = position
            prevButton.tag = position
            prevButton.isVisible = position != 0
            titleTextView.setText(onboardingList[position].title)
            onboardingImage.setImageResource(onboardingList[position].image)
        }
    }

    override fun getItemCount(): Int = onboardingList.size

    class Holder(
        val binding: ItemOnboardingBinding
    ) : RecyclerView.ViewHolder(binding.root)

    interface Listener {
        fun showNextPage(onboardingCompleted: Boolean)
        fun showPrevPage()
    }
}