package fr.cornier.phonegg.History

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import fr.cornier.phonegg.MainActivity
import fr.cornier.phonegg.R
import kotlinx.android.synthetic.main.cell_history.view.*
import org.json.JSONObject
import kotlin.math.roundToInt

class HistoryAdapter(private val matchList: JSONObject, val summonerAccountId: String, val region:String, val parentFragment: HistoryFragment): RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    private lateinit var requestQueue: RequestQueue

    private infix fun Int.fdiv(i: Int): Double = this / i.toDouble()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val rootView =
            LayoutInflater.from(parent.context).inflate(R.layout.cell_history, parent, false)

        return HistoryViewHolder(rootView)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.fillCell(position)
    }

    override fun getItemCount(): Int {
        return matchList.getJSONArray("matches").length()
    }

    inner class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun fillCell(position: Int) {

            requestQueue = Volley.newRequestQueue(parentFragment.requireContext())

            val apiKey = parentFragment.activity?.getString(R.string.api_key)

            val matchId =
                matchList.getJSONArray("matches").getJSONObject(position)
                    .getLong("gameId").toString()

            val matchUrl =
                "https://$region.api.riotgames.com/lol/match/v4/matches/$matchId?api_key=$apiKey"

            val matchRequest = JsonObjectRequest(Request.Method.GET, matchUrl, null,
                    { matchJSON ->

                        var participantId = 0

                        for (participantIdentities in 0 until matchJSON.getJSONArray(
                            "participantIdentities"
                        ).length()) {

                            if (matchJSON.getJSONArray("participantIdentities")
                                    .getJSONObject(participantIdentities)
                                    .getJSONObject("player")
                                    .getString("accountId") == summonerAccountId
                            ) {
                                participantId = participantIdentities
                            }
                        }

                        val player = matchJSON.getJSONArray("participants")
                            .getJSONObject(participantId)

                        val champUrl = "https://raw.communitydragon.org/latest/plugins/rcp-be-lol-game-data/global/default/v1/champion-icons/${player.getInt("championId")}.png"

                        val champRequest = ImageRequest(
                            champUrl,
                            {bitmap ->
                                itemView.champIcon.setImageBitmap(bitmap)
                            },0,0,null,null,
                            {  }
                        )

                        requestQueue.add(champRequest)

                        val kills = player.getJSONObject("stats").getInt("kills")
                        val deaths = player.getJSONObject("stats").getInt("deaths")
                        val assists = player.getJSONObject("stats").getInt("assists")

                        val kda: Double = if (kills == 0 && deaths == 0 && assists == 0) {
                            0.0
                        } else if (deaths == 0) {
                            (kills + assists).toDouble()
                        } else {
                            (kills + assists) fdiv deaths
                        }

                        itemView.kda.text = "%.2f KDA".format(kda)

                        when {
                            kda >= 2 && kda < 4 -> {
                                itemView.kda.setTextColor(ContextCompat.getColor(parentFragment.requireContext(), R.color.middleLowKDA))
                            }
                            kda >= 4 && kda < 10 -> {
                                itemView.kda.setTextColor(ContextCompat.getColor(parentFragment.requireContext(), R.color.middleHighKDA))
                            }
                            kda >= 10 -> {
                                itemView.kda.setTextColor(ContextCompat.getColor(parentFragment.requireContext(), R.color.highKDA))
                            }
                            else -> {
                                itemView.kda.setTextColor(ContextCompat.getColor(parentFragment.requireContext(), R.color.lowKDA))
                            }
                        }

                        itemView.detailedKDA.text = "$kills/$deaths/$assists"

                        val vision = player.getJSONObject("stats").getInt("visionScore").toDouble() / (matchJSON.getInt("gameDuration") fdiv 60)

                        itemView.visionPerMinutes.text =  "%.2f Vis/Min".format(vision)

                        itemView.win.text = if (player.getJSONObject("stats").getBoolean("win")) "Victory" else "Defeat"

                        if (player.getJSONObject("stats").getBoolean("win")) {
                            itemView.win.setTextColor(ContextCompat.getColor(parentFragment.requireContext(), R.color.highWinRate))
                        } else {
                            itemView.win.setTextColor(ContextCompat.getColor(parentFragment.requireContext(), R.color.lowWinRate))
                        }

                        val csPerMinutes = (player.getJSONObject("stats").getInt("totalMinionsKilled") + player.getJSONObject("stats").getInt("neutralMinionsKilled")).toDouble() / (matchJSON.getInt("gameDuration") fdiv 60)

                        itemView.csPerMinutes.text = "%.1f CS/Min".format(csPerMinutes)

                        itemView.CS.text = (player.getJSONObject("stats").getInt("totalMinionsKilled") + player.getJSONObject("stats").getInt("neutralMinionsKilled")).toString() + " CS"

                        val dmgPerMinutes = player.getJSONObject("stats").getInt("totalDamageDealtToChampions").toDouble() / (matchJSON.getInt("gameDuration") fdiv 60)

                        itemView.DmgPerMinutes.text = "${dmgPerMinutes.roundToInt()} DMG/Min"

                        var totalDmg = 0
                        var totalKills = 0

                        for (participant in 0 until matchJSON.getJSONArray("participants").length()) {
                            if (matchJSON.getJSONArray("participants").getJSONObject(participant).getInt("teamId") == player.getInt("teamId")) {
                                totalDmg += matchJSON.getJSONArray("participants")
                                    .getJSONObject(participant).getJSONObject("stats")
                                    .getInt("totalDamageDealtToChampions")
                                totalKills += matchJSON.getJSONArray("participants")
                                    .getJSONObject(participant).getJSONObject("stats")
                                    .getInt("kills")
                            }
                        }

                        itemView.killParticipation.text = (((kills + assists) fdiv totalKills) * 100).roundToInt().toString() + "% KP"
                        itemView.percentTeamDmg.text = ((player.getJSONObject("stats").getInt("totalDamageDealtToChampions") fdiv totalDmg) * 100).roundToInt().toString() + "% of team"

                        if (matchList.getJSONArray("matches").getJSONObject(position).getString("lane") == "NONE") {
                            val roleURL = "https://raw.communitydragon.org/latest/plugins/rcp-fe-lol-clash/global/default/assets/images/position-selector/positions/icon-position-none-disabled.png"

                            val roleRequest = ImageRequest(
                                roleURL,
                                {bitmap ->
                                    itemView.roleIcon.setImageBitmap(bitmap)
                                    (parentFragment.activity as MainActivity).setNavStatus(true)
                                },0,0,null,null,
                                {  }
                            )

                            requestQueue.add(roleRequest)
                        } else if (matchList.getJSONArray("matches").getJSONObject(position).getString("lane") == "TOP") {
                            val roleURL = "https://raw.communitydragon.org/latest/plugins/rcp-fe-lol-clash/global/default/assets/images/position-selector/positions/icon-position-top-disabled.png"

                            val roleRequest = ImageRequest(
                                roleURL,
                                {bitmap ->
                                    itemView.roleIcon.setImageBitmap(bitmap)
                                    (parentFragment.activity as MainActivity).setNavStatus(true)
                                },0,0,null,null,
                                {  }
                            )

                            requestQueue.add(roleRequest)
                        } else if (matchList.getJSONArray("matches").getJSONObject(position).getString("lane") == "JUNGLE") {
                            val roleURL = "https://raw.communitydragon.org/latest/plugins/rcp-fe-lol-clash/global/default/assets/images/position-selector/positions/icon-position-jungle-disabled.png"

                            val roleRequest = ImageRequest(
                                roleURL,
                                {bitmap ->
                                    itemView.roleIcon.setImageBitmap(bitmap)
                                    (parentFragment.activity as MainActivity).setNavStatus(true)
                                },0,0,null,null,
                                {  }
                            )

                            requestQueue.add(roleRequest)
                        } else if (matchList.getJSONArray("matches").getJSONObject(position).getString("lane") == "MID") {
                            val roleURL = "https://raw.communitydragon.org/latest/plugins/rcp-fe-lol-clash/global/default/assets/images/position-selector/positions/icon-position-middle-disabled.png"

                            val roleRequest = ImageRequest(
                                roleURL,
                                {bitmap ->
                                    itemView.roleIcon.setImageBitmap(bitmap)
                                    (parentFragment.activity as MainActivity).setNavStatus(true)
                                },0,0,null,null,
                                {  }
                            )

                            requestQueue.add(roleRequest)
                        } else if (matchList.getJSONArray("matches").getJSONObject(position).getString("lane") == "BOTTOM") {
                            if (matchList.getJSONArray("matches").getJSONObject(position).getString("role") == "DUO_CARRY") {
                                val roleURL =
                                    "https://raw.communitydragon.org/latest/plugins/rcp-fe-lol-clash/global/default/assets/images/position-selector/positions/icon-position-bottom-disabled.png"

                                val roleRequest = ImageRequest(
                                    roleURL,
                                    { bitmap ->
                                        itemView.roleIcon.setImageBitmap(bitmap)
                                        (parentFragment.activity as MainActivity).setNavStatus(true)
                                    }, 0, 0, null, null,
                                    { }
                                )

                                requestQueue.add(roleRequest)
                            } else {
                                val roleURL =
                                    "https://raw.communitydragon.org/latest/plugins/rcp-fe-lol-clash/global/default/assets/images/position-selector/positions/icon-position-none-disabled.png"

                                val roleRequest = ImageRequest(
                                    roleURL,
                                    { bitmap ->
                                        itemView.roleIcon.setImageBitmap(bitmap)
                                        (parentFragment.activity as MainActivity).setNavStatus(true)
                                    }, 0, 0, null, null,
                                    { }
                                )

                                requestQueue.add(roleRequest)
                            }
                        }
                    }, {})

            (parentFragment.activity as MainActivity).setNavStatus(false)
            requestQueue.add(matchRequest)
        }
    }
}