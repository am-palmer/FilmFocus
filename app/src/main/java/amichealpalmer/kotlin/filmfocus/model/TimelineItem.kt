package amichealpalmer.kotlin.filmfocus.model

import android.os.Parcel
import android.os.Parcelable
import org.joda.time.LocalDate

enum class TIMELINE_ITEM_STATUS {
    WATCHED, DROPPED
}

// Holds FilmThumbnail, ID, Review, Star Rating, and Date marked watched. Displayed in the watched film timeline
class TimelineItem(val film: FilmThumbnail, val rating: FilmRating, val date: LocalDate, private var review: String?, val status: TIMELINE_ITEM_STATUS) : Parcelable {

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

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(film, flags)

        parcel.writeParcelable(rating, flags)

        parcel.writeString(date.toString())

        parcel.writeString(review)

        parcel.writeString(status.name)

    }

    constructor(parcel: Parcel) : this(parcel.readParcelable<FilmThumbnail>(FilmThumbnail::class.java.classLoader)!!,
            parcel.readParcelable<FilmRating>(FilmRating::class.java.classLoader)!!,
            LocalDate(parcel.readString()!!),
            parcel.readString()!!,
            TIMELINE_ITEM_STATUS.valueOf(parcel.readString()!!))

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TimelineItem> {
        override fun createFromParcel(parcel: Parcel): TimelineItem {
            return TimelineItem(parcel)
        }

        override fun newArray(size: Int): Array<TimelineItem?> {
            return arrayOfNulls(size)
        }
    }

}