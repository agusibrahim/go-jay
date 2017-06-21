package ai.agusibrahim.brojekdemo;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.view.*;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.GoogleMap;
import android.widget.AutoCompleteTextView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.CameraUpdateFactory;
import android.widget.ImageView;
import com.google.android.gms.maps.model.*;
import com.google.android.gms.maps.Projection;
import android.graphics.Point;
import java.util.Locale;
import android.location.Geocoder;
import android.location.Address;
import java.util.List;
import android.widget.Toast;
import com.google.android.gms.common.api.*;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.common.*;
import android.graphics.Color;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;
import android.animation.*;
import com.jakewharton.scalpel.*;
import android.widget.EditText;
import java.util.*;
import android.location.Location;
import ai.agusibrahim.brojekdemo.Widget.MyMarker;
import ai.agusibrahim.brojekdemo.Helper.PlaceAutoCompleteHelper;
import ai.agusibrahim.brojekdemo.Helper.DirectionDrawHelper;
import ai.agusibrahim.brojekdemo.Helper.Utils;
import ai.agusibrahim.brojekdemo.Widget.TariffView;
import java.text.DecimalFormat;
import java.math.RoundingMode;
import android.support.v7.app.AlertDialog;
import android.content.*;
import ai.agusibrahim.brojekdemo.Model.*;
import android.widget.ProgressBar;
import android.location.LocationManager;
import com.google.android.gms.location.*;
import android.support.v4.app.ActivityCompat;
import android.content.pm.PackageManager;

public class MainActivity extends AppCompatActivity implements DirectionDrawHelper.OnNavigateReadyListener, View.OnClickListener, OnMapReadyCallback,PlaceAutoCompleteHelper.onSuggestResultListener,PlaceAutoCompleteHelper.onTextFocusListener {
	Toolbar toolbar;
	MyMarker centerMarker;
	private GoogleMap gmaps;
	LatLng start_place,end_place;
	AutoCompleteTextView addr_from,addr_to;
	private PlaceAutoCompleteHelper pickerHelper;
	LatLng home=new LatLng(-6.169994, 106.830928);
	private Geocoder geoCoder;
	private View searcharea,mapframe;
	TariffView tariff;
	private int mapsPadding;
	ViewPropertyAnimator searchareaAnimate;
	private boolean mMapIsTouched;
	boolean sbOnceMove=true;
	ScalpelFrameLayout scapel;
	EditText fok;
	boolean haszoom=false;
	boolean movetomylocation=false;
	List<Marker> drivers=new ArrayList<Marker>();
	private Marker firstMarker;
	final int REQUEST_CHECK_SETTINGS=122;
	final int REQUEST_LOCATION=125;
	private ProgressBar waitIndicator;
	private android.os.Handler handler=new android.os.Handler();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		waitIndicator=(ProgressBar) toolbar.findViewById(R.id.toolbarProgressBar1);
		mapframe=findViewById(R.id.map_frame);
		tariff=(TariffView) findViewById(R.id.tariff);
		centerMarker=(MyMarker) findViewById(R.id.mainactivity_makercenter);
		addr_from=(AutoCompleteTextView) findViewById(R.id.booking2_from);
		addr_to=(AutoCompleteTextView) findViewById(R.id.booking2_dest);
		scapel=(ScalpelFrameLayout) findViewById(R.id.scapel);
		fok=(EditText) findViewById(R.id.fok);
		findViewById(R.id.clearfrom).setOnClickListener(this);
		findViewById(R.id.clearto).setOnClickListener(this);
		searcharea=findViewById(R.id.searcharea);
		addr_from.setTag(new LatLng(0,0));
		addr_to.setTag(new LatLng(0,0));
		searchareaAnimate=searcharea.animate();
		setSupportActionBar(toolbar);
		initilizeMap();
		geoCoder = new Geocoder(getBaseContext(), Locale.getDefault());
		pickerHelper=new PlaceAutoCompleteHelper(addr_from, addr_to);
		pickerHelper.setOnSuggestResultListener(this);
		pickerHelper.setOnFocusListener(this);
		centerMarker.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View p1) {
					onMarkerClick();
				}
			});
		KeyboardVisibilityEvent.setEventListener(
			this,
			new KeyboardVisibilityEventListener() {
				@Override
				public void onVisibilityChanged(boolean isOpen) {
					if(!isOpen&&isNavigationReady()&&!mMapIsTouched){
						setNavigationFocus();
					}
				}
			});
		waitIndicator.getIndeterminateDrawable().setColorFilter(Color.WHITE, android.graphics.PorterDuff.Mode.MULTIPLY);
	}
	
	private void initilizeMap() {
        MapFragment mapf=(MapFragment) getFragmentManager().findFragmentById(R.id.map);
		mapf.getMapAsync(this);
		tariff.hide(false);
	}
	private void checkPerms(){
		if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
		} else {
			GPSrequest();
			gmaps.setMyLocationEnabled(true);
			gmaps.getMyLocation();
		}
	}
	@Override
	public void onRequestPermissionsResult(int requestCode,
										   String[] permissions,
										   int[] grantResults) {
		if (requestCode == REQUEST_LOCATION) {
			if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				GPSrequest();
			} else {
				Toast.makeText(this, "Akses lokasi tidak di izinkan", 6000).show();
				finish();
			}
		}
	}
	
	public void onMarkerClick() {
		// centerMarker di klik
		LatLng cur=gmaps.getCameraPosition().target;
		if(addr_from.isFocused()){
			setAddrValue(addr_from, cur);
		}else if(addr_to.isFocused()){
			setAddrValue(addr_to, cur);
		}
	}
	
	@Override
	public void onClick(View p1) {
		if(p1.getId()==R.id.clearfrom){
			addr_from.setText("", false);
			addr_from.requestFocus();
		}else if(p1.getId()==R.id.clearto){
			addr_to.setText("", false);
			addr_to.requestFocus();
		}
	}
	
	@Override
	public void onSuggestResult(Place place, AutoCompleteTextView act) {
		if(!isNavigationReady()&&(addr_from.getText().length()<1||addr_to.getText().length()<1))
			gmaps.animateCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
		setAddrValue(act==addr_from?addr_from:addr_to, place.getLatLng());
	}
	
	@Override
	public void onFocus(final AutoCompleteTextView act) {
		LatLng actpos=(LatLng)act.getTag();
		// atur centerMarker tampil saat rute telah siap, saat sebelumnya sembunyi
		if(isNavigationReady()&&centerMarker.getVisibility()==View.GONE&&(addr_from.isFocused()||addr_to.isFocused())){
			centerMarker.setVisibility(View.VISIBLE);
		}
		if(act==addr_from&&drivers.size()>0&&!drivers.get(0).isVisible()){
			for(Marker m:drivers) {m.setVisible(true);}
		}
		// saat EditText fokus, sorot ke arah alamat EditText tersebut
		if (actpos.latitude != 0) {
			gmaps.animateCamera(CameraUpdateFactory.newLatLng(actpos), 1500, new GoogleMap.CancelableCallback(){
					@Override
					public void onFinish() {
						// animasi sorot selesai
						// jika haszoom bernilai false, zoom 0.5
						if(!haszoom) gmaps.animateCamera(CameraUpdateFactory.zoomBy(0.5f), 800,null);
						haszoom=true;
					}

					@Override
					public void onCancel() {
						// TODO: Implement this method
					}
				});
		}
		// atur label pada centerMarker
		if (act == addr_from) {
			centerMarker.setText("Lokasi Jemput");
			centerMarker.setColorRes(R.color.colorAddrStart, R.color.colorAddrStartPressed, 0);
		} else if (act == addr_to) {
			centerMarker.setText("Lokasi Tujuan");
			centerMarker.setColorRes(R.color.colorAddrEnd, R.color.colorAddrEndPressed, 0);
		}
	}
	
	// saat rute siap
	// di trigger saat request mencari arah rute/navigasi menggunakan DirectionDrawHelper
	@Override
	public void onNavigationReady(Polyline path, Jarak j) {
		// sembunyikan centerMarker, hilangkan fokus pada searchview dan tampilkan tarif
		centerMarker.setVisibility(View.GONE);
		fok.setFocusable(true);
		fok.requestFocus();
		tariff.show();
		// dapatkan jarak dari titik A ke B dari google maps
		// ini akurat karena menghitung berdasarkan rute jalan yang ditempuh
		double jarak=j.distanceInKm;//Utils.distance(na[0], na[1], 'K');
		DecimalFormat df = new DecimalFormat("#.#");
		//df.setRoundingMode(RoundingMode.CEILING);
		haszoom=false;
		// hapus driver, hapus marker pertama
		for(Marker m:drivers) {m.setVisible(false);}
		if(firstMarker!=null)firstMarker.remove();
		// set label jarak serta tarif
		tariff.setTarifByJarak(jarak);
		tariff.setJarak(String.format("Jarak (%s Km)", df.format(jarak)));
	}

	// gagal menemukan rute
	@Override
	public void onNavigationFailed() {
		Toast.makeText(this, "Gagal menemukan rute. Cek koneksi Internet kamu",0).show();
	}
	
	// gagal membuat koneksi ke layanan Google
	@Override
	public void onSuggestConnectionFailed(ConnectionResult result) {
		Toast.makeText(this, "Tidak bisa terhubung ke layanan Google.",1).show();
	}

	// gagal memuat suggestion
	@Override
	public void onSuggestFail(Status status) {
		Toast.makeText(this, "Tidak ditemukan, Cek koneksi Internet kamu",0).show();
	}
	
	// saat maps siap
	@Override
	public void onMapReady(final GoogleMap map) {
		gmaps = map;
		checkPerms();
		map.getUiSettings(). setMyLocationButtonEnabled(false);
		// saat searcharea siap, cari tahu tingginya berapa, dan jadikan tinggi searcharea sebagai variabel maps padding
		searcharea.post(new Runnable(){
				@Override
				public void run() {
					mapsPadding=searcharea.getHeight();
				}
			});
		map.setPadding(20,0,20,10);
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(home, 16f));
		// camera move listener
		map.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener(){
				@Override
				public void onCameraMove() {
					// atur centerMarker samar saat maps digerakan
					centerMarker.setAlpha(0.5f);
					// hanya saat maps digerakan menggunakan tangan (buka programatically)
					if (mMapIsTouched) {
						// one shot action
						if (sbOnceMove) {
							// animasi sembunyi pada searchbar
							searchareaAnimate.setStartDelay(0);
							searchareaAnimate.setDuration(500);
							searchareaAnimate.translationY(-mapsPadding + (-toolbar.getMeasuredHeight()));
							searchareaAnimate.start();
							UIUtil.hideKeyboard(MainActivity.this);
						}
						sbOnceMove = false;
					}
				}
			});
		final Runnable runnableOnTimedOut=new Runnable(){
			@Override
			public void run() {
				if(waitIndicator.getVisibility()==View.VISIBLE){
					Toast.makeText(MainActivity.this, "GPS Waiting Timed out", 1).show();
				}
			}
		};
		// camera idle (diam) listener
		// di trigger saat pertama maps dijalankan dan saat selesai dari move (menggeser maps)
		map.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener(){
				@Override
				public void onCameraIdle() {
					// atur centerMarker ke solid (bukan samar lagi)
					centerMarker.setAlpha(1.0f);
					// animasi tampil pada searchbar
					searchareaAnimate.setStartDelay(1500).setDuration(800).translationY(0).start();
					sbOnceMove=true;
				}
			});
		// saat menekan tombol GPS (my location)
		tariff.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener(){
				@Override
				public boolean onMyLocationButtonClick() {
					// cek apakah GPS aktif
					// jika tidak maka jalankan GPSrequest()
					final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
					if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
						GPSrequest();
					}else{
						// jika GPS aktif, tampikan waiting indikator dan cari lokasi pengguna
						movetomylocation=true;
						waitIndicator.setVisibility(View.VISIBLE);
						gmaps.getMyLocation();
						handler.postDelayed(runnableOnTimedOut, (3 * 60)*1000);
					}
					return false;
				}
			});
		// di trigger saat lokasi pengguna terdeteksi atau terjadi perubahan lokasi
		map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener(){
				@Override
				public void onMyLocationChange(Location loc) {
					if(movetomylocation){
						handler.removeCallbacks(runnableOnTimedOut);
						waitIndicator.setVisibility(View.INVISIBLE);
						gmaps.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(loc.getLatitude(), loc.getLongitude()), 15f), 1500, null);
					}
					movetomylocation=false;
				}
			});
	}
	
	// fungsi fokus pada navigasi
	// di trigger saat keyboard hilang dari searchbar (biasanya saat menekan back) dan saat tekan back pada mode fokus navigasi
	private void setNavigationFocus(){
		centerMarker.setVisibility(View.GONE);
		LatLng[] na=getNaviagatePoint();
		Utils.requestCenterCamera(this, gmaps, na[0], na[1], mapsPadding);
		fok.setFocusable(true);
		fok.requestFocus();
		haszoom=false;
		for(Marker m:drivers) {m.setVisible(false);}
	}
	
	// fungsi untuk mencari tahu poin A dan B (dari dan lokasi tujuan)
	private LatLng[] getNaviagatePoint(){
		LatLng addr_fromPos=(LatLng)addr_from.getTag();
		LatLng addr_toPos=(LatLng)addr_to.getTag();
		return new LatLng[]{addr_fromPos, addr_toPos};
	}
	
	// fungsi mengecek apakah rute siap (point A dan B sudah ditentukan)
	private boolean isNavigationReady(){
		LatLng[] na=getNaviagatePoint();
		return (na[0].latitude!=0&&na[1].latitude!=0);
	}
	
	// fungsi atur value di searchbar
	// di trigger saat pengguna mengklik hasil pencarian (place suggestion) atau menekan centerMarker
	private void setAddrValue(AutoCompleteTextView acc, final LatLng loc) {
		// dapatkan nama tempat dari koordinat point
		try {
			List<Address> result=geoCoder.getFromLocation(loc.latitude, loc.longitude, 1);
			if (result.size() > 0) {
				Address addr=result.get(0);
				String ll=String.format("%s, %s, %s, %s %s", addr.getAddressLine(0),
										addr.getAddressLine(1), addr.getAddressLine(2), addr.getAddressLine(3), addr.getAddressLine(4));
				acc.setTag(loc);
				acc.setText(ll, false);
				// beralih fokus antar Lokasi Jemput dan Tujuan
				if(acc==addr_from&&!isNavigationReady()) addr_to.requestFocus();
				else if(acc==addr_to&&!isNavigationReady()) addr_from.requestFocus();
			}
		} catch (Exception e) {
			Toast.makeText(MainActivity.this, "Gagal menemukan lokasi, cek koneksi internet kamu.", 1).show();
			return;
		}
		// dapatkan lokasi A dan B
		LatLng[] na=getNaviagatePoint();
		android.util.Log.d("lok", "start:"+na[0]+"----end:"+na[1]);
		// saat rute siap (point A dan B sudah ditentukan)
		if(isNavigationReady()){
			// cegah jarak yang terlalu jauh (batas 27Km)
			// ini tidak akurat karena mengabaikan rute jalan yang ada
			// tapi ini bekerja offline
			if(Utils.distance(na[0], na[1])>27){
				//acc.setText("", false);
				acc.setTag(new LatLng(0,0));
				Toast.makeText(this, "Jarak terlalu jauh, maksimal 27 Km",1).show();
				return;
			}
			// jika path (jalur navigasi) sudah dibuat
			if(DirectionDrawHelper.pathline!=null){
				// hapus path serta marker A B
				DirectionDrawHelper.pathline.remove();
				DirectionDrawHelper.add_startMarker.remove();
				DirectionDrawHelper.add_endMarker.remove();
			}
			UIUtil.hideKeyboard(this);
			// cari arah navigasi menggunakan DirectionDrawHelper dan membuat path (jalur navigasi) nya
			DirectionDrawHelper pos=new DirectionDrawHelper(this, gmaps, na[0], na[1], mapsPadding);
			pos.setOnNavigateReadyListener(this);
			pos.start();
			
		// saat navigasi belum siap (baru menentukan 1 point, A/B)
		} else{
			// jika belum di zoom, zoom kamera ke point pertama
			if(!haszoom||gmaps.getCameraPosition().zoom<=16f)
				gmaps.animateCamera(CameraUpdateFactory.zoomBy(0.8f), 1000, new GoogleMap.CancelableCallback(){
						@Override
						public void onFinish() {
							// geser kamera supaya tidak menutup/timpa marker pertama
							gmaps.animateCamera(CameraUpdateFactory.scrollBy(Utils.dp2px(MainActivity.this, 50),Utils.dp2px(MainActivity.this, 50)));
						}
						@Override
						public void onCancel() {
							// TODO: Implement this method
						}
					});
			// buat marker di point pertama
			firstMarker=gmaps.addMarker(new MarkerOptions().position(loc).icon(BitmapDescriptorFactory.fromResource(acc==addr_from?R.drawable.ic_start_marker:R.drawable.ic_end_marker)));
			
			haszoom=true;
		}
		// jika data dari addr_from
		if(acc==addr_from){
			// hapus dan hilangkan semua drivers
			for(Marker m:drivers) m.remove();
			drivers.clear();
			// buat driver (palsu) yang tersedia diradius 1km
			// --for testing purpose only--
			for(int i=0;i<(acc==addr_from?7:0);i++){
				LatLng rnd=Utils.getRandLocation(loc, 1000);
				drivers.add(gmaps.addMarker(new MarkerOptions().position(rnd).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_bajaj_driver))));
			}
		}
	}

	// override dispatchTouchEvent
	// untuk menentukan apakah view sedang di sentuh atau tidak
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(!mMapIsTouched){
					mMapIsTouched = true;
				}
                break;
            case MotionEvent.ACTION_UP:
                mMapIsTouched = false;
                break;
        }
        return super.dispatchTouchEvent(ev);
	}

	// saat tombol back ditekan
	@Override
	public void onBackPressed() {
		// jika rute siap dan centerMarker nampil
		if(isNavigationReady()&&centerMarker.getVisibility()==View.VISIBLE){
			// fokus ke navigasi
			setNavigationFocus();
			return;
		// jika hanya rute siap
		}else if(isNavigationReady()){
			// tampilkan notif untuk mencancel booking (navigasi)
			AlertDialog.Builder d=new AlertDialog.Builder(this);
			d.setTitle("Konfirmasi");
			d.setMessage("Batalkan Booking?");
			d.setPositiveButton("Ya", new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface p1, int p2) {
						// clear, kembalikan ke awal
						addr_from.setTag(new LatLng(0,0));
						addr_to.setTag(new LatLng(0,0));
						addr_from.setText("", false);
						addr_to.setText("", false);
						drivers.clear();
						tariff.hide(true);
						DirectionDrawHelper.clearNavigate();
						centerMarker.setVisibility(View.VISIBLE);
						addr_from.requestFocus();
						haszoom=false;
						waitIndicator.setVisibility(View.INVISIBLE);
						Toast.makeText(MainActivity.this, "Booking Dibatalkan",0).show();
					}
				});
			d.setNegativeButton("Tidak", null);
			d.show();
		}else{
			haszoom=false;
			super.onBackPressed();
		}
	}
	
	public void GPSrequest()
    {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
			.addLocationRequest(locationRequest);
        builder.setAlwaysShow(true); //this is the key ingredient

        PendingResult<LocationSettingsResult> result =
			LocationServices.SettingsApi.checkLocationSettings(pickerHelper.mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
				@Override
				public void onResult(LocationSettingsResult result) {
					final Status status = result.getStatus();
					final LocationSettingsStates state = result.getLocationSettingsStates();
					switch (status.getStatusCode()) {
						case LocationSettingsStatusCodes.SUCCESS:
							// All location settings are satisfied. The client can initialize location
							// requests here.
							break;
						case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
							// Location settings are not satisfied. But could be fixed by showing the user
							// a dialog.
							try {
								// Show the dialog by calling startResolutionForResult(),
								// and check the result in onActivityResult().
								status.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
							} catch (IntentSender.SendIntentException e) {
								// Ignore the error.
							}
							break;
						case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
							// Location settings are not satisfied. However, we have no way to fix the
							// settings so we won't show the dialog.
							break;
					}
				}
			});
    }
	private void AlertNoGps() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
			.setCancelable(false)
			.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
					startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
				}
			})
			.setNegativeButton("No", new DialogInterface.OnClickListener() {
				public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                    dialog.cancel();
				}
			});
		final AlertDialog alert = builder.create();
		alert.show();
	}
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
// Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case RESULT_OK:
                        gmaps.setMyLocationEnabled(true);
						gmaps.getMyLocation();
                        break;
                    case RESULT_CANCELED:
                        GPSrequest();//keep asking if imp or do whatever
                        break;
                }
                break;
        }
    }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		scapel.setLayerInteractionEnabled(!scapel.isLayerInteractionEnabled());
		return super.onOptionsItemSelected(item);
	}
	
}
