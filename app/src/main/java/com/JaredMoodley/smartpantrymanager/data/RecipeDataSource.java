package com.JaredMoodley.smartpantrymanager.data;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.JaredMoodley.smartpantrymanager.model.Recipe;
import com.JaredMoodley.smartpantrymanager.model.RecipeIngredient;

import java.util.ArrayList;

/**
 * Reads recipes and their required ingredients.
 *
 * Recipes are seeded once and never modified by the user, so this class is
 * read-only - there is no insert, update or delete. Keeping it separate from
 * PantryDataSource means each class deals with one part of the data model.
 */
public class RecipeDataSource {

    private SQLiteDatabase database;
    private final PantryDBHelper dbHelper;

    public RecipeDataSource(Context context) {
        dbHelper = new PantryDBHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    /**
     * Returns every recipe, sorted by name. Ingredients are not loaded here;
     * fetch them per recipe with getIngredientsForRecipe().
     */
    public ArrayList<Recipe> getAllRecipes() {
        ArrayList<Recipe> recipes = new ArrayList<>();
        Cursor cursor = null;

        try {
            String query = "SELECT * FROM " + PantryDBHelper.TABLE_RECIPE +
                    " ORDER BY " + PantryDBHelper.RECIPE_NAME;
            cursor = database.rawQuery(query, null);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                recipes.add(cursorToRecipe(cursor));
                cursor.moveToNext();
            }
        } catch (Exception e) {
            Log.e("PANTRY_DB", "Could not read recipes: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return recipes;
    }

    /**
     * Returns a single recipe, or null if no row has that id.
     */
    public Recipe getRecipeById(int id) {
        Recipe recipe = null;
        Cursor cursor = null;

        try {
            String query = "SELECT * FROM " + PantryDBHelper.TABLE_RECIPE +
                    " WHERE " + PantryDBHelper.RECIPE_ID + " = ?";
            cursor = database.rawQuery(query, new String[]{String.valueOf(id)});

            if (cursor.moveToFirst()) {
                recipe = cursorToRecipe(cursor);
            }
        } catch (Exception e) {
            Log.e("PANTRY_DB", "Could not read recipe " + id + ": " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return recipe;
    }

    /**
     * Returns the ingredients a recipe requires. This is the "many" side of
     * the one-to-many relationship, matched on the recipe_id foreign key.
     */
    public ArrayList<RecipeIngredient> getIngredientsForRecipe(int recipeId) {
        ArrayList<RecipeIngredient> ingredients = new ArrayList<>();
        Cursor cursor = null;

        try {
            String query = "SELECT * FROM " + PantryDBHelper.TABLE_RECIPE_INGREDIENT +
                    " WHERE " + PantryDBHelper.RI_RECIPE_ID + " = ?" +
                    " ORDER BY " + PantryDBHelper.RI_NAME;
            cursor = database.rawQuery(query, new String[]{String.valueOf(recipeId)});

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                ingredients.add(cursorToIngredient(cursor));
                cursor.moveToNext();
            }
        } catch (Exception e) {
            Log.e("PANTRY_DB", "Could not read ingredients for recipe "
                    + recipeId + ": " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return ingredients;
    }

    /** Convenience count, used to verify that seeding ran. */
    public int countRecipes() {
        return getAllRecipes().size();
    }

    private Recipe cursorToRecipe(Cursor cursor) {
        Recipe recipe = new Recipe();
        recipe.setId(cursor.getInt(
                cursor.getColumnIndexOrThrow(PantryDBHelper.RECIPE_ID)));
        recipe.setName(cursor.getString(
                cursor.getColumnIndexOrThrow(PantryDBHelper.RECIPE_NAME)));
        recipe.setSteps(cursor.getString(
                cursor.getColumnIndexOrThrow(PantryDBHelper.RECIPE_STEPS)));
        return recipe;
    }

    private RecipeIngredient cursorToIngredient(Cursor cursor) {
        RecipeIngredient ingredient = new RecipeIngredient();
        ingredient.setId(cursor.getInt(
                cursor.getColumnIndexOrThrow(PantryDBHelper.RI_ID)));
        ingredient.setRecipeId(cursor.getInt(
                cursor.getColumnIndexOrThrow(PantryDBHelper.RI_RECIPE_ID)));
        ingredient.setName(cursor.getString(
                cursor.getColumnIndexOrThrow(PantryDBHelper.RI_NAME)));
        ingredient.setQuantity(cursor.getDouble(
                cursor.getColumnIndexOrThrow(PantryDBHelper.RI_QUANTITY)));
        ingredient.setUnit(cursor.getString(
                cursor.getColumnIndexOrThrow(PantryDBHelper.RI_UNIT)));
        return ingredient;
    }
}