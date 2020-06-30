package com.example.publictransport;

import org.neo4j.driver.types.Point;
import java.util.List;

public class Line {

    private Point startStation;
    private Point endStation;
    private List<Point> line1WayPoints;
    private List<Point> line2WayPoints;
    private String line1Name;
    private String line2Name;
    private String line1Fees;
    private String line2Fees;
    private Double line1Distance;
    private Long line1Duration;
    private Double line2Distance;
    private Long line2Duration;

    public Line(Point startStation, Point endStation, List<Point> line1WayPoints, Double line1Distance, Long line1Duration, List<Point> line2WayPoints, String line1Name, String line2Name,  String line1Fees, String line2Fees, Double line2Distance, Long line2Duration) {
        this.startStation = startStation;
        this.endStation = endStation;
        this.line1WayPoints = line1WayPoints;
        this.line2WayPoints = line2WayPoints;
        this.line1Name = line1Name;
        this.line2Name = line2Name;
        this.line1Fees = line1Fees;
        this.line2Fees = line2Fees;
        this.line1Distance = line1Distance;
        this.line1Duration = line1Duration;
        this.line2Distance = line2Distance;
        this.line2Duration = line2Duration;
    }

    public Point getStartStation() {
        return startStation;
    }

    public Point getEndStation() {
        return endStation;
    }

    public List<Point> getLine1WayPoints(){
        return line1WayPoints;
    }

    public List<Point> getLine2WayPoints(){
        return line2WayPoints;
    }

    public String getLine1Name() {
        return line1Name;
    }

    public String getLine2Name() {
        return line2Name;
    }

    public String getLine1Fees() {
        return line1Fees;
    }

    public String getLine2Fees() {
        return line2Fees;
    }

    public Double getLine1Distance() {
        return line1Distance;
    }

    public Long getLine1Duration() {
        return line1Duration;
    }

    public Double getLine2Distance() {
        return line2Distance;
    }

    public Long getLine2Duration() {
        return line2Duration;
    }
}
