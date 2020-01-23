package com.example.booklistingapp;

class Book {

    private final String mImageLinks;
    private final String mTitle;
    private final String mAuthor;
    private final String mPublishedDate;
    private final double mRatingsCount;
    private final String mLanguage;
    private final double mAmount;
    private final String mCurrencyCode;
    private final String mBuyLink;

    Book(String imageLinks, String title, String author, String publishedDate, double ratingsCount, String language, double amount, String currencyCode, String buyLink) {
        this.mImageLinks = imageLinks;
        this.mTitle = title;
        this.mAuthor = author;
        this.mPublishedDate = publishedDate;
        this.mRatingsCount = ratingsCount;
        this.mLanguage = language;
        this.mAmount = amount;
        this.mCurrencyCode = currencyCode;
        this.mBuyLink = buyLink;
    }

    String getImageLinks() {
        return mImageLinks;
    }

    String getTitle() {
        return mTitle;
    }

    String getAuthor() {
        return mAuthor;
    }

    String getPublishedDate() {
        return mPublishedDate;
    }

    double getRatingsCount() {
        return mRatingsCount;
    }

    String getLanguage() {
        return mLanguage;
    }

    double getAmount() {
        return mAmount;
    }

    String getCurrencyCode() {
        return mCurrencyCode;
    }

    String getBuyLink() {
        return mBuyLink;
    }
}