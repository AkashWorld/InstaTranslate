package finalproject.mae.maptranslate;
/**
 * Final Project for Mobile App Engineering
 */

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import finalproject.mae.maptranslate.ImageTranslation.PicChooserFrag;
import finalproject.mae.maptranslate.ImageTranslation.RETCONSTANT;

public class MainActivity extends AppCompatActivity implements PicChooserFrag.OnFragmentInteractionListener, View.OnClickListener{
    ImageButton picChooser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        picChooser = findViewById(R.id.takePicButton);
        picChooser.setOnClickListener(this);
    }

    public void onClick(View v){
        //launch frag
    }

    public void pickImageToTranslate(int flag){
        if(flag == RETCONSTANT.CAMERA){
            cameraIntent();
        }
        else if(flag == RETCONSTANT.GALLERY){
            galleryIntent();
        }
    }

    public void cameraIntent(){

    }

    public void galleryIntent(){

    }
}
