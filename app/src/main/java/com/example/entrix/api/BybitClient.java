package com.example.entrix.api;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BybitClient {

    private static final String TAG = "BybitClient";
    private static final String BASE_URL = "https://api.bybit.com";
    private static final String RECV_WINDOW = "10000";

    private final String apiKey;
    private final String apiSecret;
    private final OkHttpClient client;

    public BybitClient(String apiKey, String apiSecret) {
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;

        this.client = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .build();
    }

    public String placeOrder(
            String symbol,
            String side,
            String qty,
            String price,
            String tp,
            String sl,
            String leverage
    ) throws Exception {

        String endpoint = BASE_URL + "/v5/order/create";
        long timestamp = System.currentTimeMillis();

        JSONObject body = new JSONObject();
        body.put("category", "linear");
        body.put("symbol", symbol);
        body.put("side", side);
        body.put("qty", qty);

        boolean isLimit = price != null && !price.trim().isEmpty();

        if (isLimit) {

            String p = price.trim();

            Log.d(TAG, "LIMIT ORDER PRICE = " + p);

            body.put("orderType", "Limit");
            body.put("price", p);
            body.put("timeInForce", "GTC");

        } else {

            Log.d(TAG, "MARKET ORDER");

            body.put("orderType", "Market");
        }

        if (tp != null && !tp.trim().isEmpty()) {
            body.put("takeProfit", tp.trim());
            body.put("tpTriggerBy", "LastPrice");
        }

        if (sl != null && !sl.trim().isEmpty()) {
            body.put("stopLoss", sl.trim());
            body.put("slTriggerBy", "LastPrice");
        }

        body.put("leverage", leverage);

        String payload = body.toString();

        Log.d(TAG, "PAYLOAD: " + payload);

        String signature = generateSignature(timestamp, payload);

        Request request = new Request.Builder()
                .url(endpoint)
                .addHeader("X-BAPI-API-KEY", apiKey)
                .addHeader("X-BAPI-SIGN", signature)
                .addHeader("X-BAPI-TIMESTAMP", String.valueOf(timestamp))
                .addHeader("X-BAPI-RECV-WINDOW", RECV_WINDOW)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(payload, MediaType.get("application/json; charset=utf-8")))
                .build();

        try (Response response = client.newCall(request).execute()) {

            String result = response.body().string();

            Log.d(TAG, "RESPONSE: " + result);

            JSONObject json = new JSONObject(result);

            String retCode = json.optString("retCode");
            String retMsg = json.optString("retMsg");

            if (!"0".equals(retCode)) {
                throw new Exception("Bybit error: " + retMsg + " (code " + retCode + ")");
            }

            return result;
        }
    }

    public void setLeverage(String symbol, int leverage) throws Exception {

        String endpoint = BASE_URL + "/v5/position/set-leverage";
        long timestamp = System.currentTimeMillis();

        JSONObject body = new JSONObject();
        body.put("category", "linear");
        body.put("symbol", symbol);
        body.put("buyLeverage", String.valueOf(leverage));
        body.put("sellLeverage", String.valueOf(leverage));

        String payload = body.toString();
        String signature = generateSignature(timestamp, payload);

        Request request = new Request.Builder()
                .url(endpoint)
                .addHeader("X-BAPI-API-KEY", apiKey)
                .addHeader("X-BAPI-SIGN", signature)
                .addHeader("X-BAPI-TIMESTAMP", String.valueOf(timestamp))
                .addHeader("X-BAPI-RECV-WINDOW", RECV_WINDOW)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(payload, MediaType.get("application/json")))
                .build();

        try (Response response = client.newCall(request).execute()) {
            Log.d(TAG, "LEVERAGE RESPONSE: " + response.body().string());
        }
    }

    public String calculateQty(String symbol, double usdt, double entryPrice, double leverage) throws Exception {

        JSONObject info = getInstrumentInfo(symbol);

        JSONObject lot = info.getJSONObject("result")
                .getJSONArray("list")
                .getJSONObject(0)
                .getJSONObject("lotSizeFilter");

        double step = lot.getDouble("qtyStep");
        double min = lot.getDouble("minOrderQty");

        double raw = (usdt * leverage) / entryPrice;
        double qty = Math.floor(raw / step) * step;

        if (qty < min) {
            throw new Exception("Qty too small. min=" + min);
        }

        return String.format(java.util.Locale.US, "%.6f", qty)
                .replaceAll("0+$", "")
                .replaceAll("\\.$", "");
    }


    public JSONObject getInstrumentInfo(String symbol) throws Exception {

        String url = BASE_URL + "/v5/market/instruments-info?category=linear&symbol=" + symbol;

        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {
            return new JSONObject(response.body().string());
        }
    }

    public double getCurrentPrice(String symbol) throws Exception {

        String url = BASE_URL + "/v5/market/tickers?category=linear&symbol=" + symbol;

        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {

            JSONObject json = new JSONObject(response.body().string());

            JSONArray list = json.getJSONObject("result").getJSONArray("list");

            return list.getJSONObject(0).getDouble("lastPrice");
        }
    }


    private String generateSignature(long timestamp, String payload) throws Exception {

        String data = timestamp + apiKey + RECV_WINDOW + payload;

        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec keySpec = new SecretKeySpec(apiSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");

        mac.init(keySpec);

        byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));

        StringBuilder sb = new StringBuilder();
        for (byte b : hash) {
            sb.append(String.format("%02x", b));
        }

        return sb.toString();
    }
}