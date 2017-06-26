package com.yinyutech.xiaolerobot.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.yinyutech.xiaolerobot.R;

public class MainActivity extends Activity {

    private Button startLocalTTS, voiceInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startLocalTTS = (Button)findViewById(R.id.begin_tts);
        voiceInput = (Button)findViewById(R.id.voice_input);
        startLocalTTS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(MainActivity.this, TTSOfflineActivity.class);
                startActivity(mIntent);

            }
        });
        voiceInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(MainActivity.this, ASROnlineActivity.class);
                startActivity(mIntent);
            }
        });


    }
}
