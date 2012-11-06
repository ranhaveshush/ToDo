package il.ac.shenkar.todo.controller.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

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
    	Intent intent = new Intent(this, TaskListActivity.class);
    	startActivity(intent);
    }

}
