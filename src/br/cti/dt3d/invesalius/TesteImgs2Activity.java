package br.cti.dt3d.invesalius;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;

import java.io.File;
import java.util.Arrays;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;
 
public class TesteImgs2Activity extends Activity implements OnClickListener, OnSeekBarChangeListener{
    /** Called when the activity is first created. */
    
	ImageViewTouch image;
	String diretorio;	
	String files[];
	int nArq=0;
	int atual=0;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.main2);
        
        diretorio = getIntent().getExtras().getString("DIR")+"/";
//        diretorio="/sdcard/invesalius/CT/Axial";
        File dir = new File(diretorio);
       
        if(dir.exists()){
        	files = dir.list();
        	Arrays.sort(files);
        	nArq=files.length;
        }
        else Toast.makeText(getApplicationContext(),"Problema ao ler arquivos",
				Toast.LENGTH_SHORT).show();
        ImageView zoom = (ImageView) findViewById(R.id.zoom);
        zoom.setOnClickListener(this);
        ImageView bc = (ImageView) findViewById(R.id.bc);
        bc.setOnClickListener(this);
		SeekBar sb = (SeekBar) findViewById(R.id.seekBar1);
		sb.setMax(nArq-1);
		sb.setOnSeekBarChangeListener(this);
		image = (ImageViewTouch) findViewById(R.id.image);
		if(dir.exists()){
			if(nArq==0)Toast.makeText(getApplicationContext(),"Não existem arquivos neste diretório",
				Toast.LENGTH_SHORT).show();
			else image.selectImage(diretorio+files[nArq/2], this);
		}
        sb.setProgress(nArq/2);
    }
	
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
		atual = progress;
		image.selectImage(diretorio+files[progress],this);
	}
	
	@Override
    public void onClick(View v){
    	//Verifica qual botão foi pressionado
		ImageView zoom = (ImageView) findViewById(R.id.zoom);
		ImageView bc = (ImageView) findViewById(R.id.bc);
    	switch(v.getId()){
	       	case R.id.zoom:
	       		image.changeMode(ImageViewTouch.PAN_MODE);
	       		zoom.setImageResource(R.drawable.z_on);
	       		bc.setImageResource(R.drawable.bc_off);
	       		break;
	       	case R.id.bc:
	       		image.changeMode(ImageViewTouch.BC_MODE);
	       		zoom.setImageResource(R.drawable.z_off);
	       		bc.setImageResource(R.drawable.bc_on);
	       		break;
	    }
    }

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		
	}
}
