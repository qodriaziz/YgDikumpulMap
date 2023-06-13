package com.example.harusbisa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    //LocationListener buat buttton curent
    //OnMapReadyCallback untuk search

    //ImageButton cariS;

    ImageButton buttonL;

    ImageButton hotel;
    ImageButton atm;
    ImageButton univ;
    TextView textView;

    LocationManager locationManager;
    SupportMapFragment supportMapFragment;
    FusedLocationProviderClient fusedLocationProviderClient;

    SearchView mapSearch;
    GoogleMap mapKU;

    private int Request_code = 101;

    private final int FINE_PERMISSION_CODE = 1;
    private double lang, leng;




    //@SuppressLint("MissingInflatedId")
    //@SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.text_location);
        //cariS = findViewById(R.id.cariSearch);


        buttonL = findViewById(R.id.button_location);
        hotel = findViewById(R.id.hotelK);

        //mapSearch = findViewById(R.id.mapLocal);


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_nya);
        fusedLocationProviderClient =  LocationServices.getFusedLocationProviderClient(this);


        Dexter.withContext(getApplicationContext()).withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        getCurrentLocation();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();

                    }
                }).check();


        //buttonL.setOnClickListener(new View.OnClickListener() {
            //@Override
            //public void onClick(View v) {

                //getLokasi();
            //}
        //});

        mapSearch = findViewById(R.id.map_id);

        mapSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                String location = mapSearch.getQuery().toString();
                List<Address> addressList = null;

                if (location != null || location.equals("")) {
                    Geocoder geocoder = new Geocoder((MainActivity.this));

                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Address address = addressList.get(0);
                    LatLng ling = new LatLng(address.getLatitude(), address.getLongitude());
                    MarkerOptions markerOptions = new MarkerOptions().position(ling).title("Current Location");
                    mapKU.addMarker(new MarkerOptions().position(ling).title(location));
                    mapKU.animateCamera(CameraUpdateFactory.newLatLngZoom(ling, 15));

                }
                return false;
            }


            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        supportMapFragment.getMapAsync(this::onMapReady);

        hotel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StringBuilder stringBuilder = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
                stringBuilder.append("location=" + lang + "," + leng);
                stringBuilder.append("&radius=1000");
                stringBuilder.append("&type=atm");
                stringBuilder.append("&sensor=true");
                stringBuilder.append("&key=" + getResources().getString(R.string.google_maps_api));


                String url = stringBuilder.toString();
                Object DataAmbil[] = new Object[2];
                DataAmbil[0] = mapKU;
                DataAmbil[1] = url;

                FetchData ambilData = new FetchData();
                ambilData.execute(DataAmbil);
            }
        });

        buttonL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String a = String.valueOf(lang);
                String b = String.valueOf(leng);

                Toast.makeText(MainActivity.this, "Latitude : " + a + "  Longitude : " + b, Toast.LENGTH_LONG).show();

            }
        });

    }

    public void onMapReady(@NonNull GoogleMap googleMap) {
        mapKU = googleMap;
    }

    //@SuppressLint("MissingSupercall")
    //@Override
    //public void onRequestPermissionsResult(int requestCode, @NonNull @org.jetbrains.annotations.NotNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ///switch (Request_code) {
            //case Request_code:
                //if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                //}
        //}
    //}

    public void getCurrentLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERMISSION_CODE);
//            return;


            return;
        }

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
//                super.onLocationResult(locationResult);
                Toast.makeText(getApplicationContext(), "location result is=" + locationResult
                        , Toast.LENGTH_LONG).show();

                if (locationResult == null) {

                    Toast.makeText(getApplicationContext(), "Current location is null"
                            , Toast.LENGTH_LONG).show();

                    return;

                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        Toast.makeText(getApplicationContext(), "Current location is" + location.getLongitude()
                                , Toast.LENGTH_LONG).show();
                    }
                }
            }
        };


        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(@NonNull GoogleMap googleMap) {
                        if (location != null) {
                            lang = location.getLatitude();
                            leng = location.getLongitude();
                            LatLng latLng = new LatLng(lang, leng);
                            mapKU.addMarker(new MarkerOptions().position(latLng).title("Saya Disini"));
                            mapKU.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                            mapKU.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
//                            lt.setText(String.valueOf(lat));
//                            nn.setText(String.valueOf(lnn));

                        } else {
                            Toast.makeText(MainActivity.this, "Tolong, Hidupkan Lokasi anda", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    //@SuppressLint("MissingPermission")
    //@SuppressLint("MissingPermission")
    //private void getLokasi() {

        //try {
            //locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, MainActivity.this);
            //getCurrentLocation();

        //}catch (Exception e){
            //e.printStackTrace();
        //}
    //}

    //public void getCurrentLocation() {
        //if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                //&& ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            //return;
        //}
        //Task<Location> task = fusedLocationProviderClient.getLastLocation();
        //task.addOnSuccessListener(new OnSuccessListener<Location>() {
            //@Override
            //public void onSuccess(Location location) {
                //supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                    //@Override
                    //public void onMapReady(@NonNull GoogleMap googleMapE) {
                        //if (location != null){
                            //LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
                            //MarkerOptions markerO = new MarkerOptions().position(latLng).title("My Location");
                            //googleMapE.addMarker(markerO);
                            //googleMapE.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                            //googleMapE.animateCamera(CameraUpdateFactory
                                    //.newLatLngZoom(latLng, 16));

                        //}else {
                            //Toast.makeText(MainActivity.this, "Aktifkan Izin Lokasi anda pada APP", Toast.LENGTH_SHORT).show();
                        //}
                    //}
                //});
            //}
        //});
    //}

    //@Override
    //public void onMapReady(@NonNull GoogleMap googleMap) {
    //    mapKU = googleMap;
   // }


    //@Override
    //public void onLocationChanged(Location location) {
        //lang = location.getLatitude();
        //leng = location.getLongitude();
        //Toast.makeText(this, ""+location.getLatitude()+","+location.getLongitude(), Toast.LENGTH_SHORT).show();
        //try{
            //Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
            //List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
              //String address = addresses.get(0).getAddressLine(0);

                 //textView.setText(address);

        //}catch (Exception e){
                 //e.printStackTrace();
        //}
    //}

    //public void onClick(View w){
        //String mosque = "mosque",hotel = "hotel", restaurant = "restaurant";
        //Object transferData[] = new Object[2];
        //GetNearbyPlaces getNearbyPlaces = new GetNearbyPlaces();
        //if (w.getId() == R.id.cariSearch){
            //EditText alamatTujuan = findViewById(R.id.isiSearch);
            //String alamat = alamatTujuan.getText().toString();
            //List<Address> addressList = null;
            //MarkerOptions markerOptions = new MarkerOptions();

            //if (!TextUtils.isEmpty(alamat)) {
                //Geocoder geo = new Geocoder(this);
                //try {
                    //addressList = geo.getFromLocationName(alamat, 6);
                    //if (addressList !=null ){
                        //for (int i = 0; i < addressList.size(); i++){
                            //Address penggunaA = addressList.get(i);
                            //supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                                //@Override
                                //public void onMapReady(@NonNull GoogleMap googleMap) {
                                    //LatLng ling = new LatLng(penggunaA.getLatitude(), penggunaA.getLongitude());
                                    //markerOptions.position(ling);
                                    //markerOptions.title("Hasil Pencarian");
                                    //markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
                                    //googleMap.addMarker(markerOptions);
                                    //googleMap.moveCamera(CameraUpdateFactory.newLatLng(ling));
                                    //googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ling, 15));
                                //}
                            //});
                        //}
                    //}else {
                        //Toast.makeText(MainActivity.this, "Tidak ada lokasi", Toast.LENGTH_SHORT).show();
                    //}
                //} catch (IOException e) {
                    //e.printStackTrace();
                //}
            //}else{
                //Toast.makeText(MainActivity.this, "input text alamat lokasi", Toast.LENGTH_SHORT).show();
           // }
        //}
        //hotel.setOnClickListener(new View.OnClickListener(){
            //@Override
            //public void onClick(View view) {
                //StringBuilder hotelB = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
                //hotelB.append("location=" + lang + "," + leng);
                //hotelB.append("&radius=1000");
                //hotelB.append("&type=mosque");
                //hotelB.append("&sensor=true");
                //hotelB.append("&key=" +"AIzaSyDofSb12YMcial14dK3KACZ5-vGLcDyPYc");
                //String link = hotelB.toString();
                //Object dataFetch[] = new Object[2];
                //dataFetch[0]= mapKU;
                //dataFetch[1]=link;

                //FetchData fetchData = new FetchData();
                //fetchData.execute(dataFetch);

            //}
        //});
        //if (w.getId() == R.id.hotelK) {
            // Handle hospital button click
            //supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                //@Override
                //public void onMapReady(@NonNull GoogleMap googleMap) {
                    //googleMap.clear();
                    //String url = getUrl(lang, leng, mosque);
                    //transferData[0] = googleMap;
                    //transferData[1] = url;
                    //getNearbyPlaces.execute(transferData);
                    //Toast.makeText(MainActivity.this, "Mencari masjid terdekat...", Toast.LENGTH_SHORT).show();
                    //Toast.makeText(MainActivity.this, "Menampilkan masjid terdekat", Toast.LENGTH_SHORT).show();
                //}
            //});
        //}
        //if (w.getId() == R.id.atm) {
            // Handle mosque button click
            //supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                //@Override
                //public void onMapReady(@NonNull GoogleMap googleMap) {
                    //googleMap.clear();
                    //String url = getUrl(leng, lang, hotel);
                    //transferData[0] = googleMap;
                    //transferData[1] = url;
                    //getNearbyPlaces.execute(transferData);
                    //Toast.makeText(MainActivity.this, "Mencari masjid terdekat...", Toast.LENGTH_SHORT).show();
                    //Toast.makeText(MainActivity.this, "Menampilkan masjid terdekat", Toast.LENGTH_SHORT).show();
                //}
            //});
        //}

        //if (w.getId() == R.id.uSchool) {
            // Handle restaurant button click
            //supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                //@Override
                //public void onMapReady(@NonNull GoogleMap googleMap) {
                    //googleMap.clear();
                    //String url = getUrl(leng, lang, restaurant);
                    //transferData[0] = googleMap;
                    //transferData[1] = url;
                    //getNearbyPlaces.execute(transferData);
                    //Toast.makeText(MainActivity.this, "Mencari restoran terdekat...", Toast.LENGTH_SHORT).show();
                    //Toast.makeText(MainActivity.this, "Menampilkan restoran terdekat", Toast.LENGTH_SHORT).show();
                //}
            //});
        //}
    //}
    //private String getUrl(double lang, double leng, String nearbyPlace) {
        ///StringBuilder googleURL = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        //googleURL.append("location=" + lang + "," + leng);
        //googleURL.append("&radius=" + proximityRadius);
        //googleURL.append("&type=" + nearbyPlace);
        //googleURL.append("&sensor=true");
        //googleURL.append("&key=" + "AIzaSyDofSb12YMcial14dK3KACZ5-vGLcDyPYc");
        //Log.d("GoogleMapsActivity", "url =" + googleURL.toString());
        //return googleURL.toString();
    //}



}