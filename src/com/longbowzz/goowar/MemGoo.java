package com.longbowzz.goowar;

import java.util.Random;

public class MemGoo extends Goo{
	public static int HIS_CNT = 10;
//	public static int  attN =Goo.attN+HIS_CNT;	//输入节点个数,8个基本输入+10个历史数据 
	
	public double[] HistoryOri = new double[HIS_CNT];
	private int hisIdx=0;  //循环指针，指向当前最老的数据
	
	@Override
	public void init() {
		attN += HIS_CNT;
		input = new double[attN];
	}
	
	
	public MemGoo(int id,Goo ancestor, double sx, double sy, double vx, double vy) {
		super(id, ancestor, sx, sy, vx, vy);
//		System.out.println("MemGoo() input size="+input.length+", attN="+attN);
	}

	
	public 	 MemGoo(int id, double sx, double sy, double vx, double vy, double[] initGene, boolean shouldMutant){
		super(id, sx, sy, vx, vy, initGene, shouldMutant);
//		System.out.println("MemGoo() input size="+input.length+", attN="+attN);
	}

	public MemGoo(int id, Goo dad, Goo mom){
		super(id, dad,mom);
//		System.out.println("MemGoo() input size="+input.length+", attN="+attN);
	}
	
	
	public MemGoo() {
		super();
	}


	/**
	 * 插入新数据，丢弃最老的数据
	 * @param newData
	 */
	public void addHistory(double newData){
		HistoryOri[hisIdx++]= newData;
		if(hisIdx==HIS_CNT){
			hisIdx=0;
		}
	}
	

	/**
	 * 输出全部历史数据(老数据在前)， hisIdx指向最老的数据
	 * @return
	 */
	public double[] getHistory(){
		double[] result = new double[HIS_CNT];
		int tempIdx = hisIdx-1;
		for(int i=0; i<HIS_CNT; i++){
			if(tempIdx<0)
				tempIdx = HIS_CNT-1;
			result[i] = HistoryOri[tempIdx--];

		}
		return result;
	}
	
	@Override
	public void setInput(Goo target, int w, int h) {
		super.setInput(target, w, h);
		//输入完数据更新history
		double[] history = getHistory();
		int start = Goo.Default_attN;
		for(int i=0; i<HIS_CNT; i++){
			input[i+start] = history[i];
		}
		addHistory(input[2]); //将ThetaDiff作为历史数据记忆

	}
	

	public String dumpInput(boolean readable) {
		StringBuilder sb = new StringBuilder();
		if(target!=null){
			if(readable){				
				sb.append(denormalize(input[0], -MAX_SPEED_VEC,MAX_SPEED_VEC));sb.append(",");
				sb.append(denormalize(input[1], -MAX_SPEED_VEC,MAX_SPEED_VEC));sb.append(",");
				sb.append(denormalize(input[2], -180,180));sb.append(",");
				sb.append(denormalize(input[3], 0,800));sb.append(",");
				sb.append(isStopped);sb.append(",");
				sb.append(isTargetHide);sb.append(",");
				sb.append(denormalize(input[6], 0, 20));sb.append(",");
				sb.append(denormalize(input[7], 0, 20));sb.append(",");
				
				sb.append(denormalize(input[8], -180,180));sb.append(",");
				sb.append(denormalize(input[9], -180,180));sb.append(",");
				sb.append(denormalize(input[10], -180,180));sb.append(",");
				sb.append(denormalize(input[11], -180,180));sb.append(",");
				sb.append(denormalize(input[12], -180,180));sb.append(",");
				sb.append(denormalize(input[13], -180,180));sb.append(",");
				sb.append(denormalize(input[14], -180,180));sb.append(",");
				sb.append(denormalize(input[15], -180,180));sb.append(",");
				sb.append(denormalize(input[16], -180,180));sb.append(",");
				sb.append(denormalize(input[17], -180,180));sb.append(",");
				
			}else{
				for(int i=0;i<input.length;i++){
					sb.append(input[i]);
					if(i<input.length-1)
						sb.append(",");
				}				
			}
			sb.append("\n");
		}
		return sb.toString();
	}
	

	
}