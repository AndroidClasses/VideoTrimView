package pgloaguen.com.library;

/**
 * Created by yangfeng on 16-1-8.
 */
public class VideoTrimPresenter implements VideoTrimContract.UserActionsListener {
    private VideoTrimContract.View mTrimView;
    public VideoTrimPresenter(VideoTrimContract.View view) {
        mTrimView = view;
    }
}
