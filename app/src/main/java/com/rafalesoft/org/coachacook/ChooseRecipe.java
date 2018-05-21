package com.rafalesoft.org.coachacook;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;

public class ChooseRecipe extends RecipesCursorHolder implements OnClickListener, OnItemClickListener, RecognitionListener
{
    private static TextToSpeech tts = null;
    private static SpeechRecognizer sr = null;

    public static final int components[] = {R.id.component1,
                                            R.id.component2,
                                            R.id.component3,
                                            R.id.component4,
                                            R.id.component5,
                                            R.id.component6,
                                            R.id.component7};

    public void DestroySpeech()
    {
        if (null != tts)
            tts.shutdown();
        if (null != sr)
            sr.destroy();
    }

    public ChooseRecipe(CoachACook owner)
	{
		super(owner);
		if (!SpeechRecognizer.isRecognitionAvailable(owner))
        {
            Toast toast = Toast.makeText(owner, "Speech recognition unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }
        else
        {
            sr = SpeechRecognizer.createSpeechRecognizer(owner);
            sr.setRecognitionListener(this);
        }

		tts = new TextToSpeech(owner, new TextToSpeech.OnInitListener()
        {
            @Override
            public void onInit(int i)
            {
                if (TextToSpeech.SUCCESS == i)
                {
                    int max = tts.getMaxSpeechInputLength();

                    Set<Voice> voices = tts.getVoices();
                    Set<Locale> languages = tts.getAvailableLanguages();

                    int lang = tts.setLanguage(new Locale("fr_FR"));
                    Voice V = null;
                    for (Voice vv: voices)
                    {
                        if (vv.getName().startsWith("fr-FR") || (vv.getName().startsWith("fr-fr")))
                            V = vv;
                    }

                    int v = tts.setVoice(V);
                    int s = tts.speak("Bienvenue a Coach eu cook", tts.QUEUE_ADD, null, "bienvenue");
                }
            }
        });
	}


	@Override
	public void onClick(View v)
	{
        View stock = _cook.switchToView(R.id.recipe_stockview);
        ListView lvl = stock.findViewById(R.id.recipe_list);

        String[] projection = {RecipesDB.ID, RecipesDB.NAME};
        String[] selectionArgs = {};

        _cursor = _cook.getRecipesDB().query(   Recipe.TABLE_NAME,
                                                projection,
                                                "",
                                                selectionArgs,
                                                RecipesDB.NAME);

        String[] fromColumns = { RecipesDB.NAME };
        int[] toViews = { R.id.recipe_item_name };

        SimpleCursorAdapter recipesDBAdapter =
                new SimpleCursorAdapter(_cook, R.layout.recipe_stockview_item,
                        _cursor, fromColumns, toViews, 0);

        lvl.setAdapter(recipesDBAdapter);
        lvl.setOnItemClickListener(this);
	}

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
    {
        View recipeView = _cook.switchToView(R.id.recipe_view);

        TextView recipe_name = view.findViewById(R.id.recipe_item_name);
        TextView tv_name = recipeView.findViewById(R.id.recipe_name);
        tv_name.setText(recipe_name.getText());

        Recipe r = _cook.getRecipesDB().getRecipe(recipe_name.getText().toString());

        TextView tv_desc = recipeView.findViewById(R.id.recipe_description);
        tv_desc.setText(r.get_preparation());

        TableLayout table = recipeView.findViewById(R.id.recipe_ingredients);

        for (int item=0;item<r.nbComponents();item++)
        {
            RecipeComponent c = r.getComponent(item);
            View row = table.getChildAt(item);
            TextView tvi = row.findViewById(components[item]);

            String ingredient = Integer.valueOf(c.get_quantity().intValue()).toString() +
                    " " + (null == c.get_unit() ? "":c.get_unit()) + " " + c.get_name();
            tvi.setText(ingredient);
        }

        for (int j=r.nbComponents();j<7;j++)
        {
            View row = table.getChildAt(j);
            TextView tvi = row.findViewById(components[j]);
            tvi.setText("");
        }

        //if (null != tts)
        // tts.speak(r.get_preparation(),tts.QUEUE_ADD, null, "recette");
        if (null != sr)
        {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "com.rafalesoft.org.coachacook");
            intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);

            sr.startListening(intent);
            Log.d("STT", "startListening()");
        }
    }

    @Override
    public void onReadyForSpeech(Bundle bundle)
    {
        //Log.d("STT","onReadyForSpeech()");
    }

    @Override
    public void onBeginningOfSpeech()
    {
        Log.d("STT","onBeginningOfSpeech()");
    }

    @Override
    public void onRmsChanged(float v)
    {
        //Log.d("STT","onRmsChanged()");
    }

    @Override
    public void onBufferReceived(byte[] bytes)
    {
        //Log.d("STT","onBufferReceived()");
    }

    @Override
    public void onEndOfSpeech()
    {
        Log.d("STT","onEndOfSpeech()");
    }

    @Override
    public void onError(int i)
    {
        Log.d("STT",  "error " +  i);
    }

    @Override
    public void onResults(Bundle bundle)
    {
        Log.d("STT","onResults()");
        final ArrayList<String> stringArrayList = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        final float[] floatArray = bundle.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES);

        if (!stringArrayList.isEmpty())
        {
            //for (int i = 0; i < stringArrayList.size(); i++)
            //{
            //Log.d("STT", "result: " + stringArrayList.get(i));
            //}
            //for (int i = 0; i < floatArray.length; i++)
            //{
            //    Log.d("STT", "confidence: " + floatArray[i]);
            //}
            if (floatArray[0] < 0.5f)
                tts.speak("Je n'ai pas compris", tts.QUEUE_ADD, null, "incompris");
            else
            {
                String msg = stringArrayList.get(0);
                Toast toast = Toast.makeText(_cook, "J'ai compris: " + msg, Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    @Override
    public void onPartialResults(Bundle bundle)
    {
        Log.d("STT","onPartialResults()");
        final ArrayList<String> stringArrayList = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        final float[] floatArray = bundle.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES);

        if (!stringArrayList.isEmpty())
        {
            for (int i = 0; i < stringArrayList.size(); i++)
            {
                Log.d("STT", "result: " + stringArrayList.get(i));
            }
            for (int i = 0; i < floatArray.length; i++)
            {
                Log.d("STT", "confidence: " + floatArray[i]);
            }
        }
    }

    @Override
    public void onEvent(int i, Bundle bundle)
    {
        Log.d("STT","onEvent() " + i);
    }
}
