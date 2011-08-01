/*
 * Zirco Browser for Android
 * 
 * Copyright (C) 2010 - 2011 J. Devauchelle and contributors.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package org.zirco.ui.activities;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.emergent.android.weave.client.WeaveAccountInfo;
import org.zirco.R;
import org.zirco.model.DbAdapter;
import org.zirco.model.WeaveBookmarkItem;
import org.zirco.sync.ISyncListener;
import org.zirco.sync.WeaveSyncTask;
import org.zirco.utils.ApplicationUtils;
import org.zirco.utils.Constants;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class WeaveBookmarksListActivity extends Activity implements ISyncListener {
	
	private static final int MENU_SYNC = Menu.FIRST;
	
	private static final String ROOT_FOLDER = "places";
	
	private TextView mNavigationView;
	private ImageButton mNavigationBack;
	private ListView mListView;
	
	private List<WeaveBookmarkItem> mNavigationList;
	
	private ProgressDialog mSyncProgressDialog;
	
	private DbAdapter mDbAdapter;
	private Cursor mCursor = null;
	
	private WeaveSyncTask mSyncTask;
	
	private static final AtomicReference<AsyncTask<WeaveAccountInfo, Integer, Throwable>> mSyncThread =
	      new AtomicReference<AsyncTask<WeaveAccountInfo, Integer, Throwable>>();
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weave_bookmarks_list_activity);
        
        mNavigationView = (TextView) findViewById(R.id.WeaveBookmarksBreadcumb);
        mNavigationBack = (ImageButton) findViewById(R.id.WeaveBookmarksBack);
        mListView = (ListView) findViewById(R.id.WeaveBookmarksList);
        
        mNavigationBack.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				mNavigationList.remove(mNavigationList.size() - 1);
				if (mNavigationList.size() == 0) {
					mNavigationList.add(new WeaveBookmarkItem(getResources().getString(R.string.WeaveBookmarksListActivity_WeaveRootFolder), null, ROOT_FOLDER, true));
				}
				
				fillData();	
			}
		});
        
        mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
				WeaveBookmarkItem selectedItem = mDbAdapter.getWeaveBookmarkById(id);

				if (selectedItem != null) {
					if (selectedItem.isFolder()) {		
						mNavigationList.add(selectedItem);
						fillData();			
					} else {		
						String url = selectedItem.getUrl();
						
						if (url != null) {				
							Intent result = new Intent();
							result.putExtra(Constants.EXTRA_ID_NEW_TAB, false);
							result.putExtra(Constants.EXTRA_ID_URL, url);

							if (getParent() != null) {
				            	getParent().setResult(RESULT_OK, result);
				            } else {
				            	setResult(RESULT_OK, result);            
				            }
							
							finish();
						}
					}
				}
			}
        });
        
        mNavigationList = new ArrayList<WeaveBookmarkItem>();
        mNavigationList.add(new WeaveBookmarkItem(getResources().getString(R.string.WeaveBookmarksListActivity_WeaveRootFolder), null, ROOT_FOLDER, true));
        
        mDbAdapter = new DbAdapter(this);
        mDbAdapter.open();
        
        fillData();
	}
	
	@Override
	protected void onDestroy() {
		if (mCursor != null) {
			mCursor.close();
		}
		mDbAdapter.close();		
		super.onDestroy();
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
    	
    	MenuItem item = menu.add(0, MENU_SYNC, 0, R.string.WeaveBookmarksListActivity_MenuSync);
    	item.setIcon(R.drawable.ic_menu_sync);
    	
    	return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {

		switch(item.getItemId()) {
		case MENU_SYNC:
			doSync();
			return true;
		default: return super.onMenuItemSelected(featureId, item);
		}
	}
	
	private void doSync() {
		String authToken = ApplicationUtils.getWeaveAuthToken(this);
		
		if (authToken != null) {
			WeaveAccountInfo info = WeaveAccountInfo.createWeaveAccountInfo(authToken);
			mSyncTask = new WeaveSyncTask(this, this);
			
			mSyncProgressDialog = ProgressDialog.show(this,
					getString(R.string.WeaveSync_SyncTitle),
					getString(R.string.WeaveSync_GenericSync),
					false,
					true,
					new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface arg0) {
					mSyncTask.cancel(true);			
				}
				
			});
			
			boolean retVal = mSyncThread.compareAndSet(null, mSyncTask);
			if (retVal) {
				mSyncTask.execute(info);
			}
			
			
		} else {
			ApplicationUtils.showErrorDialog(this, R.string.Errors_WeaveSyncFailedTitle, R.string.Errors_WeaveAuthFailedMessage);
		}
		
	}
	
	private void fillData() {
		
		String[] from = { DbAdapter.WEAVE_BOOKMARKS_TITLE, DbAdapter.WEAVE_BOOKMARKS_URL };
		int[] to = { R.id.BookmarkRow_Title, R.id.BookmarkRow_Url };
		
		mCursor = mDbAdapter.getWeaveBookmarksByParentId(mNavigationList.get(mNavigationList.size() - 1).getWeaveId());
		
		ListAdapter adapter = new SimpleCursorAdapter(this,
				R.layout.weave_bookmark_row,
				mCursor,
				from,
				to);	
		
		mListView.setAdapter(adapter);
		
		mNavigationView.setText(getNavigationText());
		
		if (mNavigationList.size() > 1) {
			mNavigationBack.setEnabled(true);
		} else {
			mNavigationBack.setEnabled(false);
		}
	}
	
	private String getNavigationText() {
		StringBuilder sb = new StringBuilder();
		
		for (WeaveBookmarkItem navigationItem : mNavigationList) {
			if (sb.length() != 0) {
				sb.append(" > ");
			}
			
			sb.append(navigationItem.getTitle());
		}
		
		return sb.toString();
	}

	@Override
	public void onSyncCancelled() {
		mSyncProgressDialog.dismiss();
		fillData();
	}

	@Override
	public void onSyncEnd(Throwable result) {
		mSyncThread.compareAndSet(mSyncTask, null);
		if (result != null) {
			String msg = String.format(getResources().getString(R.string.Errors_WeaveSyncFailedMessage), result.getMessage());
			Log.e("MainActivity: Sync failed.", msg);
			
			ApplicationUtils.showErrorDialog(this, R.string.Errors_WeaveSyncFailedTitle, msg);
		}
		
		mSyncProgressDialog.dismiss();
		fillData();
	}

	@Override
	public void onSyncProgress(int done, int total) {
		mSyncProgressDialog.setMax(total);
		mSyncProgressDialog.setProgress(done);
		mSyncProgressDialog.setMessage(String.format(getResources().getString(R.string.WeaveSync_SyncInProgress), done, total));
	}

}
