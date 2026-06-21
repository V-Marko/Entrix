package com.example.entrix;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class AddTrade extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_YES);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addtrade);

        AutoCompleteTextView coinDropdown = findViewById(R.id.coinDropdown);

        ArrayList<String> coins = new ArrayList<>();
        coins.add("BTC");
        coins.add("ETH");
        coins.add("SOL");
        coins.add("HYPE");
        coins.add("AAVE");
        coins.add("AVAX");
        coins.add("PNUT");
        coins.add("IMX");
        coins.add("FARTCOIN");
        coins.add("SUI");
        coins.add("XRP");
        coins.add("NEAR");
        coins.add("LINK");
        coins.add("1000PEPE");
        coins.add("POPCAT");
        coins.add("DOGE");
        coins.add("SEI");




        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                coins
        ) {

            @Override
            public android.view.View getView(int position,
                                             android.view.View convertView,
                                             android.view.ViewGroup parent) {

                android.widget.TextView textView =
                        (android.widget.TextView) super.getView(position, convertView, parent);

                String coin = getItem(position);

                int icon = getIcon(coin);

                Drawable drawable = ContextCompat.getDrawable(AddTrade.this, icon);

                if (drawable != null) {
                    drawable.setBounds(0, 0, 60, 60);
                    textView.setCompoundDrawables(drawable, null, null, null);
                    textView.setCompoundDrawablePadding(20);
                }

                textView.setText(coin);

                return textView;
            }
        };

        coinDropdown.setAdapter(adapter);
    }

    private int getIcon(String coin) {
        switch (coin) {

            case "BTC": return R.drawable.btc;
            case "ETH": return R.drawable.eth;
            case "SOL": return R.drawable.sol;
            case "HYPE": return R.drawable.hype;
            case "AAVE": return R.drawable.aave;
            case "AVAX": return R.drawable.avax;
            case "PNUT": return R.drawable.pnut;
            case "IMX": return R.drawable.imx;
            case "FARTCOIN": return R.drawable.fartcoin;
            case "SUI": return R.drawable.sui;
            case "XRP": return R.drawable.xrp;
            case "NEAR": return R.drawable.near;
            case "LINK": return R.drawable.link;
            case "1000PEPE": return R.drawable.pepe;
            case "POPCAT": return R.drawable.popcat;
            case "DOGE": return R.drawable.doge;
            case "SEI": return R.drawable.sei;

            default: return R.drawable.btc;
        }
    }
}