package org.refugerestrooms.android.server;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.refugerestrooms.android.model.Bathroom;
import org.refugerestrooms.android.model.ListOfBathrooms;

import android.location.Address;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;


public class Server {

	protected static final String TAG =  Server.class.getSimpleName();
	private ServerListener mListener;
	
	public Server(ServerListener mListener) {
		super();
		this.mListener = mListener;
	}
	

	@SuppressWarnings("unused")
	public void performSearch(final String searchTerm) {
		// TODO Lark around on the internet
		
		new RemoteCallTask() {
			private String searchTerm;

			private RemoteCallTask setSearchTerm(String searchTerm) {
				this.searchTerm = searchTerm;
				return this;
			}
			
			@Override
			public URI buildUrl() throws URISyntaxException {
<<<<<<< HEAD
                if(!location)
				    return new URI("http://www.refugerestrooms.org:80/api/v1/restrooms/search.json?query=" + Uri.encode(searchTerm));
				else {
                    String[] latLng = searchTerm.split(",");
                    String lat = latLng[0];
                    String lng = latLng[1];
                    Log.d("Captain's log", "lat: " + lat + " lng:" + lng);
                    Log.d("Captain's log", "uri: http://www.refugerestrooms.org:80/api/v1/restrooms/location.json?lat=" + Uri.encode(lat) + "&lng=" + Uri.encode(lng) );
                    return new URI("http://www.refugerestrooms.org:80/api/v1/restrooms/by_location.json?lat=" + Uri.encode(lat) + "&lng=" + Uri.encode(lng) );
                }
=======
				return new URI("http://www.refugerestrooms.org:80/api/v1/restrooms/search.json?query=" + Uri.encode(searchTerm));
>>>>>>> d35d72205c123ef96b8803dd6d041208794dd373
			}
			
			@Override
			protected void onPostExecute(String result) {
				Log.d(TAG, "Result: " + result);
				if (result != null) {
					try {
						Gson gson = new Gson();
						ListOfBathrooms list = gson.fromJson(result, ListOfBathrooms.class);
						
						if (mListener != null) {
							mListener.onSearchResults(list);
						}
					} catch (JsonSyntaxException jse) {
						String msg = "JSON Error: " + jse.getMessage();
						Log.e(TAG, msg);
						reportError(msg);
					}
				} else {
                    Log.d("Captain's log", "onpostexecute");
                    if(mListener != null){
                        mListener.onSearchResults(null);
                    }
				}
			}
			
		}.setSearchTerm(searchTerm).execute();
	}

	protected void reportError(String errorMessage) {
		if (mListener != null) {
			mListener.onError(errorMessage);
		}
	}

	public void submitNewEntry() {
		// TODO Lark around on the internet
		if (mListener != null) {
			mListener.onSubmission(true);
		}
	}
	
	private abstract class RemoteCallTask extends
			AsyncTask<Void, Void, String> {
		
		@Override
		protected String doInBackground(Void... arg0) {
			HttpGet request;
			try {
				request = new HttpGet(buildUrl());
				HttpClient client = new DefaultHttpClient();
				HttpResponse response = client.execute(request);
				int code = response.getStatusLine().getStatusCode();
				
				if (code == HttpStatus.SC_OK) {
					return EntityUtils.toString(response.getEntity());
				} else {
					reportError("Failed with HTTP code " + code);
				}
			} catch (ClientProtocolException e) {
				Log.e(TAG, e.getMessage());
				reportError("ClientProtocolException");
			} catch (IOException e) {
				Log.e(TAG, e.getMessage());
				reportError("IOException");
			} catch (URISyntaxException e1) {
				Log.e(TAG, "Failed to build URL " + e1.getMessage());
			}
			return null;
		}

		public abstract URI buildUrl() throws URISyntaxException;
	}

	public interface ServerListener {
		public void onSearchResults(List<Bathroom> results);
		public void onSubmission(boolean success);
		public void onError(String errorMessage);
	}
	
}
