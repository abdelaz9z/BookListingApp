package com.example.booklistingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class BookAdapter extends ArrayAdapter<Book> {

    BookAdapter(@NonNull Context context, ArrayList<Book> books) {
        super(context, 0, books);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_book, parent, false);
        }

        Book book = getItem(position);

        ImageView bookImage = convertView.findViewById(R.id.image_book);
        assert book != null;
        loadImageFromUtl(book.getImageLinks(), bookImage);

        TextView titleText = convertView.findViewById(R.id.text_title);
        titleText.setText(book.getTitle());

        TextView authorText = convertView.findViewById(R.id.text_author);
        authorText.setText(book.getAuthor());

        TextView publishedDateText = convertView.findViewById(R.id.text_published_date);
        publishedDateText.setText(book.getPublishedDate());

        RatingBar ratingBar = convertView.findViewById(R.id.rating_bar);
        ratingBar.setRating((float) book.getRatingsCount());

        TextView languageText = convertView.findViewById(R.id.text_language);
        languageText.setText(book.getLanguage());

        TextView currencyCodeText = convertView.findViewById(R.id.text_currency_code);
        currencyCodeText.setText(book.getCurrencyCode());

        TextView amountText = convertView.findViewById(R.id.text_amount);
        amountText.setText((String.valueOf(book.getAmount())));
        if (book.getAmount() == 0) {
            amountText.setText("");
        }
        return convertView;
    }

    private void loadImageFromUtl(String url, ImageView imageView) {
        // optional: .placeholder(R.mipmap.ic_launcher)
        Picasso.with(getContext()).load(url)
                .error(R.drawable.ic_image)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onError() {
                    }
                });
    }
}