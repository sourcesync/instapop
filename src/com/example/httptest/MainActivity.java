package com.example.httptest;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        GetIt();
        
    }
    
    public void Next()
    {
    	this.GetIt();
    }
    
    public void GetIt()
    {
    	Intent intent = getIntent();
    	String token = intent.getStringExtra("access_token");
    	
    	new InstaGet(this, token)
    	{
    	@Override public void onPostExecute(Bitmap result)
    	{
    		if (result!=null)
    		{
    			Drawable d = new BitmapDrawable(getResources(),result); 			 
    			ImageView iv = (ImageView) findViewById( R.id.imageView1);
    			iv.setImageDrawable(d);
    			
    			//TextView tv = (TextView) findViewById( R.id.tview1);
    			//tv.setText( "Got " + this.likes_count + " Likes! ");
    		}
    		
    		MainActivity parent = (MainActivity)this.parent;
    		parent.Next();
    	}
    	
    	}.execute(null,null,null);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
}
