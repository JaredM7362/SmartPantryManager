# Smart Pantry Manager

An Android application (Java) that helps reduce household food waste by tracking the ingredients
you already have at home and suggesting **only** the recipes you can cook right now with no
shopping trip required.

Built for **Mobile App Development 700**, Faculty of Information Technology, Richfield Graduate
Institute of Technology.

---

## Features

- **Pantry management** - add, view, edit and delete ingredients, each with a name, quantity,
  unit of measure and an optional expiry date.
- **Strict recipe matching** - a recipe appears in the suggestions list only if *all* of its
  required ingredients are present in sufficient quantity. A recipe needing five ingredients when
  the pantry holds four of them is excluded.
- **Tolerant ingredient matching** - matching is normalised so that trivial real-world
  differences do not break it: `Tomatoes` matches `tomato`, leading and trailing spaces are
  ignored, and quantities are compared after converting units to a common base (1 kg is
  recognised as enough for a recipe needing 500 g).
- **Pre-loaded recipe collection** - 18 recipes are seeded into the database on first launch,
  each with its full ingredient list and preparation steps.
- **Recipe detail view** - full ingredients and method for any suggested recipe.
- **Helpful empty state** - when nothing matches, the app explains.
- **Settings** - unit preference and an expiring-soon alert toggle, stored in SharedPreferences.

---

## Database choice: SQLite

This app uses **SQLite**, accessed through a `SQLiteOpenHelper` subclass.

I chose SQLite because it ships as part of the Android platform, requires no configuration or API
keys, and works entirely offline. Its relational structure also maps directly onto the data:
recipes have many ingredients, which is a one-to-many relationship expressed cleanly as a child
table with a foreign key. Finally, SQLite is the persistence approach covered in the module's
persistent data chapter, which meant it could be implemented with full understanding of what each
layer does rather than by adopting a configuration that could not be explained.

---

## Data model

Three tables:

**`pantry_item`** - the ingredients the user currently has

| Column | Type | Notes |
|---|---|---|
| `_id` | INTEGER PRIMARY KEY AUTOINCREMENT | Named `_id` by Android convention |
| `name` | TEXT NOT NULL | Stored as the user typed it |
| `quantity` | REAL | REAL so that 0.5 kg and 1.5 cups are valid |
| `unit` | TEXT | One of: g, kg, ml, l, tsp, tbsp, cup, unit |
| `expiry_date` | TEXT | Nullable. ISO format (YYYY-MM-DD) so it sorts correctly as text |

**`recipe`** - the seeded recipe collection

| Column | Type |
|---|---|
| `_id` | INTEGER PRIMARY KEY AUTOINCREMENT |
| `name` | TEXT NOT NULL |
| `steps` | TEXT |

**`recipe_ingredient`** - what each recipe requires (one-to-many child of `recipe`)

| Column | Type | Notes |
|---|---|---|
| `_id` | INTEGER PRIMARY KEY AUTOINCREMENT | |
| `recipe_id` | INTEGER | Foreign key to `recipe._id` |
| `name` | TEXT NOT NULL | |
| `quantity` | REAL | |
| `unit` | TEXT | |

Required ingredients are held in their own table. This keeps the model properly normalised, allows each ingredient to carry its own
quantity and unit, and lets the matching logic iterate real rows instead of parsing text.

---

## Screens

| Screen | Purpose |
|---|---|
| `PantryListActivity` | Launcher. Lists all pantry items in a RecyclerView with a custom adapter |
| `AddEditIngredientActivity` | One screen serving add, edit and delete. Edit mode is triggered by an item id passed in the Intent; its absence means add mode |
| `SuggestedRecipesActivity` | Runs the strict-matching logic against the pantry and lists what can be cooked now |
| `RecipeDetailActivity` | Full ingredient list and method for a selected recipe |
| `SettingsActivity` | Unit preference and expiring-soon alert toggle |

A bottom navigation bar moves between Pantry, Recipes and Settings.

---

## How the strict-matching rule works

For each recipe, the matcher walks its required ingredients. The moment one is missing from the
pantry - or present but in an insufficient quantity - the recipe is disqualified and the check
stops. A recipe qualifies only if every ingredient passes, making this a logical AND across the
whole list rather than a score or a percentage.

Two supporting steps make this robust against real-world input:

1. **Name normalisation** - both sides are lower-cased and trimmed, punctuation is removed, and
   simple plurals are reduced to their singular form before comparison.
2. **Unit conversion** - quantities are converted to a base unit within their family (grams for
   mass, millilitres for volume, whole units for countable items) before being compared.

---

## Tech stack

- **Language:** Java
- **IDE:** Android Studio
- **Minimum SDK:** API 24 (Android 7.0)
- **Target SDK:** API 34
- **Persistence:** SQLite via `SQLiteOpenHelper`, plus SharedPreferences for user settings
- **UI:** RecyclerView with custom adapters, ConstraintLayout and LinearLayout, BottomNavigationView

No mapping SDK, location services or GPS permissions are used - the app's scope is strictly the
user's own pantry and recipe matching.

---

## Setup and run

**Prerequisites**

- Android Studio (Giraffe or newer)
- An Android device running API 24 or higher, or an emulator (developed against a Pixel 3a, API 33)

**Steps**

1. Clone the repository:

   ```
   git clone https://github.com/JaredM7362/SmartPantryManager.git
   ```

2. Open the project folder in Android Studio (**File > Open**, then select the project root).
3. Wait for the initial Gradle sync to finish. The first sync downloads dependencies and may take
   several minutes.
4. Select a device from the dropdown in the toolbar.
5. Press **Run**.

No API keys, configuration files or backend services are required. The recipe collection is
seeded into the local database automatically on first launch.

---

## Project structure

```
app/src/main/java/com/YOURNAME/smartpantrymanager/
├── model/
│   ├── PantryItem.java
│   ├── Recipe.java
│   └── RecipeIngredient.java
├── data/
│   ├── PantryDBHelper.java        // creates and upgrades the database
│   ├── PantryDataSource.java      // opens, closes and queries the database
│   └── RecipeSeeder.java          // loads the starter recipes on first run
├── logic/
│   └── RecipeMatcher.java         // the strict-matching algorithm
├── adapter/
│   ├── PantryAdapter.java
│   └── RecipeAdapter.java
└── ui/
    ├── PantryListActivity.java
    ├── AddEditIngredientActivity.java
    ├── SuggestedRecipesActivity.java
    ├── RecipeDetailActivity.java
    └── SettingsActivity.java
```

---

## Author

**Jared Jason Moodley**
Student Number: 402312409
Mobile App Development 700 - Richfield Graduate Institute of Technology
