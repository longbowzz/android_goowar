package com.longbowzz.goowar;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class RenderView extends SurfaceView implements SurfaceHolder.Callback{
	private int mWidth, mHeight;
	private SurfaceHolder mSurHolder;
	public static RenderThread mRenderThread;
	static boolean isRunning = false;	

	
	public RenderView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public RenderView(Context context, AttributeSet attrs) {
		super(context, attrs);
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);
		setFocusable(true);
		
		
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		mWidth = width;
		mHeight = height;
		
		if(mRenderThread!=null){
		    mRenderThread.setSize(mWidth, mHeight);
		    mRenderThread.state = mRenderThread.PRE_TITLE;
		}
		//TODO: 强制刷界面
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mSurHolder = holder;
		mRenderThread = new RenderThread(mSurHolder);
		// mSurHolder.setFormat(PixelFormat.RGBA_8888);
		mSurHolder.setFormat(PixelFormat.RGB_565);
		mRenderThread.state = mRenderThread.PRE_TITLE;
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}	
	
	public void startRenderThread(){
		if (mRenderThread == null) { //一般不会走
			mRenderThread = new RenderThread(mSurHolder);
			mRenderThread.setSize(mWidth, mHeight);
		}

		mRenderThread.start();
		isRunning = true;		
	}
	
	
	public boolean onTouchEvent(MotionEvent event) {
		// TODO: handle UI change
		int x = (int) event.getX();
		int y = (int) event.getY();
		// Log.e("TB","x,y="+x+","+y);
		if (!isRunning) {

		} else {
			if (mRenderThread != null) {
								
			}
		}

		return super.onTouchEvent(event);
	}
	
	
}