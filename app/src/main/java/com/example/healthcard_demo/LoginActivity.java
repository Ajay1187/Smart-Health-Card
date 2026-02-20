package com.example.healthcard_demo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private TextView tvRegister, tvForgetPassword;
    private Button btnLogin;
    private EditText edtMedicalId, edtPassword;
    private TestAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // MUST be first

        // Initialize views
        tvRegister = findViewById(R.id.register);
        tvForgetPassword = findViewById(R.id.password_link);
        btnLogin = findViewById(R.id.btn_login);
        edtMedicalId = findViewById(R.id.txt_mobile);
        edtPassword = findViewById(R.id.txt_password);

        // Initialize database adapter safely
        try {
            adapter = new TestAdapter(this);
            adapter.createDatabase();
            adapter.open();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Database error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        // Click listener for forget password
        tvForgetPassword.setOnClickListener(view -> {
            Intent i = new Intent(LoginActivity.this, ForgetPassword.class);
            startActivity(i);
        });

        // Click listener for register
        tvRegister.setOnClickListener(view -> {
            Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(i);
        });

        // Click listener for login
        btnLogin.setOnClickListener(view -> {
            String medicalId = edtMedicalId.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if (TextUtils.isEmpty(medicalId)) {
                edtMedicalId.setError("Medical Id is Required.");
                return;
            }

            if (TextUtils.isEmpty(password)) {
                edtPassword.setError("Password is Required.");
                return;
            }

            // Check login in database
            int i = adapter.checkUserLogin(medicalId, password);
            if (i == 1) {
                userLogin(medicalId);
                return;
            }

            // Default doctor login
            if (medicalId.equalsIgnoreCase("9197") && password.equalsIgnoreCase("1234")) {
                Intent intent = new Intent(getApplicationContext(), DoctorHomeActivity.class);
                startActivity(intent);
                Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(LoginActivity.this, "Invalid Medical ID Or Password", Toast.LENGTH_SHORT).show();
        });
    }

    private void userLogin(String medicalId) {
        final ProgressDialog dialog = new ProgressDialog(LoginActivity.this);
        dialog.setIcon(R.drawable.login);
        dialog.setTitle("Login");
        dialog.setMessage("Please wait, User Login is Processing...");
        dialog.show();

        // Delay to simulate login process
        new Handler().postDelayed(() -> {
            dialog.dismiss();

            Intent i = new Intent(LoginActivity.this, UserHomeActivity.class);
            i.putExtra("Key", medicalId);
            startActivity(i);

            Toast.makeText(getApplicationContext(), "Your Login is Successful...", Toast.LENGTH_SHORT).show();

            edtMedicalId.setText("");
            edtPassword.setText("");
        }, 3000); // Reduced to 3 seconds for better UX
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adapter != null) {
            adapter.close(); // Close database safely
        }
    }
}
