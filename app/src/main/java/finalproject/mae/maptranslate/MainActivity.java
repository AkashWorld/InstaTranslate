package finalproject.mae.maptranslate;
/**
 * Final Project for Mobile App Engineering
 */

import android.*;
import android.Manifest;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.location.Location;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.sql.Timestamp;
import java.text.DecimalFormat;


public class MainActivity extends AppCompatActivity
        implements OnMapReadyCallback,GoogleMap.OnMapLoadedCallback{


    private static final int GPS_REQUEST = 739;
    private static final int NETWORK_REQUEST = 951;
    private FusedLocationProviderClient fusedLocationClient;
    private double current_Lat;
    private double current_Lng;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MapFragment mf = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mf.getView().setClickable(true);
        mf.getMapAsync(this);

        Button camera_button= findViewById(R.id.Camera_button);
        camera_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent picIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(picIntent, 2);
            }
        });

        Button choose_gallery=findViewById(R.id.gallery_button);
        choose_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 0);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap gmap)
    {
        map_initialize(gmap);
        location_initialize();
        mMap.setOnMapLoadedCallback(this);
    }

    @Override
    public void onMapLoaded()
    {
        ;
    }


    private void map_initialize(GoogleMap map)
    {
        mMap=map;
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        try
        {
            mMap.setMyLocationEnabled(true);
        }
        catch (SecurityException e)
        {
            Toast.makeText(this,"Permission Denied",Toast.LENGTH_SHORT).show();
        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                return true;
            }
        });

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(final Marker marker) {
                marker.hideInfoWindow();
            }
        });


    }

    private void location_initialize()
    {
        if(ContextCompat.checkSelfPermission( getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions( this, new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION},GPS_REQUEST);
        if(ContextCompat.checkSelfPermission( getApplicationContext(), Manifest.permission.INTERNET ) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions( this, new String[] {android.Manifest.permission.INTERNET},NETWORK_REQUEST);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location!=null)
                {
                    get_current_location(location);
                }
            }
        });

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(500);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationCallback locationCallback=new LocationCallback()
        {
            @Override
            public void onLocationResult(final LocationResult locationResult)
            {
                for(Location location: locationResult.getLocations())
                {
                    if(location!=null)
                        get_current_location(location);
                    else
                        break;
                }
            }
        };
        fusedLocationClient.requestLocationUpdates(locationRequest,locationCallback,null);

    }


    private void get_current_location(Location loc)
    {
        current_Lat = Double.parseDouble(new DecimalFormat("#0.000000").format(loc.getLatitude()));
        current_Lng = Double.parseDouble(new DecimalFormat("#0.000000").format(loc.getLongitude()));
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(current_Lat,current_Lng),15,0,0)));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,Intent intent)
    {
        if (requestCode == 2 && resultCode == RESULT_OK)
        {
            Bitmap bmp = (Bitmap) intent.getExtras().get("data");
        }
        else if(requestCode ==0 && resultCode==RESULT_OK)
        {
            Bitmap bmp = (Bitmap) intent.getExtras().get("data");
        }

    }
}
