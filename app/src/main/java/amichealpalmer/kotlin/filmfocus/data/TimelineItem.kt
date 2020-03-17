package amichealpalmer.kotlin.filmfocus.data

import android.os.Parcel
import android.os.Parcelable
import org.joda.time.LocalDate

// Holds FilmThumbnail, ID, Review, Star Rating, and Date marked watched. Displayed in the watched film timeline
class TimelineItem(val film: FilmThumbnail, val rating: Int?, val date: LocalDate, private var review: String?) : Parcelable {

    fun hasReview(): Boolean {
        return review != null
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

        if (rating != null) {
            parcel.writeInt(rating!!)
        } else parcel.writeInt(0)

        parcel.writeString(date.toString())

        parcel.writeString(review)
    }

    constructor(parcel: Parcel) : this(parcel.readParcelable<FilmThumbnail>(FilmThumbnail::class.java.classLoader)!!, parcel.readInt()!!, LocalDate(parcel.readString()!!), parcel.readString()!!)

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