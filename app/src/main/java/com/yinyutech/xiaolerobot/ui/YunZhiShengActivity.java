package com.yinyutech.xiaolerobot.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.yinyutech.xiaolerobot.R;

/**
 * Created by yinyu-tiejiang on 17-7-10.
 */

public class YunZhiShengActivity extends Activity{

    private Button startLocalTTS, voiceInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yunzhisheng);

        startLocalTTS = (Button)findViewById(R.id.begin_tts);
        voiceInput = (Button)findViewById(R.id.voice_input);
        startLocalTTS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(YunZhiShengActivity.this, TTSOfflineActivity.class);
                startActivity(mIntent);

            }
        });
        voiceInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(YunZhiShengActivity.this, ASROnlineActivity.class);
                startActivity(mIntent);
            }
        });


    }
}
