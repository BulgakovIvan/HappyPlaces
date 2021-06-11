package com.example.happyplaces.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.result.contract.ActivityResultContract
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode

class MyAutocompleteContract : ActivityResultContract<List<Place.Field>, Place?>() {

    override fun createIntent(context: Context, fields: List<Place.Field>) =
        Autocomplete.IntentBuilder(
            AutocompleteActivityMode.FULLSCREEN, fields).build(context)

    override fun parseResult(resultCode: Int, result: Intent?): Place?  {
        var mPlace: Place? = null
        when (resultCode) {
            Activity.RESULT_OK -> {
                result?.let {
                    mPlace = Autocomplete.getPlaceFromIntent(result)
                }
            }
            AutocompleteActivity.RESULT_ERROR -> {
                // Handle the error.
                result?.let {
                    val status = Autocomplete.getStatusFromIntent(result)
                    status.statusMessage?.let { it1 -> Log.e("ups", it1) }
                }
            }
            Activity.RESULT_CANCELED -> {
                // The user canceled the operation.
            }
        }
        return mPlace
    }
}