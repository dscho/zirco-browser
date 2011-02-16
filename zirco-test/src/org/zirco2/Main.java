package org.zirco2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnTouchListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

public class Main extends Activity implements OnTouchListener {
	
	public static int ACTIVITY_SHOW_TABS = 0;
	
	private GestureDetector mGestureDetector;
	private FrameLayout mWebViewContainer;
	private LayoutInflater mInflater = null;
	
	private int mCurrentViewIndex = -1;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.main);
        
        mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        mGestureDetector = new GestureDetector(this, new GestureListener());
        
        mWebViewContainer = (FrameLayout) findViewById(R.id.WebWiewContainer);
        
        
        addTab("http://fr.m.wikipedia.org/");
        addTab("http://www.google.com/");
        
        /*
        WebView webView = (WebView) findViewById(R.id.webview);
        
        TabsController.getInstance().addWebView(webView);
        
        webView.loadUrl("http://fr.m.wikipedia.org/");
        
        webView.setLongClickable(true);
        
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient());
        
        webView.setOnTouchListener(this);        
        
        webView.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				Toast.makeText(Main.this, "OnLongClickListenerOnLink", Toast.LENGTH_SHORT).show();
				return true;
			}
		});
		*/		
    }

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return mGestureDetector.onTouchEvent(event);
	}
	
	private void addTab(String url) {
		RelativeLayout view = (RelativeLayout) mInflater.inflate(R.layout.webview, mWebViewContainer, false);
		
		WebView webView = (WebView) view.findViewById(R.id.webview);
		
		mCurrentViewIndex = TabsController.getInstance().addWebViewContainer(new WebViewContainer(view, webView));
		
		webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient());        
        webView.setOnTouchListener(this);
        
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadsImagesAutomatically(true);
        
        webView.loadUrl(url);
        
        mWebViewContainer.addView(view);
        showTab(mCurrentViewIndex);
	}
	
	private void showTab(int tabIndex) {
		View view = TabsController.getInstance().getWebViews().get(tabIndex).getView();
		mWebViewContainer.bringChildToFront(view);
		view.requestFocus();
		mCurrentViewIndex = tabIndex;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if ((requestCode == ACTIVITY_SHOW_TABS) &&
				(resultCode == RESULT_OK)) {
			if (data != null) {
        		Bundle b = data.getExtras();
        		if (b != null) {
        			int position = b.getInt("TAB_INDEX");
        			showTab(position);        			
        		}
			}
		}
	}
	
	private class GestureListener extends GestureDetector.SimpleOnGestureListener {
		@Override
		public void onLongPress(MotionEvent e) {
			Intent i = new Intent(Main.this, GalleryActivity.class);
			i.putExtra("CURRENT_VIEW_INDEX", mCurrentViewIndex);
			
			Main.this.startActivityForResult(i, Main.ACTIVITY_SHOW_TABS);
			Main.this.overridePendingTransition(R.anim.tab_view_enter, R.anim.browser_view_exit);
		}
	}
	
}