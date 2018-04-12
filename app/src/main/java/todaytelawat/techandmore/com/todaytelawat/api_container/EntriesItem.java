package todaytelawat.techandmore.com.todaytelawat.api_container;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class EntriesItem implements Parcelable {

	@SerializedName("reciter_name")
	private String reciterName;

	@SerializedName("category_name")
	private String categoryName;

	@SerializedName("down_votes")
	private int downVotes;

	@SerializedName("created_at")
	private String createdAt;

	@SerializedName("title")
	private String title;

	@SerializedName("reciter_photo")
	private String reciterPhoto;

	@SerializedName("content")
	private String content;

	@SerializedName("shares")
	private int shares;

	@SerializedName("path")
	private String path;

	@SerializedName("updated_at")
	private String updatedAt;

	@SerializedName("up_votes")
	private int upVotes;

	@SerializedName("category_desc")
	private String categoryDesc;

	@SerializedName("id")
	private int id;

	@SerializedName("views_count")
	private int viewsCount;

	@SerializedName("author_id")
	private int authorId;

	public void setReciterName(String reciterName){
		this.reciterName = reciterName;
	}

	public String getReciterName(){
		return reciterName;
	}

	public void setCategoryName(String categoryName){
		this.categoryName = categoryName;
	}

	public String getCategoryName(){
		return categoryName;
	}

	public void setDownVotes(int downVotes){
		this.downVotes = downVotes;
	}

	public int getDownVotes(){
		return downVotes;
	}

	public void setCreatedAt(String createdAt){
		this.createdAt = createdAt;
	}

	public String getCreatedAt(){
		return createdAt;
	}

	public void setTitle(String title){
		this.title = title;
	}

	public String getTitle(){
		return title;
	}

	public void setReciterPhoto(String reciterPhoto){
		this.reciterPhoto = reciterPhoto;
	}

	public String getReciterPhoto(){
		return reciterPhoto;
	}

	public void setContent(String content){
		this.content = content;
	}

	public String getContent(){
		return content;
	}

	public void setShares(int shares){
		this.shares = shares;
	}

	public int getShares(){
		return shares;
	}

	public void setPath(String path){
		this.path = path;
	}

	public String getPath(){
		return path;
	}

	public void setUpdatedAt(String updatedAt){
		this.updatedAt = updatedAt;
	}

	public String getUpdatedAt(){
		return updatedAt;
	}

	public void setUpVotes(int upVotes){
		this.upVotes = upVotes;
	}

	public int getUpVotes(){
		return upVotes;
	}

	public void setCategoryDesc(String categoryDesc){
		this.categoryDesc = categoryDesc;
	}

	public String getCategoryDesc(){
		return categoryDesc;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setViewsCount(int viewsCount){
		this.viewsCount = viewsCount;
	}

	public int getViewsCount(){
		return viewsCount;
	}

	public void setAuthorId(int authorId){
		this.authorId = authorId;
	}

	public int getAuthorId(){
		return authorId;
	}

	@Override
 	public String toString(){
		return 
			"EntriesItem{" + 
			"reciter_name = '" + reciterName + '\'' + 
			",category_name = '" + categoryName + '\'' + 
			",down_votes = '" + downVotes + '\'' + 
			",created_at = '" + createdAt + '\'' + 
			",title = '" + title + '\'' + 
			",reciter_photo = '" + reciterPhoto + '\'' + 
			",content = '" + content + '\'' + 
			",shares = '" + shares + '\'' + 
			",path = '" + path + '\'' + 
			",updated_at = '" + updatedAt + '\'' + 
			",up_votes = '" + upVotes + '\'' + 
			",category_desc = '" + categoryDesc + '\'' + 
			",id = '" + id + '\'' + 
			",views_count = '" + viewsCount + '\'' + 
			",author_id = '" + authorId + '\'' + 
			"}";
		}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.reciterName);
		dest.writeString(this.categoryName);
		dest.writeInt(this.downVotes);
		dest.writeString(this.createdAt);
		dest.writeString(this.title);
		dest.writeString(this.reciterPhoto);
		dest.writeString(this.content);
		dest.writeInt(this.shares);
		dest.writeString(this.path);
		dest.writeString(this.updatedAt);
		dest.writeInt(this.upVotes);
		dest.writeString(this.categoryDesc);
		dest.writeInt(this.id);
		dest.writeInt(this.viewsCount);
		dest.writeInt(this.authorId);
	}

	public EntriesItem() {
	}

	protected EntriesItem(Parcel in) {
		this.reciterName = in.readString();
		this.categoryName = in.readString();
		this.downVotes = in.readInt();
		this.createdAt = in.readString();
		this.title = in.readString();
		this.reciterPhoto = in.readString();
		this.content = in.readString();
		this.shares = in.readInt();
		this.path = in.readString();
		this.updatedAt = in.readString();
		this.upVotes = in.readInt();
		this.categoryDesc = in.readString();
		this.id = in.readInt();
		this.viewsCount = in.readInt();
		this.authorId = in.readInt();
	}

	public static final Parcelable.Creator<EntriesItem> CREATOR = new Parcelable.Creator<EntriesItem>() {
		@Override
		public EntriesItem createFromParcel(Parcel source) {
			return new EntriesItem(source);
		}

		@Override
		public EntriesItem[] newArray(int size) {
			return new EntriesItem[size];
		}
	};
}