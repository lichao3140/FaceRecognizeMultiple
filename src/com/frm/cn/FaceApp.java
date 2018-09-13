package com.frm.cn;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.face.db.FaceProvider;
import com.face.sv.FaceDM2016;
import com.face.sv.FaceDetect;
import com.face.sv.FaceLive;
import com.face.sv.FaceRecognize;
import com.face.sv.ImageUtil;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.util.Log;

/**
 * 用于初始化配置应用中使用到的人脸算法的配置文件。
 * @author 邹丰
 * @datetime 2016-05-03
 */
public class FaceApp extends Application {
    private final static String TAG = "FaceApp";
    public final static int CAMERA_IMAGE_WIDTH = 640; // 摄像头图像分辨率宽
    public final static int CAMERA_IMAGE_HEIGHT = 480; // 摄像头图像分辨率高
    public final static int RECORD_IMAGE_WIDTH = 320;  // 识别图像宽度
    public final static int RECORD_IMAGE_HEIGHT = 240; // 识别图像高度
    public final static int RECOGNICE_PADDING_SIZE = 50;  // 用户注册人脸坐标离边界间隔
    public final static int IMAGE_SAVE_WIDTH = 320;
    public final static int IMAGE_SAVE_HEIGHT = 240;
    public final static int IMAGE_ID_SAVE_WIDTH = 102;
    public final static int IMAGE_ID_SAVE_HEIGHT = 126;
    public final static String IMG_TYPE = ".jpg";
    private final static String DETECT_KO_SO = "libTHDetect_ko.so";
    private final static String DETECT_KO = "libTHDetect_ko";
    private final static String POS_KO_SO = "libTHFacialPos_ko.so";
    private final static String POS_KO = "libTHFacialPos_ko";
    private final static String FEATURE_DB30A_KO_SO = "libTHFeature_db30a_ko.so";
    private final static String DB30A_KO = "libTHFeature_db30a_ko";
    private final static String LIVE_VI_SO = "libTHFaceLive_vi.so";
    private final static String LIVE_VI = "libTHFaceLive_vi";
    // 操作类型
    public final static int TYPE_FACE_RECOGNIZE = 0;
    public final static int TYPE_FACE_REGISTER = 1;

    // 默认人脸比对相似度门限
    public final static String DEFAULT_DEVICE_SERIAL = "88888888";
    public final static int DEFAULT_RECOGNIZE_LIMIT = 70;
    public final static int DEFAULT_LIVE_LIMIT = 20;
    public final static boolean DEFAULT_LIVE_STATUS = true;  // 默认启动活体检测
    
    public final static int MAX_USER_NUMBER = 10000;       // 用户最大数量
    
    public final static  int DEFAULT_SAME_TIME = 5000;  // 同人开门间隔
    
    public final static int cameraIndexKJ = 0;
    
    // 默认人脸检测边界像素
    public final static int DEFAULT_DETECT_PADDING = 20;
    
    public final static boolean DEFAULT_IS_VIEW_SIM = false; 
    
    public final static int OPERATE_TYPE_REGISTER = 0;    // 操作类型注册
    public final static int OPERATE_TYPE_UPDATE_INFO = 0X1001; // 操作类型修改用户信息
    public final static int OPERATE_TYPE_UPDATE_FEATURE = 0X1002; // 操作类型修改用户模型
    public final static int OPERATE_TYPE_UPDATE_ALL = 0X1003; // 操作类型修改用户信息和模型
    
    public final static String KEY_DEVICE_SERIAL = "deviceSerial";
    public final static String KEY_RECOGNIZE_LIMIT = "recognizeLimit";
    public final static String KEY_SAME_TIME = "sameTime";
    public final static String KEY_MODEL_NUMBER = "modelNumber";
    
    public final static String KEY_USER_RID = "userRId";
    public final static String KEY_USER_ID = "userId";
    public final static String KEY_USER_NAME = "userName";
    public final static String KEY_USER_IMAGE = "userImage";
    public final static String KEY_REC_RESULT = "recResult";
    public final static String KEY_REC_TIME = "recTime";
    public final static String KEY_OPERATE_TYPE = "operateType";
    
	public final static int WIDTH = 640;
	public final static int HEIGHT = 480;
	
	public static ImageUtil mImgUtil = new ImageUtil();
	public static FaceDM2016 mDM2016 = new FaceDM2016();
	public static FaceDetect mDetect = new FaceDetect();
	public static FaceLive mLive = new FaceLive();
	public static FaceRecognize mRecognize = new FaceRecognize();
	public static int mInitStatus = 0;
	
	public static FaceProvider mProvider = null;
	    
    public final static boolean isDebug = false;
    public static String tempDir = null;
    public static String libDir = null;
    public static String userDataPath = null;
    public static String recordImagePath = null;
    public static String recognizeTempImage = null;
    public static String registerTempImage = null;
    public static String saveImagePath = null;
    public static String mDeviceSerial = DEFAULT_DEVICE_SERIAL;
    public static int    mRecognizeLimit = DEFAULT_RECOGNIZE_LIMIT;
    public static int    mSameTime = DEFAULT_SAME_TIME;
    
    @Override
    public void onCreate() {
        super.onCreate();
        log("onCreate()");
        
        String dataPath =  this.getFilesDir().getAbsolutePath();

        File file = this.getCacheDir();
        //tempDir = file.getAbsolutePath();
        //libDir = tempDir.replace("cache", "lib/");
        // 设置配置目录
        tempDir = "/mnt/sdcard/SysConfig/cache/";  //算法运行所需要的缓存目录，需要读写权限。
        libDir = "/mnt/sdcard/SysConfig/model/";  // 对应initFaceModel()模型文件创建目录，需要读取权限。
        
        saveImagePath = "/mnt/sdcard/SysConfig/image/";

        log("tempDir:" + tempDir + " libDir:" + libDir);
        // 初始化目录
        createDir(tempDir);
        createDir(libDir);
        createDir(saveImagePath);
        
        userDataPath = dataPath + "/userData";
        recordImagePath = dataPath + "/recordImage/";
        createDir(userDataPath);
        createDir(recordImagePath);
        recognizeTempImage = dataPath + "/recTemp.jpg";
        registerTempImage = dataPath + "/regTemp.jpg";
        
        // 加载模型文件
        initFaceModel();
        
        // 获取配置参数
        loadConfig();
        
        mProvider = new FaceProvider(this);
        
        initSdkLib();
    }

    private void initSdkLib() {
    	log("initSdkLib()");

    	mInitStatus = 0;
		new Thread() {
			@Override
			public void run() {
				super.run();
				byte[] bts = null;
				int ret = 0;
				bts = mDetect.getDetectSN();
				bts = mDM2016.encodeKeyCode(bts);
				ret = mDetect.checkDetectSN(bts);
				log("mDetect.checkDetectSN(bts) ret:" + ret);
			    ret = mDetect.initFaceDetectLib(FaceApp.libDir, FaceApp.tempDir);
			    log("mDetect.initFaceDetectLib(FaceApp.libDir, FaceApp.tempDir, 1) ret:" + ret);
			    if (ret == 1) {
			    	mInitStatus++;
			    }
			    
			    bts = mLive.getLiveSN();
			    bts = mDM2016.encodeKeyCode(bts);
			    ret = mLive.checkLiveSN(bts);
			    log("mLive.checkLiveSN(bts) ret:" + ret);
			    mLive.InitFaceLive(FaceApp.libDir, FaceApp.tempDir);
			    log("mLive.InitFaceLive(FaceApp.libDir, FaceApp.tempDir) ret:" + ret);
			    
			    bts = mRecognize.getFeatureSN();
			    bts = mDM2016.encodeKeyCode(bts);
			    ret = mRecognize.checkFeatureSN(bts);
			    log("mRecognize.checkFeatureSN(bts) ret:" + ret);
				// 初始化人脸检测算法库
				ret = mRecognize.initFaceLibrary(FaceApp.libDir, FaceApp.tempDir, FaceApp.userDataPath, MAX_USER_NUMBER);
				log("mRecognize.initFaceLibrary(FaceApp.libDir, FaceApp.tempDir, FaceApp.userDataPath, MAX_USER_NUMBER) ret:" + ret);
				if (ret == 1) {
					mInitStatus++;
				}
			}

		}.start();
    }
    
    public void createDir(String dir) {
    	File file = new File(dir);
    	if (!file.exists() || !file.isDirectory()) {
    		file.mkdirs();
    	}
    }
    
    // 获取配置参数
   public void loadConfig() {
	   log("loadConfig()");
       SharedPreferences pre = this.getSharedPreferences("config", MODE_PRIVATE);
       mDeviceSerial = pre.getString(KEY_DEVICE_SERIAL, DEFAULT_DEVICE_SERIAL);
       mRecognizeLimit = pre.getInt(KEY_RECOGNIZE_LIMIT, DEFAULT_RECOGNIZE_LIMIT);
    }
    
    public void initFaceModel() {
    	log("initFaceModel()");
    	AssetManager assetMan = this.getResources().getAssets();
    	String mainPath = null;
    	String subPath = null;
    	File manFile = null;
    	File subFile = null;
    	FileOutputStream output = null;
    	InputStream      input = null;
    	byte[] bts = new byte[1024];
    	int size = 0;
    	// 生成libTHDect_dpbin.so文件

    	try {
    		mainPath = libDir + DETECT_KO_SO;
			manFile = new File(mainPath);
			if (!manFile.exists()) {
				manFile.createNewFile();
			    String[] dpbins = assetMan.list(DETECT_KO);
			    if (dpbins != null && dpbins.length > 0) {
				    int len = dpbins.length;
					output = new FileOutputStream(manFile, true);
					for(int i = 0; i < len; i++) {
						subPath = DETECT_KO + "/" + DETECT_KO + (i + 1);
						input = assetMan.open(subPath);
						if (input != null) {
						    while((size = input.read(bts)) != -1) {
							    output.write(bts, 0, size);
						    }
						    input.close();
						} else {
							log("AssetManager.open(file) is null. path:" + subPath);
						}
					}
					output.flush();
					output.close();
			    } else {
				    log("dpbins == null || dpbins.length <= 0 fileName:" + DETECT_KO);
			    }
			} else {
				log("This model file is exist. file:" + mainPath);
			}
		} catch (IOException e) {
			e.printStackTrace();
			log("create model file fail. file:" + mainPath);
		}
    	
    	// 生成libTHFacialPos_con.so文件
    	try {
    		mainPath = libDir + POS_KO_SO;
	    	manFile = new File(mainPath);
			if (!manFile.exists()) {
				manFile.createNewFile();
			    String[] cons = assetMan.list(POS_KO);
			    if (cons != null && cons.length > 0) {
			    	int len = cons.length;
					output = new FileOutputStream(manFile, true);
					for(int i = 0; i < len; i++) {
						subPath = POS_KO + "/" + POS_KO + (i + 1);
						input = assetMan.open(subPath);
						if (input != null) {
						    while((size = input.read(bts)) != -1) {
							    output.write(bts, 0, size);
						    }
						    input.close();
						} else {
							log("AssetManager.open(file) is null. path:" + subPath);
						}
					}
					output.flush();
					output.close();
			    } else {
				    log("cons == null || cons.length <= 0 fileName:" + POS_KO);
			    }
			} else {
				log("This model file is exist. file:" + mainPath);
			}
		} catch (IOException e) {
			e.printStackTrace();
			log("create model file fail. file:" + mainPath);
		}
    	
    	// 生成libTHFeature_db30a.so文件
    	try {
    		mainPath = libDir + FEATURE_DB30A_KO_SO;
			manFile = new File(mainPath);
			if (!manFile.exists()) {
				manFile.createNewFile();
			    String[] db30as = assetMan.list(DB30A_KO);
			    if (db30as != null && db30as.length > 0) {
				int len = db30as.length;
				output = new FileOutputStream(manFile, true);
				for(int i = 0; i < len; i++) {
					subPath = DB30A_KO + "/" + DB30A_KO + (i + 1);
					input = assetMan.open(subPath);
					if (input != null) {
					    while((size = input.read(bts)) != -1) {
						    output.write(bts, 0, size);
					    }
					    input.close();
					} else {
						log("AssetManager.open(file) is null. path:" + subPath);
					}
				}
				output.flush();
				output.close();

			    } else {
			    	log("db30as == null || db30as.length <= 0 fileName:" + DB30A_KO);
			    }
			} else {
				log("This model file is exist. file:" + mainPath);
			} 
		} catch (IOException e) {
			e.printStackTrace();
			log("create model file fail. file:" + mainPath);
		}
    	
    	// 生成libTHFaceLive_vi.so文件
    	try {
    		mainPath = libDir + LIVE_VI_SO;
			manFile = new File(mainPath);
			if (!manFile.exists()) {
				manFile.createNewFile();
			    String[] vis = assetMan.list(LIVE_VI);
			    if (vis != null && vis.length > 0) {
			    	int len = vis.length;
			    	output = new FileOutputStream(manFile, true);
			    	for(int i = 0; i < len; i++) {
				    	subPath = LIVE_VI + "/" + LIVE_VI + (i + 1);
				    	input = assetMan.open(subPath);
					    if (input != null) {
					        while((size = input.read(bts)) != -1) {
					    	    output.write(bts, 0, size);
					        }
					        input.close();
				    	} else {
					    	log("AssetManager.open(file) is null. path:" + subPath);
					    }
				    }
				    output.flush();
				    output.close();
			    } else {
			    	log("vis == null || vis.length <= 0 fileName:" + LIVE_VI);
			   }
			} else {
				log("This model file is exist. file:" + mainPath);
			}
		} catch (IOException e) {
			e.printStackTrace();
			log("create model file fail. file:" + mainPath);
		}
    }

    private void log(String msg) {
        Log.d(TAG, msg);
    }
}
