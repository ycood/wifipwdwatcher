package com.wificodeview;


import android.os.Parcel;
import android.os.Parcelable;

public class wifiMessage implements Parcelable{
public String wifiName;
public String wifiCode;
public wifiMessage()
{
	
}
@Override
public int describeContents() {
	return 0;
}

@Override
public void writeToParcel(Parcel dest, int flags) {
	dest.writeString(wifiName);
	dest.writeString(wifiCode);
}
public static final Parcelable.Creator<wifiMessage> CREATOR = new Parcelable.Creator<wifiMessage>() {

	@Override
	public wifiMessage createFromParcel(Parcel source) {
		wifiMessage wifimsg = new wifiMessage();
		wifimsg.wifiName = source.readString();
		wifimsg.wifiCode = source.readString();
		return wifimsg;
	}
	@Override
	public wifiMessage[] newArray(int size) {
		return new wifiMessage[size];
	}
};
}
