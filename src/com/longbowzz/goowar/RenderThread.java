package com.longbowzz.goowar;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.SurfaceHolder;


public class RenderThread extends Thread{
	private int mWidth;
	private int mHeight;
	private SurfaceHolder mSurfaceHolder;
	private Bitmap mBackgroundImage;
	private Universe universe;
	
	public volatile boolean mRun = false;


    
	public final int PRE_TITLE=0;
	public final int TITLE=1;
	public final int STUDY=2;
	public final int TRAINING=3;
	public final int INGAME=4;
	public int state=PRE_TITLE;
	

	public RenderThread(SurfaceHolder surfaceHolder) {
		mSurfaceHolder = surfaceHolder;
	}

	public void setSize(int width, int height) {
		mBackgroundImage = Bitmap.createBitmap(width, height,
				Config.RGB_565);
		mBackgroundImage.eraseColor(Color.BLACK);
		mWidth = width;
		mHeight = height;
		
		if(universe == null)
			universe = new Universe(width, height);
		else
			universe.setSize(width, height);
		
		updateCanvas();
	}

	private void updateCanvas() {
		// TODO: draw things
		Canvas c = null;
		try {
			c = mSurfaceHolder.lockCanvas(null);
			synchronized (mSurfaceHolder) {
				if (mRun) {
					
//					c.drawBitmap(mBackgroundImage, 0, 0, null);
					c.drawColor(Color.BLACK);
					universe.oneStep();
					universe.draw(c);
					
					
				}
			}
		} finally {
			if (c != null) {
				mSurfaceHolder.unlockCanvasAndPost(c);
			}
		}
	}

	public void run() {


		mRun = true; 

		try {
			// Enter into refresh canvas loop
			while (universe.time < 10000) {
				// TODO: main loop
					long profileStart = System.currentTimeMillis();
				    updateCanvas();
//				    Thread.sleep(30);
				    Log.d("GNM673", "profile-drawing cost "+(System.currentTimeMillis() - profileStart)+"ms");
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("GNM673","exception:"+e.getMessage());
			
			
		}
        Log.e("GNM673","END Thread:mRun="+mRun+", state="+state);
		mRun = false;

	}	
}