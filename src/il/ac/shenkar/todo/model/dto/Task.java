/**
 * 
 */
package il.ac.shenkar.todo.model.dto;

import il.ac.shenkar.todo.controller.activities.R;
import il.ac.shenkar.todo.controller.adapters.Adaptable;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
public class Task implements Adaptable {
	
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
	 * @see il.ac.shenkar.todo.controller.adapters.Adaptable#getLayoutId()
	 */
	@Override
	public int getLayoutId() {
		return R.layout.task_view;
	}
	
	/* (non-Javadoc)
	 * @see il.ac.shenkar.todo.controller.adapters.Adaptable#buildView(android.content.Context)
	 */
	@Override
	public View buildView(View convetView) {
		RelativeLayout holder = new RelativeLayout(convetView.getContext());
		holder.addView( (TextView) convetView.findViewById(R.id.tvDescription) );
		holder.addView( (Button) convetView.findViewById(R.id.bDone) );
		return holder;
	}
	
	/* (non-Javadoc)
	 * @see il.ac.shenkar.todo.controller.adapters.Adaptable#setView(android.view.View)
	 */
	@Override
	public void setView(View convertView) {
		RelativeLayout holder = (RelativeLayout) convertView.getTag();
		TextView tv = (TextView) holder.findViewById(R.id.tvDescription);
		tv.setText(description);
	}

}
