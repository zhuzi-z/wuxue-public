package com.wuda.wuxue.ui.toolkit;

import com.wuda.wuxue.bean.BookItem;
import com.wuda.wuxue.network.BookSearchNetwork;
import com.wuda.wuxue.network.ResponseHandler;
import com.wuda.wuxue.network.ResponseResult;
import com.wuda.wuxue.ui.base.BaseResponseViewModel;

import java.util.ArrayList;
import java.util.List;

public class BookSearchViewModel extends BaseResponseViewModel<List<BookItem>> {

    String keyword;
    String filed;

    public BookSearchViewModel() {
        currentPage = 1;
        data = new ArrayList<>();
    }

    public void requestBookList() {
        BookSearchNetwork.requestBookList(keyword, filed, currentPage, new ResponseHandler<List<BookItem>>() {
            @Override
            public void onHandle(ResponseResult<List<BookItem>> result) {
                if (result.isSuccess()) {
                    getSuccessResponse().postValue(result.getData());
                    data.addAll(result.getData());
                    totalPage = result.getTotalPages(); // 最大条数
                } else {
                    getFailResponse().postValue(result);
                }
            }
        });
    }

    public void loadMore() {
        currentPage++;
        requestBookList();
    }

    @Override
    public boolean hasMore() {
        return data.size() < totalPage;
    }
}
