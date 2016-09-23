package com.cavytech.wear2.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cavytech.wear2.R;
import com.cavytech.wear2.base.AppCompatActivityEx;
import com.cavytech.wear2.entity.GetPhone;
import com.cavytech.wear2.http.HttpUtils;
import com.cavytech.wear2.http.RequestCallback;
import com.cavytech.wear2.util.CacheUtils;
import com.cavytech.widget.SwitchButtonEx;
import com.squareup.okhttp.Request;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hf on 2016/5/27.
 */
public class SecurityActivity extends AppCompatActivityEx {

    private static final int ADDSECURITY = 1;
    @ViewInject(R.id.title)
    private TextView title;

    @ViewInject(R.id.back)
    private ImageView back;

    @ViewInject(R.id.tv_security_add)
    private TextView tv_security_add;

    @ViewInject(R.id.ls_security)
    private ListView ls_security;

    @ViewInject(R.id.sbe_clock_item)
    private SwitchButtonEx sbe_clock_item;

    private String username;
    private ArrayList<GetPhone.ContactsBean> list = new ArrayList<GetPhone.ContactsBean>();
    private PhoneAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_security);

        x.view().inject(this);

        title.setText(getString(R.string.safety));

        getdatafromnet();

        initlistener();
        setToolBar();
//        stopService(new Intent("com.cavytech.wear2.application.PhoneService"));

    }

    /**
     * 获取紧急联系人
     */
    private void getdatafromnet() {

        HttpUtils.getInstance().getEmergencyPhone(SecurityActivity.this,new RequestCallback<GetPhone>() {
            @Override
            public void onError(Request request, Exception e) {

                try {
                    JSONObject jsonObj = new JSONObject(e.getLocalizedMessage());
                    int code = jsonObj.optInt("code");
                    if(HttpUtils.CODE_ACCOUNT_NOT_LOGIN==code){
                        AlertDialog.Builder dialog = new AlertDialog.Builder(SecurityActivity.this);
                        dialog.setCancelable(false);
                        dialog.setMessage(R.string.not_login);
                        dialog.setPositiveButton(getString(R.string.ALERT_DLG_BTN_OK), new android.content.DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                arg0.dismiss();
                                startActivity(new Intent(SecurityActivity.this,LoginActivity.class));
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
                } catch (Exception e2) {
                    e2.printStackTrace();
                }


            }

            @Override
            public void onResponse(GetPhone response) {
                Log.e("TAG","获取紧急联系人----"+response.toString());
                for(int i=0;i<response.getContacts().size();i++){
                    list.add(response.getContacts().get(i));
                }
                adapter = new PhoneAdapter();
                ls_security.setAdapter(adapter);
            }
        });
    }

    private void initlistener() {

        if(CacheUtils.isOPen(SecurityActivity.this)){
            sbe_clock_item.setChecked(true);
        }else {
            sbe_clock_item.setChecked(false);
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

            tv_security_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(tv_security_add.isClickable()){
                        Log.e("TAG","-----"+tv_security_add.isClickable());
                        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                        startActivityForResult(intent, ADDSECURITY);

//                        Intent intent = new Intent(Intent.ACTION_PICK);
//                        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
//                        startActivityForResult(intent, ADDSECURITY);

//                        Intent intent = new Intent(Intent.ACTION_PICK);
//                        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
//                        startActivityForResult(intent, ADDSECURITY);

                    }
                }
            });

        sbe_clock_item.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){//打开
                    Log.e("TAG","监听打开---"+isChecked);
                    CacheUtils.initGPS(SecurityActivity.this);
                    tv_security_add.setClickable(true);
                }else {//关闭
                    Log.e("TAG","监听关闭---"+isChecked);
                    tv_security_add.setClickable(false);
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ADDSECURITY:
                if (resultCode == RESULT_OK) {
                    Uri contactData = data.getData();
                    Cursor cursor = this.getContentResolver().query(contactData, null, null, null, null);
//                    Cursor cursor = this.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
                  if(cursor.moveToFirst()){
                        username = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        Log.e("TAG","用户名--"+username);
                        List<String> nums=this.getContactPhone(cursor);
                      if(nums==null|| nums.isEmpty()){
                          return;
                      }else if(nums.size()==1){
                          String num =nums.get(0);
                          Log.e("TAG", "hhhhhh----" + num.replace(" ", ""));
                          if (isMobileNO(num.replace(" ", ""))) {
                              GetPhone.ContactsBean phoneBean = new GetPhone.ContactsBean(username, num.replace(" ", ""));
                              if (list.size() < 3) {
                                  if (!list.contains(phoneBean)) {
                                      list.add(phoneBean);
                                  }
                              } else {
                                  Toast.makeText(SecurityActivity.this,getString(R.string.at_most_3_emergency_contact_numbers_can_be_added), Toast.LENGTH_SHORT).show();
                              }

                          } else {
                              Toast.makeText(SecurityActivity.this, getString(R.string.invalid_contact_number), Toast.LENGTH_SHORT).show();
                          }
                          if (list != null&&adapter!=null) {
                              posttonet(list);
                              CacheUtils.saveArray(SecurityActivity.this, list);
                              adapter.notifyDataSetChanged();
                          }
                      }else {
                          AlertDialog.Builder builder = new AlertDialog.Builder(SecurityActivity.this);
                          builder.setTitle(getString(R.string.select_a_contact_number));
                          //    指定下拉列表的显示数据
                          final String[] cities = new String[nums.size()];
                          nums.toArray(cities);
                          //    设置一个下拉的列表选择项
                          builder.setItems(cities, new DialogInterface.OnClickListener() {
                              @Override
                              public void onClick(DialogInterface dialog, int which) {
                                  String num = cities[which];
                                  Log.e("TAG", "hhhhhh----" + num.replace(" ", ""));
                                  if (isMobileNO(num.replace(" ", ""))) {
                                      GetPhone.ContactsBean phoneBean = new GetPhone.ContactsBean(username, num.replace(" ", ""));
                                      if (list.size() < 3) {
                                          if (!list.contains(phoneBean)) {
                                              list.add(phoneBean);
                                          }
                                      } else {
                                          Toast.makeText(SecurityActivity.this, getString(R.string.at_most_3_emergency_contact_numbers_can_be_added), Toast.LENGTH_SHORT).show();
                                      }

                                  } else {
                                      Toast.makeText(SecurityActivity.this, getString(R.string.invalid_contact_number), Toast.LENGTH_SHORT).show();
                                  }
                                  if (list != null&&adapter!=null) {
                                      posttonet(list);
                                      CacheUtils.saveArray(SecurityActivity.this, list);
                                      adapter.notifyDataSetChanged();
                                  }
                              }
                          });
                          builder.show();
                      }
                        cursor.close();
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * 上传紧急联系人数据到net
     */
    private void posttonet(ArrayList<GetPhone.ContactsBean> list) {

        HttpUtils.getInstance().setEmergencyPhone(SecurityActivity.this,list, new RequestCallback<Object>() {
            @Override
            public void onError(Request request, Exception e) {

                try {
                    JSONObject jsonObj = new JSONObject(e.getLocalizedMessage());
                    int code = jsonObj.optInt("code");
                    if(HttpUtils.CODE_ACCOUNT_NOT_LOGIN==code){
                        AlertDialog.Builder dialog = new AlertDialog.Builder(SecurityActivity.this);
                        dialog.setCancelable(false);
                        dialog.setMessage(R.string.not_login);
                        dialog.setPositiveButton(getString(R.string.ALERT_DLG_BTN_OK), new android.content.DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                arg0.dismiss();
                                startActivity(new Intent(SecurityActivity.this,LoginActivity.class));
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
            public void onResponse(Object response) {
                Log.e("TAG","上传紧急联系人----"+response.toString());
            }
        });

    }

    private List<String> getContactPhone(Cursor cursor) {
        int phoneColumn = cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
        int phoneNum = cursor.getInt(phoneColumn);
        //String result = "";
        List<String> result=new ArrayList<>();
        if (phoneNum > 0) {
            // 获得联系人的ID号
            int idColumn = cursor.getColumnIndex(ContactsContract.Contacts._ID);
            String contactId = cursor.getString(idColumn);
            // 获得联系人电话的cursor
            Cursor phone = getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "="
                            + contactId, null, null);
            if (phone.moveToFirst()) {
                for (; !phone.isAfterLast(); phone.moveToNext()) {
                    int index = phone
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    int typeindex = phone
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);
//                    int phone_type = phone.getInt(typeindex);
                    String phoneNumber = phone.getString(index);
                    result.add(phoneNumber);
                }
                if (!phone.isClosed()) {
                    phone.close();
                }
            }
        }
        return result;
    }

    class PhoneAdapter extends BaseAdapter{

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
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if(convertView == null){
                convertView = View.inflate(SecurityActivity.this,R.layout.phone_item_list,null);
                viewHolder = new ViewHolder();
                viewHolder.tv_phone_name = (TextView) convertView.findViewById(R.id.tv_phone_name);
                viewHolder.tv_phone_juli = (TextView) convertView.findViewById(R.id.tv_phone_juli);
                viewHolder.ll_phone_add = (LinearLayout) convertView.findViewById(R.id.ll_phone_add);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.ll_phone_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    list.remove(position);
                    adapter.notifyDataSetChanged();
                    posttonet(list);
                }
            });
            if(list.get(position).getName()!=null){
                viewHolder.tv_phone_name.setText(list.get(position).getName());
            }
            if(list.get(position).getPhone()!=null){
                viewHolder.tv_phone_juli.setText(list.get(position).getPhone());
            }
            return convertView;
        }
    }

    public static class ViewHolder{
        TextView tv_phone_name;
        TextView tv_phone_juli;
        LinearLayout ll_phone_add;
    }


    /**
     * 判断用户是否为手机号码
     */

    public boolean isMobileNO(String mobiles) {
        Pattern p = Pattern.compile("^((13[0-9])|(147)|(177)|(15[^4,\\D])|(18[0-4,5-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

}
