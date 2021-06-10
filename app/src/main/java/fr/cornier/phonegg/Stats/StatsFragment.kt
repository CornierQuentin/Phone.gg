package fr.cornier.phonegg.Stats

import android.os.Bundle
import android.util.Log
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
import androidx.recyclerview.widget.LinearLayoutManager
import fr.cornier.phonegg.History.HistoryAdapter
import fr.cornier.phonegg.History.HistoryFragmentArgs
import fr.cornier.phonegg.History.HistoryFragmentDirections
import fr.cornier.phonegg.History.HistoryViewModel
import fr.cornier.phonegg.R
import fr.cornier.phonegg.StatsViewModel.StatsViewModel
import fr.cornier.phonegg.Summoner
import fr.cornier.phonegg.SummonerInformation.SummonerInformationFragmentDirections
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

    private var navigationEnable = true

    private var drawerOpen = false

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

        binding.homeButton.setOnClickListener { if (navigationEnable) findNavController().navigate(R.id.action_statsFragment_to_homeFragment) }

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
                "Summoner" -> {
                    val direction: NavDirections = StatsFragmentDirections.actionStatsFragmentToSummonerInformationFragment(args.summonerAccountId)

                    findNavController().navigate(direction)
                }
                "History" -> {
                    val direction: NavDirections = StatsFragmentDirections.actionStatsFragmentToHistoryFragment(args.summonerAccountId)

                    findNavController().navigate(direction)
                }
                "Home" -> {

                    findNavController().navigate(R.id.action_statsFragment_to_homeFragment)
                }
            }

            binding.drawerLayout.close()
            true
        }

        binding.drawerLayout.setScrimColor(ContextCompat.getColor(requireContext(), R.color.drawerShadow))
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.getSummonerMainInformation(args.summonerAccountId, activity)
    }
}