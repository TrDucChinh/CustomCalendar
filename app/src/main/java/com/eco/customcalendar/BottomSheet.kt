package com.eco.customcalendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.eco.customcalendar.databinding.FragmentBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BottomSheet(
    private val date: Date,
    private val onNoteSaved: (Date, String) -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: FragmentBottomSheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dateFormatted = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date)
        binding.noteTitle.text = getString(R.string.note_title_with_date, dateFormatted)

        binding.saveButton.setOnClickListener {
            val content = binding.noteEditText.text.toString()
            if (content.isNotBlank()) {
                onNoteSaved(date, content)
                dismiss()
            } else {
                Toast.makeText(requireContext(), getString(R.string.toast_save_note), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
