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
        drawHeart(canvas);
        drawShit(canvas);
    }

    private void drawShit(Canvas canvas) {
        try {
            Paint paint = new Paint();
            paint.setColor(Color.RED);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(2);

            String path = "M495.4 899.6c-94.7 0-184.1-18.9-251.9-53.3-74-37.6-114.7-89.6-114.7-146.4 0-35.8 16.6-70.4 48-100.5-7.1-17.5-10.7-35.2-10.7-52.7 0-34.1 13.8-67.2 39.8-95.7 18.7-20.6 43.9-38.7 73.4-52.8-0.9-7.2-1.3-14.5-1.3-21.7 0-26.2 8.1-51.5 24.1-75.4 14.3-21.4 34.2-40.6 59.2-57.1 48.2-31.9 110.8-50.2 171.6-50.2 13.7 0 25.6 8.3 29.7 20.6 4.2 12.6-1.1 26.5-12.8 33.7-9.1 5.7-23.1 19.1-23.1 26.3 0 14.3 14.6 21.5 43.4 21.5 78.8 0 143 59.1 143 131.7 0 9.9-1.2 19.8-3.6 29.5 73.6 28.3 115.5 73 115.5 123.8 0 19.2-6.3 46-33.8 71.6 58.5 33 71.2 71.5 71.2 98.6 0 34.9-20.7 83.4-119.1 116.2-63.4 20.7-151.3 32.3-247.9 32.3z m37.2-681c-56 0-113.6 16.8-157.8 46-22.2 14.7-39.7 31.5-52.2 50.1-13.2 19.7-19.9 40.4-19.9 61.6 0 8.9 0.8 18 2.4 26.9l1.7 9.7-9 4c-30 13.2-55.5 30.8-73.7 50.8-21.8 23.9-33.3 51.2-33.3 78.9 0 16.6 4.1 33.6 12.1 50.5l4 8.5-7.1 6.2c-30.3 26.6-46.3 57-46.3 88 0 47 35.9 91.1 101 124.1 64.3 32.7 149.8 50.6 240.6 50.6 94 0 179.1-11.1 239.6-31.3 66.7-22.2 102-54.2 102-92.4 0-30.6-22.7-58.5-65.7-80.7-2.6-1.3-5.4-2.8-8.4-4.2l-19.3-9.1 17.3-12.4c25.7-18.4 38.8-39.9 38.8-63.9 0-20.4-9.3-39.7-27.7-57.6-19-18.5-47.4-34.6-82.3-46.7l-11.9-4.1 4.3-11.9c3.8-10.7 5.8-21.8 5.8-32.9 0-58.8-52.9-106.7-118-106.7-17.4 0-31.8-2.6-42.8-7.6-16.5-7.7-25.6-21.5-25.6-38.9 0-25.5 33.5-46.7 34.9-47.6 1.5-0.9 2.8-2.6 2.2-4.5-0.4-1.9-2.9-3.4-5.7-3.4z";
            SvgPathToAndroidPath svgParse = new SvgPathToAndroidPath();
            Path parser = svgParse.parser(path);
            canvas.drawPath(parser, paint);
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
        canvas.drawPath(path, paint);

        Path path2 = new Path();
        path2.moveTo(width / 2, height / 4);
        path2.cubicTo(width / 7, height / 9, width / 21, (height * 2) / 5,
                width / 2, (height * 7) / 12);
        canvas.drawPath(path2, paint);

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
