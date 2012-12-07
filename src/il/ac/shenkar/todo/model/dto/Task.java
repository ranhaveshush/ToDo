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
	 * Task's description.
	 */
	private String description = null;

	/**
	 * Default constructor.
	 */
	public Task() {
		super();
	}

	/**
	 * Full constructor.
	 * 
	 * @param id
	 * @param description
	 */
	public Task(long id, String description) {
		super();
		this.id = id;
		this.description = description;
	}
	
	/**
	 * Partcial constructor.
	 * 
	 * @param description
	 */
	public Task(String description) {
		super();
		this.description = description;
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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TASK[ID:" + id + ", DESCRIPTION:" + description + "]";
	}

}
