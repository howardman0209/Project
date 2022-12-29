package com.howard.project.uiComponent;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

/**
 * Fix RecyclerView.findLastVisibleItemPosition() not working in AppBarLayout
 */
public class AppBarLayoutManager extends LinearLayoutManager {
    public AppBarLayoutManager(@NonNull Context context) {
        super(context);
    }

    //public AppBarLayoutManager(@NonNull Context context, int orientation, boolean reverseLayout) {
    //    super(context, orientation, reverseLayout);
    //}
    //
    //public AppBarLayoutManager(final @NonNull Context context, final @NonNull AttributeSet attrs, final int defStyleAttr, final int defStyleRes) {
    //    super(context, attrs, defStyleAttr, defStyleRes);
    //}

    @Override
    public int findFirstVisibleItemPosition() {
        final View item = findVisibleItem(0, getChildCount(), false);
        return item == null ? -1 : getPosition(item);
    }

    @Override
    public int findFirstCompletelyVisibleItemPosition() {
        final View item = findVisibleItem(0, getChildCount(), true);
        return item == null ? -1 : getPosition(item);
    }

    @Override
    public int findLastVisibleItemPosition() {
        final View item = findVisibleItem(getChildCount() - 1, -1, false);
        return item == null ? -1 : getPosition(item);
    }

    @Override
    public int findLastCompletelyVisibleItemPosition() {
        final View item = findVisibleItem(getChildCount() - 1, -1, true);
        return item == null ? -1 : getPosition(item);
    }

    private View findVisibleItem(final int fromIndex, final int toIndex, final boolean isCompletely) {
        final int next = toIndex > fromIndex ? 1 : -1;
        for (int i = fromIndex; i != toIndex; i += next) {
            final View child = getChildAt(i);
            if (child != null && checkIsVisible(child, isCompletely)) {
                return child;
            }
        }

        return null;
    }

    private boolean checkIsVisible(final View child, final boolean isCompletely) {
        final int[] location = new int[2];
        child.getLocationOnScreen(location);

        final View parent = (View) child.getParent();
        final Rect parentRect = new Rect();
        parent.getGlobalVisibleRect(parentRect);

        if (getOrientation() == HORIZONTAL) {
            final int childLeft = location[0];
            final int childRight = location[0] + child.getWidth();
            return isCompletely
                    ? childLeft >= parentRect.left && childRight <= parentRect.right
                    : childLeft <= parentRect.right && childRight >= parentRect.left;
        } else {
            final int childTop = location[1];
            final int childBottom = location[1] + child.getHeight();
            return isCompletely
                    ? childTop >= parentRect.top && childBottom <= parentRect.bottom
                    : childTop <= parentRect.bottom && childBottom >= parentRect.top;
        }
    }
}
