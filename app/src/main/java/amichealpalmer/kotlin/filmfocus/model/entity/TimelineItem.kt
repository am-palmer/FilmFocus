package amichealpalmer.kotlin.filmfocus.model.entity

import amichealpalmer.kotlin.filmfocus.R
import amichealpalmer.kotlin.filmfocus.model.FilmThumbnail
import android.os.Parcel
import android.os.Parcelable
import android.widget.ImageView
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.picasso.Picasso
import org.joda.time.LocalDate

enum class TIMELINE_ITEM_STATUS {
    Watched, Dropped
}

/*
 Model which represents an individual item in the Timeline view. Stored in room database
 */

@Entity(tableName = "timeline")
class TimelineItem(val film: FilmThumbnail, val rating: Float, val date: LocalDate, private var review: String?, val status: TIMELINE_ITEM_STATUS) : Parcelable {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    fun hasReview(): Boolean {
        return !review.isNullOrBlank()
    }

    fun getReview(): String {
        if (hasReview()) {
            return review as String
        } else return ""
    }

    fun setReview(review: String?) {
        this.review = review
    }

    override fun equals(other: Any?): Boolean {
        return if (javaClass == other?.javaClass) {
            other as TimelineItem
            (this.id == other.id &&
                    this.status == other.status &&
                    this.getReview() == other.getReview() &&
                    this.rating == other.rating &&
                    this.date == other.date &&
                    this.film == other.film)
        }
        else super.equals(other)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(film, flags)
        parcel.writeFloat(rating)
        parcel.writeString(date.toString())
        parcel.writeString(review)
        parcel.writeString(status.name)
        parcel.writeInt(id)
    }

    constructor(parcel: Parcel) : this(parcel.readParcelable<FilmThumbnail>(FilmThumbnail::class.java.classLoader)!!,
            parcel.readFloat(),
            LocalDate(parcel.readString()!!),
            parcel.readString()!!,
            TIMELINE_ITEM_STATUS.valueOf(parcel.readString()!!))

    override fun describeContents(): Int {
        return 0
    }

    fun retrievePoster(imageView: ImageView, posterURL: String) {
        Picasso.get().load(posterURL).error(R.drawable.ic_image_loading_grey_48dp)
                .placeholder(R.drawable.ic_image_loading_grey_48dp).into(imageView)
    }

    companion object CREATOR : Parcelable.Creator<TimelineItem> {
        override fun createFromParcel(parcel: Parcel): TimelineItem {
            val timelineItem = TimelineItem(parcel)
            timelineItem.id = parcel.readInt() // Set the id member variable
            return timelineItem
        }

        override fun newArray(size: Int): Array<TimelineItem?> {
            return arrayOfNulls(size)
        }
    }

}