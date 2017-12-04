package finalproject.mae.maptranslate.ImageTranslation;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.cloud.translate.Detection;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;

import finalproject.mae.maptranslate.R;

public class TranslationActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String API_KEY = "AIzaSyDyDuLRbiaqxZgpX0yy-03zNoZXTCndi54";
    Button goodButton;
    Button badButton;
    TextView origin;
    TextView translation;
    ImageView image;
    Bitmap originalImage;
    String extractedText;
    String translatedText;
    TextView detectText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translation);
        goodButton = (Button)findViewById(R.id.goodbutt);
        badButton = (Button)findViewById(R.id.badbutt);
        origin = (TextView)findViewById(R.id.origText);
        translation = (TextView)findViewById(R.id.translationText);
        image = (ImageView)findViewById(R.id.translationPic);
        detectText = (TextView)findViewById(R.id.detectText);
        originalImage = getIntent().getParcelableExtra(RETCONSTANT.BITMAP);
        image.setImageBitmap(originalImage);
        extractedText = "Original Text: ";
        translatedText = "Translated Text: ";
        extractTextFromImage();

    }

    public void extractTextFromImage(){
        TextRecognizer textRecognizer = new TextRecognizer.Builder(this).build();
        if(!textRecognizer.isOperational()){
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
        extractedText = extractedText.trim().replaceAll("\\n"," ");
        origin.setText(extractedText);
        translateText();
    }

    public void translateText(){
    TranslateOptions options = TranslateOptions.newBuilder().setApiKey(API_KEY).build();
    Translate translate = options.getService();
    Detection detector = translate.detect(extractedText);
    detectText.setText(detector.getLanguage());
    /*Translation translationString = translate.translate(extractedText);
    translation.setText(translationString.getTranslatedText());*/
    }



    public void onClick(View v){
        if(v.getId() == R.id.badbutt){
            //return to map activity

        }
        else if(v.getId() == R.id.goodbutt){
            //add to database

        }


    }
}
