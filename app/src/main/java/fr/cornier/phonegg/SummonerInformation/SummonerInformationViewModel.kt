package fr.cornier.phonegg.SummonerInformation

import android.graphics.Bitmap
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import fr.cornier.phonegg.R
import fr.cornier.phonegg.Summoner
import io.realm.Realm
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Math.round
import kotlin.math.roundToInt

class SummonerInformationViewModel : ViewModel() {

    infix fun Int.fdiv(i: Int): Double = this / i.toDouble();

    private lateinit var requestQueue: RequestQueue

    private val realm = Realm.getDefaultInstance()

    fun getSummonerMainInformation(summonerAccountId:String, activityContext: FragmentActivity?) {
        val summoner = realm.where(Summoner::class.java).equalTo("summonerAccountId", summonerAccountId).findFirst()

        requestQueue = Volley.newRequestQueue(activityContext)

        val region = summoner?.summonerRegion

        val summonerId = summoner?.summonerId

        // Load the Api Key from the string.xml
        val apiKey = activityContext?.getString(R.string.api_key)

        // Prepare the url for the request, changing the region, the summoner name and the Api Key
        val summonerRequestUrl = "https://$region.api.riotgames.com/lol/summoner/v4/summoners/by-account/$summonerAccountId?api_key=$apiKey"

        if (summoner != null) {
            val summonerMainInformationRequest = JsonObjectRequest(Request.Method.GET, summonerRequestUrl, null, {  summonerJSON ->
                summonerMainInformation.value = summonerJSON

                val summonerIconId = summonerJSON.getInt("profileIconId")

                // Prepare the url for the request, changing the summoner icon id
                val iconUrl = "http://ddragon.leagueoflegends.com/cdn/11.10.1/img/profileicon/$summonerIconId.png"
                val rankUrl = "https://$region.api.riotgames.com/lol/league/v4/entries/by-summoner/$summonerId?api_key=$apiKey"
                val masteryURL = "https://$region.api.riotgames.com/lol/champion-mastery/v4/champion-masteries/by-summoner/$summonerId?api_key=$apiKey"

                // Prepare the GET request with the url and if it succeed, change summonerIcon
                // value to the bitmap response and if it fail change summonerIcon to null
                val summonerIconRequest = ImageRequest(
                    iconUrl,
                    {bitmap ->
                        summonerIcon.value = bitmap
                    },0,0,null,null,
                    {  }
                )

                // Add the summonerIconRequest to the request Queue
                requestQueue.add(summonerIconRequest)

                val summonerMasteryRequest = JsonArrayRequest(Request.Method.GET, masteryURL, null,
                    { summonerMasteryJSON ->

                        if (summonerMasteryJSON.length() >= 1) {
                            val champ1 = summonerMasteryJSON.getJSONObject(0).getInt("championId")
                            val champ1Url = "https://raw.communitydragon.org/latest/plugins/rcp-be-lol-game-data/global/default/v1/champion-icons/$champ1.png"

                            val summonerChamp1Request = ImageRequest(
                                champ1Url,
                                {bitmap ->
                                    summonerMasteryChamp1Icon.value = bitmap
                                },0,0,null,null,
                                {  }
                            )

                            requestQueue.add(summonerChamp1Request)
                        }
                        if (summonerMasteryJSON.length() >= 2) {
                            val champ2 = summonerMasteryJSON.getJSONObject(1).getInt("championId")
                            val champ2Url = "https://raw.communitydragon.org/latest/plugins/rcp-be-lol-game-data/global/default/v1/champion-icons/$champ2.png"

                            val summonerChamp2Request = ImageRequest(
                                champ2Url,
                                {bitmap ->
                                    summonerMasteryChamp2Icon.value = bitmap
                                },0,0,null,null,
                                {  }
                            )

                            requestQueue.add(summonerChamp2Request)
                        }
                        if (summonerMasteryJSON.length() >= 3) {
                            val champ3 = summonerMasteryJSON.getJSONObject(2).getInt("championId")
                            val champ3Url = "https://raw.communitydragon.org/latest/plugins/rcp-be-lol-game-data/global/default/v1/champion-icons/$champ3.png"

                            val summonerChamp3Request = ImageRequest(
                                champ3Url,
                                {bitmap ->
                                    summonerMasteryChamp3Icon.value = bitmap
                                },0,0,null,null,
                                {  }
                            )

                            requestQueue.add(summonerChamp3Request)
                        }

                        if (summonerMasteryJSON.length() >= 1) {

                            noMasteries.value = false

                            when {
                                summonerMasteryJSON.getJSONObject(0)
                                    .getInt("championLevel") == 1 -> {
                                    mastery1.value = R.drawable.mastery_1

                                }
                                summonerMasteryJSON.getJSONObject(0)
                                    .getInt("championLevel") == 2 -> {
                                    mastery1.value = R.drawable.mastery_2

                                }
                                summonerMasteryJSON.getJSONObject(0)
                                    .getInt("championLevel") == 3 -> {
                                    mastery1.value = R.drawable.mastery_3

                                }
                                summonerMasteryJSON.getJSONObject(0)
                                    .getInt("championLevel") == 4 -> {
                                    mastery1.value = R.drawable.mastery_4

                                }
                                summonerMasteryJSON.getJSONObject(0)
                                    .getInt("championLevel") == 5 -> {
                                    mastery1.value = R.drawable.mastery_5

                                }
                                summonerMasteryJSON.getJSONObject(0)
                                    .getInt("championLevel") == 6 -> {
                                    mastery1.value = R.drawable.mastery_6

                                }
                                summonerMasteryJSON.getJSONObject(0)
                                    .getInt("championLevel") == 7 -> {
                                    mastery1.value = R.drawable.mastery_7

                                }
                            }

                            mastery1Points.value = summonerMasteryJSON.getJSONObject(0).getInt("championPoints")
                        } else {
                            noMasteries.value = true
                        }

                        if (summonerMasteryJSON.length() >= 2) {

                            when {
                                summonerMasteryJSON.getJSONObject(1)
                                    .getInt("championLevel") == 1 -> {
                                    mastery2.value = R.drawable.mastery_1

                                }
                                summonerMasteryJSON.getJSONObject(1)
                                    .getInt("championLevel") == 2 -> {
                                    mastery2.value = R.drawable.mastery_2

                                }
                                summonerMasteryJSON.getJSONObject(1)
                                    .getInt("championLevel") == 3 -> {
                                    mastery2.value = R.drawable.mastery_3

                                }
                                summonerMasteryJSON.getJSONObject(1)
                                    .getInt("championLevel") == 4 -> {
                                    mastery2.value = R.drawable.mastery_4

                                }
                                summonerMasteryJSON.getJSONObject(1)
                                    .getInt("championLevel") == 5 -> {
                                    mastery2.value = R.drawable.mastery_5

                                }
                                summonerMasteryJSON.getJSONObject(1)
                                    .getInt("championLevel") == 6 -> {
                                    mastery2.value = R.drawable.mastery_6

                                }
                                summonerMasteryJSON.getJSONObject(1)
                                    .getInt("championLevel") == 7 -> {
                                    mastery2.value = R.drawable.mastery_7

                                }
                            }

                            mastery2Points.value = summonerMasteryJSON.getJSONObject(1).getInt("championPoints")
                        }

                        if (summonerMasteryJSON.length() >= 3) {

                            when {
                                summonerMasteryJSON.getJSONObject(2)
                                    .getInt("championLevel") == 1 -> {
                                    mastery3.value = R.drawable.mastery_1

                                }
                                summonerMasteryJSON.getJSONObject(2)
                                    .getInt("championLevel") == 2 -> {
                                    mastery3.value = R.drawable.mastery_2

                                }
                                summonerMasteryJSON.getJSONObject(2)
                                    .getInt("championLevel") == 3 -> {
                                    mastery3.value = R.drawable.mastery_3

                                }
                                summonerMasteryJSON.getJSONObject(2)
                                    .getInt("championLevel") == 4 -> {
                                    mastery3.value = R.drawable.mastery_4

                                }
                                summonerMasteryJSON.getJSONObject(2)
                                    .getInt("championLevel") == 5 -> {
                                    mastery3.value = R.drawable.mastery_5

                                }
                                summonerMasteryJSON.getJSONObject(2)
                                    .getInt("championLevel") == 6 -> {
                                    mastery3.value = R.drawable.mastery_6

                                }
                                summonerMasteryJSON.getJSONObject(2)
                                    .getInt("championLevel") == 7 -> {
                                    mastery3.value = R.drawable.mastery_7

                                }
                            }

                            mastery3Points.value = summonerMasteryJSON.getJSONObject(2).getInt("championPoints")
                        }

                    },{})

                requestQueue.add(summonerMasteryRequest)

                val summonerRankRequest = JsonArrayRequest(Request.Method.GET, rankUrl, null,
                    {summonerRankJSON ->

                        if (summonerRankJSON.length() > 0) {

                            unranked.value = false

                            lp.value = summonerRankJSON.getJSONObject(0).getInt("leaguePoints").toString() + "LP"

                            val wins = summonerRankJSON.getJSONObject(0).getInt("wins")
                            val losses = summonerRankJSON.getJSONObject(0).getInt("losses")

                            winLoseRation.value = wins.toString() + "W-" + losses.toString() + "L"
                            winrate.value = ((wins fdiv (wins + losses)) * 100).roundToInt().toString() + "%"

                            if (((wins fdiv (wins + losses)) * 100).roundToInt() > 50) {
                                winrateColor.value = R.color.highWinRate
                            } else if (((wins fdiv (wins + losses)) * 100).roundToInt() < 50) {
                                winrateColor.value = R.color.lowWinRate
                            } else {
                                winrateColor.value = R.color.middleWinRate
                            }

                            var category = ""
                            var subCategory = ""
                            var tier = ""
                            var wing = "sr"

                            var rankText = ""

                            var supraMaster = false

                            when {
                                summonerRankJSON.getJSONObject(0).getString("tier") == "IRON" -> {

                                    category = "01_iron"
                                    subCategory = "iron"
                                    wing = "s"

                                    rankText += "Iron"
                                    divTextColor.value = R.color.iron

                                }
                                summonerRankJSON.getJSONObject(0).getString("tier") == "BRONZE" -> {

                                    category = "02_bronze"
                                    subCategory = "bronze"
                                    wing = "s"

                                    rankText += "Bronze"
                                    divTextColor.value = R.color.bronze

                                }
                                summonerRankJSON.getJSONObject(0).getString("tier") == "SILVER" -> {

                                    category = "03_silver"
                                    subCategory = "silver"

                                    rankText += "Silver"
                                    divTextColor.value = R.color.silver

                                }
                                summonerRankJSON.getJSONObject(0).getString("tier") == "GOLD" -> {

                                    category = "04_gold"
                                    subCategory = "gold"
                                    wing = "s"

                                    rankText += "Gold"
                                    divTextColor.value = R.color.gold

                                }
                                summonerRankJSON.getJSONObject(0)
                                    .getString("tier") == "PLATINUM" -> {

                                    category = "05_platinum"
                                    subCategory = "platinum"
                                    wing = "s"

                                    rankText += "Platinum"
                                    divTextColor.value = R.color.platinum

                                }
                                summonerRankJSON.getJSONObject(0)
                                    .getString("tier") == "DIAMOND" -> {

                                    category = "06_diamond"
                                    subCategory = "diamond"

                                    rankText += "Diamond"
                                    divTextColor.value = R.color.diamond

                                }
                                summonerRankJSON.getJSONObject(0).getString("tier") == "MASTER" -> {

                                    category = "07_master"
                                    subCategory = "master"

                                    rankText += "Master"
                                    divTextColor.value = R.color.master

                                    supraMaster = true

                                }
                                summonerRankJSON.getJSONObject(0)
                                    .getString("tier") == "GRANDMASTER" -> {

                                    category = "08_grandmaster"
                                    subCategory = "grandmaster"

                                    rankText += "GrandMaster"
                                    divTextColor.value = R.color.grandmaster

                                    supraMaster = true

                                }
                                summonerRankJSON.getJSONObject(0)
                                    .getString("tier") == "CHALLENGER" -> {

                                    category = "09_challenger"
                                    subCategory = "challenger"

                                    rankText += "Challenger"
                                    divTextColor.value = R.color.challenger

                                    supraMaster = true

                                }
                            }

                            if (!supraMaster) {

                                when {
                                    summonerRankJSON.getJSONObject(0).getString("rank") == "I" -> {

                                        tier = "1"

                                        rankText += " 1"

                                    }
                                    summonerRankJSON.getJSONObject(0).getString("rank") == "II" -> {

                                        tier = "2"

                                        rankText += " 2"

                                    }
                                    summonerRankJSON.getJSONObject(0)
                                        .getString("rank") == "III" -> {

                                        tier = "3"

                                        rankText += " 3"

                                    }
                                    summonerRankJSON.getJSONObject(0).getString("rank") == "IV" -> {

                                        tier = "4"

                                        rankText += " 4"

                                    }
                                }
                            }

                            divText.value = rankText

                            val iconRankBorderUrl =
                                "https://raw.communitydragon.org/latest/plugins/rcp-be-lol-game-data/global/default/content/src/leagueclient/rankedcrests/$category/images/${subCategory}_base.png"
                            val rankIconUrl =
                                "https://raw.communitydragon.org/latest/plugins/rcp-be-lol-game-data/global/default/content/src/leagueclient/rankedcrests/$category/images/${subCategory}_baseface_matte.png"

                            var iconRankBorderCrownUrl = ""

                            if (!supraMaster) {
                                iconRankBorderCrownUrl =
                                    "https://raw.communitydragon.org/latest/plugins/rcp-be-lol-game-data/global/default/content/src/leagueclient/rankedcrests/$category/images/${subCategory}_crown_d$tier.png"
                            } else {
                                iconRankBorderCrownUrl =
                                    "https://raw.communitydragon.org/latest/plugins/rcp-be-lol-game-data/global/default/content/src/leagueclient/rankedcrests/$category/images/${subCategory}_crown.png"
                            }

                            val iconRankBorderWingUrl =
                                "https://raw.communitydragon.org/latest/plugins/rcp-be-lol-game-data/global/default/content/src/leagueclient/rankedcrests/$category/images/${subCategory}_${wing}1.png"
                            val iconRankBorderSecondWingUrl =
                                "https://raw.communitydragon.org/latest/plugins/rcp-be-lol-game-data/global/default/content/src/leagueclient/rankedcrests/$category/images/${subCategory}_${wing}2.png"
                            val iconRankBorderSwordUrl =
                                "https://raw.communitydragon.org/latest/plugins/rcp-be-lol-game-data/global/default/content/src/leagueclient/rankedcrests/$category/images/${subCategory}_${wing}3.png"

                            val summonerIconRankBorderCrownUrlRequest = ImageRequest(
                                iconRankBorderCrownUrl,
                                { bitmap ->
                                    summonerRankBorderCrown.value = bitmap
                                }, 0, 0, null, null,
                                { }
                            )

                            // Add the summonerIconRequest to the request Queue
                            requestQueue.add(summonerIconRankBorderCrownUrlRequest)

                            val summonerIconRankBorderWingUrlRequest = ImageRequest(
                                iconRankBorderWingUrl,
                                { bitmap ->
                                    summonerRankBorderWing.value = bitmap
                                }, 0, 0, null, null,
                                { }
                            )

                            // Add the summonerIconRequest to the request Queue
                            requestQueue.add(summonerIconRankBorderWingUrlRequest)

                            val summonerIconRankBorderSecondWingUrlRequest = ImageRequest(
                                iconRankBorderSecondWingUrl,
                                { bitmap ->
                                    summonerRankBorderSecondWing.value = bitmap
                                }, 0, 0, null, null,
                                { }
                            )

                            // Add the summonerIconRequest to the request Queue
                            requestQueue.add(summonerIconRankBorderSecondWingUrlRequest)

                            val summonerIconRankBorderRequest = ImageRequest(
                                iconRankBorderUrl,
                                { bitmap ->
                                    summonerRankBorder.value = bitmap
                                }, 0, 0, null, null,
                                { }
                            )

                            // Add the summonerIconRequest to the request Queue
                            requestQueue.add(summonerIconRankBorderRequest)

                            val summonerIconRankBorderSwordRequest = ImageRequest(
                                iconRankBorderSwordUrl,
                                { bitmap ->
                                    summonerRankBorderSword.value = bitmap
                                }, 0, 0, null, null,
                                { }
                            )

                            // Add the summonerIconRequest to the request Queue
                            requestQueue.add(summonerIconRankBorderSwordRequest)

                            val summonerRankIconRequest = ImageRequest(
                                rankIconUrl,
                                { bitmap ->
                                    summonerRankIcon.value = bitmap
                                }, 0, 0, null, null,
                                { }
                            )

                            // Add the summonerIconRequest to the request Queue
                            requestQueue.add(summonerRankIconRequest)

                        } else {
                            unranked.value = true

                        }
                    },
                    {
                        Log.i("Test", "Error")
                    }
                )

                // Add the summonerIconRequest to the request Queue
                requestQueue.add(summonerRankRequest)

            }, {
                summonerMainInformation.value = null
            })

            requestQueue.add(summonerMainInformationRequest)
        }
    }

    val summonerMainInformation = MutableLiveData<JSONObject>()
    val summonerIcon = MutableLiveData<Bitmap>()
    val summonerRankBorder = MutableLiveData<Bitmap>()
    val summonerRankIcon = MutableLiveData<Bitmap>()
    val summonerRankBorderSword = MutableLiveData<Bitmap>()
    val summonerRankBorderCrown = MutableLiveData<Bitmap>()
    val summonerRankBorderWing = MutableLiveData<Bitmap>()
    val summonerRankBorderSecondWing = MutableLiveData<Bitmap>()
    val summonerMasteryChamp1Icon = MutableLiveData<Bitmap>()
    val summonerMasteryChamp2Icon = MutableLiveData<Bitmap>()
    val summonerMasteryChamp3Icon = MutableLiveData<Bitmap>()
    val mastery1 = MutableLiveData<Int>()
    val mastery2 = MutableLiveData<Int>()
    val mastery3 = MutableLiveData<Int>()
    val mastery1Points = MutableLiveData<Int>()
    val mastery2Points = MutableLiveData<Int>()
    val mastery3Points = MutableLiveData<Int>()
    val divText = MutableLiveData<String>()
    val divTextColor = MutableLiveData<Int>()
    val lp = MutableLiveData<String>()
    val winLoseRation = MutableLiveData<String>()
    val winrate = MutableLiveData<String>()
    val winrateColor = MutableLiveData<Int>()

    val unranked = MutableLiveData<Boolean>()
    val noMasteries = MutableLiveData<Boolean>()
}