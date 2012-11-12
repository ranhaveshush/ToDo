/**
 * 
 */
package il.ac.shenkar.todo.controller.adapters;

import android.view.View;


/**
 * @author ran
 *
 */
public interface Adaptable {
	
	/**
	 * @return
	 */
	public int getLayoutId();
	
	/**
	 * @param convertView
	 * @return
	 */
	public View buildView(View convertView);
	
	/**
	 * @param convertView
	 */
	public void setView(View convertView);

}
