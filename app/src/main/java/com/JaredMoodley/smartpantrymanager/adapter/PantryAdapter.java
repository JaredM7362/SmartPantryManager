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
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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

    private final boolean showExpiryBadges;
    private final int warningDays;

    public PantryAdapter(ArrayList<PantryItem> items,
                         OnItemClickListener clickListener,
                         boolean showExpiryBadges,
                         int warningDays) {
        this.items = items;
        this.clickListener = clickListener;
        this.showExpiryBadges = showExpiryBadges;
        this.warningDays = warningDays;
    }


    /**
     * Holds the views for one row so they are looked up once, not on every bind.
     */
    static class PantryViewHolder extends RecyclerView.ViewHolder {
        final TextView textName;
        final TextView textQuantity;
        final TextView textExpiry;

        final TextView textExpiryBadge;

        PantryViewHolder(View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textItemName);
            textQuantity = itemView.findViewById(R.id.textItemQuantity);
            textExpiry = itemView.findViewById(R.id.textItemExpiry);
            textExpiryBadge = itemView.findViewById(R.id.textExpiryBadge);
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

        bindExpiryBadge(holder, item);


    }

    /**
     * Shows an expiry warning when the setting is on and the item is close to,
     * or past, its date. Visibility is set on both paths because a recycled
     * row may still be showing a previous item's badge.
     */
    private void bindExpiryBadge(PantryViewHolder holder, PantryItem item) {
        holder.textExpiryBadge.setVisibility(View.GONE);

        if (!showExpiryBadges || item.getExpiryDate() == null
                || item.getExpiryDate().trim().isEmpty()) {
            return;
        }

        try {
            // Dates are stored as ISO yyyy-MM-dd, which LocalDate parses
            // directly - one of the reasons that format was chosen.
            LocalDate expiry = LocalDate.parse(item.getExpiryDate().trim());
            long daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), expiry);

            if (daysLeft < 0) {
                holder.textExpiryBadge.setText(R.string.expiry_expired);
                holder.textExpiryBadge.setVisibility(View.VISIBLE);
            } else if (daysLeft <= warningDays) {
                holder.textExpiryBadge.setText(R.string.expiry_soon);
                holder.textExpiryBadge.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            // A malformed date should not break the row - leave the badge off.
        }
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