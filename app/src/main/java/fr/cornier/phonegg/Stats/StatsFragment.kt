package fr.cornier.phonegg.Stats

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import fr.cornier.phonegg.History.HistoryFragmentArgs
import fr.cornier.phonegg.History.HistoryFragmentDirections
import fr.cornier.phonegg.History.HistoryViewModel
import fr.cornier.phonegg.R
import fr.cornier.phonegg.StatsViewModel.StatsViewModel
import fr.cornier.phonegg.databinding.FragmentHistoryBinding
import fr.cornier.phonegg.databinding.FragmentStatsBinding
import io.realm.Realm
import org.json.JSONObject

class StatsFragment : Fragment() {

    private val args: StatsFragmentArgs by navArgs()

    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: StatsViewModel by viewModels()

    lateinit var realm: Realm

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentStatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.homeButton.setOnClickListener { findNavController().navigate(R.id.action_statsFragment_to_homeFragment) }

        binding.drawerButton.setOnClickListener { binding.drawerLayout.open() }

        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            // Handle menu item selected

            if (menuItem.itemId == 2131361818) {
                val direction: NavDirections = StatsFragmentDirections.actionStatsFragmentToHistoryFragment(args.summonerAccountId)

                findNavController().navigate(direction)
            } else if (menuItem.itemId == 2131361802) {
                val direction: NavDirections = StatsFragmentDirections.actionStatsFragmentToSummonerInformationFragment(args.summonerAccountId)

                findNavController().navigate(direction)
            } else if (menuItem.itemId == 2131361803) {

                findNavController().navigate(R.id.action_statsFragment_to_homeFragment)
            }

            binding.drawerLayout.close()
            true
        }

        binding.drawerLayout.setScrimColor(ContextCompat.getColor(requireContext(), R.color.drawerShadow))
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.summonerMainInformation.observe(requireActivity()) { summonerMainInformation -> setSummonerMainInformation(summonerMainInformation) }

        viewModel.getSummonerMainInformation(args.summonerAccountId, activity)
    }

    private fun setSummonerMainInformation(summonerInformation: JSONObject?) {
        if (summonerInformation != null) {
            binding.summonerNameText.text = summonerInformation.getString("name")
        } else {
            binding.summonerNameText.text = null
        }
    }
}