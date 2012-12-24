/**
 * 
 */
package il.ac.shenkar.todo.model.contentproviders;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * @author ran
 *
 */
public final class ToDo {
	
	public static final String AUTHORITY = "il.ac.shenkar.todo.contentprovider";

	// This class cannot be instantiated
	private ToDo() {}
	
	/**
	 * Tasks table contract.
	 * 
	 * @author ran
	 */
	public static final class Tasks implements BaseColumns {
		
		// This class cannot be instantiated
		private Tasks() {}
		
		/**
		 * The table name offered by this provider.
		 */
		public static final String TABLE_NAME_TASKS = "tasks";
		
		/*
         * Column definitions
         */
		
		/**
         * Column name for the title of the task.
         * <P>Type: TEXT</P>
         * <P>Constraint: NOT NULL</p>
         */
		public static final String COLUMN_NAME_TITLE = "title";
        
        /**
         * Column name for the description of the task.
         * <P>Type: TEXT</P>
         */
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        
        /**
         * Column name for the datetime of the task.
         * <P>Type: REAL</P>
         */
        public static final String COLUMN_NAME_DATETIME = "datetime";
        
        /*
         * Column positions.
         */
        
        /**
         * Column position for the id of the task.
         */
        public static final int COLUMN_POSITION_ID = 0;
        
        /**
         * Column position for the title of the task.
         */
        public static final int COLUMN_POSITION_TITLE = 1;
        
        /**
         * Column position for the description of the task.
         */
        public static final int COLUMN_POSITION_DESCRIPTION = 2;
        
        /**
         * Column position for the datetime of the task.
         */
        public static final int COLUMN_POSITION_DATETIME = 3;
        
        /*
         * Column defaults content.
         */
        
        /**
         * Column default content for the title of the task.
         */
        public static final String COLUMN_DEFAULT_CONTENT_TITLE = "None Title";
		
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
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/il.ac.shenkar.todo.model.dto.Task";

        /**
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single
         * task.
         */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/il.ac.shenkar.todo.model.dto.Task";
        
        /*
         * Sort Order definitions.
         */
        
        /**
         * Order by task's title.
         */
        public static final String SORT_BY_TITLE = COLUMN_NAME_TITLE + " ASC";
        
        /**
         * Order by task's date.
         */
        public static final String SORT_BY_DATETIME = COLUMN_NAME_DATETIME + " ASC";

        /**
         * The default sort order for this table.
         */
        public static final String DEFAULT_SORT_ORDER = SORT_BY_TITLE;
		
	}
	
	/**
	 * Intent actions contact.
	 * 
	 * @author ran
	 *
	 */
	public static final class Actions {
		
		// This class cnnot be instantiated
		private Actions() {}
		
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
		 * <P>Type: long</P>
		 */
		public static final String EXTRA_TASK_ID = "il.ac.shenkar.todo.TASK_ID";
		
		/**
		 * Extra key for task's title.
		 * <P>Type: String</P>
		 */
		public static final String EXTRA_TASK_TITLE = "il.ac.shenkar.todo.TASK_TITLE";
		
		/**
		 * Extra key for task's description.
		 * <P>Type: String</P>
		 */
		public static final String EXTRA_TASK_DESCRIPTION = "il.ac.shenkar.todo.TASK_DESCRIPTION";
		
		/**
		 * Extra key for task's datetime in milliseconds from 1.1.1970.
		 * <P>Type: long</P>
		 */
		public static final String EXTRA_TASK_DATETIME_MILLISEC = "il.ac.shenkar.todo.TASK_DATETIME_MILLISEC";
		
	}

}
