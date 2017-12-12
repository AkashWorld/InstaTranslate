package finalproject.mae.maptranslate;
/**
 * Final Project for Mobile App Engineering
 */

import android.Manifest;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.provider.MediaStore;
import android.renderscript.ScriptGroup;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;

import finalproject.mae.maptranslate.ImageTranslation.RETCONSTANT;
import finalproject.mae.maptranslate.ImageTranslation.TranslationActivity;

import android.widget.Spinner;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class MainActivity extends AppCompatActivity
        implements OnMapReadyCallback,GoogleMap.OnMapLoadedCallback, View.OnClickListener, AdapterView.OnItemSelectedListener{



    private FusedLocationProviderClient fusedLocationClient;
    private double current_Lat;
    private double current_Lng;
    private GoogleMap mMap;
    public String targetLanguage;
    ImageButton picChooser;
    DatabaseReference mDatabase;
    StorageReference mStorage;
    private List<String> targetCode;
    private List<String> languageList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        getWindow().getDecorView().setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                        | View.SYSTEM_UI_FLAG_FULLSCREEN
//                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);




        MapFragment mf = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mf.getView().setClickable(true);
        mf.getMapAsync(this);



    }

    @Override
    public void onMapReady(GoogleMap gmap)
    {
        map_initialize(gmap);
        ui_initialize();
        location_initialize();
        mMap.setOnMapLoadedCallback(this);
    }

    @Override
    public void onMapLoaded()
    {
        ;
    }

    //Initialize spinner and target language
    private void ui_initialize()
    {
        Log.d("ui_initialize","initializing the User Interface");
        Spinner choose_targlang=findViewById(R.id.choose_targlang);

        Gson gson=new Gson();
        Intent intent=getIntent();
        String language=intent.getStringExtra("Language List");
        String code=intent.getStringExtra("Code List");
        languageList= gson.fromJson(language,new ArrayList<String>().getClass());
        targetCode=gson.fromJson(code,new ArrayList<String>().getClass());
        Log.d("Size of Language List","" + languageList.size());
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,languageList);
        choose_targlang.setAdapter(adapter);
        choose_targlang.setOnItemSelectedListener(this);
        SharedPreferences load_pref = getSharedPreferences("TargetLanguage",MODE_PRIVATE);
        targetLanguage = load_pref.getString("TargetLanguage","en");
        int index=targetCode.indexOf(targetLanguage);
        if(index==-1)
            index=targetCode.indexOf("en");
        choose_targlang.setSelection(index);
        picChooser = (ImageButton)findViewById(R.id.takePicButton);
        picChooser.setOnClickListener(this);
    }

    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
        Log.d("Spinner", "Item:" + position + " selected!");
        SharedPreferences prefs = getSharedPreferences("TargetLanguage", MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = prefs.edit();
//        targetLanguage = targetCode.get(position);
        prefsEditor.putString(RETCONSTANT.SHAREDPREFTARGETLANG, targetLanguage);
        prefsEditor.commit();

        Log.d("Target Language Code", targetLanguage);
    }

    public void onNothingSelected(AdapterView<?> parent) {

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
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
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
        try
        {
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
        catch (SecurityException e)
        {
            Toast.makeText(this,"Permission Denied",Toast.LENGTH_SHORT).show();
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }

    }


    private void get_current_location(Location loc)
    {
        current_Lat = Double.parseDouble(new DecimalFormat("#0.000000").format(loc.getLatitude()));
        current_Lng = Double.parseDouble(new DecimalFormat("#0.000000").format(loc.getLongitude()));
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(current_Lat,current_Lng),15,0,0)));
    }

    public void onClick(View v){
        if(v.getId() == R.id.takePicButton){
            cameraIntent();
        }
    }


    public void cameraIntent(){
        Intent pickIntent = new Intent();
        pickIntent.setType("image/*");
        pickIntent.setAction(Intent.ACTION_GET_CONTENT);

        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        String pickTitle = "Select or take a new Picture"; // Or get from strings.xml
        Intent chooserIntent = Intent.createChooser(pickIntent, pickTitle);
        chooserIntent.putExtra
                (
                        Intent.EXTRA_INITIAL_INTENTS,
                        new Intent[] { takePhotoIntent }
                );

        startActivityForResult(chooserIntent, RETCONSTANT.CAMERA);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode,Intent data)
    {
        if(requestCode == RETCONSTANT.CAMERA && resultCode == RESULT_OK){
            Log.d("URI location", data.getData().toString());
            Intent actIntent = new Intent(this, TranslationActivity.class);
            InputStream image_stream;
            try {
                image_stream = getContentResolver().openInputStream(data.getData());
            }
            catch (FileNotFoundException f){
                f.printStackTrace();
                return;
            }
            Bitmap bmp = BitmapFactory.decodeStream(image_stream);
            actIntent.putExtra(RETCONSTANT.BITMAP,bmp);
            actIntent.putExtra(RETCONSTANT.CURRLONG, current_Lng);
            actIntent.putExtra(RETCONSTANT.CURRLAT, current_Lat);
            actIntent.putExtra(RETCONSTANT.IMAGEURI, data.getData().toString());
            if(targetLanguage == null || targetLanguage.equals("")){
                Log.d("TargetLanguage ERROR", "Custom target language not found!");
                targetLanguage = "de"; //german for demo purposes
            }
            Log.d("TargetLanguage",targetLanguage);
            actIntent.putExtra(RETCONSTANT.TARGETLANG,targetLanguage);
            startActivity(actIntent);

        }

    }


}
