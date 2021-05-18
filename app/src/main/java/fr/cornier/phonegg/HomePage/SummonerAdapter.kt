package fr.cornier.phonegg.HomePage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import fr.cornier.phonegg.R

class SummonerAdapter(registeredSummonerNumber : Int, val parentFragment: Fragment) : RecyclerView.Adapter<SummonerAdapter.SummonerViewHolder>() {

    private val registeredSummonerNumber = registeredSummonerNumber

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SummonerViewHolder {
        val rootView =
            LayoutInflater.from(parent.context).inflate(R.layout.cell_summoner, parent, false)

        return SummonerViewHolder(rootView)
    }

    override fun onBindViewHolder(holder: SummonerViewHolder, position: Int) {
        if (position == registeredSummonerNumber) {
            holder.fillWithButton()
        }
    }

    override fun getItemCount(): Int {
        return registeredSummonerNumber + 1
    }

    inner class SummonerViewHolder(rootView: View) : RecyclerView.ViewHolder(rootView) {

        private var addSummonerButton = rootView.findViewById<View>(R.id.addSummonerButton)

        fun fillWithButton() {
            addSummonerButton.setOnClickListener { onAddSummonerButtonClick() }
        }

        private fun onAddSummonerButtonClick() {
            parentFragment.findNavController().navigate(R.id.action_homeFragment_to_addSummonerFragment)
        }
    }
}