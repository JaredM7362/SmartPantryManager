package com.JaredMoodley.smartpantrymanager.logic;

import java.util.HashMap;
import java.util.Map;

/**
 * Reduces an ingredient name to a comparable form.
 *
 * The user types names freely while recipe ingredients are stored in a fixed
 * form, so "Tomatoes ", "tomato" and "TOMATO" must all be recognised as the
 * same thing.
 */
public final class IngredientNormaliser {

    /**
     * Names that differ by more than spelling or plurality.
     */
    private static final Map<String, String> SYNONYMS = new HashMap<>();

    static {
        SYNONYMS.put("aubergine", "eggplant");
        SYNONYMS.put("brinjal", "eggplant");
        SYNONYMS.put("chilli", "chili");
        SYNONYMS.put("chile", "chili");
        SYNONYMS.put("coriander", "cilantro");
        SYNONYMS.put("spring onion", "green onion");
        SYNONYMS.put("scallion", "green onion");
        SYNONYMS.put("maize meal", "mealie meal");
        SYNONYMS.put("cornmeal", "mealie meal");
        SYNONYMS.put("courgette", "zucchini");
        SYNONYMS.put("capsicum", "pepper");
        SYNONYMS.put("bell pepper", "pepper");
        SYNONYMS.put("cheddar", "cheddar cheese");
        SYNONYMS.put("mince", "ground beef");
    }

    /** Utility class - never instantiated. */
    private IngredientNormaliser() { }

    /**
     * Converts a name into the form used for comparison.
     */
    public static String normalise(String raw) {
        if (raw == null) {
            return "";
        }

        String result = raw.trim().toLowerCase();

        // Remove anything that is not a letter, digit or space, so that
        // "tomato," and "tomato" are treated alike.
        result = result.replaceAll("[^a-z0-9 ]", "");

        // Collapse runs of whitespace into a single space.
        result = result.replaceAll("\\s+", " ").trim();

        if (result.isEmpty()) {
            return "";
        }

        result = singularise(result);

        String synonym = SYNONYMS.get(result);
        return synonym != null ? synonym : result;
    }

    /**
     * Reduces a simple English plural to its singular form.
     */
    private static String singularise(String text) {
        int lastSpace = text.lastIndexOf(' ');
        String prefix = lastSpace == -1 ? "" : text.substring(0, lastSpace + 1);
        String word = lastSpace == -1 ? text : text.substring(lastSpace + 1);

        return prefix + singulariseWord(word);
    }

    private static String singulariseWord(String word) {
        // Short words are left alone. Stripping the "s" from "gas" or the
        // "es" from "rice" would corrupt them, and no ingredient name is a
        // plural at three letters or fewer.
        if (word.length() <= 3) {
            return word;
        }

        // Words that end in "s" but are not plurals.
        if (word.endsWith("ss") || word.endsWith("us") || word.endsWith("is")) {
            return word;
        }

        // berries -> berry
        if (word.endsWith("ies") && word.length() > 4) {
            return word.substring(0, word.length() - 3) + "y";
        }

        // tomatoes -> tomato, potatoes -> potato
        if (word.endsWith("oes")) {
            return word.substring(0, word.length() - 2);
        }

        // dishes -> dish, boxes -> box
        if (word.endsWith("shes") || word.endsWith("ches") || word.endsWith("xes")) {
            return word.substring(0, word.length() - 2);
        }

        // onions -> onion
        if (word.endsWith("s")) {
            return word.substring(0, word.length() - 1);
        }

        return word;
    }
}