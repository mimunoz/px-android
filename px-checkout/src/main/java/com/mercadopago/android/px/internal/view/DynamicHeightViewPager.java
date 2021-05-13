package com.mercadopago.android.px.internal.view;

import android.content.Context;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;

public class DynamicHeightViewPager extends ViewPager {
    private boolean isAccessibilityFocused = false;

    public DynamicHeightViewPager(@NonNull final Context context) {
        this(context, null);
    }

    public DynamicHeightViewPager(@NonNull final Context context, @Nullable final AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final View firstChild = getChildAt(0);
        int measuredHeight = 0;
        if (firstChild != null) {
            firstChild.measure(widthMeasureSpec, heightMeasureSpec);
            measuredHeight = firstChild.getMeasuredHeight();
        }
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(measuredHeight, MeasureSpec.EXACTLY));
    }

    @Override
    public boolean dispatchPopulateAccessibilityEvent(final AccessibilityEvent event) {
        isAccessibilityFocused = event.getEventType() == AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED;
        return super.dispatchPopulateAccessibilityEvent(event);
    }

    public boolean hasAccessibilityFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return isAccessibilityFocused();
        } else {
            return isAccessibilityFocused;
        }
    }
}