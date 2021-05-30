package fr.cornier.phonegg.AddSummonerPage

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import fr.cornier.phonegg.R
import fr.cornier.phonegg.Summoner
import fr.cornier.phonegg.databinding.FragmentAddSummonerBinding
import io.realm.Realm
import org.json.JSONObject


class AddSummonerFragment : Fragment() {

    /*
    *   In this fragment the user enter a summoner name and the fragment search for this summoner
    *   in the webservice and display either an error message either a box with the summoner
    *   information that stands for a button that redirect TODO(Prochain Fragment)
    */

    private var _binding: FragmentAddSummonerBinding? = null
    private val binding get() = _binding!!

    // Set the view model of the fragment as an AddSummonerViewModel
    private val viewModel:AddSummonerViewModel by viewModels()

    lateinit var realm:Realm

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment using binding
        _binding = FragmentAddSummonerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        realm = Realm.getDefaultInstance()

        // Start observing the summonerInformation value, when it change execute the showSummoner function
        viewModel.summonerInformation.observe(requireActivity(), { summonerInformation -> showSummoner(summonerInformation) })

        // Start observing the summonerIcon value, when it change execute the displayIcon function
        viewModel.summonerIcon.observe(requireActivity(), { summonerIcon -> displayIcon(summonerIcon) })

        // Start observing the summonerNameInput value, when it change execute the searchForSummoner function of the viewModel
        binding.summonerNameInput.editText?.doOnTextChanged { summonerName, _, _, _ ->
            if (summonerName != null && summonerName.any()) {
                viewModel.searchForSummoner(summonerName, activity)
            }
        }

        // Start observing the click on the summonerDisplay, when clicked execute the addSummoner function
        binding.summonerDisplay.setOnClickListener { addSummoner() }

        binding.backArrowButton.setOnClickListener { findNavController().navigate(R.id.action_addSummonerFragment_to_homeFragment) }
        binding.homeButton.setOnClickListener { findNavController().navigate(R.id.action_addSummonerFragment_to_homeFragment) }
    }

    private fun displayIcon(summonerIcon: Bitmap?) {
        /*
        *   args : summonerIcon as a Bitmap returned by a get request to the data dragon database
        *   with the "profileIconId" value of the JSON
        *
        *   func : Change the image of the summonerIcon imageView with a given Bitmap which can be
        *   null
        */

        binding.summonerIcon.setImageBitmap(summonerIcon)
    }

    private fun addSummoner() {
        val summonerInformation =  viewModel.summonerInformation.value

        if(summonerInformation != null) {

            val summonerId = summonerInformation.getString("id")
            val summonerAccountId = summonerInformation.getString("accountId")
            val summonerPuuid = summonerInformation.getString("puuid")

            val newSummoner = Summoner()
            newSummoner.summonerId = summonerId
            newSummoner.summonerAccountId = summonerAccountId
            newSummoner.summonerPuuid = summonerPuuid
            newSummoner.summonerRegion = viewModel.getRegion()

            val isSummonerAlreadyExist = realm.where(Summoner::class.java).equalTo("summonerAccountId", newSummoner.summonerAccountId).findFirst()

            if (isSummonerAlreadyExist == null) {

                realm.beginTransaction()

                realm.copyToRealm(newSummoner)

                realm.commitTransaction()

                binding.summonerNameInput.editText?.text?.clear()
            }

            val direction = AddSummonerFragmentDirections.actionAddSummonerFragmentToSummonerInformationFragment(newSummoner.summonerAccountId)

            findNavController().navigate(direction)
        }
    }

    private fun showSummoner(summonerInformation: JSONObject?) {

        /*
        *   args : summonerInformation as a JSON object returned by a get request to the riotgames
        *   api for the SUMMONER-V4 part
        *
        *   func :
        *   if the summonerInformation is not null it will show the summonerName text with
        *   the "name" value of the JSON and enable the clickable aspect of the box
        *
        *   if summonerInformation is null it will show an error message in the same box to the user
        *    by hiding the information container and disable the clickable aspect of the box
        */

        if (summonerInformation != null) {
            // Hiding and showing element by changing size to not change the visibility which modify position of all elements
            binding.summonerNotfoundText.textSize = 0F
            binding.summonerName.textSize = 28F

            // Change summonerName text with the "name" value of the JSON
            binding.summonerName.text = summonerInformation.getString("name")

            // The box with the summoner information is clickable
            binding.summonerDisplay.isClickable = true
        } else {
            // Hiding and showing element by changing size to not change the visibility which modify position of all elements
            binding.summonerNotfoundText.textSize = 28F
            binding.summonerName.textSize = 0F

            // The box with the error message is not clickable
            binding.summonerDisplay.isClickable = false
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Load the array from the resources
        val region = resources.getStringArray(R.array.serverRegion)

        // Configure the spinner adapter with the spinner item layout to configure the text style
        val adapter = ArrayAdapter(requireActivity(),
            R.layout.spinner_item, region)

        (binding.regionSpinnerTest.editText as? AutoCompleteTextView)?.setAdapter(adapter)

        (binding.regionSpinnerTest.editText as? AutoCompleteTextView)?.setText(adapter.getItem(2), false)

        (binding.regionSpinnerTest.editText as? AutoCompleteTextView)?.setOnItemClickListener { _, _, i, _ ->
            viewModel.setRegion(i)
            viewModel.searchForSummoner(binding.summonerNameInput.editText?.text.toString(), activity)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        // Reset the binding
        _binding = null
    }
}