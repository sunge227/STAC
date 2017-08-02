package dgsw.hs.kr.nugu;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;

public class message extends Service {

    IBinder mBinder = new MyBinder();

    class MyBinder extends Binder {
        message getService(){
            return message.this;
        }
    }

    public int onStartCommand(Intent intent , int flags , int startid){
        sendMessage();
        return super.onStartCommand(intent , flags , startid);
    }

    public message() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void sendMessage() {

        Log.d("에러","service in message");
        String [] Number = {"01075334191"};
        String Text = "GPS  , 상황 , 날짜";
        for(int i = 0 ; i < Number.length ; i++){
            sendSMS(Number[i] , Text);
        }
        // db 에서 전화번호 불러옴
    }
    private void sendSMS(String phoneNumber , String Message)
    {
        PendingIntent pi = PendingIntent.getActivity(this , 0 , new Intent(this , message.class) , 0);
        SmsManager sms = SmsManager.getDefault();

        sms.sendTextMessage(phoneNumber , null , Message , pi , null );
    }
}
