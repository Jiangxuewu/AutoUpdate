package com.transsnet.autoupdate;

import android.app.Activity;
import android.os.Bundle;
import android.os.Process;
import android.widget.TextView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((TextView) findViewById(R.id.hello)).setText("Hello");
//        ((TextView) findViewById(R.id.hello)).setText("Hello World");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Process.killProcess(Process.myPid());
    }
}
