package com.carlRodriguezThesis.googleMapsTotalCostCalculator;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.carlRodriguezThesis.googleMapsTotalCostCalculator.POJO.Distance;
import com.carlRodriguezThesis.googleMapsTotalCostCalculator.POJO.Duration;
import com.carlRodriguezThesis.googleMapsTotalCostCalculator.POJO.Example;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Started by Navneet, modified by Carl Rodriguez
 */
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LatLng origin;
    LatLng dest;
    ArrayList<LatLng> MarkerPoints;
    TextView ShowDistanceDuration;
    ArrayList<Polyline> lines = new ArrayList<>();
    DataHolder holder = DataHolder.getInstance();
    boolean haveData = false;

    final String WALK = "walking";
    final String DRIVE = "driving";
    final String BIKE = "bicycling";

    String flowTag = "asdf";
    String ret = "ret";
    String onMap = "onMapReady";

    boolean choosingOrg = true;
    boolean orgSet = false;
    boolean destSet = false;




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d("asdf","OnCreate");
        super.onCreate(savedInstanceState);
        setTitle("asdf");
        SharedPreferences sharedPref = getSharedPreferences(getString(com.carlRodriguezThesis.googleMapsTotalCostCalculator.R.string.settingPref),0);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString(getString(com.carlRodriguezThesis.googleMapsTotalCostCalculator.R.string.unitPref), "imperial");
        editor.putString(getString(com.carlRodriguezThesis.googleMapsTotalCostCalculator.R.string.weight), "180");
        editor.putString(getString(com.carlRodriguezThesis.googleMapsTotalCostCalculator.R.string.mpg), "26");

        editor.commit();

        setContentView(com.carlRodriguezThesis.googleMapsTotalCostCalculator.R.layout.activity_maps);

        //ShowDistanceDuration = (TextView) findViewById(R.id.show_distance_time);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        // Initializing
        MarkerPoints = new ArrayList<>();

        //show error dialog if Google Play Services not available
        if (!isGooglePlayServicesAvailable()) {
            Log.d("onCreate", "Google Play Services not available. Ending Test case.");
            finish();
        }
        else {
            Log.d("onCreate", "Google Play Services available. Continuing.");
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(com.carlRodriguezThesis.googleMapsTotalCostCalculator.R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in  WU and move the camera
        if(!holder.havePos()) {
            origin = new LatLng(44.935971, -123.031860);
            dest  = new LatLng(44.941758, -123.026426);

            holder.setStart(origin);
            holder.setDest(dest);
        }
        else {
            origin = holder.getStart();
            dest = holder.getDest();
        }


        MarkerOptions optionsStart = new MarkerOptions();
        optionsStart.position(origin);
        optionsStart.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

        mMap.addMarker(optionsStart);
        orgSet = true;
        MarkerPoints.add(origin);

        MarkerOptions optionsDest = new MarkerOptions();
        optionsDest.position(dest);
        optionsDest.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

        mMap.addMarker(optionsDest);
        destSet = true;

        MarkerPoints.add(dest);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(origin));
        int extraZoom = 0;
        if(holder.haveDistance()){
             double distance = holder.getDistance()[0].getValue();
            extraZoom = (int)Math.round(-(Math.log(distance)/Math.log(2.2)-7));
        }
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15+extraZoom));


        // Setting onclick event listener for the map
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {
                haveData = false;
                // clearing map and generating new marker points if user clicks on map more than two times

                if (choosingOrg == orgSet || choosingOrg != destSet) {

                    clearPaths();
                }

                // Adding new item to the ArrayList

                    if(choosingOrg){
                        Log.d("asdf","point Added");
                        MarkerPoints.add(0,point);
                        orgSet = true;
                        Log.d("asdf","point Added");
                    }
                    else
                    {
                        Log.d("asdf","point Added");
                        MarkerPoints.add(1,point);
                        destSet = true;
                        Log.d("asdf","point Added");
                    }


                // Creating MarkerOptions
                MarkerOptions options = new MarkerOptions();

                // Setting the position of the marker
                options.position(point);

                /**
                 * For the start location, the color of marker is GREEN and
                 * for the end location, the color of marker is RED.
                 */
                if (choosingOrg) {
                    options.icon(BitmapDescriptorFactory.
                            defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                } else {
                    options.icon(BitmapDescriptorFactory.
                            defaultMarker(BitmapDescriptorFactory.HUE_RED));
                }
                Log.d("asdf","Options set");



                // Add new marker to the Google Map Android API V2
                mMap.addMarker(options);
                Log.d("asdf","Marker Added");
                // Checks, whether start and end locations are captured
                if(choosingOrg){

                    Log.d("asdf","Starting choosing org");
                    origin = MarkerPoints.get(0);
                    holder.setStart(origin);
                    Log.d("asdf","Choosing Org done");
                }else{
                    try{
                        Log.d("asdf","Starting choosing dest");
                        dest = MarkerPoints.get(1);
                        holder.setDest(dest);
                        Log.d("asdf","Choosing Org done");
                    }catch (Exception e){
                        Log.d("asdf",e.toString());
                    }

                }
                Log.d("asdf","Origin/Dest set");


                choosingOrg = !choosingOrg;
                Log.d("asdf","setting colors");
                changeSelectColors();
            }
        });

        doButtonListeners();
        changeSelectColors();
    }

    private void changeSelectColors(){
        Button btnDest = (Button) findViewById(com.carlRodriguezThesis.googleMapsTotalCostCalculator.R.id.btnDest);
        Button btnOrg = (Button) findViewById(com.carlRodriguezThesis.googleMapsTotalCostCalculator.R.id.btnOrg);
        if(choosingOrg){
            btnOrg.setTextColor(Color.RED);
            btnDest.setTextColor(Color.BLACK);
        }
        else{
            btnOrg.setTextColor(Color.BLACK);
            btnDest.setTextColor(Color.RED);
        }
    }
    private void doButtonListeners(){
        Button btnAll = (Button) findViewById(com.carlRodriguezThesis.googleMapsTotalCostCalculator.R.id.btnAll);
        btnAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                build_retrofit_and_get_response("driving", true);
                build_retrofit_and_get_response("walking", true );
                build_retrofit_and_get_response("bicycling", true);
                haveData = true;
            }
        });

        Button btnClear = (Button) findViewById(com.carlRodriguezThesis.googleMapsTotalCostCalculator.R.id.btnClear);
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearPaths();
            }
        });

        Button btnData = (Button) findViewById(com.carlRodriguezThesis.googleMapsTotalCostCalculator.R.id.btnData);

        btnData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("asdf",haveData+"");
                if(haveData)
                {
                    Intent myItent = new Intent(MapsActivity.this,DataActivity.class);
                    startActivity(myItent);
                }
            }
        });

        Button btnOrg = (Button) findViewById(com.carlRodriguezThesis.googleMapsTotalCostCalculator.R.id.btnOrg);
        btnOrg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosingOrg = true;
                changeSelectColors();

            }
        });

        Button btnDest = (Button) findViewById(com.carlRodriguezThesis.googleMapsTotalCostCalculator.R.id.btnDest);
        btnDest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosingOrg = false;
                changeSelectColors();

            }
        });


    }

    private void clearPaths(){
        mMap.clear();
        holder.clear();
        haveData  = false;
        orgSet = false;
        destSet = false;
        origin = null;
        dest = null;
    }

    private void build_retrofit_and_get_response(final String type, final boolean draw) {

        String url = "https://maps.googleapis.com/maps/";
        Retrofit retrofit;
        try{
            retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            RetrofitMaps service = retrofit.create(RetrofitMaps.class);


            if(origin == null || dest == null)
            {
                //Raise note to user
                return;
            }
            Call<Example> call = service.getDistanceDuration("imperial", origin.latitude + ","
                    + origin.longitude,dest.latitude + "," + dest.longitude, type);
            Log.d("asdf","asdfasdf");
            call.enqueue(new Callback<Example>() {

                @Override
                public void onResponse(Response<Example> response, Retrofit retrofit) {
                    try {
                        // This loop will go through all the results and add marker on each location.
                        Log.d("show",response.body().getRoutes().size()+"");
                        for (int i = 0; i < response.body().getRoutes().size(); i++) {

                            Distance distance = response.body().getRoutes().get(i).getLegs().get(i).getDistance();
                            Duration time = response.body().getRoutes().get(i).getLegs().get(i).getDuration();
                            //ShowDistanceDuration.setText("Distance:" + distance + ", Duration:" + time);
                            String encodedString = response.body().getRoutes().get(0).getOverviewPolyline().getPoints();
                            List<LatLng> list = decodePoly(encodedString);
                            int routeColor;
                            if(type.equals(WALK)){
                                routeColor = Color.GREEN;

                                holder.setTime(time,2);
                                holder.setDistance(distance,2);

                            }
                            else if(type.equals(BIKE))
                            {
                                routeColor = Color.BLUE;
                                holder.setTime(time,1);
                                holder.setDistance(distance,1);
                            }
                            else{
                                routeColor = Color.RED;
                                holder.setTime(time,0);
                                holder.setDistance(distance,0);
                            }
                            if(draw){
                                lines.add(mMap.addPolyline(new PolylineOptions()
                                        .addAll(list)
                                        .width(20)
                                        .color(routeColor)
                                        .geodesic(true)
                                ));
                            }


                        }

                    } catch (Exception e) {
                        Log.d("asdf", "There is an error");
                        Log.d("asdf",e.toString());
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(Throwable t) {
                    Log.d("asdf", t.toString());
                }

            });
        } catch (Exception e){
            Log.d("asdf",e.toString());
        }



    }

    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng( (((double) lat / 1E5)),
                    (((double) lng / 1E5) ));
            poly.add(p);
        }

        return poly;
    }

    // Checking if Google Play Services Available or not
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if(result != ConnectionResult.SUCCESS) {
            if(googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result,
                        0).show();
            }
            return false;
        }
        return true;
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

}