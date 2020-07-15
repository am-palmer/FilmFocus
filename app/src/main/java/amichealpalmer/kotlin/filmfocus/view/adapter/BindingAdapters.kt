package amichealpalmer.kotlin.filmfocus.view.adapter

import amichealpalmer.kotlin.filmfocus.R
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Picasso

@BindingAdapter("isGone")
fun bindIsGone(view: View, isGone: Boolean) {
    view.visibility = if (isGone) {
        View.GONE
    } else {
        View.VISIBLE
    }
}

@BindingAdapter("imageFromUrl")
fun bindImageFromUrl(view: ImageView, imageUrl: String?) {
    if (!imageUrl.isNullOrEmpty()) {
        Picasso.get().load(imageUrl).error(R.drawable.ic_image_loading_grey_48dp)
                .placeholder(R.drawable.ic_image_loading_grey_48dp).into(view)
    }
}

// Figure out the ordinal indicator to display for the date and append it, i.e. 4 becomes 4th
@BindingAdapter("dayWithOrdinal")
fun getDayWithOridinal(view: TextView, day: Int) {
    val j = day % 10
    val k = day % 100
    when {
        j == 1 && k != 11 -> {
            view.text = (day.toString() + "st")
        }
        j == 2 && k != 12 -> {
            view.text = (day.toString() + "nd")
        }
        j == 3 && k != 13 -> {
            view.text = (day.toString() + "rd")
        }
        else -> {
            view.text = (day.toString() + "th")
        }
    }
}