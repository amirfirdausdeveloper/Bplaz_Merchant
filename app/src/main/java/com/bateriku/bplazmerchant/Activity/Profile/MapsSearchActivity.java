package com.bateriku.bplazmerchant.Activity.Profile;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.bateriku.bplazmerchant.Class.GpsTracker;
import com.bateriku.bplazmerchant.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MapsSearchActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Button button_next;
    private GpsTracker gpsTracker;
    double longitude,latitude;
    AutocompleteSupportFragment autocompleteFragment;
    String address_place;
    String mapsSearch,et_unitNo,pickup_address,pickup_date,pickup_name,pickup_contact,latitudeFirst,longitideFirst,pickup_email,unit_no;
    String apiKey;
    TextView textView_header;
    String person,email,mobile,name,unit_nos,edit_id;
    ImageView imageView_back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_search);



        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        textView_header = findViewById(R.id.textView_header);
        imageView_back = findViewById(R.id.imageView_back);
        imageView_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyCq0k23gmwpp7gdKUzD6K0ZmVr_rVsdyyI");
        }
        PlacesClient placesClient = Places.createClient(this);
        button_next = findViewById(R.id.button_next);

        autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setPlaceFields(Arrays.asList(com.google.android.libraries.places.api.model.Place.Field.ID, com.google.android.libraries.places.api.model.Place.Field.NAME
        , Place.Field.LAT_LNG));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.


                mMap.clear();
                mMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 13));
                address_place = place.getName().toString();

                String remove = place.getLatLng().toString();
                String news =  remove.replace("lat/lng:","");
                String newss = news.replace("(","");
                String newsss = newss.replace(")","");

                String[] latlong = newsss.trim().split(",");
                latitude= Double.parseDouble(latlong[0]);
                longitude = Double.parseDouble(latlong[1]);
                ProfileActivity.latitude = String.valueOf(Double.parseDouble(latlong[0]));
                ProfileActivity.longitude = String.valueOf(Double.parseDouble(latlong[1]));


            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("TEST", "An error occurred: " + status);
            }
        });

//        autocompleteFragment = (PlaceAutocompleteFragment)
//                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
//        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
//            @Override
//            public void onPlaceSelected(Place place) {
//                mMap.clear();
//                mMap.addMarker(new MarkerOptions().position(place.getLatLng()).title(place.getName().toString()));
//                mMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
//                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 13));
//                address_place = place.getName().toString();
//
//                String remove = place.getLatLng().toString();
//                String news =  remove.replace("lat/lng:","");
//                String newss = news.replace("(","");
//                String newsss = newss.replace(")","");
//
//                String[] latlong = newsss.trim().split(",");
//                latitude= Double.parseDouble(latlong[0]);
//                longitude = Double.parseDouble(latlong[1]);
//
//                Log.d("remove",news.trim());
//            }
//
//            @Override
//            public void onError(Status status) {
//
//            }
//        });


        button_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        getLocation();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MapsSearchActivity.this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void getLocation() {
        gpsTracker = new GpsTracker(MapsSearchActivity.this);
        if (gpsTracker.canGetLocation()) {
            latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();

        } else {
            gpsTracker.showSettingsAlert();
        }
    }

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                address_place = strReturnedAddress.toString();
                autocompleteFragment.setText(strReturnedAddress.toString());

                ProfileActivity.latitude = String.valueOf(LATITUDE);
                ProfileActivity.longitude = String.valueOf(LONGITUDE);
                ProfileActivity.et_address.setText(strReturnedAddress.toString());
            } else {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strAdd;
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
            googleMap.clear();
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mMap = googleMap;
                    LatLng pickup = new LatLng(latitude, longitude);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pickup, 13));

                    mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                        @Override
                        public void onCameraIdle() {
                            CameraPosition cameraPosition = mMap.getCameraPosition();
                            getCompleteAddressString(cameraPosition.target.latitude, cameraPosition.target.longitude);
                        }
                    });
                }
            }, 1000);
    }


}