package todaytelawat.techandmore.com.todaytelawat.api_container

import com.google.gson.annotations.SerializedName

data class ResponseContainer(
    @SerializedName("eContent")
    var eContent: EContent,

    @SerializedName("eDesc")
    var eDesc: String? = null,

    @SerializedName("eCode")
    var eCode: Int = 0,
    )