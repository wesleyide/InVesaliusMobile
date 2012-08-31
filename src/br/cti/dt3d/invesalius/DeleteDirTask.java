package br.cti.dt3d.invesalius;

import java.io.File;

import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;

public class DeleteDirTask extends AsyncTask<String, Void, Void> {
	
	ProgressDialog dialog;
	
	protected Void doInBackground(String... dir){
		deleteDir(new File(dir[0]));
		return null;
	}
	
	protected void onPostExecute(Void v){
		dialog.cancel();
		DatasetsListActivity.a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
	}
	
	protected void onPreExecute(){
		//DatasetsListActivity.a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		dialog = ProgressDialog.show(DatasetsListActivity.c, "", "Deleting Files. Please wait...", true);
	}
	
	//Função recursiva para deletar um diretório e todo seus arquivos
	public void deleteDir(File f){
		int i;
		if(f.exists()){
			File[] files = f.listFiles();
			for(i=0;i<files.length;i++){
				if(files[i].isDirectory())deleteDir(files[i]);
				else files[i].delete();
			}
			f.delete();
		}
	}

}
