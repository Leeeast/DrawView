package com.gentler.drawview.ui;

import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.gentler.drawview.R;
import com.gentler.drawview.adapter.DIYGiftAdapter;
import com.gentler.drawview.model.DIYGiftModel;
import com.gentler.drawview.model.DIYGiftRes;
import com.gentler.drawview.view.DIYGiftView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by admin on 2017/11/9.
 */

public class GestureActivity extends BaseActivity {

    @BindView(R.id.btn_reset)
    AppCompatButton mBtnReset;

    @BindView(R.id.gesturegiftview)
    DIYGiftView mGestureGiftView;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.btn_save)
    AppCompatButton mBtnSave;


    private ArrayList<DIYGiftModel> mGiftModelList = new ArrayList<>();
    private DIYGiftAdapter mAdapter;


    @Override
    public int getLayoutId() {
        return R.layout.activity_gesture;
    }

    @Override
    public void initView() {
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(mActivity, 4);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
    }

    @Override
    public void initData() {
        mAdapter = new DIYGiftAdapter(getLayoutInflater());
        mRecyclerView.setAdapter(mAdapter);
        DIYGiftModel model;
        for (int i = 0; i < DIYGiftRes.mDIYGiftResArray.length; i++) {
            model = new DIYGiftModel();
            model.setGiftRes(DIYGiftRes.mDIYGiftResArray[i]);
            mGiftModelList.add(model);
        }
        mAdapter.setData(mGiftModelList);
    }

    @Override
    public void initListener() {
        mAdapter.setOnGiftItemClickListener(new DIYGiftAdapter.OnGiftItemClickListener() {
            @Override
            public void onGiftItemClick(DIYGiftAdapter.DIYGiftViewHolder holder, int position) {
                DIYGiftModel model = mGiftModelList.get(position);
                mGestureGiftView.setBitmapSource(model.getGiftRes());
            }
        });
    }

    @OnClick(R.id.btn_reset)
    public void onClickReset(View view) {
        mGestureGiftView.reset();
    }

    @OnClick(R.id.btn_save)
    public void onClickSave(View view) {
//        ArrayList<DIYGiftModel> diyGiftModels= (ArrayList<DIYGiftModel>) mDrawSurfaceView.getDataList();
//        Intent intent=new Intent(GestureActivity.this,ReappearActivity.class);
//        intent.putParcelableArrayListExtra(MyParams.INTENT_ARRAY_LIST_GIFT,diyGiftModels);
//        Rect rect=mDrawSurfaceView.getGraphicRect();
//
//        startActivity(intent);
    }
}
