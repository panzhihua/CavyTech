package com.cavytech.wear2.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cavytech.wear2.R;

/**
 * 作者：李彬
 * 邮箱：bin.li@tunshu .com
 */

public class RecommandFragmentPager extends Fragment{
    int mNum;

    public static RecommandFragmentPager newInstance(int num)
    {
        RecommandFragmentPager f=new RecommandFragmentPager();

        Bundle args=new Bundle();
        args.putInt("num",num);
        f.setArguments(args);
        return f;
    }
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mNum=getArguments()!=null?getArguments().getInt("num"):1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View v=inflater.inflate(R.layout.tab_fargment_content,container,false);
        View tv=v.findViewById(R.id.text);
        ((TextView)tv).setText("Fragment #---------------------------------- "+mNum);
        return v;
    }
}
