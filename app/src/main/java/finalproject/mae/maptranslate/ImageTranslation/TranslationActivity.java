package finalproject.mae.maptranslate.ImageTranslation;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import finalproject.mae.maptranslate.MainActivity;
import finalproject.mae.maptranslate.R;

public class TranslationActivity extends AppCompatActivity implements View.OnClickListener {

    DatabaseReference mDatabase;
    StorageReference mStorage;
    
    String targetLanguage;
    Button goodButton;
    Button badButton;
    TextView origin;
    TextView translation;
    ImageView image;
    Bitmap originalImage;
    Uri imageUri;
    String extractedText;
    String translatedText;
    TextView detectText;
    String language;
    double currentLongitude;
    double currentLatitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translation);
        
        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        
        goodButton = (Button) findViewById(R.id.goodbutt);
        badButton = (Button) findViewById(R.id.badbutt);
        goodButton.setOnClickListener(this);
        badButton.setOnClickListener(this);
        origin = (TextView) findViewById(R.id.origText);
        translation = (TextView) findViewById(R.id.translationText);
        image = (ImageView) findViewById(R.id.translationPic);
        detectText = (TextView) findViewById(R.id.detectText);
        currentLatitude = getIntent().getDoubleExtra(RETCONSTANT.CURRLAT, 0);
        currentLongitude = getIntent().getDoubleExtra(RETCONSTANT.CURRLONG, 0);
        originalImage = getIntent().getParcelableExtra(RETCONSTANT.BITMAP);
        imageUri = Uri.parse(getIntent().getStringExtra(RETCONSTANT.IMAGEURI));
        image.setImageBitmap(originalImage);
        extractedText = "";
        translatedText = "";
        targetLanguage = getIntent().getStringExtra(RETCONSTANT.TARGETLANG);
        language = "";
        extractTextFromImage();

    }

    public void extractTextFromImage() {
        TextRecognizer textRecognizer = new TextRecognizer.Builder(this).build();
        if (!textRecognizer.isOperational()) {
            Log.d("Text Recognizer", "Detector dependencies are not available yet");
            return;
        }
        Frame imageFrame = new Frame.Builder().setBitmap(originalImage).build();
        SparseArray<TextBlock> textBlocks = textRecognizer.detect(imageFrame);
        for (int i = 0; i < textBlocks.size(); i++) {
            TextBlock textBlock = textBlocks.get(textBlocks.keyAt(i));
            extractedText = extractedText + textBlock.getValue();
            Log.d("String:", extractedText);
        }
        extractedText = extractedText.trim().replaceAll("\\n", " ");
        origin.setText(extractedText);
        translateText();
    }

    public void translateText() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://translation.googleapis.com/language/translate/v2";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Response is", response);
                try {
                    JSONObject mainObject = new JSONObject(response);
                    JSONObject dataobj = mainObject.getJSONObject("data");
                    JSONArray translationArray = dataobj.getJSONArray("translations");
                    String tempTranslation = translationArray.getString(0);
                    getTranslation(tempTranslation);

                } catch (org.json.JSONException e) {
                    e.printStackTrace();
                    Log.d("JSON", "Could not parse json string");
                    return;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Volley", "Error on response");
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("q", extractedText);
                params.put("target", targetLanguage);
                params.put("key", RETCONSTANT.API_KEY);
                return params;
            }
        };
        queue.add(stringRequest);

    }


    private void getTranslation(String JSONstr) {
        Log.d("JSONSTR", JSONstr);
        Log.d("Translation ends at", "" + JSONstr.lastIndexOf("\",\""));
        int translationEnd = JSONstr.lastIndexOf("\",\"");
        int translationStart = 19;
        Log.d("Translation", JSONstr.substring(translationStart, translationEnd));
        translatedText = JSONstr.substring(translationStart, translationEnd);
        translation.setText(translatedText);
    }

    public void onClick(View v) {
        if (v.getId() == R.id.badbutt) {
            //return to map activity
            Log.d("onClick", "Bad Button Pressed. Start map activity");
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);

        } else if (v.getId() == R.id.goodbutt) {
            Log.d("onClick", "Good Button Pressed. Add to databse, start map activity");
            //TODO: add the following params to database
            //Bitmap originalImage
            //String translatedText
            //String targetLanguage
            //Double currentLongitude
            //Double currentLatitude
            
            addToFirebaseDatabase();


            //return to map activity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);

        }
    }
    
    private void addToFirebaseDatabase() {
        String id = mDatabase.push().getKey(); // Unique id (primary key)
        Translation translation = new Translation(targetLanguage, currentLatitude, currentLongitude, id, translatedText);
        mDatabase.child(id).setValue(translation); // Add to DB
        
        storeImageinStorage(id); // Add image to Storage
        Toast.makeText(this, "Database updated successfully.", Toast.LENGTH_LONG).show();
    }

    private void storeImageinStorage(String id) {
        StorageReference path = mStorage.child("images").child(id);
        path.putFile(imageUri)
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


