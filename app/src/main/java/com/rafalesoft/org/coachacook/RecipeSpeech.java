package com.rafalesoft.org.coachacook;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;

public class RecipeSpeech implements RecognitionListener
{
    private static TextToSpeech _tts = null;
    private static SpeechRecognizer _sr = null;
    private Context _ctx;
    private RecognitionCallback _rc = null;

    private static final int _toRecognize[] = { R.string.speech_demarre,
                                                R.string.speech_apres,
                                                R.string.speech_avant,
                                                R.string.speech_repete,
                                                R.string.speech_recommence};

    public interface RecognitionCallback
    {
        void onRecognized(int stringId);
    }

    public boolean speak(String speech)
    {
        if (null == _tts)
            return false;
        return (TextToSpeech.SUCCESS == _tts.speak(speech, _tts.QUEUE_ADD, null, "recipe_step"));
    }

    public RecipeSpeech(Context ctx)
    {
        _ctx = ctx;

        if (!SpeechRecognizer.isRecognitionAvailable(ctx))
        {
            Toast toast = Toast.makeText(ctx, "Speech recognition unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }
        else
        {
            _sr = SpeechRecognizer.createSpeechRecognizer(ctx);
            _sr.setRecognitionListener(this);
        }

        _tts = new TextToSpeech(ctx, new TextToSpeech.OnInitListener()
        {
            @Override
            public void onInit(int i)
            {
                if (TextToSpeech.SUCCESS == i)
                {
                    Set<Voice> voices = _tts.getVoices();
                    String lan = PreferenceManager.getDefaultSharedPreferences(_ctx)
                        .getString(_ctx.getString(R.string.language_key), "");

                    if (TextToSpeech.SUCCESS != _tts.setLanguage(new Locale(lan)))
                    {
                        Toast toast = Toast.makeText(_ctx, "Unsupported language "+lan, Toast.LENGTH_SHORT);
                        toast.show();
                    }

                    Voice V = null;
                    for (Voice vv : voices)
                        if (vv.getLocale().toString().equals(lan))
                            V = vv;

                    int v = _tts.setVoice(V);
                    int s = _tts.speak("Bienvenue a Coach eu cook", _tts.QUEUE_ADD, null, "bienvenue");
                }
            }
        });
    }

    public void Recognize(RecognitionCallback rc)
    {
        if (null != _sr)
        {
            _rc = rc;

            try
            {
                while (_tts.isSpeaking())
                    Thread.sleep(100);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }

            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, RecipeSpeech.class.getPackage().getName());
            intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);

            _sr.startListening(intent);
            Log.d("STT", "startListening()");
        }
    }


    public void DestroySpeech()
    {
        if (null != _tts)
            _tts.shutdown();
        if (null != _sr)
            _sr.destroy();
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
            if (floatArray[0] < 0.5f)
                _tts.speak(_ctx.getString(R.string.speech_not_understood), _tts.QUEUE_ADD, null, "incompris");
            else
            {
                boolean understood = false;
                String msg = stringArrayList.get(0);
                for (int id:_toRecognize)
                {
                    if (msg.equals(_ctx.getString(id)))
                    {
                        understood = true;
                        if (null != _rc)
                            _rc.onRecognized(id);
                    }
                }

                if (!understood)
                {
                    _tts.speak(_ctx.getString(R.string.speech_not_understood), _tts.QUEUE_ADD, null, "incompris");
                }
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
