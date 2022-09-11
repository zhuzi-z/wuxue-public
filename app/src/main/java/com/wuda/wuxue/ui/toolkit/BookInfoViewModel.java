package com.wuda.wuxue.ui.toolkit;

import com.wuda.wuxue.bean.BookInfo;
import com.wuda.wuxue.bean.BookItem;
import com.wuda.wuxue.network.BookSearchNetwork;
import com.wuda.wuxue.network.ResponseHandler;
import com.wuda.wuxue.network.ResponseResult;
import com.wuda.wuxue.ui.base.BaseResponseViewModel;

public class BookInfoViewModel extends BaseResponseViewModel<BookInfo> {

    public void requestBookInfo(BookItem bookItem) {
        BookSearchNetwork.requestBookInfo(bookItem.getUrl(), new ResponseHandler<BookInfo>() {
            @Override
            public void onHandle(ResponseResult<BookInfo> result) {
                if (result.isSuccess()) {
                    getSuccessResponse().postValue(result.getData());
                } else {
                    getFailResponse().postValue(result);
                }
            }
        });
    }
}
