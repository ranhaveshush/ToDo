package il.ac.shenkar.todo.controller.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

/**
 * @author ran
 *
 * @param <D>
 */
// FIXME: if not needed delete
public abstract class AsyncLoader<D> extends AsyncTaskLoader<D> {

	/**
	 * Logger tag.
	 */
	private static final String TAG = "AsyncLoader<D>";

    /**
     * Holds the data.
     */
    private D data;

    /**
     * Full constructor.
     * 
     * @param context
     */
    public AsyncLoader(Context context) {
        super(context);
        
        // Logger
        Log.d(TAG, "AsyncLoader(Context context)");
    }

    /* (non-Javadoc)
     * @see android.support.v4.content.Loader#deliverResult(java.lang.Object)
     */
    @Override
    public void deliverResult(D data) {
        // Logger
        Log.d(TAG, "deliverResult(D data)");
        
    	// An async query came in while the loader is stopped
        if (isReset()) {
            return;
        }

        this.data = data;

        super.deliverResult(data);
    }


    /* (non-Javadoc)
     * @see android.support.v4.content.Loader#onStartLoading()
     */
    @Override
    protected void onStartLoading() {
        // Logger
        Log.d(TAG, "onStartLoading()");
        
    	// If the Loader is currently started,
    	// we can immediately deliver its results.
        if (data != null) {
            deliverResult(data);
        }

        if (takeContentChanged() || data == null) {
            forceLoad();
        }
    }

    /* (non-Javadoc)
     * @see android.support.v4.content.Loader#onStopLoading()
     */
    @Override
    protected void onStopLoading() {
        // Logger
        Log.d(TAG, "onStopLoading()");
        
         // Attempts to cancel the current load task if possible.
        cancelLoad();
    }

    /* (non-Javadoc)
     * @see android.support.v4.content.Loader#onReset()
     */
    @Override
    protected void onReset() {
        super.onReset();
        
        // Logger
        Log.d(TAG, "onReset()");

        // Ensures the loader is stopped
        onStopLoading();

        data = null;
    }
}