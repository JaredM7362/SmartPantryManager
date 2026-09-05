package com.JaredMoodley.smartpantrymanager.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.JaredMoodley.smartpantrymanager.R;
import com.JaredMoodley.smartpantrymanager.adapter.RecipeAdapter;
import com.JaredMoodley.smartpantrymanager.data.PantryDataSource;
import com.JaredMoodley.smartpantrymanager.data.RecipeDataSource;
import com.JaredMoodley.smartpantrymanager.logic.RecipeMatcher;
import com.JaredMoodley.smartpantrymanager.model.PantryItem;
import com.JaredMoodley.smartpantrymanager.model.Recipe;
import com.JaredMoodley.smartpantrymanager.model.RecipeIngredient;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Lists only the recipes the user can cook from their current pantry.
 *
 * The strict-matching rule itself lives in RecipeMatcher; this screen reads
 * the data, hands it to the matcher, and displays whatever comes back.
 */
public class SuggestedRecipesActivity extends AppCompatActivity
        implements RecipeAdapter.OnRecipeClickListener {

    private RecyclerView recyclerRecipes;
    private View textEmptySuggestions;
    private RecipeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_suggested_recipes);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.suggestedRoot),
                (v, insets) -> {
                    Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
                    return insets;
                });

        MaterialToolbar toolbar = findViewById(R.id.toolbarSuggested);
        toolbar.setNavigationOnClickListener(v -> finish());

        recyclerRecipes = findViewById(R.id.recyclerRecipes);
        textEmptySuggestions = findViewById(R.id.textEmptySuggestions);

        recyclerRecipes.setLayoutManager(new LinearLayoutManager(this));
        recyclerRecipes.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        adapter = new RecipeAdapter(new ArrayList<>(), new HashMap<>(), this);
        recyclerRecipes.setAdapter(adapter);

        setUpBottomNavigation();
    }

    /**
     * Wires the bottom bar. Pantry finishes this screen rather than starting a
     * new one, because the pantry list is the launcher Activity and is already
     * underneath in the back stack - starting it again would stack a duplicate.
     */
    private void setUpBottomNavigation() {
        BottomNavigationView nav = findViewById(R.id.bottomNavRecipes);
        nav.setSelectedItemId(R.id.nav_recipes);

        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_recipes) {
                return true;   // already here
            }
            if (id == R.id.nav_pantry) {
                finish();
                return true;
            }
            if (id == R.id.nav_settings) {
                startActivity(new Intent(this, SettingsActivity.class));
                finish();
                return true;
            }
            return false;
        });
    }

    /**
     * Re-runs the match every time the screen is shown. The pantry may have
     * changed since the last visit, so the suggestions are recalculated rather
     * than cached - this is what makes a recipe appear or disappear
     * immediately after an ingredient is added or removed.
     */
    @Override
    protected void onResume() {
        super.onResume();
        loadSuggestions();
    }

    private void loadSuggestions() {
        PantryDataSource pantryDs = new PantryDataSource(this);
        RecipeDataSource recipeDs = new RecipeDataSource(this);

        ArrayList<Recipe> makeable = new ArrayList<>();
        Map<Integer, Integer> counts = new HashMap<>();

        try {
            pantryDs.open();
            ArrayList<PantryItem> pantry = pantryDs.getAllPantryItems();
            pantryDs.close();

            recipeDs.open();
            ArrayList<Recipe> allRecipes = recipeDs.getAllRecipes();

            // Each recipe's ingredients are fetched once and reused, rather
            // than queried again inside the matching loop.
            Map<Integer, List<RecipeIngredient>> ingredientsById = new HashMap<>();
            for (Recipe recipe : allRecipes) {
                List<RecipeIngredient> required =
                        recipeDs.getIngredientsForRecipe(recipe.getId());
                ingredientsById.put(recipe.getId(), required);
                counts.put(recipe.getId(), required.size());
            }
            recipeDs.close();

            RecipeMatcher matcher = new RecipeMatcher(pantry);
            makeable = matcher.filterMakeable(allRecipes, ingredientsById);

            Log.d("PANTRY_UI", "Pantry " + pantry.size() + " items, "
                    + makeable.size() + " of " + allRecipes.size() + " recipes makeable");

        } catch (Exception e) {
            Log.e("PANTRY_UI", "Could not load suggestions: " + e.getMessage());
            Toast.makeText(this, R.string.msg_recipe_not_found,
                    Toast.LENGTH_SHORT).show();
        }

        adapter.setRecipes(makeable, counts);
        showEmptyState(makeable.isEmpty());
    }

    private void showEmptyState(boolean isEmpty) {
        textEmptySuggestions.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        recyclerRecipes.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    /** Opens the detail screen, passing only the recipe's id. */
    @Override
    public void onRecipeClick(Recipe recipe) {
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, recipe.getId());
        startActivity(intent);
    }

}