package finalproject.mae.maptranslate;

/**
 * Created by govind on 03/12/17.
 */

public class Translation {
    String sourceLanguage;
    String targetLanguage;
    Double latitude;
    Double longitude;
    // Image
    String translatedText;

    public Translation() {

    }

    public Translation(String sourceLanguage, String targetLanguage, Double latitude, Double longitude, String translatedText) {
        this.sourceLanguage = sourceLanguage;
        this.targetLanguage = targetLanguage;
        this.latitude = latitude;
        this.longitude = longitude;
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

    public String getTranslatedText() {
        return translatedText;
    }
}
