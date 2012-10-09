package com.example.httptest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EncodingUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.webkit.SslErrorHandler;
import android.net.http.SslError;
import org.json.JSONObject;

@SuppressLint("SetJavaScriptEnabled")
public class AuthActivity extends Activity 
{
	public int state = 0;
	public WebView webview = null;
	public String code = null;
	public String errorStr = null;
	String authURL = "https://api.instagram.com/oauth/authorize/?client_id=f007c59615a5482b9ff03332c3fc1b28" +
    		"&redirect_uri=http://www.codetodesign.com&response_type=code";
	public String token = null;
	
	class MyWebViewClient extends WebViewClient
	{
		AuthActivity parent;
	}
	
	class MyJSI
	{
		public String html = null;
		public void getHTML(String html)
		{
    		System.out.println(html);
    		this.html = html;
		}
	}
	
	public MyJSI myjsi = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        this.webview = new WebView(this);  
        WebSettings settings = webview.getSettings();
        settings.setJavaScriptEnabled(true);
        
        //this.myjsi = new MyJSI();        
        
        webview.setVisibility( View.VISIBLE );
        setContentView(webview);
        
        MyWebViewClient client = new MyWebViewClient()
        {
        	@Override
        	public void onPageFinished(WebView view, String url)
        	{
        		System.out.println(url);
        		this.parent.pageFinished(url);
        	};
        	
        	@Override
        	public void onReceivedError(WebView view, int errorCode, String desc, String fUrl)
        	{
        		this.parent.showError(desc);
        	};
        	
        	@Override
        	public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error )
        	{
        		this.parent.showError("SSL ERROR=" + error.toString());
        	};
        };
        client.parent = this;
        
        webview.setWebViewClient( client );
   
        this.Start();
        
    }
    
    public void Start()
    {
    	token = null;
    	code = null;
        state = 0; // make initial request...
        try {
			this.makeRequest();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    
    }
    
    public class MyDialogInterface implements DialogInterface.OnClickListener
    {
    	public AuthActivity parent = null;
		public void onClick(DialogInterface dialog, int which)
		{
			this.parent.Start();
		}
    	
    }
    
    public void showError(String err)
    {
    	this.state = -1;
    	this.errorStr = err;
    	
    	AlertDialog alert = new AlertDialog.Builder( this ).create();
    	
    	alert.setTitle("Error");
    	
    	alert.setMessage("Cannot Authorize");
    	
    	MyDialogInterface intface = new MyDialogInterface() 
    	{	
			public void onClick(DialogInterface dialog, int which) 
			{
				this.parent.Start();
			}
    	};
    	intface.parent = this;
    	
    	alert.setButton("OK", intface );
    	
    	alert.show();
    }
    
    public void pageFinished(String url)
    {
    	//	check for error first...
    	String err = this.extractError(url);
		if ( err!=null ) // check error....
		{			
			this.showError(err);
		};
		
    	if (state==0) // back from auth code request...
    	{

    		System.out.println(url);
    		System.out.println(this.authURL);
    		
    		//    		detect if its a redirect...
    		code = this.extractCode(url);
    		if ( code != null)
    		{
    			state = 2;
    			try {
					this.makeRequest();
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}
    		else
    		{
    			//	its at the instagram landing page...
    			this.state = 1;
    		}
    	}
    	else if (state==1) // user has interacted with the instagram login page likely...
    	{
    		//	try to get the code...
    		code = this.extractCode(url);
    		if ( code == null)
    		{
    			this.showError("Cannot extract code from url.");
    		}
    		else // make access code request...
    		{
    			state = 2;
    			try {
					this.makeRequest();
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}
    	}
    	else if (state==2) 
    	{
    	}
    	else if (state==3)
    	{
    		try {
				this.extractToken(url);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	else
    	{
    		this.showError("Invalid state.");
    	}
    }
    
    public String extractToken(String url) throws JSONException
    { 
    	this.webview.loadUrl("javascript:window.HTMLOUT.getHTML(document.documentElement.innerHTML);");
    	if ( this.myjsi.html == null)
    	{
    		this.showError("Could not extract access token.");
    		return null;
    	}
    	else
    	{
    		int idx1 = this.myjsi.html.indexOf("{");
    		int idx2 = this.myjsi.html.lastIndexOf("}") + 1;
    		String json = this.myjsi.html.substring(idx1, idx2);
    		
    		JSONObject obj = new JSONObject( json );
    		String token = obj.getString("access_token");
    		if (token==null)
    		{
    			this.showError("Cannot get token.");
    			return null;
    		}
    		else
    		{
    			return token;
    		}
    	}
    
    }
   
    public String extractCode(String url)
    {
    	Pattern regex = Pattern.compile(".*code=(.*)");
    	Matcher m = regex.matcher(url);
    	if ( m.find() ) 
    		return m.group(1);
    	else 
    		return null;
    }
    

    public String extractError(String url)
    {
    	Pattern regex = Pattern.compile(".*error=(.*)");
    	Matcher m = regex.matcher(url);
    	if ( m.find() ) return m.group(0);
    	else return null;
    }
    
    public void GotToken(String tokenstr)
    {
    	if ( tokenstr != null )
    	{
    		try 
    		{
				JSONObject jsonObject = new JSONObject(tokenstr);
				this.token = jsonObject.getString("access_token");
				
				Intent intent = new Intent(this, MainActivity.class);
				intent.putExtra("access_token", this.token);
				startActivity(intent);
				
			} catch (JSONException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				this.showError("Could not get access token.");
			}
    	}
    }
    
    public void getToken(String code)
    {
    	new TokenGet(this, code)
    	{
    	@Override public void onPostExecute(String token)
    	{
    		AuthActivity parent = (AuthActivity)this.parent;
    		parent.GotToken(token);
    	}
    	
    	}.execute(null,null,null);
    }
    
    
    public void makeRequest() throws ClientProtocolException, IOException
    {

    	//this.webview.addJavascriptInterface( new MyJSI(), "HTMLOUT"); 
    	
    	if (state==0)
    	{
            webview.loadUrl(this.authURL);
    	}
    	else if (state==2)
    	{
    		this.getToken(this.code);
    	}
    	else
    	{
    		this.showError("Don't know what request to make.");
    	}
    }
}
