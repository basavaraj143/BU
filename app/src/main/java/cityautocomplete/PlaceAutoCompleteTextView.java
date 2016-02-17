package cityautocomplete;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

import java.util.HashMap;

/** Customizing AutoCompleteTextView to return Place Description
 * corresponding to the selected item
 */
public class PlaceAutoCompleteTextView extends AutoCompleteTextView {

    //this is the method declaration for customAutocompleteTextview1
    public PlaceAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Returns the place description corresponding to the selected item
     */
    @Override
    protected CharSequence convertSelectionToString(Object selectedItem) {
        /** Each item in the autocompetetextview suggestion list is a hashmap object */
        HashMap<String, String> hm = (HashMap<String, String>) selectedItem;
        String c = hm.get("description");
        String[] parts = c.split(",");


       return hm.get("description");
        //return parts[0]+parts[1];
    }
}