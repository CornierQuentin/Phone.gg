package fr.cornier.phonegg.History

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import fr.cornier.phonegg.R
import fr.cornier.phonegg.Stats.StatsFragmentDirections
import fr.cornier.phonegg.SummonerInformation.SummonerInformationFragmentDirections
import fr.cornier.phonegg.databinding.FragmentHistoryBinding
import io.realm.Realm
import org.json.JSONObject

class HistoryFragment : Fragment() {

    private val args: HistoryFragmentArgs by navArgs()

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HistoryViewModel by viewModels()

    lateinit var realm: Realm

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.homeButton.setOnClickListener { findNavController().navigate(R.id.action_historyFragment_to_homeFragment) }

        binding.drawerButton.setOnClickListener { binding.drawerLayout.open() }

        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            // Handle menu item selected

            if (menuItem.itemId == 2131361818) {
                val direction: NavDirections = HistoryFragmentDirections.actionHistoryFragmentToSummonerInformationFragment(args.summonerAccountId)

                findNavController().navigate(direction)
            } else if (menuItem.itemId == 2131361817) {
                val direction: NavDirections = HistoryFragmentDirections.actionHistoryFragmentToStatsFragment(args.summonerAccountId)

                findNavController().navigate(direction)
            } else if (menuItem.itemId == 2131361803) {

                findNavController().navigate(R.id.action_historyFragment_to_homeFragment)
            }

            binding.drawerLayout.close()
            true
        }

        binding.drawerLayout.setScrimColor(ContextCompat.getColor(requireContext(), R.color.drawerShadow))
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.summonerMainInformation.observe(requireActivity()) { summonerMainInformation ->
            setSummonerMainInformation(
                summonerMainInformation
            )
        }

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