package com.ljz.base.widget.selector;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CompoundButton;

import com.ljz.base.widget.selector.injection.SelectorInjection;

/**
 * @author Kale
 *  2016/3/14
 */
public class SelectorButton extends CompoundButton implements SelectorView {

    private SelectorInjection injection;

    public SelectorButton(Context context) {
        this(context, null);
    }

    public SelectorButton(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.buttonStyle);
    }

    public SelectorButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        injection = initSelectorInjection(context, attrs);
        injection.injection(this);
    }

    @Override
    public SelectorInjection initSelectorInjection(Context context, AttributeSet attr) {
        return new SelectorInjection(context, attr);
    }

    @Override
    public SelectorInjection getInjection() {
        return injection;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        injection.setEnabled(this, enabled);
    }

    /**
     * instead by {@link #toggleCompat()}
     */
    @Deprecated
    @Override
    public void toggle() {
        // 覆盖选中事件
    }

    /**
     * 代替{@link #toggle()}
     */
    public void toggleCompat() {
        setChecked(!isChecked());
    }

}
