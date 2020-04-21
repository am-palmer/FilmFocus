package amichealpalmer.kotlin.filmfocus.model

import android.os.Parcel
import android.os.Parcelable

class FilmThumbnail(val title: String,
                    val year: String,
                    val imdbID: String,
                    private val type: String,
                    val posterURL: String) : Parcelable {
    override fun toString(): String {
        return "FilmThumbnail(title='$title', year='$year', imdbID='$imdbID', type='$type', posterURL='$posterURL')"
    }

    constructor(parcel: Parcel) : this(
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(year)
        parcel.writeString(imdbID)
        parcel.writeString(type)
        parcel.writeString(posterURL)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FilmThumbnail> {
        override fun createFromParcel(parcel: Parcel): FilmThumbnail {
            return FilmThumbnail(parcel)
        }

        override fun newArray(size: Int): Array<FilmThumbnail?> {
            return arrayOfNulls(size)
        }
    }

}