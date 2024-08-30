package com.swift.swiftcourier;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

public class OrdersAdapter extends BaseAdapter {
    private Context context;
    private List<JSONObject> orders;

    public OrdersAdapter(Context context, List<JSONObject> orders) {
        this.context = context;
        this.orders = orders;
    }

    @Override
    public int getCount() {
        return orders.size();
    }

    @Override
    public Object getItem(int position) {
        return orders.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.order_item, parent, false);
        }

        TextView tvOrderId = convertView.findViewById(R.id.tvOrderId);
        TextView tvOrderFrom = convertView.findViewById(R.id.tvOrderFrom);
        TextView tvOrderTo = convertView.findViewById(R.id.tvOrderTo);
        TextView tvOrderStatus = convertView.findViewById(R.id.tvOrderStatus);

        JSONObject order = orders.get(position);

        try {
            tvOrderId.setText("Order ID: " + order.getString("order_id"));
            tvOrderFrom.setText("From: " + order.getString("order_from"));
            tvOrderTo.setText("To: " + order.getString("order_to"));
            tvOrderStatus.setText("Status: " + order.getString("status"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return convertView;
    }
}
