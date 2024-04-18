package todaytelawat.techandmore.com.todaytelawat.api_container

import com.google.gson.annotations.SerializedName

data class EntriesItem (
    @SerializedName("reciter_name")
    var reciterName: String? = null,

    @SerializedName("category_name")
    var categoryName: String? = null,

    @SerializedName("down_votes")
    var downVotes: Int = 0,

    @SerializedName("created_at")
    var createdAt: String? = null,

    @SerializedName("title")
    var title: String,

    @SerializedName("reciter_photo")
    var reciterPhoto: String? = null,

    @SerializedName("content")
    var content: String? = null,

    @SerializedName("shares")
    var shares: Int = 0,

    @SerializedName("path")
    var path: String,

    @SerializedName("updated_at")
    var updatedAt: String? = null,

    @SerializedName("up_votes")
    var upVotes: Int = 0,

    @SerializedName("category_desc")
    var categoryDesc: String? = null,

    @SerializedName("id")
    var id: Int = 0,

    @SerializedName("views_count")
    var viewsCount: Int = 0,

    @SerializedName("author_id")
    var authorId: Int = 0,
)