package com.JaredMoodley.smartpantrymanager.ui;

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
import com.JaredMoodley.smartpantrymanager.adapter.PantryAdapter;
import com.JaredMoodley.smartpantrymanager.data.PantryDataSource;
import com.JaredMoodley.smartpantrymanager.data.RecipeDataSource;
import com.JaredMoodley.smartpantrymanager.logic.RecipeMatcher;
import com.JaredMoodley.smartpantrymanager.model.PantryItem;
import com.JaredMoodley.smartpantrymanager.model.Recipe;
import com.JaredMoodley.smartpantrymanager.model.RecipeIngredient;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;

/**
 * Launcher screen. Displays every pantry item in a RecyclerView and provides the entry point to adding a new one.
 */
public class PantryListActivity extends AppCompatActivity
        implements PantryAdapter.OnItemClickListener {

    private RecyclerView recyclerPantry;
    private View textEmptyPantry;
    private PantryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pantry_list);

        // Keeps content clear of the status and navigation bars.
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.pantryListRoot),
                (v, insets) -> {
                    Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
                    return insets;
                });

        recyclerPantry = findViewById(R.id.recyclerPantry);
        textEmptyPantry = findViewById(R.id.textEmptyPantry);

        // The LayoutManager decides how rows are positioned. Linear and
        // vertical is the standard stacked list.
        recyclerPantry.setLayoutManager(new LinearLayoutManager(this));
        recyclerPantry.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        // Start with an empty list so the adapter is never null; real data is
        // loaded in onResume().
        adapter = new PantryAdapter(new ArrayList<>(), this);
        recyclerPantry.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fabAddItem);
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu_pantry_list);
        toolbar.setOnMenuItemClickListener(menuItem -> {
            if (menuItem.getItemId() == R.id.action_recipes) {
                startActivity(new Intent(this, SuggestedRecipesActivity.class));
                return true;
            }
            return false;
        });

        fab.setOnClickListener(v ->
                startActivity(new Intent(this, AddEditIngredientActivity.class)));
    }



    /**
     * Reloading here rather than in onCreate() means the list refreshes every
     * time this screen comes back to the foreground - including when the user
     * returns from adding, editing or deleting an item. onCreate() runs only
     * once per Activity instance and would leave the list stale.
     */
    @Override
    protected void onResume() {
        super.onResume();
        loadPantryItems();
    }

    /**
     * Reads the pantry from the database and hands it to the adapter, then
     * shows either the list or the empty-state message.
     */
    private void loadPantryItems() {
        PantryDataSource ds = new PantryDataSource(this);
        ArrayList<PantryItem> items = new ArrayList<>();

        try {
            ds.open();
            items = ds.getAllPantryItems();
            ds.close();
        } catch (Exception e) {
            Log.e("PANTRY_UI", "Could not load pantry: " + e.getMessage());
            Toast.makeText(this, "Could not load your pantry", Toast.LENGTH_SHORT).show();
        }

        adapter.setItems(items);
        showEmptyState(items.isEmpty());
    }

    /** Only one of the list and the empty message is ever visible. */
    private void showEmptyState(boolean isEmpty) {
        textEmptyPantry.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        recyclerPantry.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    /** Called by the adapter when a row is tapped.*/
    @Override
    public void onItemClick(PantryItem item) {
        Intent intent = new Intent(this, AddEditIngredientActivity.class);
        intent.putExtra(AddEditIngredientActivity.EXTRA_ITEM_ID, item.getId());
        startActivity(intent);
    }
}