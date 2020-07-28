package amichealpalmer.kotlin.filmfocus.view

import amichealpalmer.kotlin.filmfocus.R
import amichealpalmer.kotlin.filmfocus.util.InjectorUtils
import amichealpalmer.kotlin.filmfocus.viewmodel.FilmDetailDialogViewModel
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_dialog_film_details.*

class FilmDetailDialogFragment : DialogFragment() {

    private val ARG_IMDBID = "imdbID"
    private val detailDialogViewModel: FilmDetailDialogViewModel by viewModels {
        InjectorUtils.provideFilmDetailDialogViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, ".OnCreate called")
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dialog_film_details, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        subscribeUi()

        fragment_film_details_back_button.setOnClickListener { this.dismiss() }

        // Request the details for the film tapped, which will be available to the observer when retrieved
        val imdbID = arguments?.getString(ARG_IMDBID) as String
        detailDialogViewModel.requestFilmDetails(imdbID)

        film_details_progressBar?.visibility = View.VISIBLE

        super.onViewCreated(view, savedInstanceState)
    }

    private fun subscribeUi() { // Todo: databinding would be nice, but need to handle the async request
        // Register observer to update fields when the film information is loaded
        detailDialogViewModel.getFilm().observe(viewLifecycleOwner) {

            // Visibility
            if (it != null) {
                film_details_progressBar?.visibility = View.GONE
                film_details_outer_card_view.visibility = View.VISIBLE
            }

            // View fields
            fragment_film_details_tv_title?.text = it?.title
            fragment_film_details_tv_director?.text = it?.director
            fragment_film_details_tv_year?.text = it?.year
            fragment_film_details_tv_runtime?.text = it?.runtime
            fragment_film_details_tv_plot?.text = it?.plot
            fragment_film_details_tv_awards?.text = it?.awards
            fragment_film_details_tv_cast?.text = it?.actors
            fragment_film_details_tv_genre?.text = it?.genre
            fragment_film_details_tv_imdbScore?.text = it?.imdbRating
            fragment_film_details_tv_metacriticScore?.text = it?.metascore
            fragment_film_details_tv_language?.text = it?.language

            if (fragment_film_details_iv_poster != null) {
                Picasso.get().load(it?.posterURL).error(R.drawable.ic_image_loading_grey_48dp)
                        .placeholder(R.drawable.ic_image_loading_grey_48dp).into(fragment_film_details_iv_poster)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Clear the film details so they aren't shown again
        detailDialogViewModel.clearFilm()
    }

    companion object {
        const val TAG = "FilmDetailDialogFrag"

        private const val ARG_IMDBID = "imdbID"

        fun newInstance(imdbID: String): FilmDetailDialogFragment {
            val fragment = FilmDetailDialogFragment()
            val args = Bundle()
            args.putString(ARG_IMDBID, imdbID)
            fragment.arguments = args
            return fragment
        }
    }
}
