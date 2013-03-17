package il.ac.shenkar.todo.controller.activities;

import il.ac.shenkar.todo.config.ToDo;
import il.ac.shenkar.todo.utilities.PrefsUtils;
import il.ac.shenkar.todo.utilities.SyncUtils;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import android.accounts.AccountManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.actionbarsherlock.app.SherlockActivity;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.tasks.TasksScopes;

public class CredentialActivity extends SherlockActivity {
	
	/**
	 * Logger tag.
	 */
	private static final String TAG = "CredentialActivity";
	
	/**
	 * Logging level.
	 * 
	 * <p>
	 * 		To turn logging on,
	 * 		set to {@link Level#CONFIG}
	 * 		or {@link Level#ALL} and run command line:
	 * </p>
	 * <pre>adb shell setprop log.tag.HttpTransport DEBUG</pre>
	 */
	private static final Level LOGGING_LEVEL = Level.ALL;

	/**
	 * Google account credential for OAuth 2.0 protocol.
	 */
	private static GoogleAccountCredential credential;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Logger
		Log.d(TAG, "onCreate(Bundle savedInstanceState)");
		
//		setContentView(R.layout.activity_credential);
		
		// Enable logging
	    Logger.getLogger("com.google.api.client").setLevel(LOGGING_LEVEL);
	    
	    // Google Accounts
	    credential = GoogleAccountCredential.usingOAuth2(
	    		CredentialActivity.this,
	    		TasksScopes.TASKS,
	    		TasksScopes.TASKS_READONLY);
	    
	    // Validates Google Play Services availability
	    if (isGooglePlayServicesAvailable()) {
			haveGooglePlayServices();
		}
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart() {
		super.onStart();
		
		// Logger
		Log.d(TAG, "onStart()");
		
		EasyTracker.getInstance().activityStart(this);
	}
	
	/* (non-Javadoc)
	 * @see com.actionbarsherlock.app.SherlockActivity#onStop()
	 */
	@Override
	protected void onStop() {
		super.onStop();
		
		// Logger
		Log.d(TAG, "onStop()");
		
		EasyTracker.getInstance().activityStop(this);
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Logger
		Log.d(TAG, "onActivityResult(int requestCode, int resultCode, Intent data)");
		
		super.onActivityResult(requestCode, resultCode, data);
		
		switch (requestCode) {
		case ToDo.Requests.REQUEST_GOOGLE_PLAY_SERVICES:
			// Logger
			Log.d(TAG, "REQUEST_GOOGLE_PLAY_SERVICES");
			// If the recoverable error solved by the user
			if (resultCode == RESULT_OK) {
				// The user have valide google play services
				haveGooglePlayServices();
			// If the recoverable error not solved by the user
			} else {
				// Try to solve the error
				isGooglePlayServicesAvailable();
			}
			break;
		case ToDo.Requests.REQUEST_AUTHORIZATION:
			// Logger
			Log.d(TAG, "REQUEST_AUTHORIZATION");
			
			if (resultCode == RESULT_OK) {
				// Gets the auth token from extras and sets it to prefs
				String authToken = data.getExtras().getString(AccountManager.KEY_AUTHTOKEN);
				Log.d(TAG, "authToken: " + authToken);
				PrefsUtils.setPrivatePref(getApplicationContext(),
						ToDo.Prefs.PREFS_FILE_AUTH,
						ToDo.Prefs.PREF_AUTH_TOKEN,
						authToken);
				// Syncs with the cloud
				SyncUtils.syncWithGoogleTasks(getApplicationContext());
				// Redirects to the MainFragmentActivity
				Intent intent = new Intent(CredentialActivity.this, MainFragmentActivity.class);
				startActivity(intent);
				// Closes this activity
				finish();
			} else {
				selectAccount();
			}
			break;
		case ToDo.Requests.REQUEST_ACCOUNT_PICKER:
			// Logger
			Log.d(TAG, "REQUEST_ACCOUNT_PICKER");
			
			if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
				// Gets the account name from extras
				String accountName = data.getExtras().getString(AccountManager.KEY_ACCOUNT_NAME);
				Log.d(TAG, "Account picked: " + accountName);
				if (accountName != null) {
					// Sets the ceredential selected google account name
					credential.setSelectedAccountName(accountName);
					// Sets the account name pref
					PrefsUtils.setPrivatePref(getApplicationContext(),
							ToDo.Prefs.PREFS_FILE_AUTH,
							ToDo.Prefs.PREF_ACCOUNT_NAME,
							accountName);
					// Sets the auth token pref
					new prefAuthTokenAsyncTask().execute();
				}
			}
			break;
		}
	}
	
	/**
	 * Checks if that Google Play Services APK is installed and up to date.
	 * 
	 * @return
	 */
	public boolean isGooglePlayServicesAvailable() {
		// Logger
		Log.d(TAG, "isGooglePlayServicesAvailable()");
		
		final int connectionStatusCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(CredentialActivity.this);
		// If the connection status code is user recoverable, notify the user
		if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
			showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
			return false;
		}
		return true;
	}
	
	/**
	 * Shows an error dialog to notify the user
	 * about a recoverable error in the google play services.
	 * 
	 * @param connectionStatusCode
	 */
	public void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode) {
		// Logger
		Log.d(TAG, "showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode)");
		
		runOnUiThread(new Runnable() {
			public void run() {
				GooglePlayServicesUtil.getErrorDialog(
						connectionStatusCode,
						CredentialActivity.this,
						ToDo.Requests.REQUEST_GOOGLE_PLAY_SERVICES).show();
			}
		});
	}
	
	/**
	 * The user have valide Google Play Services APK,
	 * Now the user promoted to select google account to sync tasks with or
	 * if he allready selected then load the data from the selected account.
	 */
	public void haveGooglePlayServices() {
		// Logger
		Log.d(TAG, "haveGooglePlayServices()");
		
	    // If the user didn't choose an account
		if (credential.getSelectedAccountName() == null) {
			// Asks user to select account
			selectAccount();
		// If the user did choose an account
		} else {
			// Syncs with the cloud
			SyncUtils.syncWithGoogleTasks(getApplicationContext());
			// Redirects to the TasksListActivity
			Intent intent = new Intent(CredentialActivity.this, MainFragmentActivity.class);
			startActivity(intent);
			// Closes this activity
			finish();
		}
	}
	
	/**
	 * Shows the user account picker dialog.
	 * Asks the user to select google account for the tasks sync.
	 */
	public void selectAccount() {
		// Logger
		Log.d(TAG, "selectAccount()");
		
		// Invokes account picker dialog
		startActivityForResult(credential.newChooseAccountIntent(), ToDo.Requests.REQUEST_ACCOUNT_PICKER);
	}
	
	/**
	 * Save the auth access token in the auth prefs file.
	 * 
	 * @author ran
	 *
	 */
	private class prefAuthTokenAsyncTask extends AsyncTask<Void, Void, String> {
		
		private static final String TAG = "prefAuthTokenAsyncTask";
		
		@Override
		protected String doInBackground(Void... ignored) {
			// Logger
			Log.d(TAG, "doInBackground(Void... ignored)");
			
			try {
				return credential.getToken();
			} catch (UserRecoverableAuthException e) {
				startActivityForResult(e.getIntent(), ToDo.Requests.REQUEST_AUTHORIZATION);
			} catch (GoogleAuthException e) {
				e.printStackTrace();
	        } catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
		@Override
		protected void onPostExecute(String authToken) {
			// Logger
			Log.d(TAG, "onPostExecute(String authToken)");
			
			Log.d(TAG, "AuthToken: " + authToken);
			
			if (authToken != null) {
				// Sets the auth token pref
				PrefsUtils.setPrivatePref(getApplicationContext(),
						ToDo.Prefs.PREFS_FILE_AUTH,
						ToDo.Prefs.PREF_AUTH_TOKEN,
						authToken);
				// Syncs with the cloud
				SyncUtils.syncWithGoogleTasks(getApplicationContext());
				// Redirects to the TasksListActivity
				Intent intent = new Intent(CredentialActivity.this, MainFragmentActivity.class);
				startActivity(intent);
				// Closes this activity
				finish();
			}
		}
	}

}
