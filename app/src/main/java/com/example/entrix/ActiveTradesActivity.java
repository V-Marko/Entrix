package com.example.entrix;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.entrix.api.BybitClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ActiveTradesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TradeAdapter adapter;
    private ArrayList<TradeModel> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_trade_active);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new TradeAdapter(list, new TradeAdapter.TradeAction() {
            @Override
            public void onDelete(TradeModel trade) {
                cancelTrade(trade);
            }

            @Override
            public void onEdit(TradeModel trade) {
                showEditDialog(trade);
            }
        });

        recyclerView.setAdapter(adapter);

        loadTrades();
    }
    private void showEditDialog(TradeModel trade) {

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Edit Order");

        android.view.View view = getLayoutInflater().inflate(R.layout.dialog_edit_trade, null);

        EditText qtyInput = view.findViewById(R.id.editQty);
        EditText priceInput = view.findViewById(R.id.editPrice);

        qtyInput.setText(trade.qty);
        priceInput.setText(trade.price);

        builder.setView(view);

        builder.setPositiveButton("Save", (dialog, which) -> {

            String newQty = qtyInput.getText().toString().trim();
            String newPrice = priceInput.getText().toString().trim();

            editTrade(trade, newQty, newPrice);
        });

        builder.setNegativeButton("Cancel", null);

        builder.show();
    }

    private void editTrade(TradeModel oldTrade, String newQty, String newPrice) {

        new Thread(() -> {
            try {

                BybitClient client = new BybitClient(
                        BuildConfig.API_KEY,
                        BuildConfig.API_SELECT
                );

                client.cancelOrder(oldTrade.symbol, oldTrade.orderId);

                String result = client.placeOrder(
                        oldTrade.symbol,
                        oldTrade.side,
                        newQty,
                        newPrice,
                        null,
                        null,
                        "1"
                );

                runOnUiThread(() -> {
                    Toast.makeText(this,
                            "Order edited",
                            Toast.LENGTH_SHORT).show();

                    loadTrades();
                });

            } catch (Exception e) {

                Log.e("EDIT", "error", e);

                runOnUiThread(() ->
                        Toast.makeText(this,
                                "Edit error: " + e.getMessage(),
                                Toast.LENGTH_LONG).show()
                );
            }
        }).start();
    }
    private void loadTrades() {

        new Thread(() -> {
            try {
                BybitClient client = new BybitClient(
                        BuildConfig.API_KEY,
                        BuildConfig.API_SELECT
                );

                JSONArray orders = client.getActiveOrders();

                list.clear();

                if (orders == null || orders.length() == 0) {
                    runOnUiThread(() ->
                            Toast.makeText(this,
                                    "Нет активных ордеров",
                                    Toast.LENGTH_SHORT).show()
                    );
                    return;
                }

                for (int i = 0; i < orders.length(); i++) {

                    JSONObject o = orders.getJSONObject(i);

                    TradeModel t = new TradeModel();
                    t.symbol = o.optString("symbol");
                    t.side = o.optString("side");
                    t.qty = o.optString("qty");
                    t.price = o.optString("price");
                    t.orderId = o.optString("orderId");

                    list.add(t);
                }

                runOnUiThread(() -> adapter.notifyDataSetChanged());

            } catch (Exception e) {
                Log.e("ActiveTrades", "load error", e);

                runOnUiThread(() ->
                        Toast.makeText(this,
                                "Ошибка загрузки: " + e.getMessage(),
                                Toast.LENGTH_LONG).show()
                );
            }
        }).start();
    }


    private void cancelTrade(TradeModel trade) {

        new Thread(() -> {
            try {
                BybitClient client = new BybitClient(
                        BuildConfig.API_KEY,
                        BuildConfig.API_SELECT
                );

                client.cancelOrder(trade.symbol, trade.orderId);

                runOnUiThread(() -> {
                    Toast.makeText(this,
                            "Ордер отменён",
                            Toast.LENGTH_SHORT).show();

                    loadTrades();
                });

            } catch (Exception e) {
                Log.e("ActiveTrades", "cancel error", e);

                runOnUiThread(() ->
                        Toast.makeText(this,
                                "Ошибка отмены: " + e.getMessage(),
                                Toast.LENGTH_LONG).show()
                );
            }
        }).start();
    }
}