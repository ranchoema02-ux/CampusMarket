package com.example.campusmarket;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.campusmarket.db.DatabaseHelper;
import com.example.campusmarket.model.Item;
import com.google.android.material.button.MaterialButton;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class ItemDetailsActivity extends AppCompatActivity {

    public static final String EXTRA_ITEM = "extra_item";
    private static final String PREF_NAME = "CampusMarketPrefs";
    private static final String KEY_USER_EMAIL = "logged_in_email";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        Item item = null;
        if (getIntent() != null && getIntent().hasExtra(EXTRA_ITEM)) {
            item = (Item) getIntent().getSerializableExtra(EXTRA_ITEM);
        }

        if (item == null) {
            Toast.makeText(this, "Item not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        final Item currentItem = item;
        DatabaseHelper dbHelper = new DatabaseHelper(this);

        // Get currently logged-in user email
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String loggedInEmail = prefs.getString(KEY_USER_EMAIL, "");

        TextView tvTitle = findViewById(R.id.tvTitle);
        TextView tvPrice = findViewById(R.id.tvPrice);
        TextView tvCategory = findViewById(R.id.tvCategory);
        TextView tvCondition = findViewById(R.id.tvCondition);
        TextView tvDescription = findViewById(R.id.tvDescription);
        TextView tvSellerEmail = findViewById(R.id.tvSellerEmail);
        TextView tvSellerPhone = findViewById(R.id.tvSellerPhone);
        ImageView ivProductImage = findViewById(R.id.ivProductImage);

        MaterialButton btnCallSeller = findViewById(R.id.btnCallSeller);
        MaterialButton btnWhatsAppSeller = findViewById(R.id.btnWhatsAppSeller);
        MaterialButton btnMarkSold = findViewById(R.id.btnMarkSold);

        tvTitle.setText(currentItem.getTitle() != null ? currentItem.getTitle() : "Untitled Item");
        tvPrice.setText(String.format(Locale.US, "UGX %,.0f", currentItem.getPrice()));
        tvCategory.setText(currentItem.getCategory() != null ? currentItem.getCategory().toUpperCase(Locale.getDefault()) : "OTHER");
        tvCondition.setText(currentItem.getCondition() != null ? currentItem.getCondition().toUpperCase(Locale.getDefault()) : "USED");

        String desc = currentItem.getDescription();
        tvDescription.setText(TextUtils.isEmpty(desc) ? "No additional details provided by seller." : desc);

        String email = currentItem.getSellerEmail() != null ? currentItem.getSellerEmail() : "student@campus.ac.ug";
        tvSellerEmail.setText(email);

        String phone = currentItem.getSellerPhone() != null ? currentItem.getSellerPhone() : "+256 780 000 000";
        tvSellerPhone.setText(phone);

        // Restriction: Allow ONLY the seller who posted the item to mark it as sold / remove it
        boolean isOwner = !TextUtils.isEmpty(loggedInEmail) && loggedInEmail.equalsIgnoreCase(currentItem.getSellerEmail());
        if (isOwner) {
            btnMarkSold.setVisibility(View.VISIBLE);
        } else {
            btnMarkSold.setVisibility(View.GONE);
        }

        // Load Product Image if available
        String imageUrl = currentItem.getImageUrl();
        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            ivProductImage.setVisibility(View.VISIBLE);
            Glide.with(this).load(imageUrl).into(ivProductImage);
        } else {
            ivProductImage.setVisibility(View.GONE);
        }

        // Mark as Sold / Remove Action
        btnMarkSold.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Mark as Sold / Remove")
                    .setMessage("Are you sure you want to mark this item as sold? It will be removed from the marketplace list.")
                    .setPositiveButton("YES, REMOVE", (dialog, which) -> {
                        if (currentItem.getId() != null) {
                            dbHelper.markItemAsSold(currentItem.getId());
                        }
                        Toast.makeText(this, "Item marked as sold and removed successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .setNegativeButton("CANCEL", null)
                    .show();
        });

        // Call Seller Action
        btnCallSeller.setOnClickListener(v -> {
            if (TextUtils.isEmpty(phone)) {
                Toast.makeText(this, "Seller phone number not available", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
            startActivity(callIntent);
        });

        // WhatsApp Chat Action
        btnWhatsAppSeller.setOnClickListener(v -> {
            if (TextUtils.isEmpty(phone)) {
                Toast.makeText(this, "Seller phone number not available", Toast.LENGTH_SHORT).show();
                return;
            }
            String cleanPhone = phone.replaceAll("[^0-9]", "");
            if (cleanPhone.startsWith("0")) {
                cleanPhone = "256" + cleanPhone.substring(1);
            }
            try {
                String message = "Hello, I am interested in buying your CampusMarket item: " + currentItem.getTitle();
                String url = "https://api.whatsapp.com/send?phone=" + cleanPhone + "&text=" + URLEncoder.encode(message, StandardCharsets.UTF_8.toString());
                Intent waIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(waIntent);
            } catch (Exception e) {
                Toast.makeText(this, "Unable to open WhatsApp: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
