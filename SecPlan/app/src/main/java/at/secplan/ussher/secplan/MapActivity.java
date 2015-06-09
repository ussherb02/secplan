package at.secplan.ussher.secplan;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.support.v4.app.FragmentActivity;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends FragmentActivity {

    private MapView mapView;
    private IMapController mapController;
    private ItemizedOverlay<OverlayItem> myLocationOverlay;
    private ResourceProxy mResourceProxy;
    private Road road = null;
    Thread task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map2);

        mapView = (MapView) this.findViewById(R.id.mapview);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);
        mapController = this.mapView.getController();
        mapController.setZoom(14);
        Location curreLocation = new GPSTracker().getLocation();

        ArrayList<GeoPoint> geolist = new ArrayList<GeoPoint>();
        ArrayList<OverlayItem> overlays = new ArrayList<OverlayItem>();
        if(curreLocation != null) {
            GeoPoint mapCenter = new GeoPoint(curreLocation.getLatitude(), curreLocation.getLongitude());
            //Set current location
            overlays.add(new OverlayItem("Current location","Your current location",mapCenter));
            geolist.add(mapCenter);
            mapController.setCenter(mapCenter);
        }
        Intent i = getIntent();
        String currentStation = i.getStringExtra("currentStation");
        ArrayList<String> stations = i.getStringArrayListExtra("stations");




        for(String address : stations){
            GeoPoint point = getLocation(address);
            geolist.add(point);
            overlays.add(new OverlayItem(address,"You are at "+address,point));
        }



        mResourceProxy = new DefaultResourceProxyImpl(getApplicationContext());
        this.myLocationOverlay = new ItemizedIconOverlay<OverlayItem>(overlays, null, mResourceProxy);
        this.mapView.getOverlays().add(this.myLocationOverlay);


        if(task != null && task.isAlive()){
            task.interrupt();
        }
        plotRoute(geolist);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        //if (id == R.id.)

        return super.onOptionsItemSelected(item);
    }

    private GeoPoint getLocation(String cityname){
            Geocoder coder = new Geocoder(getApplicationContext());
            GeoPoint lngltd = null;
            List<Address> address;
            try {
                address = coder.getFromLocationName(cityname, 5);
                if (address == null) {
                    Log.d("test", "############Address not correct #########");
                }
                Address location = address.get(0);
                lngltd = new GeoPoint(location.getLatitude(),location.getLongitude());
                Log.d("test", "Address Latitude : " + location.getLatitude() + "Address Longitude : " + location.getLongitude());
            }
            catch(Exception e) {

            }
        return lngltd;
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private class GPSTracker implements LocationListener {

        // flag for GPS status
        boolean isGPSEnabled = false;

        // flag for network status
        boolean isNetworkEnabled = false;

        boolean canGetLocation = false;

        Location location; // location
        double latitude; // latitude
        double longitude; // longitude

        // The minimum distance to change Updates in meters
        private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

        // The minimum time between updates in milliseconds
        private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

        // Declaring a Location Manager
        protected LocationManager locationManager;

        public GPSTracker() {
            getLocation();
        }

        public Location getLocation() {
            try {
                LocationManager locationManager = (LocationManager) getApplicationContext()
                        .getSystemService(LOCATION_SERVICE);

                // getting GPS status
                boolean isGPSEnabled = locationManager
                        .isProviderEnabled(LocationManager.GPS_PROVIDER);

                // getting network status
                boolean isNetworkEnabled = locationManager
                        .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                if (!isGPSEnabled && !isNetworkEnabled) {
                    // no network provider is enabled
                } else {
                    this.canGetLocation = true;
                    // First get location from Network Provider
                    if (isNetworkEnabled) {
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("Network", "Network");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                    // if GPS Enabled get lat/long using GPS Services
                    if (isGPSEnabled) {
                        if (location == null) {
                            locationManager.requestLocationUpdates(
                                    LocationManager.GPS_PROVIDER,
                                    MIN_TIME_BW_UPDATES,
                                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                            Log.d("GPS Enabled", "GPS Enabled");
                            if (locationManager != null) {
                                location = locationManager
                                        .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                if (location != null) {
                                    latitude = location.getLatitude();
                                    longitude = location.getLongitude();
                                }
                            }
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return location;
        }

        @Override
        public void onLocationChanged(Location location) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        /*@Override
        public IBinder onBind(Intent arg0) {
            return null;
        }*/

        /**
         * Function to get latitude
         * */
        public double getLatitude(){
            if(location != null){
                latitude = location.getLatitude();
            }

            // return latitude
            return latitude;
        }

        /**
         * Function to get longitude
         * */
        public double getLongitude(){
            if(location != null){
                longitude = location.getLongitude();
            }

            // return longitude
            return longitude;
        }
    }
    /*@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mResourceProxy = new ResourceProxyImpl(inflater.getContext().getApplicationContext());
        mMapView = new MapView(inflater.getContext(), 256, mResourceProxy);
        return mMapView;
    }*/


    private void plotRoute(final ArrayList<GeoPoint> list){
        task = new Thread(new Runnable()
        {
            public void run()
            {
                RoadManager roadManager = new OSRMRoadManager();

                try
                {
                    road = roadManager.getRoad(list);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable()
                {
                    public void run()
                    {
                        if (road.mStatus != Road.STATUS_OK)
                        {
                            //handle error... warn the user, etc.
                        }

                        Polyline roadOverlay = RoadManager.buildRoadOverlay(road, Color.RED, 5, getApplicationContext());
                        mapView.getOverlays().add(roadOverlay);
                    }
                });
            }
        });
        task.start();
    }
}