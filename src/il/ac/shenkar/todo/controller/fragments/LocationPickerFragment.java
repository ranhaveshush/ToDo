/**
 * 
 */
package il.ac.shenkar.todo.controller.fragments;

import il.ac.shenkar.todo.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.RadioGroup;
import android.widget.TextView;


/**
 * @author ran
 *
 */
public class LocationPickerFragment extends DialogFragment {

	/**
	 * Logger's tag.
	 */
	private static final String TAG = "LocationPickerFragment";
	
	/**
	 * The threshold of the autocomplete address.
	 */
	private static final int AUTO_COMPLETE_TEXT_VIEW_THRESHOLD = 3;
	
	/**
	 * The address locale.
	 */
	private static final Locale LOCALE = new Locale("IW", "IL");
	
	/**
	 * Holds the lcoation state.
	 */
	private static final String STATE_LOCATION = "Location";
	
	/**
	 * Callback interface.
	 * Container activity must implement this interface.
	 * 
	 * @author ran
	 *
	 */
	public interface LocationPickerListener {
		/**
		 * When location on this is the event callback function
		 * to notify the parent activity.
		 * 
		 * @param fragmentTag			String represents the fragment tag
		 * @param location				String represnets the location
		 * @param isProximityEntering	boolean represents is proximity entering (true) or exiting (false)
		 */
		public void onLocationOn(String fragmentTag, String location, boolean isProximityEntering);
		
		/**
		 * When location off this is the event callback function
		 * to notify the parent activity.
		 * 
		 * @param fragmentTag	String represents the fragment tag
		 */
		public void onLocationOff(String fragmentTag);
	}
	
	/**
	 * Holds the hosting activity.
	 */
	private Activity hostingActivity = null;
	
	/**
	 * Listener.
	 */
	private LocationPickerListener mListener = null;
	
	/**
	 * Autocomplete text view represents the location (address).
	 */
	private AutoCompleteTextView autoCompleteTextViewAddress = null;
	
	/**
	 * Autocomplete dropdown list adapter.
	 */
	private ArrayAdapter<String> autoCompleteAdapter = null;
	
	/**
	 * RadioGroup represents the notify when (entering or exiting).
	 */
	private RadioGroup radioGroupNotifyWhen = null;
	
	/**
     * Instantiates a new instance of location picker fragment.
     * 
     * @param location		String represents location
     * @return				DialogFragment new instance of location picker fragment
     */
	public static DialogFragment newInstance(String location) {
    	// Logger
    	Log.d(TAG, "newInstance(String location)");
    	
    	LocationPickerFragment newDialogFragment = new LocationPickerFragment();
    	
    	Bundle args = new Bundle();
    	args.putString(STATE_LOCATION, location);
    	newDialogFragment.setArguments(args);
    	
    	return newDialogFragment;
    }

	/* (non-Javadoc)
	 * @see android.support.v4.app.DialogFragment#onAttach(android.app.Activity)
	 */
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		// Logger
		Log.d(TAG, "onAttach(Activity activity)");
		
		try {
			mListener = (LocationPickerListener) activity;
			hostingActivity = activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity + " must implement LocationPickerListener");
		}
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.DialogFragment#onCreateDialog(android.os.Bundle)
	 */
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Logger
		Log.d(TAG, "onCreateDialog(Bundle savedInstanceState)");
		
		LayoutInflater inflater = hostingActivity.getLayoutInflater();
		final View locationDialog = inflater.inflate(R.layout.location_dialog, null);
		
		radioGroupNotifyWhen = (RadioGroup) locationDialog.findViewById(R.id.radio_group_notify_when);
		
		autoCompleteAdapter = new ArrayAdapter<String>(hostingActivity, android.R.layout.simple_dropdown_item_1line);
		
		autoCompleteTextViewAddress = (AutoCompleteTextView) locationDialog.findViewById(R.id.auto_complete_text_view_location);
		autoCompleteTextViewAddress.setThreshold(AUTO_COMPLETE_TEXT_VIEW_THRESHOLD);
		autoCompleteTextViewAddress.setAdapter(autoCompleteAdapter);
		autoCompleteTextViewAddress.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String toAutoCompleteAddressStr = s.toString();
				// If the address string isn't empty and longer then threshold, autocomplete
				if (!TextUtils.isEmpty(toAutoCompleteAddressStr) && toAutoCompleteAddressStr.length() >= AUTO_COMPLETE_TEXT_VIEW_THRESHOLD) {
					new AddressesAutoCompleteTask().execute(new String[] { toAutoCompleteAddressStr });
				}
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		autoCompleteTextViewAddress.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				String selectedAddressStr = ((TextView) view).getText().toString();
				autoCompleteTextViewAddress.setText(selectedAddressStr);
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// Do nothing.
			}
		});
		
		// During creation, if arguments have been supplied to the fragment
	    // then parse those out
		Bundle args = getArguments();
		if (args != null) {
			// Sets the auto complete text to the old location text
			String location = args.getString(STATE_LOCATION);
			autoCompleteTextViewAddress.setText(location);
		}
		
		// Create a new instance of AlertDialog and return it
		AlertDialog locationPickerDialog = new AlertDialog.Builder(hostingActivity)
		.setTitle(R.string.location_picker_title)
		.setView(locationDialog)
		.setPositiveButton(R.string.alert_dialog_button_positive, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// Gets the is proximitry entering  
				boolean isProximityEntering;
				if (radioGroupNotifyWhen.getCheckedRadioButtonId() == R.id.radio_button_entering) {
					isProximityEntering = true;
				} else {
					isProximityEntering = false;
				}
				// Gets the location string
				String location = autoCompleteTextViewAddress.getText().toString();
				mListener.onLocationOn(getTag(), location, isProximityEntering);
			}
		})
		.setNegativeButton(R.string.alert_dialog_button_negative, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mListener.onLocationOff(getTag());
			}
		})
		.create();
		
		return locationPickerDialog;
	}
	
	/**
	 * Auto complete async task to geocode addresses.
	 * 
	 * @author ran
	 * 
	 */
	private class AddressesAutoCompleteTask extends AsyncTask<String, Void, List<Address>> {
		
		/**
		 * Logger's tag.
		 */
		private static final String TAG = "AddressesAutoCompleteTask: TaskEditorActivity";

		/**
		 * Max results per autocomplete.
		 */
		private static final int MAX_RESULTS = 3;

		@Override
		protected List<Address> doInBackground(String... addresses) {
			// Logger
			Log.d(TAG, "doInBackground(String... addresses)");
			
			String toAutoCompleteAddressStr = addresses[0];
				try {
					return new Geocoder(hostingActivity, LOCALE)
							.getFromLocationName(toAutoCompleteAddressStr, MAX_RESULTS);
				} catch (IOException e) {
					e.printStackTrace();
				}
			return null;
		}
		@Override
		protected void onPostExecute(List<Address> autoCompleteAddressesSuggestions) {
			// Logger
			Log.d(TAG, "onPostExecute(List<Address> autoCompleteAddressesSuggestions)");
			
			autoCompleteAdapter.clear();
			if (autoCompleteAddressesSuggestions == null) {
				return;
			}
			for (Address address : autoCompleteAddressesSuggestions) {
				String addressLine = (address.getMaxAddressLineIndex() > 0) ? address.getAddressLine(0) : "";
				String addressLocality = (null != address.getLocality()) ? address.getLocality() : "";
				String addressCountry = (null != address.getCountryName()) ? address.getCountryName() : "";
				String addressText = String.format("%s, %s, %s", addressLine, addressLocality, addressCountry);
				autoCompleteAdapter.add(addressText);
			}
			autoCompleteAdapter.notifyDataSetChanged();
		}
	}
	
}
