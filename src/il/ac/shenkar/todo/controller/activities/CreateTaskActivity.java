package il.ac.shenkar.todo.controller.activities;

import il.ac.shenkar.todo.model.dao.DAOFactory;
import il.ac.shenkar.todo.model.dto.Task;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class CreateTaskActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_task);

		// Get the button widget
		Button buttonCreateTask = (Button) findViewById(R.id.bCreate);
		buttonCreateTask.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				createTask();
			}
		});
	}

	/**
	 * Create a new task and returns to the parent activity (ListTaskActivity).
	 */
	public void createTask() {
		// Gets the task description
		EditText editTextDescription = (EditText) findViewById(R.id.etDescription);

		// Create a new task
		Task task = new Task(editTextDescription.getText().toString());

		// Presist the new task
		DAOFactory.getDAOFactory(DAOFactory.SQL_LITE).getTaskDAO(this)
				.createTask(task);

		// Redirect to TaskListActivity
		Intent intent = new Intent(CreateTaskActivity.this,
				TasksListActivity.class);
		startActivity(intent);
	}

}