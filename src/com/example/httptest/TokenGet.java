package com.example.httptest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TokenGet extends AsyncTask<Void,Void,String>
{
	   public Object parent = null;
	   public String code = null;
	   
	   public TokenGet( Object o, String code)
	   {
		   this.parent = o;
		   this.code = code;
	   }
	   
	   private String streamToString(InputStream is) throws IOException {
		   String str = "";
		   
		   if ( is!=null)
		   {
			   StringBuilder sb = new StringBuilder();
			   String line;
			   
			   try
			   {
				   BufferedReader reader = new BufferedReader(
						   new InputStreamReader(is));
				   
				   while ((line = reader.readLine()) != null )
						   {
					   sb.append(line);
						   }
				   
				   reader.close();
			   }
			   finally
			   {
				   is.close();
			   }
			   
			   str = sb.toString();
		   }
		   
		   return str;
	   }
						 
	   
	   public String doRequest(String code) throws IOException 
	   {

	        StringBuilder builder = new StringBuilder();
   			String reqURL = "https://api.instagram.com/oauth/access_token";
   		
   		/*
   		String postData = 
   				"client_id=f007c59615a5482b9ff03332c3fc1b28" +
   				"&client_secret=4027673378f8497ab43a9e29d3e52543" +
   				"&grant_type=grant_type" +
   				"&redirect_uri=http://www.codetodesign.com" +
   				"&code=" + this.code;
           //webview.loadUrl(reqURL);
           webview.postUrl(reqURL, EncodingUtils.getBytes(postData,"base64"));
           */
   		
           /*
           HttpClient httpclient = new DefaultHttpClient();
           HttpPost httppost = new HttpPost(reqURL);
           
           List<NameValuePair> nvps = new ArrayList<NameValuePair>(4);
           nvps.add( new BasicNameValuePair("client_id", "f007c59615a5482b9ff03332c3fc1b28" ) );
           nvps.add( new BasicNameValuePair("client_secret", "4027673378f8497ab43a9e29d3e52543" ) );
           nvps.add( new BasicNameValuePair("grant_type", "grant_type" ) );
           nvps.add( new BasicNameValuePair("redirect_uri", "http://www.codetodesign.com" ) );
           nvps.add( new BasicNameValuePair("code", this.code) );
           
           try {
			httppost.setEntity( new UrlEncodedFormEntity(nvps) );
           } catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
           }
           
           HttpResponse response = null;
           try {
			response = httpclient.execute(httppost);
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
           */
   			
   			URL url = new URL( reqURL );
   			HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
   			urlConnection.setRequestMethod("POST");
   			urlConnection.setDoInput(true);
   			urlConnection.setDoOutput(true);
   			
   			OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
   			writer.write( "client_id=f007c59615a5482b9ff03332c3fc1b28" +
   	   				"&client_secret=4027673378f8497ab43a9e29d3e52543" +
   	   				"&grant_type=authorization_code" +
   	   				"&redirect_uri=http://www.codetodesign.com" +
   	   				"&code=" + code );
   			writer.flush();
           
   			String response = streamToString( urlConnection.getInputStream());
   			
            return response;
	   }
	   
	   
	public void onPostExecute(Bitmap result)
	{
	
	}

	@Override
	protected String doInBackground(Void... arg0) 
	{
		String response = null;
		
		try {
			response = doRequest(this.code);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return response;
	}

}
