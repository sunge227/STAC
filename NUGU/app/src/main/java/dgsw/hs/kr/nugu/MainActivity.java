package dgsw.hs.kr.nugu;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView mouse = (ImageView)findViewById(R.id.click);

        mouse.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                if(v.getId() ==R.id.click){
                    Intent intent = new Intent(MainActivity.this , GPStrans.class);
                    startActivity(intent);
                }
            }
        });
    }
}
