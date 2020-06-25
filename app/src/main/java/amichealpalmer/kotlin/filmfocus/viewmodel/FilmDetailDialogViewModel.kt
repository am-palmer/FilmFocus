package amichealpalmer.kotlin.filmfocus.viewmodel

import amichealpalmer.kotlin.filmfocus.model.Film
import amichealpalmer.kotlin.filmfocus.model.remote.OMDBRepository
import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner

class FilmDetailDialogViewModel internal constructor(private val repository: OMDBRepository): ViewModel() {

    fun requestFilmDetails(imdbID: String) {
        repository.requestFilmDetails(imdbID)
    }

    fun getFilm(): MutableLiveData<Film?> {
        return repository.getFilm
    }

    fun clearFilm(){
        repository.clearFilm()
    }

    class FilmDetailDialogViewModelFactory(private val repository: OMDBRepository,
                                           owner: SavedStateRegistryOwner,
                                           defaultArgs: Bundle? = null) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(key: String, modelClass: Class<T>, handle: SavedStateHandle): T {
            return FilmDetailDialogViewModel(repository) as T
        }


    }
}