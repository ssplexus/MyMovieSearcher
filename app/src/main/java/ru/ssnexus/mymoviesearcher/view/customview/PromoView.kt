package ru.ssnexus.mymoviesearcher.view.customview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import ru.ssnexus.mymoviesearcher.R
import ru.ssnexus.mymoviesearcher.databinding.MergePromoBinding
import ru.ssnexus.remote_module.entity.ApiConstants

class PromoView(context: Context, attributeSet: AttributeSet?) : FrameLayout(context, attributeSet) {
    val binding = MergePromoBinding.inflate(LayoutInflater.from(context), this)
    val watchButton = binding.watchButton

    fun setLinkForPoster(link: String) {
        Glide.with(binding.root)
            .load(ApiConstants.IMAGES_URL  + resources.getString(R.string.poster_normal) + link)
            .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(resources.getInteger(R.integer.ang55))))
            .into(binding.poster)
    }
    companion object{
        const val POSTER_ANIM_DURATION:Long = 1500
        const val POSTER_ANIM_ALPHA = 1f
        const val POSTER_LINK_KEY = "film_link"
        const val POSTER_ID_KEY = "film_id"
    }
}