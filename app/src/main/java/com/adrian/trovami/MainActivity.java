package com.adrian.trovami;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback {

    private static final int ACCESS_FINE_LOCATION_PERMISSION_REQUEST_CODE = 0;
    private GoogleApiClient googleApiClient;
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private Location userLocation;
    private ArrayList<Taco> tacos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        tacos= new ArrayList<>();
        tacos.add(new Taco(19.549781, -99.236493,"Tacos Rocky's"));
        tacos.add(new Taco(19.556858, -99.234690,"Tacos Don Javier"));
        tacos.add(new Taco(19.556333, -99.239840,"Taqueria la modelo"));
        tacos.add(new Taco(19.560538, -99.241728,"Tacos El Tio"));
        tacos.add(new Taco(19.556454, -99.242973,"Taqueria los primos"));
    }

    @Override
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (checkSelfPermission(ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                Location userLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                getUserLastLocation(userLocation);

            }else{
                final String[] permissions = new String[]{ACCESS_FINE_LOCATION};
                requestPermissions(permissions, ACCESS_FINE_LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            Location userLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            getUserLastLocation(userLocation);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == ACCESS_FINE_LOCATION_PERMISSION_REQUEST_CODE){
            if (grantResults.length > 0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                googleApiClient.reconnect();
            }else if(shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Acceder a la ubicación del telefono");
                builder.setMessage("Debes aceptar este permiso para utlizar la app Trovami");
                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final String[] permissions = new String[]{ACCESS_FINE_LOCATION};
                        requestPermissions(permissions, ACCESS_FINE_LOCATION_PERMISSION_REQUEST_CODE);
                    }
                });
                builder.show();
            }
        }
    }

    private void getUserLastLocation(Location userLocation) {
        if(userLocation!=null){
            String longitude= String.valueOf(userLocation.getLongitude());
            String latitude= String.valueOf(userLocation.getLatitude());
            this.userLocation = userLocation;
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        BitmapDescriptor tacoMarketIcon = BitmapDescriptorFactory.fromResource(R.drawable.taco);

        LatLng userCoordinates = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
        mMap.addMarker(new MarkerOptions().position(userCoordinates).title("User's Location"));

        for (Taco taco: tacos) {
            mMap.addMarker(new MarkerOptions().position(new LatLng(taco.getLatitude(),taco.getLongitude()))
            .title(taco.getFlavor())
            .icon(tacoMarketIcon));
        }
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(userCoordinates));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userCoordinates, 12));
    }
}