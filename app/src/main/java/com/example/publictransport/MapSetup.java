package com.example.publictransport;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.matching.v5.MapboxMapMatching;
import com.mapbox.api.matching.v5.models.MapMatchingResponse;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.ui.v5.route.OnRouteSelectionChangeListener;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapSetup implements PermissionsListener{

    private Context mContext;
    private MapView mMapView;
    private MapboxMap mMapboxMap;
    private LatLng mSourceOrDestLocation;

    private Style mStyle;

    private Point mSourceStation;

    // variables for calculating and drawing a route
    private DirectionsRoute currentRoute;
    private ArrayList<DirectionsRoute> routes;

    private NavigationMapRoute navigationMapRoute;

    private DirectionsRoute selectedRoute;

    private ArrayList<SymbolOptions> symbols1;
    private ArrayList<SymbolOptions> symbols2;

    private SymbolManager symbolManager1;
    private SymbolManager symbolManager2;
    //private Symbol symbol;

    public MapSetup(Context context, MapView mapView){
        mContext = context;
        mMapView = mapView;
        symbols1 = new ArrayList<>();
        symbols2 = new ArrayList<>();
    }

    public MapSetup(Context context){
        mContext = context;
    }

    void displayMap(LatLng location){
        Log.d("MapSetup", "hellooooo???");
        Log.d("MapSetup", "llllooo " + location);
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMap) {
                Log.d("MapSetup", "llllooo " + location);
                mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        Log.d("MapSetup", "llllooo " + location);
                        mMapboxMap = mapboxMap;
                        mStyle = style;
                        setCamera(location);

                        symbolManager1 = new SymbolManager(mMapView, mMapboxMap, mStyle);
                        symbolManager1.setIconAllowOverlap(true);
                        symbolManager1.setTextAllowOverlap(true);

                        symbolManager2 = new SymbolManager(mMapView, mMapboxMap, mStyle);
                        symbolManager2.setIconAllowOverlap(true);
                        symbolManager2.setTextAllowOverlap(true);
                    }
                });
            }
        });
    }

    //same as getLastLocation()
    public void getCurrentLocation(){

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMap) {
                mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {

// Map is set up and the style has loaded. Now you can add data or make other map adjustments.

                        mMapboxMap = mapboxMap;
                        mStyle = style;
                        enableLocationComponent();

                        /*symbolManager1 = new SymbolManager(mMapView, mMapboxMap, mStyle);
                        symbolManager1.setIconAllowOverlap(true);
                        symbolManager1.setTextAllowOverlap(true);

                        symbolManager2 = new SymbolManager(mMapView, mMapboxMap, mStyle);
                        symbolManager2.setIconAllowOverlap(true);
                        symbolManager2.setTextAllowOverlap(true);*/





                        //symbolManager1 = new SymbolManager(mMapView, mMapboxMap, mStyle);

                        /*if (mContext.getClass().getName().equals(JourneyActivity.class.getName())){
                            symbol = symbolManager1.create(new SymbolOptions()
                                    .withLatLng(new LatLng(15.536355, 32.583614))
                                    .withTextField("test...test"));
                        }*/

                        //addAnnotations();

                        /*if (mPolylineOptions != null){
                            for (PolylineOptions polylineOption : mPolylineOptions) {
                                mMapboxMap.addPolyline(polylineOption);
                                //Polyline.setJointType(JointType.ROUND);
                            }
                        }*/


                    }
                });
            }
        });

    }

    private void enableLocationComponent(){
        if (PermissionsManager.areLocationPermissionsGranted(mContext)) {
            // Permission sensitive logic called here, such as activating the Maps SDK's LocationComponent to show the device's location

            // Get an instance of the component
            LocationComponent locationComponent = mMapboxMap.getLocationComponent();

            // Activate with a built LocationComponentActivationOptions object
            locationComponent.activateLocationComponent(LocationComponentActivationOptions.builder(mContext, mStyle).build());

            // Enable to make component visible
            locationComponent.setLocationComponentEnabled(true);

            // Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);

            // Set the component's render mode
            locationComponent.setRenderMode(RenderMode.COMPASS);

            LatLng currentLocation = new LatLng(locationComponent.getLastKnownLocation().getLatitude(),
                    locationComponent.getLastKnownLocation().getLongitude());

            //set camera position and zoom
            setCamera(currentLocation);
        }else {
            setPermission();
        }

    }

    public void setPermission() {
        PermissionsManager permissionsManager = new PermissionsManager(this);
        permissionsManager.requestLocationPermissions((Activity) mContext);
        enableLocationComponent();
        }

        public void setCamera(LatLng location){
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(location)
                    .zoom(15)
                    .build();

            mMapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            mMapboxMap.addOnCameraIdleListener(new MapboxMap.OnCameraIdleListener() {
                @Override
                public void onCameraIdle() {
                    mSourceOrDestLocation = new LatLng(mMapboxMap.getCameraPosition().target.getLatitude(), mMapboxMap.getCameraPosition().target.getLongitude());
                    if (!mContext.getClass().getName().equals(JourneyActivity.class.getName())) {
                        Toast.makeText(mContext, mSourceOrDestLocation.getLatitude() + "" + mSourceOrDestLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    public LatLng getSourceOrDestLocation() {
        return mSourceOrDestLocation;
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {

    }

    @Override
    public void onPermissionResult(boolean granted) {

    }

    void calculateDirections(Point sourceStation, Point destinationStation, ArrayList<Point> wayPoints1, @Nullable ArrayList<Point> wayPoints2, Double line1Distance, @Nullable Double line2Distance, Long line1Duration, @Nullable Long line2Duration, String line1Name, String line2Name){
        routes = new ArrayList<>();

        //mSourceStation = sourceStation;
        ArrayList<Point> waypoints = new ArrayList<>(wayPoints1);
        if (wayPoints2 != null) {
            waypoints.addAll(wayPoints2);
        }
        waypoints.add(sourceStation);
        waypoints.add(destinationStation);


        MapboxMapMatching.Builder builder = MapboxMapMatching.builder()
                .accessToken(mContext.getString(R.string.mapbox_access_token))
                .profile(DirectionsCriteria.PROFILE_DRIVING)
                .coordinates(waypoints)
                .waypointIndices(0, waypoints.size()-1)
                .steps(true)
                .voiceInstructions(true)
                .bannerInstructions(true)
                //.annotations("Hello this is an annotation")
                .tidy(true);
        /*if (wayPoints2 != null){
            builder.coordinates(wayPoints2)
                    .waypointIndices(0, );
        }else builder.waypointIndices(0, wayPoints1.size()-1);*/

                builder.build().enqueueCall(new Callback<MapMatchingResponse>() {
                    @Override
                    public void onResponse(Call<MapMatchingResponse> call, Response<MapMatchingResponse> response) {
                        // You can get the generic HTTP info about the response
                        Log.d("MapSetup", "Response code: " + response.code());
                        if (response.body() == null) {
                            Log.e("MapSetup", "No routes found, make sure you set the right user and access token.");
                            return;
                        } /*else if (response.body().routes().size() < 1) {
                            Log.e("MapSetup", "No routes found");
                            return;
                        }

                        Log.d("MapSetup", "Response: " + response.body().routes().get(0));*/
                        currentRoute = response.body().matchings().get(0).toDirectionRoute();
                        Log.d("MapSetup", "currentRoute: " + currentRoute);


                        routes.add(currentRoute);

// Draw the route on the map
                        /*if (navigationMapRoute != null) {
                            navigationMapRoute.removeRoute();
                        } else {*/
                        if (navigationMapRoute == null) {
                            navigationMapRoute = new NavigationMapRoute(null, mMapView, mMapboxMap, R.style.NavigationMapRoute);

                            selectedRoute = currentRoute;
                        }
                        // }
                        navigationMapRoute.addRoutes(routes);
                        //navigationMapRoute.showAlternativeRoutes(true);

                        navigationMapRoute.setOnRouteSelectionChangeListener(new OnRouteSelectionChangeListener() {
                            @Override
                            public void onNewPrimaryRouteSelected(DirectionsRoute directionsRoute) {
                                Log.i("MapSetup", "There is a route selected!!!!!");
                                selectedRoute = directionsRoute;
                            }
                        });

                        /*Double totalDistance;
                        Long totalTime;
                        if (line2Distance != null){
                            totalDistance = line1Distance+line2Distance;
                            totalTime = line1Duration+line2Duration;
                        }else {
                            totalDistance = line1Distance;
                            totalTime = line1Duration;
                        }*/

                        if (line2Name != null) {
                            symbols1.add(new SymbolOptions()
                                    .withLatLng(new LatLng(sourceStation.latitude(), sourceStation.longitude()))
                                    .withTextField(line1Name + " - " + line2Name + "\n" + Math.round((currentRoute.distance() * 10.0) / 10.0) / 1000 + "Km" + "\n" + Math.round(currentRoute.duration()) / 60 + "min")
                                    .withTextSize(20f));
                            symbolManager1.create(symbols1);
                        }else
                        {
                            symbols1.add(new SymbolOptions()
                                    .withLatLng(new LatLng(sourceStation.latitude(), sourceStation.longitude()))
                                    .withTextField(line1Name + "\n" + Math.round((currentRoute.distance() * 10.0) / 10.0) / 1000 + "Km" + "\n" + Math.round(currentRoute.duration()) / 60 + "min")
                                    .withTextSize(20f));
                            symbolManager1.create(symbols1);
                        }
                    }

                    @Override
                    public void onFailure(Call<MapMatchingResponse> call, Throwable t) {

                    }
                });

       /* *//*NavigationRoute.Builder builder = NavigationRoute.builder(mContext)
                .accessToken(mContext.getString(R.string.mapbox_access_token))
                .origin(sourceStation)
                .destination(destinationStation);*//*

        for (Point wayPoint : wayPoints1) {
            builder.addWaypoint(wayPoint);
        }
        if (wayPoints2 != null) {
            for (Point wayPoint : wayPoints2) {
                builder.addWaypoint(wayPoint);
            }
        }

        builder
                .profile(DirectionsCriteria.PROFILE_DRIVING)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        // You can get the generic HTTP info about the response
                        Log.d("MapSetup", "Response code: " + response.code());
                        if (response.body() == null) {
                            Log.e("MapSetup", "No routes found, make sure you set the right user and access token.");
                            return;
                        } else if (response.body().routes().size() < 1) {
                            Log.e("MapSetup", "No routes found");
                            return;
                        }

                        Log.d("MapSetup", "Response: " + response.body().routes().get(0));
                        currentRoute = response.body().routes().get(0);
                        Log.d("MapSetup", "currentRoute: " + currentRoute);


                        routes.add(currentRoute);

// Draw the route on the map
                        *//*if (navigationMapRoute != null) {
                            navigationMapRoute.removeRoute();
                        } else {*//*
                        if (navigationMapRoute == null) {
                            navigationMapRoute = new NavigationMapRoute(null, mMapView, mMapboxMap, R.style.NavigationMapRoute);

                            selectedRoute = currentRoute;
                        }
                       // }
                        navigationMapRoute.addRoutes(routes);
                        //navigationMapRoute.showAlternativeRoutes(true);

                        navigationMapRoute.setOnRouteSelectionChangeListener(new OnRouteSelectionChangeListener() {
                            @Override
                            public void onNewPrimaryRouteSelected(DirectionsRoute directionsRoute) {
                                Log.i("MapSetup", "There is a route selected!!!!!");
                                selectedRoute = directionsRoute;
                            }
                        });

                        *//*Double totalDistance;
                        Long totalTime;
                        if (line2Distance != null){
                            totalDistance = line1Distance+line2Distance;
                            totalTime = line1Duration+line2Duration;
                        }else {
                            totalDistance = line1Distance;
                            totalTime = line1Duration;
                        }*//*

                            symbols1.add(new SymbolOptions()
                                    .withLatLng(new LatLng(sourceStation.latitude(), sourceStation.longitude()))
                                    .withTextField(Math.round((currentRoute.distance()*10.0) / 10.0)/1000 + "\n" + Math.round(currentRoute.duration())/60));
                            symbolManager1.create(symbols1);*/

                    }

                    /*@Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {

                    }
                });*/
        /*addAnnotations();*/


    void addAnnotations(){
        SymbolManager symbolManager = new SymbolManager(mMapView, mMapboxMap, mStyle);

        // Add symbol at specified lat/lon
        Symbol symbol = symbolManager.create(new SymbolOptions()
        .withLatLng(new LatLng(15.536355, 32.583614))
                .withTextField("test...test"));
    }

    public DirectionsRoute getSelectedRoute(){
        if (selectedRoute !=null)
        return selectedRoute;
        else
            return null;
    }

/*    @Override
    public void onNewPrimaryRouteSelected(DirectionsRoute directionsRoute) {
        NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                .directionsRoute(directionsRoute)
                .shouldSimulateRoute(true)
                .build();
// Call this method with Context from within an Activity
        NavigationLauncher.startNavigation(((Activity) mContext), options);
    }*/

    /*public void drawLine(ArrayList<PolylineOptions> polylineOptions) {
        mPolylineOptions = polylineOptions;
        if (mMapboxMap == null) {
            getCurrentLocation();
        } else {
            for (PolylineOptions polylineOption : mPolylineOptions) {
                mMapboxMap.addPolyline(polylineOption);
                //Polyline.setJointType(JointType.ROUND);
            }
        }
    }*/
}
