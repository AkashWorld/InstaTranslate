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
    String langCode;
    List<String> languageList;
    public List<String> getLanguageList(){
        List<String> languages = new ArrayList<>();
        languages.add("Afrikaans");
        languages.add("Albanian");
        languages.add("Amharic");
        languages.add("Arabic");
        languages.add("Armenian");
        languages.add("Azeerbaijani");
        languages.add("Basque");
        languages.add("Belarusian");
        languages.add("Bengali");
        languages.add("Bosnian");
        languages.add("Bulgarian");
        languages.add("Catalan");
        languages.add("Cebuano");
        languages.add("Chinese (Simplified)");
        languages.add("Chinese (Traditional)");
        languages.add("Corsican");
        languages.add("Croatian");
        languages.add("Czech");
        languages.add("Danish");
        languages.add("Dutch");
        languages.add("English");
        languages.add("Esperanto");
        languages.add("Estonian");
        languages.add("Finnish");
        languages.add("French");
        languages.add("Frisian");
        languages.add("Galician");
        languages.add("Georgian");
        languages.add("German");
        languages.add("Greek");
        languages.add("Gujarati");
        languages.add("Haitian Creole");
        languages.add("Hausa");
        languages.add("Hawaiian");
        languages.add("Hebrew");
        languages.add("Hindi");
        languages.add("Hmong");
        languages.add("Hungarian");
        languages.add("Icelandic");
        languages.add("Igbo");
        languages.add("Indonesian");
        languages.add("Irish");
        languages.add("Italian");
        languages.add("Japanese");
        languages.add("Javanese");
        languages.add("Kannada");
        languages.add("Kazakh");
        languages.add("Khmer");
        languages.add("Korean");
        languages.add("Kurdish");
        languages.add("Kyrgyz");
        languages.add("Lao");
        languages.add("Latin");
        languages.add("Latvian");
        languages.add("Lithuanian");
        languages.add("Luxembourgish");
        languages.add("Macedonian");
        languages.add("Malagasy");
        languages.add("Malay");
        languages.add("Malayalam");
        languages.add("Maltese");
        languages.add("Maori");
        languages.add("Marathi");
        languages.add("Mongolian");
        languages.add("Myanmar (Burmese)");
        languages.add("Nepali");
        languages.add("Norwegian");
        languages.add("Nyanja (Chichewa)");
        languages.add("Pashto");
        languages.add("Persian");
        languages.add("Polish");
        languages.add("Portuguese (Portugal, Brazil)");
        languages.add("Punjabi");
        languages.add("Romanian");
        languages.add("Russian");
        languages.add("Samoan");
        languages.add("Scots Gaelic");
        languages.add("Serbian");
        languages.add("Sesotho");
        languages.add("Shona");
        languages.add("Sindhi");
        languages.add("Sinhala (Sinhalese)");
        languages.add("Slovak");
        languages.add("Slovenian");
        languages.add("Somali");
        languages.add("Spanish");
        languages.add("Sundanese");
        languages.add("Swahili");
        languages.add("Swedish");
        languages.add("Tagalog (Filipino)");
        languages.add("Tajik");
        languages.add("Tamil");
        languages.add("Telugu");
        languages.add("Thai");
        languages.add("Turkish");
        languages.add("Ukrainian");
        languages.add("Urdu");
        languages.add("Uzbek");
        languages.add("Vietnamese");
        languages.add("Welsh");
        languages.add("Xhosa");
        languages.add("Yiddish");
        languages.add("Yoruba");
        languages.add("Zulu");
        Log.d("Number of languages", languages.size() + "");
        languageList = languages;
        return languages;
    }

    public String getLanguageCode(Context context, int index){
        final int ind = index;
        final String LanguageCode = "";
        Log.d("getLanguageCode", "in Static method");
        List<String> languageCode = new ArrayList<>();
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "https://translation.googleapis.com/language/translate/v2/languages";
        url = url + "?key=" + RETCONSTANT.API_KEY;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Response",response);
                try {
                    JSONObject mainObj = new JSONObject(response);
                    JSONObject dataObj = mainObj.getJSONObject("data");
                    JSONArray langArray = dataObj.getJSONArray("languages");
                    JSONObject language = langArray.getJSONObject(ind);
                    langCode = language.getString("language");
                    Log.d("Language code", language.getString("language"));
                    Log.d("JSONArray size", "" + langArray.length());
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
        return langCode;
    }
}
