package pgloaguen.com.library;

import android.net.Uri;

import java.io.File;

/**
 * Created by yangfeng on 16-1-8.
 */
public class VideoTrimPresenter implements VideoTrimContract.UserActionsListener {

    private boolean hasAVideo;

    private final float minTrimInSecond = 3;
    private float widthInSecond = 15;
    private float minBetweenCursorInPercent = minTrimInSecond / widthInSecond;

    private float cursorLeftX = 0f;
    private float cursorRightX = 1.0f;
    private int demiCursorWidth = 40;


    private boolean isCursorLeftTriggeredScroll;
    private boolean isCursorRightTriggeredScroll;

    private VideoTrimContract.View mTrimView;
    public VideoTrimPresenter(VideoTrimContract.View view) {
        mTrimView = view;
    }

    @Override
    public void onLayout(int left, int top, int right, int bottom) {
        int height = bottom - top;
        int width = (right - left);

        int rightCursorLeftPosition = (int) ((cursorLeftX * width) + demiCursorWidth);
        int xLeft = rightCursorLeftPosition - width;
        int xRight = rightCursorLeftPosition;
        mTrimView.layoutLeftCursor(xLeft, 0, xRight, height);

        int leftCursorRightPosition = (int) ((cursorRightX * width) - demiCursorWidth);
        xLeft = leftCursorRightPosition;
        xRight = leftCursorRightPosition + width;
        mTrimView.layoutRightCursor(xLeft, 0, xRight, height);
    }

    @Override
    public void onTouchEventDispatch(float startInSecond, float maxOffsetInSecond) {
        if (hasAVideo) {
            float startInS = startInSecond + (widthInSecond * cursorLeftX);
            float endInS = Math.min(maxOffsetInSecond, startInSecond + (widthInSecond * cursorRightX));
            mTrimView.setTrimRange(startInS, endInS);
        }
    }

    @Override
    public void updateTrimWidthInSecond(float second) {
        widthInSecond = second;
        minBetweenCursorInPercent =  minTrimInSecond / widthInSecond;
        mTrimView.notifyTrimWidthChanged(second);
    }

    @Override
    public void updateVideoSource(Uri uri) {
        hasAVideo = uri != null;
        mTrimView.notifyVideoSourceChanged(uri);
    }

    @Override
    public void updateVideoSource(File file) {
        hasAVideo = file != null;
        mTrimView.notifyVideoSourceChanged(file);
    }

    @Override
    public void updateVideoSource(String path) {
        hasAVideo = path != null;
        mTrimView.notifyVideoSourceChanged(path);
    }

    @Override
    public boolean onTouchDown(boolean isLeftCursor, boolean isRightCursor) {
        isCursorLeftTriggeredScroll = isLeftCursor;
        isCursorRightTriggeredScroll = isRightCursor;
        return isCursorLeftTriggeredScroll || isCursorRightTriggeredScroll;
    }

    @Override
    public boolean onTouchDrag(float percent) {
        if (isCursorLeftTriggeredScroll) {
            cursorLeftX -= percent;
            cursorLeftX = Math.max(Math.min(cursorLeftX, cursorRightX - minBetweenCursorInPercent), 0f);
            notifyLeftCursorChanged();
            return true;
        } else if (isCursorRightTriggeredScroll) {
            cursorRightX -= percent;
            cursorRightX = Math.min(Math.max(cursorRightX, cursorLeftX + minBetweenCursorInPercent), 1.0f);
            notifyRightCursorChanged();
            return true;
        } else {
            return false;
        }
    }

    private void notifyRightCursorChanged() {
        mTrimView.notifyCursorChanged(true);
    }

    private void notifyLeftCursorChanged() {
        mTrimView.notifyCursorChanged(false);
    }
}
