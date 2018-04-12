package todaytelawat.techandmore.com.todaytelawat.bodies;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class TelawatBody implements Parcelable {

    @SerializedName("locale")
    String locale;

    @SerializedName("sort")
    int sort;

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public TelawatBody(String locale, int sort) {
        this.locale = locale;
        this.sort = sort;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public TelawatBody() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.locale);
        dest.writeInt(this.sort);
    }

    protected TelawatBody(Parcel in) {
        this.locale = in.readString();
        this.sort = in.readInt();
    }

    public static final Creator<TelawatBody> CREATOR = new Creator<TelawatBody>() {
        @Override
        public TelawatBody createFromParcel(Parcel source) {
            return new TelawatBody(source);
        }

        @Override
        public TelawatBody[] newArray(int size) {
            return new TelawatBody[size];
        }
    };
}
