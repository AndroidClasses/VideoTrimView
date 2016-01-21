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
public class VideoTrimView extends FrameLayout implements GestureDetector.OnGestureListener {

    private VideoFrameView videoFrameView;
    private ImageView mCursorLeftView;
    private ImageView mCursorRightView;

    private GestureDetector gestureDetector;

    private float widthInSecond = 15f;

    private final static float MIN_TRIM_IN_SECOND = 3f;
    private static final float MIN_BETWEEN_CURSOR_IN_PERCENT = 0.1f;

    private static final int DEMI_CURSOR_WIDTH = 40;

    private float cursorLeftX = 0f;
    private float cursorRightX = 1.0f;

    private onTrimPositionListener onTrimPositionListener;

    private boolean hasAVideo;

    private float getMinTrimInSecond() {
        float trimPercent = MIN_TRIM_IN_SECOND / widthInSecond;
        if (trimPercent < MIN_BETWEEN_CURSOR_IN_PERCENT) {
            return trimPercent;
        } else {
            return MIN_BETWEEN_CURSOR_IN_PERCENT;
        }
    }

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
        videoFrameView = new VideoFrameView(getContext());
        mCursorLeftView = new ImageView(getContext());
        mCursorRightView = new ImageView(getContext());
        gestureDetector = new GestureDetector(getContext(), this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        addView(videoFrameView);

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

        int heigth = bottom - top;
        int width = (right - left);

        int rightCursorLeftPosition = (int) ((cursorLeftX * width) + DEMI_CURSOR_WIDTH);
        mCursorLeftView.layout(rightCursorLeftPosition - width, 0, rightCursorLeftPosition, heigth);

        int leftCursorRightPosition = (int) ((cursorRightX * width) - DEMI_CURSOR_WIDTH);
        mCursorRightView.layout(leftCursorRightPosition, 0, leftCursorRightPosition + width, heigth);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (hasAVideo) {
            float leftCursorUSecond = getLeftCursorOffset();
            float rightCursorUSecond = getRightCursorOffset();
            float rightestScrollOffset = videoFrameView.getVideoDurationInSecond() * US_PER_SECOND;

            float startUSecond = Math.max(0, leftCursorUSecond);
            float endUSecond = Math.min(rightestScrollOffset, rightCursorUSecond);
            onTrimPositionListener.onTrimPositionUpdated(startUSecond, endUSecond);
        }

        if(!gestureDetector.onTouchEvent(event)) {
            return super.dispatchTouchEvent(event);
        }

        return true;
    }

    public void setWidthInSecond(float second) {
        float maxDuration = videoFrameView.getVideoDurationInSecond();
        if (0 < second && second < maxDuration) {
        widthInSecond = second;
        } else {
            widthInSecond = maxDuration;
        }

        videoFrameView.setWidthInSecond(widthInSecond);
    }

    public void setOnTrimPositionListener(onTrimPositionListener listener) {
        onTrimPositionListener = listener;
    }

    public void setVideo(Uri uri) {
        hasAVideo = uri != null;
        videoFrameView.setVideo(uri);
    }

    public void setVideo(File file) {
        hasAVideo = file != null;
        videoFrameView.setVideo(file);
    }

    public void setVideo(String path) {
        hasAVideo = path != null;
        videoFrameView.setVideo(path);
    }

    private boolean isCursorLeftTouch(float x) {
        return x < mCursorLeftView.getRight();
    }

    private boolean isCursorRightTouch(float x) {
        return x > mCursorRightView.getLeft();
    }

    private boolean isCursorLeftTriggeredScroll;
    private boolean isCursorRightTriggeredScroll;

    @Override
    public boolean onDown(MotionEvent e) {
        isCursorLeftTriggeredScroll = isCursorLeftTouch(e.getX());
        isCursorRightTriggeredScroll = isCursorRightTouch(e.getX());
        return isCursorLeftTriggeredScroll || isCursorRightTriggeredScroll;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (isCursorLeftTriggeredScroll) {
            cursorLeftX -= distanceX/getWidth();
            cursorLeftX = Math.max(Math.min(cursorLeftX, cursorRightX - getMinTrimInSecond()), 0f);
            requestLayout();
            return true;
        } else if (isCursorRightTriggeredScroll) {
            cursorRightX -= distanceX/getWidth();
            cursorRightX = Math.min(Math.max(cursorRightX, cursorLeftX + getMinTrimInSecond()), 1.0f);
            requestLayout();
            return true;
        } else {
            return false;
        }
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

    private static final long US_PER_SECOND = 1000000l;
    public long getLeftCursorOffset() {
        return (long)(videoFrameView.getStartInSecond() + cursorLeftX * widthInSecond * US_PER_SECOND);
    }

    public long getRightCursorOffset() {
        return (long) (videoFrameView.getStartInSecond() + cursorRightX * widthInSecond * US_PER_SECOND);
    }

    public interface onTrimPositionListener {
        void onTrimPositionUpdated(float startInS, float endInS);
    }
}
