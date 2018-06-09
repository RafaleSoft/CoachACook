package com.rafalesoft.org.coachacook;

public class Category
{
    public Category() { }

    public enum Model
    {
        LEGUME(R.string.category_legume, R.mipmap.ic_legumes),
        FRUIT(R.string.category_fruit, R.mipmap.ic_fruits),
        BOUCHERIE(R.string.category_boucherie, R.mipmap.ic_boucherie),
        CHARCUTERIE(R.string.category_charcuterie, R.mipmap.ic_charcuterie),
        POISSONNERIE(R.string.category_poissonnerie, R.mipmap.ic_poissonnerie),
        LAITAGE(R.string.category_laitage, R.mipmap.ic_laitage),
        EPICERIE(R.string.category_epicerie, R.mipmap.ic_epicerie),
        BOISSONS(R.string.category_boissons, R.mipmap.ic_boissons),
        MODEL_SIZE(0,0);

        final private int mStringResId;
        final private int mImageResId;

        Model(int titleResId, int layoutResId)
        {
            mStringResId = titleResId;
            mImageResId = layoutResId;
        }

        public int getTitleResId()
        {
            return mStringResId;
        }
        public int getImageResId()
        {
            return mImageResId;
        }
    }
}
