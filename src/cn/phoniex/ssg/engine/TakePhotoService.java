package cn.phoniex.ssg.engine;

import java.io.IOException;

import cn.phoniex.ssg.handler.PhotoHandler;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.os.IBinder;
import android.view.SurfaceView;

public class TakePhotoService {

	
	private static Camera camera;

	public static void takePhoto( Context context) {
		//默认打开前置摄像头
		camera = openFacingPreCamera();
		if (camera == null) {
			camera = openFacingBackCamera();
		}
		if (camera != null) {
			
			SurfaceView dummy = new SurfaceView(context);
			try {
				camera.setPreviewDisplay(dummy.getHolder());
			} catch (IOException e) {
				e.printStackTrace();
			}
			camera.startPreview();
//前两个回调函数我们不需要 我们只需要保持jpg的
			camera.takePicture(null, null, new PhotoHandler(context));
		}
	}

	public void StoptakePhoto() {
		if (camera != null) {
			camera.stopPreview();
			camera.release();
			camera = null;
		}
	}
	
	
// 原来设置的sdk最小是8 @SuppressLint("NewApi")  这里用的是9才有的API 不够后来修改了minisdk 
	 private static Camera openFacingBackCamera() {
		Camera cam = null;
		Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
		;
		for (int camIdx = 0, cameraCount = Camera.getNumberOfCameras(); camIdx < cameraCount; camIdx++) {
			Camera.getCameraInfo(camIdx, cameraInfo);
			if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
				try {
					cam = Camera.open(camIdx);
				} catch (RuntimeException e) {
					e.printStackTrace();
				}
			}
		}

		return cam;
	}
	
	private static  Camera openFacingPreCamera()
	{
		Camera cam = null;
		CameraInfo cameraInfo = new CameraInfo();
		
		for (int camIdx = 0, cameraCount = Camera.getNumberOfCameras(); camIdx < cameraCount; camIdx++) {
			Camera.getCameraInfo(camIdx, cameraInfo);
			if (cameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT) {
				
				try {
					cam = Camera.open(camIdx);
				} catch (RuntimeException e) {
					e.printStackTrace();
				}
			}
			
		}
		return camera;
	}
	
}
