/**
 * 
 */
package il.ac.shenkar.todo.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * @author ran
 *
 */
public abstract class PrefsUtils {
	
	/**
	 * Logger's tag.
	 */
	private static final String TAG = "PrefsUtils";
	
	/**
	 * Gets a pref value from given pref file name and pref key.
	 * 
	 * @param context
	 * @param prefFileName		String represents the pref file name
	 * @param prefKey			String represents the pref key
	 * 
	 * @return					String the pref value
	 */
	public static String getPrivatePref(Context context, String prefFileName, String prefKey) {
		SharedPreferences prefsFileAuth = context.getSharedPreferences(prefFileName, Context.MODE_PRIVATE);
		return prefsFileAuth.getString(prefKey, null);
	}
	
	/**
	 * Sets given pref key and value to given pref file name.
	 *
	 * @param context
	 * @param prefFileName	String represents the pref file name
	 * @param prefKey		String represents the pref key
	 * @param prefValue		String represents the pref value
	 */
	public static void setPrivatePref(Context context, String prefFileName, String prefKey, String prefValue) {
		// Logger
		Log.d(TAG, "setPrivatePref(Context context, String prefFileName, String prefKey, String prefValue)");
		
		SharedPreferences prefsFileAuth = context.getSharedPreferences(prefFileName, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefsFileAuth.edit();
		editor.putString(prefKey, prefValue);
		editor.commit();
	}

}
