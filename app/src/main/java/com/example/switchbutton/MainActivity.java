package com.example.switchbutton;

import android.graphics.Color;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.library.SwitchButton;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout viewGroup = findViewById(R.id.ll_switch);

        SwitchButton switchButton = new SwitchButton(this);
        switchButton.setLayoutParams(new ViewGroup.LayoutParams(200, 100));
        viewGroup.addView(switchButton);
        switchButton.setOnStatusListener(new SwitchButton.OnStatusListener() {
            @Override
            public void onOpen() {
                Toast.makeText(MainActivity.this, "打开", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onClose() {
                Toast.makeText(MainActivity.this, "关闭", Toast.LENGTH_SHORT).show();
            }
        });
        switchButton.setCircleColor(Color.WHITE);
        switchButton.setCloseBackground(Color.GRAY);
        switchButton.setOpenBackground(Color.MAGENTA);
        switchButton.setInterpolator(SwitchButton.OVER_SHOOT);
        switchButton.open();
    }
}
