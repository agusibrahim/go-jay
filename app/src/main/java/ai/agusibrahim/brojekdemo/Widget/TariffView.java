package ai.agusibrahim.brojekdemo.Widget;
/*
 Agus Ibrahim
 http://fb.me/mynameisagoes

 */
import android.widget.FrameLayout;
import android.util.AttributeSet;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import ai.agusibrahim.brojekdemo.R;
import android.widget.TextView;
import android.graphics.Paint;
import ai.agusibrahim.brojekdemo.Helper.*;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.GoogleMap.*;
import android.animation.*;

public class TariffView extends FrameLayout
{
	TextView jarak, oldprice,lowprice,normprice,reload;
	private View v, myLoc, jarakBar, rootv;
	public static int myHeight=0;
	public static int[] jarakBarHeight=new int[2];
	private GoogleMap.OnMyLocationButtonClickListener callmyloc;
	public TariffView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }
    public TariffView(Context context, AttributeSet attrs) {
        super(context, attrs);
		initView();
    }
    public TariffView(Context context) {
        super(context);
        initView();
    }

	private void initView() {
		v=LayoutInflater.from(getContext()).inflate(R.layout.tariff_view, null);
		jarak=(TextView) v.findViewById(R.id.tariffviewJarak);
		oldprice=(TextView) v.findViewById(R.id.tariffviewOldPrice);
		normprice=(TextView) v.findViewById(R.id.tariffviewNormalPrice);
		lowprice=(TextView) v.findViewById(R.id.tariffviewLowPrice);
		reload=(TextView) v.findViewById(R.id.tariffviewReload);
		jarakBar=v.findViewById(R.id.tariffviewLinearLayout1);
		myLoc=v.findViewById(R.id.tariffviewMyLoc);
		rootv=v.findViewById(R.id.tariffviewLinearLayout2);
		oldprice.setPaintFlags(oldprice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
		myLoc.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View p1) {
					if(callmyloc!=null) callmyloc.onMyLocationButtonClick();
				}
			});
		post(new Runnable(){
				@Override
				public void run() {
					myHeight=getHeight();
				}
			});
		addView(v);
	}
	public void setJarak(String s){
		jarak.setText(s);
	}
	public void setOnMyLocationButtonClickListener(GoogleMap.OnMyLocationButtonClickListener x){
		callmyloc=x;
	}
	public void hide(final boolean animate){
		rootv.post(new Runnable(){
				@Override
				public void run() {
					animate().setStartDelay(0);
					animate().setDuration(animate?500:0);
					animate().translationY(rootv.getHeight());
					animate().start();
				}
			});
	}
	public void show(){
		animate().setStartDelay(1000);
		animate().setDuration(1500);
		animate().translationY(0);
		animate().start();
	}
	public void setTarifByJarak(double d){
		long tarif=0;
		long tarifdisc=0;
		if(d<=3){
			tarif=6000;
			tarifdisc=tarif-1000;
		}else{
			tarif=Math.round(((d-3)*2000)+6000);
			tarifdisc=tarif-(tarif>20000?10000:1000);
		}
		long roundedTarif = ((tarif + 99) / 100 ) * 100;
		long roundedTarifdisc = ((tarifdisc + 99) / 100 ) * 100;
		normprice.setText(priceFormater(roundedTarif, "Rp"));
		oldprice.setText(priceFormater(roundedTarif, "Rp"));
		lowprice.setText(priceFormater(roundedTarifdisc, "Rp"));
	}
	public String priceFormater(long s, String currency){
		return (currency+s).replaceAll("(\\d)(?=(\\d{3})+(?!\\d))", "$1.");
	}
}
