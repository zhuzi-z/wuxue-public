package com.wuda.wuxue.bean;

import com.wuda.wuxue.ui.toolkit.BookInfoFragment;
import com.wuda.wuxue.network.ServerURL;

public class BookItem implements Tool {

    String name;
    String author;
    String publisher;
    String url;

    public BookItem(String name, String author, String publisher, String url) {
        this.name = name;
        this.author = author;
        this.publisher = publisher;
        this.url = url;
    }

    @Override
    public String getToolName() {
        return "图书详情";
    }

    @Override
    public String getUrl() {
        return ServerURL.LIB + "/" + url;
    }

    @Override
    public Class<?> getTargetFragmentCls() {
        return BookInfoFragment.class;
    }

    public String getAuthor() {
        return author;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getName() {
        return name;
    }
}
