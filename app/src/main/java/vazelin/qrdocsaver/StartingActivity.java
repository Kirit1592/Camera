package vazelin.qrdocsaver;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;

import android.content.Intent;
import android.os.Environment;
import android.os.Bundle;

import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

//import android.hardware.camera2.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static com.google.zxing.integration.android.IntentIntegrator.QR_CODE_TYPES;

public class StartingActivity extends AppCompatActivity implements View.OnClickListener{

    File defaultDocPath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath() + "/Scans");

    EditText editText_pathToFolder;
    ListView listView_savedDocs;
    Button buttonStartScan;

    final int PHOTO_RESULT = 1;
    private static final int REQUEST_WRITE_STORAGE = 112;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestExternalStorageWritePermission();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeUIAndLinks();

        testSDWrite();
    }

    private void testSDWrite() {
        String dirPath = editText_pathToFolder.getText() + "/";
        String filePath = dirPath + "Shit.txt";
        new File(dirPath).mkdirs();
        File sf = new File(filePath);


        boolean cr = sf.canWrite();
        try {
            FileWriter sfw = new FileWriter(sf, false);
            sfw.write("Writing right onto yo SD!");
            sfw.close();
        }catch(IOException e){
            Log.e("TestSDWrite: ", "Error!!!");
            e.printStackTrace();
        }

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
                String subfolderName = preprocessSubfolderName(result.getContents());
                captureAndWriteDocumentToSDCard(subfolderName);
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private String preprocessSubfolderName(String subfolderName) {
        String rez = subfolderName;
        rez = rez.replaceAll("/|:|", "_");
        rez = rez.replaceAll("/", "_");
        return rez;
    }

    private void captureAndWriteDocumentToSDCard(String subfolderName) {
        String dirPath = editText_pathToFolder.getText() + "/" + subfolderName + "/";
        //dirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getPath();
        File dirF = new File("/storage/sdcard0/Documents/");
        if (!(dirF.exists())){
            Log.e("Main:", String.format("Creating dirs: %b", dirF.mkdirs()));
        }
        String fullPath = dirPath + "Doc.jpg";
        File tmpImage;
        try{
            tmpImage= File.createTempFile(
                "Doc",  /* prefix */
                ".jpg",         /* suffix */
                dirF      /* directory */
        );
        }catch (IOException e){
            Log.e("Main:", "Failed to create the temp file!");
            e.printStackTrace();
            return;
        }

        Uri fileUri = FileProvider.getUriForFile(getApplicationContext(), "vazelin.qrdocsaver.StartingActivity", tmpImage);

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getPackageManager())!=null){
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            startActivityForResult(takePictureIntent, PHOTO_RESULT);
        }
    }

    protected void captureAndWriteDocumentToSDCard_INSTANT(String docName){
        String dirPath = editText_pathToFolder.getText() + "/" + docName + "/";
        File dirF = new File(dirPath);
        if (! dirF.exists()){
            dirF.mkdirs();
        }
        new CameraHelper().captureFromCamAndSaveToSDCard(dirPath + "Doc.jpg", (CameraManager) getSystemService(CAMERA_SERVICE));
    }

    protected void requestExternalStorageWritePermission(){
        boolean hasPermission = (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_STORAGE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case REQUEST_WRITE_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    //reload my activity with permission granted or use the features what required the permission
                } else
                {
                    Toast.makeText(this, "The app was not allowed to write to your storage. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        }

    }

}

