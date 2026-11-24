package com.example.campusmarket.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.campusmarket.model.Item;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "campus_market.db";
    private static final int DATABASE_VERSION = 2; // Incremented version to recreate tables with name column

    // Users Table
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USER_ID = "id";
    private static final String COLUMN_USER_NAME = "name";
    private static final String COLUMN_USER_EMAIL = "email";
    private static final String COLUMN_USER_PASSWORD = "password";

    // Items Table
    private static final String TABLE_ITEMS = "items";
    private static final String COLUMN_ITEM_ID = "id";
    private static final String COLUMN_ITEM_TITLE = "title";
    private static final String COLUMN_ITEM_CATEGORY = "category";
    private static final String COLUMN_ITEM_CONDITION = "condition";
    private static final String COLUMN_ITEM_DESCRIPTION = "description";
    private static final String COLUMN_ITEM_SELLER_EMAIL = "seller_email";
    private static final String COLUMN_ITEM_SELLER_PHONE = "seller_phone";
    private static final String COLUMN_ITEM_IMAGE_URL = "image_url";
    private static final String COLUMN_ITEM_PRICE = "price";
    private static final String COLUMN_ITEM_SOLD = "sold";
    private static final String COLUMN_ITEM_CREATED_AT = "created_at";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USER_NAME + " TEXT,"
                + COLUMN_USER_EMAIL + " TEXT UNIQUE,"
                + COLUMN_USER_PASSWORD + " TEXT" + ")";
        db.execSQL(CREATE_USERS_TABLE);

        String CREATE_ITEMS_TABLE = "CREATE TABLE " + TABLE_ITEMS + "("
                + COLUMN_ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_ITEM_TITLE + " TEXT,"
                + COLUMN_ITEM_CATEGORY + " TEXT,"
                + COLUMN_ITEM_CONDITION + " TEXT,"
                + COLUMN_ITEM_DESCRIPTION + " TEXT,"
                + COLUMN_ITEM_SELLER_EMAIL + " TEXT,"
                + COLUMN_ITEM_SELLER_PHONE + " TEXT,"
                + COLUMN_ITEM_IMAGE_URL + " TEXT,"
                + COLUMN_ITEM_PRICE + " REAL,"
                + COLUMN_ITEM_SOLD + " INTEGER,"
                + COLUMN_ITEM_CREATED_AT + " INTEGER" + ")";
        db.execSQL(CREATE_ITEMS_TABLE);

        // Insert default sample student user and items
        insertDefaultData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
        onCreate(db);
    }

    private void insertDefaultData(SQLiteDatabase db) {
        // Default test user
        ContentValues userValues = new ContentValues();
        userValues.put(COLUMN_USER_NAME, "Campus Student");
        userValues.put(COLUMN_USER_EMAIL, "student@campus.ac.ug");
        userValues.put(COLUMN_USER_PASSWORD, "password123");
        db.insert(TABLE_USERS, null, userValues);

        // Sample items
        insertSampleItem(db, "Calculus & Analytic Geometry textbook", "Textbooks", "Like New", "Standard engineering math textbook, 9th edition.", "alex@campus.ac.ug", "+256781234567", null, 45000);
        insertSampleItem(db, "HP Core i5 Laptop 8GB RAM 256GB SSD", "Electronics", "Used", "Fast student laptop, good battery backup.", "sarah@campus.ac.ug", "+256772345678", null, 850000);
        insertSampleItem(db, "Campus Hoodie Unisex Large", "Fashion", "New", "Brand new warm fleece hoodie.", "david@campus.ac.ug", "+256753456789", null, 35000);
        insertSampleItem(db, "Single Mattress & Pillow Set", "Hostel", "Used", "6 inch comfortable mattress for hostel room.", "grace@campus.ac.ug", "+256784567890", null, 120000);
        insertSampleItem(db, "Assignments & Thesis Printing Service", "Services", "New", "High quality color & B/W printing near main gate.", "printshop@campus.ac.ug", "+256775678901", null, 500);
        insertSampleItem(db, "Wireless Bluetooth Earbuds", "Electronics", "New", "Noise isolating earbuds with charging case.", "brian@campus.ac.ug", "+256756789012", null, 65000);
    }

    private void insertSampleItem(SQLiteDatabase db, String title, String category, String condition, String description, String sellerEmail, String sellerPhone, String imageUrl, double price) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ITEM_TITLE, title);
        values.put(COLUMN_ITEM_CATEGORY, category);
        values.put(COLUMN_ITEM_CONDITION, condition);
        values.put(COLUMN_ITEM_DESCRIPTION, description);
        values.put(COLUMN_ITEM_SELLER_EMAIL, sellerEmail);
        values.put(COLUMN_ITEM_SELLER_PHONE, sellerPhone);
        values.put(COLUMN_ITEM_IMAGE_URL, imageUrl);
        values.put(COLUMN_ITEM_PRICE, price);
        values.put(COLUMN_ITEM_SOLD, 0);
        values.put(COLUMN_ITEM_CREATED_AT, System.currentTimeMillis());
        db.insert(TABLE_ITEMS, null, values);
    }

    // User Operations
    public boolean registerUser(String name, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_NAME, name);
        values.put(COLUMN_USER_EMAIL, email);
        values.put(COLUMN_USER_PASSWORD, password);
        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;
    }

    public boolean loginUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_USER_ID},
                COLUMN_USER_EMAIL + "=? AND " + COLUMN_USER_PASSWORD + "=?",
                new String[]{email, password},
                null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public String getUserName(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_USER_NAME},
                COLUMN_USER_EMAIL + "=?",
                new String[]{email},
                null, null, null);
        String name = null;
        if (cursor.moveToFirst()) {
            name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_NAME));
        }
        cursor.close();
        return name != null && !name.trim().isEmpty() ? name : email;
    }

    // Item Operations
    public long insertItem(Item item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ITEM_TITLE, item.getTitle());
        values.put(COLUMN_ITEM_CATEGORY, item.getCategory());
        values.put(COLUMN_ITEM_CONDITION, item.getCondition());
        values.put(COLUMN_ITEM_DESCRIPTION, item.getDescription());
        values.put(COLUMN_ITEM_SELLER_EMAIL, item.getSellerEmail());
        values.put(COLUMN_ITEM_SELLER_PHONE, item.getSellerPhone());
        values.put(COLUMN_ITEM_IMAGE_URL, item.getImageUrl());
        values.put(COLUMN_ITEM_PRICE, item.getPrice());
        values.put(COLUMN_ITEM_SOLD, item.isSold() ? 1 : 0);
        values.put(COLUMN_ITEM_CREATED_AT, item.getCreatedAt());
        return db.insert(TABLE_ITEMS, null, values);
    }

    public void markItemAsSold(String itemId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ITEM_SOLD, 1);
        db.update(TABLE_ITEMS, values, COLUMN_ITEM_ID + "=?", new String[]{itemId});
    }

    public void deleteItem(String itemId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ITEMS, COLUMN_ITEM_ID + "=?", new String[]{itemId});
    }

    public List<Item> getAllItems() {
        List<Item> itemList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ITEMS + " ORDER BY " + COLUMN_ITEM_CREATED_AT + " DESC", null);

        if (cursor.moveToFirst()) {
            do {
                Item item = new Item();
                item.setId(String.valueOf(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ITEM_ID))));
                item.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ITEM_TITLE)));
                item.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ITEM_CATEGORY)));
                item.setCondition(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ITEM_CONDITION)));
                item.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ITEM_DESCRIPTION)));
                item.setSellerEmail(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ITEM_SELLER_EMAIL)));
                item.setSellerPhone(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ITEM_SELLER_PHONE)));
                item.setImageUrl(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ITEM_IMAGE_URL)));
                item.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_ITEM_PRICE)));
                item.setSold(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ITEM_SOLD)) == 1);
                item.setCreatedAt(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ITEM_CREATED_AT)));

                if (!item.isSold()) {
                    itemList.add(item);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return itemList;
    }
}
