package finalproject.mae.maptranslate;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.cloud.translate.Translation;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import finalproject.mae.maptranslate.Details;
import finalproject.mae.maptranslate.ImageTranslation.TranslationFB;
import finalproject.mae.maptranslate.R;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private final View mWindow;
    private Context mContext;
    DatabaseReference mDatabase;
    StorageReference mStorage;
    private ArrayAdapter<TranslationFB> adapter;
    private ArrayList<TranslationFB> info_list=new ArrayList<>();
    private double marker_Lat;
    private double marker_Lng;
    private String targetLanguage;

    public CustomInfoWindowAdapter(Context context, String tl) {
        this.mContext = context;
        mWindow = LayoutInflater.from(context).inflate(R.layout.infowindow_list, null);
        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // Use this while retrieving from D
        targetLanguage=tl;
    }

    private View WindowContent(final Marker marker, View view){
        view = mWindow;

        marker_Lat=marker.getPosition().latitude;
        marker_Lng=marker.getPosition().longitude;

        adapter=new ArrayAdapter<TranslationFB>(view.getContext(),R.layout.custom_info_window,info_list)
        {
            @Override
            public View getView(int pos,View convertView,ViewGroup parent)
            {
                convertView=View.inflate(getContext(),R.layout.custom_info_window, null);
                TextView textView=convertView.findViewById(R.id.markerText);
                final ImageView imageView=convertView.findViewById(R.id.markerImage);
                textView.setText(info_list.get(pos).getTranslatedText());
                textView.setMovementMethod(new ScrollingMovementMethod());
                mStorage.child("images/"+info_list.get(pos).imageName()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                            Picasso.with(mContext).load(uri).into(imageView);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                        imageView.setImageAlpha(android.R.drawable.sym_def_app_icon);
                    }
                });


//                imageView.setImageBitmap(info_list.get(pos).imageName());
                return convertView;
            }
        };

        ListView listView=view.findViewById(R.id.infowindow_list);
        listView.setAdapter(adapter);


        Query query=mDatabase.getRef();
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("myfilter", "Randommmmm");
                for(DataSnapshot translation:dataSnapshot.getChildren())
                {
                    TranslationFB translationFB = translation.getValue(TranslationFB.class);
                    Log.d("myfilter", "Target Language: " + translationFB.getTargetLanguage());
                    if(translationFB.getTargetLanguage().equals("ro"))
                    {
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
                }


                }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


//        Button show_details=view.findViewById(R.id.Show_Details);
//        show_details.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent=new Intent(view.getContext(),Details.class);
//                intent.putExtra("Lat",marker_Lat);
//                intent.putExtra("Lng",marker_Lng);
//                intent.putExtra("Targetlanguage",targetLanguage);
//                view.getContext().startActivity(intent);
//            }
//        });
//        String title = marker.getTitle();
//        TextView tv = (TextView) view.findViewById(R.id.markerText);
//        tv.setText(title);
//        ImageView iv = (ImageView) view.findViewById(R.id.markerImage);

        return view;

    }
    @Override
    public View getInfoWindow(Marker marker) {
        WindowContent(marker, mWindow);
        return mWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        WindowContent(marker, mWindow);
        return mWindow;
    }




}
