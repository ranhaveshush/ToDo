/**
 * 
 */
package il.ac.shenkar.todo.controller.adapters;

import il.ac.shenkar.todo.model.dto.Task;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * @author ran
 *
 */
public class GenericBaseAdapter extends BaseAdapter {
	
	/**
	 * 
	 */
	private LayoutInflater inflater = null;
	
	/**
	 * 
	 */
	private ArrayList<Task> itemsArrayList = null;

	/**
	 * @param context
	 * @param tasksArrayList
	 */
	public GenericBaseAdapter(Context context, ArrayList<Task> tasksArrayList) {
		super();
		this.inflater = LayoutInflater.from(context);
		this.itemsArrayList = tasksArrayList;
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		return (null != itemsArrayList) ? itemsArrayList.size() : 0;
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		return itemsArrayList.indexOf(position);
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		return position;
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (null == convertView) {
			int itemLayoutId = itemsArrayList.get(position).getLayoutId();
			convertView = inflater.inflate(itemLayoutId, null);
			convertView.setTag( itemsArrayList.get(position).buildView(convertView) );
		}
		
		itemsArrayList.get(position).setView(convertView);
		
		return convertView;
	}
	
}
