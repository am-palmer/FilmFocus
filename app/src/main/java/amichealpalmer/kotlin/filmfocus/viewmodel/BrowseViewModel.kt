package amichealpalmer.kotlin.filmfocus.viewmodel

import amichealpalmer.kotlin.filmfocus.model.FilmThumbnail
import amichealpalmer.kotlin.filmfocus.model.remote.FilmThumbnailRepository
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

// Holds data displayed in the Browse fragment

class BrowseViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FilmThumbnailRepository by lazy { FilmThumbnailRepository(application) }

    fun newQuery(query: String) {
        repository.newQuery(query)
    }

    fun nextPage(){
        repository.getNextPage()
    }

    fun getResults(): MutableLiveData<ArrayList<FilmThumbnail?>> {
        return repository.getResults
    }

    fun getQuery(): MutableLiveData<String?> {
        return repository.getQuery
    }

}

class BrowseViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return BrowseViewModel(application) as T
    }
}