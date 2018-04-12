package todaytelawat.techandmore.com.todaytelawat.api_container;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class RecitersItem implements Parcelable {

	@SerializedName("brief_biography")
	private String briefBiography;

	@SerializedName("total_listened")
	private String totalListened;

	@SerializedName("full_biography")
	private String fullBiography;

	@SerializedName("recitations_count")
	private int recitationsCount;

	@SerializedName("name")
	private String name;

	@SerializedName("photo")
	private String photo;

	@SerializedName("id")
	private int id;

	@SerializedName("total_likes")
	private int totalLikes;

	public void setBriefBiography(String briefBiography){
		this.briefBiography = briefBiography;
	}

	public String getBriefBiography(){
		return briefBiography;
	}

	public void setTotalListened(String totalListened){
		this.totalListened = totalListened;
	}

	public String getTotalListened(){
		return totalListened;
	}

	public void setFullBiography(String fullBiography){
		this.fullBiography = fullBiography;
	}

	public String getFullBiography(){
		return fullBiography;
	}

	public void setRecitationsCount(int recitationsCount){
		this.recitationsCount = recitationsCount;
	}

	public int getRecitationsCount(){
		return recitationsCount;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setPhoto(String photo){
		this.photo = photo;
	}

	public String getPhoto(){
		return photo;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setTotalLikes(int totalLikes){
		this.totalLikes = totalLikes;
	}

	public int getTotalLikes(){
		return totalLikes;
	}

	@Override
 	public String toString(){
		return 
			"RecitersItem{" + 
			"brief_biography = '" + briefBiography + '\'' + 
			",total_listened = '" + totalListened + '\'' + 
			",full_biography = '" + fullBiography + '\'' + 
			",recitations_count = '" + recitationsCount + '\'' + 
			",name = '" + name + '\'' + 
			",photo = '" + photo + '\'' + 
			",id = '" + id + '\'' + 
			",total_likes = '" + totalLikes + '\'' + 
			"}";
		}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.briefBiography);
		dest.writeString(this.totalListened);
		dest.writeString(this.fullBiography);
		dest.writeInt(this.recitationsCount);
		dest.writeString(this.name);
		dest.writeString(this.photo);
		dest.writeInt(this.id);
		dest.writeInt(this.totalLikes);
	}

	public RecitersItem() {
	}

	protected RecitersItem(Parcel in) {
		this.briefBiography = in.readString();
		this.totalListened = in.readString();
		this.fullBiography = in.readString();
		this.recitationsCount = in.readInt();
		this.name = in.readString();
		this.photo = in.readString();
		this.id = in.readInt();
		this.totalLikes = in.readInt();
	}

	public static final Parcelable.Creator<RecitersItem> CREATOR = new Parcelable.Creator<RecitersItem>() {
		@Override
		public RecitersItem createFromParcel(Parcel source) {
			return new RecitersItem(source);
		}

		@Override
		public RecitersItem[] newArray(int size) {
			return new RecitersItem[size];
		}
	};
}