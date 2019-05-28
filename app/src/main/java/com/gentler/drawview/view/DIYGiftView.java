package com.gentler.drawview.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.gentler.drawview.R;
import com.gentler.drawview.model.DIYGiftModel;
import com.gentler.drawview.utils.ImageUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by admin on 2017/11/8.
 */

public class DIYGiftView extends View {
    private static final String TAG = DIYGiftView.class.getSimpleName();
    private Context mContext;
    private Paint mPaint;
    private Bitmap mBitmap;
    private Bitmap mScaledBitmap;
    private DIYGiftModel mDIYGiftModel;
    private int mBitmapResId;
    //    CopyOnWriteArrayList<DIYGiftModel> mDiyGiftModelList = new CopyOnWriteArrayList<>();
    private List<DIYGiftModel> mDiyGiftModelList = new ArrayList<>();
    private int mDownX;
    private int mDownY;
    private int mLastX;
    private int mLastY;
    private int moveDistance;
    private int backgroundColor = Color.argb(204, 0, 0, 0);
    private int mWidth;
    private int mHeight;
    private Paint mBorderPaint;
    private int mBorderPadding = 3;
    private Path mBorderPath;
    private float mDisplaySize;
    private int mDistance;


    public DIYGiftView(Context context) {
        this(context, null);
    }

    public DIYGiftView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DIYGiftView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        mDisplaySize = mContext.getResources().getDimension(R.dimen.gesture_gift_size);
        init();
    }

    private void init() {
        initBitmap();
        initPaint();
        initView();
    }

    private void initView() {
        setFocusable(true);
        setFocusableInTouchMode(true);
        setKeepScreenOn(true);
        setBackgroundResource(R.color.colorPrimaryDark);
        setBackgroundColor(backgroundColor);
    }

    public void setBitmapSource(int bitmapSource) {
        mDiyGiftModelList.clear();
        this.mBitmapResId = bitmapSource;
        if (null != mBitmap) {
            mBitmap = BitmapFactory.decodeResource(getResources(), bitmapSource);
            float scaleW = mDisplaySize / mBitmap.getWidth();
            float scaleH = mDisplaySize / mBitmap.getHeight();
            mScaledBitmap = ImageUtils.scale(mBitmap, scaleW, scaleH);
            mDistance = mScaledBitmap.getWidth();
        }
    }

    private void initBitmap() {
        mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.heart_small);
        mScaledBitmap = ImageUtils.scale(mBitmap, 1f, 1f);
    }


    private void initPaint() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(mContext.getResources().getColor(R.color.colorAccent));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(3);

        mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBorderPaint.setColor(Color.parseColor("#FF979797"));
        mBorderPaint.setStrokeWidth(1f);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        PathEffect effects = new DashPathEffect(new float[]{10, 10}, 0);//设置虚线的间隔和点的长度
        mBorderPaint.setPathEffect(effects);
    }

    public void setDataList(CopyOnWriteArrayList<DIYGiftModel> dataList) {
        this.mDiyGiftModelList = dataList;
    }

    public List<DIYGiftModel> getDataList() {
        ArrayList<DIYGiftModel> diyGiftModels = new ArrayList<>();
        diyGiftModels.addAll(mDiyGiftModelList);
        return diyGiftModels;
    }


    private void drawSomething(Canvas canvas) {
//        if (mDiyGiftModelList.size() == 0) {
//            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.SRC);
//            return;
//        }
//        canvas.drawRect(mRect,mPaint);
//        canvas.drawBitmap(mScaledBitmap,100,100,mPaint);
        Iterator<DIYGiftModel> iterator = mDiyGiftModelList.iterator();
        while (iterator.hasNext()) {
            DIYGiftModel point = iterator.next();
            if (null != point) {
                Matrix matrix = new Matrix();
                matrix.postTranslate(point.getX() - mScaledBitmap.getWidth() / 2, point.getY() - mScaledBitmap.getHeight() / 2);
                canvas.drawBitmap(mScaledBitmap, matrix, mPaint);
                //canvas.drawBitmap(mScaledBitmap, point.getX() - mScaledBitmap.getWidth() / 2, point.getY() - mScaledBitmap.getHeight() / 2, mPaint);
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ListIterator<DIYGiftModel> iterator = mDiyGiftModelList.listIterator();
        while (iterator.hasNext()) {
            iterator.next().cancelTask();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        long startTime = System.currentTimeMillis();
        drawBorder(canvas);
        drawSomething(canvas);
        long drawTime = System.currentTimeMillis() - startTime;
        postInvalidateDelayed(50 - drawTime);
    }

    private void drawBorder(Canvas canvas) {
        if (mBorderPath == null) {
            mBorderPath = new Path();
            mBorderPath.moveTo(mBorderPadding, mBorderPadding);
            mBorderPath.lineTo(mWidth - mBorderPadding, mBorderPadding);
            mBorderPath.lineTo(mWidth - mBorderPadding, mHeight - mBorderPadding);
            mBorderPath.lineTo(mBorderPadding, mHeight - mBorderPadding);
            mBorderPath.lineTo(mBorderPadding, mBorderPadding);
        }
        canvas.drawPath(mBorderPath, mBorderPaint);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mDIYGiftModel = new DIYGiftModel();
                mDIYGiftModel.setGiftRes(mBitmapResId);
                mDownX = (int) event.getX();
                mDownY = (int) event.getY();
                mLastX = mDownX;
                mLastY = mDownY;
                moveDistance = 0;
                mDIYGiftModel.setX(mDownX);
                mDIYGiftModel.setY(mDownY);
                mDiyGiftModelList.add(mDIYGiftModel);
                //postInvalidate();
                return true;
            case MotionEvent.ACTION_MOVE:
                int moveX = (int) event.getX();
                int moveY = (int) event.getY();

                int distance = (int) Math.pow(moveX - mLastX, 2) + (int) Math.pow(moveY - mLastY, 2);
                moveDistance += (int) Math.pow(distance, 0.5);
                mLastX = moveX;
                mLastY = moveY;
                Log.e(TAG, "distance:" + distance);
                Log.e(TAG, "distance开方:" + Math.pow(distance, 0.5));
                Log.e(TAG, "moveDistance:" + moveDistance);

//                Log.e(TAG,"Math.pow(90,2):"+(int)Math.pow(90,2));
                if (moveDistance >= mDistance/* - 2000&& distance <= reference + 2000*/) {
                    mDIYGiftModel = new DIYGiftModel();
                    mDIYGiftModel.setGiftRes(mBitmapResId);
                    mDIYGiftModel.setX(moveX);
                    mDIYGiftModel.setY(moveY);
                    mDiyGiftModelList.add(mDIYGiftModel);
                    //postInvalidate(mLastX, mLastY, mLastX + mScaledBitmap.getWidth(), mLastY + mScaledBitmap.getHeight());
                    moveDistance = 0;
//                    postInvalidate();
                }
//                Log.e(TAG,"action move mLastX: "+mLastX+"_mLastY:"+mLastY);
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.onTouchEvent(event);
    }

    public void reset() {
        mDiyGiftModelList.clear();
        postInvalidate();
    }
}
