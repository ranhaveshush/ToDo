/**
 * 
 */
package il.ac.shenkar.todo.model.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author ran
 *
 */
public class TasksOpenHelper extends SQLiteOpenHelper {

	/**
	 * Database version.
	 */
	private static final int DATABASE_VERSION = 2;
	
	/**
	 * Database name.
	 */
	private static final String DATABASE_NAME = "tasksDatabase";
	
	/**
	 * Tasks table name.
	 */
	public static final String TABLE_TASKS = "tasks";
	
	/**
	 * Tasks table column id.
	 */
	public static final String KEY_ID = "_id";
	
	/**
	 * Tasks table column description.
	 */
	public static final String KEY_DESCRIPTION = "description";
	
	/**
	 * Create Tasks table SQL.
	 */
	private static final String CREATE_TABLE_TASKS = 
			"CREATE TABLE " + TABLE_TASKS + "("
			+ KEY_ID + " INTEGER PRIMARY KEY,"
			+ KEY_DESCRIPTION + " TEXT)";
	
	/**
	 * Drop Tasks table SQL.
	 */
	private static final String DROP_TABLE_TASKS =
			"DROP TABLE IF EXISTS " + TABLE_TASKS;
	
	/**
	 * Full Constructor.
	 */
	public TasksOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_TASKS);
	}

	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL(DROP_TABLE_TASKS);
		
		// Create table again
		onCreate(db);
	}

}
