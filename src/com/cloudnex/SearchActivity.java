package com.cloudnex;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi.MetadataBufferResult;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;

public class SearchActivity extends ActionBarActivity implements
		ConnectionCallbacks, OnConnectionFailedListener {

	private static AutoCompleteTextView autoCompleteTextView;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		View decorView = getWindow().getDecorView();
		// Hide the status bar.
		int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
		decorView.setSystemUiVisibility(uiOptions);
		// Remember that you should never show the action bar if the
		// status bar is hidden, so hide that too if necessary.
		android.app.ActionBar actionBar = getActionBar();
		actionBar.hide();

		// Set Layout
		setContentView(R.layout.activity_search);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}

		// Set Buttons
		ImageButton imageButtonSearch = (ImageButton) this
				.findViewById(R.id.imageButtonSearch);
		ImageButton imageButtonAPlus = (ImageButton) this
				.findViewById(R.id.imageButtonAPlus);
		ImageButton imageButtonFind = (ImageButton) this
				.findViewById(R.id.imageButtonFind);
		ImageButton imageButtonAdd = (ImageButton) this
				.findViewById(R.id.imageButtonAdd);
		autoCompleteTextView = (AutoCompleteTextView) this.findViewById(R.id.autoCompleteTextView);
		// Set Buttons Attributes
		imageButtonSearch.setEnabled(false);
		imageButtonSearch.setVisibility(View.INVISIBLE);
		// Set On-Click Listeners
		imageButtonAPlus.setOnClickListener(new TrendingListner());
		imageButtonFind.setOnClickListener(new FindListner());
		imageButtonAdd.setOnClickListener(new AddAccountListner());
	}

	// On-Click Listener For The Continue Button
	private class FindListner implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			String searchQuery = autoCompleteTextView.getText().toString();
			if (searchQuery.trim().equals("")){
				Toast.makeText(SearchActivity.this, "There is no text to search.", Toast.LENGTH_SHORT)
				.show();
			} else {
				//TODO googleQuery(MainActivity.mGoogleApiClient, searchQuery);
				DropboxSearch searchDropBox = new DropboxSearch(SearchActivity.this,
						MainActivity.mApi, searchQuery, null);
				searchDropBox.execute();
				try {
					searchDropBox.get(10, TimeUnit.SECONDS);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				} catch (TimeoutException e) {
					e.printStackTrace();
				}
				// Open Results
				Intent resultsIntent = new Intent(SearchActivity.this,
						ResultsActivity.class);
				SearchActivity.this.startActivity(resultsIntent);
				SearchActivity.this.finish();
			}
		}
		
		private void googleQuery(GoogleApiClient mGoogleApiClient,
				String searchQuery) {
	    	Query query = new Query.Builder()
	        .addFilter(Filters.eq(SearchableField.TITLE, "Android Photo.png")).build() ;
	    	Drive.DriveApi.query(mGoogleApiClient, query);
	    	
	    	ResultCallback<MetadataBufferResult> contentsOpenedCallback =
	    	        new ResultCallback<MetadataBufferResult>() {


				@Override
				public void onResult(MetadataBufferResult result) {
					// TODO Auto-generated method stub
					result.getMetadataBuffer().get(1);
						if (result.getMetadataBuffer().getNextPageToken() != null) {
							
						}
				}
	    	};
	    	
	    	Drive.DriveApi.query(mGoogleApiClient, query).setResultCallback(contentsOpenedCallback);
		}
	}

	// On-Click Listener For The Continue Button
	private class ButtonListner implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			Toast.makeText(SearchActivity.this, "Click!", Toast.LENGTH_SHORT)
					.show();
		}
	}

	// On-Click Listener For The Trending Button
	private class TrendingListner implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			// Open Results
			Intent trendingIntent = new Intent(SearchActivity.this,
					TrendingActivity.class);
			SearchActivity.this.startActivity(trendingIntent);
			SearchActivity.this.finish();

		}
	}
	// On-Click Listener For The Continue Button
	private class AddAccountListner implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			/* Create an Intent that will start the Search-Activity. */
			Intent mainIntent = new Intent(SearchActivity.this,
					MainActivity.class);
			SearchActivity.this.startActivity(mainIntent);
			SearchActivity.this.finish();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_search,
					container, false);
			return rootView;
		}
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		
	}

	@Override
	public void onConnectionSuspended(int cause) {

	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {

	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onActivityResult(final int requestCode,
			final int resultCode, final Intent data) {

	}
}
