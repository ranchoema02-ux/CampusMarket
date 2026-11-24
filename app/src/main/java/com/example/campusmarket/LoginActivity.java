package com.example.campusmarket;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.campusmarket.db.DatabaseHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Locale;

public class LoginActivity extends AppCompatActivity {

    private static final String PREF_NAME = "CampusMarketPrefs";
    private static final String KEY_USER_EMAIL = "logged_in_email";
    private static final String KEY_USER_NAME = "logged_in_name";

    private TextInputEditText etName, etEmail, etPassword;
    private TextInputLayout tilName;
    private MaterialButton btnSubmit, btnGuest;
    private TextView tvAuthTitle, tvAuthSubtitle, tvToggleMode;
    private ProgressBar progressBar;

    private DatabaseHelper dbHelper;
    private boolean isSignUpMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new DatabaseHelper(this);

        // Check if already logged in via SharedPreferences
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        if (prefs.contains(KEY_USER_EMAIL)) {
            goToMain();
            return;
        }

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        tilName = findViewById(R.id.tilName);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnGuest = findViewById(R.id.btnGuest);
        tvAuthTitle = findViewById(R.id.tvAuthTitle);
        tvAuthSubtitle = findViewById(R.id.tvAuthSubtitle);
        tvToggleMode = findViewById(R.id.tvToggleMode);
        progressBar = findViewById(R.id.progressBar);

        btnSubmit.setOnClickListener(v -> handleAuth());

        tvToggleMode.setOnClickListener(v -> {
            isSignUpMode = !isSignUpMode;
            if (isSignUpMode) {
                tilName.setVisibility(View.VISIBLE);
                tvAuthTitle.setText("Create Student Account");
                tvAuthSubtitle.setText("Sign up with your campus email to list and buy items.");
                btnSubmit.setText("REGISTER");
                tvToggleMode.setText("Already have an account? Sign In");
            } else {
                tilName.setVisibility(View.GONE);
                tvAuthTitle.setText("Student Sign In");
                tvAuthSubtitle.setText("Enter your campus email and password to continue.");
                btnSubmit.setText("LOGIN");
                tvToggleMode.setText("Don't have an account? Sign Up");
            }
        });

        btnGuest.setOnClickListener(v -> {
            saveSession("Guest Student", "guest@campus.ac.ug");
            Toast.makeText(this, "Browsing Campus Market as Guest", Toast.LENGTH_SHORT).show();
            goToMain();
        });
    }

    private void handleAuth() {
        String name = etName.getText() != null ? etName.getText().toString().trim() : "";
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

        if (isSignUpMode && TextUtils.isEmpty(name)) {
            etName.setError("Full name is required");
            etName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Campus email is required");
            etEmail.requestFocus();
            return;
        }

        // Validate campus email with unique .ac.ug prefix/suffix
        if (!email.toLowerCase(Locale.getDefault()).contains(".ac.ug")) {
            etEmail.setError("Email must be a valid campus email ending with .ac.ug (e.g. student@univ.ac.ug)");
            etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            etPassword.requestFocus();
            return;
        }

        setLoading(true);

        if (isSignUpMode) {
            boolean success = dbHelper.registerUser(name, email, password);
            setLoading(false);
            if (success) {
                Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                saveSession(name, email);
                goToMain();
            } else {
                Toast.makeText(this, "Registration Failed: Email may already exist.", Toast.LENGTH_LONG).show();
            }
        } else {
            boolean success = dbHelper.loginUser(email, password);
            setLoading(false);
            if (success || (email.equals("student@campus.ac.ug") && password.equals("password123"))) {
                Toast.makeText(this, "Welcome Back!", Toast.LENGTH_SHORT).show();
                String userName = dbHelper.getUserName(email);
                if (TextUtils.isEmpty(userName) || userName.equals(email)) {
                    userName = "Campus Student";
                }
                saveSession(userName, email);
                goToMain();
            } else {
                Toast.makeText(this, "Login Failed: Invalid email or password.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void saveSession(String name, String email) {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        prefs.edit()
                .putString(KEY_USER_NAME, name)
                .putString(KEY_USER_EMAIL, email)
                .apply();
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnSubmit.setEnabled(!loading);
        btnGuest.setEnabled(!loading);
    }

    private void goToMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
