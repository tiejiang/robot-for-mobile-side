package com.yinyutech.xiaolerobot.utils;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {
	private static final String TAG = "FileUtil";
	private static final File parentPath = Environment.getExternalStorageDirectory();
	private static String storagePath = "";
	private static final String DST_FOLDER_NAME = "XiaoLeRobotAlbum";

	/**?????????��??
	 * @return
	 */
	private static String initPath(){
		if(storagePath.equals("")){
			storagePath = parentPath.getAbsolutePath()+"/" + DST_FOLDER_NAME;
			File f = new File(storagePath);
			if(!f.exists()){
				f.mkdir();
			}
		}
		return storagePath;
	}

	/**save Bitmap to sdcard
	 * @param b
	 */
	public static Uri saveBitmap(Bitmap b){

		String path = initPath();
		long dataTake = System.currentTimeMillis();
		String jpegName = path + "/"  + "XiaoleTakePhoto" + String.valueOf(dataTake) +".png";
//		Log.i("TIEJIANG", "saveBitmap:jpegName = " + jpegName);
		try {
			FileOutputStream fout = new FileOutputStream(jpegName);
			BufferedOutputStream bos = new BufferedOutputStream(fout);
			b.compress(Bitmap.CompressFormat.PNG, 100, bos);
			bos.flush();
			bos.close();
			File temp = new File(jpegName);
			Uri imageFileUri = Uri.fromFile(temp);
			Log.d("TIEJIANG", "saveBitmap:jpegName = " + jpegName + " ??url=" + imageFileUri);
			return imageFileUri;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.i("TIEJIANG", "saveBitmap: failed! ");
			e.printStackTrace();
		}
		return null;
	}


	//遍历SD卡中某一路径下指定类型的图片
	public static List<String> getSD() {

		String path = initPath();
		// 遍历符合条件的列表
		List<String> it = new ArrayList<String>();
		File f = new File(path);
		File[] files = f.listFiles();

		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if (getImageFile(file.getPath()))
				it.add(file.getPath());
		}
		return it;
	}

	//指定遍历文件类型
	private static boolean getImageFile(String fName) {
		boolean re;
		String end = fName
				.substring(fName.lastIndexOf(".") + 1, fName.length())
				.toLowerCase();
		if (end.equals("jpg") || end.equals("gif") || end.equals("png")
				|| end.equals("jpeg") || end.equals("bmp")) {
			re = true;
		} else {
			re = false;
		}
		return re;
	}

}
