package fr.cornier.phonegg.SummonerInformation

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import fr.cornier.phonegg.AddSummonerPage.AddSummonerFragment
import fr.cornier.phonegg.AddSummonerPage.AddSummonerFragmentDirections
import fr.cornier.phonegg.HomePage.HomeFragmentDirections
import fr.cornier.phonegg.MainActivity
import fr.cornier.phonegg.R
import fr.cornier.phonegg.Stats.StatsFragmentDirections
import fr.cornier.phonegg.databinding.FragmentSummonerInformationBinding
import io.realm.Realm
import org.json.JSONObject
import java.text.NumberFormat

class SummonerInformationFragment : Fragment() {

    private val args: SummonerInformationFragmentArgs by navArgs()

    private var _binding: FragmentSummonerInformationBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SummonerInformationViewModel by viewModels()

    lateinit var realm: Realm

    private var navigationEnable = false

    private var drawerOpen = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSummonerInformationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).setNavStatus(false)

        binding.homeButton.setOnClickListener { if (navigationEnable) findNavController().navigate(R.id.action_summonerInformationFragment_to_homeFragment) }

        binding.drawerButton.setOnClickListener {
            if (navigationEnable) {
                drawerOpen = true
                binding.drawerLayout.open()
                requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        if (drawerOpen) {
                            binding.drawerLayout.close()
                            drawerOpen = false
                        } else {
                            isEnabled = false
                            activity?.onBackPressed()
                        }
                    }
                })
            }
        }

        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            // Handle menu item selected

            when (menuItem.title) {
                "History" -> {
                    val direction: NavDirections = SummonerInformationFragmentDirections.actionSummonerInformationFragmentToHistoryFragment(args.summonerAccountId)

                    findNavController().navigate(direction)
                }
                "Stats" -> {
                    val direction: NavDirections = SummonerInformationFragmentDirections.actionSummonerInformationFragmentToStatsFragment(args.summonerAccountId)

                    findNavController().navigate(direction)
                }
                "Home" -> {

                    findNavController().navigate(R.id.action_summonerInformationFragment_to_homeFragment)
                }
            }

            binding.drawerLayout.close()
            true
        }

        binding.drawerLayout.setScrimColor(ContextCompat.getColor(requireContext(), R.color.drawerShadow))

        binding.SummonerHistoryDisplay.setOnClickListener {
            if (navigationEnable) {
                val direction: NavDirections =
                    SummonerInformationFragmentDirections.actionSummonerInformationFragmentToHistoryFragment(
                        args.summonerAccountId
                    )

                findNavController().navigate(direction)
            }
        }

        binding.SummonerMasteryDisplay.setOnClickListener {
            if (navigationEnable) {
                val direction: NavDirections =
                    SummonerInformationFragmentDirections.actionSummonerInformationFragmentToStatsFragment(
                        args.summonerAccountId
                    )

                findNavController().navigate(direction)
            }
        }
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
        viewModel.numberLastGames.observe(requireActivity(), { numberLastGames -> setNumberLastGames(numberLastGames) })
        viewModel.winLoseLastGames.observe(requireActivity(), { winLoseLastGames -> setWinLoseLastGames(winLoseLastGames) })
        viewModel.firstChamp.observe(requireActivity(), {firstChamp -> setfirstChamp(firstChamp) })
        viewModel.firstChampWinLose.observe(requireActivity(), {firstChampWinLose -> setfirstChampWinLose(firstChampWinLose) })
        viewModel.firstChampWinrate.observe(requireActivity(), {firstChampWinrate -> setfirstChampWinrate(firstChampWinrate) })
        viewModel.firstChampKDA.observe(requireActivity(), {firstChampKDA -> setfirstChampKDA(firstChampKDA) })
        viewModel.secondChamp.observe(requireActivity(), {secondChamp -> setsecondChamp(secondChamp) })
        viewModel.secondChampWinLose.observe(requireActivity(), {secondChampWinLose -> setsecondChampWinLose(secondChampWinLose) })
        viewModel.secondChampWinrate.observe(requireActivity(), {secondChampWinrate -> setssecondChampWinrate(secondChampWinrate) })
        viewModel.secondChampKDA.observe(requireActivity(), {secondChampKDA -> setsecondChampKDA(secondChampKDA) })
        viewModel.thirdChamp.observe(requireActivity(), {thirdChamp -> setthirdChamp(thirdChamp) })
        viewModel.thirdChampWinLose.observe(requireActivity(), {thirdChampWinLose -> setthirdChampWinLose(thirdChampWinLose) })
        viewModel.thirdChampWinrate.observe(requireActivity(), {thirdChampWinrate -> setthirdChampWinrate(thirdChampWinrate) })
        viewModel.thirdChampKDA.observe(requireActivity(), {thirdChampKDA -> setthirdChampKDA(thirdChampKDA) })
        viewModel.firstChampWinrateColor.observe(requireActivity(), { firstChampWinrateColor -> setfirstChampWinrateColor(firstChampWinrateColor) })
        viewModel.firstChampKDAColor.observe(requireActivity(), { firstChampKDAColor -> setfirstChampKDAColor(firstChampKDAColor) })
        viewModel.secondChampWinrateColor.observe(requireActivity(), { secondChampWinrateColor -> setsecondChampWinrateColor(secondChampWinrateColor) })
        viewModel.secondChampKDAColor.observe(requireActivity(), { secondChampKDAColor -> setsecondChampKDAColor(secondChampKDAColor) })
        viewModel.thirdChampWinrateColor.observe(requireActivity(), { thirdChampWinrateColor -> setthirdChampWinrateColor(thirdChampWinrateColor) })
        viewModel.thirdChampKDAColor.observe(requireActivity(), { thirdChampKDAColor -> setthirdChampKDAColor(thirdChampKDAColor) })

        viewModel.unranked.observe(requireActivity(), { unranked -> setUnrankedTextVisibility(unranked) })
        viewModel.noMasteries.observe(requireActivity(), { noMasteries -> setNoMasteriesTextVisibility(noMasteries)})
        viewModel.noLastGame.observe(requireActivity(), { noLastGame -> setNoLastGame(noLastGame) })

        viewModel.getSummonerMainInformation(args.summonerAccountId, activity)
    }

    private fun setNoLastGame(noLastGame: Boolean?) {
        if (noLastGame != null) {
            if (noLastGame) {
                binding.noLastGameText.visibility = View.VISIBLE
            } else {
                binding.noLastGameText.visibility = View.INVISIBLE
            }
        }
    }

    private fun setthirdChampKDAColor(thirdChampKDAColor: Int?) {
        if (thirdChampKDAColor != null) {
            binding.ThirdKDA.setTextColor(ContextCompat.getColor(requireContext(), thirdChampKDAColor))
        } else {
            binding.ThirdKDA.setTextColor(null)
        }
    }

    private fun setsecondChampKDAColor(secondChampKDAColor: Int?) {
        if (secondChampKDAColor != null) {
            binding.SecondKDA.setTextColor(ContextCompat.getColor(requireContext(), secondChampKDAColor))
        } else {
            binding.SecondKDA.setTextColor(null)
        }
    }

    private fun setfirstChampKDAColor(firstChampKDAColor: Int?) {
        if (firstChampKDAColor != null) {
            binding.FirstKDA.setTextColor(ContextCompat.getColor(requireContext(), firstChampKDAColor))
        } else {
            binding.FirstKDA.setTextColor(null)
        }
    }

    private fun setthirdChampWinrateColor(thirdChampWinrateColor: Int?) {
        if (thirdChampWinrateColor != null) {
            binding.ThirdWinrate.setTextColor(ContextCompat.getColor(requireContext(), thirdChampWinrateColor))
        } else {
            binding.ThirdWinrate.setTextColor(null)
        }
    }

    private fun setsecondChampWinrateColor(secondChampWinrateColor: Int?) {
        if (secondChampWinrateColor != null) {
            binding.SecondWinrate.setTextColor(ContextCompat.getColor(requireContext(), secondChampWinrateColor))
        } else {
            binding.SecondWinrate.setTextColor(null)
        }
    }

    private fun setfirstChampWinrateColor(firstChampWinrateColor: Int?) {
        if (firstChampWinrateColor != null) {
            binding.FirstWinrate.setTextColor(ContextCompat.getColor(requireContext(), firstChampWinrateColor))
        } else {
            binding.FirstWinrate.setTextColor(null)
        }
    }

    private fun setthirdChampKDA(thirdChampKDA: String?) {
        if (thirdChampKDA != null) {
            binding.ThirdKDA.text = thirdChampKDA
        } else {
            binding.ThirdKDA.text = null
        }
    }

    private fun setthirdChampWinrate(thirdChampWinrate: String?) {
        if (thirdChampWinrate != null) {
            binding.ThirdWinrate.text = thirdChampWinrate
        } else {
            binding.ThirdWinrate.text = null
        }
    }

    private fun setthirdChampWinLose(thirdChampWinLose: String?) {
        if (thirdChampWinLose != null) {
            binding.ThirdWinLose.text = thirdChampWinLose
        } else {
            binding.ThirdWinLose.text = null
        }
    }

    private fun setsecondChampKDA(secondChampKDA: String?) {
        if (secondChampKDA != null) {
            binding.SecondKDA.text = secondChampKDA
        } else {
            binding.SecondKDA.text = null
        }
    }

    private fun setssecondChampWinrate(secondChampWinrate: String?) {
        if (secondChampWinrate != null) {
            binding.SecondWinrate.text = secondChampWinrate
        } else {
            binding.SecondWinrate.text = null
        }
    }

    private fun setsecondChampWinLose(secondChampWinLose: String?) {
        if (secondChampWinLose != null) {
            binding.SecondWinLose.text = secondChampWinLose
        } else {
            binding.SecondWinLose.text = null
        }
    }

    private fun setfirstChampKDA(firstChampKDA: String?) {
        if (firstChampKDA != null) {
            binding.FirstKDA.text = firstChampKDA
        } else {
            binding.FirstKDA.text = null
        }
    }

    private fun setfirstChampWinrate(firstChampWinrate: String?) {
        if (firstChampWinrate != null) {
            binding.FirstWinrate.text = firstChampWinrate
        } else {
            binding.FirstWinrate.text = null
        }
    }

    private fun setfirstChampWinLose(firstChampWinLose: String?) {
        if (firstChampWinLose != null) {
            binding.FirstWinLose.text = firstChampWinLose
        } else {
            binding.FirstWinLose.text = null
        }
    }

    private fun setthirdChamp(thirdChamp: Bitmap?) {
        if (thirdChamp != null) {
            binding.ThirdChamp.setImageBitmap(thirdChamp)

            enableNavigation()
        } else {
            binding.ThirdChamp.setImageBitmap(null)
        }
    }

    private fun enableNavigation() {
        navigationEnable = true
        (activity as MainActivity).setNavStatus(true)
    }

    private fun setsecondChamp(secondChamp: Bitmap?) {
        if (secondChamp != null) {
            binding.SecondChamp.setImageBitmap(secondChamp)
        } else {
            binding.SecondChamp.setImageBitmap(null)
        }
    }

    private fun setfirstChamp(firstChamp: Bitmap?) {
        if (firstChamp != null) {
            binding.FirstChamp.setImageBitmap(firstChamp)
        } else {
            binding.FirstChamp.setImageBitmap(null)
        }
    }

    private fun setWinLoseLastGames(winLoseLastGames: String?) {
        if (winLoseLastGames != null) {
            binding.WinLoseLastGame.text = winLoseLastGames
        } else {
            binding.WinLoseLastGame.text = null
        }
    }

    private fun setNumberLastGames(numberLastGames: String?) {
        if (numberLastGames != null) {
            binding.LastGameNumber.text = numberLastGames
        } else {
            binding.LastGameNumber.text = null
        }
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
        binding.SummonerIcon.setImageBitmap(summonerIcon)
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