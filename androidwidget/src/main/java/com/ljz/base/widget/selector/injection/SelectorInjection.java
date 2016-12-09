package com.ljz.base.widget.selector.injection;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;

import com.ljz.base.widget.R;

/**
 * View的一个selector注入装置，通过构造函数即可注入。之后调用{@link #injection(View)}即可.
 *
 * @author Kale
 *  2015/5/25
 */
public class SelectorInjection {

    public static int DEFAULT_COLOR = 0x0106000d;

    public static final int DEFAULT_STROKE_WIDTH = 2;

    public static final int ANIMATION_TIME = 10;

    /**
     * 是否是智能模式，如果是的那么会自动计算按下后的颜色
     */
    public boolean isSmart = true;

    /**
     * 正常情况颜色
     */
    public int normalColor;

    /**
     * 按下后的颜色（smart关闭后才有效）
     */
    public int pressedColor;

    /**
     * 描边的颜色
     */
    public int normalStrokeColor;

    /**
     * 描边的宽度，如果不设置会根据默认的宽度进行描边
     */
    public int normalStrokeWidth;

    /**
     * 按下后描边的颜色
     */
    public int pressedStrokeColor;

    /**
     * 按下后描边的宽度
     */
    public int pressedStrokeWidth;

    /**
     * 选中后的描边颜色
     */
    public int checkedStrokeColor;

    /**
     * 选中后的描边宽度
     */
    public int checkedStrokeWidth;

    /**
     * 正常情况下的drawable
     */
    public Drawable normal;

    /**
     * 按下后的drawable
     */
    public Drawable pressed;

    /**
     * 是否将drawable设置到src中，如果不是那么默认是background
     */
    public boolean isSrc;

    /**
     * 被选中时的图片
     */
    public Drawable checked;

    /**
     * 被选中时的颜色
     */
    public int checkedColor;

    public boolean showRipple;

    public SelectorInjection(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SelectorInjection);

        isSmart = a.getBoolean(R.styleable.SelectorInjection_isSmart, true);

        normal = a.getDrawable(R.styleable.SelectorInjection_normalDrawable);
        pressed = a.getDrawable(R.styleable.SelectorInjection_pressedDrawable);
        checked = a.getDrawable(R.styleable.SelectorInjection_checkedDrawable);

        normalColor = getColor(a, R.styleable.SelectorInjection_normalColor);
        pressedColor = getColor(a, R.styleable.SelectorInjection_pressedColor);
        checkedColor = getColor(a, R.styleable.SelectorInjection_checkedColor);

        normalStrokeColor = getColor(a, R.styleable.SelectorInjection_normalStrokeColor);
        normalStrokeWidth = a.getDimensionPixelSize(R.styleable.SelectorInjection_normalStrokeWidth, DEFAULT_STROKE_WIDTH);

        pressedStrokeColor = getColor(a, R.styleable.SelectorInjection_pressedStrokeColor);
        pressedStrokeWidth = a.getDimensionPixelOffset(R.styleable.SelectorInjection_pressedStrokeWidth, DEFAULT_STROKE_WIDTH);

        checkedStrokeColor = getColor(a, R.styleable.SelectorInjection_checkedStrokeColor);
        checkedStrokeWidth = a.getDimensionPixelSize(R.styleable.SelectorInjection_checkedStrokeWidth, DEFAULT_STROKE_WIDTH);

        isSrc = a.getBoolean(R.styleable.SelectorInjection_isSrc, false);

        showRipple = a.getBoolean(R.styleable.SelectorInjection_showRipple, true);
        a.recycle();
    }

    public void injection(View view) {
        StateListDrawable selector = new StateListDrawable();// 背景选择器
        // 如果是智能模式，那么自动处理按压效果
        if (isSmart && normal != null && pressed == null) {
            pressed = normal.getConstantState().newDrawable();
        }

        if (pressed != null) {
            setPressedDrawable(selector);
        }
        if (checked != null) {
            setCheckedDrawable(selector);
        }
        if (normal != null) {
            setNormalDrawable(selector);
        }

        setSelector(view, selector);
    }

    public void setSelector(View view, StateListDrawable selector) {
        selector.setEnterFadeDuration(ANIMATION_TIME);
        selector.setExitFadeDuration(ANIMATION_TIME);

        if (view instanceof ImageButton && isSrc) {
            // 如果是imageButton，那么就看这个selector是给背景的还是给src的
            ((ImageButton) view).setImageDrawable(selector);
            //mView.setBackgroundDrawable(null);
        } else {
            if (showRipple && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                RippleDrawable ripple = (RippleDrawable) view.getContext().getDrawable(R.drawable.si_ripple);
                assert ripple != null;
                ripple.setDrawableByLayerId(android.R.id.background, selector);
                ripple.setColor(createColorStateList(pressedColor, pressedColor, pressedColor, pressedColor));
                view.setBackground(ripple);
            } else {
                view.setBackgroundDrawable(selector);
            }
        }
    }

    public void setEnabled(View view, boolean enabled) {
        view.setAlpha(!enabled ? 0.3f : 1);
    }

    /**
     * 设置按下后的样式（颜色，描边）
     */
    private void setPressedDrawable(StateListDrawable selector) {
        if (pressedColor == DEFAULT_COLOR) {
            pressedColor = isSmart ? getPressedColor(normalColor) : pressedColor;
        }
        setColorAndStroke(pressed, pressedColor, pressedStrokeColor, pressedStrokeWidth, false);
        // 给selector设置pressed的状态
        selector.addState(new int[]{android.R.attr.state_pressed}, pressed);
        selector.addState(new int[]{android.R.attr.state_focused}, pressed);
        pressed.mutate();
    }

    /**
     * 设置选中状态下的样子
     */
    private void setCheckedDrawable(StateListDrawable selector) {
        setColorAndStroke(checked, checkedColor, checkedStrokeColor, checkedStrokeWidth, false);
        selector.addState(new int[]{android.R.attr.state_checked}, checked);
        checked.mutate();
    }

    /**
     * 开始设置普通状态时的样式（颜色，描边）
     */
    private void setNormalDrawable(StateListDrawable selector) {
        setColorAndStroke(normal, normalColor, normalStrokeColor, normalStrokeWidth, true);
        selector.addState(new int[]{}, normal);
    }

    /**
     * 设置背景颜色和描边的颜色/宽度
     */
    private void setColorAndStroke(Drawable drawable, int color, int strokeColor, int strokeWidth, boolean isNormal) {
        if (drawable instanceof GradientDrawable) {
            setShape((GradientDrawable) drawable, color, strokeColor, strokeWidth, isNormal);
        } else if (drawable instanceof LayerDrawable) {
            // 如果是layer-list，先找到要设置的shape
            Drawable shape = ((LayerDrawable) drawable).findDrawableByLayerId(android.R.id.background);
            if (shape instanceof GradientDrawable) {
                setShape((GradientDrawable) shape, color, strokeColor, strokeWidth, isNormal);
            }
        }
    }

    /**
     * 设置shape的颜色和描边
     *
     * @param shape       shape对象
     * @param color       shape对象的背景色
     * @param strokeColor shape的描边颜色
     * @param strokeWidth shape的描边宽度
     */
    private void setShape(GradientDrawable shape, int color, int strokeColor, int strokeWidth, boolean isNormal) {
        if (showRipple && !isNormal && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            shape.setColor(normalColor);
        } else {
            shape.setColor(color);
        }
        if (strokeColor != DEFAULT_COLOR) {
            shape.setStroke(strokeWidth, strokeColor);
        }
    }

    /**
     * Make a dark color to press effect
     * 自动计算得到按下的颜色，如果不满足需求可重写
     */
    protected int getPressedColor(int normalColor) {
        int alpha = 255;
        int r = (normalColor >> 16) & 0xFF;
        int g = (normalColor >> 8) & 0xFF;
        int b = (normalColor >> 1) & 0xFF;
        r = (r - 50 < 0) ? 0 : r - 50;
        g = (g - 50 < 0) ? 0 : g - 50;
        b = (b - 50 < 0) ? 0 : b - 50;
        return Color.argb(alpha, r, g, b);
    }

    /**
     * 设置不同状态时其文字颜色
     *
     * @see "http://blog.csdn.net/sodino/article/details/6797821"
     */
    private ColorStateList createColorStateList(int normal, int pressed, int focused, int unable) {
        int[] colors = new int[]{pressed, focused, normal, focused, unable, normal};
        int[][] states = new int[6][];
        states[0] = new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled};
        states[1] = new int[]{android.R.attr.state_enabled, android.R.attr.state_focused};
        states[2] = new int[]{android.R.attr.state_enabled};
        states[3] = new int[]{android.R.attr.state_focused};
        states[4] = new int[]{android.R.attr.state_window_focused};
        states[5] = new int[]{};
        return new ColorStateList(states, colors);
    }

    private static int getColor(TypedArray a, int styleResId) {
        return a.getColor(styleResId, DEFAULT_COLOR);
    }

}
