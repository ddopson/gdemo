package glympse.demo;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MapView.ReticleDrawMode;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

public class GlympseActivity extends MapActivity {
	public static final String prodMapsApiKey = "0MiKPRrzSyn-hQakMXx5SdiPxpnAZjQtwsK_mtw";
	public static final String prodKeyMd5 = "64:7C:06:84:5A:B5:8D:12:D7:7B:ED:CF:72:D4:8E:6A";
	
	public static final String devMapsApiKey = "0MiKPRrzSyn8lirqUWj-9vbqc0HU4N_47w1sivA";
	public static final String devKeyMd5 = "5D:83:40:9E:96:B7:61:36:C9:A4:FC:5E:F0:5C:EA:F6";
	
	MapView mapView;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		findLocation();
		setContentView(R.layout.main);
		mapView = (MapView)this.findViewById(R.id.mapView);
	}

	private void findLocation () {
		LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

		// Define a listener that responds to location updates
		System.out.println("Registering location listener");
		Log.i("ddopson", "Registering location listener");
		LocationListener locationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				// Called when a new location is found by the network location provider.
				System.out.println("Location is: :" + location.toString());
				mapView.setEnabled(true);
				GeoPoint gp = new GeoPoint((int)(1000000 * location.getLatitude()), (int)(1000000 * location.getLongitude()));
				Runnable cb = new Runnable() {
					public void run() {
						System.out.println("Map Animation Done.");
					}
				};
				mapView.setReticleDrawMode(ReticleDrawMode.DRAW_RETICLE_OVER);
				mapView.getController().setZoom(15);
				mapView.getController().animateTo(gp, cb);
			}

			public void onStatusChanged(String provider, int status, Bundle extras) {}

			public void onProviderEnabled(String provider) {}

			public void onProviderDisabled(String provider) {}
		};

		// Register the listener with the Location Manager to receive location updates
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
//		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
		locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, locationListener);
	}
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		System.out.println("onSaveInstanceState");
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		System.out.println("onRestoreInstanceState");
		super.onRestoreInstanceState(savedInstanceState);
		mapView.onRestoreInstanceState(savedInstanceState);
	}
	
	public void foo() {
		System.out.println("Button Clicked");
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
}