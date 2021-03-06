package dgsw.hs.kr.nugu;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by minseong on 2017-08-02.
 */

public class ContactActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        Button btn =  (Button)findViewById(R.id.backbutton_contact);
        Button btn2 = (Button)findViewById(R.id.addbutton_contact);
        Button btn3 = (Button)findViewById(R.id.rembutton_contact);

        //돌아가기 버튼
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //추가 버튼
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "앙 추가했디", Toast.LENGTH_SHORT).show();
            }
        });

        //제거 버튼
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "앙 제거했디", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override //뒤로가기 버튼
    public void onBackPressed() {

        super.onBackPressed();
        finish();
    }
}
