package com.frm.cn;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.util.Log;

public class ImageUtils {
    public final static String TAG = "ImageUtils";
    public static ImageUtils imgUtils = null;
	
    public static ImageUtils getInstance() {
    	if (null  == imgUtils) {
    		imgUtils = new ImageUtils();
    	}
    	return imgUtils;
    }
    
	/**
	 * 获取人脸切图
	 * 
	 * @param bmp
	 *            原始图片
	 * @param rect
	 *            人脸坐标
	 * @return 人脸图像
	 */
	public Bitmap cutFaceImage(Bitmap bmp, Rect rect) {
		if (bmp == null) {
			return null;
		}
		int width = bmp.getWidth();
		int height = bmp.getHeight();
		int mWidth = rect.width();
		int mHight = rect.height();
		int mw = mWidth / 4;
		int mh = mHight / 4;
		int x = rect.left - mw;
		if (x < 0) {
			x = 0;
		}

		int y = rect.top - mh;
		if (y < 0) {
			y = 0;
		}
		mWidth = mWidth * 3 / 2;
		if (mWidth + x > width) {
			mWidth = width - x;
		}
		mHight = mHight * 3 / 2;
		if (mHight + y > height) {
			mHight = height - y;
		}
//		log("x:" + x + " y:" + y + " mWidth:" + mWidth + " mHight:" + mHight);
		Bitmap tmp = Bitmap.createBitmap(bmp, x, y, mWidth, mHight);
		return tmp;
	}
	
	public Bitmap getDiskBitmap(String pathString) {
		Bitmap bitmap = null;
		try {
			File file = new File(pathString);
			if (file.exists()) {
				bitmap = BitmapFactory.decodeFile(pathString);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return bitmap;
	}
	

	public String parseTime2String(long time) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(new Date(time));
	}

	public String getIdeImgPath(String rootDir, String ideNo) {
		StringBuilder strb = new StringBuilder();
		strb.append(rootDir);
		strb.append("/");
		strb.append(ideNo);
		strb.append(FaceApp.IMG_TYPE);
		return strb.toString();
	}

	public String getCamImgPath(String rootDir, long currentTime) {
		StringBuilder strb = new StringBuilder();
		strb.append(rootDir);
		strb.append("/");
		strb.append(currentTime);
		strb.append(FaceApp.IMG_TYPE);
		return strb.toString();
	}
	
	public void saveImage(String imgPath, Bitmap bmp) {
		//log("saveImage() imgPath:" + imgPath);
		if (bmp == null || bmp.getWidth() <= 0) {
			log("saveImage() bmp == null || bmp.getWidth() <= 0");
			return;
		}
		File file = new File(imgPath);
		File pFile = file.getParentFile();
		if (!pFile.exists() || !pFile.isDirectory()) {
			pFile.mkdirs();
		}
		if (file.exists()) {
			file.delete();
			file = new File(imgPath);
		}
		//log("saveImage() out:");
		FileOutputStream out = null;
		try {
			//log("saveImage() createScaledBitmap() end");
			out = new FileOutputStream(file);
			//log("saveImage() compress() start");
			bmp.compress(CompressFormat.JPEG, 100, out);
			//log("saveImage() compress() end");
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
			//log("saveImage() ex:" + e.getMessage());
		} finally {
			//log("saveImage() finally");
			try {

				if (out != null) {
					out.close();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	public static void saveImage(String imgPath, byte[] buffer) {
		File file = new File(imgPath);
		if (file.exists() && file.isFile()) {
			file.delete();
			file = new File(imgPath);
		}
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(file);
			out.write(buffer);
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	public void saveScaleImage(String imgPath, Bitmap bmp, float maxSize) {
		//log("saveImage() imgPath:" + imgPath);
		if (bmp == null || bmp.getWidth() <= 0) {
			log("saveImage() bmp == null || bmp.getWidth() <= 0");
			return;
		}
		File file = new File(imgPath);
		File pFile = file.getParentFile();
		if (!pFile.exists() || !pFile.isDirectory()) {
			pFile.mkdirs();
		}
		if (file.exists()) {
			file.delete();
			file = new File(imgPath);
		}
		//log("saveImage() out:");
		FileOutputStream out = null;
		Bitmap tmp = null;
		int width = bmp.getWidth();
		int height = bmp.getHeight();
		log("saveScaleImage() width:" + width + " height:" + height);
		try {
			//log("saveImage() createScaledBitmap() start");
			float scale = (width > height?maxSize / width: maxSize / height);
			if (scale < 1) {
			    Matrix matrix = new Matrix();
			    matrix.setScale(scale, scale);
				tmp = Bitmap.createBitmap(bmp, 0, 0, width, height, matrix, false);
			} else {
				tmp = bmp;
			}
			//log("saveImage() createScaledBitmap() end");
			out = new FileOutputStream(file);
			//log("saveImage() compress() start");
			tmp.compress(CompressFormat.JPEG, 100, out);
			//log("saveImage() compress() end");
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
			//log("saveImage() ex:" + e.getMessage());
		} finally {
			//log("saveImage() finally");
			try {

				if (out != null) {
					out.close();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			if (tmp != null && !tmp.isRecycled() && !tmp.sameAs(bmp)) {
				tmp.recycle();
			}
		}
	}

	/**
	 * 保存文件
	 * 
	 * @param path
	 * @param buffer
	 * @return
	 */
	public boolean saveFile(String path, byte[] buffer) {
		if (path == null || path.isEmpty()) {
			return false;
		}
		File file = new File(path);

		FileOutputStream out = null;
		try {
			if (file.exists()) {
				new File(path).delete();
			}
			file.createNewFile();
			out = new FileOutputStream(file);
			out.write(buffer);
			out.flush();
			out.close();
			return true;
		} catch (IOException ex) {
			ex.printStackTrace();
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}
	
	/**
	 * Yuv420转Bitmap
	 * @param data yuv420数据
	 * @param width 图像宽度
	 * @param height 图像高度
	 * @param rotate 旋转角度
	 * @return
	 */
	public Bitmap getBitmapFromYuv420(byte[] data, int width, int height, int rotate) {
		if (data == null || data.length == 0) {
			return null;
		}
		Bitmap bmp = null;
		Bitmap tmp = null;
		if (data.length > 0) {
			YuvImage yuvImgKj = new YuvImage(data, ImageFormat.NV21, width, height, null);
			if (yuvImgKj != null) {
				ByteArrayOutputStream outs = null;
				try {
				    outs = new ByteArrayOutputStream();
					if (yuvImgKj.compressToJpeg(new Rect(0, 0, width, height), 100, outs)) {
					    outs.flush();
						byte[] bts = outs.toByteArray();
						tmp = BitmapFactory.decodeByteArray(bts, 0, bts.length);
						if (tmp != null && rotate != 0) {
						    Matrix matrix = new Matrix();
						    matrix.postRotate(rotate);
						    bmp = Bitmap.createBitmap(tmp, 0, 0, width, height, matrix, true);
						    if (!tmp.equals(bmp)) {
						    	tmp.recycle();
						    }
						} else {
						    bmp = tmp;
						}
					}
				} catch(Exception ex) {
					ex.printStackTrace();
				} finally {
					if (outs != null) {
						try {
							outs.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		return bmp;
	}
	
	public Bitmap decodeYUV420SP(byte[] yuv420sp, int width, int height) {  
	  
	    int frameSize = width * height;  
	    int[] rgbBuf = new int[frameSize];
	    
	    if (yuv420sp == null || yuv420sp.length < frameSize * 3 / 2) {
	       return null;
	    }
	  
	    int i = 0, y = 0;  
	  
	    int uvp = 0, u = 0, v = 0;  
	  
	    int y1192 = 0, r = 0, g = 0, b = 0;  
	  
	    for (int j = 0, yp = 0; j < height; j++) {  
	        uvp = frameSize + (j >> 1) * width;  
	        u = 0;  
	        v = 0;  
	  
	        for (i = 0; i < width; i++, yp++) {  
	            y = (0xff & ((int) yuv420sp[yp])) - 16;  
	  
	            if (y < 0)  
	                y = 0;  
	  
	            if ((i & 1) == 0) {  
	                v = (0xff & yuv420sp[uvp++]) - 128;  
	                u = (0xff & yuv420sp[uvp++]) - 128;  
	            }  
	  
	            y1192 = 1192 * y;  
	            r = (y1192 + 1634 * v);  
	            g = (y1192 - 833 * v - 400 * u);  
	            b = (y1192 + 2066 * u);  
	  
	            if (r < 0)
	                r = 0;
	            else if (r > 262143)
	                r = 262143;
	  
	            if (g < 0)
	                g = 0;
	            else if (g > 262143)
	                g = 262143;
	  
	            if (b < 0)  
	                b = 0;  
	            else if (b > 262143)  
	                b = 262143;  
	  
	            rgbBuf[yp] = ((byte)(b >> 10) & 0xFF) | ((byte)(g >> 10) & 0xFF) << 8 | (((byte)r >> 10) & 0xFF) << 16;
	        }

	    }
        Bitmap bmp = Bitmap.createBitmap(rgbBuf, width, height, Config.ARGB_8888);
        return bmp;
	}
	
	/**
	 * Yuv420转Bitmap
	 * @param data yuv420数据
	 * @param width 图像宽度
	 * @param height 图像高度
	 * @return 图像缩小一半
	 */
	public Bitmap getBitmapFromYuv420(byte[] data, int camWidth, int camHeight, int recWidth, int recHeight) {
		if (data == null || data.length == 0) {
			return null;
		}
		Bitmap bmp = null;
		Bitmap tmp = null;
		if (data.length > 0) {
			YuvImage yuvImgKj = new YuvImage(data, ImageFormat.NV21, camWidth, camHeight, null);
			if (yuvImgKj != null) {
				ByteArrayOutputStream outs = null;
				try {
				    outs = new ByteArrayOutputStream();
					if (yuvImgKj.compressToJpeg(new Rect(0, 0, camWidth, camHeight), 100, outs)) {
					    outs.flush();
						byte[] bts = outs.toByteArray();
						tmp = BitmapFactory.decodeByteArray(bts, 0, bts.length);
						if (tmp != null) {
						    Matrix matrix = new Matrix();
						    //matrix.setScale(-1, 1);
						    float wScale = recWidth;
						    wScale /= camWidth;
						    wScale = -wScale;
						    float hScale = recHeight;
						    hScale /= camHeight;
						    matrix.setScale( wScale, hScale); 
						    bmp = Bitmap.createBitmap(tmp, 0, 0, camWidth, camHeight, matrix, true);
						}
					}
				} catch(Exception ex) {
					ex.printStackTrace();
				} finally {
				    if (tmp != null && !tmp.sameAs(bmp)) {
				    	tmp.recycle();
				    }
					if (outs != null) {
						try {
							outs.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		return bmp;
	}
	
	/**
	 * Yuv420转Bitmap
	 * @param data yuv420数据
	 * @param width 图像宽度
	 * @param height 图像高度
	 * @return 图像缩小一半

	public Bitmap getBitmapFromYuv420(byte[] data, int camWidth, int camHeight, int recWidth, int recHeight) {
		if (data == null || data.length == 0) {
			return null;
		}
		Bitmap bmp = null;
		Bitmap tmp = null;
		if (data.length > 0) {
			YuvImage yuvImgKj = new YuvImage(data, ImageFormat.NV21, camWidth, camHeight, null);
			if (yuvImgKj != null) {
				ByteArrayOutputStream outs = null;
				try {
				    outs = new ByteArrayOutputStream();
					if (yuvImgKj.compressToJpeg(new Rect(0, 0, camWidth, camHeight), 100, outs)) {
					    outs.flush();
						byte[] bts = outs.toByteArray();
						tmp = BitmapFactory.decodeByteArray(bts, 0, bts.length);
						if (tmp != null) {
						    Matrix matrix = new Matrix();
						    //matrix.setScale(-1, 1);
						    float wScale = recWidth;
						    wScale /= camWidth;
						    float hScale = recHeight;
						    hScale /= camHeight;
						    matrix.setScale(wScale, hScale);
						    bmp = Bitmap.createBitmap(tmp, 0, 0, camWidth, camHeight, matrix, true);
						}
					}
				} catch(Exception ex) {
					ex.printStackTrace();
				} finally {
				    if (tmp != null && !tmp.sameAs(bmp)) {
				    	tmp.recycle();
				    }
					if (outs != null) {
						try {
							outs.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		return bmp;
	}
	
			/**
			 * Yuv420转Bitmap
			 * @param data yuv420数据
			 * @param width 图像宽度
			 * @param height 图像高度
			 * @return 图像缩小一半
			 
			public Bitmap getBitmapFromYuv420(byte[] data, int camWidth, int camHeight) {
				if (data == null || data.length == 0) {
					return null;
				}
				Bitmap bmp = null;
				Bitmap tmp = null;
				if (data.length > 0) {
					YuvImage yuvImgKj = new YuvImage(data, ImageFormat.NV21, camWidth, camHeight, null);
					if (yuvImgKj != null) {
						ByteArrayOutputStream outs = null;
						try {
						    outs = new ByteArrayOutputStream();
							if (yuvImgKj.compressToJpeg(new Rect(0, 0, camWidth, camHeight), 100, outs)) {
							    outs.flush();
								byte[] bts = outs.toByteArray();
								tmp = BitmapFactory.decodeByteArray(bts, 0, bts.length);
								if (tmp != null) {
								    Matrix matrix = new Matrix();
								    matrix.setScale(-1, 1);
								    bmp = Bitmap.createBitmap(tmp, 0, 0, camWidth, camHeight, matrix, true);
								}
							}
						} catch(Exception ex) {
							ex.printStackTrace();
						} finally {
						    if (tmp != null && !tmp.sameAs(bmp)) {
						    	tmp.recycle();
						    }
							if (outs != null) {
								try {
									outs.close();
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
				return bmp;
			}
			
			private byte[] getBGR24FromYuv420(byte[] data, int width, int height) {
				if (data == null || data.length == 0) {
					return null;
				}
				int size = width * height;
				byte[] bgr = new byte[size * 3];
				byte value = 0;
				for (int i = 0; i < size; i++) {
					value = data[i];
					bgr[i * 3] = value;
					bgr[i * 3 + 1] = value;
					bgr[i * 3 + 2] = value;
				}
				return bgr;
			}
			
			private byte[] getBGR24FromBitmap(Bitmap bmp) {
				if (bmp == null) {
					return null;
				}
				int width = bmp.getWidth();
				int height = bmp.getHeight();
		        int[] pixels = new int[width * height];
		        // 获取RGB32数据
		        bmp.getPixels(pixels, 0, width, 0, 0, width, height);
		        byte[] BGR24 = new byte[width * height * 3];
		        //byte[] gray = new byte[width * height];
		        // 获取图片的RGB24数据和灰度图数据
		        for (int i = 0; i < width * height; i++) {
		            int r = (pixels[i] >> 16) & 0x000000FF;
		            int g = (pixels[i] >> 8) & 0x000000FF;
		            int b = pixels[i] & 0x000000FF;
		            BGR24[i * 3] = (byte)(b & 0xFF);
		            BGR24[i * 3 + 1] = (byte)(g & 0xFF);
		            BGR24[i * 3 + 2] = (byte)(r & 0xFF);
		            //gray[i] = (byte) ((306 * r + 601 * g + 117 * b) >> 10);
		        }
		        return BGR24;
			}
 */
	
	public byte[] getBGR24FromBitmap(Bitmap bmp) {
		if (bmp == null) {
			return null;
		}
		int width = bmp.getWidth();
		int height = bmp.getHeight();
        int[] pixels = new int[width * height];
        // 获取RGB32数据
        bmp.getPixels(pixels, 0, width, 0, 0, width, height);
        byte[] BGR24 = new byte[width * height * 3];
        //byte[] gray = new byte[width * height];
        // 获取图片的RGB24数据和灰度图数据
        for (int i = 0; i < width * height; i++) {
            int r = (pixels[i] >> 16) & 0x000000FF;
            int g = (pixels[i] >> 8) & 0x000000FF;
            int b = pixels[i] & 0x000000FF;
            BGR24[i * 3] = (byte)(b & 0xFF);
            BGR24[i * 3 + 1] = (byte)(g & 0xFF);
            BGR24[i * 3 + 2] = (byte)(r & 0xFF);
            //gray[i] = (byte) ((306 * r + 601 * g + 117 * b) >> 10);
        }
        return BGR24;
	}
	
	public static void log(String msg) {
		Log.e(TAG, msg);
	}
}
