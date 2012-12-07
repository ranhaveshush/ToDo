/**
 * 
 */
package il.ac.shenkar.todo.controller.adapters;

import il.ac.shenkar.todo.controller.activities.R;
import il.ac.shenkar.todo.model.dao.DAOFactory;
import il.ac.shenkar.todo.model.dto.Task;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

/**
 * This class take
 * 
 * @author ran
 * 
 */
public class TasksBaseAdapter extends BaseAdapter {

	/**
	 * Array list of tasks dto objects to dynamically fill the AdapterView
	 * subclasses, ListView or GridView with views.
	 * 
	 */
	private static ArrayList<Task> tasksArrayList = null;

	/**
	 * 
	 */
	private Context context = null;

	/**
	 * 
	 */
	private LayoutInflater inflater = null;

	/**
	 * Full constructor.
	 * 
	 * @param context
	 * @param tasksArrayList
	 */
	public TasksBaseAdapter(Context context, ArrayList<Task> tasksArrayList) {
		TasksBaseAdapter.tasksArrayList = tasksArrayList;
		this.context = context;
		this.inflater = LayoutInflater.from(context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		if (tasksArrayList != null) {
			return tasksArrayList.size();
		} else {
			return 0;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		if (tasksArrayList != null) {
			return tasksArrayList.indexOf(position);
		} else {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		return position;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (null == convertView) {
			convertView = inflater.inflate(R.layout.task_view, null);
			holder = new ViewHolder();
			holder.tv_itemDescription = (TextView) convertView
					.findViewById(R.id.tvDescription);
			holder.b_itemDone = (Button) convertView.findViewById(R.id.bDone);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Task task = tasksArrayList.get(position);

		holder.tv_itemDescription.setText(task.getDescription());
		holder.b_itemDone.setTag(position);
		holder.b_itemDone.setOnClickListener(buttonDoneOnClickListener);

		return convertView;
	}

	private static class ViewHolder {
		TextView tv_itemDescription;
		Button b_itemDone;
	}

	private OnClickListener buttonDoneOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			int position = (Integer) v.getTag();

			// Get task by it's position in the list
			Task task = tasksArrayList.get(position);

			// Remove task from database by it's id
			DAOFactory.getDAOFactory(DAOFactory.SQL_LITE).getTaskDAO(context)
					.deleteTask(task.getId());

			// Remove task from array list by it's position
			tasksArrayList.remove(position);
			notifyDataSetChanged();
		}
	};

}
