package com.example.happyplaces.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.happyplaces.R
import com.example.happyplaces.adapters.HappyPlacesAdapter
import com.example.happyplaces.database.DatabaseHandler
import com.example.happyplaces.databinding.ActivityMainBinding
import com.example.happyplaces.models.HappyPlaceModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private lateinit var bi: ActivityMainBinding

    private val addHappyPlaceResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            getHappyPlaceListFromLocalDB()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bi = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bi.root)

        findViewById<FloatingActionButton>(R.id.fabAddHappyPlaces).setOnClickListener {
            val intent = Intent(this, AddHappyPlaceActivity::class.java)
            addHappyPlaceResult.launch(intent)
        }

        getHappyPlaceListFromLocalDB()
    }

    private fun getHappyPlaceListFromLocalDB() {
        val dbHandler = DatabaseHandler(this)

        // TODO: 02.06.2021 delete fun
//        dbHandler.deleteDataFromHappyPLaceTable()

        val getHappyPlaceList: ArrayList<HappyPlaceModel> = dbHandler.getHappyPlacesList()

        if (getHappyPlaceList.size > 0) {
            bi.rvHappyPlacesList.visibility = View.VISIBLE
            bi.tvNoRecordsAvailable.visibility = View.GONE

            setupHappyPlacesRecyclerView(getHappyPlaceList)
        } else {
            bi.rvHappyPlacesList.visibility = View.GONE
            bi.tvNoRecordsAvailable.visibility = View.VISIBLE
        }
    }

    private fun setupHappyPlacesRecyclerView(happyPlaceList: ArrayList<HappyPlaceModel>) {
        bi.rvHappyPlacesList.layoutManager = LinearLayoutManager(this)
        bi.rvHappyPlacesList.setHasFixedSize(true)

        val placesAdapter = HappyPlacesAdapter(this, happyPlaceList)
        bi.rvHappyPlacesList.adapter = placesAdapter

        placesAdapter.setOnClickListener(object :
            HappyPlacesAdapter.OnClickListener {
            override fun onClick(position: Int, model: HappyPlaceModel) {
                val intent = Intent(this@MainActivity, HappyPlaceDetailActivity::class.java)
                intent.putExtra(EXTRA_PLACE_DETAILS, model)
                startActivity(intent)
            }
        })
    }

    companion object {
        internal const val EXTRA_PLACE_DETAILS = "extra_place_details"
    }
}