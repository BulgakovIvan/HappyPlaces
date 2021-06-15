package com.example.happyplaces.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import com.example.happyplaces.databinding.ActivityHappyPlaceDetailBinding
import com.example.happyplaces.models.HappyPlaceModel

class HappyPlaceDetailActivity : AppCompatActivity() {
    private lateinit var bi: ActivityHappyPlaceDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bi = ActivityHappyPlaceDetailBinding.inflate(layoutInflater)
        setContentView(bi.root)

        var happyPlaceDetailModel: HappyPlaceModel? = null

        if (intent.hasExtra(MainActivity.EXTRA_PLACE_DETAILS)) {
            happyPlaceDetailModel = intent.getParcelableExtra<Parcelable>(
                MainActivity.EXTRA_PLACE_DETAILS) as HappyPlaceModel
        }

        if (happyPlaceDetailModel != null) {
            setSupportActionBar(bi.toolbarHappyPlaceDetail)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.title = happyPlaceDetailModel.title

            bi.toolbarHappyPlaceDetail.setNavigationOnClickListener {
                onBackPressed()
            }

            bi.ivPlaceImage.setImageURI(Uri.parse(happyPlaceDetailModel.image))
            bi.tvDescription.text = happyPlaceDetailModel.description
            bi.tvLocation.text = happyPlaceDetailModel.location

            bi.btnViewOnMap.setOnClickListener {
                val intent = Intent(this, MapActivity::class.java)
                intent.putExtra(MainActivity.EXTRA_PLACE_DETAILS, happyPlaceDetailModel)
                startActivity(intent)
            }

            bi.btnViewOnMapYandex.setOnClickListener {
                val intent = Intent(this, MapKitActivity::class.java)
                intent.putExtra("latitude", happyPlaceDetailModel.latitude)
                intent.putExtra("longitude", happyPlaceDetailModel.longitude)
                intent.putExtra("title", happyPlaceDetailModel.title)
                startActivity(intent)
            }
        }
    }
}