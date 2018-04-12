package todaytelawat.techandmore.com.todaytelawat.api_container;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class ResponseContainer implements Parcelable {

	@SerializedName("eContent")
	private EContent eContent;

	@SerializedName("eDesc")
	private String eDesc;

	@SerializedName("eCode")
	private int eCode;

	public void setEContent(EContent eContent){
		this.eContent = eContent;
	}

	public EContent getEContent(){
		return eContent;
	}

	public void setEDesc(String eDesc){
		this.eDesc = eDesc;
	}

	public String getEDesc(){
		return eDesc;
	}

	public void setECode(int eCode){
		this.eCode = eCode;
	}

	public int getECode(){
		return eCode;
	}

	@Override
 	public String toString(){
		return 
			"ResponseContainer{" + 
			"eContent = '" + eContent + '\'' + 
			",eDesc = '" + eDesc + '\'' + 
			",eCode = '" + eCode + '\'' + 
			"}";
		}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(this.eContent, flags);
		dest.writeString(this.eDesc);
		dest.writeInt(this.eCode);
	}

	public ResponseContainer() {
	}

	protected ResponseContainer(Parcel in) {
		this.eContent = in.readParcelable(EContent.class.getClassLoader());
		this.eDesc = in.readString();
		this.eCode = in.readInt();
	}

	public static final Parcelable.Creator<ResponseContainer> CREATOR = new Parcelable.Creator<ResponseContainer>() {
		@Override
		public ResponseContainer createFromParcel(Parcel source) {
			return new ResponseContainer(source);
		}

		@Override
		public ResponseContainer[] newArray(int size) {
			return new ResponseContainer[size];
		}
	};
}