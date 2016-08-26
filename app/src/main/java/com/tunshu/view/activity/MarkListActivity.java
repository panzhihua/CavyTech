package com.tunshu.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tunshu.util.Constants;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.RequestParams;
import com.tunshu.R;
import com.tunshu.fragment.GamesListView;
import com.tunshu.util.Constants;

/**
 * Created by longjining on 16/1/26.
 */
public class MarkListActivity extends CommonActivity {

    MarkGamesListViewEx markListView;

    private String mark_id = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark_list);
        initView();
        addListener();

        mark_id = getIntent().getStringExtra("markid");

        initMarkListViewEx();
     }

    void initMarkListViewEx(){
        markListView = new MarkGamesListViewEx();
        PullToRefreshListView listView = (PullToRefreshListView) findViewById(R.id.main_pull_refresh_view);

        markListView.setListViewTag("MarkListActivity");
        markListView.setActivity(MarkListActivity.this);
        markListView.setListView(listView);
        markListView.setReceiver();
        markListView.getData(false);
    }

    private void initView() {
        findTitle();
        title.setText(getIntent().getStringExtra("markname"));
     }

    private void addListener() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public class MarkGamesListViewEx extends GamesListView{

        public void setListView(PullToRefreshListView listView){

            this.listView = listView;
        }
        public void setActivity(Activity activity){
            this.activity = activity;
        }
        public void getData(final boolean isMore){

            showProgress();
            RequestParams params = getCommonParams();
            params.put("ac", "getmarkgames");
            params.put("mark_id", String.valueOf(mark_id));
            params.put("pagenum", String.valueOf(pageNo));
            params.put("pagesize", Constants.PAGESIZE);

            super.getData(params, Constants.HTTP_PRE_APPINDEX, isMore, MarkListActivity.this);
        }
        public void setReceiver(){
            super.setReceiver();
        }
    }
}
