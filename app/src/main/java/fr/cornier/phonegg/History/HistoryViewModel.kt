package fr.cornier.phonegg.History

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import fr.cornier.phonegg.R
import fr.cornier.phonegg.Summoner
import io.realm.Realm
import org.json.JSONObject

class HistoryViewModel : ViewModel() {

    private lateinit var requestQueue: RequestQueue

    private val realm = Realm.getDefaultInstance()

    fun getSummonerMainInformation(summonerAccountId:String, activityContext: FragmentActivity?) {
        val summoner = realm.where(Summoner::class.java).equalTo("summonerAccountId", summonerAccountId).findFirst()

        requestQueue = Volley.newRequestQueue(activityContext)

        val region = summoner?.summonerRegion

        val summonerAccountId = summoner?.summonerAccountId

        // Load the Api Key from the string.xml
        val apiKey = activityContext?.getString(R.string.api_key)

        // Prepare the url for the request, changing the region, the summoner name and the Api Key
        val summonerRequestUrl = "https://$region.api.riotgames.com/lol/match/v4/matchlists/by-account/$summonerAccountId?api_key=$apiKey"

        if (summoner != null) {
            val matchListRequest =
                JsonObjectRequest(Request.Method.GET, summonerRequestUrl, null, { summonerJSON ->
                    matchList.value = summonerJSON
                }, {})

            requestQueue.add(matchListRequest)
        }
    }

    val matchList = MutableLiveData<JSONObject>()
}