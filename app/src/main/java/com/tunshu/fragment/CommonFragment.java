package com.tunshu.fragment;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.basecore.activity.BaseFragment;
import com.basecore.util.core.FileUtils;
import com.basecore.util.core.ListUtils;
import com.basecore.widget.CustomToast;
import com.golshadi.majid.core.enums.TaskStates;
import com.golshadi.majid.report.ReportStructure;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.tunshu.R;
import com.tunshu.application.CommonApplication;
import com.tunshu.entity.GameListEntity;
import com.tunshu.util.Constants;
import com.tunshu.util.LogUtil;

import java.io.File;
import java.util.List;
import java.util.Locale;

/**
 * Created by ShadowNight on 2015/7/9.
 */
public class CommonFragment extends BaseFragment {

    public RequestParams getCommonParams() {
        RequestParams params = new RequestParams();
        params.put("phonetype", "ANDROID");
        params.put("userid", getCoreApplication().getPreferenceConfig().getString(Constants.USERID, "-1"));
        params.put("token", getCoreApplication().getPreferenceConfig().getString(Constants.TOKEN, "1"));
        params.put("language", getLanguage());
        return params;
    }
    public String getLanguage() {
        Locale locale = getResources().getConfiguration().locale;
        String language = locale.getLanguage();

        if (language.endsWith("en")){
            language = "en";
        }else{
            String cnt = locale.getCountry();

            if(!"".equals(cnt)){
                language = language + "_" + locale.getCountry();
            }
        }

        return language;
    }

    public void deleteDownloadInfo(String packageName) {
        final List<GameListEntity> downloadInfoList = ((CommonApplication) getCoreApplication()).finalDb.findAll(GameListEntity.class);
        if (!ListUtils.isEmpty(downloadInfoList)) {
            for (int i = 0, size = downloadInfoList.size(); i < size; i++) {
                if (packageName.equals(downloadInfoList.get(i).getPackpagename())) {
                    LogUtil.getLogger().d(downloadInfoList.get(i).getTaskId());
                    getCoreApplication().getPreferenceConfig().setLong(new HashCodeFileNameGenerator().generate(downloadInfoList.get(i).getDownurl()), -10L);
                    Intent intent = new Intent(Constants.DOWNLOAD_INFO_DELETE);
                    intent.putExtra("taskToken", downloadInfoList.get(i).getTaskId());
                    downloadInfoList.remove(i);
                    break;
                }
            }
            ((CommonApplication) getCoreApplication()).finalDb.deleteAll(GameListEntity.class);
            for (int j = 0, length = downloadInfoList.size(); j < length; j++) {
                ((CommonApplication) getCoreApplication()).finalDb.save(downloadInfoList.get(j));
            }
        }
    }



}
