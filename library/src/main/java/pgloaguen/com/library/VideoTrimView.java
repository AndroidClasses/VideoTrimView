package pgloaguen.com.library;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.io.File;

/**
 * Created by root on 13/08/15.
 */
public class VideoTrimView extends FrameLayout implements GestureDetector.OnGestureListener, VideoTrimContract.View {
    private ImageView mCursorLeftView;
    private ImageView mCursorRightView;

    private GestureDetector gestureDetector;

    private onTrimPositionListener onTrimPositionListener;

    VideoTrimContract.UserActionsListener mActionListener;

    public VideoTrimView(Context context) {
        super(context);
        init();
    }

    public VideoTrimView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VideoTrimView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
//        videoFrameView = new VideoFrameView(getContext());
        mActionListener = new VideoTrimPresenter(this);
        mCursorLeftView = new ImageView(getContext());
        mCursorRightView = new ImageView(getContext());
        gestureDetector = new GestureDetector(getContext(), this);
        initFrameHost(getContext());

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        addView(mFrameHost.getView());

        View borderView = new View(getContext());
        borderView.setBackgroundResource(R.drawable.border_trim_video);
        addView(borderView);

        mCursorLeftView.setBackgroundResource(R.drawable.trim_video_left);
        mCursorRightView.setBackgroundResource(R.drawable.trim_video_right);

        addView(mCursorLeftView);
        addView(mCursorRightView);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        mActionListener.onLayout(left, top, right, bottom);
    }

    @Override
    public void layoutLeftCursor(int xLeft, int yTop, int xRight, int yBottom) {
        mCursorLeftView.layout(xLeft, yTop, xRight, yBottom);
    }

    @Override
    public void layoutRightCursor(int xLeft, int yTop, int xRight, int yBottom) {
        mCursorRightView.layout(xLeft, yTop, xRight, yBottom);
    }

    @Override
    public void setTrimRange(float startInS, float endInS) {
        onTrimPositionListener.onTrimPositionUpdated(startInS, endInS);
    }

    @Override
    public void notifyTrimWidthChanged(float second) {
        mFrameHost.setWidthInSecond(second);
    }

    @Override
    public void notifyVideoSourceChanged(Uri uri) {
        mFrameHost.setVideo(uri);
    }

    @Override
    public void notifyVideoSourceChanged(File file) {
        mFrameHost.setVideo(file);
    }

    @Override
    public void notifyVideoSourceChanged(String path) {
        mFrameHost.setVideo(path);
    }

    @Override
    public void notifyCursorChanged(boolean left) {
        requestLayout();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        mActionListener.onTouchEventDispatch(getLeftOffsetInSecond(), getMaxiOffsetInSecond());

        if(!gestureDetector.onTouchEvent(event)) {
            return super.dispatchTouchEvent(event);
        }

        return true;
    }

    public void setWidthInSecond(float second) {
        mActionListener.updateTrimWidthInSecond(second);
    }

    public void setOnTrimPositionListener(onTrimPositionListener listener) {
        onTrimPositionListener = listener;
    }

    public void setVideo(Uri uri) {
        mActionListener.updateVideoSource(uri);
    }

    public void setVideo(File file) {
        mActionListener.updateVideoSource(file);
    }

    public void setVideo(String path) {
        mActionListener.updateVideoSource(path);
    }

    private boolean isCursorLeftTouch(float x) {
        return x < mCursorLeftView.getRight();
    }

    private boolean isCursorRightTouch(float x) {
        return x > mCursorRightView.getLeft();
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return mActionListener.onTouchDown(isCursorLeftTouch(e.getX()), isCursorRightTouch(e.getX()));
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return mActionListener.onTouchDrag(distanceX / getWidth());
    }

    @Override
    public void onShowPress(MotionEvent e) {}

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {}

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    public interface onTrimPositionListener {
        void onTrimPositionUpdated(float startInS, float endInS);
    }

//    private VideoFrameView videoFrameView;
    private TrimSourceInterface mFrameHost;
    private void initFrameHost(Context context) {
        mFrameHost = new VideoFrameView(context);
    }

    float getLeftOffsetInSecond() {
        return mFrameHost.getStartInSecond();
    }
    private float getMaxiOffsetInSecond() {
        return mFrameHost.getVideoDurationInSecond();
    }
}
