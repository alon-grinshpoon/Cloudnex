package com.cloudnex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class TrendingActivity extends ActionBarActivity implements
		ConnectionCallbacks, OnConnectionFailedListener {

	public static ArrayList<FileItem> items = new ArrayList<FileItem>();

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
		setContentView(R.layout.activity_results);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}

		// Set Buttons
		ImageButton imageButtonSearch = (ImageButton) this
				.findViewById(R.id.imageButtonSearch);
		ImageButton imageButtonAPlus = (ImageButton) this
				.findViewById(R.id.imageButtonAPlus);
		// Set Buttons Attributes
		imageButtonAPlus.setEnabled(false);
		imageButtonAPlus.setVisibility(View.INVISIBLE);
		// Set On-Click Listeners
		imageButtonAPlus.setOnClickListener(new ButtonListner());
		imageButtonSearch.setOnClickListener(new SearchListner());

		// Set List
		ListView listView = (ListView) this.findViewById(R.id.listView);
		for(int i = 0; i < 10; i++){
			int rRank = 60 + (int) (Math.random() * ((130 - 60) + 1));
			items.add(new FileItem("CldFile"+i, "", "", String.valueOf(rRank), "", ""));			
		}
		Collections.sort(items, new rankComparator());
		listView.setAdapter(new FilesListAdapter(this, items));
	}

	public class rankComparator implements Comparator<FileItem> {

		public int compare(FileItem item1, FileItem item2) {
			return (Integer.valueOf(item1.mRank) > Integer.valueOf(item2.mRank)) ? -1
					: 1;
		}
	}

	private class FilesListAdapter extends ArrayAdapter<FileItem> {
		private final Context context;
		private final ArrayList<FileItem> items;

		public FilesListAdapter(Context context, ArrayList<FileItem> items) {
			super(context, R.layout.list_item, items);
			this.context = context;
			this.items = items;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			View rowView = inflater.inflate(R.layout.list_item, parent, false);
			TextView filename = (TextView) rowView.findViewById(R.id.filename);
			TextView rank = (TextView) rowView.findViewById(R.id.rank);
			ImageView source = (ImageView) rowView.findViewById(R.id.source);
			ImageButton shareType = (ImageButton) rowView
					.findViewById(R.id.share_type);

			// Set Text Color
			rank.setTextColor(Color.parseColor("#2e8b57"));

			// Change icons based on attributes
			filename.setText(items.get(position).mFileName);
			int rSource = 1 + (int) (Math.random() * ((10 - 1) + 1));
			if (rSource > 5) {
				source.setImageResource(R.drawable.button_small_dropbox);
			} else {
				source.setImageResource(R.drawable.button_small_googledrive);
			}
			int rType = 1 + (int) (Math.random() * ((12 - 1) + 1));
			if (rType > 9) {
				shareType.setImageResource(R.drawable.share_word);
			} else if (rType > 6) {
				shareType.setImageResource(R.drawable.share_powerpoint);
			} else {
				shareType.setImageResource(R.drawable.share_pdf);
			}
			rank.setText("Got " + items.get(position).mRank + " A+'s!");
			return rowView;
		}
	}

	// On-Click Listener For The Continue Button
	private class ButtonListner implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			Toast.makeText(TrendingActivity.this, "Click!", Toast.LENGTH_SHORT)
					.show();
		}
	}

	private class SearchListner implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			// Open Results
			Intent searchIntent = new Intent(TrendingActivity.this,
					SearchActivity.class);
			TrendingActivity.this.startActivity(searchIntent);
			TrendingActivity.this.finish();

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
			View rootView = inflater.inflate(R.layout.fragment_results,
					container, false);
			return rootView;
		}
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnectionSuspended(int cause) {
		// TODO Auto-generated method stub

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
