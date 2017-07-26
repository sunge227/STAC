package dgsw.hs.kr.nugu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;

import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by minseong on 2017-07-26.
 */

public class SpeechResponse extends Activity implements OnInitListener{

    private TextView text;
    ArrayList<String> results;

    /*@Override
    public void onInit(int status) {
        String myText1 = "안녕하세요";
        String myText2 = "말하는 스피치 입니다";
        myTTs.speak(myText1 , TextToSpeech.QUEUE_FLUSH , null);
        myTTs.speak(myText2 , TextToSpeech.QUEUE_ADD , null);
    }*/

    private TextToSpeech myTTs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_response);
        text = (TextView) findViewById(R.id.listen);
        text.setText("");
        //myTTs = new TextToSpeech(this , this);
        //onInit(1);

        //onDestory();

        findViewById(R.id.speech).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voice();
            }
        });

    }

    @Override
    public void onInit(int status){
        String myText1 = "아 밥먹고 싶다";
        String myText2 = "말하는 스피치 입니다";

        String[] arr = new String[results.size()];

        for(int i = 0 ; i < arr.length;i++)
        {
            arr[i] = results.get(i);
        }

        myTTs.speak(arr[0],TextToSpeech.QUEUE_FLUSH , null );

        for(int i = 1; i < arr.length;i++)
            myTTs.speak(arr[i],TextToSpeech.QUEUE_ADD,null);

        /*myTTs.speak(myText1 , TextToSpeech.QUEUE_FLUSH , null);
        myTTs.speak(myText2 , TextToSpeech.QUEUE_ADD , null);*/
    }

    protected  void onDestory(){
        super.onDestroy();
        myTTs.shutdown();
    }

    public void voice(){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"speak");
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 20);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        startActivityForResult(intent , 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == 1 && resultCode == RESULT_OK)
        {
            myTTs = new TextToSpeech(this , this);

            results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            for (int i = 0; i < results.size(); i++) {
                text.append(results.get(i) + "\n");
            }

            onInit(1);
            //onDestory();

        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
