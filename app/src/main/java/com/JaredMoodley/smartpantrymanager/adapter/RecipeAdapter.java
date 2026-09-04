package com.JaredMoodley.smartpantrymanager.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.JaredMoodley.smartpantrymanager.R;
import com.JaredMoodley.smartpantrymanager.model.Recipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Binds a list of Recipe objects to rows in a RecyclerView.
 *
 * Follows the same pattern as PantryAdapter.
 */
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe);
    }

    private ArrayList<Recipe> recipes;

    /** Ingredient counts, keyed by recipe id, used for the row subtitle. */
    private Map<Integer, Integer> ingredientCounts;

    private final OnRecipeClickListener clickListener;

    public RecipeAdapter(ArrayList<Recipe> recipes,
                         Map<Integer, Integer> ingredientCounts,
                         OnRecipeClickListener clickListener) {
        this.recipes = recipes;
        this.ingredientCounts = ingredientCounts;
        this.clickListener = clickListener;
    }

    static class RecipeViewHolder extends RecyclerView.ViewHolder {
        final TextView textName;
        final TextView textSummary;

        RecipeViewHolder(View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textRecipeName);
            textSummary = itemView.findViewById(R.id.textRecipeSummary);
        }
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe, parent, false);
        return new RecipeViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);

        holder.textName.setText(recipe.getName());

        Integer count = ingredientCounts.get(recipe.getId());
        int safeCount = (count == null) ? 0 : count;

        holder.textSummary.setText(safeCount == 1
                ? holder.itemView.getContext().getString(R.string.ingredient_count_one)
                : holder.itemView.getContext()
                .getString(R.string.ingredient_count, safeCount));

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onRecipeClick(recipe);
            }
        });
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public void setRecipes(ArrayList<Recipe> newRecipes,
                           Map<Integer, Integer> newCounts) {
        this.recipes = newRecipes;
        this.ingredientCounts = (newCounts == null) ? new HashMap<>() : newCounts;
        notifyDataSetChanged();
    }
}