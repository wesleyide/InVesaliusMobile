package it.sephiroth.android.library.imagezoom;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ViewConfiguration;
import android.widget.Toast;

public class ImageViewTouch extends ImageViewTouchBase {
	
	static final float					MIN_ZOOM	= 0.5f;
	protected ScaleGestureDetector	mScaleDetector;
	protected GestureDetector			mGestureDetector;
	protected int							mTouchSlop;
	protected float						mCurrentScaleFactor;
	protected float						mScaleFactor;
	protected int							mDoubleTapDirection;
	protected GestureListener			mGestureListener;
	protected ScaleListener				mScaleListener;
	public static final int				PAN_MODE	=	1;
	public static final int				BC_MODE	=	0;
	private int							mode = PAN_MODE;
	private int brilho = 0;
	private int contraste = 0;
	Bitmap bitmap;
	int bitmapWidth = 0;
	int bitmapHeight = 0;
	int[] pix ;
	Bitmap bm ;
	int sensitivity = 100;
	int nmax=0;
	
		public ImageViewTouch( Context context, AttributeSet attrs )
	{
		super( context, attrs );
	}
	
	@Override
	protected void init()
	{
		super.init();
		mTouchSlop = ViewConfiguration.getTouchSlop();
		mGestureListener = new GestureListener();
		mScaleListener = new ScaleListener();
		
		mScaleDetector = new ScaleGestureDetector( getContext(), mScaleListener );
		mGestureDetector = new GestureDetector( getContext(), mGestureListener, null, true );
		mCurrentScaleFactor = 1f;
		mDoubleTapDirection = 1;
	}
	
	public void selectImage(String file, Context c)
	{
		String s;
//		int b=0;
		int j=0,i=2;
		File f = new File(file);
		
		try {
			FileInputStream i1 = new FileInputStream(f);
			long file_length = f.length();
			BufferedReader input1 =  new BufferedReader(new InputStreamReader(i1));
			s = input1.readLine();
			s = input1.readLine();
			
			String[] dim = s.split(" ");
			bitmapWidth = Integer.parseInt(dim[0]);
			bitmapHeight = Integer.parseInt(dim[1]);
			pix = new int[bitmapWidth * bitmapHeight];
			s = input1.readLine();
			nmax=Integer.parseInt(s);
			i1 = new FileInputStream(f);
			byte[] buff = new byte[(int)f.length()];
			i1.read(buff, 0, (int)f.length());
			do{i++;}while(buff[i]!='\n');
			do{i++;}while(buff[i]!='\n');
			
			i++;
			while(i<file_length){
				pix[j] = ((buff[i] << 8) & 0xFF00) | (0xFF & buff[i+1]);
				j++;
				i=i+2;
			}
			bm = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
			
			input1.close();
			i1.close();
			
		}
		catch ( IOException e ) {
			Toast.makeText( c, e.toString(), Toast.LENGTH_LONG ).show();
		}
		editarContraste(0,0);
	}
	
	@Override
	public void setImageRotateBitmapReset( RotateBitmap bitmap, boolean reset )
	{
		super.setImageRotateBitmapReset( bitmap, reset );
		mScaleFactor = getMaxZoom() / 3;
	}
	
	@Override
	public boolean onTouchEvent( MotionEvent event )
	{
		mScaleDetector.onTouchEvent( event );
		if ( !mScaleDetector.isInProgress() ) mGestureDetector.onTouchEvent( event );
		int action = event.getAction();
		switch ( action & MotionEvent.ACTION_MASK ) {
			case MotionEvent.ACTION_UP:
				if ( getScale() < 1f ) {
					zoomTo( 1f, 50 );
				}
				break;
		}
		return true;
	}
	
	@Override
	protected void onZoom( float scale )
	{
		super.onZoom( scale );
		if ( !mScaleDetector.isInProgress() ) mCurrentScaleFactor = scale;
	}
	
	protected float onDoubleTapPost( float scale, float maxZoom )
	{
		if ( mDoubleTapDirection == 1 ) {
			if ( ( scale + ( mScaleFactor * 2 ) ) <= maxZoom ) {
				return scale + mScaleFactor;
			} else {
				mDoubleTapDirection = -1;
				return maxZoom;
			}
		} else {
			mDoubleTapDirection = 1;
			return 1f;
		}
	}
	
	class GestureListener extends GestureDetector.SimpleOnGestureListener {
		
		@Override
		public boolean onDoubleTap( MotionEvent e )
		{
			if(mode==PAN_MODE){
				float scale = getScale();
				float targetScale = scale;
				targetScale = onDoubleTapPost( scale, getMaxZoom() );
				targetScale = Math.min( getMaxZoom(), Math.max( targetScale, MIN_ZOOM ) );
				mCurrentScaleFactor = targetScale;
				zoomTo( targetScale, e.getX(), e.getY(), 200 );
				invalidate();
			}
			else{
				brilho=0;
				contraste=0;
				editarContraste(0,0);
			}
			return super.onDoubleTap( e );
		}
		
		
		@Override
		public boolean onScroll( MotionEvent e1, MotionEvent e2, float distanceX, float distanceY )
		{
			if(mode==PAN_MODE){
				if ( e1 == null || e2 == null ) return false;
				if ( e1.getPointerCount() > 1 || e2.getPointerCount() > 1 ) return false;
				if ( mScaleDetector.isInProgress() ) return false;
				if ( getScale() == 1f ) return false;
				scrollBy( -distanceX, -distanceY );
				invalidate();
				return super.onScroll( e1, e2, distanceX, distanceY );
			}
			else if(mode==BC_MODE){
					editarContraste(distanceX, distanceY);
			}
			return super.onScroll( e1, e2, distanceX, distanceY );
		}
		
		@Override
		public boolean onFling( MotionEvent e1, MotionEvent e2, float velocityX, float velocityY )
		{
			if(mode==PAN_MODE){
				if ( e1.getPointerCount() > 1 || e2.getPointerCount() > 1 ) return false;
				if ( mScaleDetector.isInProgress() ) return false;
				
				float diffX = e2.getX() - e1.getX();
				float diffY = e2.getY() - e1.getY();
				
				if ( Math.abs( velocityX ) > 800 || Math.abs( velocityY ) > 800 ) {
					scrollBy( diffX / 2, diffY / 2, 300 );
					invalidate();
				}
			}
			return super.onFling( e1, e2, velocityX, velocityY );
		}
	}
	
	class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
		
		@SuppressWarnings( "unused" )
		@Override
		public boolean onScale( ScaleGestureDetector detector )
		{
			float span = detector.getCurrentSpan() - detector.getPreviousSpan();
			float targetScale = mCurrentScaleFactor * detector.getScaleFactor();
			if ( true ) {
				targetScale = Math.min( getMaxZoom(), Math.max( targetScale, MIN_ZOOM ) );
				zoomTo( targetScale, detector.getFocusX(), detector.getFocusY() );
				mCurrentScaleFactor = Math.min( getMaxZoom(), Math.max( targetScale, MIN_ZOOM ) );
				mDoubleTapDirection = 1;
				invalidate();
				return true;
			}
			return false;
		}
	}
	
	public void changeMode(int newmode){
		mode = newmode;
		if(mode==PAN_MODE)Log.v("IMG", "Modo zoom");
		else if(mode==BC_MODE)Log.v("IMG", "Modo Brilho e Contraste");
	}
	
	public void editarContraste(float dContraste, float dBrilho){
		contraste+=(dContraste)*nmax/255;
		brilho+=(dBrilho)*nmax/255;
		int value;
		if(contraste>nmax/2) contraste=nmax/2;
		float fator = (float)(nmax-2*contraste)/nmax;
		float fator2 = (float)255/nmax;
		int[] pix2 = new int[bitmapWidth * bitmapHeight];
		for (int index = 0; index<(bitmapHeight*bitmapWidth); index++) {
			value = (int)(fator*pix[index]) + contraste;
			value+=brilho;
			value= (int)(fator2 * (float)value);
			if(value>255)value=255;
			else if(value<0)value=0;
			pix2[index]= 0xFF000000 | value << 16 | value << 8 | value;
		}
		
		bm.setPixels(pix2, 0, bitmapWidth, 0, 0, bitmapWidth, bitmapHeight);
		setImageBitmapReset( bm, false);
	}
	
}
