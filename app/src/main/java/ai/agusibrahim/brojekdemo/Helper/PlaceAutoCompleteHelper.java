package ai.agusibrahim.brojekdemo.Helper;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;
import android.widget.ArrayAdapter;
import android.text.style.CharacterStyle;
import android.content.Context;
import android.graphics.Typeface;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;
import ai.agusibrahim.brojekdemo.R;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.data.DataBufferUtils;
import android.support.v4.app.FragmentActivity;
import ai.agusibrahim.brojekdemo.Helper.PlaceAutoCompleteHelper.*;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.app.Application;
import android.widget.ImageView;
import com.google.android.gms.location.LocationServices;

public class PlaceAutoCompleteHelper implements GoogleApiClient.OnConnectionFailedListener, Application.ActivityLifecycleCallbacks, View.OnFocusChangeListener {
	public GoogleApiClient mGoogleApiClient;
	private PlaceAutoCompleteAdapter mAdapter;
	private PlaceAutoCompleteHelper.onSuggestResultListener callback;
	public AutoCompleteTextView myact;
	private LatLngBounds mBounds=null;
	private Drawable placeIcon;
	private PlaceAutoCompleteHelper.onTextFocusListener callbacx;
	@Override
	public void onConnectionFailed(ConnectionResult p1) {
		if (callback != null) callback.onSuggestConnectionFailed(p1);
	}
	public interface onSuggestResultListener {
		void onSuggestResult(Place place, AutoCompleteTextView act);
		void onSuggestConnectionFailed(ConnectionResult result);
		void onSuggestFail(Status status);
	}
	public interface onTextFocusListener {
		void onFocus(AutoCompleteTextView act);
	}
	Context ctx;
	public PlaceAutoCompleteHelper(AutoCompleteTextView ... act, FragmentActivity fa) {
		this.ctx = act[0].getContext();
		mGoogleApiClient = new GoogleApiClient.Builder(ctx)
			.enableAutoManage(fa, 0, this)
			.addApi(Places.GEO_DATA_API)
			.addApi(LocationServices.API)
			.build();
        mAdapter = new PlaceAutoCompleteAdapter(ctx, mGoogleApiClient,
												null);
		for (AutoCompleteTextView ac:act) {
			ac.setOnItemClickListener(mAutocompleteClickListener);
			ac.setAdapter(mAdapter);
			ac.setOnFocusChangeListener(this);
		}
		placeIcon = ctx.getResources().getDrawable(R.drawable.ic_map_marker);
		((Activity)ctx).getApplication().registerActivityLifecycleCallbacks(this);
	}
	public void setBound(LatLngBounds b) {
		mBounds = b;
	}
	public PlaceAutoCompleteHelper(AutoCompleteTextView... act) {
		this.ctx = act[0].getContext();
		mGoogleApiClient = new GoogleApiClient.Builder(ctx)
			.addApi(Places.GEO_DATA_API)
			.addApi(LocationServices.API)
			.build();
        mAdapter = new PlaceAutoCompleteAdapter(ctx, mGoogleApiClient,
												null);
        for (AutoCompleteTextView ac:act) {
			ac.setOnItemClickListener(mAutocompleteClickListener);
			ac.setAdapter(mAdapter);
			ac.setOnFocusChangeListener(this);
		}
		((Activity)ctx).getApplication().registerActivityLifecycleCallbacks(this);
	}
	public void setOnSuggestResultListener(onSuggestResultListener x) {
		callback = x;
	}
	public void setOnFocusListener(onTextFocusListener x) {
		callbacx = x;
	}
	@Override
	public void onFocusChange(View p1, boolean p2) {
		if (p2) {
			myact = (AutoCompleteTextView)p1;
			if (callbacx != null) callbacx.onFocus(myact);
		}
 	}
	public void connect() {
		mGoogleApiClient.connect();
	}
	public void disconnect() {
		mGoogleApiClient.disconnect();
	}
	private AdapterView.OnItemClickListener mAutocompleteClickListener
	= new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			final AutocompletePrediction item = mAdapter.getItem(position);
            final String placeId = item.getPlaceId();
			PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
				.getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
		}
    };
	private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
	= new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
				if (callback != null) callback.onSuggestFail(places.getStatus());
				places.release();
                return;
            }
			final Place place = places.get(0);
			if (callback != null) callback.onSuggestResult(place, myact);
            places.release();
        }
    };
 	public class PlaceAutoCompleteAdapter
	extends ArrayAdapter<AutocompletePrediction> implements Filterable {
 		private static final String TAG = "PlaceAutocompleteAdapter";
		private static final CharacterStyle STYLE_BOLD = new StyleSpan(Typeface.BOLD);
 		private ArrayList<AutocompletePrediction> mResultList;
		private GoogleApiClient mGoogleApiClient;
		private AutocompleteFilter mPlaceFilter;
  		public PlaceAutoCompleteAdapter(Context context, GoogleApiClient googleApiClient,
										AutocompleteFilter filter) {
			super(context, R.layout.item_place, R.id.place1);
			mGoogleApiClient = googleApiClient;
			mPlaceFilter = filter;
		}
  		public void setBounds(LatLngBounds bounds) {
			mBounds = bounds;
		}
  		@Override
		public int getCount() {
			return mResultList.size();
		}
  		@Override
		public AutocompletePrediction getItem(int position) {
			return mResultList.get(position);
		}
 		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = super.getView(position, convertView, parent);
			AutocompletePrediction item = getItem(position);
 			TextView textView1 = (TextView) row.findViewById(R.id.place1);
			TextView textView2 = (TextView) row.findViewById(R.id.place2);
			ImageView img=(ImageView) row.findViewById(R.id.itemplaceImageView1);
			textView1.setText(item.getPrimaryText(STYLE_BOLD));
			textView2.setText(item.getFullText(STYLE_BOLD));
			img.setColorFilter(android.graphics.Color.GRAY);
  			return row;
		}
  		@Override
		public Filter getFilter() {
			return new Filter() {
				@Override
				protected FilterResults performFiltering(CharSequence constraint) {
					FilterResults results = new FilterResults();
   					ArrayList<AutocompletePrediction> filterData = new ArrayList<>();
  					if (constraint != null) {
 						filterData = getAutocomplete(constraint);
					}
 					results.values = filterData;
					if (filterData != null) {
						results.count = filterData.size();
					} else {
						results.count = 0;
					}
 					return results;
				}
 				@Override
				protected void publishResults(CharSequence constraint, FilterResults results) {
 					if (results != null && results.count > 0) {
 						mResultList = (ArrayList<AutocompletePrediction>) results.values;
						notifyDataSetChanged();
					} else {
 						notifyDataSetInvalidated();
					}
				}
 				@Override
				public CharSequence convertResultToString(Object resultValue) {
  					if (resultValue instanceof AutocompletePrediction) {
						return ((AutocompletePrediction) resultValue).getFullText(null);
					} else {
						return super.convertResultToString(resultValue);
					}
				}
			};
		}
  		private ArrayList<AutocompletePrediction> getAutocomplete(CharSequence constraint) {
			if (mGoogleApiClient.isConnected()) {
				PendingResult<AutocompletePredictionBuffer> results =
                    Places.GeoDataApi
					.getAutocompletePredictions(mGoogleApiClient, constraint.toString(),
												mBounds, mPlaceFilter);
   				AutocompletePredictionBuffer autocompletePredictions = results
                    .await(60, TimeUnit.SECONDS);
  				final Status status = autocompletePredictions.getStatus();
				if (!status.isSuccess()) {
					if (callback != null) callback.onSuggestFail(status);
					autocompletePredictions.release();
					return null;
				}
 				return DataBufferUtils.freezeAndClose(autocompletePredictions);
			}
 			return null;
		}
	}
 	@Override
	public void onActivityCreated(Activity p1, Bundle p2) {
 	}
 	@Override
	public void onActivityStarted(Activity p1) {
		mGoogleApiClient.connect();
	}
 	@Override
	public void onActivityResumed(Activity p1) {
 	}
 	@Override
	public void onActivityPaused(Activity p1) {
 	}
 	@Override
	public void onActivityStopped(Activity p1) {
		mGoogleApiClient.disconnect();
	}
 	@Override
	public void onActivitySaveInstanceState(Activity p1, Bundle p2) {
 	}
 	@Override
	public void onActivityDestroyed(Activity p1) {
 	}
}

