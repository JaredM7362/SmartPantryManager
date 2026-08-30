package com.JaredMoodley.smartpantrymanager.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.JaredMoodley.smartpantrymanager.model.PantryItem;

import java.util.ArrayList;
public class PantryDataSource {

    private SQLiteDatabase database;
    private final PantryDBHelper dbHelper;

    public PantryDataSource(Context context) {
        dbHelper = new PantryDBHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    /**
     * Inserts a new pantry item.
     */
    public boolean insertPantryItem(PantryItem item) {
        boolean didSucceed = false;
        try {
            ContentValues values = new ContentValues();
            values.put(PantryDBHelper.PANTRY_NAME, item.getName());
            values.put(PantryDBHelper.PANTRY_QUANTITY, item.getQuantity());
            values.put(PantryDBHelper.PANTRY_UNIT, item.getUnit());
            values.put(PantryDBHelper.PANTRY_EXPIRY, item.getExpiryDate());

            // insert() returns the new row's id, or -1 if the insert failed
            long insertId = database.insert(PantryDBHelper.TABLE_PANTRY, null, values);
            didSucceed = insertId > 0;

        } catch (Exception e) {
            Log.e("PANTRY_DB", "Insert failed: " + e.getMessage());
        }
        return didSucceed;
    }

    /**
     * Updates a existing pantry item.
     */
    public boolean updatePantryItem(PantryItem item) {
        boolean didSucceed = false;
        try {
            // The values to write - identical to insert, because every
            // editable field can be changed on the edit screen.
            ContentValues values = new ContentValues();
            values.put(PantryDBHelper.PANTRY_NAME, item.getName());
            values.put(PantryDBHelper.PANTRY_QUANTITY, item.getQuantity());
            values.put(PantryDBHelper.PANTRY_UNIT, item.getUnit());
            values.put(PantryDBHelper.PANTRY_EXPIRY, item.getExpiryDate());

            // The row to write them to. The id is supplied through the args
            // array rather than concatenated into the where clause.
            String whereClause = PantryDBHelper.PANTRY_ID + " = ?";
            String[] whereArgs = new String[]{ String.valueOf(item.getId()) };

            int rowsAffected = database.update(
                    PantryDBHelper.TABLE_PANTRY, values, whereClause, whereArgs);

            didSucceed = rowsAffected > 0;

        } catch (Exception e) {
            Log.e("PANTRY_DB", "Update failed: " + e.getMessage());
        }
        return didSucceed;
    }

    /**
     * Deletes the pantry item with the given id.
     */
    public boolean deletePantryItem(int id) {
        boolean didSucceed = false;
        try {
            String whereClause = PantryDBHelper.PANTRY_ID + " = ?";
            String[] whereArgs = new String[]{ String.valueOf(id) };

            int rowsAffected = database.delete(
                    PantryDBHelper.TABLE_PANTRY, whereClause, whereArgs);

            didSucceed = rowsAffected > 0;

        } catch (Exception e) {
            Log.e("PANTRY_DB", "Delete failed: " + e.getMessage());
        }
        return didSucceed;
    }
    /**
     * Builds a PantryItem from the row the cursor is currently sitting on.
     */
    private PantryItem cursorToPantryItem(Cursor cursor) {
        PantryItem item = new PantryItem();
        item.setId(cursor.getInt(
                cursor.getColumnIndexOrThrow(PantryDBHelper.PANTRY_ID)));
        item.setName(cursor.getString(
                cursor.getColumnIndexOrThrow(PantryDBHelper.PANTRY_NAME)));
        item.setQuantity(cursor.getDouble(
                cursor.getColumnIndexOrThrow(PantryDBHelper.PANTRY_QUANTITY)));
        item.setUnit(cursor.getString(
                cursor.getColumnIndexOrThrow(PantryDBHelper.PANTRY_UNIT)));
        item.setExpiryDate(cursor.getString(
                cursor.getColumnIndexOrThrow(PantryDBHelper.PANTRY_EXPIRY)));
        return item;
    }
    /**
     * Returns a single pantry item, or null if no row has that id.
     */
    public PantryItem getPantryItemById(int id) {
        PantryItem item = null;          // stays null if nothing is found
        Cursor cursor = null;

        try {
            String query = "SELECT * FROM " + PantryDBHelper.TABLE_PANTRY +
                    " WHERE " + PantryDBHelper.PANTRY_ID + " = ?";

            cursor = database.rawQuery(query, new String[]{ String.valueOf(id) });

            if (cursor.moveToFirst()) {
                item = cursorToPantryItem(cursor);
            }


        } catch (Exception e) {
            Log.e("PANTRY_DB", "Could not read item " + id + ": " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return item;
    }
    /**
     * Returns every pantry item, sorted alphabetically.
     */
    public ArrayList<PantryItem> getAllPantryItems() {
        ArrayList<PantryItem> items = new ArrayList<>();
        Cursor cursor = null;

        try {
            String query = "SELECT * FROM " + PantryDBHelper.TABLE_PANTRY +
                    " ORDER BY " + PantryDBHelper.PANTRY_NAME;
            cursor = database.rawQuery(query, null);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                items.add(cursorToPantryItem(cursor));
                cursor.moveToNext();
            }
        } catch (Exception e) {
            Log.e("PANTRY_DB", "Could not read pantry items: " + e.getMessage());
        } finally {
            // A cursor holds native memory and must always be released,
            // including when an exception occurred part-way through.
            if (cursor != null) {
                cursor.close();
            }
        }
        return items;
    }




}
