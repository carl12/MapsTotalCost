package com.carlRodriguezThesis.googleMapsTotalCostCalculator;

import com.carlRodriguezThesis.googleMapsTotalCostCalculator.POJO.Distance;
import com.carlRodriguezThesis.googleMapsTotalCostCalculator.POJO.Duration;
import com.google.android.gms.maps.model.LatLng;


public class DataHolder {
    private Distance distance[] = new Distance[3];
    public Distance[] getDistance(){return distance;}
    public void setDistance(Distance distIn, int num){
        distance[num] = distIn;
    }

    public boolean haveDistance(){
        return distance[0] != null && distance[1] != null && distance[2] != null;
    }

    private Duration[] time = new Duration[3];
    public Duration[] getTime(){return time;}
    public void setTime(Duration timeIn, int num){
        time[num] = timeIn;
    }


    private LatLng start;
    public void setStart(LatLng startIn){start = startIn;}
    public LatLng getStart(){return start;}

    private LatLng dest;
    public void setDest(LatLng destIn){dest = destIn;}
    public LatLng getDest(){return dest;}

    public boolean havePos(){
        return start != null && dest != null;

    }
    public void clear(){
        distance = new Distance[3];
        time = new Duration[3];
        start = null;
        dest = null;
    }

    private static final DataHolder holder = new DataHolder();

    public static DataHolder getInstance(){return holder;}




}
