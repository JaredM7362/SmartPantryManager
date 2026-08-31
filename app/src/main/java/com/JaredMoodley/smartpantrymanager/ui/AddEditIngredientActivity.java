package com.JaredMoodley.smartpantrymanager.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.JaredMoodley.smartpantrymanager.R;
import com.JaredMoodley.smartpantrymanager.data.PantryDataSource;
import com.JaredMoodley.smartpantrymanager.model.PantryItem;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;
import java.util.Locale;

/**
 * Serves as both the Add and the Edit screen, and hosts the delete action.
 */
public class AddEditIngredientActivity extends AppCompatActivity {

    /** Key used to pass the id of the item being edited. */
    public static final String EXTRA_ITEM_ID = "itemId";

    /** Value meaning "no id supplied", i.e. add mode. */
    private static final int NO_ID = -1;

    private TextInputLayout layoutName;
    private TextInputLayout layoutQuantity;
    private TextInputEditText editName;
    private TextInputEditText editQuantity;
    private TextInputEditText editExpiry;
    private Spinner spinnerUnit;

    private int editingItemId = NO_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_edit_ingredient);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.addEditRoot),
                (v, insets) -> {
                    Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
                    return insets;
                });

        layoutName = findViewById(R.id.layoutName);
        layoutQuantity = findViewById(R.id.layoutQuantity);
        editName = findViewById(R.id.editName);
        editQuantity = findViewById(R.id.editQuantity);
        editExpiry = findViewById(R.id.editExpiry);
        spinnerUnit = findViewById(R.id.spinnerUnit);

        MaterialToolbar toolbar = findViewById(R.id.toolbarAddEdit);
        MaterialButton buttonSave = findViewById(R.id.buttonSave);
        MaterialButton buttonDelete = findViewById(R.id.buttonDelete);
        MaterialButton buttonClearDate = findViewById(R.id.buttonClearDate);

        ArrayAdapter<CharSequence> unitAdapter = ArrayAdapter.createFromResource(
                this, R.array.units, android.R.layout.simple_spinner_item);
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUnit.setAdapter(unitAdapter);

        editingItemId = getIntent().getIntExtra(EXTRA_ITEM_ID, NO_ID);
        boolean isEditMode = editingItemId != NO_ID;

        toolbar.setTitle(isEditMode
                ? R.string.title_edit_ingredient
                : R.string.title_add_ingredient);
        toolbar.setNavigationOnClickListener(v -> finish());

        if (isEditMode) {
            buttonDelete.setVisibility(android.view.View.VISIBLE);
            loadExistingItem(editingItemId);
        }

        editExpiry.setOnClickListener(v -> showDatePicker());
        buttonClearDate.setOnClickListener(v -> editExpiry.setText(""));
        buttonSave.setOnClickListener(v -> saveItem());
        buttonDelete.setOnClickListener(v -> confirmDelete());
    }

    /**
     * Reads the record being edited and puts its values into the form.
     */
    private void loadExistingItem(int id) {
        PantryDataSource ds = new PantryDataSource(this);
        try {
            ds.open();
            PantryItem item = ds.getPantryItemById(id);
            ds.close();

            if (item == null) {
                // The row was deleted while this screen was opening.
                Toast.makeText(this, R.string.msg_delete_failed, Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            editName.setText(item.getName());
            editQuantity.setText(formatQuantity(item.getQuantity()));
            editExpiry.setText(item.getExpiryDate() == null ? "" : item.getExpiryDate());
            selectUnitInSpinner(item.getUnit());

        } catch (Exception e) {
            Log.e("PANTRY_UI", "Could not load item " + id + ": " + e.getMessage());
            finish();
        }
    }

    /**
     * Moves the spinner to the item's stored unit.
     */
    private void selectUnitInSpinner(String unit) {
        if (unit == null) return;
        for (int i = 0; i < spinnerUnit.getCount(); i++) {
            if (unit.equalsIgnoreCase(spinnerUnit.getItemAtPosition(i).toString())) {
                spinnerUnit.setSelection(i);
                return;
            }
        }
    }

    /**
     * Validates the form, then inserts or updates depending on the mode.
     */
    private void saveItem() {
        if (!isFormValid()) {
            return;   // errors are already displayed on the fields
        }

        PantryItem item = new PantryItem();
        item.setName(getTextOf(editName));
        item.setQuantity(Double.parseDouble(getTextOf(editQuantity)));
        item.setUnit(spinnerUnit.getSelectedItem().toString());

        String expiry = getTextOf(editExpiry);
        item.setExpiryDate(expiry.isEmpty() ? null : expiry);

        PantryDataSource ds = new PantryDataSource(this);
        boolean success = false;
        try {
            ds.open();
            if (editingItemId == NO_ID) {
                success = ds.insertPantryItem(item);
            } else {
                item.setId(editingItemId);
                success = ds.updatePantryItem(item);
            }
            ds.close();
        } catch (Exception e) {
            Log.e("PANTRY_UI", "Save failed: " + e.getMessage());
        }

        if (success) {
            Toast.makeText(this, R.string.msg_saved, Toast.LENGTH_SHORT).show();
            // finish() returns to the pantry list, whose onResume() re-reads
            // the database and redraws with the change already applied.
            finish();
        } else {
            Toast.makeText(this, R.string.msg_save_failed, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Checks every rule and marks each failing field.
     */
    private boolean isFormValid() {
        boolean valid = true;

        layoutName.setError(null);
        layoutQuantity.setError(null);

        String name = getTextOf(editName);
        if (name.isEmpty()) {
            layoutName.setError(getString(R.string.error_name_required));
            valid = false;
        } else if (name.length() < 2) {
            layoutName.setError(getString(R.string.error_name_too_short));
            valid = false;
        }

        String quantity = getTextOf(editQuantity);
        if (quantity.isEmpty()) {
            layoutQuantity.setError(getString(R.string.error_quantity_required));
            valid = false;
        } else {
            try {
                if (Double.parseDouble(quantity) <= 0) {
                    layoutQuantity.setError(getString(R.string.error_quantity_invalid));
                    valid = false;
                }
            } catch (NumberFormatException e) {

                layoutQuantity.setError(getString(R.string.error_quantity_invalid));
                valid = false;
            }
        }

        return valid;
    }

    /**
     * Deleting is irreversible, so it is confirmed first.
     */
    private void confirmDelete() {
        String name = getTextOf(editName);

        new AlertDialog.Builder(this)
                .setTitle(R.string.confirm_delete_title)
                .setMessage(getString(R.string.confirm_delete_message, name))
                .setNegativeButton(R.string.action_cancel, null)
                .setPositiveButton(R.string.action_delete, (dialog, which) -> deleteItem())
                .show();
    }

    private void deleteItem() {
        PantryDataSource ds = new PantryDataSource(this);
        boolean success = false;
        try {
            ds.open();
            success = ds.deletePantryItem(editingItemId);
            ds.close();
        } catch (Exception e) {
            Log.e("PANTRY_UI", "Delete failed: " + e.getMessage());
        }

        Toast.makeText(this,
                success ? R.string.msg_deleted : R.string.msg_delete_failed,
                Toast.LENGTH_SHORT).show();

        if (success) {
            finish();
        }
    }

    /**
     * Opens a date picker and writes the chosen date back in ISO format
     * (yyyy-MM-dd), which is how expiry_date is stored so that it sorts
     * correctly as plain text.
     */
    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();

        new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    // month is zero-based in DatePicker but one-based in a date
                    // string, hence the +1.
                    String iso = String.format(Locale.UK, "%04d-%02d-%02d",
                            year, month + 1, dayOfMonth);
                    editExpiry.setText(iso);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    /** Null-safe read of an EditText's trimmed contents. */
    private String getTextOf(TextInputEditText field) {
        return field.getText() == null ? "" : field.getText().toString().trim();
    }

    /** Drops a trailing ".0" so the edit form shows "500" rather than "500.0". */
    private String formatQuantity(double value) {
        return (value == Math.floor(value))
                ? String.valueOf((int) value)
                : String.valueOf(value);
    }
}