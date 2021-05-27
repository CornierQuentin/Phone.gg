package fr.cornier.phonegg.SummonerInformation

import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import fr.cornier.phonegg.AddSummonerPage.AddSummonerViewModel
import fr.cornier.phonegg.R
import fr.cornier.phonegg.databinding.FragmentHomeBinding
import fr.cornier.phonegg.databinding.FragmentSummonerInformationBinding
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_add_summoner.*
import kotlinx.android.synthetic.main.fragment_summoner_information.*
import org.json.JSONObject
import java.text.NumberFormat

class SummonerInformationFragment : Fragment() {

    private val args: SummonerInformationFragmentArgs by navArgs()

    private var _binding: FragmentSummonerInformationBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SummonerInformationViewModel by viewModels()

    lateinit var realm: Realm

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSummonerInformationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.homeButton.setOnClickListener { findNavController().navigate(R.id.action_summonerInformationFragment_to_homeFragment) }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.summonerMainInformation.observe(requireActivity(), { summonerMainInformation -> showMainInformation(summonerMainInformation) })
        viewModel.summonerIcon.observe(requireActivity(), { summonerIcon -> setIcon(summonerIcon) })
        viewModel.summonerRankBorder.observe(requireActivity(), { summonerRankBorder -> setIconRankBorder(summonerRankBorder) })
        viewModel.summonerRankIcon.observe(requireActivity(), { summonerRankIcon -> setIconRankIcon(summonerRankIcon) })
        viewModel.summonerRankBorderSword.observe(requireActivity(), { summonerRankBorderSword -> setIconRankBorderSword(summonerRankBorderSword) })
        viewModel.summonerRankBorderCrown.observe(requireActivity(), { summonerRankBorderCrown -> setIconRankBorderCrown(summonerRankBorderCrown) })
        viewModel.summonerRankBorderWing.observe(requireActivity(), { summonerRankBorderWing -> setIconRankBorderWing(summonerRankBorderWing) })
        viewModel.summonerRankBorderSecondWing.observe(requireActivity(), { summonerRankBorderSecondWing -> setIconRankBorderSecondWing(summonerRankBorderSecondWing) })
        viewModel.summonerMasteryChamp1Icon.observe(requireActivity(), { summonerMasteryChamp1Icon -> setSummonerMasteryChamp1Icon(summonerMasteryChamp1Icon) })
        viewModel.summonerMasteryChamp2Icon.observe(requireActivity(), { summonerMasteryChamp2Icon -> setSummonerMasteryChamp2Icon(summonerMasteryChamp2Icon) })
        viewModel.summonerMasteryChamp3Icon.observe(requireActivity(), { summonerMasteryChamp3Icon -> setSummonerMasteryChamp3Icon(summonerMasteryChamp3Icon) })
        viewModel.mastery1.observe(requireActivity(), { mastery1 -> setMastery1(mastery1) })
        viewModel.mastery2.observe(requireActivity(), { mastery2 -> setMastery2(mastery2) })
        viewModel.mastery3.observe(requireActivity(), { mastery3 -> setMastery3(mastery3) })
        viewModel.mastery1Points.observe(requireActivity(), { mastery1Points -> setMastery1Points(mastery1Points) })
        viewModel.mastery2Points.observe(requireActivity(), { mastery2Points -> setMastery2Points(mastery2Points) })
        viewModel.mastery3Points.observe(requireActivity(), { mastery3Points -> setMastery3Points(mastery3Points) })
        viewModel.divText.observe(requireActivity(), { divText -> setDivText(divText) })
        viewModel.divTextColor.observe(requireActivity(), { divTextColor -> setDivTextColor(divTextColor) })
        viewModel.lp.observe(requireActivity(), { lp -> setLPText(lp) })
        viewModel.winLoseRation.observe(requireActivity(), { winLoseRation -> setWinLoseRationText(winLoseRation) })
        viewModel.winrate.observe(requireActivity(), { winrate -> setWinrateText(winrate) })
        viewModel.winrateColor.observe(requireActivity(), { winrateColor -> setWinrateColor(winrateColor) })

        viewModel.unranked.observe(requireActivity(), { unranked -> setUnrankedTextVisibility(unranked) })
        viewModel.noMasteries.observe(requireActivity(), { noMasteries -> setNoMasteriesTextVisibility(noMasteries)})

        viewModel.getSummonerMainInformation(args.summonerAccountId, activity)
    }

    private fun setWinrateColor(winrateColor: Int?) {
        if (winrateColor != null) {
            binding.winrate.setTextColor(ContextCompat.getColor(requireContext(), winrateColor))
        } else {
            binding.winrate.setTextColor(null)
        }
    }

    private fun setWinrateText(winrate: String?) {
        if (winrate != null) {
            binding.winrate.text = winrate
        } else {
            binding.winrate.text = null
        }
    }

    private fun setWinLoseRationText(winLoseRation: String?) {
        if (winLoseRation != null) {
            binding.winLoseRation.text = winLoseRation
        } else {
            binding.winLoseRation.text = null
        }
    }

    private fun setLPText(lp: String?) {
        if (lp != null) {
            binding.LPNumber.text = lp
        } else {
            binding.LPNumber.text = null
        }
    }

    private fun setDivTextColor(divTextColor: Int?) {
        if (divTextColor != null) {
            binding.DivText.setTextColor(ContextCompat.getColor(requireContext(), divTextColor))
        } else {
            binding.DivText.setTextColor(null)
        }
    }

    private fun setNoMasteriesTextVisibility(noMasteries: Boolean?) {
        if (noMasteries != null) {
            if (noMasteries) {
                binding.noMasteriesText.visibility = View.VISIBLE
            } else {
                binding.noMasteriesText.visibility = View.INVISIBLE
            }
        }
    }

    private fun setDivText(divText: String?) {
        if (divText != null) {
            binding.DivText.text = divText
        } else {
            binding.DivText.text = null
        }
    }

    private fun setIconRankBorderSword(summonerRankBorderSword: Bitmap?) {
        if (summonerRankBorderSword != null) {
            binding.rankIconSword.setImageBitmap(summonerRankBorderSword)
        } else {
            binding.rankIconSword.setImageBitmap(null)
        }
    }

    private fun setIconRankIcon(summonerRankIcon: Bitmap?) {
        if (summonerRankIcon != null) {
            binding.rankIcon.setImageBitmap(summonerRankIcon)
        } else {
            binding.rankIcon.setImageBitmap(null)
        }
    }

    private fun setUnrankedTextVisibility(unranked: Boolean?) {
        if (unranked != null) {
            if (unranked) {
                binding.unrankedText.visibility = View.VISIBLE
            } else {
                binding.unrankedText.visibility = View.INVISIBLE
            }
        }
    }

    private fun setMastery3Points(mastery3Points: Int?) {
        if (mastery3Points != null) {

            val masteryPointsText = NumberFormat.getIntegerInstance().format(mastery3Points) + " pts"
            binding.masteryPoints3.text = masteryPointsText
        } else {
            binding.masteryPoints3.text = null
        }
    }

    private fun setMastery2Points(mastery2Points: Int?) {
        if (mastery2Points != null) {

            val masteryPointsText = NumberFormat.getIntegerInstance().format(mastery2Points) + " pts"
            binding.masteryPoints2.text = masteryPointsText
        } else {
            binding.masteryPoints2.text = null
        }
    }

    private fun setMastery1Points(mastery1Points: Int?) {
        if (mastery1Points != null) {

            val masteryPointsText = NumberFormat.getIntegerInstance().format(mastery1Points) + " pts"
            binding.masteryPoints1.text = masteryPointsText
        } else {
            binding.masteryPoints1.text = null
        }
    }

    private fun setMastery3(mastery3: Int?) {
        if (mastery3 != null) {
            binding.mastery3.setImageDrawable(ContextCompat.getDrawable(requireContext(), mastery3))
        } else {
            binding.mastery3.setImageBitmap(null)
        }
    }

    private fun setMastery2(mastery2: Int?) {
        if (mastery2 != null) {
            binding.mastery2.setImageDrawable(ContextCompat.getDrawable(requireContext(), mastery2))
        } else {
            binding.mastery2.setImageBitmap(null)
        }
    }

    private fun setMastery1(mastery1: Int?) {
        if (mastery1 != null) {
            binding.mastery1.setImageDrawable(ContextCompat.getDrawable(requireContext(), mastery1))
        } else {
            binding.mastery1.setImageBitmap(null)
        }
    }

    private fun setSummonerMasteryChamp3Icon(summonerMasteryChamp3Icon: Bitmap?) {
        if (summonerMasteryChamp3Icon != null) {
            binding.SummonerChampionMastery3.setImageBitmap(summonerMasteryChamp3Icon)
        } else {
            binding.SummonerChampionMastery3.setImageBitmap(null)
        }
    }

    private fun setSummonerMasteryChamp2Icon(summonerMasteryChamp2Icon: Bitmap?) {
        if (summonerMasteryChamp2Icon != null) {
            binding.SummonerChampionMastery2.setImageBitmap(summonerMasteryChamp2Icon)
        } else {
            binding.SummonerChampionMastery2.setImageBitmap(null)
        }
    }

    private fun setSummonerMasteryChamp1Icon(summonerMasteryChamp1Icon: Bitmap?) {
        if (summonerMasteryChamp1Icon != null) {
            binding.SummonerChampionMastery1.setImageBitmap(summonerMasteryChamp1Icon)
        } else {
            binding.SummonerChampionMastery1.setImageBitmap(null)
        }
    }

    private fun setIconRankBorderSecondWing(summonerRankBorderSecondWing: Bitmap?) {
        if (summonerRankBorderSecondWing != null) {
            binding.rankBorderSecondWing.setImageBitmap(summonerRankBorderSecondWing)
            binding.rankIconSecondWing.setImageBitmap(summonerRankBorderSecondWing)
        } else {
            binding.rankBorderSecondWing.setImageBitmap(null)
            binding.rankIconSecondWing.setImageBitmap(null)
        }
    }

    private fun setIconRankBorderWing(summonerRankBorderWing: Bitmap?) {
        if (summonerRankBorderWing != null) {
            binding.rankBorderWing.setImageBitmap(summonerRankBorderWing)
            binding.rankIconWing.setImageBitmap(summonerRankBorderWing)
        } else {
            binding.rankBorderWing.setImageBitmap(null)
            binding.rankIconWing.setImageBitmap(null)
        }
    }

    private fun setIconRankBorderCrown(summonerRankBorderCrown: Bitmap?) {
        if (summonerRankBorderCrown != null) {
            binding.rankBorderCrown.setImageBitmap(summonerRankBorderCrown)
            binding.rankIconCrown.setImageBitmap(summonerRankBorderCrown)
        } else {
            binding.rankBorderCrown.setImageBitmap(null)
            binding.rankIconCrown.setImageBitmap(null)
        }
    }

    private fun setIconRankBorder(summonerRankBorder: Bitmap?) {
        if (summonerRankBorder != null) {
            binding.rankBorder.setImageBitmap(summonerRankBorder)
        } else {
            binding.rankBorder.setImageBitmap(null)
        }
    }

    private fun setIcon(summonerIcon: Bitmap) {
        if (summonerIcon != null) {
            binding.SummonerIcon.setImageBitmap(summonerIcon)
        } else {
            binding.SummonerIcon.setImageBitmap(null)
        }
    }

    private fun showMainInformation(summonerMainInformation: JSONObject?) {
        if (summonerMainInformation != null) {
            binding.SummonerName.text = summonerMainInformation.getString("name")
            binding.summonerLevel.text = summonerMainInformation.getInt("summonerLevel").toString()

        } else {
            binding.SummonerName.text = null
            binding.summonerLevel.text = null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        // Reset the binding
        _binding = null
    }
}