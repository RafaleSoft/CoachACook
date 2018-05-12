package com.rafalesoft.org.coachacook;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;


public class ManageStock extends RecipesCursorHolder implements OnClickListener, OnItemClickListener 
{
	public ManageStock(CoachACook owner,View v) 
	{
		super(owner);
		v.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v)
	{
		LinearLayout layout = (LinearLayout)_cook.switchToView(R.id.stock_view);
		ListView lvl = layout.findViewById(R.id.stock_list_view);
		
		String[] projection = { RecipesDB.ID,
								Ingredient.COLUMN_NAME_TITLE, 
								Ingredient.COLUMN_STOCK_TITLE,
								Ingredient.COLUMN_UNIT_TITLE };
		String[] selectionArgs = { };
		
		_cursor = _cook.getRecipesDB().query(	Ingredient.TABLE_NAME,
												projection,
												"", selectionArgs,
												Ingredient.COLUMN_NAME_TITLE);
		
		String[] fromColumns = {Ingredient.COLUMN_NAME_TITLE,
								Ingredient.COLUMN_STOCK_TITLE,
								Ingredient.COLUMN_UNIT_TITLE};
		int[] toViews = { R.id.stock_item_name, R.id.stock_item_quantity, R.id.stock_item_unit};
	
		SimpleCursorAdapter recipesDBAdapter = 
				new SimpleCursorAdapter(_cook,R.layout.stock_view_item,
										_cursor,fromColumns,toViews,0);

		lvl.setAdapter(recipesDBAdapter);
		lvl.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
	{
		/*
	    LayoutInflater inflater = _cook.getLayoutInflater();
	    View dialogView = inflater.inflate(R.layout.stock_dialog, null);
	    
	    TextView ingredient_name_tv = (TextView)view.findViewById(R.id.stock_item_name);
	    final String ingredient_name = ingredient_name_tv.getText().toString();
	    TextView ingredient_quantity = (TextView)view.findViewById(R.id.stock_item_quantity);
		TextView ingredient_unit = (TextView)view.findViewById(R.id.stock_item_unit);
		
		final EditText quantity = (EditText)dialogView.findViewById(R.id.stock_dialog_item_quantity);
		((TextView)dialogView.findViewById(R.id.stock_dialog_item_name)).setText(ingredient_name);
		quantity.setText(ingredient_quantity.getText());
		((TextView)dialogView.findViewById(R.id.stock_dialog_item_unit)).setText(ingredient_unit.getText());
	
		AlertDialog.Builder builder = new AlertDialog.Builder(_cook);
		builder.setView(dialogView);
	    builder.setMessage(R.string.update_ingredient);
		builder.setPositiveButton(R.string.update_yes, new DialogInterface.OnClickListener() 
		{ 	public void onClick(DialogInterface dialog, int id)
			{
				Double amount = Double.valueOf(quantity.getText().toString());
				if (!_cook.getRecipesDB().updateStock(ingredient_name,amount))
				{
	    			String message = _cook.getResources().getString(R.string.invalid_ingredient);
	    			message += ": ";
	    			message += ingredient_name;
	    			Toast toast = Toast.makeText(_cook, message, Toast.LENGTH_LONG);
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
		*/
	}

	public void search(String query)
	{
		// TODO Auto-generated method stub
	}
}
