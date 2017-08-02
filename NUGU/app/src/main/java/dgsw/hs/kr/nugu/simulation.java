package dgsw.hs.kr.nugu;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;

public class simulation extends Service {

    public simulation() {
    }

    IBinder mBinder = new MyBinder();

    class MyBinder extends Binder{
        simulation getService(){
            return simulation.this;
        }
    }

    public int onStartCommand(Intent intent , int flags , int startid){
        return super.onStartCommand(intent , flags , startid);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
    //유의어 나중에 api 형식으로 바꿀 것
    public boolean[] CheckString(ArrayList<String> input){
        String [] temp = new String[input.size()];
        boolean [] word = new boolean[5];

        Log.d("에러","service in simulation");
        /*
            1 응급
            2 전화
            3 호흡
            4 기절
            5 CPR : 함수 호출 명령어 최상위 우선순위
            * 키워드 들어오면 true 로 바뀌고 true 로 인식된 단어 들을 파악해 상황에 맞는 함수 시작
         */
        for(int i=0 ; i < word.length;i++)
            word[i] = false;

        for(int i = 0; i < input.size();i++)
        {
            temp[i] = input.get(i);
            temp[i] = temp[i].trim();
        }

        for(String s : temp){
            if(s.contains("응급") ||
                    s.contains("위기") ||
                    s.contains("위험")
                    ) {
                Log.d("확인" , "들어옴");
                word[0] = true;
            }
            if(s.contains("전화") ||
                    s.contains("119") ||
                    s.contains("연락")
                    ){
                word[1] = true;
            }
            if(s.contains("호흡") ||
                    s.contains("숨")
                    ){
                word[2] = true;
            }
            if(s.contains("기절") ||
                    s.contains("의식") ||
                    s.contains("쓰러짐")
            ) {
                word[3] = true;
            }
            if(s.contains("심폐소생술") ||
                    s.contains("소생술") ||
                    s.contains("심폐")   ||
                    s.contains("소생")
                    ) {
                word[4] = true;
            }
        } // end for construction

        // 위치를 기억해 무슨 단어 뒤에 뭐가 오는지를 인식해서 호흡이 없고 , 의식도 없을때 는 따로 인식을하게 만들기
        return word;

    }
}
