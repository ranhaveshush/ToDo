package il.ac.shenkar.todo.controller.activities;

import il.ac.shenkar.todo.controller.adapters.TasksBaseAdapter;
import il.ac.shenkar.todo.model.dao.DAOFactory;
import il.ac.shenkar.todo.model.dto.Task;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

/**
 * @author ran
 * 
 */
public class TasksListActivity extends Activity {

	/**
	 * Logger tag.
	 */
	private static final String TAG = "TaskListActivity";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Logger
		Log.d(TAG, "onCreate()");

		setContentView(R.layout.activity_tasks_list);

		// Get all tasks from the database.
		ArrayList<Task> tasksArrayList = DAOFactory
				.getDAOFactory(DAOFactory.SQL_LITE).getTaskDAO(this)
				.readAllTasks();

		// Get listview widget
		ListView lv = (ListView) findViewById(R.id.lvTasks);
		TasksBaseAdapter tasksBaseAdapter = new TasksBaseAdapter(this,
				tasksArrayList);
		lv.setAdapter(tasksBaseAdapter);

		// Get button widget
		Button buttonAddTask = (Button) findViewById(R.id.bAddTask);
		buttonAddTask.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Redirect to CreateTaskActivity
				Intent intent = new Intent(TasksListActivity.this,
						CreateTaskActivity.class);
				startActivity(intent);
			}
		});
	}

}
