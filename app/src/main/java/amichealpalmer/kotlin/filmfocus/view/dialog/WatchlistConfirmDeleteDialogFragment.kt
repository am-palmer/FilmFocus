package amichealpalmer.kotlin.filmfocus.view.dialog

import amichealpalmer.kotlin.filmfocus.R
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.fragment_dialog_generic_confirm.*

// Prompt inflated when the user chooses to clear the watchlist from the app bar menu
class WatchlistConfirmDeleteDialogFragment : DialogFragment(), View.OnClickListener {

    private val TAG = "WatchlistConfirmDelDia"
    private lateinit var callback: onWatchlistConfirmDeleteDialogListener

    interface onWatchlistConfirmDeleteDialogListener {
        fun onWatchlistConfirmDeleteDialogSubmit()
    }

    fun setOnWatchlistConfirmDeleteDialogListener(callback: onWatchlistConfirmDeleteDialogListener) {
        this.callback = callback
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        isCancelable = true
        return inflater.inflate(R.layout.fragment_dialog_generic_confirm, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragment_dialog_generic_prompt_text.setText(R.string.dialog_clear_watchlist_prompt)
        fragment_dialog_generic_takeActionButton.setText(R.string.button_clear)

        fragment_dialog_generic_cancelButton.setOnClickListener(this)
        fragment_dialog_generic_takeActionButton.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        Log.d(TAG, ".onClick triggered")
        when (v?.id) {
            fragment_dialog_generic_cancelButton.id -> this.dismiss()
            fragment_dialog_generic_takeActionButton.id -> {
                callback.onWatchlistConfirmDeleteDialogSubmit()
                this.dismiss()
            }
        }
    }

    companion object {

        const val TAG = "WatchlistConfirmDelDia"

        fun newInstance(callback: onWatchlistConfirmDeleteDialogListener): WatchlistConfirmDeleteDialogFragment {
            val fragment = WatchlistConfirmDeleteDialogFragment()
            fragment.setOnWatchlistConfirmDeleteDialogListener(callback)
            return fragment
        }
    }
}