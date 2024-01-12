package com.example.dndhpsystem

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.activityViewModels

class CalculatorFragment : Fragment() {
    /**
     * purpose: calculate armor
     */

    val viewModel by activityViewModels<DndViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_calculator, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.btn_calculate)?.setOnClickListener {
            validateInputs()
        }
        val mcSection = view.findViewById<LinearLayout>(R.id.multiclass_section)
        val mcSwitch = view.findViewById<SwitchCompat>(R.id.sw_multiclass)
        mcSwitch?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked){
                mcSection?.visibility = View.VISIBLE
            }
            else{
                mcSection?.visibility = View.GONE
            }
        }

        viewModel.checkSaveData()

        if ((viewModel.totalArmor.value ?: 0) > 0){
            displayValue()
        }
        setObserver()
    }

    /**
     * hit die = d6, d8, d10, d12
     */

    private fun validateInputs() {
        //validation booleans
        var clValidated = false
        var mcClValidated = false
        var validated = false
        var multiClassed = false

        //get ui elements
        val etLevel = view?.findViewById<EditText>(R.id.et_class_level)
        val mcSwitch = view?.findViewById<SwitchCompat>(R.id.sw_multiclass)
        val etMcLevel = view?.findViewById<EditText>(R.id.et_mc_class_level)

        //validate class level
        if (!etLevel?.text.isNullOrEmpty()) {
            if (etLevel?.text.toString().toInt() > 0) {
                clValidated = true
            } else {
                Toast.makeText(requireContext(), "class level cannot be less than 1", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "class level cannot be empty", Toast.LENGTH_SHORT).show()
        }

        //validate multiclass level
        if (mcSwitch?.isChecked == true) {
            multiClassed = true
            if (!etMcLevel?.text.isNullOrEmpty()) {
                if (etMcLevel?.text.toString().toInt() > 0) {
                    mcClValidated = true
                } else {
                    Toast.makeText(requireContext(), "Multi-class level cannot be less than 1", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Multi-class level cannot be empty", Toast.LENGTH_SHORT).show()
            }
        } else {
            multiClassed = false
        }


        if ((clValidated && multiClassed && mcClValidated) ||
            (clValidated && !multiClassed)
        ) {
            validated = true
        }

        if (validated){
            collectValues(multiClassed)
        }
    }

    private fun collectValues(multiClassed: Boolean) {
        //get ui elements
        val etLevel = view?.findViewById<EditText>(R.id.et_class_level)
        val etMcLevel = view?.findViewById<EditText>(R.id.et_mc_class_level)
        val light = view?.findViewById<RadioButton>(R.id.rbtn_light)
        val medium = view?.findViewById<RadioButton>(R.id.rbtn_medium)
        val heavy = view?.findViewById<RadioButton>(R.id.rbtn_heavy)

        //create variables to pass to ViewModel
        //HD = hit die / CL = class level
        val cl = etLevel?.text.toString().toInt()
        val hd = getHitDice()
        var mcCL = -1
        var mcHD = -1
        var armorBonusPercent = 1.25

        //get multiclass hit dice and class levels
        if (multiClassed) {
            mcHD = getMcHitDice()
            mcCL = etMcLevel?.text.toString().toInt()
        }

        //get armor type
        if (light?.isChecked == true){
            armorBonusPercent = .75
        }else if (medium?.isChecked == true){
            armorBonusPercent = 1.25
        }else if (heavy?.isChecked == true){
            armorBonusPercent = 1.50
        }

        viewModel.calculateValues(hd, cl, mcHD, mcCL, armorBonusPercent)
        displayValue()
    }

    private fun getHitDice(): Int {
        val d6 = view?.findViewById<RadioButton>(R.id.rbtn_d6)
        val d8 = view?.findViewById<RadioButton>(R.id.rbtn_d8)
        val d10 = view?.findViewById<RadioButton>(R.id.rbtn_d10)
        val d12 = view?.findViewById<RadioButton>(R.id.rbtn_d12)
        val buttonList = listOf(d6, d8, d10, d12)

        if (d6 != null &&
            d8 != null &&
            d10 != null &&
            d12 != null) {
            for (rbtn in  buttonList) {
                if (rbtn!!.isChecked)  {
                    when (rbtn.id) {
                        R.id.rbtn_d6 -> return 6
                        R.id.rbtn_d8 -> return 8
                        R.id.rbtn_d10 -> return 10
                        R.id.rbtn_d12 -> return 12
                    }
                }
            }
        }
        return 0
    }

    private fun getMcHitDice(): Int {
        val mcD6 = view?.findViewById<RadioButton>(R.id.rbtn_mc_d6)
        val mcD8 = view?.findViewById<RadioButton>(R.id.rbtn_mc_d8)
        val mcD10 = view?.findViewById<RadioButton>(R.id.rbtn_mc_d10)
        val mcD12 = view?.findViewById<RadioButton>(R.id.rbtn_mc_d12)
        val buttonList = listOf(mcD6, mcD8, mcD10, mcD12)

        if (mcD6 != null &&
            mcD8 != null &&
            mcD10 != null &&
            mcD12 != null) {
            for (rbtn in  buttonList) {
                if (rbtn!!.isChecked)  {
                    when (rbtn.id) {
                        R.id.rbtn_mc_d6 -> return 6
                        R.id.rbtn_mc_d8 -> return 8
                        R.id.rbtn_mc_d10 -> return 10
                        R.id.rbtn_mc_d12 -> return 12
                    }
                }
            }
        }
        return 0
    }

    private fun displayValue() {
        val text = "Total Armor Hp: ${viewModel.totalArmor.value}"
        view?.findViewById<TextView>(R.id.tv_total_armor)?.text =  text
    }

    private fun setObserver(){
        viewModel.totalArmor.observe(viewLifecycleOwner) {
            displayValue()
            viewModel.sharedPrefs.edit().apply {
                putInt("maxAHP", it)
                apply()
            }
        }
    }
}