/**
 * 
 */
package il.ac.shenkar.todo.utilities;

import il.ac.shenkar.todo.config.ToDo;
import android.accounts.Account;
import android.content.ContentResolver;
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
	 * Syncs tasks in the content provider with Google Tasks service.
	 */
	public static void syncWithGoogleTasks() {
		// Logger
		Log.d(TAG, "syncWithGoogleTasks()");
		
		Account account = new Account(ToDo.Auth.ACCOUNT_NAME, ToDo.Auth.ACCOUNT_TYPE);
		ContentResolver.requestSync(account, ToDo.Tasks.AUTHORITY, new Bundle());
	}

}
