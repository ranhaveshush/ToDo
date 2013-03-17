/**
 * 
 */
package il.ac.shenkar.todo.model.dto;

import com.google.api.services.tasks.model.TaskList;

/**
 * @author ran
 *
 */
public class TaskListWrapper implements Comparable<TaskListWrapper> {
	
	/**
	 * Inner task list.
	 */
	private TaskList taskList;
	
	/**
	 * Default constructor.
	 */
	public TaskListWrapper() {
		taskList = new TaskList();
	}
	
	/**
	 * Full constructor.
	 * 
	 * @param taskList
	 */
	public TaskListWrapper(TaskList taskList) {
		this.taskList = taskList;
	}
	
	/**
	 * Returns the inner task list.
	 * 
	 * @return
	 */
	public TaskList getTaskList() {
		return taskList;
	}
	
	/**
	 * Sets the inner task list.
	 * 
	 * @param taskList
	 */
	public void setTaskList(TaskList taskList) {
		this.taskList = taskList;
	}
	
	/*
	 * 
	 * Final class Task method delegation.
	 * 
	 */
	
	public String getId() {
		return taskList.getId();
	}
	
	public void setId(String id) {
		taskList.setId(id);
	}
	
	/*
	 * 
	 * Overriding methods
	 * 
	 */

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object lhs) {
		String rhsId = getId();
		
		if (rhsId == null) {
			return false;
		}
		
		String lhsId = ((TaskListWrapper)lhs).getId();
		
		if (lhsId == null) {
			return false;
		}
		
		return rhsId.equals(lhsId);
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(TaskListWrapper another) {
		String rhsId = getId();
		String lhsId = another.getId();
		
		return rhsId.compareTo(lhsId);
	}

}
