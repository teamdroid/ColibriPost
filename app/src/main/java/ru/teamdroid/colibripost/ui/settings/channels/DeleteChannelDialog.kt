package ru.teamdroid.colibripost.ui.settings.channels

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import ru.teamdroid.colibripost.R

class DeleteChannelDialog(
    private val channelId: Long,
    private val deleteChannel: (channelId: Long) -> Unit
) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            builder.setView(inflater.inflate(R.layout.dialog_window, null))
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onStart() {
        super.onStart()

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        dialog?.findViewById<TextView>(R.id.tvMessage)?.setText(getString(R.string.do_you_want_delete_channel))

        dialog?.findViewById<TextView>(R.id.tvOk)?.setOnClickListener {
            deleteChannel(channelId)
            dialog!!.dismiss()
        }
        dialog?.findViewById<TextView>(R.id.tvCancel)
            ?.setOnClickListener { dialog!!.dismiss() }

    }

    companion object {
        const val TAG = "DeleteChannelDialogFragment"
    }
}