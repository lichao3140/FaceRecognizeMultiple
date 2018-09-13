package com.face.db;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

public class FaceProvider {
	private final static String TAG = "FaceProvider";
    private final static String FACE_DB = "FaceRec3288.db";
    private FaceDataHelper faceHelper;

    public FaceProvider(Context context) {
        faceHelper = new FaceDataHelper(context, FACE_DB, null, 1);
    }

    /**
     * 添加用户记录
     * @param user 用户信息
     * @return 0成功，小于0 失败。
     */
    public int addUser(User user) {
        if (null != faceHelper) {
            SQLiteDatabase db = faceHelper.getWritableDatabase();
            if (null != db) {
                //userName, identityNo, identityImg, cameraImg, similarity, createTime
                db.execSQL("insert into tUser (userId, userName, userType, userImage, createTime) values(?,?,?,?,?)", 
                        new Object[]{
                        user.getUserId(),
                        user.getUserName(), 
                        user.getUserType(),
                        user.getUserImage(),
                        user.getCreateTime()
                });
                return 0;
            } else {
                return -2;
            }
        } else {
            return -1;
        }
    }
    
    /**
     * 添加用户记录
     * @param record 记录信息
     * @return 0成功，小于0 失败。
     */
    public int addRecord(Record record) {
        if (null != faceHelper) {
            SQLiteDatabase db = faceHelper.getWritableDatabase();
            if (null != db) {
                //userName, identityNo, identityImg, cameraImg, similarity, createTime
                db.execSQL("insert into tRecord (userId, userName, userType, userImage, recResult, recImage, createTime) values(?,?,?,?,?,?,?)", 
                        new Object[]{
                		record.getUserId(),
                		record.getUserName(), 
                		record.getUserType(),
                		record.getUserImage(),
                		record.getRecResult(),
                		record.getRecImage(),
                		record.getCreateTime()
                });
                return 0;
            } else {
                return -2;
            }
        } else {
            return -1;
        }
    }
    
    /**
     * 添加用户记录
     * @param user 用户信息
     * @return 添加记录的id号, 小于0表示失败。
     */
    public int addUserOutId(User user) {
    	int ret = -1;
        if (null != faceHelper) {
            SQLiteDatabase db = faceHelper.getWritableDatabase();
            if (null != db) {
                //userName, identityNo, identityImg, cameraImg, similarity, createTime
                db.execSQL("insert into tUser (userId, userName, userType, userImage, createTime) values(?,?,?,?,?)", 
                        new Object[]{
                        user.getUserId(),
                        user.getUserName(), 
                        user.getUserType(),
                        user.getUserImage(),
                        user.getCreateTime(),
                });
                Cursor cursor = db.rawQuery("select last_insert_rowid() from tUser",null);
                if (cursor != null) {
                	if (cursor.getCount() > 0) {
                	    cursor.moveToFirst();
                	    ret = cursor.getInt(0);
                	} else {
                		ret = -4;
                	}
                	cursor.close();
                } else {
                	ret = -3;
                }
            } else {
            	ret = -2;
            }
        } else {
        	ret = -1;
        }
        return ret;
    }
    
    /**
     * 添加用户记录
     * @param record 记录信息
     * @return 添加记录的id号, 小于0表示失败。
     */
    public int addRecordOutId(Record record) {
    	int ret = -1;
        if (null != faceHelper) {
            SQLiteDatabase db = faceHelper.getWritableDatabase();
            if (null != db) {
                //userName, identityNo, identityImg, cameraImg, similarity, createTime
                db.execSQL("insert into tRecord (userId, userName, userType, userImage, recResult, recImage, createTime) values(?,?,?,?,?,?,?)", 
                        new Object[]{
                		record.getUserId(),
                		record.getUserName(), 
                		record.getUserType(),
                		record.getUserImage(),
                		record.getRecResult(),
                		record.getRecImage(),
                		record.getCreateTime(),
                });
                Cursor cursor = db.rawQuery("select last_insert_rowid() from tRecord",null);
                if (cursor != null) {
                	if (cursor.getCount() > 0) {
                	    cursor.moveToFirst();
                	    ret = cursor.getInt(0);
                	} else {
                		ret = -4;
                	}
                	cursor.close();
                } else {
                	ret = -3;
                }
            } else {
            	ret = -2;
            }
        } else {
        	ret = -1;
        }
        return ret;
    }

    public int deleteUserById(int id) {
        if (null != faceHelper) {
            SQLiteDatabase db = faceHelper.getWritableDatabase();
            if (null != db) {
                //userName, identityNo, identityImg, cameraImg, similarity, createTime
                db.execSQL("delete from tUser where id =" + id);
                return 0;
            } else {
                return -2;
            }
        } else {
            return -1;
        }
    }
    
    public int deleteRecordById(int id) {
        if (null != faceHelper) {
            SQLiteDatabase db = faceHelper.getWritableDatabase();
            if (null != db) {
                //userName, identityNo, identityImg, cameraImg, similarity, createTime
                db.execSQL("delete from tRecord where id =" + id);
                return 0;
            } else {
                return -2;
            }
        } else {
            return -1;
        }
    }
    
    public int deleteByUserId(int userId) {
        if (null != faceHelper) {
            SQLiteDatabase db = faceHelper.getWritableDatabase();
            if (null != db) {
                //userName, identityNo, identityImg, cameraImg, similarity, createTime
                db.execSQL("delete from tUser where userId =" + userId);
                return 0;
            } else {
                return -2;
            }
        } else {
            return -1;
        }
    }
    
    public int deleteRecordByUserId(int userId) {
        if (null != faceHelper) {
            SQLiteDatabase db = faceHelper.getWritableDatabase();
            if (null != db) {
                //userName, identityNo, identityImg, cameraImg, similarity, createTime
                db.execSQL("delete from tRecord where userId =" + userId);
                return 0;
            } else {
                return -2;
            }
        } else {
            return -1;
        }
    }
    
    public int deleteAllUser() {
        if (null != faceHelper) {
            SQLiteDatabase db = faceHelper.getWritableDatabase();
            if (null != db) {
                //userName, identityNo, identityImg, cameraImg, similarity, createTime
                db.execSQL("delete from tUser");
                return 0;
            } else {
                return -2;
            }
        } else {
            return -1;
        }
    }
    
    public int deleteAllRecord() {
        if (null != faceHelper) {
            SQLiteDatabase db = faceHelper.getWritableDatabase();
            if (null != db) {
                //userName, identityNo, identityImg, cameraImg, similarity, createTime
                db.execSQL("delete from tRecord");
                return 0;
            } else {
                return -2;
            }
        } else {
            return -1;
        }
    }

    public int update() {
        return -1;
    }
    
    /**
     * 修改用户记录
     * @param user 用户信息
     * @return 0成功，小于0 失败。
     */
    public int updateUserbyId(int id, User user) {
        if (null != faceHelper) {
            SQLiteDatabase db = faceHelper.getWritableDatabase();
            if (null != db) {
                //userName, identityNo, identityImg, cameraImg, similarity, createTime
                db.execSQL("update tUser set userId= ?, userName = ?, userType = ?, userImage = ?, createTime = ? where id = ?", 
                        new Object[]{
                        user.getUserId(),
                        user.getUserName(), 
                        user.getUserType(),
                        user.getUserImage(),
                        user.getCreateTime(),
                        id
                });
                return 0;
            } else {
                return -2;
            }
        } else {
            return -1;
        }
    }
    
    /**
     * 修改用户记录
     * @param user 用户信息
     * @return 0成功，小于0 失败。
     */
    public int updateUserSubById(int id, User user) {
        if (null != faceHelper) {
            SQLiteDatabase db = faceHelper.getWritableDatabase();
            if (null != db) {
                //userName, identityNo, identityImg, cameraImg, similarity, createTime
                db.execSQL("update tUser set userId= ?, userName = ?, userType = ? where id = ?", 
                        new Object[]{
                        user.getUserId(),
                        user.getUserName(), 
                        user.getUserType(),
                        id
                });
                return 0;
            } else {
                return -2;
            }
        } else {
            return -1;
        }
    }
    
    public List<User> queryUsers() {
        List<User> list = null;
        if (null != faceHelper) {
            SQLiteDatabase db = faceHelper.getWritableDatabase();
            if (null != db) {
                String sql = "select id, userId, userName, createTime from tUser order by id asc";
                Cursor cursor = db.rawQuery( sql, null);
                if (null != cursor) {
                    list = new ArrayList<User>();
                    User user;
                    while (cursor.moveToNext()) {
                        user = new User();
                        user.setId(cursor.getInt(cursor.getColumnIndex(User.KEY_ID)));
                        user.setUserId(cursor.getInt(cursor.getColumnIndex(User.KEY_USER_ID)));
                        user.setUserName(cursor.getString(cursor.getColumnIndex(User.KEY_USER_NAME)));
                        user.setCreateTime(cursor.getString(cursor.getColumnIndex(User.KEY_CREATE_TIME)));
                        list.add(user);
                    }
                    cursor.close();
                }
            }
        }
        return list;
    }

    public List<User> queryUsers(int limit) {
        List<User> list = null;
        if (null != faceHelper) {
            SQLiteDatabase db = faceHelper.getWritableDatabase();
            if (null != db) {
                String sql = "select id, userId, userName, userType, userImage, createTime from tUser order by id desc limit " + limit;
                Cursor cursor = db.rawQuery( sql, null);
                if (null != cursor) {
                    list = new ArrayList<User>();
                    User user;
                    while (cursor.moveToNext()) {
                        user = new User();
                        user.setId(cursor.getInt(cursor.getColumnIndex(User.KEY_ID)));
                        user.setUserId(cursor.getInt(cursor.getColumnIndex(User.KEY_USER_ID)));
                        user.setUserName(cursor.getString(cursor.getColumnIndex(User.KEY_USER_NAME)));
                        user.setUserType(cursor.getInt(cursor.getColumnIndex(User.KEY_USER_TYPE)));
                        user.setUserImage(cursor.getString(cursor.getColumnIndex(User.KEY_USER_IMAGE)));
                        user.setCreateTime(cursor.getString(cursor.getColumnIndex(User.KEY_CREATE_TIME)));
                        list.add(user);
                    }
                    cursor.close();
                }
            }
        }
        return list;
    }
    
    public List<Record> queryRecords(int limit) {
        List<Record> list = null;
        if (null != faceHelper) {
            SQLiteDatabase db = faceHelper.getWritableDatabase();
            if (null != db) {
                String sql = "select id, userId, userName, userType, userImage, recResult, recImage, createTime from tRecord order by id desc limit " + limit;
                Cursor cursor = db.rawQuery( sql, null);
                if (null != cursor) {
                    list = new ArrayList<Record>();
                    Record record;
                    while (cursor.moveToNext()) {
                    	record = new Record();
                    	record.setId(cursor.getInt(cursor.getColumnIndex(Record.KEY_ID)));
                    	record.setUserId(cursor.getInt(cursor.getColumnIndex(Record.KEY_USER_ID)));
                    	record.setUserName(cursor.getString(cursor.getColumnIndex(Record.KEY_USER_NAME)));
                    	record.setUserType(cursor.getInt(cursor.getColumnIndex(Record.KEY_USER_TYPE)));
                    	record.setUserImage(cursor.getString(cursor.getColumnIndex(Record.KEY_USER_IMAGE)));
                    	record.setRecResult(cursor.getInt(cursor.getColumnIndex(Record.KEY_REC_RESULT)));
                    	record.setRecImage(cursor.getString(cursor.getColumnIndex(Record.KEY_REC_IMAGE)));
                    	record.setCreateTime(cursor.getString(cursor.getColumnIndex(Record.KEY_CREATE_TIME)));
                        list.add(record);
                    }
                    cursor.close();
                }
            }
        }
        return list;
    }
    
    public List<User> queryUserPage(int pageIndex, int size) {
    	log("pageIndex:" + pageIndex + " size:" + size);
        List<User> list = null;
        if (null != faceHelper) {
            SQLiteDatabase db = faceHelper.getWritableDatabase();
            if (null != db) {
            	String sql = "select id, userId, userName, userType, userImage, createTime from tUser order by id desc limit " + size + " offset " + (pageIndex-1) * size;//size:每页显示条数，index页码
                //userName, identityNo, identityImg, cameraImg, similarity, createTime
                //String[] cols = new String[]{"id", "userName", "identityNo", "identityImg", "cameraImg", "similarity", "createTime"};
                //Cursor cursor = db.query(false,"tUser", cols, null, null, null, null, null, null);
                Cursor cursor = db.rawQuery( sql, null);
                if (null != cursor) {
                    list = new ArrayList<User>();
                    User user;
                    log("cursor.size:" + cursor.getCount());
                    while (cursor.moveToNext()) {
                        user = new User();
                        user.setId(cursor.getInt(cursor.getColumnIndex(User.KEY_ID)));
                        user.setUserId(cursor.getInt(cursor.getColumnIndex(User.KEY_USER_ID)));
                        user.setUserName(cursor.getString(cursor.getColumnIndex(User.KEY_USER_NAME)));
                        user.setUserType(cursor.getInt(cursor.getColumnIndex(User.KEY_USER_TYPE)));
                        user.setUserImage(cursor.getString(cursor.getColumnIndex(User.KEY_USER_IMAGE)));
                        user.setCreateTime(cursor.getString(cursor.getColumnIndex(User.KEY_CREATE_TIME)));
                        list.add(user);
                    }
                    log("list.size:" + list.size());
                    cursor.close();
                }
            }
        }
        return list;
    }
    
    public List<User> queryUserPage(String userId, String userName, int pageIndex, int size) {
    	log("pageIndex:" + pageIndex + " size:" + size);
        List<User> list = null;
        if (null != faceHelper) {
            SQLiteDatabase db = faceHelper.getWritableDatabase();
            if (null != db) {
            	//String sql = "select id, userId, userName, userType, cardNumber, userImage, createTime from tUser order by id desc limit " + size + " offset " + (pageIndex-1) * size;//size:每页显示条数，index页码
            	StringBuilder strb = new StringBuilder();
            	strb.append("select id, userId, userName, userType, userImage, createTime from tUser");
            	if (!TextUtils.isEmpty(userId) || !TextUtils.isEmpty(userName)) {
            		strb.append(" where ");
            		boolean hasName = false;
            		if (!TextUtils.isEmpty(userId)) {
            		    strb.append("userId like '");
            		    strb.append(userId.trim());
            		    strb.append("' ");
            		    hasName = true;
            		}
            		if (!TextUtils.isEmpty(userName)) {
            			if (hasName) {
            				strb.append(" and ");
            			}
            		    strb.append("userName like '");
            		    strb.append(userName.trim());
            		    strb.append("' ");
            		}
            	}
            	strb.append(" order by id desc limit " + size + " offset " + (pageIndex-1) * size); //size:每页显示条数，index页码
        		String sql = strb.toString();
                Cursor cursor = db.rawQuery( sql, null);
                if (null != cursor) {
                    list = new ArrayList<User>();
                    User user;
                    log("cursor.size:" + cursor.getCount());
                    while (cursor.moveToNext()) {
                        user = new User();
                        user.setId(cursor.getInt(cursor.getColumnIndex(User.KEY_ID)));
                        user.setUserId(cursor.getInt(cursor.getColumnIndex(User.KEY_USER_ID)));
                        user.setUserName(cursor.getString(cursor.getColumnIndex(User.KEY_USER_NAME)));
                        user.setUserType(cursor.getInt(cursor.getColumnIndex(User.KEY_USER_TYPE)));
                        user.setUserImage(cursor.getString(cursor.getColumnIndex(User.KEY_USER_IMAGE)));
                        user.setCreateTime(cursor.getString(cursor.getColumnIndex(User.KEY_CREATE_TIME)));
                        list.add(user);
                    }
                    log("list.size:" + list.size());
                    cursor.close();
                }
            }
        }
        return list;
    }
    
    public List<Record> queryRecordPage(int pageIndex, int size) {
    	log("pageIndex:" + pageIndex + " size:" + size);
        List<Record> list = null;
        if (null != faceHelper) {
            SQLiteDatabase db = faceHelper.getWritableDatabase();
            if (null != db) {
            	String sql = "select id, userId, userName, userType, userImage, recResult, recImage, createTime from tRecord order by id desc limit " + size + " offset " + (pageIndex-1) * size;
            	//size:每页显示条数，index页码
                Cursor cursor = db.rawQuery( sql, null);
                if (null != cursor) {
                    list = new ArrayList<Record>();
                    Record record;
                    while (cursor.moveToNext()) {
                    	record = new Record();
                    	record.setId(cursor.getInt(cursor.getColumnIndex(Record.KEY_ID)));
                    	record.setUserId(cursor.getInt(cursor.getColumnIndex(Record.KEY_USER_ID)));
                    	record.setUserName(cursor.getString(cursor.getColumnIndex(Record.KEY_USER_NAME)));
                    	record.setUserType(cursor.getInt(cursor.getColumnIndex(Record.KEY_USER_TYPE)));
                    	record.setUserImage(cursor.getString(cursor.getColumnIndex(Record.KEY_USER_IMAGE)));
                    	record.setRecResult(cursor.getInt(cursor.getColumnIndex(Record.KEY_REC_RESULT)));
                    	record.setRecImage(cursor.getString(cursor.getColumnIndex(Record.KEY_REC_IMAGE)));
                    	record.setCreateTime(cursor.getString(cursor.getColumnIndex(Record.KEY_CREATE_TIME)));
                        list.add(record);
                    }
                    log("list.size:" + list.size());
                    cursor.close();
                }
            }
        }
        return list;
    }
    
	/**
	 * 查询数据库中的总条数.
	 * 
	 * @return
	 */
	public int quaryUserTableRowCount() {
		int count = 0;
		String sql = "select count(id) from tUser";
		if (null != faceHelper) {
			SQLiteDatabase db = faceHelper.getWritableDatabase();
			if (null != db) {
				Cursor cursor = db.rawQuery(sql, null);
				if (cursor != null && cursor.getCount() > 0) {
				    cursor.moveToFirst();
				    count = cursor.getInt(0);
				}
	            if (cursor != null) {
	                cursor.close();
	            }
			}
		}
		return count;
	}
    
	/**
	 * 查询数据库中的总条数.
	 * 
	 * @return
	 */
	public int quaryUserTableRowCount(String userId, String userName) {
		int count = 0;
		//String sql = "select count(id) from tUser";
    	StringBuilder strb = new StringBuilder();
    	strb.append("select count(id) from tUser");
    	if (!TextUtils.isEmpty(userId) || !TextUtils.isEmpty(userName)) {
    		strb.append(" where ");
    		boolean hasName = false;
    		if (!TextUtils.isEmpty(userId)) {
    		    strb.append("userId like '");
    		    strb.append(userId.trim());
    		    strb.append("' ");
    		    hasName = true;
    		}
    		if (!TextUtils.isEmpty(userName)) {
    			if (hasName) {
    				strb.append(" and ");
    			}
    		    strb.append("userName like '");
    		    strb.append(userName.trim());
    		    strb.append("' ");
    		}
    	}
		String sql = strb.toString();
		if (null != faceHelper) {
			SQLiteDatabase db = faceHelper.getWritableDatabase();
			if (null != db) {
				Cursor cursor = db.rawQuery(sql, null);
				if (cursor != null && cursor.getCount() > 0) {
				    cursor.moveToFirst();
				    count = cursor.getInt(0);
				}
	            if (cursor != null) {
	                cursor.close();
	            }
			}
		}
		return count;
	}
	
	/**
	 * 查询数据库中的总条数.
	 * 
	 * @return
	 */
	public int quaryRecordTableRowCount() {
		int count = 0;
		String sql = "select count(id) from tRecord";
		if (null != faceHelper) {
			SQLiteDatabase db = faceHelper.getWritableDatabase();
			if (null != db) {
				Cursor cursor = db.rawQuery(sql, null);
				if (cursor != null && cursor.getCount() > 0) {
				    cursor.moveToFirst();
				    count = cursor.getInt(0);
				}
	            if (cursor != null) {
	                cursor.close();
	            }
			}
		}
		return count;
	}
	
    public User getUserById(int id) {
        User user = null;
        if (null != faceHelper) {
            SQLiteDatabase db = faceHelper.getWritableDatabase();
            if (null != db) {
                String sql = "select id, userId, userName, userType, userImage, createTime from tUser where id = " + id;
                Cursor cursor = db.rawQuery( sql, null);
                if (null != cursor && cursor.getCount() > 0) {
                	cursor.moveToFirst();
                    user = new User();
                    user.setId(cursor.getInt(cursor.getColumnIndex(User.KEY_ID)));
                    user.setUserId(cursor.getInt(cursor.getColumnIndex(User.KEY_USER_ID)));
                    user.setUserName(cursor.getString(cursor.getColumnIndex(User.KEY_USER_NAME)));
                    user.setUserType(cursor.getInt(cursor.getColumnIndex(User.KEY_USER_TYPE)));
                    user.setUserImage(cursor.getString(cursor.getColumnIndex(User.KEY_USER_IMAGE)));
                    user.setCreateTime(cursor.getString(cursor.getColumnIndex(User.KEY_CREATE_TIME)));
                }
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        return user;
    }
	
    public User getUserByUserId(int userId) {
        User user = null;
        if (null != faceHelper) {
            SQLiteDatabase db = faceHelper.getWritableDatabase();
            if (null != db) {
                String sql = "select id, userId, userName, userType, userImage, createTime from tUser where userId = " + userId;
                Cursor cursor = db.rawQuery( sql, null);
                if (null != cursor && cursor.getCount() > 0) {
                	cursor.moveToFirst();
                    user = new User();
                    user.setId(cursor.getInt(cursor.getColumnIndex(User.KEY_ID)));
                    user.setUserId(cursor.getInt(cursor.getColumnIndex(User.KEY_USER_ID)));
                    user.setUserName(cursor.getString(cursor.getColumnIndex(User.KEY_USER_NAME)));
                    user.setUserType(cursor.getInt(cursor.getColumnIndex(User.KEY_USER_TYPE)));
                    user.setUserImage(cursor.getString(cursor.getColumnIndex(User.KEY_USER_IMAGE)));
                    user.setCreateTime(cursor.getString(cursor.getColumnIndex(User.KEY_CREATE_TIME)));
                }
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        return user;
    }
    
    public Record getRecordById(int id) {
    	Record record = null;
        if (null != faceHelper) {
            SQLiteDatabase db = faceHelper.getWritableDatabase();
            if (null != db) {
                String sql = "select id, userId, userName, userType, userImage, recResult, recImage, createTime from tRecord from tRecord where id = " + id;
                Cursor cursor = db.rawQuery( sql, null);
                if (null != cursor && cursor.getCount() > 0) {
                	cursor.moveToFirst();
                	record = new Record();
                	record.setId(cursor.getInt(cursor.getColumnIndex(Record.KEY_ID)));
                	record.setUserId(cursor.getInt(cursor.getColumnIndex(Record.KEY_USER_ID)));
                	record.setUserName(cursor.getString(cursor.getColumnIndex(Record.KEY_USER_NAME)));
                	record.setUserType(cursor.getInt(cursor.getColumnIndex(Record.KEY_USER_TYPE)));
                	record.setUserImage(cursor.getString(cursor.getColumnIndex(Record.KEY_USER_IMAGE)));
                	record.setRecResult(cursor.getInt(cursor.getColumnIndex(Record.KEY_REC_RESULT)));
                	record.setRecImage(cursor.getString(cursor.getColumnIndex(Record.KEY_REC_IMAGE)));
                	record.setCreateTime(cursor.getString(cursor.getColumnIndex(Record.KEY_CREATE_TIME)));
                }
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        return record;
    }
    
    public List<Record> getRecordsByUserId(int userId, int limit) {
        List<Record> list = null;
        if (null != faceHelper) {
            SQLiteDatabase db = faceHelper.getWritableDatabase();
            if (null != db) {
            	String sql = "select id, userId, userName, userType, userImage, recResult, recImage, createTime from tRecord where userId = " + userId + " order by id desc limit " + limit;
            	//size:每页显示条数，index页码
                Cursor cursor = db.rawQuery( sql, null);
                if (null != cursor) {
                    list = new ArrayList<Record>();
                    Record record;
                    while (cursor.moveToNext()) {
                    	record = new Record();
                    	record.setId(cursor.getInt(cursor.getColumnIndex(Record.KEY_ID)));
                    	record.setUserId(cursor.getInt(cursor.getColumnIndex(Record.KEY_USER_ID)));
                    	record.setUserName(cursor.getString(cursor.getColumnIndex(Record.KEY_USER_NAME)));
                    	record.setUserType(cursor.getInt(cursor.getColumnIndex(Record.KEY_USER_TYPE)));
                    	record.setUserImage(cursor.getString(cursor.getColumnIndex(Record.KEY_USER_IMAGE)));
                    	record.setRecResult(cursor.getInt(cursor.getColumnIndex(Record.KEY_REC_RESULT)));
                    	record.setRecImage(cursor.getString(cursor.getColumnIndex(Record.KEY_REC_IMAGE)));
                    	record.setCreateTime(cursor.getString(cursor.getColumnIndex(Record.KEY_CREATE_TIME)));
                        list.add(record);
                    }
                    log("list.size:" + list.size());
                    cursor.close();
                }
            }
        }
        return list;
    }
	
	public int getMaxId() {
		int id = -1;
		String sql = "select max(id) from tUser";
		if (null != faceHelper) {
			SQLiteDatabase db = faceHelper.getWritableDatabase();
			if (null != db) {
				Cursor cursor = db.rawQuery(sql, null);
				if (cursor != null && cursor.getCount() > 0) {
				    cursor.moveToFirst();
				    id = cursor.getInt(0);
				}
                if (cursor != null) {
                    cursor.close();
                }
			}
		}
		return id;
	}
	
	public int getMaxUserId() {
		int id = -1;
		String sql = "select max(userId) from tUser";
		if (null != faceHelper) {
			SQLiteDatabase db = faceHelper.getWritableDatabase();
			if (null != db) {
				Cursor cursor = db.rawQuery(sql, null);
				if (cursor != null && cursor.getCount() > 0) {
				    cursor.moveToFirst();
				    id = cursor.getInt(0);
				}
	            if (cursor != null) {
	                cursor.close();
	            }
			}
		}
		return id;
	}
	
    private void log(String msg) {
        Log.e(TAG, msg);
    }
}
