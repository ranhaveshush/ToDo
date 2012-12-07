/**
 * 
 */
package il.ac.shenkar.todo.controller.adapters;

import il.ac.shenkar.todo.controller.activities.R;
import il.ac.shenkar.todo.model.dao.DAOFactory;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * @author ran
 * 
 */
public class TasksCursorAdapter extends CursorAdapter {

	/**
	 * 
	 */
	private Context context = null;

	/**
	 * 
	 */
	private LayoutInflater inflater = null;

	/**
	 * 
	 */
	private OnClickListener doneOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			long taskId = (Long) view.getTag();
			DAOFactory.getDAOFactory(DAOFactory.SQL_LITE).getTaskDAO(context)
					.deleteTask(taskId);
		}
	};

	/**
	 * Full constructor.
	 * 
	 * @param context
	 * @param cursor
	 * @param flags
	 */
	public TasksCursorAdapter(Context context, Cursor cursor, int flags) {
		super(context, cursor, flags);
		inflater = LayoutInflater.from(context);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		TextView tvDescription = (TextView) view
				.findViewById(R.id.tvDescription);
		Button bDone = (Button) view.findViewById(R.id.bDone);

		tvDescription.setText(cursor.getString(1));
		bDone.setTag(cursor.getLong(0));
		bDone.setOnClickListener(doneOnClickListener);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return inflater.inflate(R.layout.task_view, parent, false);
	}

}
