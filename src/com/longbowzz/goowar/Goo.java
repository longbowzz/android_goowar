package com.longbowzz.goowar;

import java.util.Comparator;
import java.util.Random;

import com.longbowzz.goowar.Universe.RealPoint;
import com.longbowzz.goowar.neuralnet.NeuralNet;

/**
 * Goo的基本型，具有神经网络、SPECIAL等属性
 * @author zhangzhen
 *
 */
public class Goo extends Ball {
	//attr 每个个体的内秉属性，可以被遗传和变异
	public float attrSTR = 1;
	public float attrPER = 1;
	public float attrEND = 1;
	public float attrCHR = 1;
	public float attrINT = 1;
	public float attrAGL = 1;
	public float attrLCK = 1;
	public static final float MAX_ATTR = 20;
	public static final float DELTA_ATTR = 0.1f;
	
	//神经网络，表现应激性
	//input:  速度1，速度2 ， 目标相对角度， 目标距离，当前是否被卡住，能否直接看到目标，special属性
	//output: 速度1，速度2
	protected final static int  Default_hidN =50;	//每层节点个数
	protected final static int  Default_attN =8;	//输入节点个数
	protected final static int  Default_outN =4;	//输出节点个数 vx,vy, eat?, mate?
	protected final static int  Default_layerN = 1; //隐藏层层数
	
	public  int hidN =Default_hidN;	//每层节点个数
	public  int  attN =Default_attN;	//输入节点个数
	public  int  outN =Default_outN;	//输出节点个数 vx,vy, eat?, mate?
	public  int  layerN = Default_layerN; //隐藏层层数


	protected static double RATE = 0.01;	//迭代步长
	
	protected  double[] input = new double[attN];	
	
	
	private static double MUTANT_STEP = 0.2d;
	private static double MUTANT_RATE = 0.3d;
	protected static final int MAX_LIFE = 1000;
	public static final int MATE_INTERVAL = MAX_LIFE/20; // (int) (MAX_LIFE*0.1f);
	public static final int EAT_INTERVAL = 0;  //(int) (MAX_LIFE*0.05f);
	public static final double MAX_SPEED_VEC = 5.0f;
	public static final double SPEED_INTERVAL = 0.5f;
	public static final int EAT_RESTORE_LIFE = MAX_LIFE/4;
	
	public float speedFactor =1.0f;
	public int ID;
	public int parentID;
	public int ancestorID;
	public int generation; //代
//	public boolean mated = false; //是否繁衍过
	public int mateTime= MAX_LIFE-MATE_INTERVAL; //当life<=mateTime 时才可以mate
	public int eatTime= MAX_LIFE-EAT_INTERVAL; //当life<=mateTime 时才可以mate
	public Goo target = null;
	public int score=0;  //分数
	public boolean isStopped = false;
	public boolean isTargetHide = false;
	
	public double v1, v2;
	public double lastv1, lastv2;
	/**
	 * 生命耗尽即死亡
	 */
	public int life = MAX_LIFE;

	
	
	public NeuralNet net;
	
	public double[] gene; //用来储存基因
	
	
	//决策
	public double eatValue;
	public double mateValue;
	
	//排序器
	public final static Comparator<Goo> SCORE_COMPARE = new Comparator<Goo>() {

		@Override
		public int compare(Goo goo0, Goo goo1) {
			if(goo0.score>goo1.score)
				return -1;
			else if(goo0.score==goo1.score)
				return 0;
			else 
				return 1;
		}

	};
	
	
	public void init(){
		//派生类会修改此处
	}

	public Goo(){
		super();
	}
	

	/**
	 * 通过一个个体来生成新的个体，如果输入个体为空，则生成第0代个体，否则延续代数
	 * @param id
	 * @param ancestor
	 * @param sx
	 * @param sy
	 * @param vx
	 * @param vy
	 */
	public Goo(int id,Goo ancestor, double sx, double sy, double vx, double vy){
		super(1, 1, sx, sy, vx, vy, 0);
		init();
		ID = id;
		if(ancestor!=null){			
			parentID = ancestor.ID;
			ancestorID = ancestor.ancestorID;
			generation = ancestor.generation;
			color =  getGooColorById(ancestorID);
			net = new NeuralNet(attN, outN, layerN, hidN, RATE);
			net.putWeights(ancestor.gene);
			gene = net.getWeights();
		}else{
			parentID = id;
			ancestorID = id;
			generation = 0;
			color =  getGooColorById(id);
			net = new NeuralNet(attN, outN, layerN, hidN, RATE);
			gene = net.getWeights();
		}

		
		size = (int) attrSTR;
		life = MAX_LIFE;
		mateTime = life - MATE_INTERVAL;
		eatTime = life -EAT_INTERVAL;

	}	
	
	
	
	/**
	 * 利用基因初始化生命体，(第一代)
	 * @param id
	 * @param initGene
	 * @param shouldMutant  是否变异
	 */
	public Goo(int id, double sx, double sy, double vx, double vy, double[] initGene, boolean shouldMutant){
		this(id,null, sx, sy, vx, vy);
		if(!shouldMutant){
			net.putWeights(initGene);
			gene = net.getWeights();
		}else{
			net.putWeights(geneMate(initGene, initGene));
			gene = net.getWeights();
		}
	}
	
	

	/**
	 * 通过繁殖生成新个体
	 * @param id
	 * @param dad
	 * @param mom
	 */
	public Goo(int id, Goo dad, Goo mom){
		this(id, null, dad.sx, dad.sy,-(dad.vx+mom.vx)/2, -(dad.vy+mom.vy)/2);
		parentID = dad.generation>= mom.generation? dad.ID:mom.ID;
		ancestorID = dad.generation>= mom.generation? dad.ancestorID:mom.ancestorID;
		generation = Math.max(dad.generation, mom.generation)+1;
		color = getGooColorById(ancestorID);
		
		gene = geneMate(dad.gene, mom.gene);
		net.putWeights(gene);
	}
		
	
	/**
	 * 与对方有性生殖
	 * @param id
	 * @param goo1
	 * @param goo2
	 * @return
	 */
	public Goo mate(int id,  Goo mom){
		return new Goo(id, this, mom);		
	}
	
	
	
	
	/**
	 * 基因突变函数,选择5%的点进行编译; 编译尺度MUTANT_STEP;
	 * @param geneDad
	 * @param geneMom
	 * @return
	 */
	public static double[] geneMate(double[] geneDad,double[] geneMom){
		int len = geneDad.length;
		double[] geneChild = new double[len];
		int mutantCnt = (int) (len*MUTANT_RATE*Math.random());
		if(mutantCnt==0)mutantCnt=1;
		//merge gene
		for(int i=0; i<len; i++){
			geneChild[i] = (Math.random()>0.5)? geneDad[i] : geneMom[i];
			
		}
		
		//mutant
		for(int i=0; i<mutantCnt; i++){
			int pos = (int) (Math.random()*len);
			geneChild[pos]= geneChild[pos] + (0.5-Math.random())*MUTANT_STEP;
		}
		
		return geneChild;
		
	}	
	
	public void prepareUpdate(int w, int h){
		if(target != null){
			setInput(target, w, h);
		}
	}

	
	
	public void setInput(Goo target, int w, int h){
		//input:  速度1，速度2 ， 目标相对角度， 目标距离，special属性
		//output: 速度1，速度2
		
		//计算相对位置
		//自己的朝向 vx, vy
		//自己的位置sx, sy
		double tx = target.sx;
		double ty = target.sy;
		
		double dx = tx - sx;
		double dy = ty - sy;
		
		double thetaS = Math.atan2(vy, vx);//(-p,p]
		double thetaD = Math.atan2(dy, dx);//(-p,p]
		double thetaDiff = thetaD - thetaS; //(-2p,2p)
		if(thetaDiff<=-Math.PI)
			thetaDiff += (2*Math.PI);
		if(thetaDiff>Math.PI)
			thetaDiff -= (2*Math.PI);  
		//Now thetaDiff is (-p,p]
		
		double dist = Math.sqrt(dx*dx+dy*dy);
		
		input[0] = normalize(v1,-MAX_SPEED_VEC,MAX_SPEED_VEC);
		input[1] = normalize(v2,-MAX_SPEED_VEC,MAX_SPEED_VEC);
		
		input[2] = normalize(thetaDiff,-Math.PI,Math.PI);
		input[3] = normalize(dist,0,w);
		input[4] = isStopped?0:1;
		input[5] = isTargetHide?0:1;
				
		input[6] = normalize(attrSTR, 0, MAX_ATTR);
		input[7] = normalize(target.attrSTR, 0, MAX_ATTR);
		
//		input[9] = normalize(attrPER, 0, 20);
//		input[10] = normalize(attrEND, 0, 20);
//		input[11] = normalize(attrCHR, 0, 20);
//		input[12] = normalize(attrINT, 0, 20);
//		input[13] = normalize(attrAGL, 0, 20);
//		input[14] = normalize(attrLCK, 0, 20);
//		input[16] = normalize(target.attrPER, 0, 20);
//		input[17] = normalize(target.attrEND, 0, 20);
//		input[18] = normalize(target.attrCHR, 0, 20);
//		input[19] = normalize(target.attrINT, 0, 20);
//		input[20] = normalize(target.attrAGL, 0, 20);
//		input[21] = normalize(target.attrLCK, 0, 20);
	}
	
	public double[] tryOnce(){
		return net.update(input);	
	}	
	
	/**
	 * 归一化 0~1
	 * @param value
	 * @param min
	 * @param max
	 * @return
	 */
	public static double normalize(double value, double min, double max){
		double maxdiff = max-min;
		return (value-min)/maxdiff;
	}
	
	/**
	 * 反归一化 0~1
	 * @param value
	 * @param min
	 * @param max
	 * @return
	 */
	public static double denormalize(double value, double min, double max){
		double maxdiff = max-min;
		return value*maxdiff+min;
	}	


 
 
 

	public RealPoint getRealPoint(){
		return new RealPoint(sx, sy);
	}
	
	
	
	
    public static int getGooColorById(int id){
    	Random rand = new Random(id);
    	
    	int R = (int) (127+rand.nextFloat()*127);
    	int G = (int) (rand.nextFloat()*255);
    	int B = (int) (rand.nextFloat()*255);
    	return 0xff000000|R<<16|G<<8|B;
    }
    
    public static int getGooColorByGen(int generation){
    	Random rand = new Random(generation);
    	int id = rand.nextInt();
    	int R = 127+id%127;
    	int G = (id/255)%255;
    	int B = id%255;
    	return 0xff000000|R<<16|G<<8|B;
    } 
	
}