package com.example.healthcard_demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EnterSymptomsActivity extends AppCompatActivity {

    private MultiAutoCompleteTextView etSymptoms;
    private AutoCompleteTextView atSeverity;
    private EditText etDuration;
    private Button btnPredict;

    private static final List<String> PREDEFINED_SYMPTOMS = Arrays.asList(
            "fever", "cough", "headache", "fatigue", "vomiting",
            "chest pain", "sore throat", "body pain", "nausea", "breathlessness"
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_symptoms);

        etSymptoms = findViewById(R.id.et_symptoms);
        atSeverity = findViewById(R.id.at_severity);
        etDuration = findViewById(R.id.et_duration);
        btnPredict = findViewById(R.id.btn_predict);

        ArrayAdapter<String> symptomsAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line,
                PREDEFINED_SYMPTOMS);
        etSymptoms.setAdapter(symptomsAdapter);
        etSymptoms.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        ArrayAdapter<String> severityAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line,
                Arrays.asList("Low", "Medium", "High"));
        atSeverity.setAdapter(severityAdapter);

        btnPredict.setOnClickListener(v -> submitPrediction());
    }

    private void submitPrediction() {
        String rawSymptoms = etSymptoms.getText().toString().trim();
        String severity = atSeverity.getText().toString().trim();
        String durationText = etDuration.getText().toString().trim();

        if (TextUtils.isEmpty(rawSymptoms) || TextUtils.isEmpty(severity) || TextUtils.isEmpty(durationText)) {
            Toast.makeText(this, "Please complete all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Arrays.asList("low", "medium", "high").contains(severity.toLowerCase(Locale.US))) {
            Toast.makeText(this, "Severity must be Low, Medium or High", Toast.LENGTH_SHORT).show();
            return;
        }

        int durationDays;
        try {
            durationDays = Integer.parseInt(durationText);
            if (durationDays <= 0) {
                Toast.makeText(this, "Duration must be greater than 0", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Duration must be numeric", Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> symptomList = new ArrayList<>();
        for (String symptom : rawSymptoms.split(",")) {
            String clean = symptom.trim();
            if (!clean.isEmpty()) {
                symptomList.add(clean);
            }
        }

        SymptomPreprocessor preprocessor = new SymptomPreprocessor();
        DiseaseRequest request = new DiseaseRequest(preprocessor.toFeatureVector(symptomList, severity, durationDays));
        long startMs = System.currentTimeMillis();

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        apiService.predictDisease(request).enqueue(new Callback<DiseaseResponse>() {
            @Override
            public void onResponse(Call<DiseaseResponse> call, Response<DiseaseResponse> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(EnterSymptomsActivity.this, "Prediction failed", Toast.LENGTH_SHORT).show();
                    return;
                }

                DiseaseResponse result = response.body();
                long elapsedMs = System.currentTimeMillis() - startMs;

                Intent intent = new Intent(EnterSymptomsActivity.this, PredictionResultActivity.class);
                intent.putExtra("disease", safeText(result.getPredictedDisease(), "Unknown"));
                intent.putExtra("confidence", result.getConfidence());
                intent.putExtra("severity", safeText(result.getSeverity(), severity));
                intent.putExtra("elapsedMs", elapsedMs);
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<DiseaseResponse> call, Throwable t) {
                Toast.makeText(EnterSymptomsActivity.this, "API error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String safeText(String value, String fallback) {
        return TextUtils.isEmpty(value) ? fallback : value;
    }
}
