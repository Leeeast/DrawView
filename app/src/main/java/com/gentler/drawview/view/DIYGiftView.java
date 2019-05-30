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
import android.graphics.PathMeasure;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.gentler.drawview.R;
import com.gentler.drawview.model.DIYGiftModel;
import com.gentler.drawview.utils.ImageUtils;
import com.gentler.drawview.utils.ScreenUtils;
import com.gentler.drawview.utils.SvgPathToAndroidPath;

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
    CopyOnWriteArrayList<DIYGiftModel> mDiyGiftModelList = new CopyOnWriteArrayList<>();
    //    private List<DIYGiftModel> mDiyGiftModelList = new ArrayList<>();
    private int mDownX;
    private int mDownY;
    private float mLastX;
    private float mLastY;
    private int moveDistance;
    private int backgroundColor = Color.argb(204, 0, 0, 0);
    private int mWidth;
    private int mHeight;
    private Paint mBorderPaint;
    private int mBorderPadding = 3;
    private Path mBorderPath, mGiftPath;
    private PathMeasure mPathMeasure;
    private float[] mPos = new float[2], mTan = new float[2];
    private List<Path> mGiftPaths = new ArrayList<>();
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
        mGiftPaths.clear();
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
        Iterator<DIYGiftModel> iterator = mDiyGiftModelList.iterator();
        while (iterator.hasNext()) {
            DIYGiftModel point = iterator.next();
            if (null != point) {
                Matrix matrix = new Matrix();
                matrix.postTranslate(point.getX() - mScaledBitmap.getWidth() / 2, point.getY() - mScaledBitmap.getHeight() / 2);
                canvas.drawBitmap(mScaledBitmap, matrix, mPaint);
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
        drawBorder(canvas);
        drawSomething(canvas);
//        drawHeart(canvas);
//        drawShit(canvas);
        drawLip(canvas);
    }

    private void drawLip(Canvas canvas) {
        try {
            Paint paint = new Paint();
            paint.setColor(Color.RED);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(2);

            String path = "M965.014,474.716 C953.875,458.004,930.967,441.82,901.458,421.041 C871.428,399.897,834.053,373.592,803.189,341.18 L796.024,333.636 C748.695,283.763,689.806,221.691,631.974,221.691 C619.433,221.691,606.366,228.229,591.244,235.798 C571.881,245.484,549.937,256.469,525.929,257.028 C501.129,256.469,479.185,245.484,459.832,235.798 C444.71,228.229,431.643,221.691,419.101,221.691 C361.295,221.691,302.432,283.712,255.129,333.542 L247.878,341.181 C217.246,373.353,180.602,399.52,151.149,420.552 C107.743,451.554,76.3801,473.953,81.0689,502.752 C82.1853,509.649,87.1944,514.962,93.4734,516.867 C99.3062,524.471,107.248,529.927,116.32,535.972 C136.673,549.529,170.727,572.384,226.211,645.286 C236.086,658.257,264.834,691.831,318.45,720.158 C374.811,749.938,441.149,766.139,515.677,767.404 C516.624,767.568,517.596,768.793,518.567,768.793 C518.636,768.793,518.713,768.793,518.782,768.793 L527.238,767.541 L535.969,766.382 C536.959,766.331,537.99,766.37,538.98,766.207 C713.721,763.429,796.18,689.127,829.848,644.922 C882.294,576.055,917.639,552.104,938.757,537.781 C950.572,529.769,960.646,522.799,967.213,512.201 C969.648,509.687,971.385,506.45,971.986,502.747 C972.036,502.435,972.026,502.138,972.069,501.828 C972.953,499.11,973.7,496.218,974.214,493.02 C975.439,485.368,971.503,478.146,965.014,474.716 Z " +
                    "M171.63,449.222 L280.686,357.8 C810.93,400.406,849.881,427.82,881.174,449.85 C892.445,457.786,905.724,467.142,916.537,475.697 C892.591,473.893,863.364,468.589,832.925,463.003 C789.476,455.029,744.548,446.789,705.048,446.789 C686.364,446.789,657.754,453.507,624.628,461.283 C589.936,469.43,550.616,478.659,525.602,478.737 C500.467,478.659,461.147,469.43,426.464,461.283 C393.329,453.507,364.719,446.789,346.035,446.789 C306.552,446.789,261.529,455.03,217.995,462.995 C188.313,468.423,159.742,473.621,136.117,475.569 C146.934,466.869,160.299,457.317,171.63,449.222 Z";
            SvgPathToAndroidPath svgParse = new SvgPathToAndroidPath();
            Path parser = svgParse.parser(path);
            canvas.drawPath(parser, paint);
//            mGiftPaths.add(parser);
//            calculatePlotPoint();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void drawShit(Canvas canvas) {
        try {
            Paint paint = new Paint();
            paint.setColor(Color.RED);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(2);

            String path = "M495.4,899.6 C400.7,899.6,311.3,880.7,243.5,846.3 C169.5,808.7,128.8,756.7,128.8,699.9 C128.8,664.1,145.4,629.5,176.8,599.4 C169.7,581.9,166.1,564.2,166.1,546.7 C166.1,512.6,179.9,479.5,205.9,451 C224.6,430.4,249.8,412.3,279.3,398.2 C278.4,391,278,383.7,278,376.5 C278,350.3,286.1,325,302.1,301.1 C316.4,279.7,336.3,260.5,361.3,244 C409.5,212.1,472.1,193.8,532.9,193.8 C546.6,193.8,558.5,202.1,562.6,214.4 C566.8,227,561.5,240.9,549.8,248.1 C540.7,253.8,526.7,267.2,526.7,274.4 C526.7,288.7,541.3,295.9,570.1,295.9 C648.9,295.9,713.1,355,713.1,427.6 C713.1,437.5,711.9,447.4,709.5,457.1 C783.1,485.4,825,530.1,825,580.9 C825,600.1,818.7,626.9,791.2,652.5 C849.7,685.5,862.4,724,862.4,751.1 C862.4,786,841.7,834.5,743.3,867.3 C679.9,888,592,899.6,495.4,899.6 Z ";
            SvgPathToAndroidPath svgParse = new SvgPathToAndroidPath();
            Path parser = svgParse.parser(path);
            canvas.drawPath(parser, paint);
            mGiftPaths.add(parser);
            calculatePlotPoint();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void drawHeart(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);

        Path path = new Path();
        int width = ScreenUtils.getScreenWidth(getContext());
        int height = (int) (width * 1.6);

        path.moveTo(width / 2, height / 4);
        path.cubicTo((width * 6) / 7, height / 9, (width * 13) / 13,
                (height * 2) / 5, width / 2, (height * 7) / 12);
        //canvas.drawPath(path, paint);

        Path path2 = new Path();
        path2.moveTo(width / 2, height / 4);
        path2.cubicTo(width / 7, height / 9, width / 21, (height * 2) / 5,
                width / 2, (height * 7) / 12);
        //canvas.drawPath(path2, paint);

        mGiftPaths.add(path);
        mGiftPaths.add(path2);
        calculatePlotPoint();

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
        int action = event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mGiftPath = new Path();
                mGiftPaths.add(mGiftPath);
                mGiftPath.moveTo(event.getX(), event.getY());
                mGiftPath.lineTo(event.getX() + 0.01f, event.getY() + 0.01f);
                mLastX = event.getX();
                mLastY = event.getY();
                calculatePlotPoint();
                return true;
            case MotionEvent.ACTION_MOVE:
                mGiftPath.quadTo(
                        mLastX,
                        mLastY,
                        (event.getX() + mLastX) / 2,
                        (event.getY() + mLastY) / 2);
                mLastX = event.getX();
                mLastY = event.getY();
                calculatePlotPoint();
                break;
        }
        return super.onTouchEvent(event);
    }

    public void calculatePlotPoint() {
        mDiyGiftModelList.clear();
        ListIterator<Path> iterator = mGiftPaths.listIterator();
        while (iterator.hasNext()) {
            Path path = iterator.next();
            mPathMeasure = new PathMeasure(path, false);
            mPathMeasure.setPath(path, false);
            float length = mPathMeasure.getLength();
            int num = (int) Math.ceil(length / mDisplaySize);
            for (int i = 0; i < num; i++) {
                boolean result = mPathMeasure.getPosTan(mDisplaySize * i, mPos, mTan);
                if (result) {
                    addPoint(mPos[0], mPos[1]);
                    Log.e(TAG, "[x,y]:[" + mPos[0] + "," + mPos[1] + "]");
                }
            }
        }
    }

    public void addPoint(float x, float y) {
        DIYGiftModel diyGiftModel = new DIYGiftModel();
        diyGiftModel.setX(Math.round(x));
        diyGiftModel.setY(Math.round(y));
        mDiyGiftModelList.add(diyGiftModel);
        invalidate();
    }

    public void reset() {
        mDiyGiftModelList.clear();
        mGiftPaths.clear();
        postInvalidate();
    }
}
