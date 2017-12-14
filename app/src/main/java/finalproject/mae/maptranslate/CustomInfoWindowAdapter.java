package finalproject.mae.maptranslate;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private final View mWindow;
    private Context mContext;

    public CustomInfoWindowAdapter(Context context) {
        this.mContext = context;
        mWindow = LayoutInflater.from(context).inflate(R.layout.custom_info_window, null);
    }

    private void WindowContent(Marker marker, View view){
        String title = marker.getTitle();
        TextView tv = (TextView) view.findViewById(R.id.markerText);
        tv.setText(title);
        ImageView iv = (ImageView) view.findViewById(R.id.markerImage);

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
