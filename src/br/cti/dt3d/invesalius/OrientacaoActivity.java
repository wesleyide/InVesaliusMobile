package br.cti.dt3d.invesalius;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class OrientacaoActivity extends Activity implements OnItemClickListener {
	
	String[] lista = {"AXIAL","SAGITTAL", "CORONAL"};
	String dir;
	@Override
    protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.custom_dialog);
		
		dir = getIntent().getExtras().getString("DIR");
		ListView lv =(ListView)findViewById(R.id.orientacoes);
		
		lv.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1 , lista));
		lv.setOnItemClickListener(this);

    }
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view,
	        int position, long id) {
			if(((TextView) view).getText().equals("Download demos...")){}
			else{
				Intent intent = new Intent(this, TesteImgs2Activity.class);
				String ndir = dir+"/"+((TextView) view).getText();
		    	intent.putExtra("DIR",ndir);
		    	startActivity(intent);
			}	    	
	}

}
