package com.rafalesoft.org.coachacook;

public class Category
{
    public Category() { }

    public enum Model
    {
        LEGUME(R.string.category_legume, R.mipmap.ic_legumes, "Legumes.xml"),
        FRUIT(R.string.category_fruit, R.mipmap.ic_fruits, "Fruits.xml"),
        BOUCHERIE(R.string.category_boucherie, R.mipmap.ic_boucherie, "Boucherie.xml"),
        CHARCUTERIE(R.string.category_charcuterie, R.mipmap.ic_charcuterie, "Charcuterie.xml"),
        POISSONNERIE(R.string.category_poissonnerie, R.mipmap.ic_poissonnerie, "Poissonnerie.xml"),
        LAITAGE(R.string.category_laitage, R.mipmap.ic_laitage, "Laitage.xml"),
        EPICERIE(R.string.category_epicerie, R.mipmap.ic_epicerie, "Epicerie.xml"),
        BOISSONS(R.string.category_boissons, R.mipmap.ic_boissons, "Boissons.xml"),
        MODEL_SIZE(0,0, "");

        final private int mStringResId;
        final private int mImageResId;
        final private String mCategoryFile;

        Model(int titleResId, int layoutResId, String file)
        {
            mStringResId = titleResId;
            mImageResId = layoutResId;
            mCategoryFile = file;
        }

        public int getTitleResId()
        {
            return mStringResId;
        }
        public int getImageResId()
        {
            return mImageResId;
        }
        public String getCategoryFile()
        {
            return mCategoryFile;
        }
    }
}
