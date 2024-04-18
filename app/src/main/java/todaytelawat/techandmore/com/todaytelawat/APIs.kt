package todaytelawat.techandmore.com.todaytelawat

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import todaytelawat.techandmore.com.todaytelawat.api_container.ResponseContainer
import todaytelawat.techandmore.com.todaytelawat.bodies.TelawatBody

interface APIs {
    @POST("home")
    fun postTodayTelawat(@Body telawatBody: TelawatBody?): Call<ResponseContainer>

    @get:POST("home")
    val todayTelawat: Call<ResponseContainer>
}
