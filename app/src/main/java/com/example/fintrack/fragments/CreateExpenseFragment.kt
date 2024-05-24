package com.example.fintrack.fragments

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.MenuProvider
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.fintrack.R
import com.example.fintrack.adapter.ColorSpinnerAdapter
import com.example.fintrack.databinding.FragmentCreatExpenseBinding
import com.example.fintrack.home.HomeViewModel
import com.example.fintrack.model.ColorTransaction
import com.example.fintrack.model.Transaction
import com.example.fintrack.util.ColorList
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener
import com.skydoves.powerspinner.PowerSpinnerView
import java.util.Calendar

class CreateExpenseFragment : DialogFragment(R.layout.fragment_creat_expense), MenuProvider {

    private var creatExpenseBinding: FragmentCreatExpenseBinding? = null
    private val binding get() = creatExpenseBinding!!
    private lateinit var selectedColorTransaction: ColorTransaction
    private val homeViewModel: HomeViewModel by activityViewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)
    }

    private fun loadColorSpinner() {
        selectedColorTransaction = ColorList().defaultColorTransaction
        binding.spinnerColors.apply {
            adapter = ColorSpinnerAdapter(requireContext(), ColorList().basicColor())
            setSelection(ColorList().colorPosition(selectedColorTransaction), false)
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    selectedColorTransaction = ColorList().basicColor()[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }
    }

    private fun loadCategorySpinner() {
        val categories = listOf(
            "Food",
            "Transport",
            "Entertainment",
            "Health",
            "Internet",
            "Home",
            "Clothe",
            "Electricity",
            "Gas station",
            "Game control",
            "Others"
        )
        binding.psvCategory.apply {
            setItems(categories)
            setOnSpinnerItemSelectedListener(OnSpinnerItemSelectedListener<String> { oldIndex, oldItem, newIndex, newItem ->
                Toast.makeText(requireContext(), "$newItem selected!", Toast.LENGTH_SHORT).show()
            })
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        creatExpenseBinding = FragmentCreatExpenseBinding.inflate(inflater, container, false)

        val pickDateButton = binding.btnPickDate
        val backButton: ImageView = binding.btnBack
        val edtTitleModal: EditText = binding.edtTitleModal
        val psvCategory: PowerSpinnerView = binding.psvCategory
        val edtPriceModal: EditText = binding.edtPriceModal
        val btnCreateExpense: Button = binding.btnCreateExpense

        edtPriceModal.addTextChangedListener(PriceFormatWatcher(edtPriceModal))

        btnCreateExpense.setOnClickListener {
            val title = edtTitleModal.text.toString()
            val category = psvCategory.text.toString()
            val amount = edtPriceModal.text.toString()
            val color = selectedColorTransaction

            val date = pickDateButton.setOnDateChangedListener { view, year, monthOfYear, dayOfMonth ->
               showDatePicker(pickDateButton)

            }

            val newTransaction = Transaction(title, category, amount, date.toString(), color, "", id)
            homeViewModel.addExpenseData(newTransaction)

            dismiss()
        }

        /*pickDateButton.setOnClickListener {

        }

         */

        backButton.setOnClickListener {
            dismiss()
        }

        loadColorSpinner()
        loadCategorySpinner()

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        dialog?.window?.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    private fun showDatePicker(datePicker: DatePicker) {
        val datePicker = binding.btnPickDate

       val calendar = Calendar.getInstance()
        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)){ view, year, month, day ->
            val month = month + 1
            val msg = "You selected: $day/$month/$year"
            Toast.makeText(requireActivity(), msg, Toast.LENGTH_SHORT).show()
        }

        /*
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)


        val datePickerDialog = DatePickerDialog(

            requireContext(),
            { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                val formattedDate = "$selectedYear/${selectedMonth + 1}/$selectedDayOfMonth"
                datePicker.updateDate(formattedDate.toInt())
            },
            year,
            month,
            day
        )
        datePickerDialog.show()

        */
    }

    override fun onDestroyView() {
        super.onDestroyView()
        creatExpenseBinding = null
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        TODO()
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        TODO()
    }
}
