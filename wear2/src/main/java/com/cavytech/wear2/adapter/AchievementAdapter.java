package com.cavytech.wear2.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.TextView;

import com.cavytech.wear2.R;
import com.cavytech.wear2.entity.UserEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by longjining on 16/4/11.
 */
public class AchievementAdapter extends BaseRecyclerAdapter<UserEntity.ProfileEntity.AwardsEntity> {
    private List<UserEntity.ProfileEntity.AwardsEntity> achievementList;
    private int imgWidth;  // 图片的高度

    public AchievementAdapter(List<UserEntity.ProfileEntity.AwardsEntity> achievementList, int imgWidth) {
        super();
        this.achievementList = achievementList;
        this.imgWidth = imgWidth;

        addDatas((ArrayList) this.achievementList);
    }

    @Override
    public RecyclerView.ViewHolder onCreate(ViewGroup parent, int viewType) {

        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.achievement_item, parent, false);

        LayoutParams lp = layout.getLayoutParams();
        return new AchViewHolder(layout);
    }

    @Override
    public void onBind(RecyclerView.ViewHolder viewHolder, int RealPosition, UserEntity.ProfileEntity.AwardsEntity data) {
        ((AchViewHolder)viewHolder).itemText.setText(achievementList.get(RealPosition).getNumber());

    }

    public class AchViewHolder extends BaseRecyclerAdapter.Holder {

        public ImageButton itemImage;
        public TextView itemText;

        public AchViewHolder(View itemView) {
            super(itemView);

            itemImage = (ImageButton)itemView.findViewById(R.id.achievement_img);
            LayoutParams lp = itemImage.getLayoutParams();
            lp.height = lp.width = imgWidth;

            itemImage.setLayoutParams(lp);

            itemText  = (TextView)itemView.findViewById(R.id.achievement_text);
        }
    }
}
