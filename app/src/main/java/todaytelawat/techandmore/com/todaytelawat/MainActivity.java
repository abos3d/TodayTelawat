package todaytelawat.techandmore.com.todaytelawat;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

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

    private static final int ITEM_KEY = R.id.ifRoom;
    private static final int ROW_INDEX = R.id.clip_vertical;
    private static final int WRITE_EXTERNAL_STORAGE_CODE = 56;
    AlertDialog.Builder builder;
    private View view;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout telawatList;
    MediaPlayer currentMp = null;
    View currentRow = null;

    LayoutInflater inflater;
    private int mediaFileLengthInMilliseconds;

    private final Handler handler = new Handler();

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        view = super.onCreateView(name, context, attrs);
        return view;
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        view = super.onCreateView(name, context, attrs);
        return view;
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
                .baseUrl("http://api.tvquran.com/")
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
                final Snackbar s = Snackbar.make(view, "حدث خطأ في الإتصال بالإنترنت يرجى المحالة لاحقا", Snackbar.LENGTH_INDEFINITE);
                s.setAction("إعادة المحاولة", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        s.dismiss();
                        loadTelawat();
                    }
                });
                findViewById(R.id.progressView).setVisibility(View.GONE);
            }
        });

    }

    private void fillTelawat(final List<EntriesItem> entries) {
        telawatList = findViewById(R.id.telawatList);
        telawatList.setVisibility(View.VISIBLE);
        for (int position = 0; position < entries.size(); position++) {
            View view = inflater.inflate(R.layout.telawa_row, telawatList, false);

            ImageView avatar;
            ImageView mediaExitButton;
            SeekBar seekBar;
            TextView mediaTime;
            TextView name;
            TextView surah;
            ImageView mediaButton;
            ImageView share;
            ImageView download;
            View mediaController;
            View textContainer;
            avatar = view.findViewById(R.id.avatar);
            mediaExitButton = view.findViewById(R.id.mediaExitButton);
            seekBar = view.findViewById(R.id.seekBar);
            mediaTime = view.findViewById(R.id.mediaTime);
            name = view.findViewById(R.id.name);
            surah = view.findViewById(R.id.surah);
            mediaButton = view.findViewById(R.id.mediaButton);
            share = view.findViewById(R.id.share);
            download = view.findViewById(R.id.download);
            mediaController = view.findViewById(R.id.mediaController);
            textContainer = view.findViewById(R.id.textContainer);


            final EntriesItem item = entries.get(position);

            view.setTag(ITEM_KEY, item);
            view.setTag(ROW_INDEX, position);

            textContainer.setOnClickListener(v -> playTelawa(((EntriesItem) view.getTag(ITEM_KEY)).getPath(), view));

            Picasso.with(MainActivity.this).load(item.getReciterPhoto()).into(avatar);
            name.setText(item.getReciterName());
            surah.setText(item.getTitle());

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
            ImageView avatar;
            ImageView mediaExitButton;
            SeekBar seekBar;
            TextView mediaTime;
            TextView name;
            TextView surah;
            ImageView mediaButton;
            ImageView share;
            ImageView download;
            View mediaController;
            avatar = view.findViewById(R.id.avatar);
            mediaExitButton = view.findViewById(R.id.mediaExitButton);
            seekBar = view.findViewById(R.id.seekBar);
            mediaTime = view.findViewById(R.id.mediaTime);
            name = view.findViewById(R.id.name);
            surah = view.findViewById(R.id.surah);
            mediaButton = view.findViewById(R.id.mediaButton);
            share = view.findViewById(R.id.share);
            download = view.findViewById(R.id.download);
            mediaController = view.findViewById(R.id.mediaController);


            download.setOnClickListener(v -> startDownload(((EntriesItem) view.getTag(ITEM_KEY)).getPath(), ((EntriesItem) view.getTag(ITEM_KEY)).getTitle()));

            share.setOnClickListener(v -> {
                EntriesItem item = ((EntriesItem) view.getTag(ITEM_KEY));
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, item.getTitle() + "\n" + item.getReciterName() + "\n" + item.getPath());
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            });

            MediaPlayer mp = new MediaPlayer();

            if (currentMp != null)
                currentMp.pause();

            currentMp = mp;

            mediaController.setVisibility(View.VISIBLE);

            mp.setDataSource(path);


            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);

            mp.prepare(); // don't use prepareAsync for mp3 playback
            mp.start();
//            mp.prepareAsync();

//            mp.setOnPreparedListener(mediaPlayer -> {
//                mediaPlayer.start();
//            });

            mediaFileLengthInMilliseconds = mp.getDuration(); // gets the song length in milliseconds from URL


            seekBar.setOnTouchListener((v, event) -> {
                /** Seekbar onTouch event handler. Method which seeks MediaPlayer to seekBar primary progress position*/
                if (mp.isPlaying()) {
                    SeekBar sb = (SeekBar) v;
                    int playPositionInMillisecconds = (mediaFileLengthInMilliseconds / 100) * sb.getProgress();
                    mp.seekTo(playPositionInMillisecconds);
                }
                return false;
            });
            mp.setOnBufferingUpdateListener((mp1, percent) -> seekBar.setSecondaryProgress(percent));
            mp.setOnCompletionListener(mp1 -> {
                int position = ((int) currentRow.getTag(ROW_INDEX));

                currentRow.findViewById(R.id.mediaController).setVisibility(View.GONE);
                if (++position < telawatList.getChildCount()) {
                    telawatList.getChildAt(position).performClick();
                }
            });


            primarySeekBarProgressUpdater(view, mp);


            mediaButton.setOnClickListener(v -> {
                mediaFileLengthInMilliseconds = mp.getDuration(); // gets the song length in milliseconds from URL
                if (!mp.isPlaying()) {
                    mp.start();
                    mediaButton.setImageResource(android.R.drawable.ic_media_pause);
                } else {
                    mp.pause();
                    mediaButton.setImageResource(android.R.drawable.ic_media_play);
                }
                primarySeekBarProgressUpdater(view, mp);
            });


            mediaExitButton.setOnClickListener(v -> {
                mp.pause();
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
    private void primarySeekBarProgressUpdater(View view, MediaPlayer mp) {
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
        }
    }


    @Override
    public void onRefresh() {
        if (currentMp != null) {
            currentMp.stop();
        }
        telawatList.removeAllViews();

        Handler handler = new Handler(getMainLooper());
        handler.postDelayed(() -> swipeRefreshLayout.setRefreshing(false), 500);
        currentRow = null;
        currentMp = null;
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
