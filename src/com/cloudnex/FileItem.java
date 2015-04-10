package com.cloudnex;

public class FileItem {
	
	public String mFileName;
	public String mType;
	public String mURL;
	public String mRank;
	public String mID;
	public String mSource;
	
	public FileItem(String FileName, String Type, String URL, String Rank, String ID, String source) {
		  mFileName = FileName;
		  mType = Type;
		  mURL = URL;
		  mRank = Rank;
		  mID = ID;
		  mSource = source;
	}
}
