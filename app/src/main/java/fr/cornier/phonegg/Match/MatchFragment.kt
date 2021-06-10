package fr.cornier.phonegg.Match

import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import fr.cornier.phonegg.History.HistoryFragmentArgs
import fr.cornier.phonegg.History.HistoryFragmentDirections
import fr.cornier.phonegg.History.HistoryViewModel
import fr.cornier.phonegg.MainActivity
import fr.cornier.phonegg.R
import fr.cornier.phonegg.databinding.FragmentHistoryBinding
import fr.cornier.phonegg.databinding.FragmentMatchBinding
import io.realm.Realm
import org.json.JSONObject

class MatchFragment : Fragment() {

    private val args: MatchFragmentArgs by navArgs()

    private var _binding: FragmentMatchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MatchViewModel by viewModels()

    private var navigationEnable = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentMatchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).setNavStatus(false)

        binding.homeButton.setOnClickListener { if (navigationEnable) findNavController().navigate(R.id.action_matchFragment_to_homeFragment) }

        binding.backButton.setOnClickListener { if (navigationEnable) activity?.onBackPressed() }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.summonerMainInformation.observe(requireActivity(), { summonerMainInformation -> setSummonerMainInfo(summonerMainInformation) })
        viewModel.summonerIcon.observe(requireActivity(), { summonerIcon -> setSummonerIcon(summonerIcon) })
        viewModel.mainMatchInformation.observe(requireActivity(), { mainMatchInformation -> setMainMatchInformation(mainMatchInformation) })
        viewModel.winOrLose.observe(requireActivity(), { winOrLose -> setWinOrLose(winOrLose) })
        viewModel.winOrLoseColor.observe(requireActivity(), { winOrLoseColor -> setWinOrLoseColor(winOrLoseColor) })
        viewModel.roleIcon.observe(requireActivity(), { roleIcon -> setRoleIcon(roleIcon) })
        viewModel.visionScore.observe(requireActivity(), { visionScore -> setVisionScore(visionScore) })
        viewModel.csScore.observe(requireActivity(), { csScore -> setCsScore(csScore) })
        viewModel.dmgScore.observe(requireActivity(), { dmgScore -> setDmgScore(dmgScore) })
        viewModel.champIcon.observe(requireActivity(), { champIcon -> setChampIcon(champIcon) })
        viewModel.teamId.observe(requireActivity(), { teamId -> setTeamId(teamId) })
        viewModel.teamIdColor.observe(requireActivity(), { teamIdColor -> setTeamIdColor(teamIdColor) })
        viewModel.firstSummonerSpellIcon.observe(requireActivity(), { firstSummonerSpellIcon -> setFirstSummonerSpellIcon(firstSummonerSpellIcon) })
        viewModel.secondSummonerSpellIcon.observe(requireActivity(), { secondSummonerSpellIcon -> setSecondSummonerSpellIcon(secondSummonerSpellIcon) })
        viewModel.firstRuneIcon.observe(requireActivity(), { firstRuneIcon -> setFirstRuneIcon(firstRuneIcon) })
        viewModel.secondRuneIcon.observe(requireActivity(), { secondRuneIcon -> setSecondRuneIcon(secondRuneIcon) })
        viewModel.kdaText.observe(requireActivity(), { kdaText -> setKdaText(kdaText) })
        viewModel.kdaColor.observe(requireActivity(), { kdaColor -> setKdaColor(kdaColor) })
        viewModel.detailedKdaText.observe(requireActivity(), { detailedKdaText -> setDetailedKdaText(detailedKdaText) })
        viewModel.csText.observe(requireActivity(), { csText -> setCsText(csText) })
        viewModel.killParticipation.observe(requireActivity(), { killParticipation -> setKillParticipation(killParticipation) })
        viewModel.dmgText.observe(requireActivity(), { dmgText -> setDmgText(dmgText) })
        viewModel.dmgPercent.observe(requireActivity(), { dmgPercent -> setDmgPercent(dmgPercent) })
        viewModel.item1.observe(requireActivity(), { item1 -> setitem1(item1) })
        viewModel.item2.observe(requireActivity(), { item2 -> setitem2(item2) })
        viewModel.item3.observe(requireActivity(), { item3 -> setitem3(item3) })
        viewModel.item4.observe(requireActivity(), { item4 -> setitem4(item4) })
        viewModel.item5.observe(requireActivity(), { item5 -> setitem5(item5) })
        viewModel.item6.observe(requireActivity(), { item6 -> setitem6(item6) })
        viewModel.item7.observe(requireActivity(), { item7 -> setitem7(item7) })
        viewModel.kda1.observe(requireActivity(), { kda1 -> setKda1(kda1) })
        viewModel.gold1.observe(requireActivity(), { gold1 -> setGold1(gold1) })
        viewModel.turret1.observe(requireActivity(), { turret1 -> setturret1(turret1) })
        viewModel.inhib1.observe(requireActivity(), { inhib1 -> setinhib1(inhib1) })
        viewModel.nash1.observe(requireActivity(), { nash1 -> setnash1(nash1) })
        viewModel.herald1.observe(requireActivity(), { herald1 -> setherald1(herald1) })
        viewModel.drake1.observe(requireActivity(), { drake1 -> setdrake1(drake1) })
        viewModel.turretIcon1.observe(requireActivity(), { turretIcon1 -> setturretIcon1(turretIcon1) })
        viewModel.inhibIcon1.observe(requireActivity(), { inhibIcon1 -> setinhibIcon1(inhibIcon1) })
        viewModel.nashIcon1.observe(requireActivity(), { nashIcon1 -> setnashIcon1(nashIcon1) })
        viewModel.heraldIcon1.observe(requireActivity(), { heraldIcon1 -> setheraldIcon1(heraldIcon1) })
        viewModel.drakeIcon1.observe(requireActivity(), { drakeIcon1 -> setdrakeIcon1(drakeIcon1) })
        viewModel.kda2.observe(requireActivity(), { kda2 -> setKda2(kda2) })
        viewModel.gold2.observe(requireActivity(), { gold2 -> setGold2(gold2) })
        viewModel.turret2.observe(requireActivity(), { turret2 -> setturret2(turret2) })
        viewModel.inhib2.observe(requireActivity(), { inhib2 -> setinhib2(inhib2) })
        viewModel.nash2.observe(requireActivity(), { nash2 -> setnash2(nash2) })
        viewModel.herald2.observe(requireActivity(), { herald2 -> setherald2(herald2) })
        viewModel.drake2.observe(requireActivity(), { drake2 -> setdrake2(drake2) })
        viewModel.turretIcon2.observe(requireActivity(), { turretIcon2 -> setturretIcon2(turretIcon2) })
        viewModel.inhibIcon2.observe(requireActivity(), { inhibIcon2 -> setinhibIcon2(inhibIcon2) })
        viewModel.nashIcon2.observe(requireActivity(), { nashIcon2 -> setnashIcon2(nashIcon2) })
        viewModel.heraldIcon2.observe(requireActivity(), { heraldIcon2 -> setheraldIcon2(heraldIcon2) })
        viewModel.drakeIcon2.observe(requireActivity(), { drakeIcon2 -> setdrakeIcon2(drakeIcon2) })

        viewModel.getMatchInformation(args.summonerId, args.matchId, activity, this)
    }

    private fun setTeamIdColor(teamIdColor: Int?) {
        if (teamIdColor != null) {
            binding.teamId.setTextColor(ContextCompat.getColor(requireContext(), teamIdColor))
        } else {
            binding.teamId.setTextColor(null)
        }
    }

    private fun setTeamId(teamId: String?) {
        if (teamId != null) {
            binding.teamId.text = teamId
        } else {
            binding.teamId.text = null
        }
    }

    private fun setdrakeIcon2(drakeIcon2: Bitmap?) {
        if (drakeIcon2 != null) {
            binding.drakeIcon2.setImageBitmap(drakeIcon2)
            enableNavigation()
        } else {
            binding.drakeIcon2.setImageBitmap(null)
            enableNavigation()
        }
    }

    private fun setheraldIcon2(heraldIcon2: Bitmap?) {
        if (heraldIcon2 != null) {
            binding.heraldIcon2.setImageBitmap(heraldIcon2)
        } else {
            binding.heraldIcon2.setImageBitmap(null)
        }
    }

    private fun setnashIcon2(nashIcon2: Bitmap?) {
        if (nashIcon2 != null) {
            binding.nashIcon2.setImageBitmap(nashIcon2)
        } else {
            binding.nashIcon2.setImageBitmap(null)
        }
    }

    private fun setinhibIcon2(inhibIcon2: Bitmap?) {
        if (inhibIcon2 != null) {
            binding.inhibIcon2.setImageBitmap(inhibIcon2)
        } else {
            binding.inhibIcon2.setImageBitmap(null)
        }
    }

    private fun setturretIcon2(turretIcon2: Bitmap?) {
        if (turretIcon2 != null) {
            binding.turretIcon2.setImageBitmap(turretIcon2)
        } else {
            binding.turretIcon2.setImageBitmap(null)
        }
    }

    private fun setdrake2(drake2: String?) {
        if (drake2 != null) {
            binding.drake2.text = drake2
        } else {
            binding.drake2.text = null
        }
    }

    private fun setherald2(herald2: String?) {
        if (herald2 != null) {
            binding.herald2.text = herald2
        } else {
            binding.herald2.text = null
        }
    }

    private fun setnash2(nash2: String?) {
        if (nash2 != null) {
            binding.nash2.text = nash2
        } else {
            binding.nash2.text = null
        }
    }

    private fun setinhib2(inhib2: String?) {
        if (inhib2 != null) {
            binding.inhib2.text = inhib2
        } else {
            binding.inhib2.text = null
        }
    }

    private fun setturret2(turret2: String?) {
        if (turret2 != null) {
            binding.turret2.text = turret2
        } else {
            binding.turret2.text = null
        }
    }

    private fun setGold2(gold2: String?) {
        if (gold2 != null) {
            binding.gold2.text = gold2

            binding.goldImage2.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.icon_gold))
        } else {
            binding.gold2.text = null

            binding.goldImage2.setImageDrawable(null)
        }
    }

    private fun setKda2(kda2: String?) {
        if (kda2 != null) {
            binding.kda2.text = kda2
        } else {
            binding.kda2.text = null
        }
    }

    private fun setdrakeIcon1(drakeIcon1: Bitmap?) {
        if (drakeIcon1 != null) {
            binding.drakeIcon1.setImageBitmap(drakeIcon1)
        } else {
            binding.drakeIcon1.setImageBitmap(null)
        }
    }

    private fun setheraldIcon1(heraldIcon1: Bitmap?) {
        if (heraldIcon1 != null) {
            binding.heraldIcon1.setImageBitmap(heraldIcon1)
        } else {
            binding.heraldIcon1.setImageBitmap(null)
        }
    }

    private fun setnashIcon1(nashIcon1: Bitmap?) {
        if (nashIcon1 != null) {
            binding.nashIcon1.setImageBitmap(nashIcon1)
        } else {
            binding.nashIcon1.setImageBitmap(null)
        }
    }

    private fun setinhibIcon1(inhibIcon1: Bitmap?) {
        if (inhibIcon1 != null) {
            binding.inhibIcon1.setImageBitmap(inhibIcon1)
        } else {
            binding.inhibIcon1.setImageBitmap(null)
        }
    }

    private fun setturretIcon1(turretIcon1: Bitmap?) {
        if (turretIcon1 != null) {
            binding.turretIcon1.setImageBitmap(turretIcon1)
        } else {
            binding.turretIcon1.setImageBitmap(null)
        }
    }

    private fun setdrake1(drake1: String?) {
        if (drake1 != null) {
            binding.drake1.text = drake1
        } else {
            binding.drake1.text = null
        }
    }

    private fun setherald1(herald1: String?) {
        if (herald1 != null) {
            binding.herald1.text = herald1
        } else {
            binding.herald1.text = null
        }
    }

    private fun setnash1(nash1: String?) {
        if (nash1 != null) {
            binding.nash1.text = nash1
        } else {
            binding.nash1.text = null
        }
    }

    private fun setinhib1(inhib1: String?) {
        if (inhib1 != null) {
            binding.inhib1.text = inhib1
        } else {
            binding.inhib1.text = null
        }
    }

    private fun setturret1(turret1: String?) {
        if (turret1 != null) {
            binding.turret1.text = turret1
        } else {
            binding.turret1.text = null
        }
    }

    private fun setGold1(gold1: String?) {
        if (gold1 != null) {
            binding.gold1.text = gold1

            binding.goldImage1.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.icon_gold))
        } else {
            binding.gold1.text = null

            binding.goldImage1.setImageDrawable(null)
        }
    }

    private fun setKda1(kda1: String?) {
        if (kda1 != null) {
            binding.kda1.text = kda1
        } else {
            binding.kda1.text = null
        }
    }

    private fun setitem7(item7: Bitmap?) {
        if (item7 != null) {
            binding.item7.setImageBitmap(item7)
        } else {
            binding.item7.setImageBitmap(null)
        }
    }

    private fun setitem6(item6: Bitmap?) {
        if (item6 != null) {
            binding.item6.setImageBitmap(item6)
        } else {
            binding.item6.setImageBitmap(null)
        }
    }

    private fun setitem5(item5: Bitmap?) {
        if (item5 != null) {
            binding.item5.setImageBitmap(item5)
        } else {
            binding.item5.setImageBitmap(null)
        }
    }

    private fun setitem4(item4: Bitmap?) {
        if (item4 != null) {
            binding.item4.setImageBitmap(item4)
        } else {
            binding.item4.setImageBitmap(null)
        }
    }

    private fun setitem3(item3: Bitmap?) {
        if (item3 != null) {
            binding.item3.setImageBitmap(item3)
        } else {
            binding.item3.setImageBitmap(null)
        }
    }

    private fun setitem2(item2: Bitmap?) {
        if (item2 != null) {
            binding.item2.setImageBitmap(item2)
        } else {
            binding.item2.setImageBitmap(null)
        }
    }

    private fun setitem1(item1: Bitmap?) {
        if (item1 != null) {
            binding.item1.setImageBitmap(item1)
        } else {
            binding.item1.setImageBitmap(null)
        }
    }

    private fun setDmgPercent(dmgPercent: String?) {
        if (dmgPercent != null) {
            binding.percentDmg.text = dmgPercent
        } else {
            binding.percentDmg.text = null
        }
    }

    private fun setDmgText(dmgText: String?) {
        if (dmgText != null) {
            binding.globalDMG.text = dmgText
        } else {
            binding.globalDMG.text = null
        }
    }

    private fun setKillParticipation(killParticipation: String?) {
        if (killParticipation != null) {
            binding.KP.text = killParticipation
        } else {
            binding.KP.text = null
        }
    }

    private fun setCsText(csText: String?) {
        if (csText != null) {
            binding.globalCS.text = csText
        } else {
            binding.globalCS.text = null
        }
    }

    private fun setDetailedKdaText(detailedKdaText: String?) {
        if (detailedKdaText != null) {
            binding.detailedKDA.text = detailedKdaText
        } else {
            binding.detailedKDA.text = null
        }
    }

    private fun setKdaColor(kdaColor: Int?) {
        if (kdaColor != null) {
            binding.kda.setTextColor(ContextCompat.getColor(requireContext(), kdaColor))
        } else {
            binding.kda.setTextColor(null)
        }
    }

    private fun setKdaText(kdaText: String?) {
        if (kdaText != null) {
            binding.kda.text = kdaText
        } else {
            binding.kda.text = null
        }
    }

    private fun setSecondRuneIcon(secondRuneIcon: Bitmap?) {
        if (secondRuneIcon != null) {
            binding.secondSummonerRune.setImageBitmap(secondRuneIcon)
        } else {
            binding.secondSummonerRune.setImageBitmap(null)
        }
    }

    private fun setFirstRuneIcon(firstRuneIcon: Bitmap?) {
        if (firstRuneIcon != null) {
            binding.firstSummonerRune.setImageBitmap(firstRuneIcon)
        } else {
            binding.firstSummonerRune.setImageBitmap(null)
        }
    }

    private fun setSecondSummonerSpellIcon(secondSummonerSpellIcon: Bitmap?) {
        if (secondSummonerSpellIcon != null) {
            binding.secondSummonerSpell.setImageBitmap(secondSummonerSpellIcon)
        } else {
            binding.secondSummonerSpell.setImageBitmap(null)
        }
    }

    private fun setFirstSummonerSpellIcon(firstSummonerSpellIcon: Bitmap?) {
        if (firstSummonerSpellIcon != null) {
            binding.firstSummonerSpell.setImageBitmap(firstSummonerSpellIcon)
        } else {
            binding.firstSummonerSpell.setImageBitmap(null)
        }
    }

    private fun setChampIcon(champIcon: Bitmap?) {
        if (champIcon != null) {
            binding.champIcon.setImageBitmap(champIcon)
        } else {
            binding.champIcon.setImageBitmap(null)
        }
    }

    private fun setDmgScore(dmgScore: String?) {
        if (dmgScore != null) {
            binding.dmgScore.text = dmgScore
        } else {
            binding.dmgScore.text = null
        }
    }

    private fun setCsScore(csScore: String?) {
        if (csScore != null) {
            binding.csScore.text = csScore
        } else {
            binding.csScore.text = null
        }
    }

    private fun setVisionScore(visionScore: String?) {
        if (visionScore != null) {
            binding.visionScore.text = visionScore
        } else {
            binding.visionScore.text = null
        }
    }

    private fun setRoleIcon(roleIcon: Bitmap?) {
        if (roleIcon != null) {
            binding.roleIcon.setImageBitmap(roleIcon)
        } else {
            binding.roleIcon.setImageBitmap(null)
        }
    }

    private fun setWinOrLoseColor(winOrLoseColor: Int?) {
        if (winOrLoseColor != null) {
            binding.pageTitle.setTextColor(ContextCompat.getColor(requireContext(), winOrLoseColor))
            binding.SummonerIcon.borderColor = ContextCompat.getColor(requireContext(), winOrLoseColor)
        } else {
            binding.pageTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.primaryText))
        }
    }

    private fun setWinOrLose(winOrLose: String?) {
        if (winOrLose != null) {
            binding.pageTitle.text = winOrLose
        } else {
            binding.pageTitle.text = "Match"
        }
    }

    private fun setMainMatchInformation(mainMatchInformation: String?) {
        if (mainMatchInformation != null) {
            binding.mainMatchInfo.text = mainMatchInformation
        } else {
            binding.mainMatchInfo.text = null
        }
    }

    private fun setSummonerIcon(summonerIcon: Bitmap?) {
        if (summonerIcon != null) {
            binding.SummonerIcon.setImageBitmap(summonerIcon)
        } else {
            binding.SummonerIcon.setImageBitmap(null)
        }
    }

    private fun setSummonerMainInfo(summonerMainInformation: JSONObject?) {
        if (summonerMainInformation != null) {
            binding.matchSummonerName.text = summonerMainInformation.getString("name")
        } else {
            binding.matchSummonerName.text = null
        }
    }

    fun enableNavigation() {
        navigationEnable = true
        (activity as MainActivity).setNavStatus(true)
    }
}