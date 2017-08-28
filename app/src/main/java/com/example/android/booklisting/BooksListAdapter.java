package com.example.android.booklisting;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Adapter for the list of books returned from the API
 */
class BooksListAdapter extends ArrayAdapter<Book> {
    BooksListAdapter(@NonNull Context context, @LayoutRes int resource) {
        super(context, resource);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.search_result_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        Book currentItem = getItem(position);

        holder.bookTitle.setText(currentItem.getTitle());

        StringBuilder authors = new StringBuilder();
        for (String author : currentItem.getAuthor()) {
            authors.append(author).append("\n");
        }

        // Removes last '\n'
        authors.deleteCharAt(authors.length() - 1);

        holder.bookAuthors.setText(authors);

        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.book_title)
        TextView bookTitle;

        @BindView(R.id.book_authors)
        TextView bookAuthors;

        private ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
