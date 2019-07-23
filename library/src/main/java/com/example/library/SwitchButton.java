package com.example.library;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.support.annotation.IntDef;
import android.support.annotation.InterpolatorRes;
import android.support.annotation.Nullable;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * 仿微信滑动开关
 * Create by 陈健宇 at 2018/7/31
 */
public class SwitchButton extends View {

    private static final String TAG = SwitchButton.class.getSimpleName();
    private static final int OFFSET = 6;
    private static final int ANIM_TIME = 200;
    private static final int LINEAR = 0;//以常量速率改变
    public static final int OVER_SHOOT = 1;//向前甩一定值后再回到原来位置
    public static final int ACCELERATE = 2;//开始慢，后面加速
    public static final int DECELERATE = 3;//开始快，后面慢
    public static final int ACCELERATE_DECELERATE = 4;//开始与结束慢，中间快
    public static final int LINEAR_OUT_SLOW_IN = 5;//平滑过渡


    private int mLeftSemiCircleRadius;//左半圆半径
    private int mRightRectangleBolder;//矩形右边界x坐标
    private int mLeftRectangleBolder;//矩形左边界x坐标
    private float mCircleCenter; //小圆圆心x坐标
    private float mPreAnimatedValue;
    private int midX; //左圆圆心和右圆圆心中间的坐标
    private float startX; //按下的x坐标
    private Paint mPathWayPaint;//轨道画笔
    private Paint mCirclePaint;//小圆画笔
    private int mMinDistance;//判断是否点击的最小距离
    private boolean isAnim;
    private ValueAnimator mValueAnimator;


    private int mOpenBackground;//按钮打开后背景色
    private int mCloseBackground;//按钮关闭后的背景色
    private int mCircleColor;//圆形按钮颜色
    private float mCircleRadius;//小圆半径
    private boolean isOpen;//按钮状态
    private TimeInterpolator mInterpolator;//差值器

    private OnStatusListener mStatusListener;

    public SwitchButton(Context context) {
        super(context);
        init(context, null);
    }

    public SwitchButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SwitchButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        TypedArray typedValue = context.obtainStyledAttributes(attrs, R.styleable.SwitchButton);
        mOpenBackground = typedValue.getColor(R.styleable.SwitchButton_sb_openBackground, 0xFF4CAF50);
        mCloseBackground = typedValue.getColor(R.styleable.SwitchButton_sb_closeBackground, Color.GRAY);
        mCircleColor = typedValue.getColor(R.styleable.SwitchButton_sb_circleColor, Color.WHITE);
        mCircleRadius = typedValue.getDimension(R.styleable.SwitchButton_sb_circleRadius, 0);
        isOpen = typedValue.getInt(R.styleable.SwitchButton_sb_status, 0) != 0;
        mInterpolator = getInterpolator(typedValue.getInt(R.styleable.SwitchButton_sb_interpolator, -1));
        typedValue.recycle();

        mPathWayPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPathWayPaint.setStyle(Paint.Style.FILL);
        int pathWayColor = isOpen ? mOpenBackground : mCloseBackground;
        mPathWayPaint.setColor(pathWayColor);

        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setStyle(Paint.Style.FILL);
        mCirclePaint.setColor(mCircleColor);

        mValueAnimator = ValueAnimator.ofFloat(mLeftRectangleBolder, mRightRectangleBolder);
        mValueAnimator.setDuration(ANIM_TIME);
        mValueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isAnim = false;
                isOpen = !isOpen;
                mPreAnimatedValue = 0;
                toBolder(isOpen);
                if(mStatusListener != null){
                    if(isOpen)
                        mStatusListener.onOpen();
                    else
                        mStatusListener.onClose();
                }
            }

            @Override
            public void onAnimationStart(Animator animation) {
                isAnim = true;
            }
        });
        mValueAnimator.addUpdateListener(animation -> {
            float value = (float)animation.getAnimatedValue();
            mCircleCenter -= mPreAnimatedValue;
            mCircleCenter += value;
            mPreAnimatedValue = value;
            invalidate();
        });
        mValueAnimator.setInterpolator(mInterpolator);

        mMinDistance = new ViewConfiguration().getScaledTouchSlop();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measuredWidthMode = MeasureSpec.getMode(widthMeasureSpec);
        int measuredHeightMode = MeasureSpec.getMode(heightMeasureSpec);
        int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measureHeight = MeasureSpec.getSize(heightMeasureSpec);
        int defaultWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, getResources().getDisplayMetrics());//控件的默认宽
        int defaultHeight = (int) (defaultWidth *  0.5f);//控件的默认高
        int offset = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, OFFSET * 2 * 1.0f, getResources().getDisplayMetrics());//控件宽和高的差距不能小于12dp, 否则按钮就不好看了
        //考虑wrap_content情况
        if(measuredWidthMode == MeasureSpec.AT_MOST && measuredHeightMode == MeasureSpec.AT_MOST){
            measuredWidth = defaultWidth;
            measureHeight = defaultHeight;
        }else if(measuredHeightMode == MeasureSpec.AT_MOST){
            measureHeight = defaultHeight;
            if(measuredWidth - measureHeight < offset)
                measuredWidth = defaultWidth;
        }else if(measuredWidthMode == MeasureSpec.AT_MOST){
            measuredWidth = defaultWidth;
            if(measuredWidth - measureHeight < offset)
                measureHeight = defaultHeight;
        }else {
            //处理输入非法的宽高情况，即高度大于宽度，把它们交换就行
            if(measuredWidth < measureHeight){
                int temp = measuredWidth;
                measuredWidth = measureHeight;
                measureHeight = temp;
            }
        }
        if(Math.abs(measureHeight - measuredWidth) < offset) throw new IllegalArgumentException("layout_width cannot close to layout_height nearly, the diff must less than 12dp!");
        setMeasuredDimension(measuredWidth, measureHeight);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        //得出左圆的半径
        mLeftSemiCircleRadius = getHeight() / 2;
        //小圆的半径 = 大圆半径减OFFER
        if(!checkCircleRaduis(mCircleRadius)) mCircleRadius = mLeftSemiCircleRadius - OFFSET;
        //长方形左边的坐标
        mLeftRectangleBolder = mLeftSemiCircleRadius;
        //长方形右边的坐标
        mRightRectangleBolder = getWidth() - mLeftSemiCircleRadius;
        //小圆的圆心x坐标一直在变化
        mCircleCenter = isOpen ? mRightRectangleBolder : mLeftRectangleBolder;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //左圆
        canvas.drawCircle(mLeftRectangleBolder, mLeftSemiCircleRadius, mLeftSemiCircleRadius, mPathWayPaint);
        //矩形
        canvas.drawRect(mLeftRectangleBolder, 0, mRightRectangleBolder, getMeasuredHeight(), mPathWayPaint);
        //右圆
        canvas.drawCircle(mRightRectangleBolder, mLeftSemiCircleRadius, mLeftSemiCircleRadius, mPathWayPaint);
        //小圆
        canvas.drawCircle(mCircleCenter, mLeftSemiCircleRadius, mCircleRadius, mCirclePaint);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(isAnim) return false;
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                //开始的x坐标
                startX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                float distance = event.getX() - startX;
                mCircleCenter += distance / 10;
                //控制范围
                if (mCircleCenter > mRightRectangleBolder) {//最右
                    toBolder(true);
                } else if (mCircleCenter < mLeftRectangleBolder) {//最左
                    toBolder(false);
                }else {
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                float offset = Math.abs(event.getX() - Math.abs(startX));
                //分2种情况，1.点击 2.没滑过中点
                if (offset < mMinDistance) { //1.点击, 按下和抬起的距离小于mMinDistance确定是点击了
                    //不在动画的时候可以点击
                    if (!isAnim) {
                        if(isOpen){
                            float diff = mLeftRectangleBolder - mCircleCenter;
                            mValueAnimator.setFloatValues(0, diff);

                        }else{
                            float diff = mRightRectangleBolder - mCircleCenter;
                            mValueAnimator.setFloatValues(0, diff);
                        }
                        mValueAnimator.start();
                    }
                } else { //2.没滑过中点,回归原点
                    //滑到中间的x坐标
                    midX = getWidth() / 2;
                    if (mCircleCenter > midX) {//最右
                        toBolder(true);
                    } else {//最左
                        toBolder(false);
                    }
                }
                 break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onDetachedFromWindow() {
        if(mValueAnimator != null){
            mValueAnimator.cancel();
            mValueAnimator.removeAllUpdateListeners();
            mValueAnimator = null;
        }
        super.onDetachedFromWindow();
    }


    /**
     * 让小圆移动到边界
     * @param isOpen 是否打开状态
     */
    private void toBolder(boolean isOpen) {
        this.isOpen = isOpen;
        if(isOpen){
            mCircleCenter = mRightRectangleBolder;
            mPathWayPaint.setColor(mOpenBackground);
        }else {
            mCircleCenter = mLeftRectangleBolder;
            mPathWayPaint.setColor(mCloseBackground);
        }
        invalidate();
    }

    /**
     * 检查半径的正确性
     * @param radius 小圆半径
     * @return true表示正确
     */
    private boolean checkCircleRaduis(float radius){
        float defaultCircleRadius = mLeftSemiCircleRadius - OFFSET;
        return radius > 0 && radius < defaultCircleRadius;
    }

    /**
     * 获得差值器
     */
    private TimeInterpolator getInterpolator(int interpolator) {
        TimeInterpolator timeInterpolator;
        switch (interpolator){
            case 0:
                timeInterpolator = new LinearInterpolator();
                break;
            case 1:
                timeInterpolator = new OvershootInterpolator();
                break;
            case 2:
                timeInterpolator = new AccelerateInterpolator();
                break;
            case 3:
                timeInterpolator = new DecelerateInterpolator();
                break;
            case 4:
                timeInterpolator = new AccelerateDecelerateInterpolator();
                break;
            case 5:
                timeInterpolator = new LinearOutSlowInInterpolator();
                break;
            default:
                timeInterpolator = new LinearOutSlowInInterpolator();
                break;
        }
        return timeInterpolator;
    }

    public void open(){
        toBolder(true);
    }

    public void  close(){
        toBolder(false);
    }

    public void setOpenBackground(@ColorInt int openBackground) {
        mOpenBackground = openBackground;
        invalidate();
    }

    public void setCloseBackground(@ColorInt int closeBackground) {
        mCloseBackground = closeBackground;
        invalidate();
    }

    public void setCircleColor(@ColorInt int circleColor) {
        mCircleColor = circleColor;
        invalidate();
    }

    public void setInterpolator(@InterpolatorRes int interpolator) {
        if(mValueAnimator == null) return;
        mInterpolator = getInterpolator(interpolator);
        mValueAnimator.setInterpolator(mInterpolator);
    }

    public void setCircleRadius(float circleRadius) {
        if(!checkCircleRaduis(circleRadius)) return;
        mCircleRadius = circleRadius;
        invalidate();
    }

    public void setClickListener(OnStatusListener onStatusListener){
        this.mStatusListener = onStatusListener;
    }

    /**
     * 开关状态监听接口
     */
    public interface OnStatusListener {
        void onOpen();
        void onClose();
    }

    /**
     * 限制Interpolator的取值范围
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({LINEAR, OVER_SHOOT, ACCELERATE, DECELERATE, ACCELERATE_DECELERATE, LINEAR_OUT_SLOW_IN})
    @interface InterpolatorRes{}

}
