package fr.cornier.phonegg.HomePage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import fr.cornier.phonegg.Summoner
import fr.cornier.phonegg.databinding.FragmentHomeBinding
import io.realm.Realm

class HomeFragment : Fragment() {

    /*
    *   This fragment represent the home page of the app and
    *   In this fragment the user choose which summoner he want or choose to add one summoner
    *   with a recyclerView that display all the summoner from the TODO Base de Donnée
    *   in a box that stands for a button and at the end of the list it display the view with the
    *   button that redirect to the AddSummonerFragment
    */

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    lateinit var realm:Realm

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment using binding
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        configureRecyclerView()
    }

    fun configureRecyclerView() {
        realm = Realm.getDefaultInstance()

        val summonerList = realm.where(Summoner::class.java).findAll()

        // Configure and initialise the recyclerView which display the available summoners and
        // the button to add a new summoner

        val summonerAdapter = SummonerAdapter(summonerList.size, summonerList, this)

        binding.summonerList.adapter = summonerAdapter
        binding.summonerList.layoutManager = LinearLayoutManager(activity)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        // Reset the binding
        _binding = null
    }
}