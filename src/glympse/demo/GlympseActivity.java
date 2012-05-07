package glympse.demo;

import glympse.demo.GoogleWeatherClient.Forecast;
import glympse.demo.GoogleWeatherClient.WeatherResult;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MapView.ReticleDrawMode;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class GlympseActivity extends MapActivity {
	public static final String prodMapsApiKey = "0MiKPRrzSyn-hQakMXx5SdiPxpnAZjQtwsK_mtw";
	public static final String prodKeyMd5 = "64:7C:06:84:5A:B5:8D:12:D7:7B:ED:CF:72:D4:8E:6A";

	public static final String devMapsApiKey = "0MiKPRrzSyn8lirqUWj-9vbqc0HU4N_47w1sivA";
	public static final String devKeyMd5 = "5D:83:40:9E:96:B7:61:36:C9:A4:FC:5E:F0:5C:EA:F6";

	public static GlympseActivity current;
	
	MapView mapView;
	Button button_centerMap;
	Button button_updateWeather;
	Button button_zoomOut;
	Button button_zoomIn;
	AsyncImageView icon_d0;
	AsyncImageView icon_d1;
	AsyncImageView icon_d2;
	AsyncImageView icon_d3;
	AsyncImageView icon_d4;
	

	GeoPoint lastLocation;

	public static final String logTag = "glympse";
	private static void log_error(String msg) {
		Log.e(logTag, msg);
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		current = this;
		
		setContentView(R.layout.main);
		mapView = (MapView)this.findViewById(R.id.mapView);
		button_centerMap = (Button)this.findViewById(R.id.button_centerMap);
		button_updateWeather = (Button)this.findViewById(R.id.button_updateWeather);
		button_zoomOut = (Button)this.findViewById(R.id.button_zoomOut);
		button_zoomIn = (Button)this.findViewById(R.id.button_zoomIn);
		
		icon_d0 = (AsyncImageView)this.findViewById(R.id.icon_d0);
		icon_d1 = (AsyncImageView)this.findViewById(R.id.icon_d1);
		icon_d2 = (AsyncImageView)this.findViewById(R.id.icon_d2);
		icon_d3 = (AsyncImageView)this.findViewById(R.id.icon_d3);
		icon_d4 = (AsyncImageView)this.findViewById(R.id.icon_d4);


		button_centerMap.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				centerMap();
			}
		});

		button_updateWeather.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				updateWeather();
			}
		});
		
		button_zoomOut.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				zoomOut();
			}
		});
		
		button_zoomIn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				zoomIn();
			}
		});
	}

	private void centerMap () {
		Log.i(logTag, "centerMap()");
		final LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		final Timer timer = new Timer();
		lastLocation = null;
		final LocationListener locationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				if(lastLocation == null || lastLocation.equals(mapView.getMapCenter())) {
					// Called when a new location is found by the network location provider.
					System.out.println("Location is: :" + location.toString());
					mapView.setEnabled(true);
					GeoPoint gp = new GeoPoint((int)(1000000 * location.getLatitude()), (int)(1000000 * location.getLongitude()));

					mapView.setReticleDrawMode(ReticleDrawMode.DRAW_RETICLE_OVER);
					mapView.getController().setZoom(16);
					mapView.getController().animateTo(gp);
					if(lastLocation == null) {
						updateWeather();
					}
					lastLocation = gp;
				} else {
					locationManager.removeUpdates(this);
				}
			}

			public void onStatusChanged(String provider, int status, Bundle extras) {}

			public void onProviderEnabled(String provider) {}

			public void onProviderDisabled(String provider) {}
		};

		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				locationManager.removeUpdates(locationListener);
			}
		}, 5000);

		// Register the listener with the Location Manager to receive location updates
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
		//		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
		locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, locationListener);
	}

	private void updateWeather() {
		Log.i(logTag, "updateWeather()");

		final GeoPoint center = mapView.getMapCenter();

		new Thread(new Runnable() {
			public void run() {
				final WeatherResult weather = GoogleWeatherClient.getWeather(center);
				icon_d0.setUrl(weather.icon);
				AsyncImageView[] views = new AsyncImageView[] {
						icon_d0,
						icon_d1,
						icon_d2,
						icon_d3,
						icon_d4,
				};
				int i = 1;
				for(Forecast forecast : weather.forecast) {
					views[i++].setUrl(forecast.icon);
				}
			}
		}).start();
	}
	
	private void zoomOut() {
		mapView.getController().zoomOut();
	}
	
	private void zoomIn() {
		mapView.getController().zoomIn();
	}

	//	String getZipFromLocation(Location location){
	//		// TODO: does this work outside the US?
	//		Geocoder geocoder = new Geocoder(this, Locale.ENGLISH);
	//
	//		List<Address> addresses = null;
	//		try {
	//		    addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 3);
	//		} catch (IOException e) {
	//			if(e.getMessage().contains("Service not Available")) {				
	//				Log.e(logTag, "getZipFromLocation - Service not Available");
	//			} else {
	//				Log.e(logTag, "getZipFromLocation - GeoCoder hit exception: ", e);
	//			}
	//			return null;
	//		}
	//
	//		for (int i = 0; i < addresses.size(); i++) {
	//		    Address address = addresses.get(i);
	//		    if (address.getPostalCode() != null) {
	//		    	Log.i(logTag, "ZipCode = " + address.getPostalCode());
	//		    	return address.getPostalCode();
	//		    }
	//		}
	//		return null;
	//	}

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

