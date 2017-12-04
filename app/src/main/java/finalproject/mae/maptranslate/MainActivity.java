package finalproject.mae.maptranslate;
/**
 * Final Project for Mobile App Engineering
 */

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    DatabaseReference mDatabase;
    StorageReference mStorage;

    String sourceLanguage;
    String targetLanguage;
    Double latitude;
    Double longitude;
    Uri image;
    String translatedText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        /* Before adding to firebase, ensure the values for sourceLanguage, targetLanguage, latitude
        longitude, image, and translatedText are not NULL. I'm not checking the validity. */

        addToFirebaseDatabase(); // Add to Firebase database
    }

    private void addToFirebaseDatabase() {
        String id = mDatabase.push().getKey(); // Unique id (primary key)
        Translation translation = new Translation(sourceLanguage, targetLanguage, latitude, longitude, id, translatedText);

//        Test:
//        Translation translation = new Translation("esp", "en", 12.3456, -87.6543, id, "It works.");

        mDatabase.child(id).setValue(translation); // Add to DB
        storeImageinStorage(id, image); // Add image to Storage
        Toast.makeText(this, "Database updated successfully.", Toast.LENGTH_LONG).show();
    }

    private void storeImageinStorage(String id, Uri image) {
        StorageReference path = mStorage.child("images").child(id);
        path.putFile(image)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(getApplicationContext(), "Storage updated successfully.", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
