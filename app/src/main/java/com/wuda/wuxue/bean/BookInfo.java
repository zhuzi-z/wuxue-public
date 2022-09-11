package com.wuda.wuxue.bean;

import java.util.ArrayList;
import java.util.List;

public class BookInfo {

    // 全馆馆藏
    public static class CollectionInfo {
        String status;  // 单册状态
        String returnDate;  // 应还日期
        String branch;  // 分馆
        String shelfId;  // 架位
        String requestNum;  // 请求数
        String barCode;  // 条码

        public CollectionInfo(String status, String returnDate, String branch, String shelfId, String requestNum, String barCode) {
            this.status = status;
            this.returnDate = returnDate;
            this.branch = branch;
            this.shelfId = shelfId;
            this.requestNum = requestNum;
            this.barCode = barCode;
        }

        public String getStatus() {
            return status;
        }

        public String getReturnDate() {
            return returnDate;
        }

        public String getBranch() {
            return branch;
        }

        public String getShelfId() {
            return shelfId;
        }

        public String getRequestNum() {
            return requestNum;
        }

        public String getBarCode() {
            return barCode;
        }
    }

    String title;  // 题名
    String author;  // 作者
    String keyWord;  // 主题词
    String publisher;  // 出版发行
    String ISBN;  // ISBN
    String digest; // abstract 摘要
    List<CollectionInfo> collectionInfoList;

    public BookInfo(String title, String author, String keyWord, String publisher, String ISBN, String digest) {
        this.title = title;
        this.author = author;
        this.keyWord = keyWord;
        this.publisher = publisher;
        this.ISBN = ISBN;
        this.digest = digest;
        this.collectionInfoList = new ArrayList<>();
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getISBN() {
        return ISBN;
    }

    public String getDigest() {
        return digest;
    }

    public List<CollectionInfo> getCollectionInfoList() {
        return collectionInfoList;
    }

    public void addCollectionInfo2List(CollectionInfo collectionInfo) {
        this.collectionInfoList.add(collectionInfo);
    }
}
