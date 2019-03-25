package karkinos.gayatri.com.transportation;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public class Route extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap googMap;

    Button zoomIn1;
    Button zoomOut1;
    String googlePlacesApiKey = "ENTER_YOUR_API_KEY_HERE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        zoomIn1 = (Button)findViewById(R.id.zoomInButton1);
        zoomOut1 = (Button)findViewById(R.id.zoomOutButton2);


    }

    public void zoomFunctionality1(View view){

        if(view.getId() == R.id.zoomInButton1){
            googMap.animateCamera(CameraUpdateFactory.zoomIn());
            Log.d("DDDDLAT",""+MapsActivity.ll[0]);
            Log.d("DDDDLNG",""+MapsActivity.ll[1]);
        }
        else{
            googMap.animateCamera(CameraUpdateFactory.zoomOut());
        }

    }


    public void showRoute(View view){

        String dest = MapsActivity.destination();

        Double[] src = MapsActivity.getll();
        String lat = src[0].toString();
        String ln = src[1].toString();
        String total = lat+","+ln;
        String url_route = "https://maps.googleapis.com/maps/api/directions/json?origin="+total+"&destination="+dest+"&mode=transit&key="+googlePlacesApiKey;

        final RequestQueue queue = Volley.newRequestQueue(this);


        final StringRequest req = new StringRequest(Request.Method.GET, url_route, new Response.Listener<String>() {
            String pName = null;
            int stepsArray=0;

            String travelMode =null;
            String startlat=null;
            String startlng=null;
            String endlat=null;
            String endlng=null;
            String latLng = null;
            String lng = null;
            @Override
            public void onResponse(String response) {
                String arr[] = response.split(",");
                try {

                    JSONObject j = new JSONObject(response.toString());
                    stepsArray = j.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps").length();

                    for (int i =0;i<stepsArray;i++){

                        travelMode = j.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps").getJSONObject(i).getString("travel_mode");

                    if (travelMode.equals("WALKING")){

                        startlat = j.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps").getJSONObject(i).getJSONObject("start_location").getString("lat");
                        startlng = j.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps").getJSONObject(i).getJSONObject("start_location").getString("lng");

                        double latitude = Double.parseDouble(startlat);
                        double longitude = Double.parseDouble(startlng);
                        LatLng latlong = new LatLng(latitude,longitude);
                        Marker m=googMap.addMarker(new MarkerOptions().position(latlong).title("Bus Station"));
                        m.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                        m.showInfoWindow();

                        String poly = j.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps").getJSONObject(i).getJSONObject("polyline").getString("points");
                        PolylineOptions plo = new PolylineOptions();
                        plo.color(Color.BLUE);
                        plo.width(15);
                        List<PatternItem> pattern = Arrays.<PatternItem>asList(new Dot(), new Gap(20), new Dash(20), new Gap(20));
                        plo.pattern(pattern);
                        plo.jointType(JointType.ROUND);
                        plo.addAll(PolyUtil.decode(poly));
                        googMap.addPolyline(plo);

                    }

                    if (travelMode.equals("TRANSIT")){

                        startlat = j.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps").getJSONObject(i).getJSONObject("start_location").getString("lat");
                        startlng = j.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps").getJSONObject(i).getJSONObject("start_location").getString("lng");

                        String busDetails = j.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps").getJSONObject(i).getString("html_instructions");
                        String busno = j.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps").getJSONObject(i).getJSONObject("transit_details").getJSONObject("line").getString("short_name");


                        Log.d("Bus Name",""+busDetails);
                        Log.d("Bus no.",""+busno);

                        double latitude = Double.parseDouble(startlat);
                        double longitude = Double.parseDouble(startlng);
                        LatLng latlong = new LatLng(latitude,longitude);
                        Marker m=googMap.addMarker(new MarkerOptions().position(latlong).title("Bus: "+busDetails+" Bus No.: "+busno));
                        m.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                        m.showInfoWindow();

                        String poly = j.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps").getJSONObject(i).getJSONObject("polyline").getString("points");
                            PolylineOptions plo = new PolylineOptions();
                            plo.color(Color.GREEN);
                            plo.width(15);
                            List<PatternItem> pattern = Arrays.<PatternItem>asList(new Dot(), new Gap(20), new Dash(20), new Gap(20));
                            plo.pattern(pattern);
                            plo.jointType(JointType.ROUND);
                            plo.addAll(PolyUtil.decode(poly));
                            googMap.addPolyline(plo);

                        }

                        if (travelMode.equals("DRIVING")){


                            String poly = j.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps").getJSONObject(i).getJSONObject("polyline").getString("points");
                            PolylineOptions plo = new PolylineOptions();
                            plo.color(Color.YELLOW);
                            plo.width(15);
                            List<PatternItem> pattern = Arrays.<PatternItem>asList(new Dot(), new Gap(20), new Dash(20), new Gap(20));
                            plo.pattern(pattern);
                            plo.jointType(JointType.ROUND);
                            plo.addAll(PolyUtil.decode(poly));
                            googMap.addPolyline(plo);


                        }

                        if (travelMode.equals("BICYCLING")){



                            String poly = j.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps").getJSONObject(i).getJSONObject("polyline").getString("points");
                            PolylineOptions plo = new PolylineOptions();
                            plo.color(Color.RED);
                            plo.width(15);
                            List<PatternItem> pattern = Arrays.<PatternItem>asList(new Dot(), new Gap(20), new Dash(20), new Gap(20));
                            plo.pattern(pattern);
                            plo.jointType(JointType.ROUND);
                            plo.addAll(PolyUtil.decode(poly));
                            googMap.addPolyline(plo);

                        }



                    }

                    latLng = j.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("end_location").getString("lat");
                    lng = j.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("end_location").getString("lng");
                    double latitude = Double.parseDouble(latLng);
                    double longitude = Double.parseDouble(lng);
                    LatLng latlong = new LatLng(latitude,longitude);
                    googMap.addMarker(new MarkerOptions().position(latlong).title("Destination"));
                    googMap.animateCamera(CameraUpdateFactory.newLatLng(latlong));




                }catch (Exception e){
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error","Error Occured in fetching location details");
            }
        });
        queue.add(req);


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
        googMap= googleMap;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            googMap.setMyLocationEnabled(true);


        } else {
            Toast.makeText(Route.this, "Turn on the location services", Toast.LENGTH_LONG).show();
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                googMap.setMyLocationEnabled(true);
            }
        }
    }
}
