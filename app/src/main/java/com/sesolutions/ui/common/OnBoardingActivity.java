package com.sesolutions.ui.common;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import com.sesolutions.R;
import com.sesolutions.ui.welcome.WelcomeActivity;

public class OnBoardingActivity extends BaseActivity {

    AppCompatButton btn_next;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout_onboarding);
        init();
    }

    void init() {
        btn_next = findViewById(R.id.btn_permission_yes);
        btn_next.setOnClickListener(v -> {
            Intent loginIntent = new Intent(OnBoardingActivity.this, WelcomeActivity.class);
            startActivity(loginIntent);
        });

    }
}
