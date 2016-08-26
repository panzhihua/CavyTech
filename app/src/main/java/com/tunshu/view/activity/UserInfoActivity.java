package com.tunshu.view.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.basecore.util.bitmap.Options;
import com.basecore.util.core.ListUtils;
import com.basecore.util.core.StringUtils;
import com.basecore.widget.CustomToast;
import com.basecore.widget.dialog.Effectstype;
import com.basecore.widget.dialog.NiftyDialogBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.taig.pmc.PopupMenuCompat;
import com.tunshu.R;
import com.tunshu.entity.CityEntity;
import com.tunshu.entity.CommentEntity;
import com.tunshu.entity.CommonEntity;
import com.tunshu.entity.DistrictEntity;
import com.tunshu.entity.ProvinceEntity;
import com.tunshu.entity.UploadFileEntity;
import com.tunshu.entity.UserEntity;
import com.tunshu.http.MyHttpClient;
import com.tunshu.util.Constants;
import com.tunshu.util.LogUtil;
import com.tunshu.util.MD5;
import com.tunshu.util.XmlParserHandler;
import com.tunshu.wheelview.OnWheelChangedListener;
import com.tunshu.wheelview.OnWheelScrollListener;
import com.tunshu.wheelview.WheelView;
import com.tunshu.wheelview.adapter.ArrayWheelAdapter;
import com.tunshu.wheelview.adapter.NumericWheelAdapter;

import org.apache.http.Header;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * 作者：yzb on 2015/7/15 17:06
 */
public class UserInfoActivity extends CommonActivity implements OnWheelChangedListener {
    private RelativeLayout userPhoto;
    private RelativeLayout nickName;
    private RelativeLayout gender;
    private RelativeLayout birthday;
    private RelativeLayout address;
    private SimpleDraweeView photoImg;
    private TextView nicknameText;
    private TextView genderText;
    private TextView birthdayText;
    private TextView addressText;
    private TextView logoutText;
    private LayoutInflater inflater;
    private WheelView year;
    private WheelView month;
    private WheelView day;
    private WheelView province;
    private WheelView city;
    private int mYear = 1996;
    private int mMonth = 1;
    private int mDay = 1;
    private View view;
    protected String[] mProvinceDatas;
    protected Map<String, String[]> mCitisDatasMap = new HashMap<String, String[]>();
    protected Map<String, String[]> mDistrictDatasMap = new HashMap<String, String[]>();
    protected Map<String, String> mZipcodeDatasMap = new HashMap<String, String>();
    protected String mCurrentProviceName;
    protected String mCurrentCityName;
    protected String mCurrentDistrictName = "";
    protected String mCurrentZipCode = "";

    private NiftyDialogBuilder mDialogBuilder;
    private String mNikeName = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);
        findView();
        getUserInfo();
        addListener();
    }

    private void findView() {
        findTitle();
        userPhoto = (RelativeLayout) findViewById(R.id.user_photo);
        nickName = (RelativeLayout) findViewById(R.id.nickname);
        gender = (RelativeLayout) findViewById(R.id.gender);
        birthday = (RelativeLayout) findViewById(R.id.birthday);
        address = (RelativeLayout) findViewById(R.id.address);
        photoImg = (SimpleDraweeView) findViewById(R.id.user_photo_img);
        nicknameText = (TextView) findViewById(R.id.nickname_text);
        genderText = (TextView) findViewById(R.id.gender_text);
        birthdayText = (TextView) findViewById(R.id.birthday_text);
        addressText = (TextView) findViewById(R.id.address_text);
        logoutText = (TextView) findViewById(R.id.logout);
        title.setText(getString(R.string.person_info));
    }

    private void fillView(UserEntity entity) {
        photoImg.setImageURI(Uri.parse(entity.getData().getAvatar()));
        nicknameText.setText(entity.getData().getNikename());
        if (!StringUtils.isEmpty(entity.getData().getGender())) {
            genderText.setText(entity.getData().getGender().equals("0") ? getString(R.string.woman) : getString(R.string.man));
        }
        birthdayText.setText(entity.getData().getBirthday());
        addressText.setText(entity.getData().getComefrom());
    }

    private void addListener() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        userPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopu(photoImg);
            }
        });

        gender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGenderPopu(genderText);
            }
        });
        nickName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCommentDialog();
            }
        });
        birthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog();
            }
        });
        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddressDialog();
            }
        });
        logoutText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showExitDialog();
            }
        });
    }

    private void showGenderPopu(final View view) {
        PopupMenuCompat menu = PopupMenuCompat.newInstance(UserInfoActivity.this, view);
        menu.inflate(R.menu.menu_gender);
        menu.setOnMenuItemClickListener(new PopupMenuCompat.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                genderText.setText(item.getTitle());
                setUserInfo();
                return false;
            }
        });
        menu.show();

    }

    private void showPopu(final View view) {
        PopupMenuCompat menu = PopupMenuCompat.newInstance(UserInfoActivity.this, view);
        menu.inflate(R.menu.menu_choice_picture);
        menu.setOnMenuItemClickListener(new PopupMenuCompat.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getTitle().equals(getString(R.string.take_a_picture))) {
                    doCamera(true, 200, 200);
                } else if (item.getTitle().equals(getString(R.string.album))) {
                    doGallery(true, 200, 200);
                }
                return false;
            }
        });
        menu.show();

    }
    private void showExitDialog() {
        final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(UserInfoActivity.this);
        dialogBuilder.withTitle(null).withTitleColor("#ffffff").withMessage(getString(R.string.exit_info)).isCancelableOnTouchOutside(true).isCancelable(true).withDuration(300).withEffect(Effectstype.Fadein)
                .withButton1Text(getString(R.string.cancel)).withButton2Text(getString(R.string.ok)).setButton1Click(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBuilder.dismiss();
            }
        }).setButton2Click(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
                dialogBuilder.dismiss();
            }
        }).show();
    }

    private void showCommentDialog() {
        final LayoutInflater inflater = LayoutInflater.from(UserInfoActivity.this);
        View layout = inflater.inflate(R.layout.activity_gamedetail_comment, null);
        final EditText commentInput = (EditText) layout.findViewById(R.id.comment_input);
        commentInput.setHint(getString(R.string.nickname_length_tips));
        commentInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16)});
        mDialogBuilder = NiftyDialogBuilder.getInstance(UserInfoActivity.this);
        mDialogBuilder.withTitle(null).withTitleColor("#ffffff").withMessage(null).isCancelableOnTouchOutside(true).isCancelable(true).withDuration(300).withEffect(Effectstype.Fadein)
                .setCustomView(layout, UserInfoActivity.this).withButton1Text(getString(R.string.cancel)).withButton2Text(getString(R.string.ok)).setButton1Click(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cloaseInputMethod();
                mDialogBuilder.dismiss();
            }
        }).setButton2Click(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = commentInput.getText().toString().replace(" ", "");
                if (StringUtils.count(comment) > 0 && StringUtils.count(comment) < 17) {
                    mNikeName =  comment;
                    showProgress();
                    setUserInfo();
                } else {
                    CustomToast.showToast(UserInfoActivity.this, getString(R.string.nickname_length_tips));
                }
            }
        }).show();
        openInputMethod(commentInput);
    }

    public void openInputMethod(final EditText editText) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                InputMethodManager inputManager = (InputMethodManager) editText.getContext().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(editText, 0);
            }
        }, 310);
    }

    public void cloaseInputMethod() {
        try {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(getCurrentFocus().getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private View getDataPick() {
        Calendar c = Calendar.getInstance();
        int norYear = c.get(Calendar.YEAR);
        int curYear = mYear;
        int curMonth = mMonth;
        int curDate = mDay;
        inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.wheel_date_picker, null);

        year = (WheelView) view.findViewById(R.id.year);
        NumericWheelAdapter numericWheelAdapter1 = new NumericWheelAdapter(this, 1950, norYear);
        numericWheelAdapter1.setLabel(getString(R.string.year));
        year.setViewAdapter(numericWheelAdapter1);
        year.setCyclic(true);//是否可循环滑动
        year.addScrollingListener(scrollListener);

        month = (WheelView) view.findViewById(R.id.month);
        NumericWheelAdapter numericWheelAdapter2 = new NumericWheelAdapter(this, 1, 12, "%02d");
        numericWheelAdapter2.setLabel(getString(R.string.month));
        month.setViewAdapter(numericWheelAdapter2);
        month.setCyclic(true);
        month.addScrollingListener(scrollListener);

        day = (WheelView) view.findViewById(R.id.day);
        initDay(curYear, curMonth);//判断年月，得到该月具体日期数
        day.setCyclic(true);
        day.addScrollingListener(scrollListener);
        if (StringUtils.isEmpty(birthdayText.getText().toString())) {
            year.setCurrentItem(curYear - 1950);
            month.setCurrentItem(curMonth - 1);
            day.setCurrentItem(curDate - 1);
        } else {
            int setYear = Integer.parseInt(birthdayText.getText().toString().split("-")[0]);
            int setMonth = Integer.parseInt(birthdayText.getText().toString().split("-")[1]);
            int setDay = Integer.parseInt(birthdayText.getText().toString().split("-")[2]);
            LogUtil.getLogger().d(setYear + "-" + setMonth + "-" + setDay);
            year.setCurrentItem(setYear - 1950);
            month.setCurrentItem(setMonth - 1);
            day.setCurrentItem(setDay - 1);
        }

        return view;
    }

    OnWheelScrollListener scrollListener = new OnWheelScrollListener() {
        @Override
        public void onScrollingStarted(WheelView wheel) {

        }

        @Override
        public void onScrollingFinished(WheelView wheel) {
            int n_year = year.getCurrentItem() + 1950;
            int n_month = month.getCurrentItem() + 1;
            initDay(n_year, n_month);
        }
    };

    /**
     * @param year
     * @param month
     * @return
     */
    private int getDay(int year, int month) {
        int day = 30;
        boolean flag = false;
        switch (year % 4) {
            case 0:
                flag = true;
                break;
            default:
                flag = false;
                break;
        }
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                day = 31;
                break;
            case 2:
                day = flag ? 29 : 28;
                break;
            default:
                day = 30;
                break;
        }
        return day;
    }

    /**
     */
    private void initDay(int arg1, int arg2) {
        NumericWheelAdapter numericWheelAdapter = new NumericWheelAdapter(this, 1, getDay(arg1, arg2), "%02d");
        numericWheelAdapter.setLabel(getString(R.string.date));
        day.setViewAdapter(numericWheelAdapter);
    }

    private void showDateDialog() {
        final LayoutInflater inflater = LayoutInflater.from(UserInfoActivity.this);
        View layout = inflater.inflate(R.layout.public_choice_date, null);
        LinearLayout datePickerLayout = (LinearLayout) layout.findViewById(R.id.datePicker);
        TextView save = (TextView) layout.findViewById(R.id.save);
        datePickerLayout.addView(getDataPick());
        final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(UserInfoActivity.this);
        dialogBuilder.withTitle(null).withTitleColor("#ffffff").withMessage(null).isCancelableOnTouchOutside(true).isCancelable(true).withDuration(700).withEffect(Effectstype.Fadein)
                .setCustomView(layout, UserInfoActivity.this).show();
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String birthday = new StringBuilder()
                        .append((year.getCurrentItem() + 1950)).append("-")
                        .append((month.getCurrentItem() + 1) < 10 ? "0" + (month.getCurrentItem() + 1) : (month.getCurrentItem() + 1)).append("-")
                        .append(((day.getCurrentItem() + 1) < 10) ? "0" + (day.getCurrentItem() + 1) : (day.getCurrentItem() + 1)).toString();
                birthdayText.setText(birthday);
                dialogBuilder.dismiss();
                setUserInfo();
            }
        });

        dialogBuilder.showMsgLinearLayout(View.GONE);
        dialogBuilder.showBtnLinearLayout(View.GONE);
    }

    private void showAddressDialog() {
        final LayoutInflater inflater = LayoutInflater.from(UserInfoActivity.this);
        View layout = inflater.inflate(R.layout.public_choice_address, null);
        province = (WheelView) layout.findViewById(R.id.id_province);
        city = (WheelView) layout.findViewById(R.id.id_city);
        province.addChangingListener(this);
        city.addChangingListener(this);
        initProvinceDatas();
        province.setViewAdapter(new ArrayWheelAdapter<String>(UserInfoActivity.this, mProvinceDatas));
        int currentId = 0;
        if (mProvinceDatas.length > 0) {
            for (int i = 0, length = mProvinceDatas.length; i < length; i++) {
                if (mProvinceDatas[i].equals(mCurrentProviceName)) {
                    currentId = i;
                    break;
                }
            }
        }
        province.setCurrentItem(currentId);
        // 设置可见条目数量
        province.setVisibleItems(5);
        city.setVisibleItems(5);
        updateCities();
        updateAreas();
        TextView save = (TextView) layout.findViewById(R.id.save);
        final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(UserInfoActivity.this);
        dialogBuilder.withTitle(null).withTitleColor("#ffffff").withMessage(null).isCancelableOnTouchOutside(true).isCancelable(true).withDuration(300).withEffect(Effectstype.Fadein)
                .setCustomView(layout, UserInfoActivity.this).show();
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addressText.setText(mCurrentProviceName + "-" + mCurrentCityName);
                dialogBuilder.dismiss();
                setUserInfo();
            }
        });

        dialogBuilder.showMsgLinearLayout(View.GONE);
        dialogBuilder.showBtnLinearLayout(View.GONE);
    }

    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {
        // TODO Auto-generated method stub
        if (wheel == province) {
            updateCities();
        } else if (wheel == city) {
            updateAreas();
        }
    }

    /**
     * 根据当前的市，更新区WheelView的信息
     */
    private void updateAreas() {
        int pCurrent = city.getCurrentItem();
        mCurrentCityName = mCitisDatasMap.get(mCurrentProviceName)[pCurrent];
        String[] areas = mDistrictDatasMap.get(mCurrentCityName);

        if (areas == null) {
            areas = new String[]{""};
        }
    }

    /**
     * 根据当前的省，更新市WheelView的信息
     */
    private void updateCities() {
        int pCurrent = province.getCurrentItem();
        mCurrentProviceName = mProvinceDatas[pCurrent];
        String[] cities = mCitisDatasMap.get(mCurrentProviceName);
        if (cities == null) {
            cities = new String[]{""};
        }
        city.setViewAdapter(new ArrayWheelAdapter<String>(this, cities));
        int currentId = 0;
        if (cities.length > 0) {
            for (int i = 0, length = cities.length; i < length; i++) {
                if (cities[i].equals(mCurrentCityName)) {
                    currentId = i;
                    break;
                }
            }
        }
        city.setCurrentItem(currentId);
        updateAreas();
    }

    protected void initProvinceDatas() {
        List<ProvinceEntity> provinceList;
        AssetManager asset = getAssets();
        try {
            InputStream input = asset.open("province_data.xml");
            // 创建一个解析xml的工厂对象
            SAXParserFactory spf = SAXParserFactory.newInstance();
            // 解析xml
            SAXParser parser = spf.newSAXParser();
            XmlParserHandler handler = new XmlParserHandler();
            parser.parse(input, handler);
            input.close();
            // 获取解析出来的数据
            provinceList = handler.getDataList();
            if (StringUtils.isEmpty(addressText.getText().toString())) {
                //*/ 初始化默认选中的省、市、区
                if (provinceList != null && !provinceList.isEmpty()) {
                    mCurrentProviceName = provinceList.get(0).getName();
                    List<CityEntity> cityList = provinceList.get(0).getCityList();
                    if (cityList != null && !cityList.isEmpty()) {
                        mCurrentCityName = cityList.get(0).getName();
                        List<DistrictEntity> districtList = cityList.get(0).getDistrictList();
                        mCurrentDistrictName = districtList.get(0).getName();
                        mCurrentZipCode = districtList.get(0).getZipcode();
                    }
                }
            } else {
                try {
                    mCurrentProviceName = addressText.getText().toString().split("-")[0];
                    mCurrentCityName = addressText.getText().toString().split("-")[1];
                    LogUtil.getLogger().d(mCurrentProviceName + "-" + mCurrentCityName);
                } catch (Exception e) {
                    //*/ 初始化默认选中的省、市、区
                    if (provinceList != null && !provinceList.isEmpty()) {
                        mCurrentProviceName = provinceList.get(0).getName();
                        List<CityEntity> cityList = provinceList.get(0).getCityList();
                        if (cityList != null && !cityList.isEmpty()) {
                            mCurrentCityName = cityList.get(0).getName();
                            List<DistrictEntity> districtList = cityList.get(0).getDistrictList();
                            mCurrentDistrictName = districtList.get(0).getName();
                            mCurrentZipCode = districtList.get(0).getZipcode();
                        }
                    }
                    e.printStackTrace();
                }
            }
            //*/
            mProvinceDatas = new String[provinceList.size()];
            for (int i = 0; i < provinceList.size(); i++) {
                // 遍历所有省的数据
                mProvinceDatas[i] = provinceList.get(i).getName();
                List<CityEntity> cityList = provinceList.get(i).getCityList();
                String[] cityNames = new String[cityList.size()];
                for (int j = 0; j < cityList.size(); j++) {
                    // 遍历省下面的所有市的数据
                    cityNames[j] = cityList.get(j).getName();
                    List<DistrictEntity> districtList = cityList.get(j).getDistrictList();
                    String[] distrinctNameArray = new String[districtList.size()];
                    DistrictEntity[] distrinctArray = new DistrictEntity[districtList.size()];
                    for (int k = 0; k < districtList.size(); k++) {
                        // 遍历市下面所有区/县的数据
                        DistrictEntity districtModel = new DistrictEntity(districtList.get(k).getName(), districtList.get(k).getZipcode());
                        // 区/县对于的邮编，保存到mZipcodeDatasMap
                        mZipcodeDatasMap.put(districtList.get(k).getName(), districtList.get(k).getZipcode());
                        distrinctArray[k] = districtModel;
                        distrinctNameArray[k] = districtModel.getName();
                    }
                    // 市-区/县的数据，保存到mDistrictDatasMap
                    mDistrictDatasMap.put(cityNames[j], distrinctNameArray);
                }
                // 省-市的数据，保存到mCitisDatasMap
                mCitisDatasMap.put(provinceList.get(i).getName(), cityNames);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {

        }
    }

    @Override
    public void onReturnImageUri(String imageUri) {
        super.onReturnImageUri(imageUri);
        if (imageUri != null) {
            LogUtil.getLogger().d(imageUri);
            uploadFile(imageUri);
        }
    }

    private void uploadFile(final String imageUri) {
        RequestParams params = getCommonParams();
        try {
            params.put("imgFile", new FileInputStream(imageUri.substring(7, imageUri.length())), UUID.randomUUID().toString(), "image/jpeg");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        MyHttpClient.post(UserInfoActivity.this, "usercenter/updateUserImg", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    LogUtil.getLogger().d(response.toString());
                    UploadFileEntity entity = com.alibaba.fastjson.JSONObject.parseObject(response.toString(), UploadFileEntity.class);
                    if (entity.getCode().equals("1001")) {
                        photoImg.setImageURI(Uri.parse(entity.getData().getImg()));
                        getCoreApplication().getPreferenceConfig().setString(Constants.USER_ICON, entity.getData().getImg());
                        sendBroadcast(new Intent(Constants.USER_INFO_REFRESH));//刷新首页用户信息
                    }
                    CustomToast.showToast(getCoreApplication(), entity.getMsg());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }

    private void getUserInfo() {
        showProgress();
        RequestParams params = getCommonParams();
        params.put("ac", "useronedata");
        MyHttpClient.get(UserInfoActivity.this, "usercenter/index", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                hideProgress();
                try {
                    LogUtil.getLogger().d(response.toString());
                    UserEntity entity = com.alibaba.fastjson.JSONObject.parseObject(response.toString(), UserEntity.class);
                    if (entity.getCode().equals("1001")) {
                        fillView(entity);
                    } else {
                        CustomToast.showToast(UserInfoActivity.this, entity.getMsg());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                hideProgress();
            }
        });
    }

    private void setUserInfo() {
        RequestParams params = getCommonParams();
        params.put("ac", "updateuserdata");
        if(mNikeName == ""){
            mNikeName = nicknameText.getText().toString();
        }
        params.put("nikename", URLEncoder.encode(mNikeName));
        params.put("birthday", birthdayText.getText().toString());
        params.put("comefrom", addressText.getText().toString());
        params.put("gender", genderText.getText().toString().equals(getString(R.string.woman)) ? "0" : "1");
        MyHttpClient.get(UserInfoActivity.this, "usercenter/index", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    hideProgress();

                    LogUtil.getLogger().d(response.toString());
                    CommonEntity entity = com.alibaba.fastjson.JSONObject.parseObject(response.toString(), CommonEntity.class);
                    if (entity.getCode().equals("1001")) {
                        nicknameText.setText(mNikeName);
                        getCoreApplication().getPreferenceConfig().setString(Constants.NICKNAME, mNikeName);
                        sendBroadcast(new Intent(Constants.USER_INFO_REFRESH));//刷新首页用户信息

                         mDialogBuilder.dismiss();
                    } else {
                        CustomToast.showToast(UserInfoActivity.this, entity.getMsg());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                hideProgress();
            }
        });
    }

    private void logout() {
        showProgress();
        RequestParams params = getCommonParams();
        params.put("ac", "outlogin");
        MyHttpClient.get(UserInfoActivity.this, "usercenter/index", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                hideProgress();
                try {
                    LogUtil.getLogger().d(response.toString());
                    CommonEntity entity = com.alibaba.fastjson.JSONObject.parseObject(response.toString(), CommonEntity.class);
                    if (entity.getCode().equals("1001")) {
                        if (!getCoreApplication().getPreferenceConfig().getBoolean(Constants.IS_REMEMBER, false)) {
                            getCoreApplication().getPreferenceConfig().setString(Constants.USERNAME, "");
                            getCoreApplication().getPreferenceConfig().setString(Constants.PASSWORD, "");
                        }
                        getCoreApplication().getPreferenceConfig().setString(Constants.NICKNAME, getString(R.string.not_login));
                        getCoreApplication().getPreferenceConfig().setString(Constants.USER_ICON, "");
                        getCoreApplication().getPreferenceConfig().setString(Constants.USERID, "-1");
                        getCoreApplication().getPreferenceConfig().setString(Constants.TOKEN, "");
                        sendBroadcast(new Intent(Constants.USER_INFO_REFRESH));//刷新首页用户信息
                        finish();
                    } else {
                        CustomToast.showToast(UserInfoActivity.this, entity.getMsg());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                hideProgress();
            }
        });
    }
}
