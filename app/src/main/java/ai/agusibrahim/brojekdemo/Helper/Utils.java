package ai.agusibrahim.brojekdemo.Helper;
/*
 Agus Ibrahim
 http://fb.me/mynameisagoes

 */
import com.google.android.gms.maps.model.LatLng;
import android.location.Location;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.Collections;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.CameraUpdateFactory;
import android.graphics.Point;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import ai.agusibrahim.brojekdemo.Widget.*;

public class Utils {
	// teken from https://stackoverflow.com/a/35843019
	public static LatLng getRandLocation(LatLng point, int radius) {

        List<LatLng> randomPoints = new ArrayList<>();
        List<Float> randomDistances = new ArrayList<>();
        Location myLocation = new Location("");
        myLocation.setLatitude(point.latitude);
        myLocation.setLongitude(point.longitude);

        //This is to generate 10 random points
        for (int i = 0; i < 10; i++) {
            double x0 = point.latitude;
            double y0 = point.longitude;

            Random random = new Random();

            // Convert radius from meters to degrees
            double radiusInDegrees = radius / 111000f;

            double u = random.nextDouble();
            double v = random.nextDouble();
            double w = radiusInDegrees * Math.sqrt(u);
            double t = 2 * Math.PI * v;
            double x = w * Math.cos(t);
            double y = w * Math.sin(t);

            // Adjust the x-coordinate for the shrinking of the east-west distances
            double new_x = x / Math.cos(y0);

            double foundLatitude = new_x + x0;
            double foundLongitude = y + y0;
            LatLng randomLatLng = new LatLng(foundLatitude, foundLongitude);
            randomPoints.add(randomLatLng);
            Location l1 = new Location("");
            l1.setLatitude(randomLatLng.latitude);
            l1.setLongitude(randomLatLng.longitude);
            randomDistances.add(l1.distanceTo(myLocation));
        }
        //Get nearest point to the centre
        int indexOfNearestPointToCentre = randomDistances.indexOf(Collections.min(randomDistances));
        return randomPoints.get(indexOfNearestPointToCentre);
    }
	public static double distance(LatLng pos1, LatLng pos2) {
		Location loc1 = new Location("");
		loc1.setLatitude(pos1.latitude);
		loc1.setLongitude(pos1.longitude);

		Location loc2 = new Location("");
		loc2.setLatitude(pos2.latitude);
		loc2.setLongitude(pos2.longitude);

		double distanceInKm = loc1.distanceTo(loc2) * 0.001;
		return distanceInKm;
	}
	// taken from https://stackoverflow.com/a/3694410 (with small modification)
	public static double distance(LatLng pos1, LatLng pos2, char unit) {
		double theta = pos1.longitude - pos2.longitude;
		double dist = Math.sin(deg2rad(pos1.latitude)) * Math.sin(deg2rad(pos2.latitude)) + Math.cos(deg2rad(pos1.latitude)) * Math.cos(deg2rad(pos2.latitude)) * Math.cos(deg2rad(theta));
		dist = Math.acos(dist);
		dist = rad2deg(dist);
		dist = dist * 60 * 1.1515;
		if (unit == 'K') {
			dist = dist * 1.609344;
		} else if (unit == 'N') {
			dist = dist * 0.8684;
        }
		return (dist);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::  This function converts decimal degrees to radians             :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private static double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::  This function converts radians to decimal degrees             :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private static double rad2deg(double rad) {
		return (rad * 180.0 / Math.PI);
    }
	// Camera correction by Agus Ibrahim
	private static void cameraCorrection(final Context ctx, final GoogleMap gmap, final LatLng start, final LatLng end, final int padding) {
		GoogleMap.CancelableCallback cameraOnFinish=new GoogleMap.CancelableCallback(){
			@Override
			public void onFinish() {
				cameraCorrection(ctx, gmap, start, end, padding);
			}

			@Override
			public void onCancel() {
				// TODO: Implement this method
			}
		};
		Point addr_startPoint=gmap.getProjection().toScreenLocation(start);
		Point addr_endPoint = gmap.getProjection().toScreenLocation(end);
		DisplayMetrics dm=ctx.getResources().getDisplayMetrics();
		int maxX=dm.widthPixels;
		if (addr_startPoint.y < padding || addr_endPoint.y < padding) {
			gmap.animateCamera(CameraUpdateFactory.zoomBy(-1f), 1000, cameraOnFinish);
		} else if (addr_startPoint.x < dp2px(ctx, 10) || addr_endPoint.x < dp2px(ctx, 10) || addr_startPoint.x > maxX - dp2px(ctx, 10) || addr_endPoint.x > maxX - dp2px(ctx, 10)) {
			gmap.animateCamera(CameraUpdateFactory.zoomBy(-0.2f), 1000, cameraOnFinish);
		} else if (addr_startPoint.y > (dm.heightPixels - (TariffView.myHeight + dp2px(ctx, 70))) || addr_endPoint.y > (dm.heightPixels - (TariffView.myHeight + dp2px(ctx, 70)))) {
			gmap.animateCamera(CameraUpdateFactory.zoomBy(-0.3f), 1000, cameraOnFinish);
		}
		android.util.Log.d("jos", "batas: " + (dm.heightPixels - (TariffView.myHeight + dp2px(ctx, 10))));
		android.util.Log.d("jos", "marker: " + addr_endPoint.y);
		android.util.Log.d("jos", "70dp: " + dp2px(ctx, 70));
	}
	
	public static void requestCenterCamera(final Context ctx, final GoogleMap gmap, final LatLng start, final LatLng end, final int padding) {
		final LatLngBounds.Builder builder = new LatLngBounds.Builder();
		builder.include(start).include(end);
		gmap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 0), 1000, new GoogleMap.CancelableCallback(){
				@Override
				public void onFinish() {
					cameraCorrection(ctx, gmap, start, end, padding);
				}
				@Override
				public void onCancel() {
					// TODO: Implement this method
				}
			});
	}
	public static float dp2px(Context context, float dipValue) {
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
	}
}
