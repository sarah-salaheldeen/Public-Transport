package com.example.publictransport;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.style.light.Position;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.ui.v5.NavigationView;
import com.mapbox.services.android.navigation.ui.v5.NavigationViewOptions;
import com.mapbox.services.android.navigation.ui.v5.OnNavigationReadyCallback;
import com.mapbox.services.android.navigation.ui.v5.feedback.FeedbackItem;
import com.mapbox.services.android.navigation.ui.v5.listeners.FeedbackListener;
import com.mapbox.services.android.navigation.ui.v5.listeners.NavigationListener;
import com.mapbox.services.android.navigation.ui.v5.listeners.RouteListener;
import com.mapbox.services.android.navigation.ui.v5.listeners.SpeechAnnouncementListener;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.ui.v5.route.OnRouteSelectionChangeListener;
import com.mapbox.services.android.navigation.ui.v5.voice.SpeechAnnouncement;
import com.mapbox.services.android.navigation.v5.milestone.Milestone;
import com.mapbox.services.android.navigation.v5.milestone.MilestoneEventListener;
import com.mapbox.services.android.navigation.v5.routeprogress.RouteProgress;

import org.neo4j.driver.types.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JourneyActivity extends AppCompatActivity implements MilestoneEventListener {

    private MapView mapView;
    private MapSetup mapSetup;

    private DatabaseQuery databaseQuery;

    private String line1Name;
    private String line2Name;

    private Button startJourney;

    private LatLng sourceLocationLatLng;

    //private NavigationView navigationView;

    //private NavigationView navigationView;

    //private PlanJourney journey ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_main);

        //change the text on the actionBar
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
            actionBar.setTitle("Your Journey");

        /*navigationView = findViewById(R.id.nav_view);
        navigationView.setVisibility(View.GONE);*/

        /*navigationView = findViewById(R.id.nav_view);
        navigationView.onCreate(savedInstanceState);
*/
        startJourney = findViewById(R.id.save_location_btn);
        startJourney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.i("JourneyActivity", "Btn clicked!");

                DirectionsRoute selectedRoute = mapSetup.getSelectedRoute();
                //Log.i("JourneyActivity", "selectedRoute: " + selectedRoute.routeOptions().voiceInstructions().toString());

                if (selectedRoute != null) {
                    Intent intent = new Intent(JourneyActivity.this, NavigationViewActivity.class);
                    intent.putExtra("EXTRA_SELECTED_ROUTE", selectedRoute);
                    intent.putExtra("EXTRA_SOURCE_LOCATION", sourceLocationLatLng);
                    startActivity(intent);
                    /*NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                            .directionsRoute(selectedRoute)
                            .shouldSimulateRoute(true)
                            .build();

                    // Call this method with Context from within an Activity
                    NavigationLauncher.startNavigation(JourneyActivity.this, options);*/

                }else
                    Toast.makeText(JourneyActivity.this, "Please select a route first", Toast.LENGTH_LONG).show();
                }

        });

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        mapSetup = new MapSetup(this, mapView);
        //mapSetup.getCurrentLocation();
        sourceLocationLatLng = getIntent().getExtras().getParcelable("EXTRA_SOURCE_LOCATION");
        Log.d("JourneyActivity", "SourceLocation: " + sourceLocationLatLng);
        mapSetup.displayMap(sourceLocationLatLng);

        getLines();

    }



    /*@Override
    public void onNavigationReady(boolean isRunning) {
        navigationView.setVisibility(View.VISIBLE);
        DirectionsRoute selectedRoute = mapSetup.getSelectedRoute();
        NavigationViewOptions options = NavigationViewOptions.builder()
                .directionsRoute(selectedRoute)
                .shouldSimulateRoute(false)
                .build();
        navigationView.startNavigation(options);
    }*/

    public void getLines(){
       /* mapSetup = new MapSetup(this, mapView);
        mapSetup.getCurrentLocation();*/

        //create an object of PlanJourney class
        //journey = new PlanJourney(this);


        //ArrayList<PolylineOptions> polylineOptions = new ArrayList<>();

        ArrayList<com.mapbox.geojson.Point> wayPoints;
        ArrayList<com.mapbox.geojson.Point> wayPoints1;
        ArrayList<com.mapbox.geojson.Point> wayPoints2;

        LatLng sourceLocationLatLng = getIntent().getExtras().getParcelable("EXTRA_SOURCE_LOCATION");
        LatLng destinationLocationLatLng = getIntent().getExtras().getParcelable("EXTRA_DESTINATION_LOCATION");



        Point sourceLocation = new Point() {
            @Override
            public int srid() {
                return 0;
            }

            @Override
            public double x() {
                return sourceLocationLatLng.getLongitude();
            }

            @Override
            public double y() {
                return sourceLocationLatLng.getLatitude();
            }

            @Override
            public double z() {
                return 0;
            }
        };

        Point destinationLocation = new Point() {
            @Override
            public int srid() {
                return 0;
            }

            @Override
            public double x() {
                return destinationLocationLatLng.getLongitude();
            }

            @Override
            public double y() {
                return destinationLocationLatLng.getLatitude();
            }

            @Override
            public double z() {
                return 0;
            }
        };

        databaseQuery = new DatabaseQuery(sourceLocation, destinationLocation, this);
        ArrayList<Line> paths = databaseQuery.queryLines(sourceLocation, destinationLocation);

        if (paths.size() == 0){
            Toast.makeText(this, "no lines were found in this location", Toast.LENGTH_LONG).show();
        }else{
        Point sourceStationPoint;
        Point destinationStationPoint;
        for (Line path : paths) {

            line1Name = path.getLine1Name();
            line2Name = path.getLine2Name();

            sourceStationPoint = path.getStartStation();
            destinationStationPoint = path.getEndStation();

            com.mapbox.geojson.Point startStation = com.mapbox.geojson.Point.fromLngLat(sourceStationPoint.x(), sourceStationPoint.y());
            com.mapbox.geojson.Point endStation = com.mapbox.geojson.Point.fromLngLat(destinationStationPoint.x(), destinationStationPoint.y());

            Log.i("JourneyActivity", "start station: " + path.getStartStation() + "\n" +
                    ", end station: " + path.getEndStation() + "\n" +
                    ", line1: " + path.getLine1Name() + "\n" +
                    ", line2: " + path.getLine2Name() + "\n" +
                    ", wayPoints: " + path.getLine1WayPoints() + "\n\n");

            if (line1Name.equals(line2Name)) {
                wayPoints = new ArrayList<>();

                List<Point> points = path.getLine1WayPoints();

                if (points != null)
                    for (Point point : points) {
                        com.mapbox.geojson.Point point1 = com.mapbox.geojson.Point.fromLngLat(point.x(), point.y());
                        wayPoints.add(point1);
                    }
                mapSetup.calculateDirections(startStation, endStation, wayPoints, null, path.getLine1Distance(), null, path.getLine1Duration(), null, path.getLine1Name(), null);
            } else {

                wayPoints1 = new ArrayList<>();
                wayPoints2 = new ArrayList<>();

                List<Point> points1 = path.getLine1WayPoints();
                List<Point> points2 = path.getLine2WayPoints();
                if (points1 != null && points2 != null) {

                    for (Point point : points1) {
                        com.mapbox.geojson.Point point1 = com.mapbox.geojson.Point.fromLngLat(point.x(), point.y());
                        wayPoints1.add(point1);
                    }


                    for (Point point : points2) {
                        com.mapbox.geojson.Point point2 = com.mapbox.geojson.Point.fromLngLat(point.x(), point.y());
                        wayPoints2.add(point2);
                    }
                }

                Log.i("JourneyActivity", "wayPoints1: " + wayPoints1 + "wayPoints2: " + " " + wayPoints2);

                mapSetup.calculateDirections(startStation, endStation, wayPoints1, wayPoints2, path.getLine1Distance(), path.getLine2Distance(), path.getLine1Duration(), path.getLine2Duration(), path.getLine1Name(), path.getLine2Name());
            }
        }
            //for (PolylineOptions polylineOption : polylineOptions) {
            //mapSetup.drawLine(polylineOptions);
            //}
        }
    }

/*    @Override
    public void onNewPrimaryRouteSelected(DirectionsRoute directionsRoute) {
        NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                .directionsRoute(directionsRoute)
                .shouldSimulateRoute(true)
                .build();
// Call this method with Context from within an Activity
        NavigationLauncher.startNavigation(JourneyActivity.this, options);
    }*/

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

    @Override
    public void onMilestoneEvent(RouteProgress routeProgress, String instruction, Milestone milestone) {

    }
}
