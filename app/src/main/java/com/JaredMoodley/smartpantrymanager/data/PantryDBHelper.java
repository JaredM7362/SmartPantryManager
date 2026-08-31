package com.JaredMoodley.smartpantrymanager.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
public class PantryDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "smartpantry.db";
    private static final int DATABASE_VERSION = 2;

    public static final String TABLE_PANTRY = "pantry_item";
    public static final String PANTRY_ID = "_id";
    public static final String PANTRY_NAME = "name";
    public static final String PANTRY_QUANTITY = "quantity";
    public static final String PANTRY_UNIT = "unit";
    public static final String PANTRY_EXPIRY = "expiry_date";

    public static final String TABLE_RECIPE = "recipe";
    public static final String RECIPE_ID = "_id";
    public static final String RECIPE_NAME = "name";
    public static final String RECIPE_STEPS = "steps";

    public static final String TABLE_RECIPE_INGREDIENT = "recipe_ingredient";
    public static final String RI_ID = "_id";
    public static final String RI_RECIPE_ID = "recipe_id";
    public static final String RI_NAME = "name";
    public static final String RI_QUANTITY = "quantity";
    public static final String RI_UNIT = "unit";

    private static final String CREATE_TABLE_PANTRY =
            "CREATE TABLE " + TABLE_PANTRY + " (" +
                    PANTRY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    PANTRY_NAME + " TEXT NOT NULL, " +
                    PANTRY_QUANTITY + " REAL, " +
                    PANTRY_UNIT + " TEXT, " +
                    PANTRY_EXPIRY + " TEXT)";

    private static final String CREATE_TABLE_RECIPE =
            "CREATE TABLE " + TABLE_RECIPE + " (" +
                    RECIPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    RECIPE_NAME + " TEXT NOT NULL, " +
                    RECIPE_STEPS + " TEXT)";

    private static final String CREATE_TABLE_RECIPE_INGREDIENT =
            "CREATE TABLE " + TABLE_RECIPE_INGREDIENT + " (" +
                    RI_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    RI_RECIPE_ID + " INTEGER NOT NULL, " +
                    RI_NAME + " TEXT NOT NULL, " +
                    RI_QUANTITY + " REAL, " +
                    RI_UNIT + " TEXT, " +
                    "FOREIGN KEY (" + RI_RECIPE_ID + ") REFERENCES " +
                    TABLE_RECIPE + "(" + RECIPE_ID + ") ON DELETE CASCADE)";

    public PantryDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Runs once, the first time the database file is created on the device.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_PANTRY);
        db.execSQL(CREATE_TABLE_RECIPE);
        db.execSQL(CREATE_TABLE_RECIPE_INGREDIENT);
        Log.d("PANTRY_DB", "Database tables created");
        RecipeSeeder.seed(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w("PANTRY_DB", "Upgrading from version " + oldVersion +
                " to " + newVersion + " - existing data will be lost");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE_INGREDIENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PANTRY);
        onCreate(db);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }
}
