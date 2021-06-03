package com.example.happyplaces.activities

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
            happyPlaceDetailModel = intent.getSerializableExtra(
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
        }
    }
}