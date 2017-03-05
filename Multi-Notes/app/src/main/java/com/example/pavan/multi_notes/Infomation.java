package com.example.pavan.multi_notes;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class Infomation extends AppCompatActivity {
    private TextView version;
    private float versionNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infomation);
        versionNum = (float) 1.0;

        version = (TextView) findViewById(R.id.tv_version);

        version.setText(getString(R.string.versionTV) + String.format("%.1f", versionNum));
    }
}
