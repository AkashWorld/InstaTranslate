package finalproject.mae.maptranslate;

import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import finalproject.mae.maptranslate.ImageTranslation.TranslationFB;

public class Details extends AppCompatActivity implements ChildEventListener{
    DatabaseReference mDatabase;
    StorageReference mStorage;
    private ArrayAdapter<TranslationFB> adapter;
    private ArrayList<TranslationFB> info_list=new ArrayList<>();
    private double marker_Lat;
    private double marker_Lng;
    private String targetLanguage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.addChildEventListener(this);

        targetLanguage=getIntent().getStringExtra("Targetlanguage");
        marker_Lat=getIntent().getDoubleExtra("Lat",0);
        marker_Lng=getIntent().getDoubleExtra("Lng",0);



        adapter=new ArrayAdapter<TranslationFB>(this,R.layout.custom_info_window,info_list)
        {
            @Override
            public View getView(int pos, View convertView, ViewGroup parent)
            {
                convertView=View.inflate(getContext(),R.layout.custom_info_window, null);
                TextView textView=convertView.findViewById(R.id.markerText);
                final ImageView imageView=convertView.findViewById(R.id.markerImage);
                textView.setText(info_list.get(pos).getTranslatedText());
                mStorage.child("images/"+info_list.get(pos).imageName()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.with(getContext()).load(uri).into(imageView);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                        imageView.setImageAlpha(android.R.drawable.sym_def_app_icon);
                    }
                });

                return convertView;
            }
        };

        ListView listView=findViewById(R.id.infowindow_list);
        listView.setAdapter(adapter);


        mDatabase.equalTo(targetLanguage,"targetLanguage");
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        TranslationFB translationFB = dataSnapshot.getValue(TranslationFB.class);
        double info_Lat = translationFB.getLatitude();
        double info_Lng = translationFB.getLongitude();
        float results[]=new float[1];
        Location.distanceBetween(info_Lat,info_Lng,marker_Lat,marker_Lng,results);
        if(results[0]<=10)
        {
            info_list.add(translationFB);
            adapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
