package com.example.dndhpsystem

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.activityViewModels


class CharacterHpFragment : Fragment() {
    /**
     * purpose: calculate max character hp
     *
     * formula = (hit die vale / 2) + con mod + tough* X class level
     * add multi-class info as needed
     */
    val viewModel by activityViewModels<DndViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_character_hp, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val baseHpLabel = view.findViewById<TextView>(R.id.tv_base_hp_label)

        if (viewModel.baseArmor > 0) {
            val labelText = "Base Max Hp: ${viewModel.baseArmor}"
            baseHpLabel.text = labelText
        } else {
            baseHpLabel.hint = "Hint: Go back and calculate armor first"
        }

        view.findViewById<Button>(R.id.btn_hp_calc).setOnClickListener {
            getValues()
        }

        if (viewModel.baseArmor > 0) {
            view.findViewById<TextView>(R.id.tv_note)?.visibility = View.GONE
        }

        if (viewModel.maxChp > 0){
            displayChp()

        }
    }

    /**
     * tough adds +2 to con mod for hp calc
     */

    private fun getValues(){
        //get ui elements
        val etCon = view?.findViewById<EditText>(R.id.et_con_mod)
        val etLevel = view?.findViewById<EditText>(R.id.et_char_level)
        val toughSwitch = view?.findViewById<SwitchCompat>(R.id.sw_tough)
        val rbtnPositive = requireView().findViewById<RadioButton>(R.id.rbtn_positive)
        val rbtnNegative = requireView().findViewById<RadioButton>(R.id.rbtn_negative)
        var toughEnabled = false
        var conMod = -6
        var charLvl = 0
        var conPN = 0

        //get tough bool value
        if (toughSwitch?.isChecked == true) {
            toughEnabled = true
        }

        //make sure +/- is checked
        if (rbtnPositive.isChecked || rbtnNegative.isChecked) {
            if (rbtnNegative.isChecked){
                conPN = -1
            }
            else {
                conPN = 1
            }
            //get and validate con mod and char level
            if (!etCon?.text.isNullOrEmpty() &&
                !etLevel?.text.isNullOrEmpty()
            ) {
                if (etLevel?.text.toString().toInt() >= 1) {
                    if (etCon?.text.toString().toInt() >= -5) {
                        charLvl = etLevel?.text.toString().toInt()
                        conMod = etCon?.text.toString().toInt()

                        //send to viewmodel for eval
                        viewModel.getMaxChp(conMod, conPN, charLvl, toughEnabled)
                        displayChp()
                    } else {
                        Toast.makeText(requireContext(), "Con modifier can't be less than -5", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Class level is less than 1", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Con modifier or Class level is empty", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "check + or - for con modifier", Toast.LENGTH_SHORT).show()
        }
    }

    private fun displayChp() {
        val text = "Max character Hp: ${viewModel.maxChp}"
        view?.findViewById<TextView>(R.id.tv_chr_hp)?.text = text
    }

}