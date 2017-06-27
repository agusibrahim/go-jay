package ai.agusibrahim.brojekdemo.Helper;
/*
 Agus Ibrahim
 http://fb.me/mynameisagoes

 */
import com.google.android.gms.maps.model.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import ai.agusibrahim.brojekdemo.Helper.DirectionDrawHelper.*;
import com.google.android.gms.maps.*;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.graphics.Point;
import ai.agusibrahim.brojekdemo.Model.*;

public class DirectionDrawHelper {
	GoogleMap map;
	private DirectionDrawHelper.DownloadTask downloadTask;
	private String url;
	private LatLng addr_start, addr_end;
	public static Marker add_startMarker,add_endMarker;
	Context ctx;
	public static MapAnimator anim;
	public static Polyline pathline;
	Point addr_startPoint, addr_endPoint;
	private VMargin vmargin;
	private DirectionDrawHelper.OnNavigateReadyListener callback;
	public interface OnNavigateReadyListener {
		void onNavigationReady(Polyline path, Jarak jarak);
		void onNavigationFailed();
	}
	public DirectionDrawHelper(Context ctx, GoogleMap map, LatLng start, LatLng end, VMargin v) {
		this.map = map;
		addr_start = start;
		addr_end = end;
		this.ctx = ctx;
		vmargin=v;
		url = getDirectionsUrl(start, end);
		android.util.Log.d("lok", "dest:" + end + "---start:" + start);
 	}
	public void start() {
		downloadTask = new DownloadTask();
		downloadTask.execute(url);
	}
	public static void clearNavigate() {
		add_endMarker.remove();
		add_startMarker.remove();
		if(anim!=null) anim.clearPolyline();
		anim=null;
	}
	public void setOnNavigateReadyListener(OnNavigateReadyListener x) {
		callback = x;
	}
	private String getDirectionsUrl(LatLng origin, LatLng dest) {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String sensor = "sensor=false";
        String parameters = str_origin + "&" + str_dest + "&" + sensor;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        return url;
    }
	private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            br.close();
        } catch (Exception e) {
            Log.d("Exception while downloading url", e.toString());
        }
		finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
	private class DownloadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                data = downloadUrl(url[0]);
				android.util.Log.d("lok0", "res:" + url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }
        @Override
        protected void onPostExecute(String result) {
			if (result.length() < 5) {
				if (callback != null)callback.onNavigationFailed();
				return;
			}
            super.onPostExecute(result);
            ParserTask parserTask = new ParserTask();
			parserTask.execute(result);
			android.util.Log.d("lok", "res:" + result);
        }
    }
	private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> > {
 		public Jarak jarak;
		ArrayList<LatLng> points = null;
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();
				routes = parser.parse(jObject);
				jarak = parser.jarak;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }
		@Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            
            PolylineOptions lineOptions = null;
  			android.util.Log.d("lok", "res2:" + result);
            for (int i=0;i < result.size();i++) {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();
				List<HashMap<String, String>> path = result.get(i);
				for (int j=0;j < path.size();j++) {
                    HashMap<String,String> point = path.get(j);
					double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
					points.add(position);
                }
				lineOptions.addAll(points);
                lineOptions.width(7);
                lineOptions.color(Color.RED);
				anim=MapAnimator.getInstance();
 				Utils.requestCenterCamera(ctx, map, addr_start, addr_end, vmargin, new Utils.OnCameraComplete(){
						@Override
						public void onComplete() {
							anim.animateRoute(map, points);
						}
					});
            }
 			add_startMarker = map.addMarker(new MarkerOptions()
											.position(addr_start)
											.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_start_marker)));
			add_endMarker = map.addMarker(new MarkerOptions()
										  .position(addr_end)
										  .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_end_marker)));
			//pathline = map.addPolyline(lineOptions);
			//pathline.setVisible(false);
			
			
			if (callback != null)callback.onNavigationReady(pathline, jarak);
        }
    }
	
	
 	public class DirectionsJSONParser {
		public Jarak jarak=null;
 		public List<List<HashMap<String,String>>> parse(JSONObject jObject) {
 			List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String,String>>>() ;
			JSONArray jRoutes = null;
			JSONArray jLegs = null;
			JSONArray jSteps = null;
			try {
 				jRoutes = jObject.getJSONArray("routes");
  				for (int i=0;i < jRoutes.length();i++) {
					jLegs = ((JSONObject)jRoutes.get(i)).getJSONArray("legs");
					List path = new ArrayList<HashMap<String, String>>();
  					for (int j=0;j < jLegs.length();j++) {
						JSONObject leg=((JSONObject)jLegs.get(j));
						jarak = new Jarak(leg);
						jSteps = leg.getJSONArray("steps");
  						for (int k=0;k < jSteps.length();k++) {
							String polyline = "";
							polyline = (String)((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
							List<LatLng> list = decodePoly(polyline);
  							for (int l=0;l < list.size();l++) {
								HashMap<String, String> hm = new HashMap<String, String>();
								hm.put("lat", Double.toString(((LatLng)list.get(l)).latitude));
								hm.put("lng", Double.toString(((LatLng)list.get(l)).longitude));
								path.add(hm);
							}
						}
						routes.add(path);
					}
				}
 			} catch (JSONException e) {
				e.printStackTrace();
			} catch (Exception e) {
			}
			return routes;
		}
 		private List<LatLng> decodePoly(String encoded) {
			List<LatLng> poly = new ArrayList<LatLng>();
			int index = 0, len = encoded.length();
			int lat = 0, lng = 0;
 			while (index < len) {
				int b, shift = 0, result = 0;
				do {
					b = encoded.charAt(index++) - 63;
					result |= (b & 0x1f) << shift;
					shift += 5;
				} while (b >= 0x20);
				int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
				lat += dlat;
 				shift = 0;
				result = 0;
				do {
					b = encoded.charAt(index++) - 63;
					result |= (b & 0x1f) << shift;
					shift += 5;
				} while (b >= 0x20);
				int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
				lng += dlng;
 				LatLng p = new LatLng((((double) lat / 1E5)),
									  (((double) lng / 1E5)));
				poly.add(p);
			}
 			return poly;
		}
	}
}

