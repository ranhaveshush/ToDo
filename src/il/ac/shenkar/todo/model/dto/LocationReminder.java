/**
 * 
 */
package il.ac.shenkar.todo.model.dto;

/**
 * @author ran
 *
 */
public class LocationReminder extends Reminder {
	
	/**
	 * LocationReminder's latitude.
	 */
	private double latitude = 0D;
	
	/**
	 * LocationReminder's longitude.
	 */
	private double longitude = 0D;

	/**
	 * Partial constructor.
	 * 
	 * @param taskId
	 */
	public LocationReminder(String taskId) {
		super(taskId);
	}
	
	/**
	 * Partial constructor.
	 * 
	 * @param taskId
	 * @param latitude
	 * @param longitude
	 */
	public LocationReminder(String taskId, double latitude, double longitude) {
		super(taskId);
		this.latitude = latitude;
		this.longitude = longitude;
	}

	/**
	 * Full constructor.
	 * 
	 * @param taskId
	 * @param reminderId
	 * @param reminderLat
	 * @param reminderLng
	 */
	public LocationReminder(String taskId, int reminderId, double latitude,
			double longitude) {
		super(taskId, reminderId);
		this.latitude = latitude;
		this.longitude = longitude;
	}

	/**
	 * @return the latitude
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * @param latitude the latitude to set
	 */
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	/**
	 * @return the longitude
	 */
	public double getLongitude() {
		return longitude;
	}

	/**
	 * @param longitude the longitude to set
	 */
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
}
