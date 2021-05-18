package fr.cornier.phonegg.AddSummonerPage

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import fr.cornier.phonegg.R
import fr.cornier.phonegg.databinding.FragmentAddSummonerBinding
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

        // Set the focus on the text input and show the keyboard
        binding.summonerNameInput.requestFocus()
        binding.summonerNameInput.showSoftKeyboard()

        // Start observing the summonerInformation value, when it change execute the showSummoner function
        viewModel.summonerInformation.observe(requireActivity(), { summonerInformation -> showSummoner(summonerInformation) })

        // Start observing the summonerNameInput value, when it change execute the searchForSummoner function of the viewModel
        binding.summonerNameInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {  }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {  }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null && s.any()) {
                    viewModel.searchForSummoner(s, activity)
                }
            }
        })

        // Start observing the click on the summonerDisplay, when clicked execute the addSummoner function
        binding.summonerDisplay.setOnClickListener { addSummoner() }
    }

    private fun addSummoner() {
        Log.i("Test", "Vas dormir")
        TODO("Save and Load new Summoner")
    }

    private fun showSummoner(summonerInformation: JSONObject?) {

        /*
        *   args : summonerInformation as a JSON object returned by a get request to the riotgames
        *   api for the SUMMONER-V4 part
        *
        *   func :
        *   if the summonerInformation is not null it will show the summonerName text with
        *   the "name" value of the JSON, TODO Complete with the other information showed
        *   and enable the clickable aspect of the box
        *
        *   if summonerInformation is null it will show an error message in the same box to the user
        *    by hiding the information container and disable the clickable aspect of the box
        */

        if (summonerInformation != null) {
            // Hiding and showing element by changing size to not change the visibility which modify position of all elements
            binding.summonerNotfoundText.textSize = 0F
            binding.summonerName.textSize = 24F

            // Change summonerName text with the "name" value of the JSON
            binding.summonerName.text = summonerInformation.getString("name")
        } else {
            // Hiding and showing element by changing size to not change the visibility which modify position of all elements
            binding.summonerNotfoundText.textSize = 28F
            binding.summonerName.textSize = 0F
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Load the array from the resources
        val region = resources.getStringArray(R.array.serverRegion)

        // Load the spinner from the binding
        val regionSpinner = binding.regionSpinner

        // Configure the spinner adapter with the spinner item layout to configure the text style
        val adapter = ArrayAdapter(requireActivity(),
            R.layout.spinner_item, region)

        // Assign the spinner adapter
        regionSpinner.adapter = adapter

        // Set the first value of the spinner when the activity is created
        // TODO Set in terms of the region of the phone
        regionSpinner.setSelection(2)

        // Start observing the regionSpinner item, when it change execute the setRegion function and
        // the searchForSummoner function of the viewModel
        regionSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {  }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                viewModel.setRegion(position)
                viewModel.searchForSummoner(binding.summonerNameInput.text, activity)
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        // Reset the binding
        _binding = null
    }
}

// extension function to open soft keyboard programmatically
fun EditText.showSoftKeyboard(){
    (this.context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
        .showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}