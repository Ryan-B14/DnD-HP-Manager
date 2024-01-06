package com.example.dndhpsystem

import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import java.lang.Math.ceil
import kotlin.math.roundToInt

class DndViewModel : ViewModel() {
    var navController: NavController? = null
    var currentFrag = 0
    var baseArmor = 0
    var totalArmor = 0
    var maxChp = 0
    var currentTempHp = 0
    var currentChp = -1
    var currentArmorHp = -1

    fun setNavController(navHostFragment: NavHostFragment) {
        navController = navHostFragment.navController
    }

    fun calculateValues(hd: Int, cl: Int, mcHD: Int, mcCL: Int, armorBonusPercent: Double) {
        //hd = hit die value
        //cl = class level
        //mcHD = multi-class hit die value
        //mcCL = class level

        baseArmor = getBaseArmorValue(hd, cl, mcHD, mcCL)

        val armorBonus = getArmorBonus(baseArmor, armorBonusPercent)

        totalArmor = baseArmor + armorBonus
        currentArmorHp = totalArmor
    }

    private fun getBaseArmorValue(hd: Int, cl: Int, mcHD: Int, mcCL: Int): Int {
        var mcValue = 0
        val baseValue = (hd/2)* cl
        if(mcHD > 0 && mcCL > 0) {
            mcValue = (mcHD/2) * mcCL
        }

        return (baseValue + mcValue)
    }

    private fun getArmorBonus(baseArmor: Int, armorBonusPercent: Double): Int {
        return kotlin.math.ceil(armorBonusPercent * baseArmor).toInt()
    }

    fun getMaxChp(conMod: Int, conPN: Int, charLvl: Int, tough: Boolean) {
        if (baseArmor > 0) {
            if (tough) {
                maxChp = baseArmor + (((conMod * conPN) + 2) * charLvl)
            } else {
                maxChp = baseArmor + ((conMod  * conPN) * charLvl)
            }
            currentChp = maxChp
        }
    }

}