package com.rafalesoft.org.coachacook;

import androidx.annotation.NonNull;

public enum Unit
{
    GRAM(R.string.unit_gram),
    KILOGRAM(R.string.unit_kilogram),
    MILLILITER(R.string.unit_milliliter),
    LITER(R.string.unit_liter),
    TEASPOON(R.string.unit_teaspoon),
    SPOON(R.string.unit_tablespoon),
    CALORIE(R.string.unit_calorie),
    COUNT(R.string.unit_count);

    /**
     * International System units, to enable units comparison.
     */
    public enum SI
    {
        LENGTH,
        MASS,
        TIME,
        CURRENT,
        TEMPERATURE,
        MOLE,
        LIGHT,
        AREA,   // Derived
        VOLUME, // Derived
        ENERGY    // Derived
    }

    final private int mStringResId;
    final private SI mSI;
    final private Double mSIFactor;

    Unit(int stringResId)
    {
        mStringResId = stringResId;
        switch(stringResId)
        {
            case R.string.unit_gram:
                mSI = SI.MASS;
                mSIFactor = 0.001;
                break;
            case R.string.unit_kilogram:
                mSI = SI.MASS;
                mSIFactor = 1.0;
                break;
            case R.string.unit_milliliter:
                mSI = SI.VOLUME;
                mSIFactor = 0.001;
                break;
            case R.string.unit_liter:
                mSI = SI.VOLUME;
                mSIFactor = 1.0;
                break;
            case R.string.unit_teaspoon:
                mSI = SI.MASS;
                mSIFactor = 0.005;
                break;
            case R.string.unit_tablespoon:
                mSI = SI.MASS;
                mSIFactor = 0.01;
                break;
            case R.string.unit_calorie:
                mSI = SI.ENERGY;
                mSIFactor = 1.0 / 4.184;  // 1 Kcal = 4184 J
                break;
            case R.string.unit_mole:
                mSI = SI.MOLE;
                mSIFactor = 1.0;
                break;
            default:
                mSI = SI.MASS;
                mSIFactor = 0.001;
                break;
        }
    }

    public static Unit parse(String label)
    {
        Unit unit;

        switch (label.toLowerCase())
        {
            case "g":
                unit = GRAM;
                break;
            case "kg":
                unit = KILOGRAM;
                break;
            case "ml":
                unit = MILLILITER;
                break;
            case "l":
                unit = LITER;
                break;
            case "tsp":
                unit = TEASPOON;
                break;
            case "sp":
                unit = SPOON;
                break;
            case "cal":
                unit = CALORIE;
                break;
            default:
                unit = GRAM;
                break;
        }

        return unit;
    }

    @NonNull
    public String toString()
    {
        return CoachACook.getCoach().getString(mStringResId);
    }

    public boolean canConvert(Unit u)
    {
        return mSI == u.mSI;
    }

    public Double getSIFactor() { return mSIFactor; }
}
