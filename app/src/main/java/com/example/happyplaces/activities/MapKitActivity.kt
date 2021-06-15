package com.example.happyplaces.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.happyplaces.R
import com.example.happyplaces.databinding.ActivityMapKitBinding
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider


class MapKitActivity : AppCompatActivity() {
    private lateinit var mapview: MapView
    private lateinit var bi: ActivityMapKitBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MapKitFactory.initialize(this)

        bi = ActivityMapKitBinding.inflate(layoutInflater)
        setContentView(bi.root)

        val extraData = intent.extras

        setSupportActionBar(bi.toolbarYandexMap)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = extraData!!.getString("title")
        bi.toolbarYandexMap.setNavigationOnClickListener {
            onBackPressed()
        }

        val point = Point(extraData.getDouble("latitude"),
                          extraData.getDouble("longitude"))

        val icImage : ImageProvider = ImageProvider.fromResource(this, R.drawable.ic_carrot_50)

        mapview = bi.yandexMap
        mapview.map.move(
            CameraPosition(point, 9.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 5F),
            null
        )
        mapview.map.mapObjects.addPlacemark(point, icImage)
    }

    override fun onStop() {
        mapview.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapview.onStart()
    }
}