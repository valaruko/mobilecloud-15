package vandy.mooc.operations;

import vandy.mooc.common.Utils;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;

public class QueryContactsCommand
       implements LoaderManager.LoaderCallbacks<Cursor> {
    /**
     * Store a reference to the ContactsOps object.
     */
    private ContactsOps mOps;

    /**
     * Constructor initializes the fields.
     */
    public QueryContactsCommand(ContactsOps ops) {
        // Store the ContactOps and the ContentResolver from the
        // Application context.
        mOps = ops;
    }

    /**
     * Run the command.
     */
    public void run() {
        // Initialize the LoaderManager. 
        // @@ Chinmaya, I'm not sure if this should be called each
        // time a query is performed.  Should it just be called once?
        mOps.getActivity().getLoaderManager().initLoader(0,
                                                         null,
                                                         this);
    }

    /**
     * This hook method is called back by the LoaderManager when the
     * loader is initialized.
     */
    public Loader<Cursor> onCreateLoader(int id, 
                                         Bundle args) {
        // Columns to query.
        final String columnsToQuery[] =
            new String[] {
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.Contacts.STARRED 
        };
	
        // Contacts to select.
        final String selection =
            "((" 
            + Contacts.DISPLAY_NAME 
            + " NOTNULL) AND ("
            + Contacts.DISPLAY_NAME 
            + " != '' ) AND (" 
            + Contacts.STARRED
            + "== 1))";

        // Create a new CursorLoader that will perfom the query
        // asynchronously.
        return new CursorLoader(mOps.getActivity(),
                                Contacts.CONTENT_URI,
                                columnsToQuery,
                                selection,
                                null,
                                Contacts._ID 
                                + " ASC");
    }

    /**
     * This hook method is called back when the query completes.
     */
    public void onLoadFinished(Loader<Cursor> loader,
                               Cursor cursor) {
        if (cursor == null
            || cursor.getCount() == 0)
            Utils.showToast(mOps.getActivity(),
                            "Contacts not found");
        else {
            mOps.setCursor(cursor);
            
            Utils.showToast(mOps.getActivity(),
                        cursor.getCount()
                        + " contact(s) found");
            
            // Call displayCursor() to swap the Cursor data with the
            // adapter, which will display the results.
            mOps.getActivity().displayCursor(cursor);
        }
    }

    /**
     * This hook method is called back when the loader is reset.
     */
    public void onLoaderReset(Loader<Cursor> loader) {
        mOps.getActivity().displayCursor(null);
    }
}

