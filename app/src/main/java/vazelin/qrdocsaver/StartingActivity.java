package vazelin.qrdocsaver;

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

import static com.google.zxing.integration.android.IntentIntegrator.QR_CODE_TYPES;

public class StartingActivity extends AppCompatActivity implements View.OnClickListener{

    File defaultDocPath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getPath() + "/Scans");

    EditText editText_pathToFolder;
    ListView listView_savedDocs;
    Button buttonStartScan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //scanBarcode(null);

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
