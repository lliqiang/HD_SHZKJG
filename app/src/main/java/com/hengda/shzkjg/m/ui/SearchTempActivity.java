package com.hengda.shzkjg.m.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.library.AutoFlowLayout;
import com.example.library.FlowAdapter;
import com.hengda.shzkjg.m.R;
import com.hengda.shzkjg.m.base.AppConfig;
import com.hengda.shzkjg.m.base.BaseActivity;

import java.util.Arrays;

public class SearchTempActivity extends BaseActivity {
    private ImageView mIvBackCommon;
    private EditText mEtSearch;
    private ImageView mIvScan;
    private AutoFlowLayout mAutoFlow;
    private String[] mData = {"Java", "C++", "Python", "JS", "Ruby"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();

        mAutoFlow.setAdapter(new FlowAdapter(Arrays.asList(mData)) {
            @Override
            public View getView(int position) {
                View item = getLayoutInflater().inflate(R.layout.special_item, null);
                TextView tvAttrTag = (TextView) item.findViewById(R.id.tv_attr_tag);
                tvAttrTag.setText(mData[position]);
                return item;
            }
        });
        mAutoFlow.setOnItemClickListener(new AutoFlowLayout.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                Toast.makeText(SearchTempActivity.this, mData[position], Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initView() {
        mIvBackCommon = (ImageView) findViewById(R.id.iv_back_common);
        mEtSearch = (EditText) findViewById(R.id.et_search);
        mIvScan = (ImageView) findViewById(R.id.iv_scan);
        mAutoFlow = (AutoFlowLayout) findViewById(R.id.auto_flow);
        mIvBackCommon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mIvScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
