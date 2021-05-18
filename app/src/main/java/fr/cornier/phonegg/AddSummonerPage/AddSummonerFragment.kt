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

    private var _binding: FragmentAddSummonerBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val viewModel:AddSummonerViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAddSummonerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.summonerNameInput.requestFocus()
        binding.summonerNameInput.showSoftKeyboard()

        viewModel.summonerInformation.observe(requireActivity(), { summonerInformation -> showSummoner(summonerInformation) })

        binding.summonerNameInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {  }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {  }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null && s.any()) {
                    viewModel.searchForSummoner(s, activity)
                }
            }
        })

        binding.summonerDisplay.setOnClickListener { addSummoner() }
    }

    private fun addSummoner() {
        Log.i("Test", "Vas dormir")
        TODO("Save and Load new Summoner")
    }

    private fun showSummoner(summonerInformation: JSONObject?) {
        if (summonerInformation != null) {
            binding.summonerNotfoundText.textSize = 0F
            binding.summonerName.textSize = 24F

            binding.summonerName.text = summonerInformation.getString("name")
        } else {
            binding.summonerNotfoundText.textSize = 28F
            binding.summonerName.textSize = 0F
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val region = resources.getStringArray(R.array.serverRegion)

        val regionSpinner = binding.regionSpinner
        val adapter = ArrayAdapter(requireActivity(),
            R.layout.spinner_item, region)
        regionSpinner.adapter = adapter

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
        _binding = null
    }
}

// extension function to open soft keyboard programmatically
fun EditText.showSoftKeyboard(){
    (this.context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
        .showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}