package com.JaredMoodley.smartpantrymanager.logic;

/**
 * Converts quantities to a common base so that amounts recorded in different
 * units can be compared.
 */
public final class UnitConverter {

    public enum Family { MASS, VOLUME, COUNT, UNKNOWN }

    private UnitConverter() { }

    /**
     * Which family a unit belongs to. An unrecognised or blank unit is
     * treated as COUNT.
     */
    public static Family familyOf(String unit) {
        if (unit == null || unit.trim().isEmpty()) {
            return Family.COUNT;
        }

        switch (unit.trim().toLowerCase()) {
            case "g":
            case "kg":
                return Family.MASS;
            case "ml":
            case "l":
            case "tsp":
            case "tbsp":
            case "cup":
                return Family.VOLUME;
            case "unit":
            case "units":
            case "piece":
            case "pieces":
                return Family.COUNT;
            default:
                return Family.UNKNOWN;
        }
    }

    /**
     * Converts an amount into its family's base unit: grams for mass,
     * millilitres for volume, unchanged for counts.
     *
     * Spoon and cup measures are by definition, so the
     * conversions used here are the standard metric ones.
     */
    public static double toBase(double amount, String unit) {
        if (unit == null || unit.trim().isEmpty()) {
            return amount;
        }

        switch (unit.trim().toLowerCase()) {
            case "kg":   return amount * 1000;   // to grams
            case "l":    return amount * 1000;   // to millilitres
            case "tsp":  return amount * 5;      // to millilitres
            case "tbsp": return amount * 15;     // to millilitres
            case "cup":  return amount * 250;    // to millilitres
            default:     return amount;          // g, ml, unit, unknown
        }
    }

    /**
     * True when the pantry holds at least as much as the recipe requires.
     */
    public static boolean hasEnough(double pantryQty, String pantryUnit,
                                    double requiredQty, String requiredUnit) {

        Family pantryFamily = familyOf(pantryUnit);
        Family requiredFamily = familyOf(requiredUnit);

        if (pantryFamily != requiredFamily
                || pantryFamily == Family.UNKNOWN) {
            return true;   // not comparable - accept on presence
        }

        // A recipe with no meaningful quantity ("salt to taste") is satisfied
        // by having the ingredient at all.
        if (requiredQty <= 0) {
            return true;
        }

        return toBase(pantryQty, pantryUnit) >= toBase(requiredQty, requiredUnit);
    }
}