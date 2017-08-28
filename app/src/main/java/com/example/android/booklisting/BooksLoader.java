package com.example.android.booklisting;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import java.util.Collections;
import java.util.List;

/**
 * Loader for the google books api
 */
class BooksLoader extends AsyncTaskLoader<List<Book>> {

    private String mUrl;

    public BooksLoader(Context context, String url) {
        super(context);
        this.mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Book> loadInBackground() {
        if (mUrl == null)
            return Collections.emptyList();

        return QueryUtils.fetchBooksData(mUrl);
    }
}
