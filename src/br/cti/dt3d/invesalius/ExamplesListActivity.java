package br.cti.dt3d.invesalius;

import java.io.File;
import java.util.Vector;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ExamplesListActivity extends Activity implements OnItemClickListener{
	
	String[] lista = {"Demo 1","Demo 2", "Demo 3"};
//	static Dialog dialog;
	public static Context c;
	ProgressDialog dialog;
	File f = new File(InVesaliusMobileActivity.diretorio);
	Vector<String> array = new Vector<String>();
	public static Activity a;
	
	@Override
    protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.examples_list);

		c = this;
		a = this;
		ListView lv =(ListView)findViewById(R.id.exemplos);
		
		lv.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1 , lista));
		lv.setOnItemClickListener(this);
		
    }
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view,
	        int position, long id) {

		Boolean[] encontrou = new Boolean[3];
		for(int i = 0; i < 3; i++) encontrou[i] = false;

		if(f.isDirectory()){
			File[] files = f.listFiles();
			for(int i = 0; i < files.length; i++){
				System.out.println(files[i].getName());
				if(files[i].getName().equalsIgnoreCase("Brain MRI Demo")) encontrou[0] = true;
				if(files[i].getName().equalsIgnoreCase("Brain MRI Demo 2")) encontrou[1] = true;
				if(files[i].getName().equalsIgnoreCase("Brain MRI Demo 3")) encontrou[2] = true;
			}
		}

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setCancelable(false)
	           .setPositiveButton("OK", new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int id) {
	               }
	           });
	    
		CharSequence s = ((TextView) view).getText();
		if(s.equals(lista[0])){
			if (encontrou[0]){
				builder.setMessage("Demo 1 already exists!");
				AlertDialog alert = builder.create();
				alert.show();
			}else{
//				dialog = ProgressDialog.show(this, "", "Downloading Files. Please wait...", true);
				new DownloadExampleTask().execute("demo1.zip",InVesaliusMobileActivity.diretorio+"/");
			}
		}
		else if(s.equals(lista[1])){
			if (encontrou[1]){
				builder.setMessage("Demo 2 already exists!");
				AlertDialog alert = builder.create();
				alert.show();
			}else{
//				dialog = ProgressDialog.show(this, "", "Downloading Files. Please wait...", true);
				new DownloadExampleTask().execute("demo2.zip",InVesaliusMobileActivity.diretorio+"/");
			}
		}
		else if(s.equals(lista[2])){
			if (encontrou[2]){
				builder.setMessage("Demo 3 already exists!");
				AlertDialog alert = builder.create();
				alert.show();
			}else{
//				dialog = ProgressDialog.show(this, "", "Downloading Files. Please wait...", true);
				new DownloadExampleTask().execute("demo3.zip",InVesaliusMobileActivity.diretorio+"/");
			}
		}
			    	
	}
	
	public static void cancelDialog(){
//		dialog.cancel();
	}

}
