package com.JaredMoodley.smartpantrymanager.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.JaredMoodley.smartpantrymanager.R;
import com.JaredMoodley.smartpantrymanager.model.PantryItem;

import java.util.ArrayList;

/**
 * Binds a list of PantryItem objects to rows in a RecyclerView.
 */
public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {

    /**
     * Lets the hosting Activity react to a row being tapped without the adapter needing to know anything about navigation.
     */
    public interface OnItemClickListener {
        void onItemClick(PantryItem item);
    }

    private ArrayList<PantryItem> items;
    private final OnItemClickListener clickListener;

    public PantryAdapter(ArrayList<PantryItem> items, OnItemClickListener clickListener) {
        this.items = items;
        this.clickListener = clickListener;
    }

    /**
     * Holds the views for one row so they are looked up once, not on every bind.
     */
    static class PantryViewHolder extends RecyclerView.ViewHolder {
        final TextView textName;
        final TextView textQuantity;
        final TextView textExpiry;

        PantryViewHolder(View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textItemName);
            textQuantity = itemView.findViewById(R.id.textItemQuantity);
            textExpiry = itemView.findViewById(R.id.textItemExpiry);
        }
    }

    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pantry, parent, false);
        return new PantryViewHolder(row);
    }

    /**
     * Puts the values of items.get(position) into an existing row.
     */
    @Override
    public void onBindViewHolder(@NonNull PantryViewHolder holder, int position) {
        PantryItem item = items.get(position);

        holder.textName.setText(item.getName());
        holder.textQuantity.setText(formatQuantity(item));

        // Expiry is optional, so hide the view entirely when there is none
        // rather than leaving an empty gap in the row.
        if (item.getExpiryDate() == null || item.getExpiryDate().trim().isEmpty()) {
            holder.textExpiry.setVisibility(View.GONE);
        } else {
            holder.textExpiry.setVisibility(View.VISIBLE);
            holder.textExpiry.setText(item.getExpiryDate());
        }

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * Replaces the backing list and redraws. Called by the Activity after the database has been re-read.
     */
    public void setItems(ArrayList<PantryItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    /**
     * Quantities are stored as REAL, so a whole number arrives as "500.0".
     * Trim the decimal when there isn't one, just for readability.
     */
    private String formatQuantity(PantryItem item) {
        double qty = item.getQuantity();
        String number = (qty == Math.floor(qty))
                ? String.valueOf((int) qty)
                : String.valueOf(qty);

        String unit = item.getUnit();
        return (unit == null || unit.trim().isEmpty())
                ? number
                : number + " " + unit;
    }
}