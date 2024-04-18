package todaytelawat.techandmore.com.todaytelawat.api_container

import com.google.gson.annotations.SerializedName

data class EContent(
    @SerializedName("sorted")
    var sorted: String? = null,

    @SerializedName("reciters")
    var reciters: List<RecitersItem>? = null,

    @SerializedName("entries")
    var entries: List<EntriesItem>

)