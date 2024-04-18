package todaytelawat.techandmore.com.todaytelawat.api_container

import com.google.gson.annotations.SerializedName

data class RecitersItem(
    @SerializedName("brief_biography")
    var briefBiography: String? = null,

    @SerializedName("total_listened")
    var totalListened: String? = null,

    @SerializedName("full_biography")
    var fullBiography: String? = null,

    @SerializedName("recitations_count")
    var recitationsCount: Int = 0,

    @SerializedName("name")
    var name: String? = null,

    @SerializedName("photo")
    var photo: String? = null,

    @SerializedName("id")
    var id: Int = 0,

    @SerializedName("total_likes")
    var totalLikes: Int = 0,
)