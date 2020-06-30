package com.example.publictransport;

import android.content.Context;
import android.graphics.Color;

import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;

import java.util.ArrayList;

public class PlanJourney {

    private Context mContext;

   /* private PolylineOptions polylineOptions1;
    private PolylineOptions polylineOptions2;
    private ArrayList<PolylineOptions> polylineOptionsList;*/

    public PlanJourney(Context context){
        mContext = context;
       /* polylineOptions1 = new PolylineOptions();
        polylineOptions2 = new PolylineOptions();
        polylineOptionsList = new ArrayList<>();*/
    }

    void calculateDirections(Point sourceStation, Point destinationStation, ArrayList<Point> wayPoints1, ArrayList<Point> wayPoints2){

        NavigationRoute.Builder builder = NavigationRoute.builder(mContext)
                .accessToken(mContext.getString(R.string.mapbox_access_token))
                .origin(sourceStation)
                .destination(destinationStation);


       /* //polylineOptionsList = new ArrayList<>();
        polylineOptions1 = new PolylineOptions();
        polylineOptions2 = new PolylineOptions();*/

      /*  for (Point point : wayPoints1){
            builder.addWaypoint(point);
        }*/
        /*polylineOptions1
                .width(10)
                .color(Color.BLUE);*/

        /*for (Point point : wayPoints2){
            builder.addWaypoint(point);
        }*/

        builder.build();
       /* polylineOptions2
                .width(10)
                .color(Color.BLUE);

        polylineOptionsList.add(polylineOptions1);
        polylineOptionsList.add(polylineOptions2);*/
        /*return polylineOptionsList;*/
    }
}
