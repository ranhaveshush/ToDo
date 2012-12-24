/**
 * 
 */
package il.ac.shenkar.todo.controller.fragments;

import java.util.Calendar;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TimePicker;

/**
 * @author ran
 *
 */
public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
	
	/**
	 * Logger tag.
	 */
	private static final String TAG = "TimePickerFragment";
	
	/**
	 * Callback interface.
	 * Container activity must implement this interface.
	 * 
	 * @author ran
	 *
	 */
	public interface OnTimePickedListener {
		/**
		 * When time picked set this is the event callback function
		 * to notify the parent activity.
		 * 
		 * @param hourOfDay
		 * @param minute
		 */
		public void onTimePicked(int hourOfDay, int minute);
	}
	
	/**
	 * Listener.
	 */
	private OnTimePickedListener listener = null;

	/* (non-Javadoc)
	 * @see android.support.v4.app.DialogFragment#onCreateDialog(android.os.Bundle)
	 */
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Uses the current time as the default values for the picker
		final Calendar calendar = Calendar.getInstance();
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);

		// Creates a new instance of TimePickerDialog and returns it
		return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
	}

	/* (non-Javadoc)
	 * @see android.app.TimePickerDialog.OnTimeSetListener#onTimeSet(android.widget.TimePicker, int, int)
	 */
	@Override
	// FIXME: Why this method invoked twice
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		Log.d(TAG, "Time picked: " + hourOfDay + ":" + minute); 
		listener.onTimePicked(hourOfDay, minute);
	}

	/* (non-Javadoc)036996
	 * @see android.support.v4.app.DialogFragment#onAttach(android.app.Activity)
	 */
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			listener = (OnTimePickedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity + " must implement OnTimePickedListener");
		}
	}

}