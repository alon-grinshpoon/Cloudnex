package com.cloudnex;

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
import android.widget.ImageButton;
import android.widget.Toast;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.android.AuthActivity;
import com.dropbox.client2.session.*;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;

import android.content.IntentSender.SendIntentException;

public class MainActivity extends ActionBarActivity implements
		ConnectionCallbacks, OnConnectionFailedListener {

	private static final int REQUEST_CODE_RESOLUTION = 3;
	private static final String TAG = "Cloudnex";
	final static private String ACCOUNT_PREFS_NAME = "prefs";
	final static private String ACCESS_KEY_NAME = "ACCESS_KEY";
	final static private String APP_KEY = "5orvvi6ilkdpy0y";
	final static private String APP_SECRET = "3mpke0rwztqgm1m";
	final static private String ACCESS_SECRET_NAME = "ACCESS_SECRET";
	private static final boolean USE_OAUTH1 = false;
	public static DropboxAPI<AndroidAuthSession> mApi;
	public static GoogleApiClient mGoogleApiClient;
	private boolean mLoggedIn;

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
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}

		// We create a new AuthSession so that we can use the Dropbox API.
		AndroidAuthSession session = buildSession();
		mApi = new DropboxAPI<AndroidAuthSession>(session);

		// Connect to GoogleDrive
		if (mGoogleApiClient == null) {
			// Create the API client and bind it to an instance variable.
			// We use this instance as the callback for connection and
			// connection
			// failures.
			// Since no account name is passed, the user is prompted to choose.
			mGoogleApiClient = new GoogleApiClient.Builder(this)
					.addApi(Drive.API).addScope(Drive.SCOPE_FILE)
					.addConnectionCallbacks(this)
					.addOnConnectionFailedListener(this).build();
		}

		// Set Buttons
		ImageButton imageButtonGoogleDrive = (ImageButton) this
				.findViewById(R.id.imageButtonGoogleDrive);
		ImageButton imageButtonDropbox = (ImageButton) this
				.findViewById(R.id.imageButtonDropbox);
		ImageButton imageButtonOneDrive = (ImageButton) this
				.findViewById(R.id.imageButtonOneDrive);
		ImageButton imageButtonSugerSync = (ImageButton) this
				.findViewById(R.id.imageButtonSugerSync);
		ImageButton imageButtonBox = (ImageButton) this
				.findViewById(R.id.imageButtonBox);
		ImageButton imageButtonOther = (ImageButton) this
				.findViewById(R.id.imageButtonOther);
		ImageButton imageButtonContinue = (ImageButton) this
				.findViewById(R.id.imageButtonContinue);
		// Set On-Click Listeners
		imageButtonGoogleDrive.setOnClickListener(new GoogleDriveListner());
		imageButtonDropbox.setOnClickListener(new DropboxListner());
		imageButtonOneDrive.setOnClickListener(new ButtonListner());
		imageButtonSugerSync.setOnClickListener(new ButtonListner());
		imageButtonBox.setOnClickListener(new ButtonListner());
		imageButtonOther.setOnClickListener(new ButtonListner());
		imageButtonContinue.setOnClickListener(new ContinueButtonListner());

		checkAppKeySetup();
	}

	// On-Click Listener For The Continue Button
	private class ButtonListner implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			Toast.makeText(MainActivity.this, "Coming soon...",
					Toast.LENGTH_SHORT).show();
		}
	}

	private class GoogleDriveListner implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			// Connect to GoogleDrive
			if (!mGoogleApiClient.isConnected()) {
				mGoogleApiClient.connect();
			} else {
				String message = null;
				message = "GoogleDrive conncted!";
				// Show message to user
				Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

	private class DropboxListner implements View.OnClickListener {
		@SuppressWarnings("deprecation")
		@Override
		public void onClick(View v) {
			// Connect to Dropbox
			if (mLoggedIn) {
				logOut();
			} else {
				// Start the remote authentication
				if (USE_OAUTH1) {
					mApi.getSession().startAuthentication(MainActivity.this);

				} else {
					mApi.getSession().startOAuth2Authentication(
							MainActivity.this);
				}
			}
		}
	}

	// On-Click Listener For The Continue Button
	private class ContinueButtonListner implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			/* Create an Intent that will start the Search-Activity. */
			Intent searchIntent = new Intent(MainActivity.this,
					SearchActivity.class);
			MainActivity.this.startActivity(searchIntent);
			MainActivity.this.finish();
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
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		Log.i(TAG, "API client connected.");
	}

	@Override
	public void onConnectionSuspended(int cause) {
		Log.i(TAG, "GoogleApiClient connection suspended");
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// Called whenever the API client fails to connect.
		Log.i(TAG, "GoogleApiClient connection failed: " + result.toString());
		if (!result.hasResolution()) {
			// show the localized error dialog.
			GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
					0).show();
			return;
		}
		// The failure has a resolution. Resolve it.
		// Called typically when the app is not yet authorized, and an
		// authorization
		// dialog is displayed to the user.
		try {
			result.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
		} catch (SendIntentException e) {
			Log.e(TAG, "Exception while starting resolution activity", e);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onResume() {
		super.onResume();
		AndroidAuthSession session = mApi.getSession();

		// The next part must be inserted in the onResume() method of the
		// activity from which session.startAuthentication() was called, so
		// that Dropbox authentication completes properly.
		if (session.authenticationSuccessful()) {
			try {
				// Mandatory call to complete the auth
				session.finishAuthentication();

				// Store it locally in our app for later use
				storeAuth(session);
			} catch (IllegalStateException e) {
				showToast("Couldn't authenticate with Dropbox:"
						+ e.getLocalizedMessage());
				Log.i(TAG, "Error authenticating", e);
			}
		}
	}

	// This is what gets called on finishing a media piece to import
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

	}

	private void logOut() {
		// Remove credentials from the session
		mApi.getSession().unlink();

		// Clear our stored keys
		clearKeys();
	}

	private void checkAppKeySetup() {
		// Check to make sure that we have a valid app key
		if (APP_KEY.startsWith("CHANGE") || APP_SECRET.startsWith("CHANGE")) {
			showToast("You must apply for an app key and secret from developers.dropbox.com, and add them to the DBRoulette ap before trying it.");
			finish();
			return;
		}

		// Check if the app has set up its manifest properly.
		Intent testIntent = new Intent(Intent.ACTION_VIEW);
		String scheme = "db-" + APP_KEY;
		String uri = scheme + "://" + AuthActivity.AUTH_VERSION + "/test";
		testIntent.setData(Uri.parse(uri));
		PackageManager pm = getPackageManager();
		if (0 == pm.queryIntentActivities(testIntent, 0).size()) {
			showToast("URL scheme in your app's "
					+ "manifest is not set up correctly. You should have a "
					+ "com.dropbox.client2.android.AuthActivity with the "
					+ "scheme: " + scheme);
			finish();
		}
	}

	private void showToast(String msg) {
		Toast error = Toast.makeText(this, msg, Toast.LENGTH_LONG);
		error.show();
	}

	/**
	 * Shows keeping the access keys returned from Trusted Authenticator in a
	 * local store, rather than storing user name & password, and
	 * re-authenticating each time (which is not to be done, ever).
	 */
	private void loadAuth(AndroidAuthSession session) {
		SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
		String key = prefs.getString(ACCESS_KEY_NAME, null);
		String secret = prefs.getString(ACCESS_SECRET_NAME, null);
		if (key == null || secret == null || key.length() == 0
				|| secret.length() == 0)
			return;

		if (key.equals("oauth2:")) {
			// If the key is set to "oauth2:", then we can assume the token is
			// for OAuth 2.
			session.setOAuth2AccessToken(secret);
		} else {
			// Still support using old OAuth 1 tokens.
			session.setAccessTokenPair(new AccessTokenPair(key, secret));
		}
	}

	/**
	 * Shows keeping the access keys returned from Trusted Authenticator in a
	 * local store, rather than storing user name & password, and
	 * re-authenticating each time (which is not to be done, ever).
	 */
	private void storeAuth(AndroidAuthSession session) {
		// Store the OAuth 2 access token, if there is one.
		String oauth2AccessToken = session.getOAuth2AccessToken();
		if (oauth2AccessToken != null) {
			SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME,
					0);
			Editor edit = prefs.edit();
			edit.putString(ACCESS_KEY_NAME, "oauth2:");
			edit.putString(ACCESS_SECRET_NAME, oauth2AccessToken);
			edit.commit();
			return;
		}
		// Store the OAuth 1 access token, if there is one. This is only
		// necessary if
		// you're still using OAuth 1.
		AccessTokenPair oauth1AccessToken = session.getAccessTokenPair();
		if (oauth1AccessToken != null) {
			SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME,
					0);
			Editor edit = prefs.edit();
			edit.putString(ACCESS_KEY_NAME, oauth1AccessToken.key);
			edit.putString(ACCESS_SECRET_NAME, oauth1AccessToken.secret);
			edit.commit();
			return;
		}
	}

	private void clearKeys() {
		SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
		Editor edit = prefs.edit();
		edit.clear();
		edit.commit();
	}

	private AndroidAuthSession buildSession() {
		AppKeyPair appKeyPair = new AppKeyPair(APP_KEY, APP_SECRET);

		AndroidAuthSession session = new AndroidAuthSession(appKeyPair);
		loadAuth(session);
		return session;
	}
}
