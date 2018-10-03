package com.example.getfood;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class MenuDisplayAdapter extends BaseAdapter {

    ArrayList<String> itemName,itemPrice;
    Context context;
    LayoutInflater inflater;

    TextView itemNameTextView, itemPriceTextView;

    public MenuDisplayAdapter(ArrayList<String> itemName, ArrayList<String> itemPrice, Context context) {
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.context = context;
        this.inflater = (LayoutInflater) LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return itemName.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        View vi = view;
        vi = inflater.inflate(R.layout.food_menu_display_custom_listview, null);
        itemNameTextView = vi.findViewById(R.id.itemNameTextView);
        itemPriceTextView = vi.findViewById(R.id.itemPriceTextView);

        itemNameTextView.setText(itemName.get(i));
        itemPriceTextView.setText("Price: Rs. "+itemPrice.get(i));

        return vi;
    }
}