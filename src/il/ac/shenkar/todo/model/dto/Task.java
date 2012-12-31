/**
 * 
 */
package il.ac.shenkar.todo.model.dto;



/**
 * This class represents a task.
 * 
 * @author ran
 *
 */
/**
 * @author ran
 *
 */
public class Task {
	
	/**
	 * Task's id.
	 */
	private long id;
	
	/**
	 * Task's title.
	 */
	private String title = null;
	
	/**
	 * Task's description.
	 */
	private String description = null;
	
	/**
	 * Task's datetime reminder.
	 */
	private long dateTimeMilliSec = 0;

	/**
	 * Default constructor.
	 */
	public Task() {
		super();
	}
	
	/**
	 * Parcial constructor.
	 * 
	 * @param title
	 */
	public Task(String title) {
		super();
		this.title = title;
	}
	
	/**
	 * Parcial constructor.
	 * 
	 * @param title
	 * @param description
	 */
	public Task(String title, String description) {
		super();
		this.title = title;
		this.description = description;
	}

	/**
	 * Full constructor.
	 * 
	 * @param id
	 * @param title
	 * @param description
	 * @param dateTime
	 */
	public Task(long id, String title, String description, long dateTimeMilliSec) {
		super();
		this.id = id;
		this.title = title;
		this.description = description;
		this.dateTimeMilliSec = dateTimeMilliSec;
	}
	
	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}
	
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * @return the dateTimeMilliSec
	 */
	public long getDateTimeMilliSec() {
		return dateTimeMilliSec;
	}

	/**
	 * @param dateTimeMilliSec the dateTimeMilliSec to set
	 */
	public void setDateTimeMilliSec(long dateTimeMilliSec) {
		this.dateTimeMilliSec = dateTimeMilliSec;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TASK[ID:" + id + ", TITLE:" + title + ", DESCRIPTION:" + description + ", DATETIMEMILLISEC: " + dateTimeMilliSec + "]";
	}

}
