package com.example.android.booklisting;

import java.util.List;

/**
 * Class to store the book data
 */
class Book {
    private String mTitle;
    private List<String> mAuthor;

    Book(String mTitle, List<String> mAuthor) {
        this.mTitle = mTitle;
        this.mAuthor = mAuthor;
    }

    String getTitle() {
        return mTitle;
    }

    List<String> getAuthor() {
        return mAuthor;
    }
}
