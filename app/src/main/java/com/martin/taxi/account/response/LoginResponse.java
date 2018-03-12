package com.martin.taxi.account.response;


import com.martin.taxi.common.http.biz.BaseBizResponse;

public class LoginResponse extends BaseBizResponse {
    Account data;

    public Account getData() {
        return data;
    }

    public void setData(Account data) {
        this.data = data;
    }
}
