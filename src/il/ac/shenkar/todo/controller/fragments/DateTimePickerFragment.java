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
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

/**
 * @author ran
 *
 */
public class DateTimePickerFragment extends DialogFragment {
	
	/**
	 * Logger tag.
	 */
	private static final String TAG = "DateTimePickerFragment";
	
	/**
	 * Callback interface.
	 * Container activity must implement this interface.
	 * 
	 * @author ran
	 *
	 */
	public interface DateTimePickedListener {
		/**
		 * When datetime on this is the event callback function
		 * to notify the parent activity.
		 * 
		 * @param fragmentTag	String represents the fragment tag
		 * @param year			Integer represents the year
		 * @param monthOfYear	Integer represents the month
		 * @param dayOfMonth	Integer represents the day
		 * @param hourOfDay		Integer represents the hour
		 * @param minute		Integer represents the minute
		 */
		public void onDateTimeOn(String fragmentTag, int year, int monthOfYear, int dayOfMonth, int hourOfDay, int minute);
		
		/**
		 * When datetime off this is the event callback function
		 * to notify the parent activity.
		 * 
		 * @param fragmentTag	String represents the fragment tag
		 */
		public void onDateTimeOff(String fragmentTag);
	}
	
	/**
	 * Listener.
	 */
	private DateTimePickedListener mListener = null;
	
	/**
	 * Holds the hosting activity.
	 */
	private Activity hostingActivity = null;
	
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.DialogFragment#onAttach(android.app.Activity)
	 */
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		// Logger
		Log.d(TAG, "onAttach(Activity activity)");
		
		try {
			mListener = (DateTimePickedListener) activity;
			// Gets the hosting activity
			hostingActivity = activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity + " must implement DateTimePickerListener");
		}
	}
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.DialogFragment#onCreateDialog(android.os.Bundle)
	 */
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Logger
		Log.d(TAG, "onCreateDialog(Bundle savedInstanceState)");

		// Inflate the date time picker view
		LayoutInflater inflater = hostingActivity.getLayoutInflater();
		final View dateTimeDialog = inflater.inflate(R.layout.datetime_dialog, null);
		
		// Gets the date and time picker widgets
		final DatePicker datePicker = (DatePicker) dateTimeDialog.findViewById(R.id.date_picker);
		final TimePicker timePicker = (TimePicker) dateTimeDialog.findViewById(R.id.time_picker);
				
		// Create a new instance of AlertDialog and return it
		AlertDialog dateTimePickerDialog = new AlertDialog.Builder(hostingActivity)
		.setTitle(R.string.datetime_picker_title)
		.setView(dateTimeDialog)
		.setPositiveButton(R.string.alert_dialog_button_positive, new DialogInterface.OnClickListener() {
	        @Override
	        public void onClick(DialogInterface dialog, int which) {
	        	mListener.onDateTimeOn(getTag(),
	        			datePicker.getYear(),
	        			datePicker.getMonth(),
	        			datePicker.getDayOfMonth(),
	        			timePicker.getCurrentHour(),
	        			timePicker.getCurrentMinute());
	        }
	    })
		.setNegativeButton(R.string.alert_dialog_button_negative, new DialogInterface.OnClickListener() {
	        @Override
	        public void onClick(DialogInterface dialog, int which) {
	        	mListener.onDateTimeOff(getTag());
	        }
	    })
	    .create();
		
		return dateTimePickerDialog;
	}

}