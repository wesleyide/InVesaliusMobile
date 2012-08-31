package br.cti.dt3d.invesalius;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class DatasetsListActivity extends Activity implements OnItemClickListener, OnItemLongClickListener, OnClickListener{
	
	static ListView lv1;
	static EditText et1;
	Vector<String> array = new Vector<String>();
	File f = new File(InVesaliusMobileActivity.diretorio);
	static ProgressDialog dialog;
	static CharSequence toDelete = "NULL";
	public static Context c;
	public static Activity a;
	
	ArrayAdapter<String> aa;
		
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.list);
		c = this;
		a = this;
		
		if(f.isDirectory()){
			File[] files = f.listFiles();
			
			for(int i=0; i<files.length;i++){
				if(files[i].isDirectory() && !(files[i].isHidden())) array.add(files[i].getName());
			}
		}
		else f.mkdirs();
		Collections.sort(array);
		final int array_size = array.size();
		
		lv1=(ListView)findViewById(R.id.listView1);
		et1 = (EditText)findViewById(R.id.editText1);
	
	    et1.addTextChangedListener(new TextWatcher(){
	        public void afterTextChanged(Editable s) {
	        	String[] busca = DatasetsListActivity.et1.getText().toString().toLowerCase().split(" ");
	        	ArrayList<String> lista = new ArrayList<String>();
	        	for (int i = 0; i < array_size; i++) {
	        		if (!array.get(i).toLowerCase().equalsIgnoreCase("download demos...")){
		        		if (busca.length == 0 || (busca.length == 1 && busca[0].equalsIgnoreCase(""))){ 
		        			lista.add(array.get(i));
		        		}else{
			        		Integer[] encontrados = new Integer[busca.length];
			        		for (int m = 0; m < busca.length; m++) encontrados[m] = 0;
			        		String[] opcao = array.get(i).toLowerCase().split(" ");
			        		for (int j = 0; j < opcao.length; j++){
			        			for (int k = 0; k < busca.length; k++){
					        	   	if (opcao[j].contains(busca[k])){ 
					        	   		encontrados[k]++;
					        	   	}
			        			}
			        		}
			        		Boolean verifica = true;
			        		for (int l = 0; l < busca.length; l++){
			        			if (encontrados[l] == 0)
			        				verifica = false;
			        		}
			        		if (verifica)
			        			lista.add(array.get(i));
		        		}
		        	}
        		}
	        	lista.add("Download demos...");
	        	aa =  new ArrayAdapter<String>(DatasetsListActivity.this,android.R.layout.simple_list_item_1, lista);
	        	lv1=(ListView)findViewById(R.id.listView1);
	            lv1.setAdapter(aa);       	
	        }
	        
			public void beforeTextChanged(CharSequence s, int start, int count, int after){}
	        public void onTextChanged(CharSequence s, int start, int before, int count){}
	    }); 
		
		// By using setAdpater method in listview we an add string array in list.
		lv1.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1 , array));
		lv1.setOnItemClickListener(this);
		lv1.setOnItemLongClickListener(this);
	}
	
	@Override	
    protected void onResume() {
        super.onResume();
//        The activity has become visible (it is now "resumed").
//        File[] files = f.listFiles();

        if (f != null && f.exists()){
// ISSO JÁ ESTÁ SENDO FEITO NO ONCREATE. PRECISA SER REFEITO AQUI????
//			for(int i=0; i<files.length;i++){
//				String name = files[i].getName();
//				if(files[i].isDirectory() && !(files[i].isHidden()) && !(array.contains(name))) 
//					array.add(name);
//				lv1.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1 , array));
//				Collections.sort(array);
//			}
			array.add("Download demos...");
        }else{
        	// Se o caminho não existe, ele cria, então nunca vai dar esse erro.
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Path not found. Fix it in settings.");
			builder.setPositiveButton("OK", this);
			builder.setTitle("Error");
			AlertDialog alert = builder.create();
			alert.show();
        }
    }
	
	public void onItemClick(AdapterView<?> parent, View view,	
	        int position, long id) {
//			Log.v("Gen", String.valueOf(position));
			if(((TextView) view).getText().equals("Download demos...")){
				Intent intent = new Intent(this, ExamplesListActivity.class);
		    	startActivity(intent);
			}
			else{
				Intent intent = new Intent(this, OrientacaoActivity.class);
		    	intent.putExtra("DIR",InVesaliusMobileActivity.diretorio+"/"+((TextView) view).getText());
		    	startActivity(intent);
			}	    	
	}
	
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id){
		if(!((TextView) view).getText().equals("Download demos...")){
			toDelete = ((TextView) view).getText();
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Are you sure ?");
			builder.setCancelable(false);
			builder.setPositiveButton("Yes", this);
			builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			                dialog.cancel();
			           }
			       });
			builder.setTitle("Delete Files");
			AlertDialog alert = builder.create();
			alert.show();
		}
		return true;
	}
	
	public void onClick(DialogInterface dialog, int id){
		delete();
	}
	
	public void delete(){
		new DeleteDirTask().execute(InVesaliusMobileActivity.diretorio+"/"+toDelete);
		array.remove(toDelete);
		lv1.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1 , array));
	}
	
}
