package todaytelawat.techandmore.com.todaytelawat

import android.Manifest
import android.app.DownloadManager
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
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

    lateinit var telawatRT: RecyclerView
    var mPlayer: MediaPlayer? = MediaPlayer()
    var currentRow: View? = null
    var currentRowModel: EntriesItem? = null
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
        loadTelawat()

        setupToolbar()
    }

    private fun setupToolbar() {
        findViewById<Toolbar>(R.id.toolbar).setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.about -> {
                    val dialog = AlertDialog.Builder(this).setMessage(
                        """
    تطوير مصعب العثمان
    للاقتراحات التواصل على
    musab.on@gmail.com
    twitter: _abos3d_
    لاتنسونا من صالح دعائكم
    """.trimIndent()
                    ).create()
                    dialog.show()
                    return@setOnMenuItemClickListener true
                }
                R.id.mode -> {

                    if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES)
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    else
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

                    SharedPrefManager(getDefaultSharedPreferences(this)).mode = AppCompatDelegate.getDefaultNightMode()

                    return@setOnMenuItemClickListener true
                }
                else -> return@setOnMenuItemClickListener false
            }
        }
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
                val snackbar = Snackbar.make(
                    view!!,
                    "حدث خطأ في الإتصال بالإنترنت يرجى المحالة لاحقا",
                    Snackbar.LENGTH_SHORT
                )
                snackbar.setAction("إعادة المحاولة") {
                    snackbar.dismiss()
                    loadTelawat()
                }
                snackbar.show()
                findViewById<View>(R.id.progressView).visibility = View.GONE
            }
        })
    }




    private fun fillTelawat(entries: List<EntriesItem>) {
        telawatRT = findViewById(R.id.telawatRT)
        telawatRT.visibility = View.VISIBLE
        val inflater = layoutInflater


        telawatRT.layoutManager = LinearLayoutManager(this)

        telawatRT.adapter = object : RecyclerView.Adapter<ViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
                val view = inflater.inflate(R.layout.telawa_row, parent, false)
                return object : ViewHolder(view){}
            }

            override fun getItemCount(): Int = entries.size

            override fun onBindViewHolder(holder: ViewHolder, position: Int) {
                val view = holder.itemView

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

                view.setOnClickListener {
                    if (currentRow != null) {
                        currentRow!!.findViewById<View>(R.id.mediaController).visibility = View.GONE
                    }
                    currentRow = view
                    currentRowModel = entries[position]

                    playTelawa(entries[position].path, view)
                }
                Picasso.with(this@MainActivity).load(entries[position].reciterPhoto).into(avatar)
                name.text = entries[position].reciterName
                surah.text = entries[position].title

                download.setOnClickListener { startDownload(entries[position].path, entries[position].title) }

                share.setOnClickListener {
                    val sendIntent = Intent()
                    sendIntent.setAction(Intent.ACTION_SEND)
                    sendIntent.putExtra(
                        Intent.EXTRA_TEXT, """
     ${entries[position].title}
     ${entries[position].reciterName}
     ${entries[position].path}
     """.trimIndent()
                    )
                    sendIntent.setType("text/plain")
                    startActivity(sendIntent)
                }
            }

        }


        lifecycleScope.launch {
            flow {
                while (telawatRT.getChildAt(0) == null) { }
                emit(0)
            }.flowOn(Dispatchers.IO)
                .collect {
                    telawatRT.getChildAt(0)?.let {
                        it.performClick()
                    }
                }
        }




    }
    private fun playTelawa(path: String, view: View) {
        try {
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
            mediaButton.setImageResource(android.R.drawable.ic_media_pause)
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
                lifecycleScope.launch { primarySeekBarProgressUpdater(view, mediaPlayer) }
                mPlayer!!.setOnCompletionListener { mp1: MediaPlayer? ->
                    if (currentRow == null) return@setOnCompletionListener
                    var position = telawatRT.getChildAdapterPosition(currentRow!!)
                    currentRow!!.findViewById<View>(R.id.mediaController).visibility = View.GONE
                    if (++position < telawatRT.adapter!!.itemCount) {
                        telawatRT.getChildAt(position).performClick()
                    }
                }
            }


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
            mediaButton.setOnClickListener {
                mediaFileLengthInMilliseconds =
                    mPlayer!!.duration // gets the song length in milliseconds from URL
                if (!mPlayer!!.isPlaying) {
                    mPlayer!!.start()
                    mediaButton.setImageResource(android.R.drawable.ic_media_pause)
                } else {
                    mPlayer!!.pause()
                    mediaButton.setImageResource(android.R.drawable.ic_media_play)
                }
//                primarySeekBarProgressUpdater(view, mPlayer)
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
    private suspend fun primarySeekBarProgressUpdater(view: View, mp: MediaPlayer?) {
        val seekBar: SeekBar = view.findViewById(R.id.seekBar)
        val mediaTime: TextView? = view.findViewById(R.id.mediaTime)
        val durationInMillis = mp!!.duration
        val curVolume = mp.currentPosition
        val HOUR = (60 * 60 * 1000).toLong()
        runOnUiThread {
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

            seekBar.progress = (mp.currentPosition.toFloat() / mediaFileLengthInMilliseconds * 100).toInt() // This math construction give a percentage of "was playing"/"song length"
        }

        if (mp.isPlaying) {
            delay(1000)
            primarySeekBarProgressUpdater(view, mp)
            Log.d("handler", "delay applied")
        }
    }

    override fun onRefresh() {
        if (mPlayer != null) {
            mPlayer!!.stop()
        }
        findViewById<RecyclerView>(R.id.telawatRT).removeAllViews()
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
                startDownload(currentRowModel!!.path, currentRowModel!!.title)
            } else {
                Toast.makeText(this@MainActivity, R.string.permissionDeniedError, Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    companion object {
        private const val WRITE_EXTERNAL_STORAGE_CODE = 56
    }
}
