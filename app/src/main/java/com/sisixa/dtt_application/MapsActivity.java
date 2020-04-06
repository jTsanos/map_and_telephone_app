package com.sisixa.dtt_application;


import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroupOverlay;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback{




    public static final int PERMISSION_REQUEST_CODE = 9001;
    private static final int PLAY_SERVICES_ERROR_CODE = 9002;
    public static final int GPS_REQUEST_CODE = 9003;
    public static final String TAG = "MapDebug";
    Dialog dialog;
    private GoogleMap mMap;
    private FusedLocationProviderClient mLocationClient;
    private final Handler handler = new Handler();








    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        if(isGPSEnabled()) {
            initGoogleMap();
        }

        // check again if my app runs in mobile...

        View rightPanel = findViewById(R.id.tabletlayout);
        if (rightPanel == null) { // that means that run in mobile



            Button theButton = (Button) findViewById(R.id.btnOne);


            theButton.setBackgroundResource(R.drawable.main_btn_bg);


            theButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog = new Dialog(MapsActivity.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.telephone_pop_up);
                    Window window = dialog.getWindow();
                    WindowManager.LayoutParams wlp = window.getAttributes();

                    wlp.gravity = Gravity.BOTTOM;

                    dialog.show();
                    dialog.setCanceledOnTouchOutside(false);


                }
            });


        } else {  //else run in tablet


            TextView tel = (TextView) findViewById(R.id.telefonika);
            tel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    Intent callIntent = new Intent(Intent.ACTION_CALL); //use ACTION_CALL class
                    callIntent.setData(Uri.parse("tel:+319007788990"));    //this is the phone number calling
                    //check permission

                    if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        //request permission from user if the app hasn't got the required permission
                        ActivityCompat.requestPermissions(MapsActivity.this,
                                new String[]{Manifest.permission.CALL_PHONE},   //request specific permission from user
                                10);
                        return;
                    } else {     //have got permission
                        try {
                            startActivity(callIntent);  //call activity and make phone call
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(getApplicationContext(), "yourActivity is not founded", Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            });


        }


    }










        // CHECK IN WI-FI IS ON
        private boolean checkWifiOn() {
            WifiManager wifiMgr = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

            if (wifiMgr.isWifiEnabled()) {
                return true;
            }
            else {

                AlertDialog alertDialog = new AlertDialog.Builder(this)
                        .setTitle("GPS uitgeschakeld")
                        .setMessage("Uw GPS is uitgeschakeld. Schakel \n het alstublieft in om door te gaan.")
                        .setNegativeButton("ANNULEREN", ((dialogInterface, i) -> {
                            Intent intent = new Intent(MapsActivity.this, MainActivity.class);

                            startActivity(intent);

                        }))
                        .setPositiveButton("AANZETTEN", ((dialogInterface, i) -> {

                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));

                        }))
                        .setCancelable(false)

                        .show();
            }
            return true;
        }










    //initialize GoogleMap
    private void initGoogleMap() {

    if(checkWifiOn()) {


            if (isServicesOk()) {


                if (checkLocationPermission()) {
                    SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.map);

                    supportMapFragment.getMapAsync(MapsActivity.this);


                } else {
                    requestLocationPermission();


                }

            }

    }
}









    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: map is showing on the screen");

        mMap = googleMap;


            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            mLocationClient = LocationServices.getFusedLocationProviderClient(this);
            mLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    // TODO: Consider calling
                                    //    Activity#requestPermissions
                                    // here to request the missing permissions, and then overriding
                                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                    //                                          int[] grantResults)
                                    // to handle the case where the user grants the permission. See the documentation
                                    // for Activity#requestPermissions for more details.
                                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.INTERNET}
                                            ,PERMISSION_REQUEST_CODE);

                                    return;
                                }
                            }




                            if (location != null) {  //CHECK IF LOCATION = NULL


                                double latitude = location.getLatitude();
                                double longitude = location.getLongitude();



                              LatLng current = new LatLng(latitude, longitude);
                                CameraUpdate center =
                                        CameraUpdateFactory.newLatLng(new LatLng(latitude,
                                                longitude));
                                CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);

                                mMap.moveCamera(center);
                                mMap.animateCamera(zoom);



                                final Marker marker = mMap.addMarker(new MarkerOptions()
                                        .position(current)

                                        .icon(BitmapDescriptorFactory
                                                .fromResource(R.drawable.map_marker)));





                                Geocoder geocoder;
                                final List<Address> addresses;
                                geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());

                                try {
                                    addresses = geocoder.getFromLocation(latitude, longitude, 1);


                                    final String address = addresses.get(0).getAddressLine(0);
                                    final String phone = addresses.get(0).getPhone();


                                    mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                                        // Use default InfoWindow frame
                                        @Override
                                        public View getInfoWindow(Marker arg0) {
                                            // Getting view from the layout file custom_info_window
                                            View v = getLayoutInflater().inflate(R.layout.custom_info_window, null);



                                            // Getting reference to the TextView to set address
                                            TextView addressTXT = (TextView) v.findViewById(R.id.address);
                                            // Getting reference to the TextView to set phone
                                            TextView phoneTXT = (TextView) v.findViewById(R.id.phone);


                                            addressTXT.setText(address);

                                            if (phone == null) {
                                                phoneTXT.setText("Onthoud deze locatie voor het \n             telefoongesprek.");
                                            } else {
                                                phoneTXT.setText(phone);
                                            }

                                            // Returning the view containing InfoWindow contents
                                            return v;

                                        }

                                        // Defines the contents of the InfoWindow
                                        @Override
                                        public View getInfoContents(Marker arg0) {

                                            return null;

                                        }
                                    });

                                }
                                catch (IOException e) {
                                    e.printStackTrace();
                                }




                                //MAKE INFO WINDOW ALWAYS VISIBLE
                                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                                    @Override
                                    public void onMapClick(LatLng arg0) {
                                        marker.showInfoWindow();
                                    }
                                });

                                marker.showInfoWindow();

                                }
                            }


                    });



}











//CHECK IF GPS IS ENABLED
    private boolean isGPSEnabled() {

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        boolean providerEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (providerEnabled) {
            return true;
        } else {

            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle("GPS Permissions")
                    .setMessage("GPS is required for this app to work. Please enable GPS.")
                    .setNegativeButton("No", ((dialogInterface, i) -> {
                        Intent intent = new Intent(MapsActivity.this, MainActivity.class);

                        startActivity(intent);

                    }))
                    .setPositiveButton("Yes", ((dialogInterface, i) -> {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);

                        startActivityForResult(intent, GPS_REQUEST_CODE);



                        doTheAutoRefresh();

                    }))
                    .setCancelable(false)

                    .show();



        }


        return true;
    }








    private boolean checkLocationPermission() {

        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }









    private boolean isServicesOk() {

        GoogleApiAvailability googleApi = GoogleApiAvailability.getInstance();

        int result = googleApi.isGooglePlayServicesAvailable(this);

        if (result == ConnectionResult.SUCCESS) {
            return true;
        } else if (googleApi.isUserResolvableError(result)) {
            Dialog dialog = googleApi.getErrorDialog(this, result, PLAY_SERVICES_ERROR_CODE, task ->
                    Toast.makeText(this, "Dialog is cancelled by User", Toast.LENGTH_SHORT).show());
            dialog.show();
        } else {
            Toast.makeText(this, "Play services are required by this application", Toast.LENGTH_SHORT).show();
        }
        return true;
    }








    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
            }
        }
    }







    private void doTheAutoRefresh() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Write code for your refresh logic
                recreate();
            }
        }, 3000);
    }









    // Mobile button OnClick Method to call
    public void btnCall(View view){
        Intent callIntent = new Intent(Intent.ACTION_CALL); //use ACTION_CALL class
        callIntent.setData(Uri.parse("tel:+319007788990"));    //this is the phone number calling
        //check permission
        if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            //request permission from user if the app hasn't got the required permission
            ActivityCompat.requestPermissions(MapsActivity.this,
                    new String[]{Manifest.permission.CALL_PHONE},   //request specific permission from user
                    10);
            return;
        }else {     //have got permission
            try{
                startActivity(callIntent);  //call activity and make phone call
            }
            catch (android.content.ActivityNotFoundException ex){
                Toast.makeText(getApplicationContext(),"yourActivity is not founded",Toast.LENGTH_SHORT).show();
            }
        }

    }






// X Button OnClick from telephone_pop_up layout
public  void btnBack(View view){

   dialog.dismiss();

}

/*    private Location getLastKnownLocation() {
        mLocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.INTERNET}
                            ,10);

                    return null;
                }
            }
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }








private void gotoLocation(double lat, double lng) {

        LatLng latLng = new LatLng(lat, lng);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM);

        mMap.moveCamera(cameraUpdate);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

    }


 */


}

