package com.face.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import android.util.Xml;
import com.face.db.User;

public class XmlUtil {
    public int exportUsersToXml(List<User> users, OutputStream output) throws IllegalArgumentException, IllegalStateException, IOException {
    	if (output == null) {
    		return -1;
    	}
    	if (users == null) {
    		return -2;
    	}
        XmlSerializer serializer = Xml.newSerializer();
        int count = 0;
        serializer.setOutput(output, "UTF-8");
        serializer.startDocument("UTF-8", true);
        serializer.startTag(null, "users");
        for (User user : users) {
            serializer.startTag(null, "user");
            serializer.attribute(null, "id", String.valueOf(user.getId()));
            serializer.startTag(null, "userId");
            serializer.text(String.valueOf(user.getUserId()));
            serializer.endTag(null, "userId");
            serializer.startTag(null, "userName");
            serializer.text(user.getUserName());
            serializer.endTag(null, "userName");
            serializer.startTag(null, "createTime");
            serializer.text(user.getCreateTime());
            serializer.endTag(null, "createTime");
            serializer.endTag(null, "person");
            count++;
        }
        serializer.endTag(null, "users");
        serializer.endDocument();
        output.flush();
        output.close();
        return count;
    }
    
    
    public List<User> inportUsersFromXml(InputStream input) throws Exception {
        //XmlPullParserFactory pullPaser = XmlPullParserFactory.newInstance();
        ArrayList<User> users = null;
        User user = null;
        // 创建一个xml解析的工厂  
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();  
        // 获得xml解析类的引用  
        XmlPullParser parser = factory.newPullParser();  
        parser.setInput(input, "UTF-8");  
        // 获得事件的类型  
        int eventType = parser.getEventType();  
        int id = 0;
        int userId = 0;
        String userName = null;
        String createTime = null;
        while (eventType != XmlPullParser.END_DOCUMENT) {  
            switch (eventType) {  
            case XmlPullParser.START_DOCUMENT:  
                users = new ArrayList<User>();  
                break;  
            case XmlPullParser.START_TAG:  
                if ("user".equals(parser.getName())) {  
                    user = new User();  
                    // 取出属性值  
                    id = Integer.parseInt(parser.getAttributeValue(0));  
                    user.setId(id);
                } else if ("userId".equals(parser.getName())) {  
                    userId = Integer.parseInt(parser.nextText());// 获取该节点的内容  
                    user.setUserId(userId);  
                } else if ("userName".equals(parser.getName())) {  
                    userName = parser.nextText();// 获取该节点的内容  
                    user.setUserName(userName);  
                } else if ("createTime".equals(parser.getName())) {  
                	createTime = parser.nextText();  
                    user.setCreateTime(createTime);  
                }  
                break;  
            case XmlPullParser.END_TAG:  
                if ("person".equals(parser.getName())) {  
                	users.add(user);  
                	user = null;  
                }  
                break;  
            }  
            eventType = parser.next();  
        }  
        return users;  
    }
/*
    public boolean exportConfigsToXml(List<Map> configs) {
    	
    }
    
    public boolean exportRecordsToXml(List<Record> records) {
    	
    }
    
    public boolean inportConfigsFromXml(List<Map> configs) {
    	
    }
    
    public boolean inportRecordsFromXml(List<Record> records) {
    	
    }
*/
}
