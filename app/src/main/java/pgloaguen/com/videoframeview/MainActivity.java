package pgloaguen.com.videoframeview;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pgloaguen.com.library.VideoFrameView;
import pgloaguen.com.library.VideoTrimView;

public class MainActivity extends AppCompatActivity implements MainContract.View {

    private static final int SELECT_VIDEO = 1000;

    @Bind(R.id.videotrim) VideoTrimView videoTrimView;
    @Bind(R.id.range) TextView rangeTextView;
    @Bind(R.id.videoframe) VideoFrameView videoFrameView;

    @OnClick(R.id.btn)
    void onPickButtonClicked(View view) {
        showPickVideoUi();
    }

    private MainContract.UserActionsListener mActionListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mActionListener = new MainPresenter(this);

        videoTrimView.setOnTrimPositionListener(new VideoTrimView.onTrimPositionListener() {
            @Override
            public void onTrimPositionUpdated(float startInS, float endInS) {
                mActionListener.updateTrimPosition(startInS, endInS);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == SELECT_VIDEO){
            Uri videoUri = data.getData();
            mActionListener.updateTrimmingVideoUrl(videoUri);
        }
    }

    @Override
    public void setTrimRangeText(String positionText) {
        rangeTextView.setText(positionText);
    }

    @Override
    public void resetTrimView(Uri videoUri, int trimStepDuration) {
        videoTrimView.setVideo(videoUri);
        videoTrimView.setWidthInSecond(trimStepDuration);

    }

    @Override
    public void resetVideoView(Uri videoUri, int trimStepDuration) {
        videoFrameView.setVideo(videoUri);
        videoFrameView.setWidthInSecond(trimStepDuration);

        videoFrameView.setDelegateAdapter(new VideoFrameView.FrameAdapterDelegate<FrameViewHolder>() {
            @Override
            public FrameViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                return new FrameViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_frame, viewGroup, false));
            }

            @Override
            public void onBindViewHolder(final FrameViewHolder viewHolder, final int i) {
                int borderVisibility = mActionListener.testSelectedIndex(i) ? View.VISIBLE : View.GONE;
                viewHolder.borderView.setVisibility(borderVisibility);
                viewHolder.imgView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mActionListener.updateSelectedIndex(i);
                    }
                });
            }

            @Override
            public ImageView getImageViewToDisplayFrame(FrameViewHolder viewHolder) {
                return viewHolder.imgView;
            }

            @Override
            public int getPhotoWidth() {
                return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 128, getResources().getDisplayMetrics());
            }
        });
    }

    @Override
    public void notifySelectedIndexChanged() {
        videoFrameView.getAdapter().notifyDataSetChanged();
    }

    private static class FrameViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgView;
        private View borderView;

        public FrameViewHolder(View itemView) {
            super(itemView);
            imgView = (ImageView) itemView.findViewById(R.id.img);
            borderView = itemView.findViewById(R.id.border);
        }
    }

    private void showPickVideoUi() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("video/*");
        startActivityForResult(photoPickerIntent, SELECT_VIDEO);
    }
}
