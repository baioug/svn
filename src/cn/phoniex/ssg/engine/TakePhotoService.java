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
		//Ĭ�ϴ�ǰ������ͷ
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
//ǰ�����ص��������ǲ���Ҫ ����ֻ��Ҫ����jpg��
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
	
	
// ԭ�����õ�sdk��С��8 @SuppressLint("NewApi")  �����õ���9���е�API ���������޸���minisdk 
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
