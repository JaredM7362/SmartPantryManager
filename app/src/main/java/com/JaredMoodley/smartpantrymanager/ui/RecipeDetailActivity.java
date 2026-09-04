package com.JaredMoodley.smartpantrymanager.ui;

import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.JaredMoodley.smartpantrymanager.R;
import com.JaredMoodley.smartpantrymanager.data.RecipeDataSource;
import com.JaredMoodley.smartpantrymanager.model.Recipe;
import com.JaredMoodley.smartpantrymanager.model.RecipeIngredient;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;

/**
 * Read-only view of one recipe: its full ingredient list and method.
 *
 * Receives only the recipe's id through the Intent and reads the record
 * itself, rather than having the whole object passed across. Passing an id is
 * cheaper, avoids making the model classes serializable, and guarantees the
 * screen shows current data rather than a copy made earlier.
 */
public class RecipeDetailActivity extends AppCompatActivity {

    public static final String EXTRA_RECIPE_ID = "recipeId";
    private static final int NO_ID = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recipe_detail);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.detailRoot),
                (v, insets) -> {
                    Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
                    return insets;
                });

        MaterialToolbar toolbar = findViewById(R.id.toolbarDetail);
        toolbar.setNavigationOnClickListener(v -> finish());

        int recipeId = getIntent().getIntExtra(EXTRA_RECIPE_ID, NO_ID);
        if (recipeId == NO_ID) {
            // Reached without an id, which should not happen. Fail visibly
            // rather than showing an empty screen.
            Toast.makeText(this, R.string.msg_recipe_not_found, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadRecipe(recipeId);
    }

    private void loadRecipe(int recipeId) {
        RecipeDataSource ds = new RecipeDataSource(this);

        try {
            ds.open();
            Recipe recipe = ds.getRecipeById(recipeId);

            if (recipe == null) {
                ds.close();
                Toast.makeText(this, R.string.msg_recipe_not_found,
                        Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            ArrayList<RecipeIngredient> ingredients =
                    ds.getIngredientsForRecipe(recipeId);
            ds.close();

            MaterialToolbar toolbar = findViewById(R.id.toolbarDetail);
            toolbar.setTitle(recipe.getName());

            TextView textName = findViewById(R.id.textDetailName);
            textName.setText(recipe.getName());

            TextView textSteps = findViewById(R.id.textDetailSteps);
            textSteps.setText(recipe.getSteps());

            showIngredients(ingredients);

        } catch (Exception e) {
            Log.e("PANTRY_UI", "Could not load recipe " + recipeId + ": " + e.getMessage());
            Toast.makeText(this, R.string.msg_recipe_not_found, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * Builds one TextView per ingredient and adds it to the container.
     */
    private void showIngredients(ArrayList<RecipeIngredient> ingredients) {
        LinearLayout container = findViewById(R.id.containerIngredients);
        container.removeAllViews();

        for (RecipeIngredient ingredient : ingredients) {
            TextView line = new TextView(this);
            line.setText("•  " + formatIngredient(ingredient));
            line.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            line.setPadding(0, 6, 0, 6);
            container.addView(line);
        }
    }

    /** Renders "3 unit garlic" as "3 garlic", and drops trailing ".0". */
    private String formatIngredient(RecipeIngredient ingredient) {
        double qty = ingredient.getQuantity();
        String number = (qty == Math.floor(qty))
                ? String.valueOf((int) qty)
                : String.valueOf(qty);

        String unit = ingredient.getUnit();
        boolean showUnit = unit != null
                && !unit.trim().isEmpty()
                && !unit.equalsIgnoreCase("unit");

        return showUnit
                ? number + " " + unit + " " + ingredient.getName()
                : number + " " + ingredient.getName();
    }
}