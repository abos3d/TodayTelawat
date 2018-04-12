package todaytelawat.techandmore.com.todaytelawat.api_container;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class EContent implements Parcelable {

	@SerializedName("sorted")
	private String sorted;

	@SerializedName("reciters")
	private List<RecitersItem> reciters;

	@SerializedName("entries")
	private List<EntriesItem> entries;

	public void setSorted(String sorted){
		this.sorted = sorted;
	}

	public String getSorted(){
		return sorted;
	}

	public void setReciters(List<RecitersItem> reciters){
		this.reciters = reciters;
	}

	public List<RecitersItem> getReciters(){
		return reciters;
	}

	public void setEntries(List<EntriesItem> entries){
		this.entries = entries;
	}

	public List<EntriesItem> getEntries(){
		return entries;
	}

	@Override
 	public String toString(){
		return 
			"EContent{" + 
			"sorted = '" + sorted + '\'' + 
			",reciters = '" + reciters + '\'' + 
			",entries = '" + entries + '\'' + 
			"}";
		}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.sorted);
		dest.writeTypedList(this.reciters);
		dest.writeList(this.entries);
	}

	public EContent() {
	}

	protected EContent(Parcel in) {
		this.sorted = in.readString();
		this.reciters = in.createTypedArrayList(RecitersItem.CREATOR);
		this.entries = new ArrayList<EntriesItem>();
		in.readList(this.entries, EntriesItem.class.getClassLoader());
	}

	public static final Parcelable.Creator<EContent> CREATOR = new Parcelable.Creator<EContent>() {
		@Override
		public EContent createFromParcel(Parcel source) {
			return new EContent(source);
		}

		@Override
		public EContent[] newArray(int size) {
			return new EContent[size];
		}
	};
}