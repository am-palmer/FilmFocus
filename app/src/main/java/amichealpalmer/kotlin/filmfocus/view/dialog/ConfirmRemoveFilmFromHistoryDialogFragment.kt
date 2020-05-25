package amichealpalmer.kotlin.filmfocus.view.dialog

import amichealpalmer.kotlin.filmfocus.R
import amichealpalmer.kotlin.filmfocus.model.entity.TimelineItem
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.fragment_dialog_generic_confirm.*
import kotlin.properties.Delegates

class ConfirmRemoveFilmFromHistoryDialogFragment : DialogFragment(), View.OnClickListener {

    private val TAG = "ConfirmRemoveFilmHisDia"

    private lateinit var callback: OnConfirmRemoveFilmDialogActionListener

    private lateinit var timelineItem: TimelineItem
    private var position by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            timelineItem = requireArguments().getParcelable<TimelineItem>("timelineItem") as TimelineItem
            position = 0 // todo: we're not even using this class anymore, so we can delete it. replace it with an alertdialog
        } catch (e: NullPointerException) {
            Log.wtf(TAG, ".onCreate - failed to retrieve timelineItem")
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        isCancelable = true
        return inflater.inflate(R.layout.fragment_dialog_generic_confirm, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val prompt = "Are you sure you want to remove ${timelineItem.film.title} from your history?"
        fragment_dialog_generic_cancelButton.setOnClickListener(this)
        fragment_dialog_generic_takeActionButton.setOnClickListener(this)
        fragment_dialog_generic_prompt_text.text = prompt
        fragment_dialog_generic_takeActionButton.setText(R.string.remove)

        dialog!!.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            fragment_dialog_generic_cancelButton.id -> this.dismiss()
            fragment_dialog_generic_takeActionButton.id -> { // We remove the item from the history
                callback.onConfirmRemoveItemDialogAction(timelineItem, position)
                this.dismiss()
            }
        }
    }

    interface OnConfirmRemoveFilmDialogActionListener {
        fun onConfirmRemoveItemDialogAction(timelineItem: TimelineItem, position: Int)
    }

    fun setOnConfirmRemoveFilmDialogActionListener(callback: OnConfirmRemoveFilmDialogActionListener) {
        this.callback = callback
    }

    companion object {

        const val TAG = "ConfirmRemoveFilmHisDia"

        fun newInstance(timelineItem: TimelineItem): ConfirmRemoveFilmFromHistoryDialogFragment {
            val fragment = ConfirmRemoveFilmFromHistoryDialogFragment()
            val bundle = Bundle()
            bundle.putParcelable("timelineItem", timelineItem)
            fragment.arguments = bundle
            return fragment
        }

    }

}

