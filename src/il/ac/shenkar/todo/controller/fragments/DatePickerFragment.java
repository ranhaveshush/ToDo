/**
 * 
 */
package il.ac.shenkar.todo.controller.fragments;

import il.ac.shenkar.todo.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.DatePicker;

/**
 * @author ran
 *
 */
public class DatePickerFragment extends DialogFragment {
	
	/**
	 * Logger tag.
	 */
	private static final String TAG = "DatePickerFragment";
	
	/**
	 * Callback interface.
	 * Container activity must implement this interface.
	 * 
	 * @author ran
	 *
	 */
	public interface DatePickerListener {
		/**
		 * When date on this is the event callback function
		 * to notify the parent activity.
		 * 
		 * @param fragmentTag	String represents the fragment's tag
		 * @param year			Integer represents the year
		 * @param monthOfYear	Integer represents the month
		 * @param dayOfMonth	Integer represents the day
		 */
		public void onDateOn(String fragmentTag, int year, int monthOfYear, int dayOfMonth);
		
		/**
		 * When date off this is the event callback function
		 * to notify the parent activity.
		 * 
		 * @param fragmentTag	String represents the fragment's tag
		 */
		public void onDateOff(String fragmentTag);
	}
	
	/**
	 * Listener.
	 */
	private DatePickerListener mListener = null;
	
	/**
	 * Holds the hosting activity.
	 */
	private Activity hostingActivity = null;
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.DialogFragment#onAttach(android.app.Activity)
	 */
	@Override
	public void onAttach(Activity activity) {
		// Logger
		Log.d(TAG, "onAttach(Activity activity)");
		
		super.onAttach(activity);
		try {
			mListener = (DatePickerListener) activity;
			// Gets the hosting activity
			hostingActivity = activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity + " must implement DatePickerListener");
		}
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.DialogFragment#onCreateDialog(android.os.Bundle)
	 */
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Logger
		Log.d(TAG, "onCreateDialog(Bundle savedInstanceState)");
		
		// Inflate the date picker view
		LayoutInflater inflater = hostingActivity.getLayoutInflater();
		final DatePicker dateDialog = (DatePicker) inflater.inflate(R.layout.date_dialog, null);
		
		// Create a new instance of AlertDialog and return it
		AlertDialog datePickerDialog = new AlertDialog.Builder(hostingActivity)
		.setTitle(R.string.date_picker_title)
		.setView(dateDialog)
		.setPositiveButton(R.string.alert_dialog_button_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            	mListener.onDateOn(getTag(),
            			dateDialog.getYear(),
            			dateDialog.getMonth(),
            			dateDialog.getDayOfMonth());
            }
        })
		.setNegativeButton(R.string.alert_dialog_button_negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            	mListener.onDateOff(getTag());
            }
        })
        .create();
		
		return datePickerDialog;
	}

}
