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

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.cloud.translate.Detection;
import com.google.cloud.translate.Language;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;

import java.util.Iterator;
import java.util.List;

import finalproject.mae.maptranslate.MainActivity;
import finalproject.mae.maptranslate.R;

public class TranslationActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String API_KEY = "AIzaSyDyDuLRbiaqxZgpX0yy-03zNoZXTCndi54";
    String targetLanguage;
    Button goodButton;
    Button badButton;
    TextView origin;
    TextView translation;
    ImageView image;
    Bitmap originalImage;
    String extractedText;
    String translatedText;
    TextView detectText;
    String language;
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
        targetLanguage = "de";
        language = "";
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
     Log.d("translateText", "before Async text");
     //LAUNCHES TWO ASYNC TASKS TO GET TRANSLATION
        //language task
     AsyncTask newTask = new LanguageTask().execute(extractedText);
        //translation task
     new TranslateTask().execute(extractedText);

    }



    public void onClick(View v){
        if(v.getId() == R.id.badbutt){
            //return to map activity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);

        }
        else if(v.getId() == R.id.goodbutt){
            //add to database


            //return to map activity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);

        }


    }

    //Language Asynchronous Task that gets the language of the extracted text
    class LanguageTask extends AsyncTask<String, String, String>{
        private Exception exception;
        protected String doInBackground(String...originalText){
            Log.d("In AsyncTask", "LanguageTask doInBackground");
            Detection detector = null;
            try{
                TranslateOptions options = TranslateOptions.newBuilder().setApiKey(API_KEY).build();
                Translate translate = options.getService();
                detector = translate.detect(extractedText);

            }
            catch(Exception e){
                this.exception = e;
                return null;
            }

            return detector.getLanguage();
        }
        protected void onPostExecute(String res){
            detectText.setText("Language: " +  res);
        }

    }
    //Translate Asynchronous Task that gets the translation of the source text
    class TranslateTask extends AsyncTask<String, String, String>{
        protected String doInBackground(String...originalText){
            Detection detector = null;
            try{
                //Not sure what to do here, methods that translate seem to not exist?
                TranslateOptions options = TranslateOptions.newBuilder().setApiKey(API_KEY).build();
                TranslateOptions targetLang = TranslateOptions.newBuilder().setTargetLanguage(targetLanguage).build();
                Translate translate = options.getService();
                translate.translate(extractedText, targetLang); //????????????

            }
            catch(Exception e){
                return null;
            }
            return null;
        }
        protected void onPostExecute(String res){

        }

    }
}
