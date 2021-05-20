package com.nekoneko.nekonekodemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }


    @OnClick({R.id.processView, R.id.swipeMenuView})
    @Override
    public void onClick(View view) {
        Intent intent = new Intent(this, DemoActivity.class);
        intent.putExtra("action", view.getId());
        startActivity(intent);
    }
}
