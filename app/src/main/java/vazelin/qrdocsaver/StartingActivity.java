package vazelin.qrdocsaver;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ListViewCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

//import android.hardware.camera2.*;

import java.io.File;

public class StartingActivity extends AppCompatActivity {

    File defaultDocPath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getPath());

    EditText editText_pathToFolder;
    ListView listView_savedDocs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scanBarcode(null);

    }

    private void initializeUIAndLinks(){
        editText_pathToFolder = (EditText)  findViewById(R.id.editText_PathToFolder);
        listView_savedDocs =    (ListView)  findViewById(R.id.listView_savedDocs);
    }

    public void scanBarcode(View view) {
        new IntentIntegrator(this).initiateScan();
    }

}
