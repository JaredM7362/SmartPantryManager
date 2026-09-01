package com.JaredMoodley.smartpantrymanager.logic;

import com.JaredMoodley.smartpantrymanager.model.PantryItem;
import com.JaredMoodley.smartpantrymanager.model.Recipe;
import com.JaredMoodley.smartpantrymanager.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Decides which recipes can be cooked from the current pantry.
 */
public class RecipeMatcher {

    /** The pantry, keyed by normalised ingredient name for fast lookup. */
    private final Map<String, PantryItem> pantryByName;

    /**
     * Builds the lookup once, rather than searching the pantry list again for
     * every ingredient of every recipe.
     */
    public RecipeMatcher(List<PantryItem> pantryItems) {
        pantryByName = new HashMap<>();

        for (PantryItem item : pantryItems) {
            String key = IngredientNormaliser.normalise(item.getName());
            if (key.isEmpty()) {
                continue;
            }

            PantryItem existing = pantryByName.get(key);
            if (existing == null) {
                pantryByName.put(key, item);
            } else if (sameUnitFamily(existing, item)) {
                // Combine into a copy so the caller's objects are not altered.
                PantryItem combined = new PantryItem();
                combined.setId(existing.getId());
                combined.setName(existing.getName());
                combined.setUnit(existing.getUnit());
                combined.setQuantity(
                        UnitConverter.toBase(existing.getQuantity(), existing.getUnit())
                                + UnitConverter.toBase(item.getQuantity(), item.getUnit()));
                combined.setUnit(baseUnitOf(existing.getUnit()));
                pantryByName.put(key, combined);
            }
        }
    }

    /**
     * Returns true when every ingredient the recipe requires is available.
     * Stops at the first failure - once one ingredient is missing the recipe
     * is disqualified, so there is nothing to gain by checking the rest.
     */
    public boolean canMake(List<RecipeIngredient> required) {
        for (RecipeIngredient ingredient : required) {
            if (!isSatisfied(ingredient)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Filters a set of recipes down to those that can be cooked right now.
     *
     * @param recipes         every recipe in the database
     * @param ingredientsById that recipe's required ingredients, keyed by recipe id
     */
    public ArrayList<Recipe> filterMakeable(
            List<Recipe> recipes,
            Map<Integer, List<RecipeIngredient>> ingredientsById) {

        ArrayList<Recipe> makeable = new ArrayList<>();

        for (Recipe recipe : recipes) {
            List<RecipeIngredient> required = ingredientsById.get(recipe.getId());

            // A recipe with no ingredients recorded is a data fault, not a
            // recipe that needs nothing. Excluded rather than always shown.
            if (required == null || required.isEmpty()) {
                continue;
            }

            if (canMake(required)) {
                makeable.add(recipe);
            }
        }
        return makeable;
    }

    /**
     * How many of a recipe's ingredients are missing.
     */
    public int countMissing(List<RecipeIngredient> required) {
        int missing = 0;
        for (RecipeIngredient ingredient : required) {
            if (!isSatisfied(ingredient)) {
                missing++;
            }
        }
        return missing;
    }

    /** True when the pantry holds this ingredient in at least the amount needed. */
    private boolean isSatisfied(RecipeIngredient ingredient) {
        String key = IngredientNormaliser.normalise(ingredient.getName());
        PantryItem inPantry = pantryByName.get(key);

        if (inPantry == null) {
            return false;   // not present at all
        }

        return UnitConverter.hasEnough(
                inPantry.getQuantity(), inPantry.getUnit(),
                ingredient.getQuantity(), ingredient.getUnit());
    }

    private boolean sameUnitFamily(PantryItem a, PantryItem b) {
        return UnitConverter.familyOf(a.getUnit())
                == UnitConverter.familyOf(b.getUnit());
    }

    private String baseUnitOf(String unit) {
        switch (UnitConverter.familyOf(unit)) {
            case MASS:   return "g";
            case VOLUME: return "ml";
            default:     return unit;
        }
    }
}