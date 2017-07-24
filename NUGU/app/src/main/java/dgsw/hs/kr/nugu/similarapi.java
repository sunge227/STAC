package dgsw.hs.kr.nugu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by minseong on 2017-07-24.
 */

public class similarapi extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simailarapi);

        Button btn = (Button)findViewById(R.id.result);

        btn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                EditText s = (EditText)findViewById(R.id.similar);
                String input = s.getText().toString();
                Similar Si = new Similar();
                String result =Si.checkString(input);
                TextView text = (TextView)findViewById(R.id.end);
                text.setText(result);
            }
        });

    }
}

class Similar{
    public Similar()
    {
    }

    public String checkString(String input) // 유의어를 걸러줌으로 정확도를 올림
    {
        String print=null;
        input = input.trim();
        if(input.equals("warning") ||
                input.equals("caution") ||
                input.equals("caveat")
                ) {
            print = "warning";
        }
        if(input.equals("knife")||
                input.equals("sword")
                )
        {
            print="knife";
        }
        if(input.equals("ok")||
                input.equals("yes")||
                input.equals("indeed")||
                input.equals("ofcourse")
                )
        {
            print="ok";
        }

        if(print!=null)
            return print;
        else
            return input;
    }
}
