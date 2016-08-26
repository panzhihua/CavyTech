package com.cavytech.wear2.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cavytech.wear2.R;
import com.cavytech.wear2.entity.HelpEntity;

import java.util.List;

/**
 * Created by longjining on 16/5/17.
 */
public class HelpAdapter extends BaseRecyclerAdapter<HelpEntity.DataBean> {

    public HelpAdapter(List<HelpEntity.DataBean> helpTitleList) {
        super();

        addDatas(helpTitleList);
    }

    @Override
    public RecyclerView.ViewHolder onCreate(ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_help_item, parent, false);

        return new HelpInfoViewHolder(layout);
    }

    @Override
    public void onBind(RecyclerView.ViewHolder viewHolder, int RealPosition, HelpEntity.DataBean data) {
        ((HelpInfoViewHolder)viewHolder).helpTitle.setText(data.getTitle().toString());
    }

    public class HelpInfoViewHolder extends BaseRecyclerAdapter.Holder {

        public TextView helpTitle;

        public HelpInfoViewHolder(View itemView) {
            super(itemView);

            helpTitle  = (TextView)itemView.findViewById(R.id.help_title);
        }
    }
}
