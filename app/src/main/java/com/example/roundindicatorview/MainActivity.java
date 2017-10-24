package com.example.roundindicatorview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    EditText edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final RoundIndicatorView roundView = (RoundIndicatorView) findViewById(R.id.round);
        edit = (EditText) findViewById(R.id.num);
        Button button = (Button) findViewById(R.id.confirm);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                roundView.setCurrentNumAnim(Integer.parseInt(edit.getText().toString()));
            }
        });
    }
}
