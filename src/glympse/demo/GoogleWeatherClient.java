package glympse.demo;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.util.Log;

import com.google.android.maps.GeoPoint;

public class GoogleWeatherClient {
	public static final String logTag = "glympse-gw";
	public static final String weatherUrlFormat = "http://www.google.com/ig/api?weather=,,,%d,%d";
	
	public static final URL googleBaseUrl;
	static {
		try {
			googleBaseUrl = new URL("http://g0.gstatic.com");
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static final String CURRENT_CONDITIONS = "current_conditions";
	public static final String FORECAST_CONDITIONS = "forecast_conditions";
	public static final String CONDITION = "condition";
	public static final String TEMP_F = "temp_f";
	public static final String TEMP_C = "temp_c";
	public static final String LOW = "low";
	public static final String HIGH = "high";
	public static final String HUMIDITY = "humidity";
	public static final String ICON = "icon";
	public static final String WIND_CONDITION = "wind_condition";

	public static final String DATA = "data";
	
	public static final String[] commonImages = new String[] {
		"http://g0.gstatic.com/ig/images/weather/sunny.png",
		"http://g0.gstatic.com/ig/images/weather/cloudy.png",
		"http://g0.gstatic.com/ig/images/weather/chance_of_rain.png"
	};

	public static class WeatherResult {
		int temp_f;
		int temp_c;
		String condition;
		String humidity;
		String wind;
		URL icon;
		List<Forecast> forecast = new ArrayList<Forecast>();
		@Override
		public String toString() {
			return String.format("[GoogleWeather: {temp=%dF/%dC, condition='%s', humidity='%s', wind='%s', icon='%s'}]", temp_f, temp_c, condition, humidity, wind, icon);
		}
	}

	public static class Forecast {
		String day_of_week;
		int low;
		int high;
		URL icon;
		String condition;
		public String toString() {
			return String.format("[Forecast: {day=%s, low=%d, high=%d, condition='%s' icon='%s'}]", day_of_week, low, high, condition, icon);
		}
	}

	public static class ElementNodeListIterator implements Iterable<Element>, Iterator<Element> {
		NodeList nodes;
		int i, l;

		public ElementNodeListIterator(NodeList nodes) {
			this.nodes = nodes;
			this.i = 0;
			this.l = nodes.getLength();
		}

		public static ElementNodeListIterator from(NodeList nodes) {
			return new ElementNodeListIterator(nodes);
		}

		public boolean hasNext() {
			return i < l;
		}

		public Element next() {
			return (Element)nodes.item(i++);
		}

		public void remove() {
		}

		public Iterator<Element> iterator() {
			return new ElementNodeListIterator(nodes);
		}
	}

	private static ElementNodeListIterator _iter(NodeList nodes) {
		return new ElementNodeListIterator(nodes);
	}

	public static WeatherResult getWeather(GeoPoint gp) {
		URL url;
		InputStream inputStream;
		try {
			url = new URL(String.format(weatherUrlFormat, gp.getLatitudeE6(), gp.getLongitudeE6()));
			inputStream = url.openStream();
		} catch (MalformedURLException e) {
			Log.e(logTag, "Failed connecting to Google Weather: " + e.getMessage());
			return null;
		} catch (IOException e) {
			Log.e(logTag, "Failed connecting to Google Weather: " + e.getMessage());
			return null;
		}

		DocumentBuilder domBuilder;
		try {
			domBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			Log.e(logTag, "Failed to get a DOM Parser: " + e.getMessage());
			return null;
		}

		Document dom;
		try {
			dom = domBuilder.parse(inputStream);
		} catch (SAXException e) {
			Log.e(logTag, "Failed parsing Google Weather response: " + e.getMessage());
			return null;
		} catch (IOException e) {
			Log.e(logTag, "Failed reading Google Weather response: " + e.getMessage());
			return null;
		}

		WeatherResult weather = new WeatherResult();
		for(Element node : _iter(dom.getElementsByTagName(CURRENT_CONDITIONS))) {
			for(Element child : _iter(node.getChildNodes())) {
				String tag = child.getTagName();
				String data = child.getAttribute(DATA);
				if(tag.equals(CONDITION)) {
					weather.condition = data;
				} else if (tag.equals(TEMP_F)) {
					weather.temp_f = Integer.parseInt(data);
				} else if (tag.equals(TEMP_C)) {
					weather.temp_c = Integer.parseInt(data);
				} else if (tag.equals(HUMIDITY)) {
					weather.humidity = data;
				} else if (tag.equals(WIND_CONDITION)) {
					weather.wind = data;
				} else if (tag.equals(ICON)) {
					try {
						weather.icon = new URL(googleBaseUrl, data);
					} catch (MalformedURLException e) {
						Log.e(logTag, "Failed making URL from 'icon' field with MalformedURLException.  icon=" + data);
					}
				}
			}
		}
		for(Element node : _iter(dom.getElementsByTagName(FORECAST_CONDITIONS))) {
			Forecast forecast = new Forecast();
			for(Element child : _iter(node.getChildNodes())) {
				String tag = child.getTagName();
				String data = child.getAttribute(DATA);
				if(tag.equals(CONDITION)) {
					forecast.condition = data;
				} else if (tag.equals(LOW)) {
					forecast.low = Integer.parseInt(data);
				} else if (tag.equals(HIGH)) {
					forecast.high = Integer.parseInt(data);
				} else if (tag.equals(ICON)) {
					try {
						forecast.icon = new URL(googleBaseUrl, data);
					} catch (MalformedURLException e) {
						Log.e(logTag, "Failed making URL from 'icon' field with MalformedURLException.  icon=" + data);
					}
				}
			}
			weather.forecast.add(forecast);
		}
		Log.i(logTag, "Weather: " + weather);
		return weather;
	}
}
