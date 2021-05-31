package fr.cornier.phonegg

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import fr.cornier.phonegg.HomePage.HomeFragment
import fr.cornier.phonegg.HomePage.SummonerAdapter
import kotlinx.android.synthetic.main.cell_summoner.*
import kotlinx.android.synthetic.main.fragment_add_summoner.*
import kotlinx.android.synthetic.main.fragment_home.*

class MainActivity : AppCompatActivity() {

    /*
    *   This Activity is just a support for all the fragments
    */

    var navigationEnable = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    override fun onBackPressed() {
        if (navigationEnable) {
            super.onBackPressed()
        }
    }

    fun setNavStatus(navStatus: Boolean) {
        navigationEnable = navStatus
    }
}