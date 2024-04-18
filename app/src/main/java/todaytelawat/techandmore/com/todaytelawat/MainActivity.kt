package todaytelawat.techandmore.com.todaytelawat;

import android.Manifest;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import todaytelawat.techandmore.com.todaytelawat.api_container.EntriesItem;
import todaytelawat.techandmore.com.todaytelawat.api_container.ResponseContainer;
import todaytelawat.techandmore.com.todaytelawat.bodies.TelawatBody;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private static final int ITEM_KEY = R.string.app_name;
    private static final int ROW_INDEX = R.string.permissionDeniedError;
    private static final int WRITE_EXTERNAL_STORAGE_CODE = 56;
    AlertDialog.Builder builder;
    private View view;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout telawatList;
    MediaPlayer mPlayer = new MediaPlayer();
    View currentRow = null;
    Snackbar snackbar;

    LayoutInflater inflater;
    private int mediaFileLengthInMilliseconds;

    private final Handler handler = new Handler();

    @Override
    public void setContentView(int layoutResID) {
        view = getLayoutInflater().inflate(layoutResID, null, false);
        super.setContentView(view);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipeRefreshLayout = findViewById(R.id.swipeRefresh);

        swipeRefreshLayout.setOnRefreshListener(this);

        builder = new AlertDialog.Builder(MainActivity.this);

        inflater = LayoutInflater.from(this);
        loadTelawat();
    }

    private void loadTelawat() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.tvquran.com/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Create an instance of our GitHub API interface.
        APIs apis = retrofit.create(APIs.class);


        findViewById(R.id.progressView).setVisibility(View.VISIBLE);


        apis.postTodayTelawat(new TelawatBody("ar", 1)).enqueue(new Callback<ResponseContainer>() {
            @Override
            public void onResponse(Call<ResponseContainer> call, Response<ResponseContainer> response) {
                findViewById(R.id.progressView).setVisibility(View.GONE);
                fillTelawat(response.body().getEContent().getEntries());
            }

            @Override
            public void onFailure(final Call<ResponseContainer> call, Throwable t) {
                snackbar = Snackbar.make(view, "حدث خطأ في الإتصال بالإنترنت يرجى المحالة لاحقا", Snackbar.LENGTH_INDEFINITE);
                snackbar.setAction("إعادة المحاولة", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        snackbar.dismiss();
                        loadTelawat();
                    }
                });
                snackbar.show();
                findViewById(R.id.progressView).setVisibility(View.GONE);
            }
        });

    }

    private void fillTelawat(final List<EntriesItem> entries) {
        telawatList = findViewById(R.id.telawatList);
        telawatList.setVisibility(View.VISIBLE);
        for (int position = 0; position < entries.size(); position++) {
            View view = inflater.inflate(R.layout.telawa_row, telawatList, false);

            ImageView avatar = view.findViewById(R.id.avatar);
            ImageView mediaExitButton = view.findViewById(R.id.mediaExitButton);
            SeekBar seekBar = view.findViewById(R.id.seekBar);
            TextView mediaTime = view.findViewById(R.id.mediaTime);
            TextView name = view.findViewById(R.id.name);
            TextView surah = view.findViewById(R.id.surah);
            ImageView mediaButton = view.findViewById(R.id.mediaButton);
            ImageView share = view.findViewById(R.id.share);
            ImageView download = view.findViewById(R.id.download);
            View mediaController = view.findViewById(R.id.mediaController);
            View textContainer = view.findViewById(R.id.textContainer);

            seekBar.setMax(99); // It means 100% .0-99

            final EntriesItem item = entries.get(position);

            view.setTag(ITEM_KEY, item);
            view.setTag(ROW_INDEX, position);

            textContainer.setOnClickListener(v -> playTelawa(((EntriesItem) view.getTag(ITEM_KEY)).getPath(), view));

            Picasso.with(MainActivity.this).load(item.getReciterPhoto()).into(avatar);
            name.setText(item.getReciterName());
            surah.setText(item.getTitle());

            download.setOnClickListener(v -> startDownload(((EntriesItem) view.getTag(ITEM_KEY)).getPath(), ((EntriesItem) view.getTag(ITEM_KEY)).getTitle()));

            share.setOnClickListener(v -> {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, item.getTitle() + "\n" + item.getReciterName() + "\n" + item.getPath());
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            });

            telawatList.addView(view);
        }

        if (telawatList != null && telawatList.getChildAt(0) != null)
            telawatList.getChildAt(0).findViewById(R.id.textContainer).performClick();
    }

    private void playTelawa(String path, View view) {
        try {
            if (currentRow != null) {
                currentRow.findViewById(R.id.mediaController).setVisibility(View.GONE);
            }

            currentRow = view;

            ImageView avatar = view.findViewById(R.id.avatar);
            ImageView mediaExitButton = view.findViewById(R.id.mediaExitButton);
            SeekBar seekBar = view.findViewById(R.id.seekBar);
            TextView mediaTime = view.findViewById(R.id.mediaTime);
            TextView name = view.findViewById(R.id.name);
            TextView surah = view.findViewById(R.id.surah);
            ImageView mediaButton = view.findViewById(R.id.mediaButton);
            ImageView share = view.findViewById(R.id.share);
            ImageView download = view.findViewById(R.id.download);
            View mediaController = view.findViewById(R.id.mediaController);
            View textContainer = view.findViewById(R.id.textContainer);

            mPlayer.setOnCompletionListener(null);
            mPlayer.reset();

            mediaController.setVisibility(View.VISIBLE);

            mPlayer.setDataSource(path);


            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            // mp.prepare(); // don't use prepareAsync for mp3 playback
            //mp.start();
            mPlayer.prepareAsync();

            final ProgressDialog progressDialog = ProgressDialog.show(this, "", "جاري تشغيل الملف الصوتي");
            progressDialog.setCancelable(true);

            mPlayer.setOnPreparedListener(mediaPlayer -> {
                mediaFileLengthInMilliseconds = mediaPlayer.getDuration(); // gets the song length in milliseconds from URL
                mediaPlayer.start();
                progressDialog.dismiss();
                primarySeekBarProgressUpdater(view, mediaPlayer);

                mPlayer.setOnCompletionListener(mp1 -> {
                    if (currentRow == null)
                        return;
                    int position = ((int) currentRow.getTag(ROW_INDEX));

                    currentRow.findViewById(R.id.mediaController).setVisibility(View.GONE);
                    if (++position < telawatList.getChildCount()) {
                        telawatList.getChildAt(position).findViewById(R.id.textContainer).performClick();
                    }
                });
            });


//            seekBar.setOnTouchListener((v, event) -> {
//                /** Seekbar onTouch event handler. Method which seeks MediaPlayer to seekBar primary progress position*/
//                if (mPlayer.isPlaying()) {
//                    SeekBar sb = (SeekBar) v;
//                    int playPositionInMillisecconds = (mediaFileLengthInMilliseconds / 100) * sb.getProgress();
//                    mPlayer.seekTo(playPositionInMillisecconds);
//                }
//                return false;
//            });
//
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    if (mPlayer.isPlaying()) {
                        int playPositionInMillisecconds = (mediaFileLengthInMilliseconds / 100) * seekBar.getProgress();
                        mPlayer.seekTo(playPositionInMillisecconds);
                    }
                }
            });

            mPlayer.setOnBufferingUpdateListener((mp1, percent) -> seekBar.setSecondaryProgress(percent));

            mediaButton.setOnClickListener(v -> {
                mediaFileLengthInMilliseconds = mPlayer.getDuration(); // gets the song length in milliseconds from URL
                if (!mPlayer.isPlaying()) {
                    mPlayer.start();
                    mediaButton.setImageResource(android.R.drawable.ic_media_pause);
                } else {
                    mPlayer.pause();
                    mediaButton.setImageResource(android.R.drawable.ic_media_play);
                }
                primarySeekBarProgressUpdater(view, mPlayer);
            });


            mediaExitButton.setOnClickListener(v -> {
                mPlayer.pause();
                mediaController.setVisibility(View.GONE);
            });


        } catch (IllegalArgumentException | IllegalStateException | IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Method which updates the SeekBar primary progress by current song playing position
     *
     * @param view
     * @param mp
     */
    synchronized private void primarySeekBarProgressUpdater(View view, MediaPlayer mp) {
        SeekBar seekBar;
        TextView mediaTime;

        seekBar = view.findViewById(R.id.seekBar);
        mediaTime = view.findViewById(R.id.mediaTime);

        int durationInMillis = mp.getDuration();
        int curVolume = mp.getCurrentPosition();
        long HOUR = 60 * 60 * 1000;
        if (mediaTime != null) {
            if (durationInMillis > HOUR) {
                mediaTime.setText(String.format("%1$tH:%1$tM:%1$tS", new Date(curVolume))
                        + " / " + String.format("%1$tH:%1$tM:%1$tS", new Date(durationInMillis)));
            } else {
                mediaTime.setText(String.format("%1$tM:%1$tS", new Date(curVolume))
                        + " / " + String.format("%1$tM:%1$tS", new Date(durationInMillis)));
            }
        }

        seekBar.setProgress((int) (((float) mp.getCurrentPosition() / mediaFileLengthInMilliseconds) * 100)); // This math construction give a percentage of "was playing"/"song length"
        if (mp.isPlaying()) {
            Runnable notification = () -> primarySeekBarProgressUpdater(view, mp);
            handler.postDelayed(notification, 1000);
            Log.d("handler", "delay applied");
        }
    }


    @Override
    public void onRefresh() {
        if (mPlayer != null) {
            mPlayer.stop();
        }
        if (telawatList != null)
            telawatList.removeAllViews();

        if (snackbar != null && snackbar.isShown())
            snackbar.dismiss();

        Handler handler = new Handler(getMainLooper());
        handler.postDelayed(() -> swipeRefreshLayout.setRefreshing(false), 500);
        currentRow = null;
        mPlayer.reset();
        loadTelawat();
    }


    public void startDownload(String link, String name) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    WRITE_EXTERNAL_STORAGE_CODE);


            return;
        } else {
            DownloadManager mManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            DownloadManager.Request mRqRequest = new DownloadManager.Request(
                    Uri.parse(link));


            mRqRequest.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, name + ".mp3");
            mRqRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); // to notify when download is complete
            mRqRequest.allowScanningByMediaScanner();// if you want to be available from media players

            long idDownLoad = mManager.enqueue(mRqRequest);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == WRITE_EXTERNAL_STORAGE_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startDownload(((EntriesItem) currentRow.getTag(ITEM_KEY)).getPath(), ((EntriesItem) currentRow.getTag(ITEM_KEY)).getTitle());
            } else {
                Toast.makeText(MainActivity.this, R.string.permissionDeniedError, Toast.LENGTH_LONG).show();
            }
        }
    }
}
