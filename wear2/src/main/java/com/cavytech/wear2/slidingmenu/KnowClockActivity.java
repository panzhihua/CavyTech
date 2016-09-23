package com.cavytech.wear2.slidingmenu;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cavytech.wear2.R;
import com.cavytech.wear2.activity.KnowClickDetailActivity;
import com.cavytech.wear2.base.AppCompatActivityEx;
import com.cavytech.wear2.entity.ClockBean;
import com.cavytech.wear2.util.Constants;
import com.cavytech.wear2.util.FileUtils;
import com.cavytech.wear2.util.LifeBandBLEUtil;
import com.cavytech.wear2.util.SerializeUtils;
import com.cavytech.widget.SwitchButtonEx;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

public class KnowClockActivity extends AppCompatActivityEx {
    private static final int REQUESTCODE = 1;

    @ViewInject(R.id.back)
    private ImageView back;

    @ViewInject(R.id.go)
    private ImageView go;

    @ViewInject(R.id.title)
    private TextView title;

    @ViewInject(R.id.iv_head)
    private ImageView iv_head;

    @ViewInject(R.id.iv_foot)
    private ImageView iv_foot;

    @ViewInject(R.id.tv_head)
    private TextView tv_head;

    @ViewInject(R.id.ls_know_clock)
    private ListView ls_know_clock;

    @ViewInject(R.id.no_colock_bg)
    private LinearLayout no_colock_bg;

    private List<ClockBean> mListItems = null;/*= new ArrayList<ClockBean> ()*/;

    private ClockBean clockBean;

    private String data;
    private int hour;
    private int minute;
    private boolean isNew;

    private Myadapter mAdapter;

   // private LifeBandBLE mLifeBand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_know_clock);
        x.view().inject(this);

        setToolBar();

        if (FileUtils.isFileExit(Constants.SERIALIZE_CLOCKBEAN)) {
            mListItems = (List<ClockBean>) SerializeUtils.unserialize(Constants.SERIALIZE_CLOCKBEAN);
        } else {

            mListItems = new ArrayList<ClockBean>();
            mListItems.add(new ClockBean(true));

        }

        updateView();
        //判断相应码

        //初始化数据
        //initDatas();

        go.setImageResource(R.drawable.icon_add);
        title.setText(getResources().getString(R.string.know_clock));
        back.setOnClickListener(new MyClockOnClickListener());
        go.setOnClickListener(new MyClockOnClickListener());

       //ls_know_clock.addHeaderView(View.inflate(getApplication(), R.layout.list_head_click, null));
        //ls_know_clock.addFooterView(View.inflate(getApplication(), R.layout.list_foot_click, null));

        mAdapter.notifyDataSetChanged();

        //mLifeBand = BandConnectActivity.getInstance(KnowClockActivity.this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        setSelectDay();

    }

    /**
     * 计算选择的星期转换
     */
    private void setSelectDay() {

        for(int i = 0 ; i<mListItems.size() ; i++){
            Log.e("TAG","mListItems-------KnowClockActivity------"+mListItems.get(i));

            ArrayList<Boolean> weekSet = mListItems.get(i).getWeekSet();
            Log.e("TAG","weekSet-------KnowClockActivity------"+"i"+i+"---------------"+weekSet);

            byte[] b = new byte[8];

            b[0]=0;
            for(int j = 0 ; j< weekSet.size() ;j++){
                if(weekSet.get(j)){
                    b[weekSet.size()-j] = 1;
                }else{
                    b[weekSet.size()-j] = 0;
                }
            }

            for (int k = 0 ; k<b.length ; k++){
                Log.e("TAg","b--------------------------"+b[k]);
            }
            int weekBand;
            if(mListItems.get(i).isNotSelect()){
                weekBand = 127;
            }else{
                //需要的星期对应的转换
                weekBand = b[0]*128+b[1]*64+b[2]*32+b[3]*16+b[4]*8+b[5]*4+b[6]*2+b[7]*1;

            }
            Log.e("tag", "weekBand--------------------"+weekBand);

            int hour = mListItems.get(i).getHour();
            int minute = mListItems.get(i).getMinute();

            //需要的minute
            int minuteBand = hour*60+minute;

            Log.e("tag", "minuteBand--------------------"+minuteBand);

            //需要的开关
            boolean open = mListItems.get(i).isOpen();
            int clockType;
            if(open){
                clockType =2;
            }else{
                clockType =0;
            }

            if(mListItems.get(i).isNotSelect()){
                clockType = 1;
            }
            setBandClock(clockType,minuteBand,weekBand);
        }
    }


    /**
     * 设置手环闹钟
     */
    private void setBandClock(int clockType,int minuteBand,int weekBand) {
        try {
            LifeBandBLEUtil.getInstance().AlarmSetup(clockType, minuteBand, weekBand);
            Log.e("TAG", "clockType----" + clockType + "-------minuteBand-----" + minuteBand + "-----weekBand---" + weekBand);
        }catch(Exception e){
            Toast.makeText(KnowClockActivity.this, getString(R.string.failure_to_set_clock_please_try_again), Toast.LENGTH_SHORT).show();
        }
    }

    public void  updateView(){
        if (mListItems.size() == 0 ) {
            iv_foot.setVisibility(View.GONE);
            iv_head.setVisibility(View.GONE);
            tv_head.setVisibility(View.GONE);
            no_colock_bg.setVisibility(View.VISIBLE);
            ls_know_clock.setAdapter(mAdapter);
            mAdapter = new Myadapter();
        } else {
            no_colock_bg.setVisibility(View.INVISIBLE);
            iv_foot.setVisibility(View.VISIBLE);
            iv_head.setVisibility(View.VISIBLE);
            tv_head.setVisibility(View.VISIBLE);
            mAdapter = new Myadapter();
            ls_know_clock.setAdapter(mAdapter);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        //判断相应码
        if (requestCode == REQUESTCODE && resultCode == RESULT_OK) {
            mListItems = (List<ClockBean>) SerializeUtils.unserialize(Constants.SERIALIZE_CLOCKBEAN);
            updateView();
            ls_know_clock.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        }
    }


    class MyClockOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (v == back) {
                finish();
            } else if (v == go) {
                if(mListItems.size()<=1){
                    Intent intent = new Intent();
                    intent.setClass(KnowClockActivity.this, KnowClickDetailActivity.class);
                    startActivityForResult(intent, REQUESTCODE);//REQUESTCODE定义一个整型做为请求对象标识
                }else{
                    Toast.makeText(KnowClockActivity.this,getString(R.string.at_most_2_clocks_can_be_added),Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    class Myadapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mListItems.size();
        }

        @Override
        public Object getItem(int position) {
            return mListItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(getApplication(), R.layout.know_click_item
                        , null);
                viewHolder = new ViewHolder();

                viewHolder.rl_keow_click = (RelativeLayout) convertView.findViewById(R.id.rl_keow_click);
                viewHolder.tv_click_time_hour = (TextView) convertView.findViewById(R.id.tv_click_time_hour);
                viewHolder.tv_click_time_minute = (TextView) convertView.findViewById(R.id.tv_click_time_minute);
                viewHolder.tv_click_day = (TextView) convertView.findViewById(R.id.tv_click_day);
                viewHolder.sbe_clock_item = (SwitchButtonEx) convertView.findViewById(R.id.sbe_clock_item);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            String hour = mListItems.get(position).getHour() + "";
            String minute = mListItems.get(position).getMinute() + "";
            if (hour.length() == 1) {
                hour = "0" + hour;
            }
            if (minute.length() == 1) {
                minute = "0" + minute;
            }
            viewHolder.tv_click_time_hour.setText(hour);

            viewHolder.tv_click_time_minute.setText(minute);
//            Boolean bBoolean = false;
//            for(int i = 0;i<mListItems.size();i++){
//                Boolean aBoolean = mListItems.get(position).getWeekSet().get(i);
//
//                bBoolean =  aBoolean & bBoolean;
//            }

            if(mListItems.get(position).isNotSelect()){
                viewHolder.tv_click_day.setText(getString(R.string.vibrate_once));
            }else{
                viewHolder.tv_click_day.setText(mListItems.get(position).getWeekCheckString(getString(R.string.everyday),KnowClockActivity.this));
            }

             viewHolder.sbe_clock_item.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if (mListItems.get(position).isOpen() != isChecked) {
                        mListItems.get(position).setOpen(isChecked);
                        SerializeUtils.serialize(mListItems, Constants.SERIALIZE_CLOCKBEAN);
                    }

                    setSelectDay();
                }
            });
            viewHolder.sbe_clock_item.setChecked(mListItems.get(position).isOpen());

            viewHolder.rl_keow_click.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(KnowClockActivity.this, KnowClickDetailActivity.class);
                    intent.putExtra("position", position);
                    startActivityForResult(intent, REQUESTCODE);//REQUESTCODE定义一个整型做为请求对象标识
                 }
            });

            return convertView;
        }
    }

    public static class ViewHolder {
        RelativeLayout rl_keow_click;
        TextView tv_click_time_hour;
        TextView tv_click_time_minute;
        TextView tv_click_day;
        SwitchButtonEx sbe_clock_item;
    }

}
