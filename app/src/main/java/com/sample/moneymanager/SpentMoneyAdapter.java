package com.sample.moneymanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class SpentMoneyAdapter extends BaseAdapter {

    Context context;

    ArrayList<SpentMoneyItem> items = new ArrayList<>();

    public SpentMoneyAdapter(Context c) {
        this.context = c;
    }

    public void addItem(SpentMoneyItem item) {
        items.add(item);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v;
        SpentMoneyItem item = items.get(position);
        if(item.isHeader()) {
            v = inflater.inflate(R.layout.spent_money_header, null);
            TextView textView = (TextView) v.findViewById(R.id.list_money_header);
            textView.setText(item.getText());
            v.setEnabled(false);
            v.setOnClickListener(null);
        } else {
            v = inflater.inflate(R.layout.spent_money_item, null);
            TextView textViewText = (TextView) v.findViewById(R.id.list_money_item_text);
            TextView textViewSpent = (TextView) v.findViewById(R.id.list_money_item_value);
            textViewText.setText(item.getText());
            textViewSpent.setText("" + item.getValue() + "$");
        }
        return v;
    }

    public void clear() {
        items.clear();
    }
}
