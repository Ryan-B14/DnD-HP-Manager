package com.example.dndhpsystem

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import io.github.muddz.styleabletoast.StyleableToast


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
        setObservers()

        if (viewModel.newMaxAhpSet == true){
            viewModel.currentArmorHp.postValue(viewModel.totalArmor.value)
            viewModel.newMaxAhpSet = false
        }
        if (viewModel.newMaxChpSet == true){
            viewModel.currentChp.postValue(viewModel.maxChp.value)
            viewModel.newMaxChpSet = false
        }

        if ((viewModel.currentArmorHp.value ?: -1) > -1) {
            displayValues(tvArmor)
        }
        if ((viewModel.currentChp.value ?: -1) > -1 ) {
            displayValues(tvChar)
        }
        if ((viewModel.currentTempHp.value ?: 0) > 0) {
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

            tvArmor.text = viewModel.currentArmorHp.value.toString()
            tvChar.text = viewModel.currentChp.value.toString()
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
            viewModel.totalArmor.value = etArmor.text.toString().toInt()
            viewModel.maxChp.value = etChar.text.toString().toInt()

            resetValues()
        }
        else {
            Toast.makeText(requireContext(), "Armor Hp and Character Hp must contain a value", Toast.LENGTH_LONG).show()
        }
    }

    private fun resetValues() {
        viewModel.currentChp.value  = viewModel.maxChp.value
        viewModel.currentArmorHp.value = viewModel.totalArmor.value
    }

    private fun increaseValue(editText: EditText) {
        if (!editText.text.isNullOrEmpty()) {
            when (editText.id) {
                R.id.et_armor_edit -> {
                    val tempVal = (viewModel.currentArmorHp.value ?: 0) + editText.text.toString().toInt()
                    viewModel.currentArmorHp.postValue(if (tempVal > (viewModel.totalArmor.value ?: 0)) {
                        viewModel.totalArmor.value
                    } else {
                        tempVal
                    })
                }
                R.id.et_char_edit -> {
                    val tempVal = (viewModel.currentChp.value ?: 0 ) + editText.text.toString().toInt()
                    viewModel.currentChp.postValue(if (tempVal > (viewModel.maxChp.value ?: 0)) {
                        viewModel.maxChp.value
                    } else {
                        tempVal
                    })
                }
                R.id.et_temp_edit -> {
                    viewModel.currentTempHp.postValue(
                        (viewModel.currentTempHp.value ?: 0) + editText.text.toString().toInt()
                    )
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
                    val tempVal = (viewModel.currentArmorHp.value ?: 0) - editText.text.toString().toInt()
                    if (tempVal < 0) {
                        viewModel.currentArmorHp.postValue(0)
                        damageCarryOver(kotlin.math.abs(tempVal))
                    } else {
                        viewModel.currentArmorHp.postValue(tempVal)
                    }
                }
                R.id.et_char_edit -> {
                    val tempVal = (viewModel.currentChp.value ?: 0) - editText.text.toString().toInt()
                    viewModel.currentChp.postValue(if (tempVal < 0) {
                        0
                    } else {
                        tempVal
                    })
                }
                R.id.et_temp_edit -> {
                    val tempVal = (viewModel.currentTempHp.value ?: 0) - editText.text.toString().toInt()
                    if ((tempVal) < 0) {
                        viewModel.currentTempHp.postValue(0)
                        damageCarryOver(kotlin.math.abs(tempVal))
                    } else {
                        viewModel.currentTempHp.postValue(tempVal)
                    }
                }
            }
        } else {
            Toast.makeText(requireContext(), "enter a value", Toast.LENGTH_SHORT).show()
        }
    }

    private fun damageCarryOver(damage: Int) {
        var tempVal: Int
        if ((viewModel.currentArmorHp.value ?: 0 ) > 0) {
            tempVal = (viewModel.currentArmorHp.value ?: 0) - damage
            if ((tempVal) >= 0) {
                viewModel.currentArmorHp.postValue(tempVal)
            } else  {
                viewModel.currentArmorHp.postValue(0)
                viewModel.currentChp.postValue(if (((viewModel.currentChp.value ?: 0 ) - kotlin.math.abs(tempVal)) < 0) {
                    0
                } else {
                    (viewModel.currentChp.value ?: 0) - kotlin.math.abs(tempVal)
                })
            }
        } else {
            tempVal = (viewModel.currentChp.value ?: 0 ) - damage
            viewModel.currentChp.postValue(if ((tempVal) >= 0) {
                tempVal
            } else {
                0
            })
        }
    }

    private fun displayValues(textView: TextView) {
        when (textView.id) {
            R.id.tv_current_armor -> {
                textView.text = viewModel.currentArmorHp.value.toString()
            }
            R.id.tv_current_char_hp -> {
                textView.text = viewModel.currentChp.value.toString()
            }
            R.id.tv_current_temp_hp -> {
                textView.text = viewModel.currentTempHp.value.toString()
            }
        }
    }

    private fun clearInputs(){
        etArmor.text = null
        etChar.text = null
        etTempHp.text = null
    }

    private fun setObservers(){
        viewModel.totalArmor.observe(viewLifecycleOwner) {
            viewModel.sharedPrefs.edit().apply {
                putInt("maxAHP", it)
                apply()
            }
        }
        viewModel.maxChp.observe(viewLifecycleOwner) {
            viewModel.sharedPrefs.edit().apply {
                putInt("maxCHP", it)
                apply()
            }
        }

        viewModel.currentArmorHp.observe(viewLifecycleOwner){
            displayValues(tvArmor)
            viewModel.sharedPrefs.edit().apply{
                putInt("currentAHP", it)
                apply()
            }
            if (it == 0) {
                StyleableToast.makeText(requireContext(),"Armor broken!",R.style.armor_broke_toast).show()
            }
        }
        viewModel.currentChp.observe(viewLifecycleOwner){
            displayValues(tvChar)
            viewModel.sharedPrefs.edit().apply{
                putInt("currentCHP", it)
                apply()
            }
            if (it == 0) {
                StyleableToast.makeText(requireContext(),"Character unconscious!",R.style.character_hp_lost_toast).show()
            }
        }
        viewModel.currentTempHp.observe(viewLifecycleOwner){
            displayValues(tvTempHp)
            viewModel.sharedPrefs.edit().apply{
                putInt("currentTHP", it)
                apply()
            }
            if (it == 0) {
                StyleableToast.makeText(requireContext(),"Temp Hp depleted!",R.style.temp_hp_lost_toast).show()
            }
        }

    }

}