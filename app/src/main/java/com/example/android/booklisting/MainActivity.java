package com.example.android.booklisting;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements LoaderManager
        .LoaderCallbacks<List<Book>> {

    private static final String TAG = "MainActivity";

    // Loader ID
    private static final int BOOKS_LOADER_ID = 1;

    // Key to save the list position
    private static final String LIST_SCROLL_POSITION = "SCROLL_POSITION";

    // Google Books API URL
    private static final String GOOGLE_BOOKS_API_URL = "https://www.googleapis" +
            ".com/books/v1/volumes?q=%s";

    // The books list adapter
    private BooksListAdapter booksListAdapter;

    // The views
    /* Start */
    @BindView(R.id.edit_query)
    EditText editQuery;

    @BindView(R.id.books_list_view)
    ListView booksListView;

    @BindView(R.id.empty_view)
    TextView emptyView;

    @BindView(R.id.loading_indicator)
    ProgressBar loadingIndicator;
    /* End */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        disableConnectionReuseIfNecessary();

        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        booksListAdapter = new BooksListAdapter(this, 0);

        booksListView.setEmptyView(emptyView);
        booksListView.setAdapter(booksListAdapter);

        // Sets the EditText to have a Search button in the keyboard
        editQuery.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        editQuery.setSingleLine();

        editQuery.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (!isInternetAvailable()) {
                    loadingIndicator.setVisibility(View.GONE);

                    emptyView.setText(R.string.no_internet_connection);

                    return false;
                }

                // Treats the search action from the EditText
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    booksListAdapter.clear();

                    // Hides the no results TextView and shows the ProgresBar
                    emptyView.setVisibility(View.GONE);
                    loadingIndicator.setVisibility(View.VISIBLE);

                    // Restarts the Loader to search the books
                    getSupportLoaderManager().restartLoader(BOOKS_LOADER_ID, null, MainActivity.this);

                    return true;
                }

                return false;
            }
        });

        // Initializes or attaches to an existing loader
        getSupportLoaderManager().initLoader(BOOKS_LOADER_ID, null, this);
    }

    @Override
    public Loader<List<Book>> onCreateLoader(int id, Bundle args) {
        String apiUrlSearch = String.format(GOOGLE_BOOKS_API_URL, editQuery.getText().toString());

        return new BooksLoader(this, apiUrlSearch);
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> books) {

        // Hides the ProgressBar and sets the no results text
        loadingIndicator.setVisibility(View.GONE);
        emptyView.setText(R.string.no_results);

        // Clears the last data and adds the new ones
        booksListAdapter.clear();
        if (books != null && !books.isEmpty()) {
            booksListAdapter.addAll(books);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {

        // Clear the data that will be released from the loader
        booksListAdapter.clear();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        int currentPosition = booksListView.getFirstVisiblePosition();
        outState.putInt(LIST_SCROLL_POSITION, currentPosition);

        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);

        int savedPosition = savedInstanceState.getInt(LIST_SCROLL_POSITION);
        booksListView.setSelection(savedPosition);
    }

    /**
     * Work around pre-Froyo bugs in HTTP connection reuse.
     */
    private void disableConnectionReuseIfNecessary() {

        if (Integer.parseInt(Build.VERSION.SDK) < Build.VERSION_CODES.FROYO) {
            System.setProperty("http.keepAlive", "false");
        }
    }

    /**
     * Checks if the internet connection is available
     *
     * @return <code>true</code> when internet is available, <code>false</code> otherwise
     */
    private boolean isInternetAvailable() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected())
            return false;

        return true;
    }
}