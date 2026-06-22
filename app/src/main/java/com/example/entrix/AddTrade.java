package com.example.entrix;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import com.example.entrix.BuildConfig;

import com.example.entrix.api.BybitClient;

import org.json.JSONObject;

import java.util.ArrayList;

public class AddTrade extends AppCompatActivity {

    private AutoCompleteTextView coinDropdown;
    private RadioGroup directionGroup;
    private EditText entryInput, tpInput, slInput, usdtInput, leverageInput;
    private Button addTradeButton;

    private CryptoAddList cryptoAddList = new CryptoAddList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addtrade);

        coinDropdown = findViewById(R.id.coinDropdown);
        directionGroup = findViewById(R.id.directionGroup);
        entryInput = findViewById(R.id.entryInput);
        tpInput = findViewById(R.id.tpInput);
        slInput = findViewById(R.id.slInput);
        usdtInput = findViewById(R.id.usdtInput);
        leverageInput = findViewById(R.id.leverageInput);
        addTradeButton = findViewById(R.id.addTradeButton);

        setupCoins();
        addTradeButton.setOnClickListener(v -> confirmTrade());

        // ТЕСТ API КЛЮЧА ПРИ ЗАПУСКЕ
        testApiKey();
    }

    private void testApiKey() {
        new Thread(() -> {
            try {


                Log.d("AddTrade", "API client created");

                runOnUiThread(() ->
                        Toast.makeText(
                                AddTrade.this,
                                "API client initialized",
                                Toast.LENGTH_LONG
                        ).show()
                );

            } catch (Exception e) {

                Log.e("AddTrade", "API test error", e);

                runOnUiThread(() ->
                        Toast.makeText(
                                AddTrade.this,
                                "❌ Ошибка теста: " + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show()
                );
            }
        }).start();
    }
    private void setupCoins() {
        ArrayList<String> coins = new ArrayList<>();
        cryptoAddList.CoinsAdd(coins);
//        coins.add("BTCUSDT");
//        coins.add("ETHUSDT");
//        coins.add("SOLUSDT");
//        coins.add("AAVEUSDT");
//        coins.add("AVAXUSDT");
//        coins.add("PNUTUSDT");
//        coins.add("IMXUSDT");
//        coins.add("FARTCOINUSDT");
//        coins.add("SUIUSDT");
//        coins.add("XRPUSDT");
//        coins.add("NEARUSDT");
//        coins.add("HYPEUSDT");
//        coins.add("LINKUSDT");
//        coins.add("1000PEPEUSDT");
//        coins.add("POPCATUSDT");
//        coins.add("DOGEUSDT");
//        coins.add("SEIUSDT");


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_dropdown_item_1line, coins) {
            @Override
            public android.view.View getView(int position, android.view.View convertView, android.view.ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                String coin = getItem(position);

                if (coin != null) {
                    Drawable drawable = ContextCompat.getDrawable(AddTrade.this, getIcon(coin));
                    if (drawable != null) {
                        drawable.setBounds(0, 0, 60, 60);
                        textView.setCompoundDrawables(drawable, null, null, null);
                        textView.setCompoundDrawablePadding(20);
                    }
                    textView.setText(coin);
                }
                return textView;
            }
        };

        coinDropdown.setAdapter(adapter);
    }

    private String getDirection() {
        int id = directionGroup.getCheckedRadioButtonId();
        return (id == R.id.longBtn) ? "Long" : "Short";
    }

    private String getSide(String direction) {
        return direction.equals("Long") ? "Buy" : "Sell";
    }

    private void confirmTrade() {
        String coin = coinDropdown.getText().toString().trim();
        if (coin.isEmpty()) {
            Toast.makeText(this, "Выберите монету", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            String entryStr = entryInput.getText().toString().trim().replace(',', '.');
            String usdtStr = usdtInput.getText().toString().trim().replace(',', '.');
            String leverageStr = leverageInput.getText().toString().trim().replace(',', '.');
            String tpStr = tpInput.getText().toString().trim().replace(',', '.');
            String slStr = slInput.getText().toString().trim().replace(',', '.');

            if (entryStr.isEmpty() || usdtStr.isEmpty() || leverageStr.isEmpty()) {
                Toast.makeText(this, "Заполните Entry, USDT и Leverage", Toast.LENGTH_SHORT).show();
                return;
            }

            final double entry = Double.parseDouble(entryStr);
            final double usdt = Double.parseDouble(usdtStr);
            final double leverage = Double.parseDouble(leverageStr);

            if (entry <= 0 || usdt <= 0 || leverage <= 0) {
                Toast.makeText(this, "Все значения должны быть > 0", Toast.LENGTH_SHORT).show();
                return;
            }

            String direction = getDirection();
            String side = getSide(direction);

            // Логика в отдельном потоке
            new Thread(() -> {
                try {
                    BybitClient client = new BybitClient(
                            BuildConfig.API_KEY,
                            BuildConfig.API_SELECT
                    );
                    // Получаем текущую цену
                    double currentPrice = client.getCurrentPrice(coin);
                    Log.d("AddTrade", "Current market price: " + currentPrice);

                    // СТРОГАЯ ПРОВЕРКА — чтобы ордер был настоящим лимитом
                    if ("Buy".equals(side) && entry >= currentPrice) {
                        runOnUiThread(() -> Toast.makeText(this,
                                "❌ Long: Entry должен быть НИЖЕ текущей цены (" + currentPrice + ")",
                                Toast.LENGTH_LONG).show());
                        return;
                    }
                    if ("Sell".equals(side) && entry <= currentPrice) {
                        runOnUiThread(() -> Toast.makeText(this,
                                "❌ Short: Entry должен быть ВЫШЕ текущей цены (" + currentPrice + ")",
                                Toast.LENGTH_LONG).show());
                        return;
                    }

                    // Устанавливаем леверидж
                    client.setLeverage(coin, (int) leverage);

                    // Рассчитываем количество
                    String calculatedQty = client.calculateQty(coin, usdt, entry, leverage);
                    Log.d("AddTrade", "✅ Calculated qty: " + calculatedQty);

                    // Выставляем ордер
                    String result = client.placeOrder(
                            coin,
                            side,
                            calculatedQty,
                            String.valueOf(entry),
                            tpStr.isEmpty() ? null : tpStr,
                            slStr.isEmpty() ? null : slStr,
                            String.valueOf(leverage)
                    );

                    Log.d("AddTrade", "✅ Order placed: " + result);

                    runOnUiThread(() -> Toast.makeText(this,
                            "✅ Лимитный ордер (PostOnly) успешно выставлен!",
                            Toast.LENGTH_LONG).show());

                } catch (Exception e) {
                    Log.e("AddTrade", "❌ Trade error", e);
                    runOnUiThread(() -> Toast.makeText(this,
                            "❌ " + e.getMessage(),
                            Toast.LENGTH_LONG).show());
                }
            }).start();

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Ошибка: Введите корректные числа", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "Ошибка ввода: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private int getIcon(String coin) {
        return cryptoAddList.getCoinIcon(coin);
    }
}