//package com.uvpv521.calendar.ui
//
//import android.app.Dialog
//import android.os.Bundle
//import androidx.appcompat.app.AlertDialog
//import androidx.fragment.app.DialogFragment
//import com.uvpv521.calendar.data.models.Holiday
//
//class HolidayDetailDialog : DialogFragment() {
//
//    companion object {
//        private const val ARG_HOLIDAY = "holiday"
//
//        fun newInstance(holiday: Holiday): HolidayDetailDialog {
//            val fragment = HolidayDetailDialog()
//            val args = Bundle()
//            args.putParcelable(ARG_HOLIDAY, holiday)
//            fragment.arguments = args
//            return fragment
//        }
//    }
//
//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        val holiday = arguments?.getParcelable<Holiday>(ARG_HOLIDAY)
//
//        return AlertDialog.Builder(requireContext())
//            .setTitle(holiday?.name)
//            .setMessage(holiday?.description)
//            .setPositiveButton("Закрыть") { dialog, _ ->
//                dialog.dismiss()
//            }
//            .create()
//    }
//}