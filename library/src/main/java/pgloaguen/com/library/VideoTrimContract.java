/*
 * Copyright 2015, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pgloaguen.com.library;

import android.net.Uri;

import java.io.File;

/**
 * This specifies the contract between the view and the presenter.
 */
public interface VideoTrimContract {

    interface View {
        void layoutLeftCursor(int xLeft, int yTop, int xRight, int yBottom);
        void layoutRightCursor(int xLeft, int yTop, int xRight, int yBottom);

        void setTrimRange(float startInS, float endInS);

        void notifyTrimWidthChanged(float second);

        void notifyVideoSourceChanged(Uri uri);
        void notifyVideoSourceChanged(File file);
        void notifyVideoSourceChanged(String path);

        void notifyCursorChanged(boolean left);
    }

    interface UserActionsListener {
        void onLayout(int left, int top, int right, int bottom);

        void onTouchEventDispatch(float startInSecond, float maxOffsetInSecond);

        void updateTrimWidthInSecond(float second);

        void updateVideoSource(Uri uri);
        void updateVideoSource(File file);
        void updateVideoSource(String path);

        boolean onTouchDown(boolean isLeftCursor, boolean isRightCursor);

        boolean onTouchDrag(float percent);
    }
}
