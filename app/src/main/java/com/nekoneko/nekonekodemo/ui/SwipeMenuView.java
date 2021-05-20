package com.nekoneko.nekonekodemo.ui;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;


/**
 * 滑动菜单
 * Created by 想法的猫 on 2017/6/2 0002.
 */

public class SwipeMenuView extends FrameLayout implements ValueAnimator.AnimatorUpdateListener {
    public int ANIMATION_DURATION = 200;//动画时长
    public static final int DEFAULT_ANIMATION_DURATION = 200;//动画时长
    public static final int ANIMATION_DISTANCE = 200;//默认的动画距离
    String TAG = "SwipeMenuView";
    View menuChild;//作为菜单的子view
    boolean swipeMenuViewEnable = true;//开关
    boolean menuVisibleOnUp;//菜单是否可见
    ValueAnimator valueAnimator = ValueAnimator.ofInt();
    private String menuTag = "menu";//view的 tag如果等于menuTag则视为菜单
    int touchSlop;
    final int SPEED_VALUE = 200;//滑动速度阀值 大于阀值 则滑动至边缘
    Rect menuRect = new Rect();

    public String getMenuTag() {
        return menuTag;
    }

    public void setMenuTag(String menuTag) {
        this.menuTag = menuTag;
        requestLayout();
    }

    public boolean isSwipeMenuViewEnable() {
        return swipeMenuViewEnable;
    }

    public void setSwipeMenuViewEnable(boolean swipeMenuViewEnable) {
        this.swipeMenuViewEnable = swipeMenuViewEnable;
        if (menuVisibleOnUp) {
            scrollToRight();
        }
    }

    public SwipeMenuView(Context context) {
        super(context);
    }

    public SwipeMenuView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAnimator();
    }

    public SwipeMenuView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAnimator();
    }


    @Override
    protected void measureChildWithMargins(View child, int parentWidthMeasureSpec, int widthUsed, int parentHeightMeasureSpec, int heightUsed) {
        super.measureChildWithMargins(child, parentWidthMeasureSpec, widthUsed, parentHeightMeasureSpec, heightUsed);
        findMenuChild(child);
    }

    /**
     * 找到标签为menu的view 作为隐藏菜单
     *
     * @param child
     */
    private void findMenuChild(View child) {
        if (menuTag.equals(child.getTag())) {
            menuChild = child;
            ANIMATION_DURATION = (int) ((float) child.getMeasuredWidth() / ANIMATION_DISTANCE * DEFAULT_ANIMATION_DURATION);
            ANIMATION_DURATION = (int) lerp(ANIMATION_DURATION, DEFAULT_ANIMATION_DURATION);
        }
    }

    /**
     * 隐藏菜单
     */
    private void hideMenu() {
        if (menuChild != null) {
            ViewGroup viewGroup = (ViewGroup) menuChild.getParent();
            menuChild.layout(viewGroup.getWidth(), menuChild.getTop(), viewGroup.getWidth() + menuChild.getWidth(), menuChild.getBottom());
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        hideMenu();
    }

    float lastX;
    float downX;
    VelocityTracker velocityTracker;
    boolean menuVisibleOnDown = false;//down事件时菜单是否可见
    boolean scrollEnable = false;//当滑动距离大于系统的滑动常量 才算滑动


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (!swipeMenuViewEnable) {
            return super.dispatchTouchEvent(event);
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = event.getX();
                downX = lastX;
                scrollEnable = false;
                menuVisibleOnDown = menuVisibleOnUp;
                trackEvent(event);
                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = event.getX() - lastX;
                //当滑动距离大于滑动常量时 才算做滑动处理
                if (!scrollEnable && Math.abs(event.getX() - downX) < touchSlop) {
                    break;
                }
                scrollEnable = true;
                velocityTracker.addMovement(event);
                lastX = event.getX();
                scrollBy((int) -moveX, 0);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                //如果down时 菜单显示 并且up时 并未滑动 则滑动回左边
                if (needHideMenu()) {
                    break;
                }
                calculateSpeed();
                break;
        }
        //如果up时 菜单滚动了 则需要模拟一个cancel事件 分发下去 以免child的drawableState混乱
        event = checkReplaceMotionEvent(event);
        requestDisallowInterceptTouchEvent(scrollEnable);
        boolean childConsumed = super.dispatchTouchEvent(event);//如果child并没有消费掉事件 则自己消费
        if (menuVisibleOnDown && !childConsumed) {
            return true;
        }
        return childConsumed;
    }

    /**
     * 替换event action为cancel
     *
     * @param event
     * @return
     */
    private MotionEvent checkReplaceMotionEvent(MotionEvent event) {
        if (scrollEnable) {
            event = MotionEvent.obtain(event.getDownTime(), event.getEventTime(), MotionEvent.ACTION_CANCEL, event.getX(), event.getY(), 0);
        }
        return event;
    }

    /**
     * 计算event速度
     */
    private void calculateSpeed() {
        velocityTracker.computeCurrentVelocity(1000);
        float speed = velocityTracker.getXVelocity();
        if (speed < -SPEED_VALUE) {
            scrollToLeft();
        } else if (speed > SPEED_VALUE) {
            calculateRightAnimDuration();
            scrollToRight();
        } else {
            calculateLeftAnimDuration();
            autoScrollOnUp();
        }
    }

    /**
     * 根据event计算速度
     *
     * @param event
     */
    private void trackEvent(MotionEvent event) {
        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain();
        } else {
            velocityTracker.clear();
        }
        velocityTracker.addMovement(event);
    }

    /**
     * 如果 down时在左侧并且up时依然在左侧 则判断为隐藏菜单
     *
     * @return
     */
    private boolean needHideMenu() {
        if (menuVisibleOnDown && onLeftEdge()) {
            calculateRightAnimDuration();
            scrollToRight();
            return true;
        }
        return false;
    }

    /**
     * 菜单是否展开 位于左边
     *
     * @return
     */
    private boolean onLeftEdge() {
        int menuChildWith = menuChild.getWidth();
        return getScrollX() == menuChildWith;
    }

    /**
     * up事件时自动滚动到某一边
     */
    private void autoScrollOnUp() {
        int menuChildWith = menuChild.getWidth();
        if (getScrollX() < 3 * menuChildWith / 5) {
            calculateRightAnimDuration();
            scrollToRight();
        } else {
            calculateLeftAnimDuration();
            scrollToLeft();
        }

    }

    private void scrollToRight() {
        valueAnimator.setIntValues(getScrollX(), 0);
        valueAnimator.start();
        menuVisibleOnUp = false;
    }

    private void calculateLeftAnimDuration() {
        int menuChildWith = menuChild.getWidth();
        long duration = ANIMATION_DURATION - (long) (ANIMATION_DURATION * (float) getScrollX() / menuChildWith);
        duration = (long) lerp(ANIMATION_DURATION, duration);
        valueAnimator.setDuration(duration);
    }

    private double lerp(float m, float n) {
        return (Math.abs(m) + Math.abs(n)) / 2;
    }

    private void calculateRightAnimDuration() {
        int menuChildWith = menuChild.getWidth();
        long duration = ANIMATION_DURATION * getScrollX() / menuChildWith;
        duration = (long) lerp(ANIMATION_DURATION, duration);
        valueAnimator.setDuration(duration);
    }

    private void scrollToLeft() {
        int menuChildWith = menuChild.getWidth();
        valueAnimator.setIntValues(getScrollX(), menuChildWith);
        valueAnimator.start();
        menuVisibleOnUp = true;
    }

    /**
     * 初始化动画
     */
    private void initAnimator() {
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.addUpdateListener(this);
        touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    @Override
    public void scrollBy(int x, int y) {
        if (menuChild == null) {
            return;
        }
        super.scrollBy(x, y);
        leftEdgeCheck();
        rightEdgeCheck();
    }


    /**
     * 左边界判断
     */
    private void leftEdgeCheck() {
        if (getScrollX() < 0) {
            scrollTo(0, 0);
        }
    }

    /**
     * 右边界判断
     */
    private void rightEdgeCheck() {
        int menuChildWith = menuChild.getWidth();
        if (getScrollX() > menuChildWith) {
            scrollTo(menuChildWith, 0);
        }
    }

    /**
     * 属性动画回调
     *
     * @param animation
     */
    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        int i = (int) animation.getAnimatedValue();
        scrollTo(i, 0);
        if (i == 0) {
            if (swipeMenuListener != null) {
                swipeMenuListener.onSwipeMenuClose();
                swipeMenuListener = null;
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (velocityTracker != null) {
            velocityTracker.recycle();
            velocityTracker = null;
        }
    }

    public void setSwipeMenuListener(SwipeMenuListener swipeMenuListener) {
        this.swipeMenuListener = swipeMenuListener;
    }

    SwipeMenuListener swipeMenuListener;

    public interface SwipeMenuListener {
        /**
         * 滑动菜单关闭时回调 如果直接在onClick时操作会导致动画未结束 就开始界面操作 导致动画不流畅 所以在onClick中 等待close回调 再处理
         */
        void onSwipeMenuClose();
    }
}
