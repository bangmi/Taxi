package com.martin.taxi.account.response;


import com.martin.taxi.common.http.biz.BaseBizResponse;

public class LoginResponse extends BaseBizResponse {
    UserAccount data;

    public UserAccount getData() {
        return data;
    }

    public void setData(UserAccount data) {
        this.data = data;
    }
}
