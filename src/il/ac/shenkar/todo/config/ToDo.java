/**
 * 
 */
package il.ac.shenkar.todo.config;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * @author ran
 *
 */
public final class ToDo {

	// This class cannot be instantiated
	private ToDo() {}
	
	/**
	 * App's package.
	 */
	public static final String PACKAGE = "il.ac.shenkar.todo";
	
	/**
	 * Authentication.
	 * 
	 * @author ran
	 *
	 */
	public static final class Auth {
		
		// This class cannot be instantiated
		private Auth() {}
		
		public static final String AUTHTOKEN_TYPE = PACKAGE + ".authtoken";
		public static final String ACCOUNT_NAME = PACKAGE + ".account";
		public static final String ACCOUNT_TYPE = PACKAGE + ".account";
		
	}
	
	/**
	 * Tasks table contract.
	 * 
	 * @author ran
	 */
	public static final class Tasks implements BaseColumns {
		
		// This class cannot be instantiated
		private Tasks() {}
		
		/**
		 * The authority.
		 */
		public static final String AUTHORITY = "il.ac.shenkar.todo.contentproviders.TasksProvider";
		
		/**
		 * The table name offered by this provider.
		 */
		public static final String TABLE_NAME_TASKS = "tasks";
		
		/*
         * Column definitions.
         * 
         * Taken from the Google Tasks API URL:
         * https://developers.google.com/google-apps/tasks/v1/reference/tasks
         * 
         * For reference on the column names and description.
         */
		
		/**
		 * Column name task's id on the client (sqlite local database).
		 * Task identifier.
		 * 
		 * <P>Type: INTEGER</P>
		 */
		public static final String COLUMN_NAME_CLIENT_ID = "_id";
		
		/**
         * Column name task's kind.
         * Type of the resource. This is always "tasks#task".
         * 
         * <P>Type: TEXT</P>
         */
		public static final String COLUMN_NAME_KIND = "kind";
		
		/**
		 * Column name task's id on the server.
		 * Task identifier.
		 * 
		 * <P>Type: TEXT</P>
		 */
		public static final String COLUMN_NAME_SERVER_ID = "id";
		
		// etag is ommited - ETag of the resource.
		
		/**
         * Column name task's title.
         * Title of the task.
         * 
         * <P>Type: TEXT</P>
         */
		public static final String COLUMN_NAME_TITLE = "title";
		
		/**
         * Column name task's updated.
         * Last modification time of the task (as a RFC 3339 timestamp).
         * 
         * <P>Type: TEXT</P>
         * <P>Constraint: NOT NULL</p>
         */
		public static final String COLUMN_NAME_UPDATED = "updated";
        
		/**
         * Column name task's selfLink.
         * URL pointing to this task.
         * Used to retrieve, update, or delete this task.
         * 
         * <P>Type: TEXT</P>
         */
		public static final String COLUMN_NAME_SELF_LINK = "selfLink";
        
		/**
         * Column name task's parent.
         * Parent task identifier.
         * This field is omitted if it is a top-level task.
         * This field is read-only.
         * Use the "move" method to move the task under a different parent or to the top level.
         * 
         * <P>Type: TEXT</P>
         */
		public static final String COLUMN_NAME_PARENT = "parent";
		
		/**
         * Column name task's position.
         * String indicating the position of the task among its sibling tasks under the same parent task or at the top level.
         * If this string is greater than another task's corresponding position string according to lexicographical ordering,
         * the task is positioned after the other task under the same parent task (or at the top level).
         * This field is read-only. Use the "move" method to move the task to another position.
         * 
         * <P>Type: TEXT</P>
         */
		public static final String COLUMN_NAME_POSITION = "position";
		
		/**
         * Column name task's notes.
         * Notes describing the task. Optional.
         * 
         * <P>Type: TEXT</P>
         */
		public static final String COLUMN_NAME_NOTES = "notes";
		
		/**
         * Column name task's status.
         * Status of the task.
         * This is either "needsAction" or "completed".
         * 
         * <P>Type: TEXT</P>
         */
		public static final String COLUMN_NAME_STATUS = "status";
		
		/**
         * Column name task's due.
         * Due date of the task (as a RFC 3339 timestamp). Optional.
         * 
         * <P>Type: TEXT</P>
         */
		public static final String COLUMN_NAME_DUE = "due";
		
		/**
         * Column name task's completed.
         * Completion date of the task (as a RFC 3339 timestamp).
         * This field is omitted if the task has not been completed.
         * 
         * <P>Type: TEXT</P>
         */
		public static final String COLUMN_NAME_COMPLETED = "completed";
		
		/**
         * Column name task's deleted.
         * Flag indicating whether the task has been deleted.
         * The default if False.
         * true as 1 and false as 0.
         * 
         * <P>Type: INTEGER</P>
         */
		public static final String COLUMN_NAME_DELETED = "deleted";
		
		/**
         * Column name task's hidden.
         * Flag indicating whether the task is hidden.
         * This is the case if the task had been marked completed when the task list was last cleared.
         * The default is False. This field is read-only.
         * true as 1 and false as 0.
         * 
         * <P>Type: INTEGER</P>
         */
		public static final String COLUMN_NAME_HIDDEN = "hidden";
		
		// Links is ommited - Collection of links. This collection is read-only.
		
		/**
		 * Column name task's task list id.
		 * TaskList identifier.
		 * 
		 * <P>Type: TEXT</P>
		 */
		public static final String COLUMN_NAME_TASKLIST_ID = "taskListId";
		
		/**
		 * Column name task's datetime reminder.
		 * Datetime reminder of the task (as a RFC 3339 timestamp).
		 * 
		 * <P>Type: TEXT</P>
		 */
		public static final String COLUMN_NAME_DATETIME_REMINDER = "datetime_reminder";
		
		/**
		 * Column name task's location reminder (Address).
		 * 
		 * <P>Type: TEXT</P>
		 */
		public static final String COLUMN_NAME_LOCATION_REMINDER = "location_reminder";
		
		/*
		 * Column values.
		 */
		
		/**
		 * Column value false as integer 0.
		 * 
		 * <P>Type: INTEGER</P>
		 */
		public static final int COLUMN_VALUE_FALSE = 0;
		
		/**
		 * Column value true as integer 1.
		 * 
		 * <P>Type: INTEGER</P>
		 */
		public static final int COLUMN_VALUE_TRUE = 1;
        
        /*
         * Column positions.
         */
        
        /**
         * Column position for task's id.
         */
        public static final int COLUMN_POSITION_CLIENT_ID = 0;
        
        /**
         * Column position for task's kind.
         */
		public static final int COLUMN_POSITION_KIND = 1;
		
		/**
         * Column position for task's id on the server.
         */
		public static final int COLUMN_POSITION_SERVER_ID = 2;
		
		// etag is ommited - ETag of the resource.
		
		/**
         * Column position for task's title.
         */
		public static final int COLUMN_POSITION_TITLE = 3;
		
		/**
         * Column position for task's updated.
         */
		public static final int COLUMN_POSITION_UPDATED = 4;
        
		/**
         * Column position for task's selfLink.
         */
		public static final int COLUMN_POSITION_SELF_LINK = 5;
        
		/**
         * Column position for task's parent.
         */
		public static final int COLUMN_POSITION_PARENT = 6;
		
		/**
         * Column position for task's position.
         */
		public static final int COLUMN_POSITION_POSITION = 7;
		
		/**
         * Column position for task's notes.
         */
		public static final int COLUMN_POSITION_NOTES = 8;
		
		/**
         * Column position for task's status.
         */
		public static final int COLUMN_POSITION_STATUS = 9;
		
		/**
         * Column position for task's due.
         */
		public static final int COLUMN_POSITION_DUE = 10;
		
		/**
         * Column position for task's completed.
         */
		public static final int COLUMN_POSITION_COMPLETED = 11;
		
		/**
         * Column position for task's deleted.
         */
		public static final int COLUMN_POSITION_DELETED = 12;
		
		/**
         * Column position for task's hidden.
         */
		public static final int COLUMN_POSITION_HIDDEN = 13;
		
		/**
		 * Column position for task's task list id.
		 */
		public static final int COLUMN_POSITION_TASKLIST_ID = 14;
		
		/**
		 * Column position for task's datetime reminder.
		 */
		public static final int CLOUMN_POSITION_DATETIME_REMINDER = 15;
		
		/**
		 * Column position for task's location reminder.
		 */
		public static final int CLOUMN_POSITION_LOCATION_REMINDER = 16;
        
        /*
         * Column defaults content.
         */
		
		/*
		 * URI definitions.
		 */
		
		/**
		 * The scheme part for this provider's URI.
		 */
		public static final String SCHEME = "content://";
		
		/*
		 * Path parts for the URIs.
		 */
		
		/**
		 * Path part for the Tasks URI.
		 */
		public static final String PATH_TASKS = "/tasks";
		
		/**
		 * Path part for the Task ID URI.
		 */
		public static final String PATH_TASK_ID = "/tasks/";
		
		/**
         * 0-relative position of a task ID segment in the path part of a task ID URI.
         */
        public static final int TASK_ID_PATH_POSITION = 1;
		
		/**
		 * The content:// style URL for this table.
		 */
		public static final Uri CONTENT_URI = Uri.parse(SCHEME + AUTHORITY + PATH_TASKS);
		
		/**
		 * The content URI base for a single task. Callers must
         * append a numeric task id to this Uri to retrieve a task.
		 */
		public static final Uri CONTENT_ID_URI_BASE = Uri.parse(SCHEME + AUTHORITY + PATH_TASK_ID);
		
		/**
		 * The content URI match pattern for a single task, specified by its ID. Use this to match
         * incoming URIs or to construct an Intent.
		 */
		public static final Uri CONTENT_ID_URI_PATTERN = Uri.parse(SCHEME + AUTHORITY + PATH_TASK_ID + "/#");
		
		/*
         * MIME type definitions.
         */
		
        /**
         * The MIME type of {@link #CONTENT_URI} providing a directory of tasks.
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/List<com.google.api.services.tasks.model.Task>";

        /**
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single
         * task.
         */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/com.google.api.services.tasks.model.Task";
        
        /*
         * Sort Order definitions.
         */
        
        /**
         * Order by task's position.
         */
        public static final String SORT_BY_POSITION = COLUMN_NAME_POSITION + " ASC";
        
        /**
         * Order by task's title.
         */
        public static final String SORT_BY_TITLE = COLUMN_NAME_TITLE + " ASC";
        
        /**
         * Order by task's due.
         */
        public static final String SORT_BY_DUE = COLUMN_NAME_DUE + " ASC";

        /**
         * The default sort order for this table.
         */
        public static final String DEFAULT_SORT_ORDER = SORT_BY_POSITION;
        
        /*
         * Creates a projection that returns the task ID and the task contents.
         */
        public static final String[] PROJECTION =
            new String[] {
                ToDo.Tasks.COLUMN_NAME_CLIENT_ID,
                ToDo.Tasks.COLUMN_NAME_KIND,
                ToDo.Tasks.COLUMN_NAME_SERVER_ID,
                ToDo.Tasks.COLUMN_NAME_TITLE,
                ToDo.Tasks.COLUMN_NAME_UPDATED,
                ToDo.Tasks.COLUMN_NAME_SELF_LINK,
                ToDo.Tasks.COLUMN_NAME_PARENT,
                ToDo.Tasks.COLUMN_NAME_POSITION,
                ToDo.Tasks.COLUMN_NAME_NOTES,
                ToDo.Tasks.COLUMN_NAME_STATUS,
                ToDo.Tasks.COLUMN_NAME_DUE,
                ToDo.Tasks.COLUMN_NAME_COMPLETED,
                ToDo.Tasks.COLUMN_NAME_DELETED,
                ToDo.Tasks.COLUMN_NAME_HIDDEN,
                ToDo.Tasks.COLUMN_NAME_TASKLIST_ID,
                ToDo.Tasks.COLUMN_NAME_DATETIME_REMINDER,
                ToDo.Tasks.COLUMN_NAME_LOCATION_REMINDER
        };
		
	}
	
	/**
	 * Preferences.
	 * 
	 * @author ran
	 *
	 */
	public static final class Prefs {
		
		// This class cannot be instantiated
		private Prefs() {}
		
		/**
		 * Preferences authentication info file name.
		 */
		public static final String PREFS_FILE_AUTH = "prefsFileAuth";
		
		/**
		 * Preference for the google account name to sync Google Tasks with.
		 */
		public static final String PREF_ACCOUNT_NAME = "prefAccountName";
		
		/**
		 * Preference for the auth token to Google Tasks Service.
		 */
		public static final String PREF_AUTH_TOKEN = "prefAuthToken";
		
	}
	
	/**
	 * Intent actions contact.
	 * 
	 * @author ran
	 *
	 */
	public static final class Actions {
		
		// This class cannot be instantiated
		private Actions() {}
		
		/**
		 * Action for the user to select Google account to sync with.
		 */
		public static final String ACTION_SELECT_ACCOUNT = "il.ac.shenkar.todo.ACTION_SELECT_ACCOUNT";
		
		/**
		 * Action to edit existing task.
		 */
		public static final String ACTION_EDIT_EXISTING_TASK = "il.ac.shenkar.todo.ACTION_EDIT_EXISTING_TASK";
		
		/**
		 * Action to edit new task.
		 */
		public static final String ACTION_EDIT_NEW_TASK = "il.ac.shenkar.todo.ACTION_EDIT_NEW_TASK";
		
		/**
		 * Action to get a datetime reminder broadcast.
		 */
		public static final String ACTION_DATETIME_REMINDER_BROADCAST = "il.ac.shenkar.todo.ACTION_DATETIME_REMINDER_BROADCAST";
		
		/**
		 * Action to get a location reminder broadcast.
		 */
		public static final String ACTION_LOCATION_REMINDER_BROADCAST = "il.ac.shenkar.todo.ACTION_LOCATION_REMINDER_BROADCAST";
		
	}
	
	/**
	 * Intent Extras contract.
	 * 
	 * @author ran
	 *
	 */
	public static final class Extras {
		
		// This class cannot be instantiated
		private Extras() {}
		
		/**
		 * Extra key for task's id.
		 * <P>Type: String</P>
		 */
		public static final String EXTRA_TASK_ID = "il.ac.shenkar.todo.EXTRA_TASK_ID";
		
		/**
		 * Extra key for task list id.
		 * <P>Type: String</P>
		 */
		public static final String EXTRA_TASK_LIST_ID = "il.ac.shenkar.todo.EXTRA_TASK_LIST_ID";
		
		/**
		 * Extra key for task's title.
		 * <P>Type: String</P>
		 */
		public static final String EXTRA_TASK_TITLE = "il.ac.shenkar.todo.EXTRA_TASK_TITLE";
		
		/**
		 * Extra key for task's description.
		 * <P>Type: String</P>
		 */
		public static final String EXTRA_TASK_DESCRIPTION = "il.ac.shenkar.todo.EXTRA_TASK_DESCRIPTION";
		
		/**
		 * Extra key for task's datetime in milliseconds from 1.1.1970.
		 * <P>Type: long</P>
		 */
		public static final String EXTRA_TASK_DATETIME_MILLISEC = "il.ac.shenkar.todo.EXTRA_TASK_DATETIME_MILLISEC";
		
		/**
		 * Extra key for reminder's id.
		 * <P>Type: int</P>
		 */
		public static final String EXTRA_REMINDER_ID = "il.ac.shenkar.todo.EXTRA_REMINDER_ID";
		
	}
	
	/**
	 * DialogFragment Arguments contract.
	 * 
	 * @author ran
	 *
	 */
	public static final class Args {
		
		// This class cannot be instantiated
		private Args() {}
		
		/**
		 * Argument key for the account picker dialog title.
		 * <P>Type: int</P>
		 */
		public static final String ARG_ACCOUNT_PICKER_DIALOG_TITLE = "il.ac.shenkar.todo.ARG_ACCOUT_PICKER_DIALOG_TITLE";
		
		/**
		 * Argument key for the tasks list identifier.
		 * <P>Type: String</P>
		 */
		public static final String ARG_TASKS_LIST_ID = "il.ac.shenkar.todo.ARG_TASKS_LIST_ID";
	}
	
	/**
	 * Request codes for activity for results.
	 * 
	 * @author ran
	 *
	 */
	public static final class Requests {
		
		/**
		 * Request code for google play services recoverable error handle by user.
		 */
		public static final int REQUEST_GOOGLE_PLAY_SERVICES = 0;
		
		/**
		 * Request code for user autorization.
		 */
		public static final int REQUEST_AUTHORIZATION = 1;
		
		/**
		 * Request code for the result from the account picker,
		 * after the user selected or not selected.
		 */
		public static final int REQUEST_ACCOUNT_PICKER = 2;
		
	}

}
