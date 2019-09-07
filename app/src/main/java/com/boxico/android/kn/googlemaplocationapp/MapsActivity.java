package com.boxico.android.kn.googlemaplocationapp;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.PolyUtil;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Location mLastKnownLocation = null;

    private final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 101;

    private boolean mLocationPermissionGranted = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Polygon polygon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        AppCompatButton button = this.findViewById(R.id.alerta);
        final TextView t = this.findViewById(R.id.coordenadas);
        final MapsActivity me = this;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOnlyDeviceLocation();
                boolean respuesta = PolyUtil.containsLocation(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude(),polygon.getPoints(),false);
                if(respuesta){
                   t.setText(t.getText() + "- ESTA ADENTRO");

                }else{
                    t.setText(t.getText() + "- ESTA AFUERA");
                }
            }
        });


    }


    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
        //    Log.e("Exception: %s", e.getMessage());
        }
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
    /*
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(0, 0);
        mMap.addMarker(new MarkerOptions().position(sydney).title("No se donde estoy"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
*/

    private void getOnlyDeviceLocation() {

        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();

                        } else {
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }

    }

    private void getDeviceLocation() {

        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            mostrarCoordenadas();
                        } else {
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }

    }




    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        // Do other setup activities here too, as described elsewhere in this tutorial.
    /*    LatLng sydney = new LatLng(0, 0);
        mMap.addMarker(new MarkerOptions().position(sydney).title("No se donde estoy"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();

    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
           // this.mostrarCoordenadas();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private void mostrarCoordenadas() {

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(mLastKnownLocation.getLatitude(),
                        mLastKnownLocation.getLongitude()), 20));

        Marker mPosition = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()))
                .title("Current Location"));


        Marker mPosition1 = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(mLastKnownLocation.getLatitude() +0.001 , mLastKnownLocation.getLongitude() + 0.001))
                .title("Punto Cerca 1"));
        Marker mPosition2 = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(mLastKnownLocation.getLatitude() -0.001 , mLastKnownLocation.getLongitude() + 0.001))
                .title("Punto Cerca 2"));
        Marker mPosition3 = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(mLastKnownLocation.getLatitude() +0.001 , mLastKnownLocation.getLongitude() - 0.001))
                .title("Punto Cerca 3"));
        Marker mPosition4 = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(mLastKnownLocation.getLatitude() -0.001 , mLastKnownLocation.getLongitude() - 0.001))
                .title("Punto Cerca 3"));
        polygon = mMap.addPolygon(new PolygonOptions()
                .add(new LatLng(mLastKnownLocation.getLatitude() +0.0001,  mLastKnownLocation.getLongitude() + 0.0001))
                .add(new LatLng(mLastKnownLocation.getLatitude() +0.0001 , mLastKnownLocation.getLongitude() - 0.0001))
                .add(new LatLng(mLastKnownLocation.getLatitude() -0.0001 , mLastKnownLocation.getLongitude() - 0.0001))
                .add(new LatLng(mLastKnownLocation.getLatitude() -0.0001 , mLastKnownLocation.getLongitude() + 0.0001))
                .strokeColor(Color.RED)
                .fillColor(0x5500ff00));


        TextView t = this.findViewById(R.id.coordenadas);
        t.setText("Latitud:" + String.valueOf(mLastKnownLocation.getLatitude()) + "Longitud:" + String.valueOf(mLastKnownLocation.getLongitude()));

    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }
}
