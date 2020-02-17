package amichealpalmer.kotlin.filmfocus

//import RecyclerViewAdapter.MyViewHolder
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
        private val mContext: Context,
        private val mData: List<GetJSONSearch.Result> // The list of films currently being displayed in the browser
) : RecyclerView.Adapter<BrowseRecyclerAdapter.HelperViewHolder>() {

    private val TAG = "BrowseRecyclerAdapter"
    ///private val mData: List<Film>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HelperViewHolder {
        val view: View
        val mInflater = LayoutInflater.from(mContext)
        view = mInflater.inflate(R.layout.browse_films_item, parent, false)
        Log.d(TAG, ".onCreateViewHolder is view null: ${(view == null)}")
        Log.d(TAG, ".onCreateViewHolder: view class is?: ${view::class.toString()}")
        // val recyclerView: CardView = mInflater.inflate(R.layout.browse_films_item, parent, false).findViewById<View>(R.id.film_item_cardview_id) as CardView
        //val filmposter: ImageView = mInflater.inflate(R.layout.browse_films_item, parent, false).findViewById<ImageView>(R.id.film_poster_id) as ImageView
        return HelperViewHolder(view)
    }

    override fun onBindViewHolder(holder: HelperViewHolder, position: Int) {
        //holder.tv_book_title.setText(mData[position].getTitle())
        Log.d(TAG, ".onBindViewHolder called. Title of film is: ${mData[position].title}")
        Picasso.get().load(mData[position].posterURL).error(R.drawable.placeholder_imageloading)
                .placeholder(R.drawable.placeholder_imageloading).into(holder.poster)
        Log.d(TAG, ".onBindViewHolder: picasso loaded poster")

        // onclick listener for each card --> todo: link up film details activity getfilmbyid
//        holder.cardView.setOnClickListener(View.OnClickListener {
//            val intent = Intent(mContext, Book_Activity::class.java)
//            // passing data to the book activity
//            intent.putExtra("Title", mData[position].getTitle())
//            intent.putExtra("Description", mData[position].getDescription())
//            intent.putExtra("Thumbnail", mData[position].getThumbnail())
//            // start the activity
//            mContext.startActivity(intent)
//        })
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    class HelperViewHolder(view: View)
        : RecyclerView.ViewHolder(view) {
        var poster: ImageView = view.findViewById(R.id.film_poster_id)
    }


    // var title: TextView = view.findViewById(R.id.title)

//    init {
//        this.mData = mData
//    }

}