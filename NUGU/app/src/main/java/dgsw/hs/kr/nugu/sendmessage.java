package dgsw.hs.kr.nugu;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Arrays;

/**
 * Created by minseong on 2017-07-24.
 */

public class sendmessage extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sendmessage);

        Button btn = (Button)findViewById(R.id.Button);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId() == R.id.Button)
                {
                    EditText smsText = (EditText)findViewById(R.id.smsText);
                    String SmsText = smsText.getText().toString();
                    String phoneNum = "01075334191";
                    if(phoneNum.length() > 0  &&  SmsText.length() > 0)
                        sendSMS(phoneNum , SmsText);

                }
            }
        });

    }

    private void sendSMS(String phoneNumber , String Message)
    {
        PendingIntent pi = PendingIntent.getActivity(this , 0 , new Intent(this , sendmessage.class) , 0);
        SmsManager sms = SmsManager.getDefault();

        sms.sendTextMessage(phoneNumber , null , Message , pi , null );
    }
}
