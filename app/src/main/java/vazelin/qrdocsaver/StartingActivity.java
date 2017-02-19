package vazelin.qrdocsaver;

import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.support.v7.app.AppCompatActivity;

import android.content.Intent;
import android.os.Environment;
import android.os.Bundle;

import android.util.Log;
import android.util.Size;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

//import android.hardware.camera2.*;

import java.io.File;

import static com.google.zxing.integration.android.IntentIntegrator.QR_CODE_TYPES;

public class StartingActivity extends AppCompatActivity implements View.OnClickListener{

    File defaultDocPath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getPath() + "/Scans");

    EditText editText_pathToFolder;
    ListView listView_savedDocs;
    Button buttonStartScan;

    Size [] mPreviewSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //scanBarcode(null);

        CameraManager camManager =  (CameraManager) getSystemService(CAMERA_SERVICE);
        try {
            String cameraId = camManager.getCameraIdList()[0];
            CameraCharacteristics characteristics = camManager.getCameraCharacteristics(cameraId);

        Size map = characteristics.get(CameraCharacteristics.SENSOR_INFO_PIXEL_ARRAY_SIZE);
        //mPreviewSize = map.getOutputSizes(SurfaceTexture.class);
        Log.v("lal?!","luck?!");
        }
        catch (Exception e){
            Log.v("lal?!","fuck");
        }

        initializeUIAndLinks();
    }

    private void initializeUIAndLinks(){
        editText_pathToFolder = (EditText)  findViewById(R.id.editText_pathToFolder);
        listView_savedDocs =    (ListView)  findViewById(R.id.listView_savedDocs);
        buttonStartScan =       (Button)    findViewById(R.id.button_beginScan);

        buttonStartScan.setOnClickListener(this);
        editText_pathToFolder.setText(defaultDocPath.getPath());
    }

    public void scanBarcode(View view) {
        new IntentIntegrator(this).initiateScan(QR_CODE_TYPES);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.button_beginScan){
            scanBarcode(null);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
