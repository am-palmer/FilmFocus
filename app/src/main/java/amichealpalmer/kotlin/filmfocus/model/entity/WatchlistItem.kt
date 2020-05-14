package amichealpalmer.kotlin.filmfocus.model.entity

import amichealpalmer.kotlin.filmfocus.model.FilmThumbnail
import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

/* An entity derived from FilmThumbnail for storage in the Room database, used for the watchlist
 */

@Entity(tableName = "watchlist")
class WatchlistItem(title: String, year: String, imdbID: String, type: String, posterURL: String) : FilmThumbnail(title, year, imdbID, type, posterURL) {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        //super.writeToParcel(parcel, flags) todo: need super call?
        parcel.writeString(title)
        parcel.writeString(year)
        parcel.writeString(imdbID)
        parcel.writeString(type)
        parcel.writeString(posterURL)
        parcel.writeInt(id)
    }

    constructor(filmThumbnail: FilmThumbnail) : this(filmThumbnail.title, filmThumbnail.year, filmThumbnail.imdbID, filmThumbnail.type, filmThumbnail.posterURL)

    constructor(parcel: Parcel) : this(parcel.readString()!!, parcel.readString()!!, parcel.readString()!!, parcel.readString()!!, parcel.readString()!!)

    companion object CREATOR : Parcelable.Creator<WatchlistItem> {
        override fun createFromParcel(source: Parcel): WatchlistItem {
            val watchlistItem = WatchlistItem(source)
            watchlistItem.id = source.readInt()
            return watchlistItem
        }

        override fun newArray(size: Int): Array<WatchlistItem?> {
            return arrayOfNulls(size)
        }
    }

}