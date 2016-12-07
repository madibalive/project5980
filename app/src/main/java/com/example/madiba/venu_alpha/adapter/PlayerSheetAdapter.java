package com.example.madiba.venu_alpha.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Madiba on 9/29/2016.
 */

public class PlayerSheetAdapter extends BaseAdapter {
    private List<ParseObject> mdatas = new ArrayList<>();
    private Context context;

    public PlayerSheetAdapter(Context context, List<ParseObject> tarefas) {
        this.context = context;
        this.mdatas = tarefas;
    }

    @Override
    public int getCount() {
        return mdatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mdatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(android.R.layout.simple_list_item_multiple_choice, parent, false);

            viewHolder = new ViewHolder(convertView);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ParseUser user = mdatas.get(position).getParseUser("from");

        if (user != null) {
            String name = user.getUsername();
            viewHolder.name.setText(name);
        }
        return convertView;
    }

    public static class ViewHolder {

        public final TextView name;

        public ViewHolder(View view) {


            name = (TextView)
                    view.findViewById(android.R.id.text1);


        }

    }
}
