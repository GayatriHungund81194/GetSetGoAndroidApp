package karkinos.gayatri.com.transportation;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.maps.android.PolyUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap googMap;
    EditText searchtext;
    public static String destination;
    String googleDirectionsApiKey = "ENTER_YOUR_API_KEY_HERE";
    Button searchbutton;
    Button zoomIn;
    Button nearby;
    Button zoomOut;
    public double latitude;
    public double longitude;
    public LocationListener locationManager;
    public static Double[] ll = new Double[2];

    public static Double[] getll() {
        return ll;
    }

    public static String destination() {
        return destination;
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onLocationChanged(Location location) {

        latitude = location.getLatitude();
        longitude = location.getLongitude();

        Toast.makeText(MapsActivity.this, "Latitude:" + latitude + " Longitude:" + longitude, Toast.LENGTH_LONG).show();


    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        searchtext = (EditText)findViewById(R.id.searchText);
        searchbutton = (Button)findViewById(R.id.searchButton);
        zoomIn = (Button)findViewById(R.id.zoomInButton);
        zoomOut = (Button)findViewById(R.id.zoomOutButton);
        nearby = (Button)findViewById(R.id.searchNearbyBusStop);



    }




    public void nearbyPlaces(View view){

        //this.destination  = searchtext.getText().toString();

        Double myLat = googMap.getMyLocation().getLatitude();
        Double mylng = googMap.getMyLocation().getLongitude();

        String nearby_route = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+myLat.toString()+","+mylng.toString()+"&radius=1500&type=bus_station&key="+googleDirectionsApiKey;

        final RequestQueue queue = Volley.newRequestQueue(this);

        final StringRequest req2 = new StringRequest(Request.Method.GET, nearby_route, new Response.Listener<String>() {
            String[] nearbyBusStops = null;
            String latb = null;
            String lngb = null;
            int numbus = 0;
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject j = new JSONObject(response.toString());
                    numbus  = j.getJSONArray("results").length();
                    for (int i=0;i<numbus;i++){
                        Log.d("Inside ","saa");
                        latb = j.getJSONArray("results").getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getString("lat");
                        lngb = j.getJSONArray("results").getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getString("lng");
                        Log.d("Inside ","saa"+latb);
                        Log.d("Inside ","saa"+lngb);
                        double latitude = Double.parseDouble(latb);
                        double longitude = Double.parseDouble(lngb);
                        LatLng latlong = new LatLng(latitude,longitude);
                        Marker m=googMap.addMarker(new MarkerOptions().position(latlong).title("Bus Station"));
                        m.showInfoWindow();
                        googMap.animateCamera(CameraUpdateFactory.newLatLng(latlong));



                    }


                }catch (Exception e){

                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(req2);



        googMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Double latitude = marker.getPosition().latitude;
                Double longitude = marker.getPosition().longitude;

                ll[0] = latitude;
                ll[1] = longitude;

                Log.d("LAT","LLL"+ll[0]);
                Log.d("LNG","LLL"+ll[1]);

                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));



             return false;

            }
        });



    }

    public void zoomFunctionality(View view){

        if(view.getId() == R.id.zoomInButton){
            googMap.animateCamera(CameraUpdateFactory.zoomIn());
        }
        else{
            googMap.animateCamera(CameraUpdateFactory.zoomOut());
        }

    }





    public void searchPlace(View view){
        String searchLoaction = searchtext.getText().toString();
        this.destination  = searchtext.getText().toString();
        Double myLat = googMap.getMyLocation().getLatitude();
        Double mylng = googMap.getMyLocation().getLongitude();


        startActivity(new Intent(MapsActivity.this, Route.class));


        Geocoder geocoder = new Geocoder(this);
        List<Address> addrList = new ArrayList<Address>();
        if (searchLoaction == null || searchLoaction == "") {
            Toast.makeText(this,"Please enter the location",Toast.LENGTH_LONG).show();
        }
        else {
            try {
               addrList= geocoder.getFromLocationName(searchLoaction, 1);
                Address match = (addrList.isEmpty() ? null : addrList.get(0));
               if(match==null){
                   Toast.makeText(this,"Location not found",Toast.LENGTH_LONG).show();
               }
               else{
                   Address addr = addrList.get(0);
                   LatLng latlong = new LatLng(addr.getLatitude(),addr.getLongitude());
                   googMap.addMarker(new MarkerOptions().position(latlong).title("Destination"));
                   googMap.animateCamera(CameraUpdateFactory.newLatLng(latlong));


               }



            }catch (Exception e){
                Toast.makeText(this,"Location not found",Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }


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
    @Override
    public void onMapReady(GoogleMap googleMap) {
        googMap = googleMap;

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            googMap.setMyLocationEnabled(true);



        } else {
            Toast.makeText(MapsActivity.this, "Turn on the location services", Toast.LENGTH_LONG).show();
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                googMap.setMyLocationEnabled(true);

            }


        }
    }
}
