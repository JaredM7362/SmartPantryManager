package com.JaredMoodley.smartpantrymanager.data;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Loads the starter recipe collection the first time the database is created.
 */
public class RecipeSeeder {

    /**
     * Writes every starter recipe and its required ingredients.
     * Called only from PantryDBHelper.onCreate().
     */
    public static void seed(SQLiteDatabase db) {

        insertRecipe(db, "Scrambled Eggs",
                "Beat the eggs with a pinch of salt.\n" +
                        "Melt the butter in a pan over low heat.\n" +
                        "Pour in the eggs and stir gently until just set.",
                new String[]{"eggs", "3", "unit"},
                new String[]{"butter", "20", "g"},
                new String[]{"salt", "1", "tsp"});

        insertRecipe(db, "Buttered Toast",
                "Toast the bread until golden.\n" +
                        "Spread the butter while still warm.",
                new String[]{"bread", "2", "unit"},
                new String[]{"butter", "20", "g"});

        insertRecipe(db, "Peanut Butter Sandwich",
                "Spread the peanut butter evenly over one slice.\n" +
                        "Top with the second slice and cut in half.",
                new String[]{"bread", "2", "unit"},
                new String[]{"peanut butter", "30", "g"});

        insertRecipe(db, "Banana Smoothie",
                "Peel the bananas and break into chunks.\n" +
                        "Blend with the milk and honey until smooth.",
                new String[]{"bananas", "2", "unit"},
                new String[]{"milk", "250", "ml"},
                new String[]{"honey", "15", "ml"});

        insertRecipe(db, "Garlic Pasta",
                "Boil the spaghetti in salted water until al dente.\n" +
                        "Gently fry the sliced garlic in the olive oil.\n" +
                        "Toss the drained pasta through the garlic oil.",
                new String[]{"spaghetti", "200", "g"},
                new String[]{"garlic", "3", "unit"},
                new String[]{"olive oil", "30", "ml"},
                new String[]{"salt", "1", "tsp"});

        insertRecipe(db, "Tomato and Onion Salad",
                "Slice the tomatoes and onion thinly.\n" +
                        "Dress with olive oil and a pinch of salt.\n" +
                        "Rest for ten minutes before serving.",
                new String[]{"tomatoes", "3", "unit"},
                new String[]{"onions", "1", "unit"},
                new String[]{"olive oil", "30", "ml"},
                new String[]{"salt", "1", "tsp"});

        insertRecipe(db, "Cheese Omelette",
                "Beat the eggs with the salt.\n" +
                        "Melt the butter in a pan and add the eggs.\n" +
                        "Scatter the cheese over one half and fold when set.",
                new String[]{"eggs", "3", "unit"},
                new String[]{"cheddar cheese", "50", "g"},
                new String[]{"butter", "20", "g"},
                new String[]{"salt", "1", "tsp"});

        insertRecipe(db, "Mielie Pap",
                "Bring the water to the boil with the salt.\n" +
                        "Stir in the maize meal a little at a time.\n" +
                        "Cover and steam on low heat for 30 minutes.",
                new String[]{"maize meal", "250", "g"},
                new String[]{"water", "750", "ml"},
                new String[]{"salt", "1", "tsp"});

        insertRecipe(db, "Tomato Pasta",
                "Boil the spaghetti until al dente.\n" +
                        "Fry the garlic and chopped tomatoes in the olive oil.\n" +
                        "Season and toss the pasta through the sauce.",
                new String[]{"spaghetti", "200", "g"},
                new String[]{"tomatoes", "3", "unit"},
                new String[]{"garlic", "2", "unit"},
                new String[]{"olive oil", "30", "ml"},
                new String[]{"salt", "1", "tsp"});

        insertRecipe(db, "French Toast",
                "Whisk the eggs with the milk and cinnamon.\n" +
                        "Soak each slice of bread briefly.\n" +
                        "Fry in butter until golden on both sides.",
                new String[]{"bread", "4", "unit"},
                new String[]{"eggs", "2", "unit"},
                new String[]{"milk", "100", "ml"},
                new String[]{"butter", "20", "g"},
                new String[]{"cinnamon", "1", "tsp"});

        insertRecipe(db, "Pancakes",
                "Whisk the flour, milk, eggs and sugar into a smooth batter.\n" +
                        "Rest the batter for 20 minutes.\n" +
                        "Fry thin pancakes in a little butter.",
                new String[]{"flour", "250", "g"},
                new String[]{"milk", "300", "ml"},
                new String[]{"eggs", "2", "unit"},
                new String[]{"sugar", "30", "g"},
                new String[]{"butter", "20", "g"});

        insertRecipe(db, "Rice and Lentils",
                "Fry the chopped onion in the olive oil until soft.\n" +
                        "Add the rice and lentils with double their volume of water.\n" +
                        "Simmer covered until the water is absorbed.",
                new String[]{"rice", "200", "g"},
                new String[]{"lentils", "150", "g"},
                new String[]{"onions", "1", "unit"},
                new String[]{"olive oil", "30", "ml"},
                new String[]{"salt", "1", "tsp"});

        insertRecipe(db, "Potato Bake",
                "Slice the potatoes thinly and layer in a dish.\n" +
                        "Pour over the milk and dot with butter.\n" +
                        "Top with cheese and bake for 45 minutes.",
                new String[]{"potatoes", "4", "unit"},
                new String[]{"milk", "250", "ml"},
                new String[]{"cheddar cheese", "100", "g"},
                new String[]{"butter", "30", "g"},
                new String[]{"salt", "1", "tsp"});

        insertRecipe(db, "Boerewors Rolls",
                "Grill the boerewors until cooked through.\n" +
                        "Fry the sliced onion until soft and browned.\n" +
                        "Serve in the rolls with onion and tomato sauce.",
                new String[]{"boerewors", "400", "g"},
                new String[]{"bread rolls", "4", "unit"},
                new String[]{"onions", "1", "unit"},
                new String[]{"tomato sauce", "30", "ml"});

        insertRecipe(db, "Creamy Mushroom Pasta",
                "Boil the spaghetti until al dente.\n" +
                        "Fry the sliced mushrooms and garlic in the butter.\n" +
                        "Stir in the cream, season, and toss with the pasta.",
                new String[]{"spaghetti", "200", "g"},
                new String[]{"mushrooms", "200", "g"},
                new String[]{"cream", "150", "ml"},
                new String[]{"garlic", "2", "unit"},
                new String[]{"butter", "20", "g"},
                new String[]{"salt", "1", "tsp"});

        insertRecipe(db, "Vegetable Stir-Fry",
                "Cut the vegetables into thin strips.\n" +
                        "Fry the garlic in hot oil, then add the vegetables.\n" +
                        "Stir-fry for five minutes and finish with soy sauce.",
                new String[]{"carrots", "2", "unit"},
                new String[]{"green pepper", "1", "unit"},
                new String[]{"onions", "1", "unit"},
                new String[]{"garlic", "2", "unit"},
                new String[]{"soy sauce", "30", "ml"},
                new String[]{"olive oil", "30", "ml"});

        insertRecipe(db, "Chakalaka",
                "Fry the onion, carrot and pepper in the oil until soft.\n" +
                        "Stir in the curry powder and chopped tomatoes.\n" +
                        "Add the baked beans and simmer for ten minutes.",
                new String[]{"onions", "1", "unit"},
                new String[]{"carrots", "2", "unit"},
                new String[]{"green pepper", "1", "unit"},
                new String[]{"tomatoes", "2", "unit"},
                new String[]{"baked beans", "400", "g"},
                new String[]{"curry powder", "2", "tsp"},
                new String[]{"olive oil", "30", "ml"});

        insertRecipe(db, "Chicken Curry",
                "Brown the chicken pieces in the oil and set aside.\n" +
                        "Fry the onions, garlic and curry powder until fragrant.\n" +
                        "Add the tomatoes, potatoes and chicken, then simmer for 40 minutes.",
                new String[]{"chicken", "500", "g"},
                new String[]{"onions", "2", "unit"},
                new String[]{"tomatoes", "3", "unit"},
                new String[]{"garlic", "3", "unit"},
                new String[]{"potatoes", "2", "unit"},
                new String[]{"curry powder", "2", "tbsp"},
                new String[]{"olive oil", "30", "ml"},
                new String[]{"salt", "1", "tsp"});

        Log.d("PANTRY_DB", "Seeded starter recipes");
    }

    /**
     * Inserts one recipe followed by its ingredient rows.
     *
     * The recipe must be written first because insert() returns the generated
     * primary key, and every ingredient row needs that key as its foreign key.
     *
     * @param ingredients each entry is {name, quantity, unit}
     */
    private static void insertRecipe(SQLiteDatabase db, String name, String steps,
                                     String[]... ingredients) {

        ContentValues recipeValues = new ContentValues();
        recipeValues.put(PantryDBHelper.RECIPE_NAME, name);
        recipeValues.put(PantryDBHelper.RECIPE_STEPS, steps);

        long recipeId = db.insert(PantryDBHelper.TABLE_RECIPE, null, recipeValues);
        if (recipeId <= 0) {
            Log.e("PANTRY_DB", "Could not seed recipe: " + name);
            return;
        }

        for (String[] ingredient : ingredients) {
            ContentValues values = new ContentValues();
            values.put(PantryDBHelper.RI_RECIPE_ID, recipeId);
            values.put(PantryDBHelper.RI_NAME, ingredient[0]);
            values.put(PantryDBHelper.RI_QUANTITY, Double.parseDouble(ingredient[1]));
            values.put(PantryDBHelper.RI_UNIT, ingredient[2]);
            db.insert(PantryDBHelper.TABLE_RECIPE_INGREDIENT, null, values);
        }
    }
}