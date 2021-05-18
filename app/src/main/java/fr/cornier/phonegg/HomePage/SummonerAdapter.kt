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

        /*
        *   Set the default view holder
        */

        val rootView =
            LayoutInflater.from(parent.context).inflate(R.layout.cell_summoner, parent, false)

        return SummonerViewHolder(rootView)
    }

    override fun onBindViewHolder(holder: SummonerViewHolder, position: Int) {

        /*
        *   Fill the view in terms of summoner information or with the addSummonerButton that
        *   that redirect to the AddSummonerFragment
        */

        if (position == registeredSummonerNumber) {
            holder.fillWithButton()
        }
    }

    override fun getItemCount(): Int {

        /*
        *   Set the number of view in the recyclerView at the number of registered summoner + 1 view
        *   for the view with the addSummonerButton
        */

        return registeredSummonerNumber + 1
    }

    inner class SummonerViewHolder(rootView: View) : RecyclerView.ViewHolder(rootView) {

        /*
        *   Configure the view holder to match the desired view
        */

        private var addSummonerButton = rootView.findViewById<View>(R.id.addSummonerButton)

        fun fillWithButton() {

            /*
            *   Fill the viewHolder with the addSummonerButton
            */

            addSummonerButton.setOnClickListener { onAddSummonerButtonClick() }
        }

        private fun onAddSummonerButtonClick() {

            /*
            *   When the addSummonerButton is clicked navigate to the AddSummonerFragment
            */

            parentFragment.findNavController().navigate(R.id.action_homeFragment_to_addSummonerFragment)
        }
    }
}