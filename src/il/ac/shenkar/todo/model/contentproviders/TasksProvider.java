/**
 * 
 */
package il.ac.shenkar.todo.model.contentproviders;

import il.ac.shenkar.todo.config.ToDo;

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

/**
 * @author ran
 * 
 */
public class TasksProvider extends ContentProvider {

	/**
	 * Tag for debugging and logging.
	 */
	private static final String TAG = "TasksProvider";

	/*
	 * Constants used by the Uri matcher to choose an action based on the
	 * pattern of the incoming URI
	 */

	/**
	 * The incoming URI matches the Tasks URI pattern.
	 */
	private static final int TASKS = 1;

	/**
	 * The incoming URI matches the Task ID URI pattern.
	 */
	private static final int TASK_ID = 2;

	/**
	 * A UriMatcher instance.
	 */
	private static final UriMatcher uriMatcher;

	/**
	 * A projection map used to select columns from the database.
	 */
	private static HashMap<String, String> tasksProjectionMap;

	/**
	 * A block that instantiates and sets static objects.
	 */
	static {
		/*
		 * Creates and initializes the URI matcher.
		 */

		// Create a new instance
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

		// Add a pattern that routes URIs terminated with "tasks" to a TASKS
		// operation
		uriMatcher.addURI(ToDo.Tasks.AUTHORITY, "tasks", TASKS);

		// Add a pattern that routes URIs terminated with "tasks" plus an
		// integer to a task ID operation
		uriMatcher.addURI(ToDo.Tasks.AUTHORITY, "tasks/#", TASK_ID);

		/*
		 * Creates and initializes a projection map that returns all columns.
		 */

		// Creates a new projection map instance. The map returns a column name
		// given a string. The two are usually equal.
		tasksProjectionMap = new HashMap<String, String>();
		
		// Maps all tasks table columns
		tasksProjectionMap.put(ToDo.Tasks.COLUMN_NAME_CLIENT_ID, ToDo.Tasks.COLUMN_NAME_CLIENT_ID);
		tasksProjectionMap.put(ToDo.Tasks.COLUMN_NAME_KIND, ToDo.Tasks.COLUMN_NAME_KIND);
		tasksProjectionMap.put(ToDo.Tasks.COLUMN_NAME_SERVER_ID, ToDo.Tasks.COLUMN_NAME_SERVER_ID);
		tasksProjectionMap.put(ToDo.Tasks.COLUMN_NAME_TITLE, ToDo.Tasks.COLUMN_NAME_TITLE);
		tasksProjectionMap.put(ToDo.Tasks.COLUMN_NAME_UPDATED, ToDo.Tasks.COLUMN_NAME_UPDATED);
		tasksProjectionMap.put(ToDo.Tasks.COLUMN_NAME_SELF_LINK, ToDo.Tasks.COLUMN_NAME_SELF_LINK);
		tasksProjectionMap.put(ToDo.Tasks.COLUMN_NAME_PARENT, ToDo.Tasks.COLUMN_NAME_PARENT);
		tasksProjectionMap.put(ToDo.Tasks.COLUMN_NAME_POSITION, ToDo.Tasks.COLUMN_NAME_POSITION);
		tasksProjectionMap.put(ToDo.Tasks.COLUMN_NAME_NOTES, ToDo.Tasks.COLUMN_NAME_NOTES);
		tasksProjectionMap.put(ToDo.Tasks.COLUMN_NAME_STATUS, ToDo.Tasks.COLUMN_NAME_STATUS);
		tasksProjectionMap.put(ToDo.Tasks.COLUMN_NAME_DUE, ToDo.Tasks.COLUMN_NAME_DUE);
		tasksProjectionMap.put(ToDo.Tasks.COLUMN_NAME_COMPLETED, ToDo.Tasks.COLUMN_NAME_COMPLETED);
		tasksProjectionMap.put(ToDo.Tasks.COLUMN_NAME_DELETED, ToDo.Tasks.COLUMN_NAME_DELETED);
		tasksProjectionMap.put(ToDo.Tasks.COLUMN_NAME_HIDDEN, ToDo.Tasks.COLUMN_NAME_HIDDEN);
		tasksProjectionMap.put(ToDo.Tasks.COLUMN_NAME_TASKLIST_ID, ToDo.Tasks.COLUMN_NAME_TASKLIST_ID);
		tasksProjectionMap.put(ToDo.Tasks.COLUMN_NAME_DATETIME_REMINDER, ToDo.Tasks.COLUMN_NAME_DATETIME_REMINDER);
		tasksProjectionMap.put(ToDo.Tasks.COLUMN_NAME_LOCATION_REMINDER, ToDo.Tasks.COLUMN_NAME_LOCATION_REMINDER);
	}

	/**
	 * Handle a new DatabaseHelper.
	 */
	private DatabaseHelper databaseHelper = null;

	/**
	 * @author ran
	 *
	 */
	private static class DatabaseHelper extends SQLiteOpenHelper {

		/**
		 * The database that the provider uses as its underlying data store.
		 */
		public static final String DATABASE_NAME = "tasks.db";

		/**
		 * The database version.
		 */
		public static final int DATABASE_VERSION = 33;
		
		/**
		 * Creates table tasks SQL.
		 */
		private static final String CREATE_TABLE_TASKS =
				"CREATE TABLE " + ToDo.Tasks.TABLE_NAME_TASKS + "("
				+ ToDo.Tasks.COLUMN_NAME_CLIENT_ID + " INTEGER PRIMARY KEY NOT NULL, "
				+ ToDo.Tasks.COLUMN_NAME_KIND + " TEXT, "
				+ ToDo.Tasks.COLUMN_NAME_SERVER_ID + " TEXT, "
				+ ToDo.Tasks.COLUMN_NAME_TITLE + " TEXT, "
				+ ToDo.Tasks.COLUMN_NAME_UPDATED + " TEXT NOT NULL, "
				+ ToDo.Tasks.COLUMN_NAME_SELF_LINK + " TEXT, "
				+ ToDo.Tasks.COLUMN_NAME_PARENT + " TEXT, "
				+ ToDo.Tasks.COLUMN_NAME_POSITION + " TEXT, "
				+ ToDo.Tasks.COLUMN_NAME_NOTES + " TEXT, "
				+ ToDo.Tasks.COLUMN_NAME_STATUS + " TEXT, "
				+ ToDo.Tasks.COLUMN_NAME_DUE + " TEXT, "
				+ ToDo.Tasks.COLUMN_NAME_COMPLETED + " TEXT, "
				+ ToDo.Tasks.COLUMN_NAME_DELETED + " INTEGER, "
				+ ToDo.Tasks.COLUMN_NAME_HIDDEN + " INTEGER, "
				+ ToDo.Tasks.COLUMN_NAME_TASKLIST_ID + " TEXT, "
				+ ToDo.Tasks.COLUMN_NAME_DATETIME_REMINDER + " TEXT, "
				+ ToDo.Tasks.COLUMN_NAME_LOCATION_REMINDER + " TEXT);";
		
		/**
		 * Drops table tasks SQL.
		 */
		private static final String DROP_TABLE_TASKS = 
				"DROP TABLE IF EXISTS " + ToDo.Tasks.TABLE_NAME_TASKS;
				

		/**
		 * Full constructor.
		 * 
		 * @param context
		 */
		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// Creates the tasks table
			db.execSQL(CREATE_TABLE_TASKS);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// Logs that the database is being upgraded
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");

			// Drops the table tasks and existing data
			db.execSQL(DROP_TABLE_TASKS);

			// Recreates the database with a new version
			onCreate(db);
		}

	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#onCreate()
	 */
	@Override
	public boolean onCreate() {
		// Creates a new helper object. Note that the database itself isn't opened until
		// something tries to access it, and it's only created if it doesn't already exist.
		databaseHelper = new DatabaseHelper(getContext());

		// Assumes that any failures will be reported by a thrown exception.
		return true;
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#query(android.net.Uri, java.lang.String[], java.lang.String, java.lang.String[], java.lang.String)
	 */
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		Log.d(TAG, "query(" + uri + ",...");
		
		// Validates the incoming URI pattern
		if (uriMatcher.match(uri) != TASKS) {
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		
		// Constructs a new query builder and sets its table name and projection
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(ToDo.Tasks.TABLE_NAME_TASKS);
		qb.setProjectionMap(tasksProjectionMap);

		String orderBy;
		// If no sort order is specified, uses the default
		if (TextUtils.isEmpty(sortOrder)) {
			orderBy = ToDo.Tasks.DEFAULT_SORT_ORDER;
		} else {
			// otherwise, uses the incoming sort order
			orderBy = sortOrder;
		}

		// Opens the database object in "read" mode,
		// since no writes need to be done.
		SQLiteDatabase db = databaseHelper.getReadableDatabase();

		/*
		 * Performs the query. If no problems occur trying to read the database,
		 * then a Cursor object is returned; otherwise, the cursor variable
		 * contains null. If no records were selected, then the Cursor object is
		 * empty, and Cursor.getCount() returns 0.
		 */
		Cursor c = qb.query(db, // The database to query
				projection, // The columns to return from the query
				selection, // The columns for the where clause
				selectionArgs, // The values for the where clause
				null, // don't group the rows
				null, // don't filter by row groups
				orderBy // The sort order
				);

		// Tells the Cursor what URI to watch,
		// so it knows when its source data changes.
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#getType(android.net.Uri)
	 */
	@Override
	public String getType(Uri uri) {
		Log.d(TAG, "getType(" + uri + ",...");
		
		// Chooses the MIME type based on the incoming URI pattern
		switch (uriMatcher.match(uri)) {
		case TASKS:
			return ToDo.Tasks.CONTENT_TYPE;
		case TASK_ID:
			return ToDo.Tasks.CONTENT_ITEM_TYPE;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#insert(android.net.Uri, android.content.ContentValues)
	 */
	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		Log.d(TAG, "insert(" + uri + ",...)");

		// Validates the incoming URI pattern
		if (uriMatcher.match(uri) != TASKS) {
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		// Validates the content values
		if (initialValues == null) {
			return null;
		}

		// Opens the database object in "read/write" mode, 
		// since writes need to be done.
		SQLiteDatabase db = databaseHelper.getWritableDatabase();

		long insertedRowId = db.insert(ToDo.Tasks.TABLE_NAME_TASKS, null, initialValues);

		// If insert failed, throw exception
		if (insertedRowId == -1) {
			throw new SQLException("Failed insert row into " + uri);
		}

		// If insert succedded return the new URI
		Uri taskUri = ContentUris.withAppendedId(
				ToDo.Tasks.CONTENT_ID_URI_BASE, insertedRowId);

		getContext().getContentResolver().notifyChange(taskUri, null);

		return taskUri;
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#delete(android.net.Uri, java.lang.String, java.lang.String[])
	 */
	@Override
	public int delete(Uri uri, String initialWhereClause, String[] whereArgs) {
		Log.d(TAG, "delete(" + uri + ",...)");

		String finalWhereClause = null;

		// Edits the where clause based on the incoming URI pattern
		switch (uriMatcher.match(uri)) {
		case TASKS:
			finalWhereClause = initialWhereClause;
			break;
		case TASK_ID:
			finalWhereClause = ToDo.Tasks.COLUMN_NAME_CLIENT_ID + "=" + ContentUris.parseId(uri);
			if (initialWhereClause != null) {
				finalWhereClause = finalWhereClause + " AND " + initialWhereClause;
			}
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		// Opens the database object in "read/write" mode, 
		// since writes need to be done.
		SQLiteDatabase db = databaseHelper.getWritableDatabase();

		int numOfRowsAffected = db.delete(ToDo.Tasks.TABLE_NAME_TASKS, finalWhereClause, whereArgs);

		getContext().getContentResolver().notifyChange(uri, null);

		return numOfRowsAffected;
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#update(android.net.Uri, android.content.ContentValues, java.lang.String, java.lang.String[])
	 */
	@Override
	public int update(Uri uri, ContentValues values, String initialWhereClause, String[] whereArgs) {
		Log.d(TAG, "update(" + uri + ",...)");
		
		String finalWhereClause = null;
		
		// Edits the where clause based on the incoming URI pattern
		switch (uriMatcher.match(uri)) {
		case TASKS:
			finalWhereClause = initialWhereClause;
			break;
		case TASK_ID:
			finalWhereClause = ToDo.Tasks.COLUMN_NAME_CLIENT_ID + "=" + ContentUris.parseId(uri);
			if (initialWhereClause != null) {
				finalWhereClause = finalWhereClause + " AND " + initialWhereClause;
			}
			break;
		default:
			throw new IllegalArgumentException("Unkown URI " + uri);
		}
		
		// Opens the database object in "read/write" mode, 
		// since writes need to be done.
		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		
		int numOfRowsAffected = db.update(ToDo.Tasks.TABLE_NAME_TASKS, values, finalWhereClause, whereArgs);
		
		getContext().getContentResolver().notifyChange(uri, null);
		
		return numOfRowsAffected;
	}

}
