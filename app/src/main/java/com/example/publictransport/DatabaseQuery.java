package com.example.publictransport;

import android.content.Context;
import android.util.Log;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.types.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DatabaseQuery {

    private Point mSourceLocation;
    private Point mDestinationLocation;
    private Context mContext;

    private ArrayList<String> sourceStationsList;
    private ArrayList<String> destinationStationsList;

    private ArrayList<String> line1 = new ArrayList<>();
    private ArrayList<String> line2 = new ArrayList<>();

    private ArrayList<Map<String,Object>> paths = new ArrayList<>();
    private ArrayList<Line> lines = new ArrayList<>();

    public DatabaseQuery(Point sourceLocation, Point destinationLocation, Context context){
        mSourceLocation = sourceLocation;
        mDestinationLocation = destinationLocation;
        mContext = context;
    }

    public ArrayList<Line> queryLines(Point sourceLocation, Point destinationLocation){
        Log.i("DatabaseQuery", "mSourceLocation: " + sourceLocation.x() + ", " + sourceLocation.y()  + ", " + "mDestinationLocation: " + destinationLocation.x() + ", " + destinationLocation.y());

        Driver driver = GraphDatabase.driver("bolt://192.168.43.154:7687", AuthTokens.basic("neo4j", "123"));
        try(Session session = driver.session()){
            session.readTransaction( tx -> {
                Log.i("DatabaseQuery", "HELLO, anybody here??");
                sourceStationsList = new ArrayList<>();
                destinationStationsList = new ArrayList<>();
                Result result = tx.run("with point({ longitude:" + sourceLocation.x() + ", latitude:" + sourceLocation.y() + "}) AS userLocation,\n"+
                        "point({longitude:" + destinationLocation.x() + ", latitude:" + destinationLocation.y() + "}) AS userDestination\n" +
                        "match (source:Station) WHERE \n" +
                        "distance(point({longitude:source.location.Longitude, latitude:source.location.Latitude }), userLocation) < 500\n" +
                        "match (destination:Station) WHERE \n" +
                        "distance(point({longitude:destination.location.Longitude, latitude:destination.location.Latitude }), userDestination) < 500\n" +
                        "with source, destination\n"+
                        "match (leg1:Leg) -[:STARTS_AT | :ENDS_AT]->(source)\n" +
                        "match (leg1)-[:NEXT_LEG*0..2]-(leg2:Leg)-[:ENDS_AT | :STARTS_AT]->(destination)\n" +
                        "with leg1, leg2, source, destination\n" +
                        "match (line1:Line)-[:HAS_LEG]-(leg1) \n" +
                        "match (line2:Line)-[:HAS_LEG]-(leg2)\n" +
                        "return distinct source.location, destination.location, line1.name, line1.fees, line1.wayPoints, line1.distance, line1.duration,  line2.name, line2.fees, line2.wayPoints, line2.distance, line2.duration"
                );

                while (result.hasNext()) {
                    Map<String,Object> row = result.next().asMap();
                    if (!paths.contains(row)) {
                        paths.add(row);
                    }
                    for (Map.Entry<String,Object> column : row.entrySet()){
                        line1.add(column.getKey() + ": " + column.getValue().toString());
                                /*if (column.getKey().equals("line1.name")){ line1.add(column.getValue().toString()); }
                                else if (column.getKey().equals("line2.name")) { line2.add(column.getValue().toString()); }*/
                        //Log.i("DatabaseQuery", "line1: " + line1 + " ");

                    }
                }

                Log.i("DatabaseQuery", "paths: " + paths + "\n");

                for (Map<String,Object> element : paths) {
                    Point sourceStation = (Point) element.get("source.location");
                    Point destinationStation = (Point) element.get("destination.location");
                    List<Point> line1WayPoints = (List) element.get("line1.wayPoints");
                    List<Point> line2WayPoints = (List) element.get("line2.wayPoints");
                    String line1Name = element.get("line1.name").toString();
                    String line2Name = element.get("line2.name").toString();
                    String line1Fees = element.get("line1.fees").toString();
                    String line2Fees = element.get("line2.fees").toString();
                    Double line1Distance = (Double) element.get("line1.distance");
                    Long line1Duration = (Long) element.get("line1.duration");
                    Double line2Distance = (Double) element.get("line2.distance");
                    Long line2Duration = (Long) element.get("line2.duration");

                    lines.add(new Line(sourceStation, destinationStation, line1WayPoints,line1Distance, line1Duration, line2WayPoints, line1Name, line2Name, line1Fees, line2Fees, line2Distance, line2Duration));
                    Log.i("DatabaseQuery", "lines: " + lines + "\n");
                }

               /* for (String element : line1) {
                    Log.i("DatabaseQuery", element + "\n");
                }*/
                return true;
            });

        }catch (Exception e){
            Log.d("DatabaseQuery", "error: " + e);
        }

        return lines;
    }
}
