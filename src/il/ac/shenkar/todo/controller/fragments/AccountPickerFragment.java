/**
 * 
 */
package il.ac.shenkar.todo.controller.fragments;

import il.ac.shenkar.todo.R;
import il.ac.shenkar.todo.config.ToDo;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import com.google.api.client.googleapis.extensions.android.accounts.GoogleAccountManager;
import com.google.api.services.tasks.TasksScopes;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;

/**
 * @author ran
 *
 */
public class AccountPickerFragment extends DialogFragment {
	
	/**
	 * Logger tag.
	 */
	private static final String TAG = "AccountPickerFragment";
	
	/**
	 * API key from the API Console.
	 */
	private static final String API_KEY = "AIzaSyCHLhkuoUXdEcSz6J7hDg0YeONpqtNL28c";
	
	/**
	 * Client id from the API Console.
	 */
	private static final String CLIENT_ID = "862893799465.apps.googleusercontent.com";
	
	/**
	 * Client secret from the API Console.
	 */
	private static final String CLIENT_SECRET = "{'installed':{'auth_uri':'https://accounts.google.com/o/oauth2/auth','token_uri':'https://accounts.google.com/o/oauth2/token','client_email':'','redirect_uris':['urn:ietf:wg:oauth:2.0:oob','oob'],'client_x509_cert_url':'','client_id':'862893799465.apps.googleusercontent.com','auth_provider_x509_cert_url':'https://www.googleapis.com/oauth2/v1/certs'}}";
	
	/**
	 * Human readable alias for Tasks API, equivalent to using the OAuth2.0 scope.
	 */
	private static final String AUTH_TOKEN_TYPE = "Manage your tasks";
	
	/**
	 * Request code authenticator returns.
	 */
	private static final int AUTHENTICATOR_REQUEST_CODE = 0;
	
	/**
	 * AccountManager handles the accounts registered on the user device.
	 */
	private AccountManager accountManager = null;

	/**
	 * Returns an instance of the AccountPickerFragment class.
	 * 
	 * @param title Integer represents string resource id
	 * @return
	 */
	public static AccountPickerFragment newInstance(int title) {
		AccountPickerFragment accountPickerFragment = new AccountPickerFragment();
		Bundle args = new Bundle();
		args.putInt(ToDo.Args.ARG_ACCOUNT_PICKER_DIALOG_TITLE, title);
		accountPickerFragment.setArguments(args);
		return accountPickerFragment;
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.DialogFragment#onCreateDialog(android.os.Bundle)
	 */
	@SuppressLint("NewApi")
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Gets the account picker dialog fragment title as
		// resource id represents a string from the strings resources
		int title = getArguments().getInt(ToDo.Args.ARG_ACCOUNT_PICKER_DIALOG_TITLE);
		
		// Gets all google accounts the user registered on the device
		accountManager = AccountManager.get(getActivity());
		final Account[] accounts = accountManager.getAccountsByType(GoogleAccountManager.ACCOUNT_TYPE);
		int size = accounts.length;
		String[] names = new String[size];
		for (int i = 0; i < size; i++) {
			names[i] = accounts[i].name;
		}
		
		return new AlertDialog.Builder(getActivity())
			.setIcon(R.drawable.ic_launcher)
			.setTitle(title)
			.setItems(names, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					gotAccount(accounts[which]);
		        }
		     })
		    .create();
	}

	@SuppressLint("NewApi")
	private void gotAccount(Account account) {
		AccountManager accountManager = AccountManager.get(getActivity());
		accountManager.getAuthToken(account, AUTH_TOKEN_TYPE, null, getActivity(), new AccountManagerCallback<Bundle>() {
			public void run(AccountManagerFuture<Bundle> future) {
				try {
					// Gets the auth token
					String token = future.getResult().getString(AccountManager.KEY_AUTHTOKEN);
					
					Toast.makeText(getActivity(), token, Toast.LENGTH_LONG).show();
					
					// Validates the auth token
					// accountManager.invalidateAuthToken(GoogleAccountManager.ACCOUNT_TYPE, token);
					
					// Gets the intent and starts it, if any
					Intent intent = (Intent) future.getResult().get(AccountManager.KEY_INTENT);
					if (intent != null) {
						startActivityForResult(intent, AUTHENTICATOR_REQUEST_CODE);
						return;
					}
					
					useTasksAPI(token);
				// The user has denied you access to the API
				} catch (OperationCanceledException e) {
					// FIXME: fix the undefined getSupportFragmentManager()
					/*// Gets the account picker dialog fragment title as
					// resource id represents a string from the strings resources
					int title = getArguments().getInt(ToDo.Args.ARG_ACCOUNT_PICKER_DIALOG_TITLE);
					
					AccountPickerFragment accountPickerFragment = AccountPickerFragment.newInstance(title);
					accountPickerFragment.show(getSupportFragmentManager(), "accountPicker");*/
				// Error communicating with the authenticator or
				// The authenticator return an invalid response
				} catch (AuthenticatorException e) {
					e.printStackTrace();
				// If the authenticator returned an error response
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}, null);
	}
	
	/**
	 * @param accessToken
	 */
	private void useTasksAPI(String accessToken) {
		try {
			URL url = new URL("https://www.googleapis.com/tasks/v1/users/@me/lists?key=" + API_KEY);
			URLConnection urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.addRequestProperty("client_id", CLIENT_ID);
			urlConnection.addRequestProperty("client_secret", CLIENT_SECRET);
			urlConnection.addRequestProperty("Authorization", "OAuth " + accessToken);
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case AUTHENTICATOR_REQUEST_CODE:
			return;
		default:
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

}