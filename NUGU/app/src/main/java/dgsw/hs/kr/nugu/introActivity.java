package dgsw.hs.kr.nugu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

/**
 * Created by minseong on 2017-08-02.
 */

public class introActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(introActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2000);
    }
}
