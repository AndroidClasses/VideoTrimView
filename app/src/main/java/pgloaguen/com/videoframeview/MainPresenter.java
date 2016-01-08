package pgloaguen.com.videoframeview;

import android.net.Uri;

/**
 * Created by yangfeng on 16-1-8.
 */
public class MainPresenter implements MainContract.UserActionsListener {

    private static final int TRIM_STEP_DURATION = 15;

    private int selectedPosition = 0;

    MainContract.View mMainView;

    public MainPresenter(MainContract.View view) {
        this.mMainView = view;
    }

    @Override
    public void updateTrimPosition(float startInS, float endInS) {
        mMainView.setTrimRangeText(startInS + " " + endInS);
    }

    @Override
    public void updateTrimmingVideoUrl(Uri videoUri) {
        mMainView.resetTrimView(videoUri, TRIM_STEP_DURATION);
        mMainView.resetVideoView(videoUri, TRIM_STEP_DURATION);
        selectedPosition = 0;
    }

    @Override
    public boolean testSelectedIndex(int index) {
        return index == selectedPosition;
    }

    @Override
    public void updateSelectedIndex(int index) {
        if (index == selectedPosition) {
            // do nothing is alright?
        } else {
            selectedPosition = index;
            mMainView.notifySelectedIndexChanged();
        }
    }
}
