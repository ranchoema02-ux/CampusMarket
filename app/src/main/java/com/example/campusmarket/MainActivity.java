package com.example.campusmarket;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campusmarket.adapter.ItemAdapter;
import com.example.campusmarket.db.DatabaseHelper;
import com.example.campusmarket.model.Item;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String PREF_NAME = "CampusMarketPrefs";
    private static final String KEY_USER_EMAIL = "logged_in_email";
    private static final String KEY_USER_NAME = "logged_in_name";

    private DatabaseHelper dbHelper;
    private ItemAdapter adapter;

    private LinearLayout layoutEmpty;
    private TextView tvUserStatus;
    private ChipGroup chipGroupCategories;

    private final List<Item> allItems = new ArrayList<>();
    private final List<Item> shownItems = new ArrayList<>();
    private String currentQuery = "";
    private String selectedCategory = "All";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String currentUserEmail = prefs.getString(KEY_USER_EMAIL, null);
        String currentUserName = prefs.getString(KEY_USER_NAME, "Campus Student");

        if (currentUserEmail == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);
        dbHelper = new DatabaseHelper(this);

        layoutEmpty = findViewById(R.id.layoutEmpty);
        tvUserStatus = findViewById(R.id.tvUserStatus);
        RecyclerView rvItems = findViewById(R.id.rvItems);
        SearchView searchView = findViewById(R.id.searchView);
        chipGroupCategories = findViewById(R.id.chipGroupCategories);
        ImageButton btnLogout = findViewById(R.id.btnLogout);

        tvUserStatus.setText(currentUserName);

        // Logout action
        btnLogout.setOnClickListener(v -> {
            prefs.edit().remove(KEY_USER_EMAIL).remove(KEY_USER_NAME).apply();
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        // 2-Column Jumia Grid Layout
        rvItems.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new ItemAdapter(shownItems, item -> {
            Intent intent = new Intent(this, ItemDetailsActivity.class);
            intent.putExtra(ItemDetailsActivity.EXTRA_ITEM, item);
            startActivity(intent);
        });
        rvItems.setAdapter(adapter);

        // Search Listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                currentQuery = newText == null ? "" : newText.trim().toLowerCase(Locale.getDefault());
                applyFilter();
                return true;
            }
        });

        // Category Chips Listener
        chipGroupCategories.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                selectedCategory = "All";
            } else {
                int chipId = checkedIds.get(0);
                Chip chip = findViewById(chipId);
                if (chip != null) {
                    String text = chip.getText().toString();
                    selectedCategory = text.startsWith("All") ? "All" : text;
                }
            }
            applyFilter();
        });

        // Floating Action Button
        findViewById(R.id.fabPost).setOnClickListener(v ->
                startActivity(new Intent(this, PostItemActivity.class))
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadItemsFromDb();
    }

    private void loadItemsFromDb() {
        allItems.clear();
        List<Item> itemsFromDb = dbHelper.getAllItems();
        if (itemsFromDb != null) {
            allItems.addAll(itemsFromDb);
        }
        applyFilter();
    }

    private void applyFilter() {
        shownItems.clear();
        for (Item item : allItems) {
            String title = item.getTitle() == null ? "" : item.getTitle().toLowerCase(Locale.getDefault());
            String category = item.getCategory() == null ? "" : item.getCategory().toLowerCase(Locale.getDefault());

            boolean matchesSearch = currentQuery.isEmpty() || title.contains(currentQuery) || category.contains(currentQuery);
            boolean matchesCategory = selectedCategory.equalsIgnoreCase("All") || category.contains(selectedCategory.toLowerCase(Locale.getDefault()));

            if (matchesSearch && matchesCategory) {
                shownItems.add(item);
            }
        }
        adapter.notifyDataSetChanged();
        if (layoutEmpty != null) {
            layoutEmpty.setVisibility(shownItems.isEmpty() ? View.VISIBLE : View.GONE);
        }
    }
}
