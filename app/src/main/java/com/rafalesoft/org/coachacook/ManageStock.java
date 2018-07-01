package com.rafalesoft.org.coachacook;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


class ManageStock implements OnClickListener, OnItemClickListener
{
    private StockAdapter _stockAdapter = null;
	public ManageStock() {
	}



	private class StockAdapter extends PagerAdapter
    {
        private final ArrayList<RecipesCursorHolder> _cursors = new ArrayList<>();

        StockAdapter()
        {
            for (int i=0; i<getCount(); i++)
                _cursors.add(new RecipesCursorHolder());
        }

        private class StockCursorAdapter implements SimpleCursorAdapter.ViewBinder
        {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex)
            {
                switch (view.getId())
                {
                    case R.id.stock_item_name:
                        String text = cursor.getString(columnIndex);
                        ((TextView) view).setText(text);
                        return true;
                    case R.id.stock_item_quantity:
                        Double q = cursor.getDouble(columnIndex);
                        ((TextView) view).setText(q.toString());
                        return true;
                    case R.id.stock_item_unit:
                        Unit u = Unit.values()[cursor.getInt(columnIndex)];
                        ((TextView) view).setText(u.toString());
                        return true;
                    default:
                        return false;
                }
            }
        }

        @Override
        public int getCount()
        {
            return Category.Model.MODEL_SIZE.ordinal();
        }

        @Override
        public boolean isViewFromObject(View view, Object object)
        {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup collection, int position)
        {
			Category.Model modelObject = Category.Model.values()[position];
            LayoutInflater inflater = LayoutInflater.from(CoachACook.getCoach());
            ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.stock_view, collection, false);

            ImageView image = layout.findViewById(R.id.stock_image);
            image.setImageResource(modelObject.getImageResId());
            TextView category = layout.findViewById(R.id.stock_category);
            category.setText(getPageTitle(position));

            ListView lvl = layout.findViewById(R.id.stock_list_view);
            String[] projection = { RecipesDB.ID,
                                    RecipesDB.NAME,
                                    Ingredient.COLUMN_STOCK_TITLE,
                                    Ingredient.COLUMN_UNIT_TITLE };

            String selection = Ingredient.COLUMN_TYPE_TITLE + "=?" + " AND " + Ingredient.COLUMN_STOCK_TITLE + ">0";
            String[] selectionArgs = { Integer.toString(modelObject.ordinal()) };
            RecipesCursorHolder c = _cursors.get(position);
            c.updateCursor(Ingredient.TABLE_NAME, projection, selection, selectionArgs);

            String[] fromColumns = {RecipesDB.NAME, Ingredient.COLUMN_STOCK_TITLE, Ingredient.COLUMN_UNIT_TITLE};
            int[] toViews = { R.id.stock_item_name, R.id.stock_item_quantity, R.id.stock_item_unit};

            SimpleCursorAdapter recipesDBAdapter =
                    new SimpleCursorAdapter(CoachACook.getCoach(),R.layout.stock_view_item,
                            c.getCursor(),fromColumns,toViews,0);

            recipesDBAdapter.setViewBinder(new StockCursorAdapter());
            lvl.setAdapter(recipesDBAdapter);
            //lvl.setOnItemClickListener(this);

            collection.addView(layout);
            return layout;
        }

        @Override
        public void destroyItem(ViewGroup collection, int position, Object view)
        {
            RecipesCursorHolder c = _cursors.get(position);
            c.close();
            collection.removeView((View) view);
        }

        @Override
        public CharSequence getPageTitle(int position)
		{
			Category.Model model = Category.Model.values()[position];
            return CoachACook.getCoach().getString(model.getTitleResId());
        }
    }

	@Override
	public void onClick(View v)
	{
		View view = CoachACook.getCoach().switchToView(R.id.stock_pager);
		if (null == _stockAdapter)
        {
            _stockAdapter = new StockAdapter();
            ViewPager vp = view.findViewById(R.id.view_pager);
            vp.setAdapter(_stockAdapter);
        }
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
	{

	    LayoutInflater inflater = CoachACook.getCoach().getLayoutInflater();
	    View dialogView = inflater.inflate(R.layout.stock_dialog, parent);
	    
	    TextView ingredient_name_tv = view.findViewById(R.id.stock_item_name);
	    final String ingredient_name = ingredient_name_tv.getText().toString();
	    TextView ingredient_quantity = view.findViewById(R.id.stock_item_quantity);
		TextView ingredient_unit = view.findViewById(R.id.stock_item_unit);
		
		final EditText quantity = dialogView.findViewById(R.id.stock_dialog_item_quantity);
		((TextView)dialogView.findViewById(R.id.stock_dialog_item_name)).setText(ingredient_name);
		quantity.setText(ingredient_quantity.getText());
		((TextView)dialogView.findViewById(R.id.stock_dialog_item_unit)).setText(ingredient_unit.getText());
	
		AlertDialog.Builder builder = new AlertDialog.Builder(CoachACook.getCoach());
		builder.setView(dialogView);
	    builder.setMessage(R.string.update_ingredient);
		builder.setPositiveButton(R.string.update_yes, new DialogInterface.OnClickListener() 
		{ 	public void onClick(DialogInterface dialog, int id)
			{
				Double amount = Double.valueOf(quantity.getText().toString());
				if (!CoachACook.getCoach().getRecipesDB().updateStock(ingredient_name,amount))
				{
	    			String message = CoachACook.getCoach().getResources().getString(R.string.invalid_ingredient);
	    			message += ": ";
	    			message += ingredient_name;
	    			Toast toast = Toast.makeText(CoachACook.getCoach(), message, Toast.LENGTH_LONG);
	    			toast.show();
				}
			}
		});
		builder.setNegativeButton(R.string.update_no, new DialogInterface.OnClickListener() 
		{	public void onClick(DialogInterface dialog, int id)
			{ 	}
		}); 
		AlertDialog dialog = builder.create();
		dialog.show();
	}
}

