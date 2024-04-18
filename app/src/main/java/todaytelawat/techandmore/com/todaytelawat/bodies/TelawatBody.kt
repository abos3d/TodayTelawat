package todaytelawat.techandmore.com.todaytelawat.bodies

import com.google.gson.annotations.SerializedName

data class TelawatBody(
    @SerializedName("locale")
    var locale: String? = null,

    @SerializedName("sort")
    var sort: Int = 0
)
