package amichealpalmer.kotlin.filmfocus.model.remote

import amichealpalmer.kotlin.filmfocus.model.FilmThumbnail
import androidx.lifecycle.MutableLiveData

class FilmThumbnailRepository {

    private val resultList: MutableLiveData<ArrayList<FilmThumbnail>> by lazy { MutableLiveData<ArrayList<FilmThumbnail>>(ArrayList()) }

    fun clearResults(){
        resultList.value?.clear()
    }

    // Called by the API accessor to update the resultlist
    fun updateResults(newResults: ArrayList<FilmThumbnail>){
        resultList.value?.addAll(newResults)
    }


    val getResults get() = resultList


}