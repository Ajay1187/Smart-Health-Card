package com.example.healthcard_demo;

import com.google.gson.annotations.SerializedName;

public class DiseaseResponse {

    @SerializedName(value = "predictedDisease", alternate = {"predicted_disease"})
    private String predictedDisease;

    private float confidence;

    private String severity;

    public String getPredictedDisease() {
        return predictedDisease;
    }

    public void setPredictedDisease(String predictedDisease) {
        this.predictedDisease = predictedDisease;
    }

    public float getConfidence() {
        return confidence;
    }

    public void setConfidence(float confidence) {
        this.confidence = confidence;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }
}
