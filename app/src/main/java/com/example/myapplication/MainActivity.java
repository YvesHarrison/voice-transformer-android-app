package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.SpeechRecognizer;
import android.view.View;
import android.widget.*;
import android.speech.RecognizerIntent;
import android.widget.Button;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Iterator;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends Activity {
    protected static final int RESULT_SPEECH = 2;
    private SpeechRecognizer sr;
    private testnetwork nt;
    EditText edit;
    TextView no_internet;
    private ListView  mylistview;
    private DBManager dbManager;
    Context context;
    List<String> mList;
    List<String> mkeywords = new ArrayList<String>();
    private ListView listView;
    ArrayAdapter<String> adapter;
    ArrayAdapter<String> adapter2;
    ListView l2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context =this;
        l2=(ListView)findViewById(R.id.lv2);
        Button butt1 = (Button)findViewById(R.id.button1);
        Button butt2 = (Button)findViewById(R.id.ok);
        no_internet = findViewById(R.id.no_network);
        dbManager =new DBManager(this);
        listView=(ListView)findViewById(R.id.lv);
        List<note> notes = notes = dbManager.query();

        ArrayList<String> messageRecord = new ArrayList();
        for (int i = notes.size() - 1; i >= 0; --i) {

            messageRecord.add((String) notes.get(i).text);

        }
        int j=notes.size();
        ArrayList<Vector> keyWords = messageStatistic(messageRecord);
        for (Vector vector : keyWords) {
                String s="";
                s+=j+"         ";
                j--;
                for (int i = 0; i < vector.size(); ++i) {
                    s += vector.get(i) + " ";
                }
//            Toast.makeText(MainActivity.this, "long "+s, Toast.LENGTH_LONG).show();
                if (vector.size() != 0)
                    mkeywords.add(s);
        }
//        Toast.makeText(MainActivity.this, mkeywords.size(), Toast.LENGTH_LONG).show();
        adapter2=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,mkeywords);
        l2.setAdapter(adapter2);
        mList = getArray();
        adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,mList);
        listView.setAdapter(adapter);//设置适配器
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(MainActivity.this, "long "+mList.get(position).split(" ")[0], Toast.LENGTH_LONG).show();
                dbManager.deletenote(Integer.parseInt(mList.get(position).split(" ")[0]));
                mList = getArray();
                adapter=new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1,mList);
                listView.setAdapter(adapter);
                List<note> notes = notes = dbManager.query();

                ArrayList<String> messageRecord = new ArrayList();
                for (int i = notes.size() - 1; i >= 0; --i) {

                    messageRecord.add((String) notes.get(i).text);

                }
                int j=notes.size();
                ArrayList<Vector> keyWords = messageStatistic(messageRecord);
                for (Vector vector : keyWords) {
                    String s="";
                    s+=j+"          ";
                    j--;
                    for (int i = 0; i < vector.size(); ++i) {
                        s += vector.get(i) + " ";
                    }
                    if (vector.size() != 0)
                        mkeywords.add(s);
                }
                adapter2=new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1,mkeywords);
                l2.setAdapter(adapter2);
                return false;
            }
        });
        displaySpeechRecognizer();
        //按钮
        butt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                }
        });
        butt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(System.currentTimeMillis());
                note n= new note();
                edit =findViewById(R.id.edit);
                n.text=edit.getText().toString();
                n.date=df.format(date);
                n.pid=10;
                dbManager.addone(n);
                mList = getArray();
                adapter=new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1,mList);
                listView.setAdapter(adapter);
                List<note> notes = notes = dbManager.query();

                ArrayList<String> messageRecord = new ArrayList();
                for (int i = notes.size() - 1; i >= 0; --i) {

                    messageRecord.add((String) notes.get(i).text);
                    Toast.makeText(MainActivity.this, "long "+notes.get(i).text, Toast.LENGTH_LONG).show();

                }
                int j=notes.size();
                ArrayList<Vector> keyWords = messageStatistic(messageRecord);
                for (Vector vector : keyWords) {
                    String s="";
                    s+=j+"         ";
                    j--;
                    for (int i = 0; i < vector.size(); ++i) {
                        s += vector.get(i) + " ";
                    }
//            Toast.makeText(MainActivity.this, "long "+s, Toast.LENGTH_LONG).show();
                    if (vector.size() != 0)
                        mkeywords.add(s);
                }
//        Toast.makeText(MainActivity.this, mkeywords.size(), Toast.LENGTH_LONG).show();
                adapter2=new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1,mkeywords);
                l2.setAdapter(adapter2);
                edit.setText("");
            }
        });
    }

    private static final int SPEECH_REQUEST_CODE = 0;

    // Create an intent that can start the Speech Recognizer activity
    private void displaySpeechRecognizer() {
        // know the state of this system.
        Locale locale = Locale.getDefault();
        String lan = locale.getDisplayLanguage();
        boolean network = nt.isNetworkAvailable(this);
        // start intent
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE,"zh-HK");
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"hello");
        if(network){
           no_internet.setVisibility(View.INVISIBLE);
        }
        else{
            no_internet.setVisibility(View.VISIBLE);
        }
        //if there is no internetwork offline.
        intent.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE,!network);
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false);
        intent.putExtra(RecognizerIntent.ACTION_VOICE_SEARCH_HANDS_FREE,1);
// Start the activity, the intent will be populated with the speech text
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }


    // This callback is invoked when the Speech Recognizer returns.
// This is where you process the intent and extract the speech text from the intent.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String spokenText =null;
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            spokenText = results.get(0);
            if (!spokenText.contains("stop taking note")) {

                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(System.currentTimeMillis());
                note n = new note(spokenText, df.format(date), 10);
                dbManager.addone(n);
                mList = getArray();
                adapter=new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1,mList);
                listView.setAdapter(adapter);
                List<note> notes = notes = dbManager.query();
                ArrayList<String> messageRecord = new ArrayList();
                for (int i = notes.size() - 1; i >= 0; --i) {

                    messageRecord.add((String) notes.get(i).text);

                }
                ArrayList<Vector> keyWords = messageStatistic(messageRecord);
                int j=0;
                for (Vector vector : keyWords) {
                    j++;
                    String s="";
                    s+=j;
                    s+="          ";
                    for (int i = 0; i < vector.size(); ++i) {
                        s += vector.get(i) + " ";
                    }
                    if (vector.size() != 0)
                        mkeywords.add(s);
                }
                adapter2=new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1,mkeywords);
            }

        }

        super.onActivityResult(requestCode, resultCode, data);
        if(!spokenText.contains("stop taking note"))
            displaySpeechRecognizer();
    }
    public void query()
    {
        List<note> notes = dbManager.query();
//        ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
        String s="";
        ArrayList<String> record = new ArrayList();
        for (note note : notes){
//            Toast.makeText(MainActivity.this,note.text,Toast.LENGTH_LONG).show();
            s+=note.tid+"   "+note.text;
            record.add(note.text);
        }

        Toast.makeText(MainActivity.this,s,Toast.LENGTH_LONG).show();
    }
    public void querypassage()
    {
        List<passage> passages = dbManager.querypassage();
//        ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
        String s="";
        for (passage passage : passages){
//            Toast.makeText(MainActivity.this,note.text,Toast.LENGTH_LONG).show();
            s+=passage.pid+"   "+passage.name;
        }
        Toast.makeText(MainActivity.this,s,Toast.LENGTH_LONG).show();
    }

    protected ArrayList<Vector> messageStatistic(ArrayList<String> messages){
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

        //ArrayList<Hashtable> sorted = new ArrayList();
        ArrayList<Vector> sorted = new ArrayList();
        for(int k=0;k<tf_idf.size();++k){
            //Hashtable sort_piece = new Hashtable();
            Vector sort_piece = new Vector();
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
            for (int i=0;i<(int)(0.25*v.size())&&i<10;++i) {
                //System.out.print(v.get(i) + " : " +(double)tf_idf_piece.get(v.get(i))+"\n");
                //sort_piece.put(v.get(i),(double)tf_idf_piece.get(v.get(i)));
                sort_piece.add(v.get(i));
            }
            //System.out.print("\n");
            sorted.add(sort_piece);
        }
        return sorted;
    }
    ArrayList<String> getArray(){
        ArrayList<String> arr = new ArrayList<String>();
        List<note> notes = dbManager.query();
        for(note note: notes){
            String temp = note.tid+"          "+note.text;
            arr.add(temp);
        }
        return arr;
    }


}
