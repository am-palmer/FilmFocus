package amichealpalmer.kotlin.filmfocus.data

import java.text.DateFormat
import java.time.LocalDate
import java.util.*

// Holds FilmThumbnail, ID, Review, Star Rating, and Date marked watched. Displayed in the watched film timeline
class TimelineItem(val film: FilmThumbnail, val rating: Int, val date: org.joda.time.LocalDate, private var review: String?) {

    fun hasReview(): Boolean{
        if (review != null){
            return true
        } else{
            return false
        }
    }

    fun getReview(): String{
        if (hasReview()){
            return review as String
        } else return ""
    }

    fun setReview(review: String?){
        this.review = review
    }

}