package todaytelawat.techandmore.com.todaytelawat

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

class SharedPrefManager(sharedPreferences: SharedPreferences?) {
    private val LAST_PAGE = "last_page"
    val DATA = "data"
    val MODE = "mode"
    var sharedPreferences: SharedPreferences? = null


    var mode: Int
        get() = sharedPreferences!!.getInt(MODE, AppCompatDelegate.MODE_NIGHT_YES)
        set(mode) {
            sharedPreferences!!.edit().putInt(MODE, mode).apply()
        }

    init {
        this.sharedPreferences = sharedPreferences
    }
}