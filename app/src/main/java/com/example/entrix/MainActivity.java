package com.example.entrix;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.appcompat.app.AppCompatDelegate;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_YES);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout addTradeBtn = findViewById(R.id.AddTrade);
        LinearLayout activeTradeBtn = findViewById(R.id.ActiveTrades);
        LinearLayout tradeHistoryBtn = findViewById(R.id.TradeHistory);


        addTradeBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddTrade.class);
            startActivity(intent);
        });
        activeTradeBtn.setOnClickListener(v->{
            Log.i("btn", "active trade btn click");
        });
        tradeHistoryBtn.setOnClickListener(v-> {
            Log.i("btn", "trade history btn click");
        });

    }
}