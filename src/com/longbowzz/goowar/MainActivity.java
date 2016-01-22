package com.longbowzz.goowar;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	private LinearLayout menulayout;
	private TextView tx;
	private RenderView rv;
	private Button btnStudy, btnFight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);
		
		menulayout = (LinearLayout)findViewById(R.id.menulayout);
		
		tx = (TextView) this.findViewById(R.id.title);
		
		btnStudy = (Button) this.findViewById(R.id.btn_study);
		btnFight = (Button) this.findViewById(R.id.btn_war);
		
		rv = (RenderView) this.findViewById(R.id.renderview);
		
		
		btnStudy.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				menulayout.setVisibility(View.GONE);
				rv.startRenderThread();				
			}
		});
    }
}
