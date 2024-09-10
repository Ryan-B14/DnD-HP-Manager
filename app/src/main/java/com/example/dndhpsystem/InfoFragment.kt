package com.example.dndhpsystem

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class InfoFragment : Fragment() {

    lateinit var tvAppInfo: TextView
    lateinit var tvRules: TextView
    lateinit var tvExtraInfo: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setResources()
        tvAppInfo.text = getAppInfo()
        tvRules.text = getRules()
        tvExtraInfo.text = getExtraInfo()
    }

    private fun setResources() {
        tvAppInfo = requireView().findViewById(R.id.tv_app_info)
        tvRules = requireView().findViewById(R.id.tv_rules)
        tvExtraInfo = requireView().findViewById(R.id.tv_extra)
    }

    private fun getAppInfo(): String {
        val string =
            "- Set values button: When a value is entered for both armor and character hp, clicking the button will set your max " +
                    "armor and character hp to the entered values.\n" +
                    "- Reset values button: Sets current armor and character hp's to their maximum.\n" +
                    "- Damage carries over to the next hp pool.  Ex: you have 5 temp hp, 5 armor hp, and 20 character hp.  " +
                    "If you take 20 damage, you can enter 20 in the temp hp line, hit minus, and it will " +
                    "take away 5 temp hp, 5 armor hp, and 10 character hp.\n" +
                    "- Healing only works in the line you enter it, does not carry over to another hp pool, " +
                    "and can't go above maximum."

        return string
    }

    private fun getRules(): String {
        val string = "- AC follows DnD 5e rules.\n" +
                "- Temp Hp is stackable, but only lasts 10 minutes.  Increasing temp hp refreshes that time limit.\n" +
                "- Healing magic, feats, and abilities can EITHER recover Character hp OR add Temporary hp.  The character performing " +
                "the action chooses which.\n" +
                "- Health potions only affect character hp.  Must use new item 'armor repair kits(ARKs)' to recover armor hp.\n" +
                "- Players can use an action to get the full healing effect from an item, or use a bonus action to roll for it.\n" +
                "- Long rests only recover character hp.\n" +
                "- The order which damage is received = temp hp -> armor hp -> character hp\n" +
                "\t\t- Extra damage carries over to the next hp in line.\n" +
                "\t\t- Psychic and poison damage temp hp and character hp, never armor hp.  All other damage types follow " +
                "normal damage flow.\n" +
                "- Armor inherits the users damage resistances.\n" +
                "- When Armor reaches 0hp, it breaks!  This means that it can only recover up to half of its usual maximum " +
                "until it can get properly repaired. It is up to the DM's discretion of what 'properly repaired' means, " +
                "but usually that means getting worked on by a professional.\n" +
                "- Light armor gives advantage on dex saves\n" +
                "- Medium armor gives no bonuses. Mithril variant gives advantage on dex saves.\n" +
                "- Heavy armor gives disadvantage on dex saves. Mithril variant removes this disadvantage.\n" +
                "- Unarmored Defence(Monks and Barbarians)\n" +
                "\t\t- Treated like medium armor\n" +
                "\t\t- Can be recovered with a hit die as a bonus action.  DnD 5e rules for hit dice apply.\n" +
                "- Pet armor hp = 5x Pet's level /or/ 5x Pet's CR (5hp minimum)\n" +
                "\t\t- Pet hp is unaffected by system.  Armor only adds extra hp.\n" +
                "- Creatures with stat blocks are unaffected by this system.\n" +
                "- Things that can recover armor hp:\n" +
                "\t\t- Tradesmen: Blacksmiths / Armor smiths / Tailors(Light Armor only) / Doctors and *Clerics(Unarmored Defence only)\n" +
                "\t\t- Hit dice(Unarmored Defence only)\n" +
                "\t\t- Armor Repair Kits (ARKs)\n" +
                "\t\t\t- ARKs work and cost exactly the same as hp potions, but only affect armor.\n" +
                "\t\t\t\t- ex: lesser ARK repairs 2d4+2, greater ARK repairs 4d4+4, etc.\n" +
                "note: The sum of the hp values in this system total to more than what a " +
                "character would normally have at their level.  This was done to compensate for the " +
                "limiting of health recovery effectiveness.  Plan accordingly.\n" +
                "\n*Cleric NPCs only.  Player clerics can't recover armor hp.  If you need an in-world " +
                "explanation, here are some you can use:\n" +
                "\t\t- Requires resources only found in a monastery.\n" +
                "\t\t- Monasteries have healing pools blessed by their deity.\n" +
                "\t\t- The clerics work as physical therapists to help aid in faster recovery.\n" +
                "note: The sum of the hp values in this system total to more than what a " +
                "character would normally have at their level.  This was done to compensate for the " +
                "limiting of health recovery effectiveness.  Plan accordingly.\n"

        return string
    }

    private fun getExtraInfo(): String {
        val string = "Purpose: \n" +
                "I created this system to change how players engage with the game.  Its intended to strike " +
                "a balance between casual play and the difficulty of survival dnd, without restructuring the whole game. " +
                "The goal is for the system to create scenarios where it encourage players to consider things like: \n" +
                "- managing their equipment\n" +
                "- interacting with NPCs\n" +
                "- choosing not to engage in battles, even if the enemy are bad guys.\n" +
                "- spending their money(ex: on ARKs, new/spare armor sets, blacksmiths, etc.)\n" +
                "- what armor to wear going into a dungeon.\n" +
                "- the danger of multiple battles in a row or multiple days in a row (if they can't find somewhere to get their " +
                "equipment fixed).\n" +
                "I also wanted this to address the oddness of characters sleeping off all damage.  Now, damage can have lasting effects " +
                "without being super intrusive or difficult to deal with.  This system also gives DMs another 'dial' to fiddle with" +
                "to help keep their players engaged.  Maybe they are 3 days away from the next town and have to cross goblin " +
                "territory.  Now they might want to consider avoiding some fights because they don't have enough supplies to " +
                "top off their hp at the end of each day.\n" +
                "Have fun.\n" +
                "\nOptional rules:\n" +
                "Leveled armor. Calculate their armor like normal, but use a lower or higher level than the character’s during the calculation.  " +
                "Character hp should always match their level, but armor hp doesn’t have to. \n" +
                "\t\t- This rule is under optional rules, but I would highly recommend using it. \n" +
                "- If players want to loot armor off creatures, I'd recommend that the armor be damaged. (25%-50% armor hp left)\n" +
                "- Using mending cantrip can repair armor.  It fully repairs 1 set of armor after an hour of casting.\n" +
                "- Armored enemies use this system.\n" +
                "- Heavy damage or certain damage types can break armor without it reaching 0hp. I would recommend waiting until " +
                "the armor reaches or falls below half hp before ruling that it breaks this way.\n" +
                "- Heat metal bypasses armor hp and damages temp hp or character hp.\n" +
                "- When rolling to use a healing item (ark or potion), the remaining recovery amount should still be " +
                "available to use. \n" +
                "\t\t- ex: a health potion can heal up to 10 hp(2d4+2). Player rolls and heals 4 hp. That health potion bottle now has " +
                "6hp worth of potion left in it.  Later the player can take an action to finish it or roll to see if they get " +
                "a 6 or higher to finish the potion.\n" +
                "- Considering +1,2,3 armor and/or weapons magical or not.\n" +
                "- Armor types could have built in vulnerabilities/resistances to certain damage types.  Some ideas:\n" +
                "\t\t- Light: vulnerable: slashing, fire / resistant: lightning\n" +
                "\t\t- Medium: vulnerable: piercing, acid / resistant: cold\n" +
                "\t\t- Heavy: vulnerable: bludgeoning, lightning / resistant: slashing, piercing\n" +
                "\nFormulas (ALWAYS round up, even on .1):\n" +
                "- character hp = ((hit die / 2) + con modifier (+2 if tough)) * character level\n" +
                "- base armor = (class hit die value / 2) * class level\n" +
                "- armor bonus = armor bonus % * base armor\n" +
                "- total armor = armor bonus + base armor\n" +
                "\nArmor bonus %\n" +
                "- Light = 75%\n" +
                "- Medium = 125%\n" +
                "- Heavy = 150%\n" +
                "\n\nI encourage you to tweak any rules as you see fit.  I hope as " +
                "a dm you can understand the spirit of this system and can find ways to improve it.  " +
                "The optional rules have not been thoroughly thought out or balanced like the main rules.  " +
                "They are meant to be more like starting ideas that can be built upon or modified.\n"
        return string
    }
}