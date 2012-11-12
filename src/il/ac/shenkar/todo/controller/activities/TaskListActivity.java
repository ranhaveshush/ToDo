package il.ac.shenkar.todo.controller.activities;

import il.ac.shenkar.todo.controller.adapters.TaskBaseAdapter;
import il.ac.shenkar.todo.model.dao.DAOFactory;
import il.ac.shenkar.todo.model.dto.Task;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

/**
 * @author ran
 * 
 */
public class TaskListActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_list);

		ArrayList<Task> tasksArrayList = DAOFactory
				.getDAOFactory(DAOFactory.SQL_LITE).getTaskDAO().readAllTasks();

		final ListView lv = (ListView) findViewById(R.id.lvTasks);
		lv.setAdapter(new TaskBaseAdapter(this, tasksArrayList));
	}

	/**
	 * Redirects to create task activity to create a task.
	 * 
	 * @param v
	 */
	public void redirectToCreateTaskActivity(View v) {
		Intent intent = new Intent(this, CreateTaskActivity.class);
		startActivity(intent);
	}

}
