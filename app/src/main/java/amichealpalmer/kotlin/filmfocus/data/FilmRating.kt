package amichealpalmer.kotlin.filmfocus.data

import android.os.Parcel
import android.os.Parcelable

enum class RATING_VALUE {
    HAS_RATING, NO_RATING
}

class FilmRating(var value: Float, val state: RATING_VALUE) : Parcelable {

    constructor(parcel: Parcel) : this(parcel.readFloat(), (RATING_VALUE.valueOf(parcel.readString()!!)))

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeFloat(value)
        dest.writeString(state.name)
    }

    companion object CREATOR : Parcelable.Creator<FilmRating> {
        override fun createFromParcel(parcel: Parcel): FilmRating {
            return FilmRating(parcel)
        }

        override fun newArray(size: Int): Array<FilmRating?> {
            return arrayOfNulls(size)
        }
    }

}