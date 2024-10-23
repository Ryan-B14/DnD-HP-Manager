package com.example.dndhpsystem

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment

class DndViewModel : ViewModel() {
    var navController: NavController? = null
    var newMaxAhpSet: Boolean = false
    var newMaxChpSet: Boolean = false
    var currentFrag = 0
    var baseArmor = 0
    var armorBroken: Boolean = false
    val totalArmor: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }
    val maxChp: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }
    val currentTempHp: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }
    val currentChp: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }
    val currentArmorHp: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }
    lateinit var sharedPrefs: SharedPreferences

    fun setNavController(navHostFragment: NavHostFragment) {
        navController = navHostFragment.navController
    }

    fun calculateValues(hd: Int, cl: Int, mcHD: Int, mcCL: Int, armorBonusPercent: Double) {
        /*
        hd = hit die value
        cl = class level
        mcHD = multi-class hit die value
        mcCL = class level
        */

        baseArmor = getBaseArmorValue(hd, cl, mcHD, mcCL)

        val armorBonus = getArmorBonus(baseArmor, armorBonusPercent)

        totalArmor.postValue(baseArmor + armorBonus)
        newMaxAhpSet = true
        currentArmorHp.postValue(totalArmor.value ?: -1)

        sharedPrefs.edit().apply {
            putInt("maxAHP", totalArmor.value ?: 0)
            apply()
        }
        sharedPrefs.edit().apply{
            putInt("currentAHP", currentArmorHp.value ?: 0)
            apply()
        }
    }

    private fun getBaseArmorValue(hd: Int, cl: Int, mcHD: Int, mcCL: Int): Int {
        /*
        hd = hit die value
        cl = class level
        mcHD = multi-class hit die value
        mcCL = class level
        */
        var mcValue = 0
        val baseValue = (hd / 2) * cl
        if (mcHD > 0 && mcCL > 0) {
            mcValue = (mcHD / 2) * mcCL
        }

        return (baseValue + mcValue)
    }

    private fun getArmorBonus(baseArmor: Int, armorBonusPercent: Double): Int {
        return kotlin.math.ceil(armorBonusPercent * baseArmor).toInt()
    }

    fun getMaxChp(
        conMod: Int,
        conPN: Int,
        charLvl: Int,
        tough: Boolean,
        mainHd: Int,
        mcHd: Int,
        charMcLvl: Int,
        mcEnabled: Boolean
    ) {
        var conVal = conMod * conPN
        if (!mcEnabled) {
            if (tough) {
                maxChp.postValue(((mainHd + conVal) + 2) * charLvl)
            } else {
                maxChp.postValue((mainHd + conVal) * charLvl)
            }
            currentChp.postValue(maxChp.value ?: -1)
            newMaxChpSet = true
            sharedPrefs.edit().apply {
                putInt("maxCHP", maxChp.value ?: 0)
                apply()
            }
        } else {
            if (tough) {
                maxChp.postValue((((mainHd + conVal) + 2) * charLvl) + (((mcHd + conVal) + 2) * charMcLvl))
            } else {
                maxChp.postValue(((mainHd + conVal) * charLvl) + ((mcHd + conVal) * charMcLvl))
            }
            currentChp.postValue(maxChp.value ?: -1)
            newMaxChpSet = true
            sharedPrefs.edit().apply {
                putInt("maxCHP", maxChp.value ?: 0)
                apply()
            }
        }
    }

    fun checkSaveData() {
        val maxAHP = sharedPrefs.getInt("maxAHP", 0)
        val maxCHP = sharedPrefs.getInt("maxCHP", 0)
        val currentAHP = sharedPrefs.getInt("currentAHP", 0)
        val currentCHP = sharedPrefs.getInt("currentCHP", 0)
        val currentTHP = sharedPrefs.getInt("currentTHP", 0)

        totalArmor.postValue(maxAHP)
        maxChp.postValue(maxCHP)
        currentArmorHp.postValue(currentAHP)
        currentChp.postValue(currentCHP)
        currentTempHp.postValue(currentTHP)
        armorBroken = sharedPrefs.getBoolean("armorBreak", false)
    }
}