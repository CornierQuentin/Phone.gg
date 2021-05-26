package fr.cornier.phonegg.HomePage

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import de.hdodenhof.circleimageview.CircleImageView
import fr.cornier.phonegg.R
import fr.cornier.phonegg.Summoner
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.cell_summoner.view.*
import kotlinx.android.synthetic.main.fragment_add_summoner.view.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*


class SummonerAdapter(private val registeredSummonerNumber : Int, val summonerList:RealmResults<Summoner>, val parentFragment: HomeFragment) : RecyclerView.Adapter<SummonerAdapter.SummonerViewHolder>() {

    private lateinit var requestQueue: RequestQueue

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
        } else {
            holder.fillWithSummonerInformation(position)
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

        private val homeFragment = parentFragment.homeFragment
        private val addSummonerButton: Button = rootView.addSummonerButton
        private val homeSummonerDisplay: ConstraintLayout = rootView.homeSummonerDisplay
        private val homeSummonerName: TextView = rootView.homeSummonerName
        private val homeSummonerIcon: CircleImageView = rootView.homeSummonerIcon
        private val deleteIcon = rootView.deleteIcon

        fun fillWithButton() {

            /*
            *   Fill the viewHolder with the addSummonerButton and hide the summoner information
            */

            addSummonerButton.visibility = View.VISIBLE
            homeSummonerDisplay.visibility = View.INVISIBLE

            addSummonerButton.setOnClickListener { onAddSummonerButtonClick() }
        }

        private fun onAddSummonerButtonClick() {

            /*
            *   When the addSummonerButton is clicked navigate to the AddSummonerFragment
            */

            parentFragment.findNavController().navigate(R.id.action_homeFragment_to_addSummonerFragment)
        }

        @SuppressLint("ClickableViewAccessibility")
        fun fillWithSummonerInformation(position: Int) {

            addSummonerButton.visibility = View.INVISIBLE
            homeSummonerDisplay.visibility = View.VISIBLE

            requestQueue = Volley.newRequestQueue(parentFragment.requireContext())

            // Load the Api Key from the string.xml
            val apiKey = parentFragment.requireContext().getString(R.string.api_key)

            val region = summonerList[position]?.summonerRegion

            val summonerAccountId = summonerList[position]?.summonerAccountId

            // Prepare the url for the request, changing the region, the summoner account ID and the Api Key
            val summonerRequestUrl = "https://$region.api.riotgames.com/lol/summoner/v4/summoners/by-account/$summonerAccountId?api_key=$apiKey"

            // Prepare the GET request with the url and if it succeed, change summonerInformation
            // value to the JSON response and if it fail change summonerInformation to null
            val summonerRequest = JsonObjectRequest(Request.Method.GET, summonerRequestUrl, null, { summonerJSON ->

                homeSummonerName.text = summonerJSON.getString("name")

                // Get "profileIconId" value of the JSON
                val summonerIconId = summonerJSON.getInt("profileIconId")

                // Prepare the url for the request, changing the summoner icon id
                val iconUrl = "http://ddragon.leagueoflegends.com/cdn/11.10.1/img/profileicon/$summonerIconId.png"

                // Prepare the GET request with the url and if it succeed, change summonerIcon
                // value to the bitmap response and if it fail change summonerIcon to null
                val summonerIconRequest = ImageRequest(
                    iconUrl,
                    {bitmap ->
                        homeSummonerIcon.setImageBitmap(bitmap)
                    },0,0,null,null,
                    {  }
                )

                // Add the summonerIconRequest to the request Queue
                requestQueue.add(summonerIconRequest)

            }, {  })

            // Add the summonerRequest to the request Queue
            requestQueue.add(summonerRequest)

            homeSummonerDisplay.setOnClickListener { onHomeSummonerDisplayClick(position, false) }

            homeSummonerDisplay.setOnLongClickListener{
                homeSummonerIcon.visibility = View.INVISIBLE
                homeSummonerName.visibility = View.INVISIBLE
                deleteIcon.visibility = View.VISIBLE

                homeSummonerDisplay.setBackgroundResource(R.drawable.remove_summoner_display_shape)

                homeSummonerDisplay.requestFocus()

                homeFragment.setOnClickListener {
                    homeSummonerIcon.visibility = View.VISIBLE
                    homeSummonerName.visibility = View.VISIBLE
                    deleteIcon.visibility = View.INVISIBLE

                    homeSummonerDisplay.setBackgroundResource(R.drawable.summoner_display_shape)

                    homeSummonerDisplay.setOnClickListener { onHomeSummonerDisplayClick(position, false) }
                }

                homeSummonerDisplay.setOnClickListener { onHomeSummonerDisplayClick(position, true) }

                return@setOnLongClickListener true
            }
        }
    }

    private fun onHomeSummonerDisplayClick(summoner: Int, deleteSummoner:Boolean) {
        if(!deleteSummoner) {

            val direction:NavDirections =
                HomeFragmentDirections.actionHomeFragmentToSummonerInformationFragment(summonerList[summoner]?.summonerAccountId.toString())

            parentFragment.findNavController().navigate(direction)

        } else {

            val realm:Realm = Realm.getDefaultInstance()

            if (summonerList[summoner] != null) {

                realm.beginTransaction()

                summonerList[summoner]?.deleteFromRealm()

                realm.commitTransaction()
            }

            parentFragment.configureRecyclerView()

            parentFragment.summonerList.adapter?.notifyDataSetChanged()
        }
    }
}