package com.cavytech.wear2.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cavytech.wear2.R;
import com.cavytech.wear2.entity.UserInfoKVEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by longjining on 16/4/11.
 */

public class UserInfoAdapter extends BaseRecyclerAdapter<UserInfoKVEntity> {

    public UserInfoAdapter(ArrayList<UserInfoKVEntity> userinfotList) {
        super();

        addDatas(userinfotList);
    }

    @Override
    public RecyclerView.ViewHolder onCreate(ViewGroup parent, int viewType) {

        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.userinfo_item, parent, false);

        return new UserInfoViewHolder(layout);
    }

    @Override
    public void onBind(RecyclerView.ViewHolder viewHolder, int RealPosition, UserInfoKVEntity data) {
        ((UserInfoViewHolder)viewHolder).itemKeyText.setText(data.getKey());
        ((UserInfoViewHolder)viewHolder).itemValueText.setText(data.getValue());
    }

    public class UserInfoViewHolder extends BaseRecyclerAdapter.Holder {

        public TextView itemKeyText;
        public TextView itemValueText;

        public UserInfoViewHolder(View itemView) {
            super(itemView);

            itemKeyText  = (TextView)itemView.findViewById(R.id.key_text);
            itemValueText  = (TextView)itemView.findViewById(R.id.value_text);
        }
    }
}
