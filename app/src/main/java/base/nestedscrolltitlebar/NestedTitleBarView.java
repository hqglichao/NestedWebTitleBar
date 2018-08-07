package base.nestedscrolltitlebar;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.NestedScrollingParent;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebBackForwardList;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.OverScroller;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by beyond on 18-8-2.
 */

public class NestedTitleBarView extends LinearLayout implements NestedScrollingParent, IPresentBottomBar, View.OnClickListener{
    int minHeight;
    int minScrollY;

    public NestedTitleBarView(Context context) {
        this(context, null);
    }

    public NestedTitleBarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NestedTitleBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        minHeight = (int) getResources().getDimension(R.dimen.top_bar_height_half);
        minScrollY = dipToPix(25);
    }

    OverScroller mScroller;
    void init()
    {
        mScroller = new OverScroller(getContext());
    }

    @Override
    public void presentBottomBar() {
        if(wvWeb != null && wvWeb.copyBackForwardList().getSize() >= 1 && rlBottomBar.getVisibility() == GONE && (wvWeb.canGoBack() || wvWeb.canGoForward())) {
            rlBottomBar.setVisibility(VISIBLE);
            ivBack.setImageResource(R.mipmap.ic_close_black);
            setBottomIconStatus(CAN_ONLY_BACK);
        }
    }

    @Override
    public int getBottomBarHeight() {
        if(rlBottomBar != null && rlBottomBar.getVisibility() == View.VISIBLE) {
            return rlBottomBar.getHeight();
        } else {
            return 0;
        }
    }

    @Override
    public void setBottomIconStatusWrap() {
        if (wvWeb == null) return;
        if (wvWeb.canGoForward() && wvWeb.canGoBack()) setBottomIconStatus(CAN_BACK_FORWARD);
        else if (wvWeb.canGoBack()) setBottomIconStatus(CAN_ONLY_BACK);
        else if (wvWeb.canGoForward()) setBottomIconStatus(CAN_ONLY_FORWARD);
        else setBottomIconStatus(-1);
    }

    WebView wvWeb;
    RelativeLayout rlTitleBar;
    RelativeLayout rlBottomBar;
    ImageView ivBottomBack, ivBottomForward;
    TextView tvWebTitle, tvScaledWebTitle;
    ImageView ivBack;
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        rlBottomBar = (RelativeLayout) findViewById(R.id.rlBottomBar);
        ivBottomBack = (ImageView) findViewById(R.id.ivBottomBack);
        ivBottomForward = (ImageView) findViewById(R.id.ivBottomForward);
        ivBottomForward.setOnClickListener(this);
        ivBottomBack.setOnClickListener(this);
        wvWeb = (WebView) findViewById(R.id.wvWeb);
        rlTitleBar = (RelativeLayout) findViewById(R.id.rlTitleBar);
        tvWebTitle = (TextView) findViewById(R.id.tvWebTitle);
        tvScaledWebTitle = (TextView) findViewById(R.id.tvScaledWebTitle);
        ivBack = (ImageView) findViewById(R.id.ivBack);
        ivBack.setOnClickListener(this);
    }


    private final int CAN_BACK_FORWARD = 0;
    private final int CAN_ONLY_BACK = 1;
    private final int CAN_ONLY_FORWARD = 2;

    private void setBottomIconStatus(final int status) {
        if (!Utils.checkRunningMainThread()) {
            ivBottomBack.post(new Runnable() {
                @Override
                public void run() {
                    setBottomIconStatus(status);
                }
            });
            return;
        }
        switch (status) {
            case CAN_BACK_FORWARD:
                setBottomIconEnable(ivBottomBack, true);
                setBottomIconEnable(ivBottomForward, true);
                break;
            case CAN_ONLY_BACK:
                setBottomIconEnable(ivBottomBack, true);
                setBottomIconEnable(ivBottomForward, false);
                break;
            case CAN_ONLY_FORWARD:
                setBottomIconEnable(ivBottomForward, true);
                setBottomIconEnable(ivBottomBack, false);
                break;
            default:
                setBottomIconEnable(ivBottomBack, wvWeb.canGoBack());
                setBottomIconEnable(ivBottomForward, wvWeb.canGoForward());
                break;
        }
    }

    private void setBottomIconEnable(ImageView ivView, boolean isEnable) {
        ivView.setEnabled(isEnable);
        ViewUtil.setColorForView(ivView, isEnable ? getResources().getColor(R.color.c_525252) : getResources().getColor(R.color.c_d2d2d2));
    }

    public void goBack() {
        WebBackForwardList webBackForwardList = wvWeb.copyBackForwardList();
        if (webBackForwardList != null) {
            if (webBackForwardList.getCurrentIndex() == 1 || webBackForwardList.getCurrentIndex() == 0) {
                setBottomIconStatus(CAN_ONLY_FORWARD);
            } else if (webBackForwardList.getCurrentIndex() - 1 > 1) {
                setBottomIconStatus(CAN_BACK_FORWARD);
            }
        }
        wvWeb.goBack();
    }


    private void goForward() {
        if (wvWeb != null && wvWeb.canGoForward()) {
            WebBackForwardList webBackForwardList = wvWeb.copyBackForwardList();
            if (webBackForwardList != null) {
                if (webBackForwardList.getCurrentIndex() >= webBackForwardList.getSize() - 2) {
                    setBottomIconStatus(CAN_ONLY_BACK);
                } else {
                    setBottomIconStatus(CAN_BACK_FORWARD);
                }
            }
            wvWeb.goForward();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivBack:
                if (getContext() instanceof Activity) {
                    ((Activity) getContext()).finish();
                }
                break;
            case R.id.ivBottomBack:
                if (wvWeb != null && wvWeb.canGoBack()) goBack();
                break;
            case R.id.ivBottomForward:
                goForward();
                break;
            default:
                break;
        }
    }

    int mTopViewHeight;
    int scaleDistance;
    int minScaleDistance;
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mTopViewHeight = rlTitleBar.getMeasuredHeight();
        scaleDistance = mTopViewHeight - minHeight;
        minScaleDistance = dipToPix(5);
        maxScrollY = mTopViewHeight;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setWebViewHeight();
    }

    private int startScrollY;
    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        startScrollY = 0;
        return true;
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int axes) {
    }

    @Override
    public void onStopNestedScroll(View child) {
        settleTitleBar();
    }

    private void settleTitleBar() {
        int scrollY = getScrollY();
        if (scrollY > 0) {
            rlTitleBar.setAlpha(0);
            scrollTo(0, mTopViewHeight - minHeight);
            if (!isScaledWebTitleVisible()) presentTitleText(true, 1);
        }
    }

    private void presentTitleText(boolean isVisible, float alphaP) {
        if (isVisible && !isScaledWebTitleVisible()) {
            tvScaledWebTitle.setText(tvWebTitle.getText());
            tvScaledWebTitle.setVisibility(VISIBLE);
        } else if (!isVisible) {
            tvScaledWebTitle.setVisibility(GONE);
            return;
        }
        tvScaledWebTitle.setAlpha(alphaP);
    }

    private boolean isScaledWebTitleVisible() {
        return tvScaledWebTitle.getVisibility() == VISIBLE;
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        Log.d("myVersion2", "dx: " + dx + " dy: " + dy);
        boolean canUp = getScaleY() < (mTopViewHeight - minHeight) && dy > 0;
        boolean canDown = dy < 0 && getScaleY() > 0;
        startScrollY += dy;
        if (Math.abs(startScrollY) > minScrollY && (canDown || canUp)) {
            scrollBy(0, dy);
            consumed[1] += dy;
            setWebViewHeight();
            setTitleBarScaleAlpha();
        }
    }

    int oldScrollY = -1;
    private void setTitleBarScaleAlpha() {
        int newScrollY = getScrollY();
        if (newScrollY != oldScrollY) {
            float scaleP = 1.0f * (scaleDistance - newScrollY) / scaleDistance;
            float alphaP = 1.0f * (scaleDistance - newScrollY) / scaleDistance;
            Log.d("myVersion516", "pivotY " + rlTitleBar.getPivotY() + " height " + rlTitleBar.getHeight());
            rlTitleBar.setPivotY(rlTitleBar.getHeight() - getScrollY());
            rlTitleBar.setScaleX(scaleP);
            rlTitleBar.setScaleY(scaleP);
            rlTitleBar.setAlpha(alphaP);
            if (alphaP != 1 && ivBack.isEnabled()) ivBack.setEnabled(false);
            else if (alphaP == 1) ivBack.setEnabled(true);
            oldScrollY = newScrollY;

            int deltaMinDistance = scaleDistance - newScrollY;
            if (deltaMinDistance < minScaleDistance) {
                presentTitleText(true, 1.0f * (minScaleDistance - deltaMinDistance) / minScaleDistance);
            } else {
                presentTitleText(false, 0);
            }

        }
    }

    private void setWebViewHeight() {
        Log.d("myVersion", "this bottom: " + getBottom() + " web bottom: " + wvWeb.getBottom() + " scrollY: " + getScrollY() + " mScroller scrollY: " + mScroller.getCurrY());
        ViewGroup.LayoutParams layoutParams = wvWeb.getLayoutParams();
        layoutParams.height = getMeasuredHeight() - (mTopViewHeight - getScrollY()) - getBottomBarHeight();
        wvWeb.setLayoutParams(layoutParams);
    }

    @Override
    public void computeScroll()
    {
        if (mScroller.computeScrollOffset())
        {
            scrollTo(0, mScroller.getCurrY());
            invalidate();
        }
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY)
    {
        if (velocityY > 0 && getScrollY() < maxScrollY) // 向上滑动, 且当前View还没滑到最顶部
        {
            fling((int) velocityY, maxScrollY);
            return true;
        }
        else if (velocityY < 0 && getScrollY() > 0) // 向下滑动, 且当前View部分在屏幕外
        {
            fling((int) velocityY, 0);
            return true;
        }
        return false;
    }

    public void fling(int velocityY, int maxY)
    {
        mScroller.fling(0, getScrollY(), 0, velocityY, 0, 0, 0, maxY);
        invalidate();
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        return false;
    }

    int maxScrollY; // 最大滚动距离
    @Override
    public void scrollTo(int x, int y)
    {
        if (y < 0) // 不允许向下滑动
        {
            y = 0;
        }
        if (y > maxScrollY - minHeight) // 防止向上滑动距离大于最大滑动距离
        {
            y = maxScrollY - minHeight;
        }
        if (y != getScrollY())
        {
            super.scrollTo(x, y);
        }
    }


    private int dipToPix(int dip) {
        return (int) this.getResources().getDisplayMetrics().density * dip;
    }
}
