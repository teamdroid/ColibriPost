package ru.teamdroid.colibripost.ui.newpost

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import ru.teamdroid.colibripost.databinding.TimeDialogFragmentBinding


class TimeDialogFragment : DialogFragment() {
    companion object {
        const val TAG = "TimeDialogFragment"
        const val REQUEST_TIME = "REQUEST_TIME"
        const val KEY_HOUR = "KEY_HOUR"
        const val KEY_MUNUTE = "KEY_MUNUTE"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = TimeDialogFragmentBinding.inflate(requireActivity().layoutInflater)
        binding.btnOk.setOnClickListener {
            parentFragmentManager.setFragmentResult(
                REQUEST_TIME, bundleOf(
                    KEY_HOUR to binding.timePicker.currentHour,
                    KEY_MUNUTE to binding.timePicker.currentMinute
                )
            )
            dismiss()
        }
        binding.btnCancel.setOnClickListener { dismiss() }
        binding.timePicker.setIs24HourView(true)

        return AlertDialog.Builder(requireActivity()).setView(binding.root).create()
    }
}