package todaytelawat.techandmore.com.todaytelawat

import android.Manifest
import android.app.DownloadManager
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import todaytelawat.techandmore.com.todaytelawat.api_container.EntriesItem
import todaytelawat.techandmore.com.todaytelawat.api_container.ResponseContainer
import todaytelawat.techandmore.com.todaytelawat.bodies.TelawatBody
import java.io.IOException
import java.util.Date

class MainActivity : AppCompatActivity(), OnRefreshListener {
    var builder: AlertDialog.Builder? = null
    private var view: View? = null
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private var telawatList: LinearLayout? = null
    var mPlayer: MediaPlayer? = MediaPlayer()
    var currentRow: View? = null
    var snackbar: Snackbar? = null
    var inflater: LayoutInflater? = null
    private var mediaFileLengthInMilliseconds = 0
    private val handler = Handler()
    override fun setContentView(layoutResID: Int) {
        view = layoutInflater.inflate(layoutResID, null, false)
        super.setContentView(view)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        swipeRefreshLayout = findViewById(R.id.swipeRefresh)
        swipeRefreshLayout.setOnRefreshListener(this)
        builder = AlertDialog.Builder(this@MainActivity)
        inflater = LayoutInflater.from(this)
        loadTelawat()
    }

    private fun loadTelawat() {
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.tvquran.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Create an instance of our GitHub API interface.
        val apis = retrofit.create(APIs::class.java)
        findViewById<View>(R.id.progressView).visibility = View.VISIBLE
        apis.postTodayTelawat(TelawatBody("ar", 1)).enqueue(object : Callback<ResponseContainer> {
            override fun onResponse(
                call: Call<ResponseContainer>,
                response: Response<ResponseContainer>
            ) {
                findViewById<View>(R.id.progressView).visibility = View.GONE
                fillTelawat(response.body()!!.eContent.entries)
            }

            override fun onFailure(call: Call<ResponseContainer>, t: Throwable) {
                snackbar = Snackbar.make(
                    view!!,
                    "حدث خطأ في الإتصال بالإنترنت يرجى المحالة لاحقا",
                    Snackbar.LENGTH_INDEFINITE
                )
                snackbar!!.setAction("إعادة المحاولة") {
                    snackbar!!.dismiss()
                    loadTelawat()
                }
                snackbar!!.show()
                findViewById<View>(R.id.progressView).visibility = View.GONE
            }
        })
    }

    private fun fillTelawat(entries: List<EntriesItem>) {
        telawatList = findViewById(R.id.telawatList)
        telawatList?.visibility = View.VISIBLE
        for (position in entries.indices) {
            val view = inflater!!.inflate(R.layout.telawa_row, telawatList, false)
            val avatar = view.findViewById<ImageView>(R.id.avatar)
            val mediaExitButton = view.findViewById<ImageView>(R.id.mediaExitButton)
            val seekBar = view.findViewById<SeekBar>(R.id.seekBar)
            val mediaTime = view.findViewById<TextView>(R.id.mediaTime)
            val name = view.findViewById<TextView>(R.id.name)
            val surah = view.findViewById<TextView>(R.id.surah)
            val mediaButton = view.findViewById<ImageView>(R.id.mediaButton)
            val share = view.findViewById<ImageView>(R.id.share)
            val download = view.findViewById<ImageView>(R.id.download)
            val mediaController = view.findViewById<View>(R.id.mediaController)
            val textContainer = view.findViewById<View>(R.id.textContainer)
            seekBar.max = 99 // It means 100% .0-99
            val item = entries[position]
            view.setTag(ITEM_KEY, item)
            view.setTag(ROW_INDEX, position)
            textContainer.setOnClickListener { v: View? ->
                playTelawa(
                    (view.getTag(ITEM_KEY) as EntriesItem).path,
                    view
                )
            }
            Picasso.with(this@MainActivity).load(item.reciterPhoto).into(avatar)
            name.text = item.reciterName
            surah.text = item.title
            download.setOnClickListener { v: View? ->
                startDownload(
                    (view.getTag(ITEM_KEY) as EntriesItem).path, (view.getTag(
                        ITEM_KEY
                    ) as EntriesItem).title
                )
            }
            share.setOnClickListener { v: View? ->
                val sendIntent = Intent()
                sendIntent.setAction(Intent.ACTION_SEND)
                sendIntent.putExtra(
                    Intent.EXTRA_TEXT, """
     ${item.title}
     ${item.reciterName}
     ${item.path}
     """.trimIndent()
                )
                sendIntent.setType("text/plain")
                startActivity(sendIntent)
            }
            telawatList?.addView(view)
        }
        if (telawatList != null && telawatList!!.getChildAt(0) != null) telawatList!!.getChildAt(0)
            .findViewById<View>(R.id.textContainer).performClick()
    }

    private fun playTelawa(path: String, view: View) {
        try {
            if (currentRow != null) {
                currentRow!!.findViewById<View>(R.id.mediaController).visibility = View.GONE
            }
            currentRow = view
            val avatar = view.findViewById<ImageView>(R.id.avatar)
            val mediaExitButton = view.findViewById<ImageView>(R.id.mediaExitButton)
            val seekBar = view.findViewById<SeekBar>(R.id.seekBar)
            val mediaTime = view.findViewById<TextView>(R.id.mediaTime)
            val name = view.findViewById<TextView>(R.id.name)
            val surah = view.findViewById<TextView>(R.id.surah)
            val mediaButton = view.findViewById<ImageView>(R.id.mediaButton)
            val share = view.findViewById<ImageView>(R.id.share)
            val download = view.findViewById<ImageView>(R.id.download)
            val mediaController = view.findViewById<View>(R.id.mediaController)
            val textContainer = view.findViewById<View>(R.id.textContainer)
            mPlayer!!.setOnCompletionListener(null)
            mPlayer!!.reset()
            mediaController.visibility = View.VISIBLE
            mPlayer!!.setDataSource(path)
            mPlayer!!.setAudioAttributes(
                AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            );

            // mp.prepare(); // don't use prepareAsync for mp3 playback
            //mp.start();
            mPlayer!!.prepareAsync()
            val progressDialog = ProgressDialog.show(this, "", "جاري تشغيل الملف الصوتي")
            progressDialog.setCancelable(true)
            mPlayer!!.setOnPreparedListener { mediaPlayer: MediaPlayer ->
                mediaFileLengthInMilliseconds =
                    mediaPlayer.duration // gets the song length in milliseconds from URL
                mediaPlayer.start()
                progressDialog.dismiss()
                primarySeekBarProgressUpdater(view, mediaPlayer)
                mPlayer!!.setOnCompletionListener { mp1: MediaPlayer? ->
                    if (currentRow == null) return@setOnCompletionListener
                    var position = currentRow!!.getTag(ROW_INDEX) as Int
                    currentRow!!.findViewById<View>(R.id.mediaController).visibility = View.GONE
                    if (++position < telawatList!!.childCount) {
                        telawatList!!.getChildAt(position).findViewById<View>(R.id.textContainer)
                            .performClick()
                    }
                }
            }


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
            seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {}
                override fun onStartTrackingTouch(seekBar: SeekBar) {}
                override fun onStopTrackingTouch(seekBar: SeekBar) {
                    if (mPlayer!!.isPlaying) {
                        val playPositionInMillisecconds =
                            mediaFileLengthInMilliseconds / 100 * seekBar.progress
                        mPlayer!!.seekTo(playPositionInMillisecconds)
                    }
                }
            })
            mPlayer!!.setOnBufferingUpdateListener { mp1: MediaPlayer?, percent: Int ->
                seekBar.secondaryProgress = percent
            }
            mediaButton.setOnClickListener { v: View? ->
                mediaFileLengthInMilliseconds =
                    mPlayer!!.duration // gets the song length in milliseconds from URL
                if (!mPlayer!!.isPlaying) {
                    mPlayer!!.start()
                    mediaButton.setImageResource(android.R.drawable.ic_media_pause)
                } else {
                    mPlayer!!.pause()
                    mediaButton.setImageResource(android.R.drawable.ic_media_play)
                }
                primarySeekBarProgressUpdater(view, mPlayer)
            }
            mediaExitButton.setOnClickListener { v: View? ->
                mPlayer!!.pause()
                mediaController.visibility = View.GONE
            }
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * Method which updates the SeekBar primary progress by current song playing position
     *
     * @param view
     * @param mp
     */
    @Synchronized
    private fun primarySeekBarProgressUpdater(view: View, mp: MediaPlayer?) {
        val seekBar: SeekBar = view.findViewById(R.id.seekBar)
        val mediaTime: TextView? = view.findViewById(R.id.mediaTime)
        val durationInMillis = mp!!.duration
        val curVolume = mp.currentPosition
        val HOUR = (60 * 60 * 1000).toLong()
        if (mediaTime != null) {
            if (durationInMillis > HOUR) {
                mediaTime.text = String.format(
                    "%1\$tH:%1\$tM:%1\$tS",
                    Date(curVolume.toLong())
                ) + " / " + String.format(
                    "%1\$tH:%1\$tM:%1\$tS",
                    Date(durationInMillis.toLong())
                )
            } else {
                mediaTime.text = String.format(
                    "%1\$tM:%1\$tS",
                    Date(curVolume.toLong())
                ) + " / " + String.format("%1\$tM:%1\$tS", Date(durationInMillis.toLong()))
            }
        }
        seekBar.progress =
            (mp.currentPosition.toFloat() / mediaFileLengthInMilliseconds * 100).toInt() // This math construction give a percentage of "was playing"/"song length"
        if (mp.isPlaying) {
            val notification = Runnable { primarySeekBarProgressUpdater(view, mp) }
            handler.postDelayed(notification, 1000)
            Log.d("handler", "delay applied")
        }
    }

    override fun onRefresh() {
        if (mPlayer != null) {
            mPlayer!!.stop()
        }
        if (telawatList != null) telawatList!!.removeAllViews()
        if (snackbar != null && snackbar!!.isShown) snackbar!!.dismiss()
        val handler = Handler(mainLooper)
        handler.postDelayed({ swipeRefreshLayout.isRefreshing = false }, 500)
        currentRow = null
        mPlayer!!.reset()
        loadTelawat()
    }

    fun startDownload(link: String?, name: String) {
        if (ContextCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@MainActivity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                WRITE_EXTERNAL_STORAGE_CODE
            )
            return
        } else {
            val mManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            val mRqRequest = DownloadManager.Request(
                Uri.parse(link)
            )
            mRqRequest.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                "$name.mp3"
            )
            mRqRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED) // to notify when download is complete
            mRqRequest.allowScanningByMediaScanner() // if you want to be available from media players
            val idDownLoad = mManager.enqueue(mRqRequest)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == WRITE_EXTERNAL_STORAGE_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startDownload((currentRow!!.getTag(ITEM_KEY) as EntriesItem).path, (currentRow!!.getTag(ITEM_KEY) as EntriesItem).title)
            } else {
                Toast.makeText(this@MainActivity, R.string.permissionDeniedError, Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    companion object {
        private const val ITEM_KEY = R.string.app_name
        private const val ROW_INDEX = R.string.permissionDeniedError
        private const val WRITE_EXTERNAL_STORAGE_CODE = 56
    }
}
