
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.TabWidget;
import android.widget.TextView;

/**
 * <h3>自定义角标</h3>
 * <br>在View的右上角设置显示数字的角标
 * <br>可以自定义动画或使用预定义的淡入淡出动画
 * <br>可以设定Margin改变角标的位置
 * <br>可以设定字体大小和颜色
 * <br>可以设定是否使用粗字体
 * <br>For example：
 * <code><br>
 * CircleBadgeView cbView = new  CircleBadgeView(this, target);<br>
 * cbView.setBadgeSize(12);<br>
 * cbView.setUseBold(true);<br>
 * cbView.setBadgeBackgroundColor(Color.parseColor("#FF3B30"));<br>
 * cbView.setBadgeMargin(10);<br>
 * cbView.show();
 * </code>
 * <p>Created by mrZh on 2016/10/18.
 */
public class CircleBadgeView extends TextView {

    /*位置分类*/
    /** 常量：左上位置 */
    public static final int POSITION_TOP_LEFT = 1;
    /** 常量：右上位置 */
    public static final int POSITION_TOP_RIGHT = 2;
    /** 常量：左下位置 */
    public static final int POSITION_BOTTOM_LEFT = 3;
    /** 常量：右下位置 */
    public static final int POSITION_BOTTOM_RIGHT = 4;
    /** 常量：中间位置 */
    public static final int POSITION_CENTER = 5;
    /*单位数值*/
    /** 默认常量：边距 */
    private static final int DEFAULT_MARGIN_DIP = 5;
    /** 默认常量：内距 */
    private static final int DEFAULT_LR_PADDING_DIP = 5;
    /** 默认常量：角标圆半径 */
    private static final int DEFAULT_CORNER_RADIUS_DIP = 8;
    /** 默认常量：位置 */
    private static final int DEFAULT_POSITION = POSITION_TOP_RIGHT;
    /** 默认常量：背景颜色 */
    private static final int DEFAULT_BACKGROUND_COLOR = Color.parseColor("#FF3B30"); //Color.RED;
    /** 默认常量：文字颜色 */
    private static final int DEFAULT_TEXT_COLOR = Color.WHITE;
    /** 默认常量：文字大小 */
    private static final float DEFAULT_TEXT_SIZE = 12;
    /*静态对象*/
    /** 动画类：入场 */
    private static Animation fadeIn;
    /** 动画类：出场 */
    private static Animation fadeOut;
    /*实例对象*/
    /** 上下文：事实上也可以通过getContext()获取 */
    private Context context;
    /** 目标视图，即悬浮其上的视图 */
    private View targetView;
    /*相关参数*/
    /** 角标位置 */
    private int badgePosition;
    /** 角标水平边距 */
    private int badgeMarginH;
    /** 角标垂直边距 */
    private int badgeMarginV;
    /** 角标文字颜色 */
    private int badgeColor;
    /** 角标文字大小 */
    private float badgeSize;
    /** 是否显示 */
    private boolean isShown;
    /** 角标背景 */
    private ShapeDrawable badgeBg;
    /** 目标视图为Tab子View时的下标 */
    private int targetTabIndex;
    /** 是否使用粗体字 */
    private boolean isUseBold = false;
    /** 是否在设置单位时直接使用dip转换方法 */
    private boolean isUseDip = false;

    /**
     * 默认构造方法1
     * @param context 上下文
     */
    public CircleBadgeView(Context context) {
        this(context, (AttributeSet) null);
    }

    /**
     * 默认构造方法2
     * @param context 上下文
     * @param attrs   属性集合
     */
    public CircleBadgeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    /**
     * 默认构造方法3
     * @param context  上下文
     * @param attrs    属性集合
     * @param defStyle 默认风格
     */
    public CircleBadgeView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        this(context, attrs, defStyle, null, 0);
    }

    /**
     * 最终构造方法
     * @param context    上下文
     * @param attrs      属性集合
     * @param defStyle   默认风格
     * @param targetView 目标视图
     * @param tabIndex   tab下标
     */
    public CircleBadgeView(Context context, @Nullable AttributeSet attrs, int defStyle,
                           View targetView, int tabIndex) {
        super(context, attrs, defStyle);
        // 初始化
        init(context, targetView, tabIndex);
    }

    /**
     * 自定义构造方法1
     * @param context    上下文
     * @param targetView 目标视图
     */
    public CircleBadgeView(Context context, View targetView) {
        this(context, null, android.R.attr.textViewStyle, targetView, 0);
    }

    /**
     * 自定义构造方法2
     * @param context    上下文
     * @param targetView 目标视图
     * @param index      tab下标
     */
    @SuppressWarnings("unused")
    public CircleBadgeView(Context context, TabWidget targetView, int index) {
        this(context, null, android.R.attr.textViewStyle, targetView, index);
    }

    /**
     * 初始化方法
     * @param context  上下文
     * @param target   目标视图
     * @param tabIndex 需要显示的tab下标
     */
    private void init(Context context, View target, int tabIndex) {
        this.context = context;
        this.targetView = target;
        this.targetTabIndex = tabIndex;

        // 应用默认设置
        badgePosition = DEFAULT_POSITION;
        badgeMarginH = dipToPixels(DEFAULT_MARGIN_DIP);
        badgeMarginV = badgeMarginH;
        badgeColor = DEFAULT_BACKGROUND_COLOR;
        badgeSize = DEFAULT_TEXT_SIZE;
        // 设置文字气泡的左右填充边距
        int paddingPixels = dipToPixels(DEFAULT_LR_PADDING_DIP);
        setPadding(paddingPixels, 0, paddingPixels, 0);
        // 设置文字默认颜色
        setTextColor(DEFAULT_TEXT_COLOR);
        // 设置文字默认大小
        setTextSize(TypedValue.COMPLEX_UNIT_SP, DEFAULT_TEXT_SIZE);
        // 创建透明度入场动画
        fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        fadeIn.setDuration(300);
        // 创建透明度出场动画
        fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setDuration(300);
        // 标记为未显示
        isShown = false;
        // 判断是否传入目标视图
        if (this.targetView != null) {
            // 应用到目标视图
            applyTo(this.targetView);
        } else {
            // 直接显示当前视图
            show();
        }
    }

    /**
     * 应用到目标视图
     * @param target 目标视图
     */
    private void applyTo(View target) {
        // 取得目标视图的布局参数
        ViewGroup.LayoutParams lp = target.getLayoutParams();
        // 得到目标视图的父布局实例
        ViewParent parent = target.getParent();
        // 创建帧布局用于替换目标视图所在位置，同时容纳目标视图和当前角标视图
        FrameLayout container = new FrameLayout(context);

        // 如果目标视图是TabWidget组件
        // fixme ——此分支从网上拷贝而来，待验证实际情况
        if (target instanceof TabWidget) {
            // 取得指定下标的标签子视图
            target = ((TabWidget) target).getChildTabViewAt(targetTabIndex);
            this.targetView = target;
            // 添加帧布局
            ((ViewGroup) target).addView(
                    container,
                    new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT));
            // 设置当前角标视图消失
            this.setVisibility(View.GONE);
            // 添加到帧布局中
            container.addView(this);

        } else {
            // 如果是其他组件
            ViewGroup group = (ViewGroup) parent;
            // 找到目标视图在父容器中的位置
            int index = group.indexOfChild(target);
            // 移除目标视图
            group.removeView(target);
            // 将帧布局添加到原先目标视图的位置
            group.addView(container, index, lp);
            // 添加原组件到帧布局中
            container.addView(target);
            // 使当前角标视图消失
            this.setVisibility(View.GONE);
            // 添加角标视图到帧布局中
            container.addView(this);
            // 重绘父容器
            group.invalidate();
        }
        // 暂存新建的帧布局，用于页面处理
        target.setTag(container);
    }

    /**
     * 显示角标1——没有动画
     */
    public void show() {
        show(false, null);
    }

    /**
     * 显示角标2——是否使用动画
     * @param animate true 表示使用动画；false 表示不使用动画
     */
    @SuppressWarnings("unused")
    public void show(boolean animate) {
        show(animate, fadeIn);
    }

    /**
     * 显示角标3——使用自定义动画
     * @param anim 自定义动画
     */
    @SuppressWarnings("unused")
    public void show(Animation anim) {
        show(true, anim);
    }

    /**
     * 隐藏角标1——没有动画
     */
    @SuppressWarnings("unused")
    public void hide() {
        hide(false, null);
    }

    /**
     * 隐藏角标2——是否使用动画
     * @param animate true 表示使用动画；false 表示不使用动画
     */
    @SuppressWarnings("unused")
    public void hide(boolean animate) {
        hide(animate, fadeOut);
    }

    /**
     * 隐藏角标3——使用自定义动画
     * @param anim 自定义动画
     */
    @SuppressWarnings("unused")
    public void hide(Animation anim) {
        hide(true, anim);
    }

    /**
     * 切换角标1——没有动画
     */
    @SuppressWarnings("unused")
    public void toggle() {
        toggle(false, null, null);
    }

    /**
     * 切换角标2——是否使用动画
     * @param animate true 表示使用动画；false 表示不使用动画
     */
    @SuppressWarnings("unused")
    public void toggle(boolean animate) {
        toggle(animate, fadeIn, fadeOut);
    }

    /**
     * 切换角标3——使用自定义动画
     * @param animIn  自定义入场动画
     * @param animOut 自定义出场动画
     */
    @SuppressWarnings("unused")
    public void toggle(Animation animIn, Animation animOut) {
        toggle(true, animIn, animOut);
    }

    /**
     * 显示角标
     * @param animate 是否显示动画
     * @param anim    动画对象
     */
    private void show(boolean animate, Animation anim) {
        // 是否使用粗字体
        if (isUseBold) {
            setTypeface(Typeface.DEFAULT_BOLD);
        } else {
            // 默认字体
            setTypeface(Typeface.DEFAULT);
        }
        // 检查是否改变字体大小
        if (DEFAULT_TEXT_SIZE != badgeSize) {
            // 应用改变后的字体大小：sp单位
            setTextSize(TypedValue.COMPLEX_UNIT_SP, badgeSize);
        }
        // 如果没有设置背景
        if (getBackground() == null) {
            // 没有自定义背景
            if (badgeBg == null) {
                // 使用默认背景
                badgeBg = getDefaultBackground();
            }
            // 根据SDK版本采用方法
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                //noinspection deprecation
                setBackgroundDrawable(badgeBg);
            } else {
                setBackground(badgeBg);
            }
        }
        // 应用布局参数
        applyLayoutParams();
        // 是否显示入场动画
        if (animate) {
            // 启动入场动画
            this.startAnimation(anim);
        }
        // 显示
        this.setVisibility(View.VISIBLE);
        // 标记已显示
        isShown = true;
    }

    /**
     * 隐藏角标
     * @param animate 是否显示动画
     * @param anim    动画对象
     */
    private void hide(boolean animate, Animation anim) {
        // 消失
        this.setVisibility(View.GONE);
        // 是否显示出场动画
        if (animate) {
            // 启动出场动画
            this.startAnimation(anim);
        }
        // 标记已消失
        isShown = false;
    }

    /**
     * 切换角标
     * @param animate 是否显示动画
     * @param animIn  入场动画
     * @param animOut 出场动画
     */
    private void toggle(boolean animate, Animation animIn, Animation animOut) {
        // 显示与否的标记
        if (isShown) {
            // 显示则隐藏
            hide(animate && (animOut != null), animOut);
        } else {
            // 隐藏则显示
            show(animate && (animIn != null), animIn);
        }
    }

    /**
     * 增加一个数字角标，如果当前显示的文字无法转换为Integer类型，则将当前文字设为——0
     * @param offset 数字增量
     * @return 加上offset之后的值
     */
    public int increment(int offset) {
        CharSequence txt = getText();
        int i;
        if (txt != null) {
            try {
                i = Integer.parseInt(txt.toString());
            } catch (NumberFormatException e) {
                i = 0;
            }
        } else {
            i = 0;
        }
        i = i + offset;
        setText(String.valueOf(i));
        return i;
    }

    /**
     * 减去一个数字角标，利用加上负数的性质，调用增加角标方法
     * @param offset 数字减量
     * @return 减去offset之后的值
     */
    @SuppressWarnings("unused")
    public int decrement(int offset) {
        return increment(-offset);
    }

    /**
     * 获取默认背景
     * @return 背景图
     */
    private ShapeDrawable getDefaultBackground() {
        // 默认的圆角半径
        int r = dipToPixels(DEFAULT_CORNER_RADIUS_DIP);
        // 创建外圆范围
        float[] outerR = new float[]{r, r, r, r, r, r, r, r};
        // 根据外圆范围创建圆——忽略内圆和圆环
        RoundRectShape rr = new RoundRectShape(outerR, null, null);
        // 画出来这个圆
        ShapeDrawable drawable = new ShapeDrawable(rr);
        // 设置圆颜色
        drawable.getPaint().setColor(badgeColor);

        return drawable;

    }

    /**
     * 应用布局参数
     */
    @SuppressLint("RtlHardcoded")
    private void applyLayoutParams() {
        // 创建帧布局参数
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        // 根据设定位置设置边距
        switch (badgePosition) {
            case POSITION_TOP_LEFT:
                lp.gravity = Gravity.LEFT | Gravity.TOP;
                lp.setMargins(badgeMarginH, badgeMarginV, 0, 0);
                break;
            case POSITION_TOP_RIGHT:
                lp.gravity = Gravity.RIGHT | Gravity.TOP;
                lp.setMargins(0, badgeMarginV, badgeMarginH, 0);
                break;
            case POSITION_BOTTOM_LEFT:
                lp.gravity = Gravity.LEFT | Gravity.BOTTOM;
                lp.setMargins(badgeMarginH, 0, 0, badgeMarginV);
                break;
            case POSITION_BOTTOM_RIGHT:
                lp.gravity = Gravity.RIGHT | Gravity.BOTTOM;
                lp.setMargins(0, 0, badgeMarginH, badgeMarginV);
                break;
            case POSITION_CENTER:
                lp.gravity = Gravity.CENTER;
                lp.setMargins(0, 0, 0, 0);
                break;
            default:
                break;
        }
        // 将参数设置进当前角标视图
        setLayoutParams(lp);
    }

    /**
     * 返回已设置进来的目标视图
     * @return 目标视图
     */
    @SuppressWarnings("unused")
    public View getTargetView() {
        return targetView;
    }

    /**
     * 当前视图是否已显示
     */
    @Override
    public boolean isShown() {
        return isShown;
    }

    /**
     * 得到当前视图位于原目标视图的重心位置
     * @return 重心位置
     */
    @SuppressWarnings("unused")
    public int getBadgePosition() {
        return badgePosition;
    }

    /**
     * 设置当前视图位于原目标视图的重心位置
     * @param layoutPosition 重心位置
     */
    @SuppressWarnings("unused")
    public CircleBadgeView setBadgePosition(int layoutPosition) {
        this.badgePosition = layoutPosition;
        return this;
    }

    /**
     * 得到角标相对于原目标视图位置的内距偏移量——左上位置则偏移左边，右上位置则偏移右边
     * @return 偏移的像素值
     */
    @SuppressWarnings("unused")
    public int getHorizontalBadgeMargin() {
        return badgeMarginH;
    }

    /**
     * 得到角标相对于原目标视图位置的内距偏移量——左上位置则偏移上面，左下位置则偏移下面
     * @return 偏移的像素值
     */
    @SuppressWarnings("unused")
    public int getVerticalBadgeMargin() {
        return badgeMarginV;
    }

    /**
     * 设置相对于角落的偏移量
     * @param badgeMargin 内距偏移量的像素值
     */
    @SuppressWarnings("unused")
    public CircleBadgeView setBadgeMargin(int badgeMargin) {
        if (isUseDip) {
            badgeMargin = dipToPixels(badgeMargin);
        }
        this.badgeMarginH = badgeMargin;
        this.badgeMarginV = badgeMargin;
        return this;
    }

    /**
     * 设置相对于上下左右的偏移量
     * @param horizontal 内距水平方向的偏移量像素值
     * @param vertical   内距垂直方向的偏移量像素值
     */
    @SuppressWarnings("unused")
    public CircleBadgeView setBadgeMargin(int horizontal, int vertical) {
        if (isUseDip) {
            horizontal = dipToPixels(horizontal);
            vertical = dipToPixels(vertical);
        }
        this.badgeMarginH = horizontal;
        this.badgeMarginV = vertical;
        return this;
    }

    /**
     * 获得当前背景颜色
     * @return 颜色值
     */
    @SuppressWarnings("unused")
    public int getBadgeBackgroundColor() {
        return badgeColor;
    }

    /**
     * 设置背景颜色值，同步转换并设置Drawable对象
     */
    @SuppressWarnings("unused")
    public CircleBadgeView setBadgeBackgroundColor(int badgeColor) {
        this.badgeColor = badgeColor;
        badgeBg = getDefaultBackground();
        return this;
    }

    /**
     * 内置的单位转换工具
     * @param dip 需要转换的单位值
     * @return 从数值转换为dip单位的值
     */
    private int dipToPixels(int dip) {
        Resources r = getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, r.getDisplayMetrics());
        return (int) px;
    }

    /**
     * 是否使用粗体字样
     * @return true 表示正在使用；false 表示没有使用
     */
    @SuppressWarnings("unused")
    public boolean isUseBold() {
        return isUseBold;
    }

    /**
     * 设置是否使用粗体字样
     * @param useBold true 使用；false 不使用
     */
    @SuppressWarnings("unused")
    public CircleBadgeView setUseBold(boolean useBold) {
        isUseBold = useBold;
        return this;
    }

    /**
     * 获得当前字体大小
     * @return 字体大小值
     */
    @SuppressWarnings("unused")
    public float getBadgeSize() {
        return badgeSize;
    }

    /**
     * 设置当前字体大小
     * @param badgeSize 字体大小值
     */
    @SuppressWarnings("unused")
    public CircleBadgeView setBadgeSize(float badgeSize) {
        this.badgeSize = badgeSize;
        return this;
    }

    /**
     * 是否在设置时直接使用Dip转换单位
     * @return true 表示使用；false 表示不使用
     */
    @SuppressWarnings("unused")
    public boolean isUseDip() {
        return isUseDip;
    }

    /**
     * 设置是否采用dip单位方法
     * <br>此方法必须在{@link #setBadgeMargin(int)}和{@link #setBadgeMargin(int, int)}之前使用
     * @param useDip true 表示使用；false 表示不使用
     * @return 链式结构
     */
    public CircleBadgeView setUseDip(boolean useDip) {
        isUseDip = useDip;
        return this;
    }
}
