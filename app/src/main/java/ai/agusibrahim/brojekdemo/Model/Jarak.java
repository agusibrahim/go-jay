package ai.agusibrahim.brojekdemo.Model;
import org.json.*;

public class Jarak
{
	public String durationText, distanceText;
	public long duration,distance;
	public double distanceInKm;

	public Jarak(String durationText, String distanceText, long duration, long distance) {
		this.durationText = durationText;
		this.distanceText = distanceText;
		this.duration = duration;
		this.distance = distance;
		this.distanceInKm = this.distance * 0.001;
	}
	public Jarak(JSONObject data){
		try {
			JSONObject dur=data.getJSONObject("duration");
			JSONObject dist=data.getJSONObject("distance");
			this.duration=dur.getLong("value");
			this.durationText=dur.getString("text");
			this.distance=dist.getLong("value");
			this.distanceText=dist.getString("text");
			this.distanceInKm=this.distance * 0.001;
		} catch (Exception e) {}
	}
}
