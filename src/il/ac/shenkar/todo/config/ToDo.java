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
		 * UNKNOWN FIELD:
		 * 
		 * Column name task's id on the client (sqlite local database).
		 * Task identifier.
		 * 
		 * <P>Type: INTEGER</P>
		 * <P>Constraint: NOT NULL</p>
		 * <P>Constraint: PRIMARY KEY</p> 
		 */
		public static final String COLUMN_NAME_CLIENT_ID = "_id";
		
		/**
		 * Column name task's id on the server.
		 * Task identifier.
		 * 
		 * <P>Type: TEXT</P>
		 */
		public static final String COLUMN_NAME_SERVER_ID = "id";
		
		/**
         * Column name task's kind.
         * Type of the resource. This is always "tasks#task".
         * 
         * <P>Type: TEXT</P>
         */
		public static final String COLUMN_NAME_KIND = "kind";
		
		// etag is ommited - ETag of the resource.
		
		/**
         * Column name task's title.
         * Title of the task.
         * 
         * <P>Type: TEXT</P>
         * <P>Constraint: NOT NULL</p>
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
		 * UNKNOWN FIELD:
		 * 
		 * Column name task's previos.
		 * The previous task client id.
		 * Needed for the move method of Google Tasks API.
		 * 
		 * <P>Type: TEXT</P>
		 */
		public static final String COLUMN_NAME_PREVIOUS = "previous";
		
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
		 * UNKNOWN FIELD:
		 * 
		 * Column name task's moved.
		 * Flag indicating whether the task was moved.
		 * True as 1 and false as 0.
		 * 
		 * <P>Type: INTEGER</P>
		 */
		public static final String COLUMN_NAME_MOVED = "moved";
		
		/**
         * Column name task's deleted.
         * Flag indicating whether the task has been deleted.
         * The default if False.
         * True as 1 and false as 0.
         * 
         * <P>Type: INTEGER</P>
         */
		public static final String COLUMN_NAME_DELETED = "deleted";
		
		/**
         * Column name task's hidden.
         * Flag indicating whether the task is hidden.
         * This is the case if the task had been marked completed when the task list was last cleared.
         * The default is False. This field is read-only.
         * True as 1 and false as 0.
         * 
         * <P>Type: INTEGER</P>
         */
		public static final String COLUMN_NAME_HIDDEN = "hidden";
		
		// Links are ommited - Collection of links. This collection is read-only.
		
		/**
 		 * UNKNOWN FIELD:
		 * 
		 * Column name task's task list client id.
		 * TaskList client identifier.
		 * 
		 * <P>Type: INTEGER</P>
		 * <P>Constraint: NOT NULL</p>
		 * <P>Constraint: FOREIGN KEY</p>
		 */
		public static final String COLUMN_NAME_TASK_LIST_CLIENT_ID = "taskListClientId";
		
		/**
		 * UNKNOWN FIELD:
		 * 
		 * Column name task's datetime reminder.
		 * Datetime reminder of the task (as a RFC 3339 timestamp).
		 * 
		 * <P>Type: TEXT</P>
		 */
		public static final String COLUMN_NAME_DATETIME_REMINDER = "datetime_reminder";
		
		/**
		 * UNKNOWN FIELD:
		 * 
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
		
		/**
		 * Column value for the status column task that needs action.
		 * 
		 * <P>Type: TEXT</P>
		 */
		public static final String COLUMN_STATUS_NEEDS_ACTION = "needsAction";
		
		/**
		 * Column value for the status column task that completed.
		 * 
		 * <P>Type: TEXT</P>
		 */
		public static final String COLUMN_STATUS_COMPLETED = "completed";
        
        /*
         * Column positions.
         */
        
        /**
         * UNKNOWN FIELD:
         * Column position for task's client id.
         */
        public static final int COLUMN_POSITION_CLIENT_ID = 0;
        
        /**
         * Column position for task's id on the server.
         */
        public static final int COLUMN_POSITION_SERVER_ID = 1;
        
        /**
         * Column position for task's kind.
         */
		public static final int COLUMN_POSITION_KIND = 2;
		
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
		 * UNKNOWN FIELD:
		 * Column position for task's previous.
		 */
		public static final int COLUMN_POSITION_PREVIOUS = 7;
		
		/**
         * Column position for task's position.
         */
		public static final int COLUMN_POSITION_POSITION = 8;
		
		/**
         * Column position for task's notes.
         */
		public static final int COLUMN_POSITION_NOTES = 9;
		
		/**
         * Column position for task's status.
         */
		public static final int COLUMN_POSITION_STATUS = 10;
		
		/**
         * Column position for task's due.
         */
		public static final int COLUMN_POSITION_DUE = 11;
		
		/**
         * Column position for task's completed.
         */
		public static final int COLUMN_POSITION_COMPLETED = 12;
		
		/**
		 * UNKNOWN FIELD:
		 * Column position for task's moved.
		 */
		public static final int COLUMN_POSITION_MOVED = 13;
		
		/**
         * Column position for task's deleted.
         */
		public static final int COLUMN_POSITION_DELETED = 14;
		
		/**
         * Column position for task's hidden.
         */
		public static final int COLUMN_POSITION_HIDDEN = 15;
		
		/**
		 * UNKNOWN FIELD:
		 * Column position for task's task list client id.
		 */
		public static final int COLUMN_POSITION_TASK_LIST_CLIENT_ID = 16;
		
		/**
		 * UNKNOWN FIELD:
		 * Column position for task's datetime reminder.
		 */
		public static final int COLUMN_POSITION_DATETIME_REMINDER = 17;
		
		/**
		 * UNKNOWN FIELD:
		 * Column position for task's location reminder.
		 */
		public static final int COLUMN_POSITION_LOCATION_REMINDER = 18;
        
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
                ToDo.Tasks.COLUMN_NAME_SERVER_ID,
                ToDo.Tasks.COLUMN_NAME_KIND,
                ToDo.Tasks.COLUMN_NAME_TITLE,
                ToDo.Tasks.COLUMN_NAME_UPDATED,
                ToDo.Tasks.COLUMN_NAME_SELF_LINK,
                ToDo.Tasks.COLUMN_NAME_PARENT,
                ToDo.Tasks.COLUMN_NAME_PREVIOUS,
                ToDo.Tasks.COLUMN_NAME_POSITION,
                ToDo.Tasks.COLUMN_NAME_NOTES,
                ToDo.Tasks.COLUMN_NAME_STATUS,
                ToDo.Tasks.COLUMN_NAME_DUE,
                ToDo.Tasks.COLUMN_NAME_COMPLETED,
                ToDo.Tasks.COLUMN_NAME_MOVED,
                ToDo.Tasks.COLUMN_NAME_DELETED,
                ToDo.Tasks.COLUMN_NAME_HIDDEN,
                ToDo.Tasks.COLUMN_NAME_TASK_LIST_CLIENT_ID,
                ToDo.Tasks.COLUMN_NAME_DATETIME_REMINDER,
                ToDo.Tasks.COLUMN_NAME_LOCATION_REMINDER
        };
		
	}
	
	/**
	 * TaskLists table contract.
	 * 
	 * @author ran
	 */
	public static final class TaskLists implements BaseColumns {
		
		// This class cannot be instantiated
		private TaskLists() {}
		
		/**
		 * The authority.
		 */
		public static final String AUTHORITY = "il.ac.shenkar.todo.contentproviders.TasksProvider";
		
		/**
		 * The table name offered by this provider.
		 */
		public static final String TABLE_NAME_TASK_LISTS = "task_lists";
		
		/*
         * Column definitions.
         * 
         * Taken from the Google Tasks API URL:
         * https://developers.google.com/google-apps/tasks/v1/reference/tasks
         * 
         * For reference on the column names and description.
         */
		
		/**
		 * UNKNOWN FIELD:
		 * 
		 * Column name task list's id on the client (sqlite local database).
		 * TaskList identifier.
		 * 
		 * <P>Type: INTEGER</P>
		 * <P>Constraint: NOT NULL</p>
		 * <P>Constraint: PRIMARY KEY</p>
		 */
		public static final String COLUMN_NAME_CLIENT_ID = "_id";
		
		/**
		 * Column name task list's id on the server.
		 * TaskList identifier.
		 * 
		 * <P>Type: TEXT</P>
		 */
		public static final String COLUMN_NAME_SERVER_ID = "id";
		
		/**
         * Column name task list's kind.
         * Type of the resource. This is always "tasks#taskList".
         * 
         * <P>Type: TEXT</P>
         */
		public static final String COLUMN_NAME_KIND = "kind";
		
		// etag is ommited - ETag of the resource.
		
		/**
         * Column name task list's title.
         * Title of the task list.
         * 
         * <P>Type: TEXT</P>
         * <P>Constraint: NOT NULL</p>
         */
		public static final String COLUMN_NAME_TITLE = "title";
		
		/**
         * Column name task list's updated.
         * Last modification time of the task list (as a RFC 3339 timestamp).
         * 
         * <P>Type: TEXT</P>
         * <P>Constraint: NOT NULL</p>
         */
		public static final String COLUMN_NAME_UPDATED = "updated";
        
		/**
         * Column name task list's selfLink.
         * URL pointing to this task list.
         * Used to retrieve, update, or delete this task list.
         * 
         * <P>Type: TEXT</P>
         */
		public static final String COLUMN_NAME_SELF_LINK = "selfLink";
		
		/**
		 * UNKNOWN FIELD:
		 * 
         * Column name task's deleted.
         * Flag indicating whether the task has been deleted.
         * The default if False.
         * True as 1 and false as 0.
         * 
         * <P>Type: INTEGER</P>
         */
		public static final String COLUMN_NAME_DELETED = "deleted";
		
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
         * UNKNOWN FIELD:
         * Column position for task list's client id.
         */
        public static final int COLUMN_POSITION_CLIENT_ID = 0;
        
        /**
         * Column position for task list's id on the server.
         */
        public static final int COLUMN_POSITION_SERVER_ID = 1;
        
        /**
         * Column position for task list's kind.
         */
		public static final int COLUMN_POSITION_KIND = 2;
		
		// etag is ommited - ETag of the resource.
		
		/**
         * Column position for task list's title.
         */
		public static final int COLUMN_POSITION_TITLE = 3;
		
		/**
         * Column position for task list's updated.
         */
		public static final int COLUMN_POSITION_UPDATED = 4;
        
		/**
         * Column position for task list's selfLink.
         */
		public static final int COLUMN_POSITION_SELF_LINK = 5;
		
		/**
         * Column position for task list's deleted.
         */
		public static final int COLUMN_POSITION_DELETED = 6;
        
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
		 * Path part for the task lists URI.
		 */
		public static final String PATH_TASK_LISTS = "/tasklists";
		
		/**
		 * Path part for the task list ID URI.
		 */
		public static final String PATH_TASK_LIST_ID = "/tasklists/";
		
		/**
         * 0-relative position of a task list ID segment in the path part of a TaskList ID URI.
         */
        public static final int TASK_LIST_ID_PATH_POSITION = 1;
		
		/**
		 * The content:// style URL for this table.
		 */
		public static final Uri CONTENT_URI = Uri.parse(SCHEME + AUTHORITY + PATH_TASK_LISTS);
		
		/**
		 * The content URI base for a single task list. Callers must
         * append a numeric task list id to this Uri to retrieve a task list.
		 */
		public static final Uri CONTENT_ID_URI_BASE = Uri.parse(SCHEME + AUTHORITY + PATH_TASK_LIST_ID);
		
		/**
		 * The content URI match pattern for a single task list, specified by its ID. Use this to match
         * incoming URIs or to construct an Intent.
		 */
		public static final Uri CONTENT_ID_URI_PATTERN = Uri.parse(SCHEME + AUTHORITY + PATH_TASK_LIST_ID + "/#");
		
		/*
         * MIME type definitions.
         */
		
        /**
         * The MIME type of {@link #CONTENT_URI} providing a directory of task lists.
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/List<com.google.api.services.tasks.model.TaskList>";

        /**
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single task list.
         */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/com.google.api.services.tasks.model.TaskList";
        
        /*
         * Sort Order definitions.
         */
        
        /**
         * Order by task list's title.
         */
        public static final String SORT_BY_TITLE = COLUMN_NAME_TITLE + " ASC";

        /**
         * The default sort order for this table.
         */
        public static final String DEFAULT_SORT_ORDER = SORT_BY_TITLE;
        
        /*
         * Creates a projection that returns the task ID and the task contents.
         */
        public static final String[] PROJECTION =
            new String[] {
                ToDo.TaskLists.COLUMN_NAME_CLIENT_ID,
                ToDo.TaskLists.COLUMN_NAME_SERVER_ID,
                ToDo.TaskLists.COLUMN_NAME_KIND,
                ToDo.TaskLists.COLUMN_NAME_TITLE,
                ToDo.TaskLists.COLUMN_NAME_UPDATED,
                ToDo.TaskLists.COLUMN_NAME_SELF_LINK,
                ToDo.TaskLists.COLUMN_NAME_DELETED
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
		
		public static final String ACTION_VIEW_TASK = "il.ac.shenkar.todo.ACTION_VIEW_TASK";
		public static final String ACTION_EDIT_TASK = "il.ac.shenkar.todo.ACTION_EDIT_TASK";
		
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
		 * Extra key for task's client id.
		 * <P>Type: long</P>
		 */
		public static final String EXTRA_TASK_CLIENT_ID = "il.ac.shenkar.todo.EXTRA_TASK_CLIENT_ID";
		
		/**
		 * Extra key for task list client id.
		 * <P>Type: long</P>
		 */
		public static final String EXTRA_TASK_LIST_CLIENT_ID = "il.ac.shenkar.todo.EXTRA_TASK_LIST_CLIENT_ID";
		
		/**
		 * Extra key for task's title.
		 * <P>Type: String</P>
		 */
		public static final String EXTRA_TASK_TITLE = "il.ac.shenkar.todo.EXTRA_TASK_TITLE";
		
		/**
		 * Extra key for task's notes.
		 * <P>Type: String</P>
		 */
		public static final String EXTRA_TASK_NOTES = "il.ac.shenkar.todo.EXTRA_TASK_NOTES";
		
		/**
		 * Extra key for reminder's id.
		 * <P>Type: int</P>
		 */
		public static final String EXTRA_REMINDER_ID = "il.ac.shenkar.todo.EXTRA_REMINDER_ID";
		
		/**
		 * Extra key for task's location reminder address.
		 * <P>Type: String</P>
		 */
		public static final String EXTRA_TASK_LOCATION_REMINDER_ADDRESS = "il.ac.shenkar.todo.EXTRA_TASK_LOCATION_REMINDER_ADDRESS";
		
		/**
		 * Extra key for indicating if the geo-fancing notification fires when
		 * Entering the proximity (true) or exiting the proximity (false).
		 * <P>Type: boolean</P>
		 */
		public static final String EXTRA_IS_PROXIMITY_ENTERING = "il.ac.shenkar.todo.EXTRA_IS_PROXIMITY_ENTERING";
		
	}
	
	/**
	 * Fragment Arguments contract.
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
		 * Argument key for the list client identifier.
		 * <P>Type: String</P>
		 */
		public static final String ARG_TASK_LIST_CLIENT_ID = "il.ac.shenkar.todo.ARG_TASK_LIST_CLIENT_ID";
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
