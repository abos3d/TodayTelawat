package todaytelawat.techandmore.com.todaytelawat;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import todaytelawat.techandmore.com.todaytelawat.api_container.ResponseContainer;
import todaytelawat.techandmore.com.todaytelawat.bodies.TelawatBody;

public interface APIs {

    @POST("home")
    Call<ResponseContainer> postTodayTelawat(@Body TelawatBody telawatBody);

    @POST("home")
    Call<ResponseContainer> getTodayTelawat();
}
