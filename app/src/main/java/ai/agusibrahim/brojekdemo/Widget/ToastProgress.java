package ai.agusibrahim.brojekdemo.Widget;

import android.widget.Toast;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.graphics.drawable.GradientDrawable;
import android.graphics.Color;
import android.widget.ProgressBar;
import android.widget.LinearLayout;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;

public class ToastProgress extends Toast{
	Context ctx;
	Runnable run, runw;
	Toast t;
	Handler h,h2;
	TextView clt;
	int textColor, bgColor, radius, progColor;
	private ToastProgress.onTimedOutListener callback;
	private ProgressBar prog;
	private LinearLayout root;
	private GradientDrawable shape;
	private boolean showing;
	public ToastProgress(android.content.Context context) {
		super(context);
		this.ctx=context;
		this.h=new Handler();
		this.h2=new Handler();
		root = new LinearLayout(ctx);
		root.setPadding((int)dp2px(ctx, 20), (int)dp2px(ctx, 10), (int)dp2px(ctx, 20), (int)dp2px(ctx, 10));
		root.setOrientation(LinearLayout.HORIZONTAL);
		prog=new ProgressBar(ctx);
		prog.setLayoutParams(new LinearLayout.LayoutParams((int)dp2px(ctx, 20), (int)dp2px(ctx, 20)));
		root.addView(prog);
		clt=new TextView(ctx);
		clt.setTextAppearance(android.R.attr.textAppearanceSmall);
		clt.setTextColor(Color.WHITE);
		LinearLayout.LayoutParams tlp=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		tlp.gravity=Gravity.CENTER_VERTICAL;
		tlp.setMargins((int)dp2px(ctx,10),0,0,0);
		clt.setLayoutParams(tlp);
		root.addView(clt);
		shape = new GradientDrawable();
		shape.setShape(GradientDrawable.RECTANGLE);
	}
	public interface onTimedOutListener{
		void onTimedOut();
	}
	public ToastProgress setOnTimedOutListener(onTimedOutListener x){
		callback=x;
		return this;
	}
	public boolean isShowing(){
		return showing;
	}
	public ToastProgress setSkin(int textColor, int bgColor, int progColor, int radius){
		this.textColor=textColor;
		this.bgColor=bgColor;
		this.radius=radius;
		this.progColor=progColor;
		return this;
	}
	public Toast show(final String str, int gravity, final int dur){
		t=makeText(ctx, str, 0);
		clt.setText(str);
		prog.getIndeterminateDrawable().setColorFilter(progColor==0? Color.WHITE:progColor, android.graphics.PorterDuff.Mode.MULTIPLY);
		shape.setColor(bgColor==0?Color.argb(200, 0,0,0):bgColor);
		shape.setCornerRadius(radius==0? 10:radius);
		root.setBackground(shape);
		clt.setTextColor(textColor==0?Color.WHITE:textColor);
		t.setGravity(gravity==0?Gravity.BOTTOM:gravity,0,0);
		t.setView(root);
		run = new Runnable(){
			@Override
			public void run() {
				t.show();
				h.postDelayed(this, 500);
			}
		};
		h.postDelayed(run, 0);
		runw=new Runnable(){
			@Override
			public void run() {
				h.removeCallbacks(run);
				t.cancel();
				showing=false;
				if(callback!=null) callback.onTimedOut();
			}
		};
		showing=true;
		h2.postDelayed(runw, dur);
		return t;
	}

	@Override
	public void cancel() {
		t.cancel();
		h.removeCallbacks(run);
		h2.removeCallbacks(runw);
		showing=false;
	}
	public float dp2px(Context context, float dipValue) {
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
	}
}
