/**
 * 
 */
package il.ac.shenkar.todo.model.dto;

import java.util.List;

import com.google.api.client.util.DateTime;
import com.google.api.services.tasks.model.Task;

/**
 * @author ran
 *
 */
public class TaskWrapper implements Comparable<TaskWrapper> {
	
	/**
	 * Inner task
	 */
	private Task task;
	
	/**
	 * Default constructor.
	 */
	public TaskWrapper() {
		task = new Task();
	}
	
	/**
	 * Full constructor.
	 * 
	 * @param task
	 */
	public TaskWrapper(Task task) {
		this.task = task;
	}
	
	/**
	 * Returns the inner task.
	 * 
	 * @return
	 */
	public Task getTask() {
		return task;
	}
	
	/**
	 * Sets the inner task.
	 * 
	 * @param task
	 */
	public void setTask(Task task) {
		this.task = task;
	}
	
	/*
	 * 
	 * Final class Task method delegation.
	 * 
	 */
	
	public DateTime getCompleted() {
		return task.getCompleted();
	}
	
	public Boolean getDeleted() {
		return task.getDeleted();
	}
	
	public DateTime getDue() {
		return task.getDue();
	}
	
	public String Etag() {
		return task.getEtag();
	}
	
	public Boolean getHidBoolean() {
		return task.getHidden();
	}
	
	public String getId() {
		return task.getId();
	}
	
	public String getKind() {
		return task.getKind();
	}
	
	public List<Task.Links> getLinks() {
		return task.getLinks();
	}
	
	public String getNotes() {
		return task.getNotes();
	}
	
	public String getParent() {
		return task.getParent();
	}
	
	public String getPosition() {
		return task.getPosition();
	}
	
	public String getSelfLink() {
		return task.getSelfLink();
	}
	
	public String getStatus() {
		return task.getStatus();
	}
	
	public String getTitle() {
		return task.getTitle();
	}
	
	public DateTime getUpdated() {
		return task.getUpdated();
	}
	
	public Task setCompleted(DateTime compeleted) {
		return task.setCompleted(compeleted);
	}
	
	public Task setDeleted(Boolean deleted) {
		return task.setDeleted(deleted);
	}
	
	public Task setDue(DateTime due) {
		return task.setDue(due);
	}
	
	public Task setEtag(String etag) {
		return task.setEtag(etag);
	}
	
	public Task setHidden(Boolean hidden) {
		return task.setHidden(hidden);
	}
	
	public Task setId(String id) {
		return task.setId(id);
	}
	
	public Task setKind(String kind) {
		return task.setKind(kind);
	}
	
	public Task setLinks(List<Task.Links> links) {
		return task.setLinks(links);
	}
	
	public Task setNotes(String notes) {
		return task.setNotes(notes);
	}
	
	public Task setParent(String parent) {
		return task.setParent(parent);
	}
	
	public Task setPosition(String position) {
		return task.setPosition(position);
	}
	
	public Task setSelfLink(String selfLink) {
		return task.setSelfLink(selfLink);
	}
	
	public Task setStatus(String status) {
		return task.setStatus(status);
	}
	
	public Task setTitle(String title) {
		return task.setTitle(title);
	}
	
	public Task setUpdated(DateTime updated) {
		return task.setUpdated(updated);
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
		
		String lhsId = ((TaskWrapper)lhs).getId();
		
		if (lhsId == null) {
			return false;
		}
		
		return rhsId.equals(lhsId);
	}

	@Override
	public int compareTo(TaskWrapper another) {
		String rhsId = getId();
		String lhsId = another.getId();
		
		return rhsId.compareTo(lhsId);
	}
	

}
