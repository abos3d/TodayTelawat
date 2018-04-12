package todaytelawat.techandmore.com.todaytelawat;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

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

public class MainActivity extends AppCompatActivity {

    private static final int HOLDER_KEY = R.id.all;
    private static final int ITEM_KEY = R.id.ifRoom;
    AlertDialog.Builder builder;
    private View view;
    private LinearLayout telawatList;

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


            final EntriesItem item = entries.get(position);

            view.setTag(ITEM_KEY, item);

            view.setOnClickListener(v -> playTelawa(((EntriesItem) v.getTag(ITEM_KEY)).getPath(), v));

            Picasso.with(MainActivity.this).load(item.getReciterPhoto()).into(avatar);
            name.setText(item.getReciterName());
            surah.setText(item.getTitle());

            telawatList.addView(view);
        }
    }

    private void playTelawa(String path, View view) {
        try {

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


            MediaPlayer mp = new MediaPlayer();



            mediaController.setVisibility(View.VISIBLE);

            mp.setDataSource(path);

//            mp.prepareAsync();

            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);

            mp.prepare(); // don't use prepareAsync for mp3 playback

            mp.start();

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

            });


            primarySeekBarProgressUpdater(view, mp);


            mediaButton.setOnClickListener(v -> {
                mediaFileLengthInMilliseconds = mp.getDuration(); // gets the song length in milliseconds from URL
                if (!mp.isPlaying()) {
                    mp.start();
                    //holder.mediaButton.setImageResource(R.drawable.ic_pause);
                } else {
                    mp.pause();
                    //holder.mediaButton.setImageResource(R.drawable.ic_play);
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
            Runnable notification = new Runnable() {
                public void run() {
                    primarySeekBarProgressUpdater(view, mp);
                }
            };
            handler.postDelayed(notification, 1000);
        }
    }


}