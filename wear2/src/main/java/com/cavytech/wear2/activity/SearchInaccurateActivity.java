package com.cavytech.wear2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cavytech.wear2.R;
import com.cavytech.wear2.base.AppCompatActivityEx;
import com.cavytech.wear2.entity.DBfriendBean;
import com.cavytech.wear2.util.PinYinUtils;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * 搜索好友
 */
public class SearchInaccurateActivity extends AppCompatActivityEx {
    @ViewInject(R.id.lv_search_inaccurate)
    private ListView lv_search_inaccurate;

    @ViewInject(R.id.back)
    private ImageView back;

    @ViewInject(R.id.searchView)
    private EditText searchView;
    private List<DBfriendBean> ll;
    private LinearLayout ll_recommend_add;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_inaccurate);
        x.view().inject(this);

        initview();

        setToolBar();

        initlistener();

    }

    private void initlistener() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        lv_search_inaccurate.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(SearchInaccurateActivity.this,"你点击了----"+ll.get(position).getNickname(),Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SearchInaccurateActivity.this,SingleSetailsActivity.class);
                intent.putExtra("fid",ll.get(position).getUserId());
                intent.putExtra("name",ll.get(position).getNickname());
                startActivity(intent);
            }
        });
    }

    private void initview() {

        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ll= PinYinUtils.searchFriend(s.toString());
//                for(int i=0;i<ll.size();i++){
//                    Log.e("TAG","search-----"+ll.get(i).getNickname());
//                }
                if(ll!=null){
                    lv_search_inaccurate.setAdapter(new SearchAdapter());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    class SearchAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return ll.size();
        }

        @Override
        public Object getItem(int position) {
            return ll.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                convertView = View.inflate(SearchInaccurateActivity.this,R.layout.recommend_item_list,null);
                ll_recommend_add = (LinearLayout)convertView.findViewById(R.id.ll_recommend_add);
                ll_recommend_add.setVisibility(View.GONE);
                new ViewHolder(convertView);

            }
            ViewHolder holder = (ViewHolder) convertView.getTag();
            holder.tv_recommend_name.setText(ll.get(position).getNickname());

            return convertView;
        }
    }

    class ViewHolder{

        LinearLayout ll_recommend_add;
        TextView tv_recommend_name;
        ImageView iv_recommend_icon;
        RelativeLayout rl_swipe_item;

        public ViewHolder(View view) {
            rl_swipe_item = (RelativeLayout) view.findViewById(R.id.rl_swipe_item);
            iv_recommend_icon = (ImageView)view.findViewById(R.id.iv_recommend_icon);
            tv_recommend_name = (TextView)view.findViewById(R.id.tv_recommend_name);
            ll_recommend_add = (LinearLayout)view.findViewById(R.id.ll_recommend_add);
            view.setTag(this);
        }

    }
}
