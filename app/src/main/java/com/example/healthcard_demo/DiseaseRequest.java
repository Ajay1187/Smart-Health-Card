package com.example.healthcard_demo;

import java.util.List;

public class DiseaseRequest {

    private List<Integer> symptoms;

    public DiseaseRequest(List<Integer> symptoms) {
        this.symptoms = symptoms;
    }

    public List<Integer> getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(List<Integer> symptoms) {
        this.symptoms = symptoms;
    }
}
