package com.longbowzz.goowar;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;

/**
 * 系统模拟器，包括物理规律、主循环等
 * 主要考虑实际坐标
 * 现实坐标系也是左上角为0，0，与屏幕坐标系同向，向各个方向无限延伸
 * @author zhangzhen
 *
 */
public class Universe{
	//视觉素材
	private Bitmap bgImg; //背景图
	private Paint gridPaint;
	
	
	//---------- 实际坐标参数 --------------
	
	public static final int EDGE_LEFT = 0;
	public static final int EDGE_RIGHT = 1000;
	public static final int EDGE_TOP = 0;
	public static final int EDGE_BOTTOM = 1000;
	
	/**
	 * 屏幕左上角对应的实际坐标
	 */
	public RealPoint offset = new RealPoint(0d, 0d);  
	
	
	/**
	 * 虚拟边界，goos只能在区域内活动，坐标为实际坐标
	 */
	public Rect borderRect = new Rect(EDGE_LEFT,EDGE_TOP,EDGE_RIGHT, EDGE_BOTTOM);
	
	
	//---------- 屏幕坐标参数 --------------
	/**
	 * 屏幕尺寸，用于计算显示区域
	 */
	public int screenW, screenH;  
	
	/**
	 * 屏幕坐标与实际坐标的比例关系
	 * 实际坐标长度 = 屏幕坐标长度*scale
	 * 实际显示区域的实际坐标为： offsetX, offsetY, offsetX+screenW*scale, offsetY+screenH*scale
	 */
	private float scale = 0.1f; 
	public static final float MIN_SCALE = 0.1f;
	public static final float MAX_SCALE = 1.0f;
	
	
	//---------------其他参数-------------
	/**
	 * 时间轴，每个步长表示迭代一次
	 */
	public int time = 0;
	
	/**
	 * 每个步长的时间跨度，越大越快，但会导致误差
	 */
	private double dt = 0.1d;
	
	

	
	/**
	 * 构造器，
	 * @param sW  屏幕像素宽
	 * @param sH  屏幕像素高
	 */
	public Universe(int sW, int sH){
		screenW = sW;
		screenH = sH;
		gridPaint = new Paint();
		gridPaint.setColor(Color.GRAY);
		gridPaint.setStrokeWidth(1f);
		offset.rx = EDGE_RIGHT/2;
		offset.ry = EDGE_BOTTOM/2;
	}
	
	public void setSize(int sW, int sH){
		screenW = sW;
		screenH = sH;
		//TODO: 强制刷新
	}
	
	/**
	 * 设置视角比例，带上下限制
	 * @param newscale
	 */
	public void setScale(float newscale){
		if(newscale > MAX_SCALE)
			scale = MAX_SCALE;		
		else if(newscale < MIN_SCALE)
			scale = MIN_SCALE;
		else
			scale = newscale;
	}
	
	
	
	/**
	 * 模拟宇宙一个步长，进行一次更新、计算
	 */
	public void oneStep(){
//		Log.d("GNM673", "step.  scale="+scale);
		setScale( (time % 1000) / 1000.0f);
		
		time++;
	}
	
	
	
	
	/**
	 * 屏幕以goo为中心
	 * @param goo
	 */
	public void focusOnGoo(Goo goo){
		if(goo == null)
			return;
		focusOnPoint(goo.getRealPoint());
	}
	
	/**
	 * 以指定坐标为观察中心
	 * @param rp
	 */
	public void focusOnPoint(RealPoint rp){
		if(rp == null)
			return;
		offset.rx = rp.rx - screenW*0.5f*scale;
		offset.ry = rp.ry - screenH*0.5f*scale;
	}	
	
	/**
	 * 绘制宇宙背景
	 * @param cvs
	 */
	public void draw(Canvas cvs){
		//TODO: 绘制宇宙背景图片，采用平铺的方式绘制

		
		//绘制网格，每整倍数坐标绘制网格   
		float realInterval = (float) (Math.ceil(scale * 10)*20f); //实际坐标间隔
		int screenInterval = (int) (realInterval / scale); //屏幕坐标间隔
		Log.d("GNM673", "universe onDraw. scale="+scale + ", screenInterval="+screenInterval);
		double startRealX = offset.rx + realInterval - offset.rx % realInterval;
		double startRealY = offset.ry + realInterval - offset.ry % realInterval;
		Point startPoint = real2Screen(startRealX, startRealY);
		while(startPoint.x < screenW){
			cvs.drawLine(startPoint.x, 0, startPoint.x, screenH, gridPaint);
			startPoint.x += screenInterval;
		}
		while(startPoint.y < screenH){
			cvs.drawLine(0, startPoint.y, screenW, startPoint.y, gridPaint);
			startPoint.y += screenInterval;
		}
	}
	
	
	
	/**
	 * 内部类用来描述真实坐标
	 * @author zhangzhen
	 *
	 */
	public static class RealPoint{
		public double rx,ry;
		public RealPoint(double x, double y){
			rx = x;
			ry = y;
		}
	}
	
	//坐标变换方法
	/**
	 * 屏幕坐标映射为真实坐标
	 * @param x
	 * @param y
	 * @return
	 */
	public RealPoint screen2Real(int x, int y){
		return  new RealPoint(offset.rx + x * scale, offset.ry + y * scale);
	}
	
	/**
	 * 真实坐标映射为屏幕坐标
	 * @param rx
	 * @param ry
	 * @return
	 */
	public Point real2Screen(double rx, double ry){
		return new Point( (int)((rx - offset.rx)/scale),(int)((ry - offset.ry)/scale) );		
	}
	
}