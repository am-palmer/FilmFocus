package amichealpalmer.kotlin.filmfocus.view.dialog

import amichealpalmer.kotlin.filmfocus.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.fragment_dialog_generic_confirm.*

class ConfirmClearHistoryDialogFragment : DialogFragment(), View.OnClickListener {

    private val TAG = "ConfirmClearHistDiaFrag"
    private lateinit var callback: onConfirmClearHistoryDialogListener

    interface onConfirmClearHistoryDialogListener {
        fun onConfirmClearHistoryDialogSubmit()
    }

    fun setOnConfirmClearHistoryDialogListener(callback: onConfirmClearHistoryDialogListener) {
        this.callback = callback
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? { // todo: multiple dialog fragments sharing this exact same oncreateview, possibility for inheritance
        isCancelable = true
        return inflater.inflate(R.layout.fragment_dialog_generic_confirm, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragment_dialog_generic_prompt_text.setText(R.string.dialog_clear_history_prompt)
        fragment_dialog_generic_takeActionButton.setText(R.string.button_clear)

        fragment_dialog_generic_takeActionButton.setOnClickListener(this)
        fragment_dialog_generic_cancelButton.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            fragment_dialog_generic_cancelButton.id -> this.dismiss()
            fragment_dialog_generic_takeActionButton.id -> {
                callback.onConfirmClearHistoryDialogSubmit()
                this.dismiss()
            }
        }
    }

    companion object {

        const val TAG = "ConfirmClearHistDiaFrag"

        fun newInstance(callback: onConfirmClearHistoryDialogListener): ConfirmClearHistoryDialogFragment {
            val fragment = ConfirmClearHistoryDialogFragment()
            fragment.setOnConfirmClearHistoryDialogListener(callback)
            return fragment
        }

    }

}