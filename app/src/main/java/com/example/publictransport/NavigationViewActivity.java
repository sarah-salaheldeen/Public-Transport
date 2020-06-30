package com.example.publictransport;

import androidx.appcompat.app.AppCompatActivity;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.api.directions.v5.models.BannerInstructions;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.services.android.navigation.ui.v5.NavigationView;
import com.mapbox.services.android.navigation.ui.v5.NavigationViewOptions;
import com.mapbox.services.android.navigation.ui.v5.OnNavigationReadyCallback;
import com.mapbox.services.android.navigation.ui.v5.listeners.BannerInstructionsListener;
import com.mapbox.services.android.navigation.ui.v5.listeners.NavigationListener;
import com.mapbox.services.android.navigation.ui.v5.listeners.SpeechAnnouncementListener;
import com.mapbox.services.android.navigation.ui.v5.voice.SpeechAnnouncement;
import com.mapbox.services.android.navigation.v5.milestone.Milestone;
import com.mapbox.services.android.navigation.v5.milestone.MilestoneEventListener;
import com.mapbox.services.android.navigation.v5.navigation.MapboxNavigationOptions;
import com.mapbox.services.android.navigation.v5.routeprogress.ProgressChangeListener;
import com.mapbox.services.android.navigation.v5.routeprogress.RouteProgress;

public class NavigationViewActivity extends AppCompatActivity implements OnNavigationReadyCallback, NavigationListener {
    private NavigationView navigationView;
    private DirectionsRoute selectedRoute;
    private LatLng sourceLocation;
    private MapSetup mapSetup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_AppCompat_NoActionBar);
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_navigation_view);

        selectedRoute = (DirectionsRoute) getIntent().getExtras().get("EXTRA_SELECTED_ROUTE");
        sourceLocation = (LatLng) getIntent().getExtras().get("EXTRA_SOURCE_LOCATION");

        mapSetup = new MapSetup(this);

        navigationView = findViewById(R.id.nav_view);
        navigationView.onCreate(savedInstanceState);
        navigationView.initialize(this);



        //startNavigation();
    }

    @Override
    public void onNavigationReady(boolean isRunning) {
        Log.i("NavigationViewActivity", "navigation is ready!");

        NavigationViewOptions navigationViewOptions = NavigationViewOptions.builder()
                .directionsRoute(selectedRoute)
                .navigationListener(this)
                .shouldSimulateRoute(true)
                .speechAnnouncementListener(new SpeechAnnouncementListener() {
                    @Override
                    public SpeechAnnouncement willVoice(SpeechAnnouncement announcement) {
                        Log.i("NavigationViewActivity", "announcement: " + announcement.announcement());
                        return null;
                    }
                })
                .milestoneEventListener(new MilestoneEventListener() {
                    @Override
                    public void onMilestoneEvent(RouteProgress routeProgress, String instruction, Milestone milestone) {

                    }
                })
                .build();
        navigationView.startNavigation(navigationViewOptions);

    }

    @Override
    public void onStart() {
        super.onStart();
        navigationView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        navigationView.onResume();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        navigationView.onLowMemory();
    }

    @Override
    public void onBackPressed() {
        // If the navigation view didn't need to do anything, call super
        if (!navigationView.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        navigationView.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        navigationView.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
        navigationView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        navigationView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        navigationView.onDestroy();
    }

    @Override
    public void onCancelNavigation() {

    }

    @Override
    public void onNavigationFinished() {

    }

    @Override
    public void onNavigationRunning() {

    }
}
