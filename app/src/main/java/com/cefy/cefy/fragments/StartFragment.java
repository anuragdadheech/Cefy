package com.cefy.cefy.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cefy.cefy.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Anurag
 */
public class StartFragment extends Fragment {

    private static final String RESOURCE_ID = "resource_id";
    private static final String DESCRIPTION_ID = "desc_id";

    @BindView(R.id.iv_hero) ImageView heroImage;
    @BindView(R.id.tv_description) TextView descText;

    public StartFragment() {
    }

    public static StartFragment newInstance(int resId, String desc) {
        StartFragment fragment = new StartFragment();
        Bundle args = new Bundle();
        args.putInt(RESOURCE_ID, resId);
        args.putString(DESCRIPTION_ID, desc);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_start, container, false);
        ButterKnife.bind(this, rootView);
        descText.setText(getArguments().getString(DESCRIPTION_ID));
        heroImage.setImageResource(getArguments().getInt(RESOURCE_ID));
        return rootView;
    }
}
