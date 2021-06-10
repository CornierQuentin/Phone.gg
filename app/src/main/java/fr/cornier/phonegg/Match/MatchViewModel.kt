package fr.cornier.phonegg.Match

import android.graphics.Bitmap
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.*
import fr.cornier.phonegg.R
import fr.cornier.phonegg.Summoner
import io.realm.Realm
import org.json.JSONObject
import java.text.NumberFormat
import java.util.*
import kotlin.math.roundToInt

class MatchViewModel : ViewModel() {

    private infix fun Int.fdiv(i: Int): Double = this / i.toDouble()

    private lateinit var requestQueue: RequestQueue

    private val realm = Realm.getDefaultInstance()

    fun getMatchInformation(summonerAccountId:String, matchId:Int, activityContext: FragmentActivity?, fragment : MatchFragment) {
        val summoner = realm.where(Summoner::class.java).equalTo("summonerAccountId", summonerAccountId).findFirst()

        requestQueue = Volley.newRequestQueue(activityContext)

        val region = summoner?.summonerRegion

        val summonerAccountId = summoner?.summonerAccountId

        // Load the Api Key from the string.xml
        val apiKey = activityContext?.getString(R.string.api_key)

        // Prepare the url for the request, changing the region, the summoner name and the Api Key
        val summonerRequestUrl = "https://$region.api.riotgames.com/lol/summoner/v4/summoners/by-account/$summonerAccountId?api_key=$apiKey"
        val matchListRequestUrl = "https://$region.api.riotgames.com/lol/match/v4/matchlists/by-account/$summonerAccountId?api_key=$apiKey"

        if (summoner != null) {
            val summonerRequest =
                JsonObjectRequest(Request.Method.GET, summonerRequestUrl, null, { summonerJSON ->
                    summonerMainInformation.value = summonerJSON

                    val summonerIconUrl = "https://ddragon.leagueoflegends.com/cdn/11.10.1/img/profileicon/${summonerJSON.getInt("profileIconId")}.png"

                    val summonerIcon = ImageRequest(
                        summonerIconUrl,
                        {bitmap ->
                            summonerIcon.value = bitmap
                        },0,0,null,null,
                        {})

                    requestQueue.add(summonerIcon)
                }, {})

            requestQueue.add(summonerRequest)

            val matchListRequest =
                JsonObjectRequest(Request.Method.GET, matchListRequestUrl, null, { matchListJSON ->

                    if (matchListJSON.getJSONArray("matches").getJSONObject(matchId).getString("lane") == "NONE") {
                        val roleURL = "https://raw.communitydragon.org/latest/plugins/rcp-fe-lol-clash/global/default/assets/images/position-selector/positions/icon-position-none-disabled.png"

                        val roleRequest = ImageRequest(
                            roleURL,
                            {bitmap ->
                                roleIcon.value = bitmap
                            },0,0,null,null,
                            {  }
                        )

                        requestQueue.add(roleRequest)
                    } else if (matchListJSON.getJSONArray("matches").getJSONObject(matchId).getString("lane") == "TOP") {
                        val roleURL = "https://raw.communitydragon.org/latest/plugins/rcp-fe-lol-clash/global/default/assets/images/position-selector/positions/icon-position-top-disabled.png"

                        val roleRequest = ImageRequest(
                            roleURL,
                            {bitmap ->
                                roleIcon.value = bitmap
                            },0,0,null,null,
                            {  }
                        )

                        requestQueue.add(roleRequest)
                    } else if (matchListJSON.getJSONArray("matches").getJSONObject(matchId).getString("lane") == "JUNGLE") {
                        val roleURL = "https://raw.communitydragon.org/latest/plugins/rcp-fe-lol-clash/global/default/assets/images/position-selector/positions/icon-position-jungle-disabled.png"

                        val roleRequest = ImageRequest(
                            roleURL,
                            {bitmap ->
                                roleIcon.value = bitmap
                            },0,0,null,null,
                            {  }
                        )

                        requestQueue.add(roleRequest)
                    } else if (matchListJSON.getJSONArray("matches").getJSONObject(matchId).getString("lane") == "MID") {
                        val roleURL = "https://raw.communitydragon.org/latest/plugins/rcp-fe-lol-clash/global/default/assets/images/position-selector/positions/icon-position-middle-disabled.png"

                        val roleRequest = ImageRequest(
                            roleURL,
                            {bitmap ->
                                roleIcon.value = bitmap
                            },0,0,null,null,
                            {  }
                        )

                        requestQueue.add(roleRequest)
                    } else if (matchListJSON.getJSONArray("matches").getJSONObject(matchId).getString("lane") == "BOTTOM") {
                        if (matchListJSON.getJSONArray("matches").getJSONObject(matchId).getString("role") == "DUO_CARRY") {
                            val roleURL =
                                "https://raw.communitydragon.org/latest/plugins/rcp-fe-lol-clash/global/default/assets/images/position-selector/positions/icon-position-bottom-disabled.png"

                            val roleRequest = ImageRequest(
                                roleURL,
                                { bitmap ->
                                    roleIcon.value = bitmap
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
                                    roleIcon.value = bitmap
                                }, 0, 0, null, null,
                                { }
                            )

                            requestQueue.add(roleRequest)
                        }
                    }

                    val gameId =
                        matchListJSON.getJSONArray("matches").getJSONObject(matchId)
                            .getLong("gameId")

                    val matchUrl =
                        "https://$region.api.riotgames.com/lol/match/v4/matches/$gameId?api_key=$apiKey"

                    val matchRequest =
                        JsonObjectRequest(Request.Method.GET, matchUrl, null, { matchJSON ->

                            var matchMainInfo = ""

                            val queueUrl =
                                "https://static.developer.riotgames.com/docs/lol/queues.json"

                            val queueRequest =
                                JsonArrayRequest(Request.Method.GET, queueUrl, null, { queueJSON ->

                                    for (queue in 0 until queueJSON.length()) {
                                        if (queueJSON.getJSONObject(queue).getInt("queueId") == matchJSON.getInt("queueId")) {
                                            matchMainInfo += queueJSON.getJSONObject(queue).getString("description").replace(" games", "").replace("5v5 ", "")
                                            break
                                        }
                                    }

                                    matchMainInfo += " · ${(matchJSON.getInt("gameDuration") / 60)}:${matchJSON.getInt("gameDuration") % 60}"

                                    val netDate = Date(matchJSON.getLong("gameCreation"))
                                    val c = Calendar.getInstance()
                                    c.time = netDate

                                    when {
                                        c.get(Calendar.MONTH) + 1 == 1 -> {
                                            matchMainInfo += " · January"
                                        }
                                        c.get(Calendar.MONTH) + 1 == 2 -> {
                                            matchMainInfo += " · February"
                                        }
                                        c.get(Calendar.MONTH) + 1 == 3 -> {
                                            matchMainInfo += " · March"
                                        }
                                        c.get(Calendar.MONTH) + 1 == 4 -> {
                                            matchMainInfo += " · April"
                                        }
                                        c.get(Calendar.MONTH) + 1 == 5 -> {
                                            matchMainInfo += " · May"
                                        }
                                        c.get(Calendar.MONTH) + 1 == 6 -> {
                                            matchMainInfo += " · June"
                                        }
                                        c.get(Calendar.MONTH) + 1 == 7 -> {
                                            matchMainInfo += " · July"
                                        }
                                        c.get(Calendar.MONTH) + 1 == 8 -> {
                                            matchMainInfo += " · August"
                                        }
                                        c.get(Calendar.MONTH) + 1 == 9 -> {
                                            matchMainInfo += " · September"
                                        }
                                        c.get(Calendar.MONTH) + 1 == 10 -> {
                                            matchMainInfo += " · October"
                                        }
                                        c.get(Calendar.MONTH) + 1 == 11 -> {
                                            matchMainInfo += " · November"
                                        }
                                        c.get(Calendar.MONTH) + 1 == 12 -> {
                                            matchMainInfo += " · December"
                                        }
                                    }

                                    matchMainInfo += " ${c.get(Calendar.DAY_OF_MONTH)}"

                                    matchMainInfo += ", ${c.get(Calendar.YEAR)}"

                                    mainMatchInformation.value = matchMainInfo
                                }, {})

                            requestQueue.add(queueRequest)

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

                            if (player.getJSONObject("stats").getBoolean("win")) {
                                winOrLose.value = "Victory"
                                winOrLoseColor.value = R.color.highWinRate
                            } else {
                                winOrLose.value = "Defeat"
                                winOrLoseColor.value = R.color.lowWinRate
                            }

                            visionScore.value = player.getJSONObject("stats").getInt("visionScore").toString()

                            val csPerMinutes = (player.getJSONObject("stats").getInt("totalMinionsKilled") + player.getJSONObject("stats").getInt("neutralMinionsKilled")).toDouble() / (matchJSON.getInt("gameDuration") fdiv 60)

                            csScore.value = "%.1f".format(csPerMinutes)

                            val dmgPerMinutes = player.getJSONObject("stats").getInt("totalDamageDealtToChampions").toDouble() / (matchJSON.getInt("gameDuration") fdiv 60)

                            dmgScore.value = (dmgPerMinutes.roundToInt()).toString()

                            val champUrl = "https://raw.communitydragon.org/latest/plugins/rcp-be-lol-game-data/global/default/v1/champion-icons/${player.getInt("championId")}.png"

                            val champRequest = ImageRequest(
                                champUrl,
                                {bitmap ->
                                    champIcon.value = bitmap
                                },0,0,null,null,
                                {  }
                            )

                            requestQueue.add(champRequest)

                            if (player.getInt("teamId") == 100) {
                                teamId.value = "Team 1"
                                teamIdColor.value = R.color.highWinRate
                            } else if (player.getInt("teamId") == 200) {
                                teamId.value = "Team 2"
                                teamIdColor.value = R.color.lowWinRate
                            }

                            val summonerSpellUrl =
                                "https://ddragon.leagueoflegends.com/cdn/11.11.1/data/en_US/summoner.json"

                            val summonerSpellRequest =
                                JsonObjectRequest(Request.Method.GET, summonerSpellUrl, null,
                                    { summonerSpellJSON ->

                                        val key = summonerSpellJSON.getJSONObject("data").names()

                                        for (summonerSpell in 0 until key.length()) {
                                            val keys = key.getString(summonerSpell)
                                            val value = summonerSpellJSON.getJSONObject("data").getJSONObject(keys).getString("key")

                                            if (value == player.getInt("spell1Id").toString()) {
                                                val spell1Url = "https://ddragon.leagueoflegends.com/cdn/11.11.1/img/spell/${summonerSpellJSON.getJSONObject("data").getJSONObject(keys).getJSONObject("image").getString("full")}"

                                                val spell1Request = ImageRequest(
                                                    spell1Url,
                                                    {bitmap ->
                                                        firstSummonerSpellIcon.value = bitmap
                                                    },0,0,null,null,
                                                    {  }
                                                )

                                                requestQueue.add(spell1Request)
                                            }
                                        }

                                        for (i in 0 until key.length()) {
                                            val keys = key.getString(i)
                                            val value = summonerSpellJSON.getJSONObject("data").getJSONObject(keys).getString("key")

                                            if (value == player.getInt("spell2Id").toString()) {
                                                val spell2Url = "https://ddragon.leagueoflegends.com/cdn/11.11.1/img/spell/${summonerSpellJSON.getJSONObject("data").getJSONObject(keys).getJSONObject("image").getString("full")}"

                                                val spell2Request = ImageRequest(
                                                    spell2Url,
                                                    {bitmap ->
                                                        secondSummonerSpellIcon.value = bitmap
                                                    },0,0,null,null,
                                                    {  }
                                                )

                                                requestQueue.add(spell2Request)
                                            }
                                        }

                                    }, {})

                            requestQueue.add(summonerSpellRequest)

                            val summonerRuneUrl =
                                "https://ddragon.leagueoflegends.com/cdn/11.11.1/data/en_US/runesReforged.json"

                            val summonerRuneRequest =
                                JsonArrayRequest(Request.Method.GET, summonerRuneUrl, null,
                                    { summonerRuneJSON ->

                                        for (style in 0 until summonerRuneJSON.length()) {

                                            val runes = summonerRuneJSON.getJSONObject(style).getJSONArray("slots").getJSONObject(0).getJSONArray("runes")

                                            for (rune in 0 until runes.length()) {

                                                if (runes.getJSONObject(rune).getInt("id") == player.getJSONObject("stats").getInt("perk0")) {
                                                    val rune1Url = "https://ddragon.leagueoflegends.com/cdn/img/${runes.getJSONObject(rune).getString("icon")}"

                                                    val rune1Request = ImageRequest(
                                                        rune1Url,
                                                        {bitmap ->
                                                            firstRuneIcon.value = bitmap
                                                        },0,0,null,null,
                                                        {  }
                                                    )

                                                    requestQueue.add(rune1Request)
                                                }
                                            }

                                            if (summonerRuneJSON.getJSONObject(style).getInt("id") == player.getJSONObject("stats").getInt("perkSubStyle")) {
                                                val rune2Url = "https://ddragon.leagueoflegends.com/cdn/img/${summonerRuneJSON.getJSONObject(style).getString("icon")}"

                                                val rune2Request = ImageRequest(
                                                    rune2Url,
                                                    {bitmap ->
                                                        secondRuneIcon.value = bitmap
                                                    },0,0,null,null,
                                                    {  }
                                                )

                                                requestQueue.add(rune2Request)
                                            }
                                        }
                                    }, {})

                            requestQueue.add(summonerRuneRequest)

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

                            kdaText.value = "%.2f KDA".format(kda)

                            when {
                                kda >= 2 && kda < 4 -> {
                                    kdaColor.value = R.color.middleLowKDA
                                }
                                kda >= 4 && kda < 10 -> {
                                    kdaColor.value = R.color.middleHighKDA
                                }
                                kda >= 10 -> {
                                    kdaColor.value = R.color.highKDA
                                }
                                else -> {
                                    kdaColor.value = R.color.lowKDA
                                }
                            }

                            detailedKdaText.value = "$kills/$deaths/$assists"

                            csText.value = "${player.getJSONObject("stats").getInt("totalMinionsKilled") + player.getJSONObject("stats").getInt("neutralMinionsKilled")} CS"
                            dmgText.value = NumberFormat.getIntegerInstance().format(player.getJSONObject("stats").getInt("totalDamageDealtToChampions")) + " Dmg"

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

                            killParticipation.value = (((kills + assists) fdiv totalKills) * 100).roundToInt().toString() + "% KP"
                            dmgPercent.value = ((player.getJSONObject("stats").getInt("totalDamageDealtToChampions") fdiv totalDmg) * 100).roundToInt().toString() + "% of team"

                            for (item in 0 until 7) {

                                val itemId = player.getJSONObject("stats").getInt("item$item")

                                val itemUrl = "https://ddragon.leagueoflegends.com/cdn/11.11.1/img/item/$itemId.png"

                                val itemRequest = ImageRequest(
                                    itemUrl,
                                    {bitmap ->
                                        itemList[item].value = bitmap
                                    },0,0,null,null,
                                    {  }
                                )

                                requestQueue.add(itemRequest)
                            }

                            val team1 = matchJSON.getJSONArray("teams").getJSONObject(0)
                            val team2 = matchJSON.getJSONArray("teams").getJSONObject(1)

                            var team1Kills = 0
                            var team1Deaths = 0
                            var team1Assists = 0
                            var team1Gold = 0

                            var team2Kills = 0
                            var team2Deaths = 0
                            var team2Assists = 0
                            var team2Gold = 0

                            for (participant in 0 until matchJSON.getJSONArray("participants").length()) {
                                if (matchJSON.getJSONArray("participants").getJSONObject(participant).getInt("teamId") == team1.getInt("teamId")) {
                                    team1Kills += matchJSON.getJSONArray("participants")
                                        .getJSONObject(participant).getJSONObject("stats")
                                        .getInt("kills")
                                    team1Deaths += matchJSON.getJSONArray("participants")
                                        .getJSONObject(participant).getJSONObject("stats")
                                        .getInt("deaths")
                                    team1Assists += matchJSON.getJSONArray("participants")
                                        .getJSONObject(participant).getJSONObject("stats")
                                        .getInt("assists")
                                    team1Gold += matchJSON.getJSONArray("participants")
                                        .getJSONObject(participant).getJSONObject("stats")
                                        .getInt("goldEarned")
                                } else if (matchJSON.getJSONArray("participants").getJSONObject(participant).getInt("teamId") == team2.getInt("teamId")) {
                                    team2Kills += matchJSON.getJSONArray("participants")
                                        .getJSONObject(participant).getJSONObject("stats")
                                        .getInt("kills")
                                    team2Deaths += matchJSON.getJSONArray("participants")
                                        .getJSONObject(participant).getJSONObject("stats")
                                        .getInt("deaths")
                                    team2Assists += matchJSON.getJSONArray("participants")
                                        .getJSONObject(participant).getJSONObject("stats")
                                        .getInt("assists")
                                    team2Gold += matchJSON.getJSONArray("participants")
                                        .getJSONObject(participant).getJSONObject("stats")
                                        .getInt("goldEarned")
                                }
                            }

                            kda1.value = "$team1Kills/$team1Deaths/$team1Assists"
                            gold1.value = "%.1fk".format(team1Gold fdiv 1000)

                            turret1.value = team1.getInt("towerKills").toString()
                            inhib1.value = team1.getInt("inhibitorKills").toString()

                            nash1.value = team1.getInt("baronKills").toString()
                            herald1.value = team1.getInt("riftHeraldKills").toString()
                            drake1.value = team1.getInt("dragonKills").toString()

                            val turret1Url = "https://raw.communitydragon.org/latest/plugins/rcp-fe-lol-match-history/global/default/tower-100.png"

                            val turret1Request = ImageRequest(
                                turret1Url,
                                {bitmap ->
                                    turretIcon1.value = bitmap
                                },0,0,null,null,
                                {  }
                            )

                            requestQueue.add(turret1Request)

                            val inhib1Url = "https://raw.communitydragon.org/latest/plugins/rcp-fe-lol-match-history/global/default/inhibitor-100.png"

                            val inhib1Request = ImageRequest(
                                inhib1Url,
                                {bitmap ->
                                    inhibIcon1.value = bitmap
                                },0,0,null,null,
                                {  }
                            )

                            requestQueue.add(inhib1Request)

                            val nash1Url = "https://raw.communitydragon.org/latest/plugins/rcp-fe-lol-match-history/global/default/baron-100.png"

                            val nash1Request = ImageRequest(
                                nash1Url,
                                {bitmap ->
                                    nashIcon1.value = bitmap
                                },0,0,null,null,
                                {  }
                            )

                            requestQueue.add(nash1Request)

                            val herald1Url = "https://raw.communitydragon.org/latest/plugins/rcp-fe-lol-match-history/global/default/herald-100.png"

                            val herald1Request = ImageRequest(
                                herald1Url,
                                {bitmap ->
                                    heraldIcon1.value = bitmap
                                },0,0,null,null,
                                {  }
                            )

                            requestQueue.add(herald1Request)

                            val drake1Url = "https://raw.communitydragon.org/latest/plugins/rcp-fe-lol-match-history/global/default/dragon-100.png"

                            val drake1Request = ImageRequest(
                                drake1Url,
                                {bitmap ->
                                    drakeIcon1.value = bitmap
                                },0,0,null,null,
                                {  }
                            )

                            requestQueue.add(drake1Request)

                            kda2.value = "$team2Kills/$team2Deaths/$team2Assists"
                            gold2.value = "%.1fk".format(team2Gold fdiv 2000)

                            turret2.value = team2.getInt("towerKills").toString()
                            inhib2.value = team2.getInt("inhibitorKills").toString()

                            nash2.value = team2.getInt("baronKills").toString()
                            herald2.value = team2.getInt("riftHeraldKills").toString()
                            drake2.value = team2.getInt("dragonKills").toString()

                            val turret2Url = "https://raw.communitydragon.org/latest/plugins/rcp-fe-lol-match-history/global/default/tower-200.png"

                            val turret2Request = ImageRequest(
                                turret2Url,
                                {bitmap ->
                                    turretIcon2.value = bitmap
                                },0,0,null,null,
                                {  }
                            )

                            requestQueue.add(turret2Request)

                            val inhib2Url = "https://raw.communitydragon.org/latest/plugins/rcp-fe-lol-match-history/global/default/inhibitor-200.png"

                            val inhib2Request = ImageRequest(
                                inhib2Url,
                                {bitmap ->
                                    inhibIcon2.value = bitmap
                                },0,0,null,null,
                                {  }
                            )

                            requestQueue.add(inhib2Request)

                            val nash2Url = "https://raw.communitydragon.org/latest/plugins/rcp-fe-lol-match-history/global/default/baron-200.png"

                            val nash2Request = ImageRequest(
                                nash2Url,
                                {bitmap ->
                                    nashIcon2.value = bitmap
                                },0,0,null,null,
                                {  }
                            )

                            requestQueue.add(nash2Request)

                            val herald2Url = "https://raw.communitydragon.org/latest/plugins/rcp-fe-lol-match-history/global/default/herald-200.png"

                            val herald2Request = ImageRequest(
                                herald2Url,
                                {bitmap ->
                                    heraldIcon2.value = bitmap
                                },0,0,null,null,
                                {  }
                            )

                            requestQueue.add(herald2Request)

                            val drake2Url = "https://raw.communitydragon.org/latest/plugins/rcp-fe-lol-match-history/global/default/dragon-200.png"

                            val drake2Request = ImageRequest(
                                drake2Url,
                                {bitmap ->
                                    drakeIcon2.value = bitmap
                                },0,0,null,null,
                                {  }
                            )

                            requestQueue.add(drake2Request)

                        }, {})

                    requestQueue.add(matchRequest)
                }, {
                    fragment.enableNavigation()
                })

            requestQueue.add(matchListRequest)
        }
    }

    val summonerMainInformation = MediatorLiveData<JSONObject>()
    val summonerIcon = MutableLiveData<Bitmap>()
    val mainMatchInformation = MutableLiveData<String>()
    val winOrLose = MutableLiveData<String>()
    val winOrLoseColor = MutableLiveData<Int>()
    val roleIcon = MutableLiveData<Bitmap>()
    val visionScore = MutableLiveData<String>()
    val csScore = MutableLiveData<String>()
    val dmgScore = MutableLiveData<String>()
    val champIcon = MutableLiveData<Bitmap>()
    val teamId = MutableLiveData<String>()
    val teamIdColor = MutableLiveData<Int>()
    val firstSummonerSpellIcon = MutableLiveData<Bitmap>()
    val secondSummonerSpellIcon = MutableLiveData<Bitmap>()
    val firstRuneIcon = MutableLiveData<Bitmap>()
    val secondRuneIcon = MutableLiveData<Bitmap>()
    val kdaText = MutableLiveData<String>()
    val kdaColor = MutableLiveData<Int>()
    val detailedKdaText = MutableLiveData<String>()
    val csText = MutableLiveData<String>()
    val killParticipation = MutableLiveData<String>()
    val dmgText = MutableLiveData<String>()
    val dmgPercent = MutableLiveData<String>()

    val item1 = MutableLiveData<Bitmap>()
    val item2 = MutableLiveData<Bitmap>()
    val item3 = MutableLiveData<Bitmap>()
    val item4 = MutableLiveData<Bitmap>()
    val item5 = MutableLiveData<Bitmap>()
    val item6 = MutableLiveData<Bitmap>()
    val item7 = MutableLiveData<Bitmap>()
    private val itemList: List<MutableLiveData<Bitmap>> = listOf(item1, item2, item3, item4, item5, item6, item7)

    val kda1 = MutableLiveData<String>()
    val gold1 = MutableLiveData<String>()

    val turret1 = MutableLiveData<String>()
    val turretIcon1 = MutableLiveData<Bitmap>()
    val inhib1 = MutableLiveData<String>()
    val inhibIcon1 = MutableLiveData<Bitmap>()

    val nash1 = MutableLiveData<String>()
    val nashIcon1 = MutableLiveData<Bitmap>()
    val herald1 = MutableLiveData<String>()
    val heraldIcon1 = MutableLiveData<Bitmap>()
    val drake1 = MutableLiveData<String>()
    val drakeIcon1 = MutableLiveData<Bitmap>()

    val kda2 = MutableLiveData<String>()
    val gold2 = MutableLiveData<String>()

    val turret2 = MutableLiveData<String>()
    val turretIcon2 = MutableLiveData<Bitmap>()
    val inhib2 = MutableLiveData<String>()
    val inhibIcon2 = MutableLiveData<Bitmap>()

    val nash2 = MutableLiveData<String>()
    val nashIcon2 = MutableLiveData<Bitmap>()
    val herald2 = MutableLiveData<String>()
    val heraldIcon2 = MutableLiveData<Bitmap>()
    val drake2 = MutableLiveData<String>()
    val drakeIcon2 = MutableLiveData<Bitmap>()
}