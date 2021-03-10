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

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int ACCESS_FINE_LOCATION_PERMISSION_REQUEST_CODE = 0;
    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
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
    public void onConnected(@Nullable Bundle bundle) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (checkSelfPermission(ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                Location userLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

                if(userLocation!=null){
                    TextView locationTextView= (TextView) findViewById(R.id.main_activity_location_textView);
                    String longitude= String.valueOf(userLocation.getLongitude());
                    String latitude= String.valueOf(userLocation.getLatitude());

                    locationTextView.setText("Longitude: "+ longitude+" .Latitude: "+latitude);
                }
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
                    public void onClick(DialogInterface dialog, int which) {
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
            TextView locationTextView= (TextView) findViewById(R.id.main_activity_location_textView);
            String longitude= String.valueOf(userLocation.getLongitude());
            String latitude= String.valueOf(userLocation.getLatitude());

            locationTextView.setText("Longitude: "+ longitude+" .Latitude: "+latitude);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}