package fr.cornier.phonegg.AddSummonerPage

import android.graphics.Bitmap
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import fr.cornier.phonegg.R
import org.json.JSONObject

class AddSummonerViewModel : ViewModel() {

    private lateinit var requestQueue:RequestQueue

    private var region = "euw1"

    private val regionList = arrayOf("br1", "eun1", "euw1", "jp1", "kr", "la1", "la2", "na1", "oc1", "ru", "tr1")

    fun searchForSummoner(summonerCharSequence:CharSequence, activityContext: FragmentActivity?) {

        /*
        *   args : summonerCharSequence as a CharSequence from the summonerNameInput
        *          activityContext as a FragmentActivity? for the context of the request queue
        *
        *   func : The function execute a GET request to the SUMMONER-V4 part of the riotgames API
        *   with the summoner name given. This will change the value of the summonerInformation to
        *   the JSON response of the request if the request succeeded and to null if the request
        *   failed
        */

        // Convert the CharSequence from the text input to String
        val summonerName = summonerCharSequence.toString()

        // Initialise the request queue
        requestQueue = Volley.newRequestQueue(activityContext)

        // Load the Api Key from the string.xml
        val apiKey = activityContext?.getString(R.string.api_key)

        // Prepare the url for the request, changing the region, the summoner name and the Api Key
        val summonerRequestUrl = "https://$region.api.riotgames.com/lol/summoner/v4/summoners/by-name/$summonerName?api_key=$apiKey"

        // Prepare the GET request with the url and if it succeed, change summonerInformation
        // value to the JSON response and if it fail change summonerInformation to null
        val summonerRequest = JsonObjectRequest(Request.Method.GET, summonerRequestUrl, null, { summonerJSON ->
            summonerInformation.value = summonerJSON

            // Get "profileIconId" value of the JSON
            val summonerIconId = summonerJSON.getInt("profileIconId")

            // Prepare the url for the request, changing the summoner icon id
            val iconUrl = "https://ddragon.leagueoflegends.com/cdn/11.10.1/img/profileicon/$summonerIconId.png"

            // Prepare the GET request with the url and if it succeed, change summonerIcon
            // value to the bitmap response and if it fail change summonerIcon to null
            val summonerIconRequest = ImageRequest(
                iconUrl,
                {bitmap ->
                    summonerIcon.value = bitmap
                },0,0,null,null,
                {
                    summonerIcon.value = null
                }
            )

            // Add the summonerIconRequest to the request Queue
            requestQueue.add(summonerIconRequest)

        }, {
            summonerInformation.value = null
            summonerIcon.value = null
        })

        // Add the summonerRequest to the request Queue
        requestQueue.add(summonerRequest)
    }

    fun setRegion(position:Int) {

        /*
        *   args : position as an Int for the position in the array of the region to set
        *
        *   func : this make the link between the spinner array and the regionList which contain
        *   the string use in the request url
        */

        region = regionList[position]
    }

    fun getRegion(): String {
        return region
    }

    // Create observable variable
    val summonerInformation = MutableLiveData<JSONObject>()

    val summonerIcon = MutableLiveData<Bitmap>()
}