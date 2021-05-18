package fr.cornier.phonegg.AddSummonerPage

import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import fr.cornier.phonegg.R
import org.json.JSONObject

class AddSummonerViewModel : ViewModel() {

    private lateinit var requestQueue:RequestQueue

    private var region = "euw1"

    private val regionList = arrayOf("br1", "eun1", "euw1", "jp1", "kr", "la1", "la2", "na1", "oc1", "ru", "tr1")

    fun searchForSummoner(summonerCharSequence:CharSequence, activityContext: FragmentActivity?) {
        val summonerName = summonerCharSequence.toString()

        requestQueue = Volley.newRequestQueue(activityContext)

        val apiKey = activityContext?.getString(R.string.api_key)

        val url = "https://$region.api.riotgames.com/lol/summoner/v4/summoners/by-name/$summonerName?api_key=$apiKey"

        val summonerRequest = JsonObjectRequest(Request.Method.GET, url, null, { summonerJSON ->
            summonerInformation.value = summonerJSON
        }, {
            summonerInformation.value = null
        })

        requestQueue.add(summonerRequest)
    }

    fun setRegion(position:Int) {
        region = regionList[position]
    }

    val summonerInformation = MutableLiveData<JSONObject>()
}