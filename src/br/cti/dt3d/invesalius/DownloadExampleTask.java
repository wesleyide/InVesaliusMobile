package br.cti.dt3d.invesalius;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.util.ByteArrayBuffer;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.util.Log;

public class DownloadExampleTask extends AsyncTask<String, Integer, Integer> {
	
	static boolean erro, cancel;
	static int total=0, progresso=0;
	ProgressDialog dialog;
	
	protected void onPreExecute(){
		total=0; progresso=0;
//		ExamplesListActivity.a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		dialog = new ProgressDialog(ExamplesListActivity.c);
		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		dialog.setMessage("Downloading Files.\nPlease wait...");
		dialog.setCancelable(false);
		dialog.setMax(total);
		dialog.setProgress(0);
		dialog.setButton(-2, "Cancel", new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	                cancelTask();
	           }
	       });
		dialog.show();
	}

	protected Integer doInBackground(String... dir){
		String fileURL = "http://www.cti.gov.br/dt3d/invesalius-mobile/demo/"+dir[0];
		String fileName =dir[1]+"Exemplo.zip";
		
		try{

            //Cria um arquivo para receber a String fileName
            File arquivo = new File(fileName);

            if (!arquivo.exists()){

                //cria novo arquivo se não existir
                arquivo.createNewFile();
            }

            //cria uma nova url da url passada por parâmetro pela String fileURL
            URL url = new URL(fileURL);
            File file = new File(fileName);

            //Abre a conexão
            URLConnection cURL = url.openConnection();

            //Cria um InputStrean da conexão
            InputStream is = cURL.getInputStream();

            //Cria um Buffer e insere o input
            BufferedInputStream bis = new BufferedInputStream(is);

            //Cria um ByteArray
            ByteArrayBuffer baf = new ByteArrayBuffer(4096);
            FileOutputStream fos = new FileOutputStream(file);
            
            total = cURL.getContentLength();

            //Variável auxiliar que irá receber o Buffer
            int current = 0;
           
            while ((current = bis.read()) != -1 && !isCancelled()){
            	baf.append((byte)current);
            	progresso++;
            	if(baf.isFull()){
            		fos.write(baf.toByteArray());
            		baf.clear();
            		publishProgress(total, progresso, 0, 0);
            	}
            }
            publishProgress(total, progresso, 0, 0);

            //Transfere os dados para o arquivo e fecha o FileOutputStream...
            if (!cancel){
            	fos.write(baf.toByteArray());
            }
            fos.close();

        }catch (IOException e) {

            //se der algum problema mostrará no log
            Log.i("IOException", e.toString());
            erro = true;
        }
		
		if(!erro && !cancel){
			publishProgress(total, progresso, 1, 0);
			Decompress d = new Decompress(dir[1]+"Exemplo.zip", dir[1]);
			d.unzip();
			File f = new File(dir[1]+"Exemplo.zip");
			f.delete();
			publishProgress(total, progresso, 0, 1);
			return 1;
		}else{
			return 0;
		}
	}
	
	protected void onProgressUpdate(Integer... progress){
		dialog.setMax(progress[0]);
		dialog.setProgress(progress[1]);
		if(progress[2] == 1)
			dialog.setMessage("Extracting Files.\nPlease wait...");
		if(progress[3] == 1){
			dialog.setMessage("Download completed!");
			dialog.getButton(-2).setText("Close");
		}
	}
	
	protected void onPostExecute(Void v){
		dialog.cancel();
		ExamplesListActivity.a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
	}
	
	public void cancelTask(){
		if (dialog.getButton(-2).getText().toString().equalsIgnoreCase("Cancel"))
			cancel = true;
		cancel(true);
	}
	
	protected void onCancelled(Void v){
		dialog.cancel();
		ExamplesListActivity.a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
	}
	
}
