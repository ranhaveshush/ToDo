/**
 * 
 */
package il.ac.shenkar.todo.model.dto;

/**
 * @author ran
 *
 */
public abstract class Reminder {
	
	/**
	 * Task's id for the reminder to remind.
	 */
	private String taskId = null;
	
	/**
	 * Reminder's id.
	 */
	private int id = -1;
	
	/**
	 * Parcial constructor.
	 * 
	 * @param taskId
	 */
	protected Reminder(String taskId) {
		this.taskId = taskId;
	}

	/**
	 * Full constructor.
	 * 
	 * @param taskId
	 * @param id
	 */
	protected Reminder(String taskId, int id) {
		super();
		this.taskId = taskId;
		this.id = id;
	}

	/**
	 * @return the taskId
	 */
	public String getTaskId() {
		return taskId;
	}

	/**
	 * @param taskId the taskId to set
	 */
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

}
