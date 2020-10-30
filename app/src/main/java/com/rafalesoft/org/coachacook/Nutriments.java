package com.rafalesoft.org.coachacook;


// http://www.aprifel.com/fiche-nutri-produit-composition-abricot,10.html
// https://ciqual.anses.fr/

class Nutriments
{
    public static final String TABLE_NAME = "nutriments";

    public static final String COLUMN_E1 = "Calories";

    // Composants
    public static final String COLUMN_C1 = "Eau";
    public static final String COLUMN_C2 = "Proteines";
    public static final String COLUMN_C3 = "Lipides";
    public static final String COLUMN_C4 = "Graisse";           // Acides gras saturés
    public static final String COLUMN_C5 = "Glucides";
    public static final String COLUMN_C6 = "Sucres";
    public static final String COLUMN_C7 = "Fibres";
    public static final String COLUMN_C8 = "Organiques";        // Acides organiques

    // Vitamines
    public static final String COLUMN_V1 = "A_beta_carotene";   // Provitamine A Béta-carotène
    public static final String COLUMN_V2 = "Vitamine_A";        // Equivalent Vitamine A
    public static final String COLUMN_V3 = "Vitamine_B1";
    public static final String COLUMN_V4 = "Vitamine_B2";
    public static final String COLUMN_V5 = "Vitamine_B3";
    public static final String COLUMN_V6 = "Vitamine_B5";
    public static final String COLUMN_V7 = "Vitamine_B6";
    public static final String COLUMN_V8 = "Vitamine_B9";
    public static final String COLUMN_V9 = "Vitamine_C";
    public static final String COLUMN_V10 = "Vitamine_E";

    // Mineraux
    public static final String COLUMN_M1 = "Calcium";
    public static final String COLUMN_M2 = "Cuivre";
    public static final String COLUMN_M3 = "Fer";
    public static final String COLUMN_M4 = "Iode";
    public static final String COLUMN_M5 = "Magnesium";
    public static final String COLUMN_M6 = "Manganese";
    public static final String COLUMN_M7 = "Phosphore";
    public static final String COLUMN_M8 = "Selenium";
    public static final String COLUMN_M9 = "Sodium";
    public static final String COLUMN_M10 = "Zinc";

    // Polyphenols
    public static final String COLUMN_P1 = "Flavonoides";
    public static final String COLUMN_P2 = "Phenoliques";       // Acides phénoliques


    /**
     * Elaborates the SQL query to create ingredient table
     * @return the sql query string
     */
    public static String getTableQuery()
    {
        return "CREATE TABLE " + Nutriments.TABLE_NAME + " ("
                + RecipesDB.ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + RecipeComponent.COLUMN_INGREDIENT + " INTEGER,"
                + Nutriments.COLUMN_E1 + " REAL,"
                + Nutriments.COLUMN_C1 + " REAL,"
                + Nutriments.COLUMN_C2 + " REAL,"
                + Nutriments.COLUMN_C3 + " REAL"
                + ");";
    }



    private float _calories = 0.0f;
    private float _eau = 0.0f;
    private float _proteins = 0.0f;
    private float _lipids = 0.0f;
    private float _fats = 0.0f;
    private float _glucid = 0.0f;
    private float _sugar = 0.0f;
    private float _fiber = 0.0f;
    private float _organics = 0.0f;
}
