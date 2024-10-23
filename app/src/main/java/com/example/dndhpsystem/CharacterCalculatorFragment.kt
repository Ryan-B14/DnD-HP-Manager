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


class CharacterCalculatorFragment : Fragment() {
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
        return inflater.inflate(R.layout.fragment_character_calc, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.btn_hp_calc).setOnClickListener {
            getValues()
        }

        val mcSection = view.findViewById<LinearLayout>(R.id.char_multiclass_container)
        val mcSwitch = view.findViewById<SwitchCompat>(R.id.sw_multiclass_char)
        mcSwitch?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                mcSection?.visibility = View.VISIBLE
            } else {
                mcSection?.visibility = View.GONE
            }
        }

        if ((viewModel.maxChp.value ?: 0) > 0) {
            displayChp()
        }
        setObserver()
    }

    /**
     * tough adds +2 to con mod for hp calc
     */

    private fun getValues() {
        //get ui elements
        val etCon = view?.findViewById<EditText>(R.id.et_con_mod)
        val etLevel = view?.findViewById<EditText>(R.id.et_class_level)
        val etMcLevel = view?.findViewById<EditText>(R.id.et_mc_class_level)
        val rbtnPositive = requireView().findViewById<RadioButton>(R.id.rbtn_positive)
        val rbtnNegative = requireView().findViewById<RadioButton>(R.id.rbtn_negative)
        val toughSwitch = view?.findViewById<SwitchCompat>(R.id.sw_tough)
        var toughEnabled = false
        val mcSwitch = view?.findViewById<SwitchCompat>(R.id.sw_multiclass_char)
        var mcEnabled = false

        //init variables
        var conPN: Int //con positive/negative
        var mainHd: Int = getHitDice() //main hit die
        var mcHd: Int = getMcHitDice() //multiclass hit die

        //get tough bool value
        if (toughSwitch?.isChecked == true) {
            toughEnabled = true
        }

        //get mc bool value
        if (mcSwitch?.isChecked == true) {
            mcEnabled = true
        }

        if (rbtnPositive.isChecked || rbtnNegative.isChecked) {
            conPN = if (rbtnNegative.isChecked) {
                -1
            } else {
                1
            }
            if (validateInputs(etCon, etLevel, etMcLevel, mcEnabled, conPN)) {
                var conMod = etCon?.text.toString().toInt()
                var charLvl = etLevel?.text.toString().toInt()
                var charMcLvl = if (mcEnabled) {
                    etMcLevel?.text.toString().toInt()
                } else {
                    0
                }

                //send to viewmodel for eval
                viewModel.getMaxChp(
                    conMod,
                    conPN,
                    charLvl,
                    toughEnabled,
                    mainHd,
                    mcHd,
                    charMcLvl,
                    mcEnabled
                )
                displayChp()
            }
        } else {
            Toast.makeText(requireContext(), "check + or - for con modifier", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun displayChp() {
        val text = "Max character Hp: ${viewModel.maxChp.value}"
        view?.findViewById<TextView>(R.id.tv_chr_hp)?.text = text
    }

    private fun setObserver() {
        viewModel.maxChp.observe(viewLifecycleOwner) {
            displayChp()
            viewModel.sharedPrefs.edit().apply {
                putInt("maxCHP", it)
                apply()
            }
        }
    }

    private fun validateInputs(
        etCon: EditText?,
        etLevel: EditText?,
        etMcLevel: EditText?,
        mcEnabled: Boolean,
        conPN: Int
    ): Boolean {
        //check if multiclass is validated
        if (validateMc(etMcLevel, mcEnabled)) {
            //get and validate con mod and char level
            if (!etCon?.text.isNullOrEmpty() &&
                !etLevel?.text.isNullOrEmpty()
            ) {
                if (etLevel?.text.toString().toInt() >= 1) {
                    val conMod = etCon?.text.toString().toInt() * conPN
                    if (conMod >= -5) {
                        return true
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Con modifier can't be less than -5",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Class level is less than 1",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    "Con modifier or Class level is empty",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            Toast.makeText(
                requireContext(),
                "multiclass info invalid",
                Toast.LENGTH_SHORT
            ).show()
        }
        return false
    }

    private fun validateMc(
        etMcLevel: EditText?,
        mcEnabled: Boolean
    ): Boolean {
        return if (mcEnabled) {
            //validate multiclass info
            !etMcLevel?.text.isNullOrEmpty() && etMcLevel?.text.toString().toInt() >= 1
        } else {
            true
        }
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
            d12 != null
        ) {
            for (rbtn in buttonList) {
                if (rbtn!!.isChecked) {
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
            mcD12 != null
        ) {
            for (rbtn in buttonList) {
                if (rbtn!!.isChecked) {
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

}