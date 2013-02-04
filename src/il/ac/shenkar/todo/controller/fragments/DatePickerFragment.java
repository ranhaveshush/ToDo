/**
 * 
 */
package il.ac.shenkar.todo.controller.fragments;

import java.util.Calendar;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.DatePicker;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;

/**
 * @author ran
 *
 */
public class DatePickerFragment extends DialogFragment implements OnDateSetListener {
	
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
	public interface OnDatePickedListener {
		/**
		 * When date picked set this is the event callback function
		 * to notify the parent activity.
		 * 
		 * @param fragmentTag	String represents the fragment's tag
		 * @param year			Integer represents the year
		 * @param monthOfYear	Integer represents the month
		 * @param dayOfMonth	Integer represents the day
		 */
		public void onDatePicked(String fragmentTag, int year, int monthOfYear, int dayOfMonth);
	}
	
	/**
	 * Listener.
	 */
	private OnDatePickedListener listener = null;

	/* (non-Javadoc)
	 * @see android.support.v4.app.DialogFragment#onCreateDialog(android.os.Bundle)
	 */
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the current date as the default date in the picker
		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);
		
		// Create a new instance of DatePickerDialog and return it
		return new DatePickerDialog(getActivity(), this, year, month, day);
	}

	/* (non-Javadoc)
	 * @see android.app.DatePickerDialog.OnDateSetListener#onDateSet(android.widget.DatePicker, int, int, int)
	 */
	@Override
	// FIXME: Why this method invoked twice
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		Log.d(TAG, "Date picked: " + year + "/" + monthOfYear + "/" + dayOfMonth);
		listener.onDatePicked(getTag(), year, monthOfYear, dayOfMonth);
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.DialogFragment#onAttach(android.app.Activity)
	 */
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			listener = (OnDatePickedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity + " must implement OnDatePickedListener");
		}
	}

}
