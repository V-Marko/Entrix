package com.example.entrix;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;



public class TradeAdapter extends RecyclerView.Adapter<TradeAdapter.VH> {

    public interface TradeAction {
        void onDelete(TradeModel trade);
        void onEdit(TradeModel trade);
    }

    private final ArrayList<TradeModel> list;
    private final TradeAction action;

    public TradeAdapter(ArrayList<TradeModel> list, TradeAction action) {
        this.list = list;
        this.action = action;
    }

    static class VH extends RecyclerView.ViewHolder {

        TextView symbol, side, qty, price, orderId;
        Button deleteBtn, editBtn;

        VH(View v) {
            super(v);
            symbol = v.findViewById(R.id.symbolText);
            side = v.findViewById(R.id.sideText);
            qty = v.findViewById(R.id.qtyText);
            price = v.findViewById(R.id.priceText);
            orderId = v.findViewById(R.id.orderIdText);

            deleteBtn = v.findViewById(R.id.deleteBtn);
            editBtn = v.findViewById(R.id.editBtn);
        }
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_trade, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(VH h, int pos) {
        TradeModel t = list.get(pos);

        h.symbol.setText(t.symbol);
        h.side.setText(t.side);
        h.qty.setText("Qty: " + t.qty);
        h.price.setText("Price: " + t.price);
        h.orderId.setText("ID: " + t.orderId);

        h.deleteBtn.setOnClickListener(v -> action.onDelete(t));
        h.editBtn.setOnClickListener(v -> action.onEdit(t));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}