package com.cavytech.wear2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.basecore.util.core.MobileFormat;
import com.basecore.util.core.StringUtils;
import com.basecore.widget.CustomToast;
import com.cavytech.wear2.R;
import com.cavytech.wear2.base.AppCompatActivityEx;
import com.cavytech.wear2.entity.FriendBean;
import com.cavytech.wear2.http.HttpUtils;
import com.cavytech.wear2.http.RequestCallback;
import com.cavytech.wear2.util.CacheUtils;
import com.cavytech.wear2.util.CircleTransform;
import com.cavytech.wear2.util.Constants;
import com.google.gson.Gson;
import com.squareup.okhttp.Request;
import com.squareup.picasso.Picasso;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

public class ContaxtSearchActivity extends AppCompatActivityEx {
    @ViewInject(R.id.back)
    private ImageView back;

    @ViewInject(R.id.lv_search_accurate)
    private ListView lv_search_accurate;

    @ViewInject(R.id.searchView)
    private EditText searchView;
    private String userid;
    private String search;
    private Gson gson;
    private List<FriendBean.FriendInfosBean> searchlist;

    private ContaxAdapter adapterContax = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contaxt_search);
        x.view().inject(this);
        setToolBar();
        gson = new Gson();

        userid = CacheUtils.getString(this, Constants.USERID);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

//
        searchView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_ENTER ){
                    search = searchView.getText().toString();
                    if (!(new MobileFormat(search.trim()).isLawful() || StringUtils.isEmail(search))) {
                        CustomToast.showToast(ContaxtSearchActivity.this, R.string.search_error);
                    }else{
                        getdatafromnet();
                    }
                    return true;
                }
                return false;
            }
        });

        lv_search_accurate.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(ContaxtSearchActivity.this,"你点击了"+searchlist.get(position).getNickname(),Toast.LENGTH_SHORT).show();

                startActivity(new Intent(ContaxtSearchActivity.this,SingleSetailsMoShengActivity.class));

            }
        });





    }
    private void getdatafromnet() {

        HttpUtils.getInstance().searchFriend(search,
                new RequestCallback<FriendBean>() {
                    @Override
                    public void onError(Request request, Exception e) {

                    }

                    @Override
                    public void onResponse(FriendBean response) {
                        searchlist = response.getFriendInfos();

                        if(searchlist!=null){
                            if(adapterContax == null){
                                adapterContax = new ContaxAdapter();
                                lv_search_accurate.setAdapter(adapterContax);
                            }else{
                                adapterContax.notifyDataSetChanged();
                            }
                        }


                     }
                });
    }

    class ContaxAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return searchlist.size();
        }

        @Override
        public Object getItem(int position) {
            return searchlist.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if(convertView == null){
                convertView = View.inflate(ContaxtSearchActivity.this,R.layout.recommend_item_list,null);
                viewHolder = new ViewHolder();
                viewHolder.iv_recommend_icon = (ImageView) convertView.findViewById(R.id.iv_recommend_icon);
                viewHolder.tv_recommend_name = (TextView) convertView.findViewById(R.id.tv_recommend_name);
                viewHolder.ll_recommend_add = (LinearLayout) convertView.findViewById(R.id.ll_recommend_add);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.tv_recommend_name.setText(searchlist.get(position).getNickname());

            if (!"".equals(searchlist.get(position).getAvatarUrl())) {

                Picasso.with(ContaxtSearchActivity.this)
                        .load(searchlist.get(position).getAvatarUrl())
                        .transform(new CircleTransform())
                        .placeholder(R.drawable.head_boy_normal)
                        .error(R.drawable.head_boy_normal)
                        .into(viewHolder.iv_recommend_icon);
            }

//            viewHolder.tv_recommend_name.setText(rblist.get(position).getNickname());
//            viewHolder.tv_recommend_name.setText(rblist.get(position).getNickname());

            viewHolder.ll_recommend_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ContaxtSearchActivity.this,VerficatonActivity.class);
                    intent.putExtra("userid",userid);
                    intent.putExtra("friendId",searchlist.get(position).getUserId());
                    startActivity(intent);
                }
            });

            return convertView;
        }
    }

    public static class ViewHolder{
        ImageView iv_recommend_icon;
        TextView tv_recommend_name;
        LinearLayout ll_recommend_add;
    }
}
