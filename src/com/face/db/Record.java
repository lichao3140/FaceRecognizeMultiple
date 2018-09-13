package com.face.db;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Record {
	public final static String KEY_ID = "id";
	public final static String KEY_USER_ID = "userId";
	public final static String KEY_USER_NAME = "userName";
	public final static String KEY_USER_TYPE = "userType";
	public final static String KEY_USER_IMAGE = "userImage";
	public final static String KEY_REC_RESULT = "recResult";
	public final static String KEY_REC_IMAGE = "recImage";
	public final static String KEY_CREATE_TIME = "createTime";
	
    private int id;
    private int userId;
    private String userName;
    private int userType;
    private String userImage;
    private int recResult;
    private String recImage;
    private String createTime;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public int getUserType() {
		return userType;
	}
	public void setUserType(int userType) {
		this.userType = userType;
	}

	public String getUserImage() {
		return userImage;
	}
	public void setUserImage(String userImage) {
		this.userImage = userImage;
	}
	public int getRecResult() {
		return recResult;
	}
	public void setRecResult(int recResult) {
		this.recResult = recResult;
	}
	public String getRecImage() {
		return recImage;
	}
	public void setRecImage(String recImage) {
		this.recImage = recImage;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(long createTime) {
    	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    	this.createTime = format.format(new Date(createTime));
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

    public String toInfoString() {
		StringBuilder strb = new StringBuilder();
		strb.append("编号:");
		strb.append(id);
		strb.append("  工号:");
		strb.append(userId);
		strb.append("  名称:");
		strb.append(userName);
		strb.append("  相似度:");
		strb.append(recResult);
		strb.append("  时间:");
		strb.append(createTime);
		return strb.toString();
    }
    
    public String toRecInfoString() {
		StringBuilder strb = new StringBuilder();
		strb.append("编号:");
		strb.append(id);
		strb.append("  工号:");
		strb.append(userId);
		strb.append("  名称:");
		strb.append(userName);
		strb.append("\r\n相似度:");
		strb.append(recResult);
		strb.append("  时间:");
		strb.append(createTime);
		return strb.toString();
    }
    
    public String toUserInfoString() {
		StringBuilder strb = new StringBuilder();
		strb.append("名称:");
		strb.append(userName);
		strb.append("\r\n工号:");
		strb.append(userId);

		return strb.toString();
    }
    
    public String toOneLineString() {
		StringBuilder strb = new StringBuilder();
		strb.append("工号:");
		strb.append(userId);
		strb.append("  名称:");
		strb.append(userName);
		strb.append("  分值:");
		strb.append(recResult);
		strb.append("  时间:");
		strb.append(createTime);
		return strb.toString();
    }
}
