package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import static java.lang.Double.parseDouble;

public class ViewTodaysEvents extends AppCompatActivity implements OnMapReadyCallback {

    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    private MapView mMapView;
    GoogleMap mMap;
    LatLngBounds mapBoundary;
    ArrayList<Marker> markersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_todays_events);

        mMapView = findViewById(R.id.event_list_map);

        initGoogleMap(savedInstanceState);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        googleMap.setMyLocationEnabled(true);
        mMap = googleMap;
        try{
            setCameraView();
            addMapMarkers();
        }catch (Exception e)
        {
            Toast.makeText(this, "Error in Add map marker. Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void setCameraView() {
        float longitude, latitude;
        if (Information.gCurrentCoordinates != null){
            String[] arr = Information.gCurrentCoordinates.split(",");
            latitude = Float.parseFloat(arr[0]);
            longitude = Float.parseFloat(arr[1]);
            double bottomBoundary = latitude - .1;
            double leftBoundary = longitude - .1;
            double topBoundary = latitude + .1;
            double rightBoundary = longitude + .1;

            mapBoundary = new LatLngBounds(new LatLng(bottomBoundary,leftBoundary), new LatLng(topBoundary,rightBoundary));

            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mapBoundary,0));
            //Toast.makeText(this, "Current Coordinates are : " + latitude + "," + longitude, Toast.LENGTH_SHORT).show();
        }
    }

    private void initGoogleMap(Bundle savedInstanceState) {
        // *** IMPORTANT ***
        // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
        // objects or sub-Bundles.
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        mMapView.onCreate(mapViewBundle);

        mMapView.getMapAsync(this);
    }

    public void addMapMarkers()
    {
        markersList = new ArrayList<Marker>();
        for (EventHelperClass AllEvents: MainEvent.todaysEventList)
        {
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(AllEvents.eventlocationlatitude,AllEvents.eventlocationlongitude))
                    .title(AllEvents.eventsubject));
            marker.setTag(0);
            markersList.add(marker);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}