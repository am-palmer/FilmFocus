package amichealpalmer.kotlin.filmfocus


import android.content.Context
import android.content.Intent
import android.media.Image
import android.util.Log
//import android.support.v7.widget.CardView
//import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso


class BrowseRecyclerAdapter(
        private val context: Context,
        private val resultList: List<GetJSONSearch.Result> // The list of films currently being displayed in the browser
) : RecyclerView.Adapter<BrowseRecyclerAdapter.HelperViewHolder>() {

    private val TAG = "BrowseRecyclerAdapter"
    ///private val mData: List<Film>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HelperViewHolder {
        Log.d(TAG, ".onCreateViewHolder called")
        val view: View
        val mInflater = LayoutInflater.from(context)
        view = mInflater.inflate(R.layout.browse_films_item, parent, false)
//        Log.d(TAG, ".onCreateViewHolder is view null: ${(view == null)}")
//        Log.d(TAG, ".onCreateViewHolder: view class is?: ${view::class.toString()}")
        return HelperViewHolder(view)
    }

    override fun onBindViewHolder(holder: HelperViewHolder, position: Int) {
        //holder.tv_book_title.setText(mData[position].getTitle())
        Log.d(TAG, ".onBindViewHolder called. Title of film is: ${resultList[position].title}")
        Picasso.get().load(resultList[position].posterURL).error(R.drawable.placeholder_imageloading)
                .placeholder(R.drawable.placeholder_imageloading).into(holder.poster)
        //Log.d(TAG, ".onBindViewHolder: picasso loaded poster")

        // onClick: get the details for the film and inflate the film detail layout
        holder.cardView.setOnClickListener(View.OnClickListener {
            val intent = Intent(context, FilmDetailsActivity::class.java)
            intent.putExtra("imdbID", resultList[position].imdbID)
            context.startActivity(intent)
        })
    }

    override fun getItemCount(): Int {
        return resultList.size
    }

    class HelperViewHolder(view: View)
        : RecyclerView.ViewHolder(view) {
        val poster: ImageView = view.findViewById(R.id.film_poster_id)
        val cardView: CardView = view.findViewById(R.id.film_item_cardview_id)
    }


    // var title: TextView = view.findViewById(R.id.title)

//    init {
//        this.mData = mData
//    }

}