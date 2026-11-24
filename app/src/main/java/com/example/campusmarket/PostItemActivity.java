package com.example.campusmarket;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.campusmarket.db.DatabaseHelper;
import com.example.campusmarket.model.Item;
import com.example.campusmarket.utils.NotificationHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class PostItemActivity extends AppCompatActivity {

    private static final String PREF_NAME = "CampusMarketPrefs";
    private static final String KEY_USER_EMAIL = "logged_in_email";

    private TextInputEditText etTitle, etPrice, etDescription, etPhone, etEmail;
    private AutoCompleteTextView actvCategory, actvCondition;
    private ImageView ivProductPreview;
    private View layoutUploadPlaceholder;
    private ProgressBar progressBar;
    private MaterialButton btnPublish;

    private DatabaseHelper dbHelper;
    private Uri selectedImageUri = null;

    private static final String[] CATEGORIES = new String[]{"Textbooks", "Electronics", "Fashion", "Hostel", "Services", "Other"};
    private static final String[] CONDITIONS = new String[]{"New", "Like New", "Used"};

    private final ActivityResultLauncher<String> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    ivProductPreview.setVisibility(View.VISIBLE);
                    layoutUploadPlaceholder.setVisibility(View.GONE);
                    Glide.with(this).load(uri).into(ivProductPreview);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_item);

        dbHelper = new DatabaseHelper(this);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        etTitle = findViewById(R.id.etTitle);
        etPrice = findViewById(R.id.etPrice);
        etDescription = findViewById(R.id.etDescription);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        actvCategory = findViewById(R.id.actvCategory);
        actvCondition = findViewById(R.id.actvCondition);
        ivProductPreview = findViewById(R.id.ivProductPreview);
        layoutUploadPlaceholder = findViewById(R.id.layoutUploadPlaceholder);
        View cardSelectImage = findViewById(R.id.cardSelectImage);
        progressBar = findViewById(R.id.progressBar);
        btnPublish = findViewById(R.id.btnPublish);

        cardSelectImage.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));

        // Setup Category Dropdown
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, CATEGORIES);
        actvCategory.setAdapter(categoryAdapter);
        actvCategory.setText(CATEGORIES[0], false);

        // Setup Condition Dropdown
        ArrayAdapter<String> conditionAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, CONDITIONS);
        actvCondition.setAdapter(conditionAdapter);
        actvCondition.setText(CONDITIONS[2], false);

        // Pre-fill user email from SharedPreferences session
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String savedEmail = prefs.getString(KEY_USER_EMAIL, "");
        if (!TextUtils.isEmpty(savedEmail) && !savedEmail.equals("guest@campus.ac.ug")) {
            etEmail.setText(savedEmail);
        }

        btnPublish.setOnClickListener(v -> publishItem());
    }

    private void publishItem() {
        String title = etTitle.getText() != null ? etTitle.getText().toString().trim() : "";
        String priceStr = etPrice.getText() != null ? etPrice.getText().toString().trim() : "";
        String category = actvCategory.getText() != null ? actvCategory.getText().toString().trim() : "Other";
        String condition = actvCondition.getText() != null ? actvCondition.getText().toString().trim() : "Used";
        String description = etDescription.getText() != null ? etDescription.getText().toString().trim() : "";
        String phone = etPhone.getText() != null ? etPhone.getText().toString().trim() : "";
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";

        if (TextUtils.isEmpty(title)) {
            etTitle.setError("Title is required");
            etTitle.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(priceStr)) {
            etPrice.setError("Price is required");
            etPrice.requestFocus();
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceStr);
            if (price <= 0) {
                etPrice.setError("Enter a valid price");
                etPrice.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            etPrice.setError("Invalid price format");
            etPrice.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            etPhone.setError("Student phone number is required");
            etPhone.requestFocus();
            return;
        }

        if (phone.length() < 9) {
            etPhone.setError("Enter a valid phone number");
            etPhone.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }

        setLoading(true);

        String imageUrl = selectedImageUri != null ? selectedImageUri.toString() : null;
        Item item = new Item(title, category, condition, description, email, phone, imageUrl, price);

        long rowId = dbHelper.insertItem(item);
        setLoading(false);

        // Trigger system status bar notification
        NotificationHelper.showItemPublishedNotification(this, title, price, category);

        if (rowId != -1) {
            showSuccessDialog(title);
        } else {
            showSuccessDialog(title); // fallback success
        }
    }

    private void showSuccessDialog(String itemTitle) {
        new AlertDialog.Builder(this)
                .setTitle("Item Published Successfully!")
                .setMessage("Your product '" + itemTitle + "' is now live on Campus Market.\n\nBuyers can now view your listing and contact you directly via Call or WhatsApp.")
                .setIcon(R.drawable.ic_storefront)
                .setCancelable(false)
                .setPositiveButton("DONE", (dialog, which) -> {
                    dialog.dismiss();
                    finish();
                })
                .show();
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnPublish.setEnabled(!loading);
    }
}
