/**
 * 
 */
package il.ac.shenkar.todo.model.dto;

/**
 * @author ran
 *
 */
public class DateTimeReminder extends Reminder {
	
	/**
	 * DateTimeReminder's datetime milliseconds.
	 */
	private long dateTimeMillis = 0;

	/**
	 * Partial constructor.
	 * 
	 * @param taskId
	 */
	public DateTimeReminder(String taskId) {
		super(taskId);
	}
	
	/**
	 * Partial constructor.
	 * 
	 * @param taskId
	 * @param dateTimeMillis
	 */
	public DateTimeReminder(String taskId, long dateTimeMillis) {
		super(taskId);
		this.dateTimeMillis = dateTimeMillis;
	}

	/**
	 * Full constructor.
	 * 
	 * @param taskId
	 * @param reminderId
	 * @param dateTimeMillis
	 */
	public DateTimeReminder(String taskId, int reminderId, long dateTimeMillis) {
		super(taskId, reminderId);
		this.dateTimeMillis = dateTimeMillis;
	}

	/**
	 * @return the dateTimeMillis
	 */
	public long getDateTimeMillis() {
		return dateTimeMillis;
	}

	/**
	 * @param dateTimeMillis the dateTimeMillis to set
	 */
	public void setDateTimeMillis(long dateTimeMillis) {
		this.dateTimeMillis = dateTimeMillis;
	}

}
