package cityautocomplete;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

public class LiquorAutocompleteTextview extends AutoCompleteTextView {

    public LiquorAutocompleteTextview(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public LiquorAutocompleteTextview(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

  /*  public CustomAutoCompleteView1(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }
*/
    // this is how to disable AutoCompleteTextView filter
    @Override
    protected void performFiltering(final CharSequence text, final int keyCode)
    {
        String filterText = " ";
        super.performFiltering(filterText, keyCode);
    }


}