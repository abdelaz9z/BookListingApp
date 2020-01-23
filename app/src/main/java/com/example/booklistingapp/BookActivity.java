package com.example.booklistingapp;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class BookActivity extends AppCompatActivity implements LoaderCallbacks<List<Book>> {

    private String mUrlRequestGoogleBooks = "";
    private BookAdapter mBookAdapter;
    private TextView mEmptyText;
    private ProgressBar mLoadingIndicatorProgress;
    private final int BOOK_LOADER_ID = 1;
    private SearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        final ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        mEmptyText = findViewById(R.id.text_empty);
        mLoadingIndicatorProgress = findViewById(R.id.progress_loading_indicator);
        mBookAdapter = new BookAdapter(this, new ArrayList<Book>());

        ListView bookList = findViewById(R.id.list_book);
        bookList.setEmptyView(mEmptyText);
        bookList.setAdapter(mBookAdapter);
        bookList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Book currentBook = mBookAdapter.getItem(position);
                assert currentBook != null;
                if (currentBook.getBuyLink() != null) {
                    Uri buyBookUri = Uri.parse(currentBook.getBuyLink());
                    Intent websiteIntent = new Intent(Intent.ACTION_VIEW, buyBookUri);
                    startActivity(websiteIntent);
                } else {
                    Toast.makeText(BookActivity.this, "Missing info of buy book", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mSearchView = findViewById(R.id.search_view);
        mSearchView.onActionViewExpanded();
        mSearchView.setIconified(true);
        mSearchView.setQueryHint("Enter a book title");

        assert connectivityManager != null;
        final boolean isConnected = checkConnection(connectivityManager);
        if (isConnected) {
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(BOOK_LOADER_ID, null, this);
        } else {
            mLoadingIndicatorProgress.setVisibility(View.GONE);
            mEmptyText.setText(R.string.msg_no_internet_connection);
        }

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (isConnected) {
                    mUrlRequestGoogleBooks = updateQueryUrl(mSearchView.getQuery().toString());
                    mLoadingIndicatorProgress.setVisibility(View.VISIBLE);
                    mEmptyText.setText(R.string.msg_no_books);
                    getLoaderManager().restartLoader(BOOK_LOADER_ID, null, BookActivity.this);
                } else {
                    mBookAdapter.clear();
                    mEmptyText.setText(R.string.msg_no_internet_connection);
                }
                return false;
            }
        });

    }

    @NonNull
    @Override
    public Loader<List<Book>> onCreateLoader(int id, @Nullable Bundle args) {
        mUrlRequestGoogleBooks = updateQueryUrl(mSearchView.getQuery().toString());
        return new BookLoader(this, mUrlRequestGoogleBooks);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Book>> loader, List<Book> books) {
        mLoadingIndicatorProgress.setVisibility(View.GONE);
        mBookAdapter.clear();
        if (books != null && !books.isEmpty()) {
            mBookAdapter.addAll(books);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Book>> loader) {
        mBookAdapter.clear();
    }

    /**
     * Check if query contains spaces if YES replace these with PLUS sign
     *
     * @param searchValue - user data from SearchView
     * @return improved String URL for making HTTP request
     */
    private String updateQueryUrl(String searchValue) {

        if (searchValue.contains(" ")) {
            searchValue = searchValue.replace(" ", "+");
        }

        // &filter=paid-ebooks
        return "https://www.googleapis.com/books/v1/volumes?q=" + searchValue + "&maxResults=40";
    }

    public boolean checkConnection(ConnectivityManager connectivityManager) {
        // Status of internet connection
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting();
    }

}