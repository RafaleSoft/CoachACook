package com.rafalesoft.org.coachacook;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;

import static android.speech.tts.TextToSpeech.*;

public class RecipeSpeech implements RecognitionListener
{
    private static TextToSpeech _tts = null;
    private static SpeechRecognizer _sr = null;
    private final Context _ctx;
    private RecognitionCallback _rc = null;

    private static final int _toRecognize[] = { R.string.speech_start,
                                                R.string.speech_next,
                                                R.string.speech_previous,
                                                R.string.speech_repeat,
                                                R.string.speech_restart,
                                                R.string.speech_finish};

    public interface RecognitionCallback {
        boolean onRecognized(int stringId);
    }

    public void stopRecognize()
    {
        if (null != _sr)
            _sr.stopListening();
    }

    public boolean speak(String speech)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            return (null != _tts) && (SUCCESS == _tts.speak(speech,
                                                            QUEUE_ADD,
                                                            null,
                                                            "recipe_step"));
        else
            return (null != _tts) && (SUCCESS == _tts.speak(speech, QUEUE_ADD, null));
    }

    public RecipeSpeech(Context ctx)
    {
        _ctx = ctx;

        if (!SpeechRecognizer.isRecognitionAvailable(ctx))
        {
            Toast toast = Toast.makeText(ctx, "Speech recognition unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }
        else if (null == _sr)
        {
            _sr = SpeechRecognizer.createSpeechRecognizer(ctx);
            _sr.setRecognitionListener(this);
        }

        if (null == _tts)
            _tts = new TextToSpeech(ctx, new OnInitListener()
            {
                @Override
                public void onInit(int i)
                {
                    if (SUCCESS == i)
                    {
                        String lan = PreferenceManager.getDefaultSharedPreferences(_ctx)
                            .getString(_ctx.getString(R.string.language_key), "");

                        if (SUCCESS != _tts.setLanguage(new Locale(lan)))
                        {
                            Toast toast = Toast.makeText(_ctx, ctx.getString(R.string.speech_language)+lan, Toast.LENGTH_SHORT);
                            toast.show();
                        }

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                        {
                            Voice V = null;
                            Set<Voice> voices = _tts.getVoices();
                            for (Voice vv : voices)
                                if (vv.getLocale().toString().equals(lan))
                                    V = vv;

                            if (null != V)
                                _tts.setVoice(V);
                            _tts.speak(ctx.getString(R.string.welcome), QUEUE_ADD, null, "bienvenue");
                        }
                        else
                            _tts.speak(ctx.getString(R.string.welcome), QUEUE_ADD, null);
                    }
                }
            });
    }

    public void recognize(RecognitionCallback rc)
    {
        if (null != _sr)
        {
            _rc = rc;

            try
            {
                Thread.sleep(200);
                while (_tts.isSpeaking())
                {
                    Log.d("STT", "TTS Buzy ...");
                    Thread.sleep(200);
                }
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }

            try
            {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                //intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, RecipeSpeech.class.getPackage().getName());
                intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);

                _sr.startListening(intent);
                Log.d("STT", "startListening()");
            }
            catch (ActivityNotFoundException e)
            {
                e.printStackTrace();
                Toast toast = Toast.makeText(_ctx, "There is no voice recognition installed or configured on this device", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }


    public void destroySpeech()
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
        switch (i)
        {
            case SpeechRecognizer.ERROR_AUDIO:
                Log.e("STT",  "Audio recording error");
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                Log.e("STT",  "Other client side errors");
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                Log.e("STT",  "Insufficient permissions ");
                Toast toast = Toast.makeText(_ctx, _ctx.getString(R.string.permission_audio), Toast.LENGTH_SHORT);
                toast.show();
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                Log.e("STT",  "Other network related errors");
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                Log.e("STT",  "Network operation timed out");
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                Log.e("STT",  "No recognition result matched");
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                Log.e("STT",  "Recognition service busy");
                break;
            case SpeechRecognizer.ERROR_SERVER:
                Log.e("STT",  "Server sends error status");
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                Log.e("STT",  "No speech input");
                break;
            default:
                break;
        }

        recognize(_rc);
    }

    @Override
    public void onResults(Bundle bundle)
    {
        Log.d("STT","onResults()");
        final ArrayList<String> stringArrayList = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        final float[] floatArray = bundle.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES);

        if ((null == stringArrayList) || (null == floatArray))
            return;

        if (!stringArrayList.isEmpty())
        {
            if (floatArray[0] < 0.5f)
            {
                Log.d("STT","... poor confidence score: " + floatArray[0] + " -> " + stringArrayList.get(0));
                _rc.onRecognized(R.string.speech_not_understood);
            }
            else
            {
                boolean understood = false;
                String msg = stringArrayList.get(0);
                for (int id:_toRecognize)
                {
                    Log.d("STT"," try recognize: "+msg+" vs "+_ctx.getString(id));
                    if (msg.equals(_ctx.getString(id)))
                    {
                        understood = true;
                        if (null != _rc)
                            _rc.onRecognized(id);
                        break;
                    }
                }

                if (!understood)
                {
                    Log.d("STT"," nothing recognized");
                    _rc.onRecognized(R.string.speech_not_understood);
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

        if ((null == stringArrayList) || (null == floatArray))
            return;

        if (!stringArrayList.isEmpty())
        {
            for (int i = 0; i < stringArrayList.size(); i++)
                Log.d("STT", "result: " + stringArrayList.get(i));
            for (float aFloatArray : floatArray)
                Log.d("STT", "confidence: " + aFloatArray);
        }
    }

    @Override
    public void onEvent(int i, Bundle bundle)
    {
        Log.d("STT","onEvent() " + i);
    }
}
