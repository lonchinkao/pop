package com.lonchin.pop;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;


public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LinearLayout container = (LinearLayout) findViewById(R.id.container);
        container.addView(new MainView(this));

    }

}
