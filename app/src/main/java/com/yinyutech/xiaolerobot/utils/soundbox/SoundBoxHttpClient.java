package com.yinyutech.xiaolerobot.utils.soundbox;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;

import java.io.UnsupportedEncodingException;

@SuppressWarnings("deprecation")
public class SoundBoxHttpClient {
	private static AsyncHttpClient normalTimeoutClient = new AsyncHttpClient();
	private static AsyncHttpClient shortTimeoutClient = new AsyncHttpClient();

	static {
		SoundBoxHttpClient.normalTimeoutClient.setMaxRetriesAndTimeout(2, 10000);
		//SoundBoxHttpClient.shortTimeoutClient.setMaxRetriesAndTimeout(1, 5000);
		shortTimeoutClient.setConnectTimeout(2000);//outman设置连接超时，以免音响关了后，app迟迟不知道
		shortTimeoutClient.setResponseTimeout(3000);//2s以内要有数据返回，不然认为超时

	}

	public static void get(String urlString, boolean useNormalTimeout, AsyncHttpResponseHandler res) {
		AsyncHttpClient client = useNormalTimeout ? normalTimeoutClient : shortTimeoutClient;
		client.get(urlString, res);
	}

	public static void get(String urlString, boolean useNormalTimeout, RequestParams params,
                           AsyncHttpResponseHandler res) {
		AsyncHttpClient client = useNormalTimeout ? normalTimeoutClient : shortTimeoutClient;
		client.get(urlString, params, res);
	}

	public static void get(String urlString, boolean useNormalTimeout, JsonHttpResponseHandler res) {
		AsyncHttpClient client = useNormalTimeout ? normalTimeoutClient : shortTimeoutClient;
		client.get(urlString, res);
	}

	public static void get(String urlString, boolean useNormalTimeout, RequestParams params,
                           JsonHttpResponseHandler res) {
		AsyncHttpClient client = useNormalTimeout ? normalTimeoutClient : shortTimeoutClient;
		client.get(urlString, params, res);
	}

	public static void get(String uString, boolean useNormalTimeout, BinaryHttpResponseHandler bHandler) {
		AsyncHttpClient client = useNormalTimeout ? normalTimeoutClient : shortTimeoutClient;
		client.get(uString, bHandler);
	}

	public static void post(String urlString, boolean useNormalTimeout, RequestParams params,
                            AsyncHttpResponseHandler res) {
		AsyncHttpClient client = useNormalTimeout ? normalTimeoutClient : shortTimeoutClient;
		client.post(urlString, params, res);
	}

	public static void post(String urlString, boolean useNormalTimeout, AsyncHttpResponseHandler res) {
		AsyncHttpClient client = useNormalTimeout ? normalTimeoutClient : shortTimeoutClient;
		client.post(urlString, res);
	}

	public static void post(Context context, String urlString, String sEntity, boolean useNormalTimeout,
                            AsyncHttpResponseHandler responseHandler) {
		HttpEntity entity = null;
		try {
			entity = new StringEntity(sEntity, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if (entity != null) {
			AsyncHttpClient client = useNormalTimeout ? normalTimeoutClient : shortTimeoutClient;
			client.post(context, urlString, entity, "text/plain",
					responseHandler);
		}
	}
}
