package com.example.myapplication;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognitionListener;
import android.os.Bundle;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.widget.*;
import android.speech.RecognizerIntent;
import android.widget.Button;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Iterator;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;

import static android.content.ContentValues.TAG;

public class MainActivity extends Activity {
    protected static final int RESULT_SPEECH = 2;
    private SpeechRecognizer sr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button butt1 = (Button)findViewById(R.id.button1);
        Button butt2 = (Button)findViewById(R.id.button2);
        Locale locale = Locale.getDefault();
        String lan = locale.getDisplayLanguage();
        Toast.makeText(MainActivity.this,lan,Toast.LENGTH_LONG).show();
       //尝试连续
        sr = SpeechRecognizer.createSpeechRecognizer(this);
//        sr.setRecognitionListener(new listener());

        //非连续
        displaySpeechRecognizer();
        //按钮
        butt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,"voice.recognition.test");
                intent.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE,true);
//                intent.putExtra(RecognizerIntent.RESULT_AUDIO_ERROR,);
//                intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,5);
                sr.startListening(intent);
                Toast.makeText(MainActivity.this,"点击了登录",Toast.LENGTH_LONG).show();
//                Log.i("111111","11111111");
                }
        });
//        butt2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(MainActivity.this,"=========",Toast.LENGTH_LONG).show();
//            }
//        });
    }
    private static final int SPEECH_REQUEST_CODE = 0;

    // Create an intent that can start the Speech Recognizer activity
    private void displaySpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE,"zh-HK");
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"hello");
        intent.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE,true);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"zh-HK");
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,"zh-HK");
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "zh-HK");
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false);
        intent.putExtra(RecognizerIntent.ACTION_VOICE_SEARCH_HANDS_FREE,1);
// Start the activity, the intent will be populated with the speech text
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }


    // This callback is invoked when the Speech Recognizer returns.
// This is where you process the intent and extract the speech text from the intent.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            Toast.makeText(MainActivity.this,spokenText,Toast.LENGTH_LONG).show();
            // Do something with spokenText
        }
        super.onActivityResult(requestCode, resultCode, data);
        displaySpeechRecognizer();
    }

    public static void messageStatistic(ArrayList<String> messages){
        //tf calculate
        ArrayList<Hashtable> tf = new ArrayList();
        for(int k=0;k<messages.size();++k){
            String message = messages.get(k);
            String[] splited = message.split("\\s+");
            int length = splited.length;
            Hashtable statistic = new Hashtable();
            //System.out.print(length+"\n");
            for (int i=0;i<length;++i){
                if(!statistic.containsKey(splited[i])) statistic.put(splited[i],0.0);
                statistic.put(splited[i],(double)statistic.get(splited[i])+1.0);
            }
            //Hashtable tf = new Hashtable();
            for(Iterator it = statistic.keySet().iterator(); it.hasNext();) {
                String key = (String) it.next();
                double value = (double) statistic.get(key);
                statistic.put(key, value / length);
            }
            tf.add(statistic);
        }
        //df calculate
        ArrayList<Hashtable> df = new ArrayList();
        for(int k=0;k<tf.size();++k){
            Hashtable tf_piece = tf.get(k);
            Hashtable df_piece = new Hashtable();
            for(Iterator it = tf_piece.keySet().iterator(); it.hasNext();) {
                String key = (String) it.next();
                for(int j=0;j<tf.size();++j){
                    Hashtable tf_piece1 = tf.get(j);
                    if(!tf_piece1.containsKey(key)) {
                        if (!df_piece.containsKey(key)) df_piece.put(key,0.0);
                        else df_piece.put(key,(double)df_piece.get(key));
                    }
                    else {
                        if (!df_piece.containsKey(key)) df_piece.put(key,0.0);
                        df_piece.put(key,(double)df_piece.get(key)+1.0);
                    }
                }
            }
            df.add(df_piece);
        }

        ArrayList<Hashtable> tf_idf = new ArrayList();
        for(int k=0;k<tf.size();++k){
            Hashtable tf_piece = tf.get(k);
            Hashtable df_piece = df.get(k);
            Hashtable tf_idf_piece = new Hashtable();
            for(Iterator it = tf_piece.keySet().iterator(); it.hasNext();) {
                String key = (String) it.next();
                tf_idf_piece.put(key,(double)tf_piece.get(key)/(Math.log((double)(df_piece.get(key)))+1));
            }
            tf_idf.add(tf_idf_piece);
        }

        ArrayList<Hashtable> sorted = new ArrayList();
        for(int k=0;k<tf_idf.size();++k){
            Hashtable sort_piece = new Hashtable();
            final Hashtable tf_idf_piece = tf_idf.get(k);
            List<String> v = new ArrayList<String>(tf_idf_piece.keySet());
            Collections.sort(v,new Comparator<Object>(){
                        public int compare(Object arg0,Object arg1)
                        {
                            double tag = (double)tf_idf_piece.get(arg1) - (double)(tf_idf_piece.get(arg0));
                            if(tag>0){
                                return 1;
                            }else if(tag<0){
                                return -1;
                            }else{
                                return 0;
                            }
                        }
                    }
            );
            for (String str : v) {
                //System.out.print(str + " : " +(double)tf_idf_piece.get(str)+"\n");
                sort_piece.put(str,(double)tf_idf_piece.get(str));
            }
            //System.out.print("\n");
            sorted.add(sort_piece);
        }
    }
}
