package finalproject.mae.maptranslate.ImageTranslation;

import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import finalproject.mae.maptranslate.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PicChooserFrag.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PicChooserFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PicChooserFrag extends Fragment implements View.OnClickListener{


    ImageButton galleryButton;
    ImageButton cameraButton;
    private OnFragmentInteractionListener mListener;

    public PicChooserFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PicChooserFrag.
     */
    // TODO: Rename and change types and number of parameters
    public static PicChooserFrag newInstance(String param1, String param2) {
        PicChooserFrag fragment = new PicChooserFrag();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pic_chooser, container, false);
        galleryButton = (ImageButton)view.findViewById(R.id.gallery);
        cameraButton = (ImageButton)view.findViewById(R.id.camera);
        galleryButton.setOnClickListener(this);
        cameraButton.setOnClickListener(this);
        return view;
    }


    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.gallery:
                mListener.pickImageToTranslate(RETCONSTANT.GALLERY);
            case R.id.camera:
                mListener.pickImageToTranslate(RETCONSTANT.CAMERA);
        }
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void pickImageToTranslate(int flag);
    }
}
