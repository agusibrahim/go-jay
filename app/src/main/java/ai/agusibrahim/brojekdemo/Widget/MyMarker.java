package ai.agusibrahim.brojekdemo.Widget;
/*
 Agus Ibrahim
 http://fb.me/mynameisagoes

 */
import android.widget.FrameLayout;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.Gravity;
import android.graphics.*;
import android.widget.ImageView;
import android.widget.*;
import android.view.*;
import android.view.ViewGroup.*;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.support.annotation.*;
import android.view.View.*;
import ai.agusibrahim.brojekdemo.R;

public class MyMarker extends FrameLayout
{
	private int colbase,colfocus,padding,paddingH,hh,ww,hhtype,wwtype;
	float radius;

	private ImageView imageView_948;
	private GradientDrawable shape;
	private CharSequence txt;
	private LinearLayout linearLayout_721;
	private TextView textView_670;
	private View.OnClickListener onclic;
	public MyMarker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
		//Toast.makeText(context, "A"+attrs.getStyleAttribute()+","+defStyle,0).show();
        initView();
    }
    public MyMarker(Context context, AttributeSet attrs) {
        super(context, attrs);
		TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.ValueTip);
		colbase=a.getColor(R.styleable.ValueTip_colorNormal, 0);
		colfocus=a.getColor(R.styleable.ValueTip_colorPressed,0);
		txt=a.getString(R.styleable.ValueTip_text);
		radius=a.getDimension(R.styleable.ValueTip_radius,0);
		padding=(int)a.getDimension(R.styleable.ValueTip_paddingVertical,40);
		paddingH=(int)a.getDimension(R.styleable.ValueTip_paddingHorizontal,60);
		//Toast.makeText(context, "B"+txt,0).show();
        initView();
    }
    public MyMarker(Context context) {
        super(context);
        initView();
    }

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		//LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(widthMeasureSpec, heightMeasureSpec);
		//linearLayout_721.setLayoutParams(layoutParams);
		hh=MeasureSpec.getSize( heightMeasureSpec);
		ww=MeasureSpec.getSize( widthMeasureSpec);
		hhtype=MeasureSpec.getMode(heightMeasureSpec);
		wwtype=MeasureSpec.getMode(widthMeasureSpec);
		////Toast.makeText(getContext(), "kari",0).show();
		android.util.Log.d("kk", "measure");
		LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(wwtype==MeasureSpec.EXACTLY? LayoutParams.MATCH_PARENT:LayoutParams.WRAP_CONTENT, hhtype==MeasureSpec.EXACTLY? LayoutParams.MATCH_PARENT:LayoutParams.WRAP_CONTENT);
		textView_670.setLayoutParams(lp);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
    private void initView() {
		linearLayout_721 = new LinearLayout(getContext());
		linearLayout_721.setOrientation(LinearLayout.VERTICAL);
		linearLayout_721.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(ww, hh);
		//linearLayout_721.setLayoutParams(llp);
		shape = new GradientDrawable();
		shape.setShape(GradientDrawable.RECTANGLE);
		shape.setCornerRadius(radius);
		shape.setColor(colbase);
		
		textView_670 = new TextView(getContext());
		textView_670.setText(txt);
		textView_670.setBackgroundDrawable(shape);
		textView_670.setGravity(Gravity.CENTER);
		textView_670.setTextColor(Color.parseColor("#FFFFFF"));
		textView_670.setTypeface(null, Typeface.BOLD);
		textView_670.setPadding(paddingH,padding,paddingH,padding);
		textView_670.setCompoundDrawablesRelativeWithIntrinsicBounds(null,null,getResources().getDrawable(R.drawable.ic_chevron_right),null);
		//textView_670.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
		////Toast.makeText(getContext(), "tipe W:"+wwtype+"--tipe H:"+MeasureSpec.AT_MOST,0).show();
		linearLayout_721.addView(textView_670);
		imageView_948 = new ImageView(getContext());
		imageView_948.setImageResource(R.drawable.ic_triangle);
		imageView_948.setColorFilter(colfocus);
		LinearLayout.LayoutParams attributLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		attributLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;
		//lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
		imageView_948.setLayoutParams(attributLayoutParams);
		linearLayout_721.addView(imageView_948);
		addView(linearLayout_721);
		textView_670.post(new Runnable(){
				@Override
				public void run() {
					imageView_948.post(new Runnable(){
							@Override
							public void run() {
								setPadding(0,0,0,(textView_670.getHeight()/2)+(imageView_948.getHeight()*2));
							}
						});
				}
			});
		linearLayout_721.setOnTouchListener(new View.OnTouchListener(){
				@Override
				public boolean onTouch(View p1, MotionEvent p2) {
					switch(p2.getAction()){
						case MotionEvent.ACTION_DOWN:
							shape.setColor(colfocus);
							imageView_948.setColorFilter(colfocus);
							break;
						case MotionEvent.ACTION_UP:
							shape.setColor(colbase);
							imageView_948.setColorFilter(colfocus);
							break;
					}
					return false;
				}
			});
		linearLayout_721.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View p1) {
					if(onclic!=null) onclic.onClick(p1);
				}
			});
	}

	@Override
	public void setOnClickListener(View.OnClickListener l) {
		onclic=l;
	}
	
	public void setColorRes(int normal, int pressed, int textColor){
		setColor_(getResources().getColor(normal), getResources().getColor(pressed), textColor==0?0:getResources().getColor(textColor));
	}
	public void setColor(int normal, int pressed, int textColor){
		setColor_(normal, pressed, textColor);
	}
	public void setColor_(int normal, int pressed, int textColor){
		shape.setColor(normal);
		imageView_948.setColorFilter(pressed);
		colbase=normal;
		colfocus=pressed;
		if(textColor!=0) textView_670.setTextColor(textColor);
	}
	public void setText(String txt){
		textView_670.setText(txt);
	}
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return super.onInterceptTouchEvent(ev);
	}
	
	/*@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch(event.getAction()){
			case MotionEvent.ACTION_DOWN:
				shape.setColor(colfocus);
				imageView_948.setColorFilter(colfocus);
				break;
			case MotionEvent.ACTION_UP:
				shape.setColor(colbase);
				imageView_948.setColorFilter(colbase);
				break;
		}
		////Toast.makeText(getContext(), ""+event.getButtonState(),0).show();
		return false;
	}*/
}
