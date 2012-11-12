package il.ac.shenkar.todo.controller.activities;

import il.ac.shenkar.todo.model.dao.DAOFactory;
import il.ac.shenkar.todo.model.dto.Task;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class CreateTaskActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);
    }

    /**
     * Create's a new task and returns to the parent activity (ListTaskActivity).
     * 
     * @param v
     */
    public void createTask(View v) {
    	EditText etDescription = (EditText) findViewById(R.id.etDescription);
    	ArrayList<Task> tasksArrayList = DAOFactory
				.getDAOFactory(DAOFactory.SQL_LITE).getTaskDAO().readAllTasks();
    	tasksArrayList.add( new Task(0,etDescription.getText().toString()) );
    	Intent intent = new Intent(this, TaskListActivity.class);
    	startActivity(intent);
    }

}
