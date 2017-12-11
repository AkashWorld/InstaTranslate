package finalproject.mae.maptranslate;


import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import finalproject.mae.maptranslate.ImageTranslation.RETCONSTANT;

/**
 * Created by Akash on 12/5/2017.
 */

public class LanguageCode {
    List<String> languageName;
    List<String> languageCode;
    public List<String> getLanguageList(Context context){
        languageName = new ArrayList<>();
        languageCode = new ArrayList<>();
        Log.d("getLanguageCode", "in Static method");
        final List<String> languageCode = new ArrayList<>();
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "https://translation.googleapis.com/language/translate/v2/languages";
        url = url + "?key=" + RETCONSTANT.API_KEY +"&target=en";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.v("Response",response);
                try {
                    JSONObject mainObj = new JSONObject(response);
                    JSONObject dataObj = mainObj.getJSONObject("data");
                    JSONArray langArray = dataObj.getJSONArray("languages");
                    for(int i = 0; i < langArray.length(); i++){
                        JSONObject langPair = langArray.getJSONObject(i);
                        languageName.add(langPair.getString("name"));
                        languageCode.add(langPair.getString("language"));
                    }
                }
                catch(org.json.JSONException e){
                    e.printStackTrace();
                    Log.d("JSON","Could not parse json string");
                    return;
                }
            }
        }, new Response.ErrorListener(){
            public void onErrorResponse(VolleyError error){
                Log.d("Response", "ERROR");
            }
        });
        queue.add(stringRequest);
        return languageName;
    }

    public String getLanguageCode(int i){
        return languageCode.get(i);
    }
}
