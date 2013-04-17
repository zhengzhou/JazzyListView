package com.twotoasters.jazzylistview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

import com.nineoldandroids.view.ViewPropertyAnimator;

public class JazzyListView extends ListView implements OnScrollListener {

    public static final int STANDARD = 0;
    public static final int GROW = 1;
    public static final int CARDS = 2;
    public static final int CURL = 3;
    public static final int WAVE = 4;
    public static final int FLIP = 5;
    public static final int FLY = 6;
    public static final int REVERSE_FLY = 7;
    public static final int HELIX = 8;
    public static final int FAN = 9;
    public static final int TILT = 10;
    public static final int ZIPPER = 11;
    public static final int FADE = 12;
    public static final int TWIRL = 13;

    private static final String TAG = "Jazzy";

    private static final boolean IS_AT_LEAST_HC =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;

    public static final int DURATION = 600;
    public static final int OPAQUE = 255, TRANSPARENT = 0;

    private int mTransitionEffect = STANDARD;
    private boolean mIsScrolling = false;
    private int mFirstVisibleItem = -1;
    private int mLastVisibleItem = -1;

    private OnScrollListener mAdditionalOnScrollListener;

    public JazzyListView(Context context) {
        this(context, null);
    }

    public JazzyListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public JazzyListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        super.setOnScrollListener(this); // call super's method to actually register this list as the listener

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.JazzyListView);
        mTransitionEffect = a.getInteger(R.styleable.JazzyListView_effect, STANDARD);
        a.recycle();
    }

    @Override
    public final void setOnScrollListener(OnScrollListener l) {
        // hijack the scroll listener setter and have this list also notify the additional listener
        mAdditionalOnScrollListener = l;
    }

    @Override
    public final void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        Log.v(TAG, String.format("onScroll() - [firstVis: %d, visItemCount: %d, totItemCount: %d]", firstVisibleItem, visibleItemCount, totalItemCount));
        boolean shouldAnimateItems = (mFirstVisibleItem != -1 && mLastVisibleItem != -1);

        int lastVisibleItem = firstVisibleItem + visibleItemCount - 1;
        if (IS_AT_LEAST_HC && mIsScrolling && shouldAnimateItems) {
            int indexAfterFirst = 0;
            while (firstVisibleItem + indexAfterFirst < mFirstVisibleItem) {
                Log.v(TAG, "Scrolled to reveal new item(s) ABOVE");
                View item = view.getChildAt(indexAfterFirst);
                doJazziness(item, firstVisibleItem + indexAfterFirst, -1);
                indexAfterFirst++;
            }

            int indexBeforeLast = 0;
            while (lastVisibleItem - indexBeforeLast > mLastVisibleItem) {
                Log.v(TAG, "Scrolled to reveal new item(s) BELOW");
                View item = view.getChildAt(lastVisibleItem - firstVisibleItem - indexBeforeLast);
                doJazziness(item, lastVisibleItem, 1);
                indexBeforeLast++;
            }
        }

        mFirstVisibleItem = firstVisibleItem;
        mLastVisibleItem = lastVisibleItem;

        notifyAdditionalScrollListener(view, firstVisibleItem, visibleItemCount, totalItemCount);
    }

    /**
     * Initializes the item view and triggers the animation.
     * @param item The view to be animated.
     * @param position The index of the view in the list.
     * @param scrollDirection Positive number indicating scrolling down, or negative number indicating scrolling up.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void doJazziness(View item, int position, int scrollDirection) {
        if (mIsScrolling) {
            ViewPropertyAnimator animator = com.nineoldandroids.view.ViewPropertyAnimator
                    .animate(item)
                    .setDuration(DURATION)
                    .setInterpolator(new AccelerateDecelerateInterpolator());

            int itemWidth = item.getWidth();
            int itemHeight = item.getHeight();
            scrollDirection = scrollDirection > 0 ? 1 : -1;

            switch (mTransitionEffect) {
                case STANDARD:
                    break;
                case GROW:
                    item.setPivotX(itemWidth / 2);
                    item.setPivotY(itemHeight / 2);
                    item.setScaleX(0.01f);
                    item.setScaleY(0.01f);
                    animator.scaleX(1).scaleY(1);
                    break;
                case CARDS:
                    item.setPivotX(itemWidth / 2);
                    item.setPivotY(itemHeight / 2);
                    item.setRotationX(90 * scrollDirection);
                    item.setTranslationY(itemHeight * scrollDirection);
                    animator.rotationXBy(-90 * scrollDirection).translationYBy(-itemHeight * scrollDirection);
                    break;
                case CURL:
                    item.setPivotX(0);
                    item.setPivotY(itemHeight / 2);
                    item.setRotationY(90);
                    animator.rotationY(0);
                    break;
                case WAVE:
                    item.setTranslationX(-itemWidth);
                    animator.translationX(0);
                    break;
                case FLIP:
                    item.setPivotX(itemWidth / 2);
                    item.setPivotY(itemHeight / 2);
                    item.setRotationX(-90 * scrollDirection);
                    animator.rotationXBy(90 * scrollDirection);
                    break;
                case FLY:
                    item.setPivotX(itemWidth / 2);
                    item.setPivotY(itemHeight / 2);
                    item.setRotationX(-135 * scrollDirection);
                    item.setTranslationY(itemHeight * 2 * scrollDirection);
                    animator.rotationXBy(135 * scrollDirection).translationYBy(-itemHeight * 2 * scrollDirection);
                    break;
                case REVERSE_FLY:
                    item.setPivotX(itemWidth / 2);
                    item.setPivotY(itemHeight / 2);
                    item.setRotationX(135 * scrollDirection);
                    item.setTranslationY(-itemHeight * 2 * scrollDirection);
                    animator.rotationXBy(-135 * scrollDirection).translationYBy(itemHeight * 2 * scrollDirection);
                    break;
                case HELIX:
                    item.setRotationY(180);
                    animator.rotationYBy(180 * scrollDirection);
                    break;
                case FAN:
                    item.setPivotX(0);
                    item.setPivotY(0);
                    item.setRotation(70 * scrollDirection);
                    animator.rotationBy(-70 * scrollDirection);
                    break;
                case TILT:
                    item.setPivotX(itemWidth / 2);
                    item.setPivotY(itemHeight / 2);
                    item.setScaleX(0.7f);
                    item.setScaleY(0.7f);
                    item.setTranslationY(itemHeight / 2 * scrollDirection);
                    item.setAlpha(OPAQUE / 2);
                    animator.translationYBy(-itemHeight / 2 * scrollDirection).scaleX(1).scaleY(1).alpha(OPAQUE);
                    break;
                case ZIPPER:
                    boolean isEven = position % 2 == 0;
                    item.setTranslationX((isEven ? -1 : 1) * itemWidth);
                    animator.translationX(0);
                    break;
                case FADE:
                    item.setAlpha(TRANSPARENT);
                    animator.setDuration(animator.getDuration() * 5);
                    animator.alpha(OPAQUE);
                    break;
                case TWIRL:
                    break;
                default:
                    break;
            }

            animator.start();
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch(scrollState) {
            case OnScrollListener.SCROLL_STATE_IDLE:
                mIsScrolling = false;
                break;
            case OnScrollListener.SCROLL_STATE_FLING:
            case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                mIsScrolling = true;
                break;
            default: break;
        }

        Log.v(TAG, String.format("isScrolling: %s", Boolean.valueOf(mIsScrolling)));
    }

    public void setTransitionEffect(int transitionEffect) {
        mTransitionEffect = transitionEffect;
    }

    private void notifyAdditionalScrollListener(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (mAdditionalOnScrollListener != null) {
            mAdditionalOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
    }
}
