package vazelin.qrdocsaver;

import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;

import java.util.Arrays;

/**
 * Created by Futurama on 2/20/2017.
 */

public class CameraHelper {

    private CameraManager camManager = null;
    private Size mPictureSize = null;
    private CameraDevice mCameraDevice = null;
    private CaptureRequest.Builder mRequestBuilder = null;
    private CameraCaptureSession mPreviewSession = null;
    private final static String TAG = "SimpleCamera";
    private TextureView mTextureView = null;
    private ImageReader iReader = null;

    public void MakeAShot(String filePath, CameraManager managerToUse){
        // TODO Auto-generated method stub
        Log.i(TAG, "onSurfaceTextureAvailable()");

        camManager = managerToUse;
        try{
            String cameraId = camManager.getCameraIdList()[0];

            CameraCharacteristics characteristics = camManager.getCameraCharacteristics(cameraId);
            mPictureSize = characteristics.get(CameraCharacteristics.SENSOR_INFO_PIXEL_ARRAY_SIZE);

            camManager.openCamera(cameraId, mCameraStateCallback, null);
        }
        catch (SecurityException e){
            e.printStackTrace();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    private CameraDevice.StateCallback mCameraStateCallback = new CameraDevice.StateCallback() {

        @Override
        public void onOpened(CameraDevice camera) {
            // TODO Auto-generated method stub
            Log.i(TAG, "onOpened");
            mCameraDevice = camera;

            iReader = ImageReader.newInstance(100,100, ImageFormat.JPEG, 1);

            try {
                mRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            } catch (CameraAccessException e){
                e.printStackTrace();
            }

            mRequestBuilder.addTarget(iReader.getSurface());

            try {
                mCameraDevice.createCaptureSession(Arrays.asList(iReader.getSurface()), mPreviewStateCallback, null);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(CameraDevice camera, int error) {
            // TODO Auto-generated method stub
            Log.e(TAG, "onError");

        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            // TODO Auto-generated method stub
            Log.e(TAG, "onDisconnected");

        }
    };

    private CameraCaptureSession.StateCallback mPreviewStateCallback = new CameraCaptureSession.StateCallback() {

        @Override
        public void onConfigured(CameraCaptureSession session) {
            // TODO Auto-generated method stub
            Log.i(TAG, "onConfigured");
            mPreviewSession = session;

            mRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);

            HandlerThread backgroundThread = new HandlerThread("CameraPreview");
            backgroundThread.start();
            Handler backgroundHandler = new Handler(backgroundThread.getLooper());

            try {
                mPreviewSession.setRepeatingRequest(mRequestBuilder.build(), null, backgroundHandler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onConfigureFailed(CameraCaptureSession session) {
            // TODO Auto-generated method stub
            Log.e(TAG, "CameraCaptureSession Configure failed");
        }


    };



}
