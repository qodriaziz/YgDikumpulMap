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
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
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

public class MainActivity extends AppCompatActivity implements LocationListener {
    //LocationListener buat buttton curent
    //OnMapReadyCallback untuk search

    ImageButton cariS;

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

    private int proximityRadius = 10000;
    private double lang, leng;




    //@SuppressLint("MissingInflatedId")
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonL = findViewById(R.id.button_location);
        textView = findViewById(R.id.text_location);
        cariS = findViewById(R.id.cariSearch);
        hotel = findViewById(R.id.hotelK);



        //mapSearch = findViewById(R.id.mapLocal);


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_nya);
        fusedLocationProviderClient = (FusedLocationProviderClient) LocationServices.getFusedLocationProviderClient(this);


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


        buttonL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getLokasi();
            }
        });

        //mapSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener(){

        //  @Override
        //public boolean onQueryTextSubmit(String s) {
        //String loc = mapSearch.getQuery().toString();
        //List<Address> addressList = null;
        //if (loc != null || loc.equals("")){
        //Geocoder geo = new Geocoder((MainActivity.this));

        //try {
        //addressList = geo.getFromLocationName(loc,1);
        //}catch (IOException e){
        //e.printStackTrace();
        //}
        //Address adds = addressList.get(0);
        //LatLng link = new LatLng(adds.getLatitude(),adds.getLongitude());
        //MarkerOptions markerOptions = new MarkerOptions().position(link).title("current location");
        //mapKU.addMarker(new MarkerOptions().position(link).title(loc));
        //mapKU.animateCamera(CameraUpdateFactory.newLatLngZoom(link,15));
        //}
        //return false;
        //}

        //@Override
        //public boolean onQueryTextChange(String s) {

        //return false;
    }

    //});
    //supportMapFragment.getMapAsync(this);
//}

    @SuppressLint("MissingPermission")
    //@SuppressLint("MissingPermission")
    private void getLokasi() {
        try {
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, MainActivity.this);
            getCurrentLocation();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(@NonNull GoogleMap googleMap) {
                        if (location != null){
                            LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
                            MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("My Location");
                            googleMap.addMarker(markerOptions);
                            googleMap.animateCamera(CameraUpdateFactory
                                    .newLatLngZoom(latLng, 16));

                        }else {
                            Toast.makeText(MainActivity.this, "Aktifkan Izin Lokasi anda pada APP", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    //@Override
    //public void onMapReady(@NonNull GoogleMap googleMap) {
    //    mapKU = googleMap;
   // }


    @Override
    public void onLocationChanged(Location location) {
        //latitude = location.getLongitude();
        Toast.makeText(this, ""+location.getLatitude()+","+location.getLongitude(), Toast.LENGTH_SHORT).show();
        try{
            Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
              String address = addresses.get(0).getAddressLine(0);

                 textView.setText(address);

        }catch (Exception e){
                 e.printStackTrace();
        }
    }

    public void onClick(View w){
        if (w.getId() == R.id.cariSearch){
            EditText alamatTujuan = findViewById(R.id.isiSearch);
            String alamat = alamatTujuan.getText().toString();
            List<Address> addressList = null;
            MarkerOptions markerOptions = new MarkerOptions();

            if (!TextUtils.isEmpty(alamat)) {
                Geocoder geo = new Geocoder(this);
                try {
                    addressList = geo.getFromLocationName(alamat, 6);
                    if (addressList !=null ){
                        for (int i = 0; i < addressList.size(); i++){
                            Address penggunaA = addressList.get(i);
                            supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                                @Override
                                public void onMapReady(@NonNull GoogleMap googleMap) {
                                    LatLng ling = new LatLng(penggunaA.getLatitude(), penggunaA.getLongitude());
                                    markerOptions.position(ling);
                                    markerOptions.title("Hasil Pencarian");
                                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
                                    googleMap.addMarker(markerOptions);
                                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(ling));
                                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ling, 15));
                                }
                            });
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                Toast.makeText(MainActivity.this, "input text alamat lokasi", Toast.LENGTH_SHORT).show();
            }
        }
        hotel.setOnClickListener(new View.OnClickListener(){



            @Override
            public void onClick(View view) {
                StringBuilder hotelB = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
                hotelB.append("location=" + lang + "," + leng);
                hotelB.append("&radius=1000");
                hotelB.append("&type=mosque");
                hotelB.append("&sensor=true");
                hotelB.append("&key=" +"AIzaSyDofSb12YMcial14dK3KACZ5-vGLcDyPYc");


                String link = hotelB.toString();
                Object dataFetch[] = new Object[2];
                dataFetch[0]= mapKU;
                dataFetch[1]=link;

                FetchData fetchData = new FetchData();
                fetchData.execute(dataFetch);

            }
        });

    }
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }



}