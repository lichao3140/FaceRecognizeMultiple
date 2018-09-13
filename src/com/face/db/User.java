package com.face.db;

public class User {
	public final static String KEY_ID = "id";
	public final static String KEY_USER_ID = "userId";
	public final static String KEY_USER_NAME = "userName";
	public final static String KEY_USER_TYPE = "userType";
	public final static String KEY_USER_IMAGE = "userImage";
	public final static String KEY_CREATE_TIME = "createTime";
	
    private int id;
    private int userId;
    private String userName;
    private int userType;
    private String userImage;
    private String createTime;
    
    public User() {
    	id = -1;
    	userId = 0;
    	userName = "00000000";
    	userType = 0;
    }

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

	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String toString() {
		StringBuilder strb = new StringBuilder();
		strb.append("User id:");
		strb.append(id);
		strb.append(" userId:");
		strb.append(userId);
		strb.append(" userName:");
		strb.append(userName);
		return strb.toString();
	}
}
