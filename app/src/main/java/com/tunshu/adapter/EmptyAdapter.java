package com.tunshu.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tunshu.R;

/**
 * 作者：yzb on 2015/7/10 13:26
 */
public class EmptyAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater mInflater;
    private String emptyInfo;


    public EmptyAdapter(Context context, String emptyInfo) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.emptyInfo = emptyInfo;
    }

    @Override
    public Object getItem(int position) {
        return 1;
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.activity_empty_list_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.emptytext.setText(emptyInfo);
        return convertView;
    }

    public class ViewHolder {
        public final TextView emptytext;
        public final View root;

        public ViewHolder(View root) {
            emptytext = (TextView) root.findViewById(R.id.empty_text);
            this.root = root;
        }
    }
}


