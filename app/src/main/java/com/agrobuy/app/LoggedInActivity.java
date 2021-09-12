package com.agrobuy.app;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.agrobuy.app.databinding.LoggedinLayoutBinding;

public class LoggedInActivity extends AppCompatActivity {
    public LoggedinLayoutBinding loggedinLayout;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loggedinLayout = LoggedinLayoutBinding.inflate(getLayoutInflater());
        setContentView(loggedinLayout.getRoot());
    }
}
