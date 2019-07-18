package com.example.android.project;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.example.android.project.login.MainActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public class viewHome extends AppCompatActivity {
    boolean locationpermission = false;
    public static double latitude;
    public static double longitude;
    private static final String TAG = "Home";
    private static final int PERMISSIONS_REQUEST_ENABLE_GPS = 100;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 101;
    private static final int ERROR_DIALOG_REQUEST = 102;
    private FusedLocationProviderClient fusedLocation;
    TextView curlocationtext,setcurloc;
 static int setToCurrentLoc=0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);
        setcurloc=findViewById(R.id.setCurLoc);
        curlocationtext=findViewById(R.id.curLocationText);
        fusedLocation = LocationServices.getFusedLocationProviderClient(this);
    setcurloc.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            setToCurrentLoc=0;
            preferLocation();
        }
    });
    }

private void preferLocation()
{
   if(setToCurrentLoc==0)
    {
        Log.d(TAG, "current");
        getLastLocation();
    }
else
   { if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
       // TODO: Consider calling
       //    Activity#requestPermissions
       // here to request the missing permissions, and then overriding
       //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
       //                                          int[] grantResults)
       // to handle the case where the user grants the permission. See the documentation
       // for Activity#requestPermissions for more details.
       return;
   }
    getdetails();
   }
}
    private void getLastLocation() {

        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        fusedLocation.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
              if(task.isSuccessful()){
                  Location location=task.getResult();
              latitude=location.getLatitude();
                  longitude=location.getLongitude();
               getdetails();
              }
            }
        });
    }
    private void getdetails()
    { Geocoder geocoder=new Geocoder(viewHome.this, Locale.getDefault());
    try{
        List<Address> addresses=geocoder.getFromLocation(latitude,longitude,1);
      Address address=addresses.get(0);
       /* String locationName = address.getAddressLine(0);
        locationName = locationName + "\n" + address.getCountryName();
        locationName = locationName + "\n" + address.getCountryCode();
        locationName = locationName + "\n" + address.getAdminArea();
        locationName = locationName + "\n" + address.getPostalCode();
        locationName = locationName + "\n" + address.getSubAdminArea();
        locationName = locationName + "\n" + address.getLocality();
        locationName = locationName + "\n" + address.getSubThoroughfare();*/
       String locationName=address.getLocality()+","+address.getAdminArea()+","+address.getCountryName();
   Log.v("Address","LocationName"+locationName);
curlocationtext.setText(locationName);
    } catch (IOException e) {
        e.printStackTrace();
    }

    }

    private boolean checkMapServices(){
        if(isServicesOK()){
            if(isMapsEnabled()){
                return true;
            }
        }
        return false;
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public boolean isMapsEnabled(){
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled  callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationpermission = true;
            getChatrooms();
            preferLocation();

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(viewHome.this);

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(viewHome.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        locationpermission = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationpermission = true;
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: called.");
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ENABLE_GPS: {
                if(locationpermission){
                    getChatrooms();
                   preferLocation();
                }
                else{
                    getLocationPermission();
                }
            }
        }

    }
    private void getChatrooms()
    {
        curlocationtext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(viewHome.this,MapsActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(checkMapServices()){
            if(locationpermission){
                getChatrooms();
                preferLocation();
            }
            else{
                getLocationPermission();
            }
        }
    }
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
