package fr.cornier.phonegg.History

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.toolbox.Volley
import fr.cornier.phonegg.HomePage.SummonerAdapter
import fr.cornier.phonegg.R
import fr.cornier.phonegg.Stats.StatsFragmentDirections
import fr.cornier.phonegg.Summoner
import fr.cornier.phonegg.SummonerInformation.SummonerInformationFragmentDirections
import fr.cornier.phonegg.databinding.FragmentHistoryBinding
import io.realm.Realm
import org.json.JSONObject

class HistoryFragment : Fragment() {

    val args: HistoryFragmentArgs by navArgs()

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HistoryViewModel by viewModels()

    lateinit var realm: Realm

    private var navigationEnable = true

    private var drawerOpen = false

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

        realm = Realm.getDefaultInstance()

        binding.homeButton.setOnClickListener { if (navigationEnable) findNavController().navigate(R.id.action_historyFragment_to_homeFragment) }

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
                    val direction: NavDirections = HistoryFragmentDirections.actionHistoryFragmentToSummonerInformationFragment(args.summonerAccountId)

                    findNavController().navigate(direction)
                }
                "Stats" -> {
                    val direction: NavDirections = HistoryFragmentDirections.actionHistoryFragmentToStatsFragment(args.summonerAccountId)

                    findNavController().navigate(direction)
                }
                "Home" -> {

                    findNavController().navigate(R.id.action_historyFragment_to_homeFragment)
                }
            }

            binding.drawerLayout.close()
            true
        }

        binding.drawerLayout.setScrimColor(ContextCompat.getColor(requireContext(), R.color.drawerShadow))
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.matchList.observe(requireActivity()) { matchList ->
            configureRecyclerView(
                matchList
            )
        }

        viewModel.getSummonerMainInformation(args.summonerAccountId, activity)
    }

    private fun configureRecyclerView(matchList: JSONObject?) {
        if (matchList != null) {

            val summoner = realm.where(Summoner::class.java).equalTo("summonerAccountId", args.summonerAccountId).findFirst()

            val region = summoner!!.summonerRegion

            val historyAdapter = HistoryAdapter(matchList, args.summonerAccountId, region, this)

            binding.history.adapter = historyAdapter
            binding.history.layoutManager = LinearLayoutManager(activity)
        }
    }
}