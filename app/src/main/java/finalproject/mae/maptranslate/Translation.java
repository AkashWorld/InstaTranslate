package finalproject.mae.maptranslate;

import android.net.Uri;

public class Translation {
    String sourceLanguage;
    String targetLanguage;
    Double latitude;
    Double longitude;
    String imageName;
    String translatedText;

    public Translation() {
    }

    public Translation(String sourceLanguage, String targetLanguage, Double latitude, Double longitude, String  imageName, String translatedText) {
        this.sourceLanguage = sourceLanguage;
        this.targetLanguage = targetLanguage;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imageName = imageName;
        this.translatedText = translatedText;
    }

    public String getSourceLanguage() {
        return sourceLanguage;
    }

    public String getTargetLanguage() {
        return targetLanguage;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String imageName() {
        return imageName;
    }

    public String getTranslatedText() {
        return translatedText;
    }
}
