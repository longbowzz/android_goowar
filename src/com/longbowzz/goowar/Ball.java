package com.longbowzz.goowar;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;



/**
 * Goo的基类， 存储基本物理信息、显示信息等
 * @author zhangzhen
 *
 */
public class Ball{
	public double m;
	public int size=0;
	
	public Bitmap img; //贴图
	public int color;
	
	public double sx,sy;
	public double vx,vy;
	public double ax,ay;
	
	public int x,y;  //屏幕坐标，需要Universe来转换
	
//	public void init(){
//		System.out.println("init - ball");
//	}
	
	public Ball(){
//		init();
	}
	


	public Ball(int size, double mass, double s_x, double s_y, double v_x, double v_y, int col){
		this();
		this.size=size;
		m=mass;
		sx=s_x; sy=s_y;		
		vx=v_x; vy=v_y;
		color=col;
	}
	

	

	
	public void draw(Canvas cvs){
//			g.setColor(c);			
//			g.fillOval(x-size/2, y-size/2, size, size);
//			g.drawOval(x-size/2, y-size/2, size, size);
		//TODO:根据当前的Scale绘制对应的尺寸，颜色球或图片
	}
	
	



  
  public String toString(){
	  return ""+"ball  x="+sx+", y="+sy+",vx="+vx+ ",vy="+vy+",ax="+ax+",ay="+ay;
  }
  
}