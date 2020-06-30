package com.example.publictransport;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;

public class DestinationActivity extends AppCompatActivity {

    private MapView mapView;
    private MapSetup mapSetup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_main);

        //change the text on the actionBar
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
            actionBar.setTitle("Destination");

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        mapSetup = new MapSetup(this, mapView);
        mapSetup.getCurrentLocation();

        saveLocation();
    }

    //save destination location and start JourneyActivity
    public void saveLocation(){
        Button saveLocationButton = findViewById(R.id.save_location_btn);
        saveLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("DestinationActivity", "mapSetup.getSourceOrDestLocation()" + mapSetup.getSourceOrDestLocation());
                LatLng destinationLocation = mapSetup.getSourceOrDestLocation();
                LatLng  sourceLocation = getIntent().getExtras().getParcelable("EXTRA_SOURCE_LOCATION");

                Log.d("DestinationActivity", "sourceLocation: " + sourceLocation + ", " + "destinationLocation: " + destinationLocation);

                if(destinationLocation != null) {
                    Bundle args = new Bundle();
                    args.putParcelable("EXTRA_DESTINATION_LOCATION", destinationLocation);
                    args.putParcelable("EXTRA_SOURCE_LOCATION", getIntent().getExtras().getParcelable("EXTRA_SOURCE_LOCATION"));
                    Intent intent = new Intent(DestinationActivity.this, JourneyActivity.class);
                    intent.putExtras(args);
                    //ActivityCompat.startActivityForResult(SourceActivity.this, intent, 0 , null);
                    //startActivityForResult(intent, 0, null);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}
