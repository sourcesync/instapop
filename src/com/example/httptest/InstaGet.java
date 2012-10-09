package com.example.httptest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;

import java.util.Random;

public class InstaGet extends AsyncTask<Void,Void,Bitmap>
{
	   public Object parent = null;
	   public String popstr = null;
	   public String imgurl = null;
	   public Object content = null;
	   public String likes_count = null;
	   public String access_token = null;
	   
	   public InstaGet( Object o, String access_token )
	   {
		   this.parent = o;
		   this.access_token = access_token;
	   }
	   
	   public String readInstagramFeed() 
	   {
	        StringBuilder builder = new StringBuilder();
	        HttpClient client = new DefaultHttpClient();
	        HttpGet httpGet = new HttpGet(
	        		"https://api.instagram.com/v1/media/popular?" + 
	        				"client_id=f007c59615a5482b9ff03332c3fc1b28" + 
	        				"&access_token=" + this.access_token );
	        try {
	          HttpResponse response = client.execute(httpGet);
	          StatusLine statusLine = response.getStatusLine();
	          int statusCode = statusLine.getStatusCode();
	          if (statusCode == 200) {
	            HttpEntity entity = response.getEntity();
	            InputStream content = entity.getContent();
	            BufferedReader reader = new BufferedReader(new InputStreamReader(content));
	            String line;
	            while ((line = reader.readLine()) != null) {
	              builder.append(line);
	            }
	          } else {
	            Log.e(MainActivity.class.toString(), "Failed to download file");
	          }
	        } catch (ClientProtocolException e) {
	          e.printStackTrace();
	        } catch (IOException e) {
	          e.printStackTrace();
	        }
	        return builder.toString();
	   }
	   

	   
	@Override
	protected Bitmap doInBackground(Void... params) 
	{
		this.popstr = readInstagramFeed();
		
		Log.i(MainActivity.class.getName(),this.popstr);
		
		try 
		{
			//	Parse the string as a json object...
			JSONObject jsonObject = new JSONObject(this.popstr);
			
			//	Get the data part...
			JSONArray data = jsonObject.getJSONArray("data");
			
			//	Check there is something...
			if ( data.length()==0) return null;
			
			//	Choose a random number within the length...
			Random generator = new Random( 45456 );		
			double r = generator.nextDouble();		
			int idx = (int)Math.floor( r* data.length() );
			
			//
			//	Get the idx'th image in the popular list...
			//
			{
		        JSONObject data_item = data.getJSONObject(idx);	        
		        JSONObject images = data_item.getJSONObject("images");	        
		        JSONObject sr = images.getJSONObject("standard_resolution");		        
		        String url = sr.getString("url");		        
		        this.imgurl = url;
		        
		        //	get the number of likes...
		        JSONObject likes = data_item.getJSONObject("likes");
		        String count = likes.getString("count");
		        this.likes_count = count;
		        
		        //	Download the image into a Bitmap object...
			    HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
			    connection.connect();
			    InputStream input = connection.getInputStream();
			    Bitmap x = BitmapFactory.decodeStream(input);
			    
			    
			    return x;        
			}
			
		    } catch (Exception e) {
		      e.printStackTrace();
		    }
		return null;
	}
	
	public void onPostExecute(Bitmap result)
	{
	
	}

}
