package pgloaguen.com.library;

import android.net.Uri;
import android.view.View;

import java.io.File;

/**
 * Created by yangfeng on 16-1-8.
 */
public interface FrameHost {
    View getView();

    float getStartInMs();
    long getVideoDurationInMs();

    void setWidthInSecond(float second);

    void setVideo(Uri uri);
    void setVideo(File file);
    void setVideo(String path);
}
