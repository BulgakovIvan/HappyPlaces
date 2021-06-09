package com.example.happyplaces.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import com.example.happyplaces.R
import com.example.happyplaces.databinding.ActivityMapBinding
import com.example.happyplaces.models.HappyPlaceModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var bi: ActivityMapBinding

    private var mHappyPlaceDetails: HappyPlaceModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bi = ActivityMapBinding.inflate(layoutInflater)
        setContentView(bi.root)

        if (intent.hasExtra(MainActivity.EXTRA_PLACE_DETAILS)) {
            mHappyPlaceDetails =
                intent.getParcelableExtra<Parcelable>(MainActivity.EXTRA_PLACE_DETAILS)
                        as HappyPlaceModel
        }

        if (mHappyPlaceDetails != null) {
            setSupportActionBar(bi.toolbarMap)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.title = mHappyPlaceDetails!!.title

            bi.toolbarMap.setNavigationOnClickListener {
                onBackPressed()
            }
        }

        val supportMapFragment: SupportMapFragment =
            supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        supportMapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        val position = LatLng(mHappyPlaceDetails!!.latitude, mHappyPlaceDetails!!.longitude)
        googleMap.addMarker(MarkerOptions().position(position).title(mHappyPlaceDetails!!.location))
        val newLatLongZoom = CameraUpdateFactory.newLatLngZoom(position, 10f)
        googleMap.animateCamera(newLatLongZoom)

    }
}