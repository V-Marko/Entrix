package com.example.entrix;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
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
            Intent intent = new Intent(MainActivity.this, ActiveTradesActivity.class);
            startActivity(intent);
        });
        tradeHistoryBtn.setOnClickListener(v-> {
            Log.i("btn", "trade history btn click");
        });

    }
}