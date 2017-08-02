package dgsw.hs.kr.nugu;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
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
    simulation sim ;
    message msg;
    boolean isService = false;
    private TextToSpeech myTTs;
    int situation=0;
    int one = 0;

    ServiceConnection sconn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            simulation.MyBinder mb = (simulation.MyBinder) service;
            sim = mb.getService();
            isService = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isService = false;
        }
    }; // simulation service

    ServiceConnection mconn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            message.MyBinder mb = (message.MyBinder) service;
            msg= mb.getService();
            isService = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isService = false;
        }
    }; // message service

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
                Intent intent = new Intent(
                        SpeechResponse.this, // 현재 화면
                        simulation.class); // 다음넘어갈 컴퍼넌트

                bindService(intent, // intent 객체
                        sconn, // 서비스와 연결에 대한 정의
                        Context.BIND_AUTO_CREATE);

                // simulation binding

                // message binding
                voice();
                one = 0 ;

            }
        });



    }

    @Override
    public void onInit(int status){
        /*String[] arr = new String[results.size()];

        for(int i = 0 ; i < arr.length;i++)
        {
            arr[i] = results.get(i);
        }

        myTTs.speak(arr[0],TextToSpeech.QUEUE_FLUSH , null );

        for(int i = 1; i < arr.length;i++)
            myTTs.speak(arr[i],TextToSpeech.QUEUE_ADD,null);*/

        // 말 한것을 말해주는 소스코드

        /*myTTs.speak(myText1 , TextToSpeech.QUEUE_FLUSH , null);
        myTTs.speak(myText2 , TextToSpeech.QUEUE_ADD , null);*/

        //TextToSpeech.QUEUE_FLUSH 는 초기에 QUEUE_ADD 는 덧붙이는것
        Log.d("확인 status",String.valueOf(status));
        if(situation == 1){
            String tell = "전화 연결이 필요 하십니까?";
            myTTs.speak(tell , TextToSpeech.QUEUE_FLUSH , null);
        }else if(situation == 2) {
            Log.d("확인", "in two scope");
            String tell = "문자를 전송합니다.";
            myTTs.speak(tell, TextToSpeech.QUEUE_FLUSH, null);
            if (one == 0){
                Intent intent = new Intent(
                        getApplicationContext(),//현재제어권자
                        message.class); // 이동할 컴포넌트
                startService(intent); // 서비스 시작
                stopService(intent);
                one ++;
        }
        }else{
            String tell = "에러 에러 에러 에러";
            myTTs.speak(tell , TextToSpeech.QUEUE_FLUSH , null);
        }


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
            /*
                음성 대답 코드
             */

            if(!isService) {
                Log.d("에러","no service connect");
            }else{
                boolean[] state = sim.CheckString(results);

                Log.d("확인 state[]", String.valueOf(state[0]));
                Log.d("확인 state[]", String.valueOf(state[1]));
                Log.d("확인 state[]", String.valueOf(state[2]));
                Log.d("확인 state[]", String.valueOf(state[3]));
                Log.d("확인 state[]", String.valueOf(state[4]));

                if(state[0] == true && state[1] == false && state[2] == false && state[3] == false && state[4] == false ){
                    situation= 1;
                }else if(state[0] == false && state[1] == true && state[2] == false && state[3] == false && state[4] == false){
                    situation= 2;
                }else{
                    situation= 0;
                }
            }

            Log.d("에러","out service");
            unbindService(sconn);
            // boolean table 만들어서 정리 할 것

            onInit(0);


        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
