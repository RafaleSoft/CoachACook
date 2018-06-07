package com.rafalesoft.org.coachacook;

public enum Unit
{
    GRAM(R.string.unit_gram),
    KILOGRAM(R.string.unit_kilogram),
    MILLILITER(R.string.unit_milliliter),
    LITER(R.string.unit_liter),
    TEASPOON(R.string.unit_teaspoon),
    TABLESPOON(R.string.unit_tablespoon);

    public enum SI
    {
        LENGTH,
        MASS,
        TIME,
        CURRENT,
        TEMPERATURE,
        MATER,
        LIGHT,
        AREA,   // Derived
        VOLUME  // Derived
    }

    final private int mStringResId;
    final private SI mSI;
    final private float mSIFactor;

    Unit(int stringResId)
    {
        mStringResId = stringResId;
        switch(stringResId)
        {
            case R.string.unit_gram:
                mSI = SI.MASS;
                mSIFactor = 0.001f;
                break;
            case R.string.unit_kilogram:
                mSI = SI.MASS;
                mSIFactor = 1.0f;
                break;
            case R.string.unit_milliliter:
                mSI = SI.VOLUME;
                mSIFactor = 0.001f;
                break;
            case R.string.unit_liter:
                mSI = SI.VOLUME;
                mSIFactor = 1.0f;
                break;
            case R.string.unit_teaspoon:
                mSI = SI.MASS;
                mSIFactor = 0.005f;
                break;
            case R.string.unit_tablespoon:
                mSI = SI.MASS;
                mSIFactor = 0.01f;
                break;
            default:
                mSI = SI.LENGTH;
                mSIFactor = 1.0f;
                break;
        }
    }

    public int getStringResId()
    {
        return mStringResId;
    }

    public boolean canConvert(Unit u)
    {
        return mSI == u.mSI;
    }
}
