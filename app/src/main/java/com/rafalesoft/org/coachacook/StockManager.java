package com.rafalesoft.org.coachacook;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

class StockManager extends RecipesCursorHolder implements AdapterView.OnItemClickListener
{
    StockManager(View view)
    {
        ListView lvl = view.findViewById(R.id.stock_list_view);

        String[] projection = { RecipesDB.ID,
                RecipesDB.NAME,
                Ingredient.COLUMN_STOCK_TITLE,
                Ingredient.COLUMN_UNIT_TITLE };
        updateCursor(Ingredient.TABLE_NAME, projection);

        String[] fromColumns = {RecipesDB.NAME,
                Ingredient.COLUMN_STOCK_TITLE,
                Ingredient.COLUMN_UNIT_TITLE};
        int[] toViews = { R.id.stock_item_name, R.id.stock_item_quantity, R.id.stock_item_unit};

        SimpleCursorAdapter recipesDBAdapter =
                new SimpleCursorAdapter(_cook,R.layout.stock_view_item,
                        getCursor(),fromColumns,toViews,0);

        lvl.setAdapter(recipesDBAdapter);
        lvl.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        LayoutInflater inflater = _cook.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.stock_dialog, parent);

        TextView ingredient_name_tv = view.findViewById(R.id.stock_item_name);
        final String ingredient_name = ingredient_name_tv.getText().toString();
        TextView ingredient_quantity = view.findViewById(R.id.stock_item_quantity);
        TextView ingredient_unit = view.findViewById(R.id.stock_item_unit);

        final EditText quantity = dialogView.findViewById(R.id.stock_dialog_item_quantity);
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
    }
}
