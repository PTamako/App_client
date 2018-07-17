package com.example.lenovo.test.model;

import java.io.Serializable;
import java.util.List;

public class StoreList extends BaseResponse {

    /**
     *
     */
    private static final long serialVersionUID = -3105255201702928733L;
    private List<Store> list;

    public List<Store> getList() {
        return list;
    }

    public void setList(List<Store> list) {
        this.list = list;
    }
}
