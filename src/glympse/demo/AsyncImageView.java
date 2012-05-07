package glympse.demo;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

public class AsyncImageView extends ImageView {
	public static final String logTag = "glympse-il";
	private static HashMap<URL, Bitmap> cache = new HashMap<URL, Bitmap>();

	public AsyncImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	public AsyncImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public AsyncImageView(Context context) {
		super(context);
	}

	public void setUrl(final URL url) {
		if(cache.containsKey(url)) {
			final Bitmap bitmap = cache.get(url);
			GlympseActivity.current.runOnUiThread(new Runnable() {
				public void run() {
					AsyncImageView.this.setImageBitmap(bitmap);
				}
			});
		} else {
			new Thread(new Runnable() {
				public void run() {
					InputStream inputStream;
					try {
						inputStream = url.openStream();
						final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
						cache.put(url, bitmap);
						GlympseActivity.current.runOnUiThread(new Runnable() {
							public void run() {
								AsyncImageView.this.setImageBitmap(bitmap);
							}
						});
					} catch (IOException e) {
						Log.e(logTag, "Failed connecting to icon url: " + url);
						return;
					}
				}
			}).start();
		}
	}
}
