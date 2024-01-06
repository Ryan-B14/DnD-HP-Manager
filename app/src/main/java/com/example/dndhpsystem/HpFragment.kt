package com.example.dndhpsystem

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.findFragment
import java.lang.Math.abs

class HpFragment : Fragment() {
    /**
     * purpose: manage hp and armor values
     */

    val viewModel by activityViewModels<DndViewModel>()
    lateinit var etArmor: EditText
    lateinit var etChar: EditText
    lateinit var etTempHp: EditText
    lateinit var tvArmor: TextView
    lateinit var tvChar: TextView
    lateinit var tvTempHp: TextView
    lateinit var armorPlus: Button
    lateinit var armorMinus: Button
    lateinit var charPlus: Button
    lateinit var charMinus: Button
    lateinit var tempPlus: Button
    lateinit var tempMinus: Button
    lateinit var resetButton: Button
    lateinit var setValsBtn: Button
    lateinit var infoBtn: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_hp, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getUiViews()
        setClickListeners()

        if (viewModel.currentArmorHp > -1) {
            displayValues(tvArmor)
        }
        if (viewModel.currentChp > -1 ) {
            displayValues(tvChar)
        }
        if (viewModel.currentTempHp > 0) {
            displayValues(tvTempHp)
        }
    }

    private fun getUiViews() {
        //set ui elements
        etArmor = requireView().findViewById(R.id.et_armor_edit)
        etChar = requireView().findViewById(R.id.et_char_edit)
        etTempHp = requireView().findViewById(R.id.et_temp_edit)
        tvArmor = requireView().findViewById(R.id.tv_current_armor)
        tvChar = requireView().findViewById(R.id.tv_current_char_hp)
        tvTempHp = requireView().findViewById(R.id.tv_current_temp_hp)
        armorPlus = requireView().findViewById(R.id.btn_armor_plus)
        armorMinus = requireView().findViewById(R.id.btn_armor_minus)
        charPlus = requireView().findViewById(R.id.btn_chp_plus)
        charMinus = requireView().findViewById(R.id.btn_chp_minus)
        resetButton = requireView().findViewById(R.id.btn_reset)
        setValsBtn = requireView().findViewById(R.id.btn_set_values)
        tempPlus = requireView().findViewById(R.id.btn_temp_plus)
        tempMinus = requireView().findViewById(R.id.btn_temp_minus)
        infoBtn = requireView().findViewById(R.id.btn_info)
    }

    private fun setClickListeners() {
        armorPlus.setOnClickListener {
            increaseValue(etArmor)
            clearInputs()
        }
        armorMinus.setOnClickListener {
            decreaseValue(etArmor)
            clearInputs()
        }
        charPlus.setOnClickListener {
            increaseValue(etChar)
            clearInputs()
        }
        charMinus.setOnClickListener {
            decreaseValue(etChar)
            clearInputs()
        }
        tempPlus.setOnClickListener {
            increaseValue(etTempHp)
            clearInputs()
        }
        tempMinus.setOnClickListener {
            decreaseValue(etTempHp)
            clearInputs()
        }
        resetButton.setOnClickListener {
            resetValues()

            tvArmor.text = viewModel.currentArmorHp.toString()
            tvChar.text = viewModel.currentChp.toString()
            clearInputs()
        }
        setValsBtn.setOnClickListener {
            setMaxValues()
            clearInputs()
        }
        infoBtn.setOnClickListener {
            viewModel.navController?.navigate(R.id.action_global_go_info)
        }
    }

    private fun setMaxValues() {
        if (!etArmor.text.isNullOrEmpty() && !etChar.text.isNullOrEmpty()) {
            viewModel.totalArmor = etArmor.text.toString().toInt()
            viewModel.maxChp = etChar.text.toString().toInt()

            resetValues()
        }
        else {
            Toast.makeText(requireContext(), "Armor Hp and Character Hp must contain a value", Toast.LENGTH_LONG).show()
        }
    }

    private fun resetValues() {
        viewModel.currentChp  = viewModel.maxChp
        viewModel.currentArmorHp = viewModel.totalArmor

        displayValues(tvArmor)
        displayValues(tvChar)
    }

    private fun increaseValue(editText: EditText) {
        if (!editText.text.isNullOrEmpty()) {
            when (editText.id) {
                R.id.et_armor_edit -> {
                    val tempVal = viewModel.currentArmorHp + editText.text.toString().toInt()
                    viewModel.currentArmorHp = if (tempVal > viewModel.totalArmor) {
                        viewModel.totalArmor

                    } else {
                        tempVal
                    }
                    displayValues(tvArmor)
                }
                R.id.et_char_edit -> {
                    val tempVal = viewModel.currentChp + editText.text.toString().toInt()
                    viewModel.currentChp = if (tempVal > viewModel.maxChp) {
                        viewModel.maxChp
                    } else {
                        tempVal
                    }
                    displayValues(tvChar)
                }
                R.id.et_temp_edit -> {
                    viewModel.currentTempHp += editText.text.toString().toInt()
                    displayValues(tvTempHp)
                }
            }
        } else {
            Toast.makeText(requireContext(), "enter a value", Toast.LENGTH_SHORT).show()
        }
    }

    private fun decreaseValue(editText: EditText) {
        if (!editText.text.isNullOrEmpty()) {
            when (editText.id) {
                R.id.et_armor_edit -> {
                    val tempVal = viewModel.currentArmorHp - editText.text.toString().toInt()
                    if (tempVal < 0) {
                        viewModel.currentArmorHp = 0
                        damageCarryOver(kotlin.math.abs(tempVal))
                    } else {
                        viewModel.currentArmorHp = tempVal
                    }
                    displayValues(tvArmor)
                }
                R.id.et_char_edit -> {
                    val tempVal = viewModel.currentChp - editText.text.toString().toInt()
                    viewModel.currentChp = if (tempVal < 0) {
                        0
                    } else {
                        tempVal
                    }
                    displayValues(tvChar)
                }
                R.id.et_temp_edit -> {
                    var tempVal = viewModel.currentTempHp - editText.text.toString().toInt()
                    if ((tempVal) < 0) {
                        viewModel.currentTempHp = 0
                        damageCarryOver(kotlin.math.abs(tempVal))
                    } else {
                        viewModel.currentTempHp = tempVal
                    }
                    displayValues(tvTempHp)
                }
            }
        } else {
            Toast.makeText(requireContext(), "enter a value", Toast.LENGTH_SHORT).show()
        }
    }

    private fun damageCarryOver(damage: Int) {
        var tempVal = 0
        if (viewModel.currentArmorHp > 0) {
            tempVal = viewModel.currentArmorHp - damage
            if ((tempVal) >= 0) {
                viewModel.currentArmorHp = tempVal
            } else  {
                viewModel.currentArmorHp = 0
                viewModel.currentChp = if ((viewModel.currentChp - kotlin.math.abs(tempVal)) < 0) {
                    0
                } else {
                    viewModel.currentChp - kotlin.math.abs(tempVal)
                }
                displayValues(tvChar)
            }
            displayValues(tvArmor)
        } else {
            tempVal = viewModel.currentChp - damage
            viewModel.currentChp = if ((tempVal) >= 0) {
                tempVal
            } else {
                0
            }
            displayValues(tvChar)
        }
    }

    private fun displayValues(textView: TextView) {
        when (textView.id) {
            R.id.tv_current_armor -> {
                textView.text = viewModel.currentArmorHp.toString()
                if (viewModel.currentArmorHp == 0) {
                    Toast.makeText(requireContext(), "Armor broken!", Toast.LENGTH_SHORT).show()
                }
            }
            R.id.tv_current_char_hp -> {
                textView.text = viewModel.currentChp.toString()
                if (viewModel.currentChp == 0) {
                    Toast.makeText(requireContext(), "Character unconscious!", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            R.id.tv_current_temp_hp -> {
                textView.text = viewModel.currentTempHp.toString()
                if (viewModel.currentTempHp == 0) {
                    Toast.makeText(requireContext(), "Temp Hp depleted!", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun clearInputs(){
        etArmor.text = null
        etChar.text = null
        etTempHp.text = null
    }

}