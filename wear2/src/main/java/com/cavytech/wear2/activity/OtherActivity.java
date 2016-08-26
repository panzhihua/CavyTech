package com.cavytech.wear2.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cavytech.wear2.R;
import com.cavytech.wear2.base.AppCompatActivityEx;
import com.cavytech.wear2.entity.RelatedAppBean;
import com.cavytech.wear2.http.HttpUtils;
import com.cavytech.wear2.http.RequestCallback;
import com.cavytech.widget.CircleCornerForm;
import com.squareup.okhttp.Request;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * Created by hf on 2016/6/14.
 *
 * 相关App
 *
 */
public class OtherActivity extends AppCompatActivityEx {

    @ViewInject(R.id.title)
    private TextView title;

    @ViewInject(R.id.back)
    private ImageView back;

    @ViewInject(R.id.lv_other_activity)
//    private PullToRefreshListView lv_other_activity;
    private ListView lv_other_activity;

//    int pagenum = 1;
    private OtherAdapter adapter;
    private List<RelatedAppBean.GameListBean> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.other_activity);
        x.view().inject(this);
        title.setText(R.string.relatve_App);
        setToolBar();
        initlistener();
        initdata();
    }


    private void initdata() {
//        lv_other_activity.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        HttpUtils.getInstance().getGameList(OtherActivity.this, new RequestCallback<RelatedAppBean>() {
            @Override
            public void onError(Request request, Exception e) {
                Toast.makeText(OtherActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                try {
                    JSONObject jsonObj = new JSONObject(e.getLocalizedMessage());
                    int code = jsonObj.optInt("code");
                    if(HttpUtils.CODE_ACCOUNT_NOT_LOGIN==code){
                        AlertDialog.Builder dialog = new AlertDialog.Builder(OtherActivity.this);
                        dialog.setCancelable(false);
                        dialog.setMessage(R.string.not_login);
                        dialog.setPositiveButton(getString(R.string.ALERT_DLG_BTN_OK), new android.content.DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                arg0.dismiss();
                                startActivity(new Intent(OtherActivity.this,LoginActivity.class));
                                finish();
                            }
                        });
                        dialog.setNeutralButton(getString(R.string.ALERT_DLG_BTN_CANCEL), new android.content.DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                arg0.dismiss();
                            }
                        } );
                        dialog.show();
                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }

            @Override
            public void onResponse(RelatedAppBean response) {
                Log.e("TAG","测试----------");
                list = response.getGame_list();
                adapter = new OtherAdapter();
                lv_other_activity.setAdapter(adapter);
            }
        });


/*        OkHttpUtils
                .get()
                .url(HttpUtils.APP_URL)
                .addParams(HttpUtils.PARAM_AC, HttpUtils.PARAM_CAVYLIFE)
                .addParams(HttpUtils.PARAM_PAGENUM, pagenum+"")
                .addParams(HttpUtils.PARAM_PAGESIZE, "10")
                .addParams(HttpUtils.PARAM_PHONETYPE, HttpUtils.PHONETYPE_ANDROID)
                .build()
                .execute(new StringCallback()
                {
                    @Override
                    public void onError(Request request, Exception e)
                    {

                    }

                    @Override
                    public void onResponse(String response)
                    {
                        Gson gson = new Gson();
                        RelatedAppBean rb = gson.fromJson(response.toString(), RelatedAppBean.class);
                        RelatedAppBean.DataBean db = rb.getData();
                        list =  db.getGamelist();
                        adapter = new OtherAdapter();
                        lv_other_activity.setAdapter(adapter);
                        pagenum++;

//                        if(list.size()!=0){
//                            refresh();
//                        }

                    }
                });*/


    }

/*    private void refresh() {
        lv_other_activity.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
            }
            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                lv_other_activity.onRefreshComplete();
                getmoredata();

            }
        });
    }*/

   /* private void getmoredata() {
        Log.e("TAG","getmoredata()----");

        Log.e("TAG","当前页--"+pagenum);

        OkHttpUtils
                .get()
                .url(HttpUtils.APP_URL)
                .addParams(HttpUtils.PARAM_AC, HttpUtils.PARAM_CAVYLIFE)
                .addParams(HttpUtils.PARAM_PAGENUM, pagenum+"")
                .addParams(HttpUtils.PARAM_PAGESIZE, "10")
                .addParams(HttpUtils.PARAM_PHONETYPE, HttpUtils.PHONETYPE_ANDROID)
                .build()
                .execute(new StringCallback()
                {
                    @Override
                    public void onError(Request request, Exception e)
                    {

                    }

                    @Override
                    public void onResponse(String response)
                    {
                        Log.e("TAG","相关APP---"+response.toString());
                        Gson gson = new Gson();
                        RelatedAppBean rb = gson.fromJson(response.toString(), RelatedAppBean.class);
                        RelatedAppBean.DataBean db = rb.getData();
//                        if(db.getGamelist().size()!=0){
//                            pagenum++;
//                            list.addAll(db.getGamelist());
//                            adapter.notifyDataSetChanged();
//                        }else {
//                            Toast.makeText(getApplicationContext(),"已加载完毕---",Toast.LENGTH_SHORT).show();
//                        }
//                        lv_other_activity.onRefreshComplete();
//                        if(db.getGamelist()!=null){
//                            refresh();
//                        }

                    }
                });
    }*/

    private void initlistener() {

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
    class OtherAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder holder;

            if(convertView==null){
                holder = new ViewHolder();
                convertView = View.inflate(OtherActivity.this,R.layout.item_other_activity,null);
                holder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
                holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
//                holder.tv_size = (TextView) convertView.findViewById(R.id.tv_size);
                holder.tv_induction = (TextView) convertView.findViewById(R.id.tv_induction);
                holder.ll_other = (LinearLayout) convertView.findViewById(R.id.ll_other);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder)convertView.getTag();
            }

//            holder.iv_icon.setImageResource(list.get(position).getIcon());+
            Log.e("TAG","测试图标---"+list.get(position).getIcon());


            if (!"".equals(list.get(position).getIcon())) {
                Picasso.with(OtherActivity.this)
                        .load(list.get(position).getIcon())
//                        .load("http://pic38.nipic.com/20140228/2531170_213554844000_2.jpg")
                        .transform(new CircleCornerForm())
                        .error(R.drawable.head)
                        .into(holder.iv_icon);

            }

            holder.tv_name.setText(list.get(position).getTitle());
//            holder.tv_size.setText(list.get(position).getFilesize()+" M");
            holder.tv_induction.setText(list.get(position).getDesc());

            holder.ll_other.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Toast.makeText(OtherActivity.this,"点击了--",Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(OtherActivity.this,WebViewActivity.class);
                    i.putExtra("web",list.get(position).getHtml_url());
                    startActivity(i);
                }

            });

            return convertView;
        }
    }

    class ViewHolder {
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_size;
        TextView tv_induction;
        LinearLayout ll_other;
    }



}
