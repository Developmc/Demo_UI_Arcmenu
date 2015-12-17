package com.example.multiable.demo_ui_arcmenu.MyView;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

import com.example.multiable.demo_ui_arcmenu.R;

/**
 * Created by macremote on 2015/12/16.
 */
public class ArcMenu extends ViewGroup implements View.OnClickListener{
    private static final String TAG = "ArcMenu" ;
    /**
     * 设置菜单的位置，四种选择
     */
    public enum Position
    {
        LEFT_TOP,RIGHT_TOP,RIGHT_BOTTOM,LEFT_BOTTOM
    }

    /**
     * 动画持续时间，默认300
     */
    private int durationTime ;
    /**
     * 默认菜单位置
     */
    private Position mPosition ;
    /**
     * 默认菜单半径是100
     */
    private int mRadius = 100  ;
    /**
     * 是否按照顺序展开，默认是yes
     */
    private boolean isOrder ;
    /**
     * 点击的按钮
     */
    private View mButton ;

    /**
     * 当前菜单的状态
     */
    private enum Status{
        OPEN,CLOSE
    }

    /**
     * 当前菜单的默认状态
     */
    private Status mCurrentStatus = Status.CLOSE ;
    /**
     * 回调接口对象
     */
    private OnMenuItemClickListener onMenuItemClickListener;

    /**
     * 自定义回调接口
     */
    public interface OnMenuItemClickListener{
        void onClick(View view,int position) ;
    }

    public ArcMenu(Context context) {
        this(context,null);
    }

    public ArcMenu(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ArcMenu(Context context,AttributeSet attrs,int defStyle){
        super(context,attrs,defStyle);
        //单位转换，dp->px
        mRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                mRadius,getResources().getDisplayMetrics()) ;
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.ArcMenu,defStyle,0) ;
        int n = a.getIndexCount() ;
        for(int i=0;i<n;i++){
            int attr = a.getIndex(i) ;
            switch (attr){
                case R.styleable.ArcMenu_position:
                    //四种情况
                    //如果没哟初始化就默认是0
                    int val = a.getInt(attr,0) ;
                    switch (val){
                        case 0:
                            mPosition = Position.LEFT_TOP;
                            break ;
                        case 1:
                            mPosition = Position.RIGHT_TOP;
                            break;
                        case 2:
                            mPosition=Position.RIGHT_BOTTOM;
                            break;
                        case 3:
                            mPosition = Position.LEFT_BOTTOM;
                            break;
                    }
                    break;
                case R.styleable.ArcMenu_radius:
                    //dp convert to px
                    mRadius = a.getDimensionPixelSize(attr,(int)TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,100f,getResources().getDisplayMetrics()
                    ));
                    break;
                case R.styleable.ArcMenu_time:
                    //默认持续时间为300
                    durationTime = a.getInt(attr,300) ;
                    break;
                case R.styleable.ArcMenu_is_order:
                    //默认是按顺序
                    isOrder = a.getBoolean(attr,false) ;
                    break;
            }
        }
        a.recycle();
    }
    /**确定子child的位置\
     * {@inheritDoc}
     * @param changed
     * @param l
     * @param t
     * @param r
     * @param b
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if(changed){
            //先设置第一个button的位置
            layoutButton();
            int count = getChildCount() ;
            //按照不同的情况设置所有childView的位置
            for(int i=0;i<count-1;i++){
                //第一个位置已设置好
                View child = getChildAt(i+1) ;
                //设置为隐藏
                child.setVisibility(View.GONE);
                //计算child的位置
                int cl = (int) (mRadius * Math.sin(Math.PI / 2 / (count - 2)
                        * i));
                int ct = (int) (mRadius * Math.cos(Math.PI / 2 / (count - 2)
                        * i));
                //获取child的宽高
                int cWidth = child.getMeasuredWidth() ;
                int cHeight = child.getMeasuredHeight() ;
                //分情况计算位置
                //左下，右下
                if(mPosition==Position.LEFT_BOTTOM||mPosition==Position.RIGHT_BOTTOM){
                    ct = getMeasuredHeight()-cHeight-ct ;
                }
                if(mPosition==Position.RIGHT_TOP||mPosition==Position.RIGHT_BOTTOM){
                    cl = getMeasuredWidth()-cWidth-cl;
                }
                child.layout(cl,ct,cl+cWidth,ct+cHeight);
            }
        }
    }

    /**计算子child的大小
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //获取子view的数量
        int count = getChildCount() ;
        for(int i=0;i<count;i++){
            //measure child
            getChildAt(i).measure(MeasureSpec.UNSPECIFIED,MeasureSpec.UNSPECIFIED);
        }
    }

    /**
     * 初始化第一个按钮的布局,第一个按钮是初始化点击的按钮
     */
    private void layoutButton(){
        View cButton = getChildAt(0) ;
        //设置点击事件
        cButton.setOnClickListener(this);
        //第一个按钮布局的位置
        int l=0;
        int t = 0;
        //获取该view的宽高
        int width = cButton.getMeasuredWidth() ;
        int height = cButton.getMeasuredHeight() ;
        switch(mPosition) {
            case LEFT_TOP:
                l=0;
                t=0;
                break ;
            case LEFT_BOTTOM:
                l=0;
                //这个getMeasuredHeight是整个viewGroup的heigh
                t = getMeasuredHeight()-height;
                break;
            case RIGHT_TOP:
                l=getMeasuredWidth()-width;
                t = 0;
                break;
            case RIGHT_BOTTOM:
                l=getMeasuredWidth()-width;
                t = getMeasuredHeight()-height;
                break;
        }
        //设置按钮的位置
        cButton.layout(l,t,l+width,t+height);
    }
    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
//        mButton = findViewById(R.id.id_button);
        if(mButton==null){
            mButton = getChildAt(0) ;
        }
        rotateView(mButton, 0f, 360f, durationTime);
        toggleMenu(durationTime) ;
    }

    /**第一个按钮的旋转动画(改进：可将这个动画效果用xml实现)
     * @param view
     * @param fromDegrees
     * @param toDegrees
     * @param durationMillis
     */
    public static void rotateView(View view,float fromDegrees,float toDegrees ,int durationMillis){
        //以中心点旋转
        RotateAnimation rotate = new RotateAnimation(fromDegrees,toDegrees,
                Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f) ;
        rotate.setDuration(durationMillis);
        rotate.setFillAfter(true);
        view.startAnimation(rotate);
    }

    /**切换菜单状态：打开or关闭,设置时间，等待动画
     * @param durationMillis
     */
    public void toggleMenu(int durationMillis){
        int count = getChildCount();
        for(int i=0;i<count-1;i++){
            final View childView = getChildAt(i+1);
            childView.setVisibility(View.VISIBLE);
            int xflag = 1 ;
            int yflag = 1 ;
            if(mPosition==Position.LEFT_TOP||mPosition==Position.LEFT_BOTTOM){
                xflag = -1;
            }
            if(mPosition == Position.LEFT_TOP || mPosition == Position.RIGHT_TOP){
                yflag = -1;
            }
            //计算child的位置，从而实现动画
            int cl = (int) (mRadius * Math.sin(Math.PI / 2 / (count - 2)
                    * i));
            int ct = (int) (mRadius * Math.cos(Math.PI / 2 / (count - 2)
                    * i));
            //设置动画
            AnimationSet animset = new AnimationSet(true);
            Animation animation = null;
            //from "CLOSE" to "Open"
            if (mCurrentStatus == Status.CLOSE)
            {// to open
                animset.setInterpolator(new OvershootInterpolator(2F));
                animation = new TranslateAnimation(xflag * cl, 0, yflag * ct, 0);
                childView.setClickable(true);
                childView.setFocusable(true);
            } else
            {// to close
                animation = new TranslateAnimation(0f, xflag * cl, 0f, yflag
                        * ct);
                childView.setClickable(false);
                childView.setFocusable(false);
            }
            //监听动画，在动画结束时，显示/隐藏控件
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }
                @Override
                public void onAnimationEnd(Animation animation) {
                    if(mCurrentStatus==Status.CLOSE){
                        childView.setVisibility(View.GONE);
                    }
                }
                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            animation.setFillAfter(true);
            //设置动画时间
            animation.setDuration(durationMillis);
            // 为动画设置一个开始延迟时间，纯属好看，可以不设
            RotateAnimation rotate = new RotateAnimation(0, 720,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            rotate.setDuration(durationMillis);
            rotate.setFillAfter(true);
            //是否按顺序展开
            if(isOrder){
                //这里有两个动画，一个是位移，一个是旋转，应同时设置延迟时间
                rotate.setStartOffset(i * 100);
                animation.setStartOffset(i * 100);
            }
            animset.addAnimation(rotate);
            animset.addAnimation(animation);
            //开始动画
            childView.startAnimation(animset);
            final int index = i+1;
            childView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onMenuItemClickListener!=null){
                        onMenuItemClickListener.onClick(childView,index-1);
                    }
                    //点击菜单的动画 TODO
                    menuItemAnin(index-1) ;
                    changeStatus();
                }
            });
        }
        changeStatus();
    }

    /**
     * 更改菜单的状态
     */
    private void changeStatus() {
        mCurrentStatus = (mCurrentStatus == Status.CLOSE ? Status.OPEN
                : Status.CLOSE);
    }
        /**开始菜单动画，点击menuItem放大消失，其他的缩小消失
         * @param item
         */
    private void menuItemAnin(int item){
        for(int i=0;i<getChildCount()-1;i++){
            View childView = getChildAt(i + 1);
            if (i == item)
            {
                childView.startAnimation(scaleBigAnim(300));
            } else
            {
                childView.startAnimation(scaleSmallAnim(300));
            }
            childView.setClickable(false);
            childView.setFocusable(false);
        }
    }
    /**
     * 缩小消失
     * @param durationMillis
     * @return
     */
    private Animation scaleSmallAnim(int durationMillis)
    {
        Animation anim = new ScaleAnimation(1.0f, 0f, 1.0f, 0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        anim.setDuration(durationMillis);
        anim.setFillAfter(true);
        return anim;
    }
    /**
     * 放大，透明度降低
     * @param durationMillis
     * @return
     */
    private Animation scaleBigAnim(int durationMillis)
    {
        AnimationSet animationset = new AnimationSet(true);

        Animation anim = new ScaleAnimation(1.0f, 4.0f, 1.0f, 4.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        Animation alphaAnimation = new AlphaAnimation(1, 0);
        animationset.addAnimation(anim);
        animationset.addAnimation(alphaAnimation);
        animationset.setDuration(durationMillis);
        animationset.setFillAfter(true);
        return animationset;
    }
    public Position getmPosition()
    {
        return mPosition;
    }

    public void setmPosition(Position mPosition)
    {
        this.mPosition = mPosition;
    }

    public int getmRadius()
    {
        return mRadius;
    }

    public void setmRadius(int mRadius)
    {
        this.mRadius = mRadius;
    }

    public Status getmCurrentStatus()
    {
        return mCurrentStatus;
    }

    public void setmCurrentStatus(Status mCurrentStatus)
    {
        this.mCurrentStatus = mCurrentStatus;
    }

    public OnMenuItemClickListener getOnMenuItemClickListener()
    {
        return onMenuItemClickListener;
    }

    public void setOnMenuItemClickListener(
            OnMenuItemClickListener onMenuItemClickListener)
    {
        this.onMenuItemClickListener = onMenuItemClickListener;
    }
    public int getDurationTime(){
        return durationTime ;
    }
    public void setDurationTime(int durationTime){
        this.durationTime = durationTime ;
    }
    public boolean getIsOder(){
        return isOrder ;
    }
    public void setOrder(boolean isOrder){
        this.isOrder = isOrder ;
    }
}
