/**
 * 
 */
package il.ac.shenkar.todo.utilities;

import il.ac.shenkar.todo.config.ToDo;
import android.accounts.Account;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

/**
 * @author ran
 *
 */
public abstract class SyncUtils {
	
	/**
	 * Logger's tag.
	 */
	private final static String TAG = "SyncUtils";
	
	/**
	 * Holds the google account type string.
	 */
	private static final String ACCOUNT_TYPE = "com.google";
	
	/**
	 * Syncs tasks in the content provider with Google Tasks service.
	 * 
	 * @param appContext	The application context
	 */
	public static void syncWithGoogleTasks(Context appContext) {
		// Logger
		Log.d(TAG, "syncWithGoogleTasks(Context context)");

		String accountName = PrefsUtils.getPrivatePref(appContext,
				ToDo.Prefs.PREFS_FILE_AUTH,
				ToDo.Prefs.PREF_ACCOUNT_NAME);
		
		Account account = new Account(accountName, ACCOUNT_TYPE);
		ContentResolver.requestSync(account, ToDo.Tasks.AUTHORITY, new Bundle());
	}

}
