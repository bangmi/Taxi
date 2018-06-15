package com.martin.taxi.common.http.impl;

import com.martin.taxi.common.http.IHttpClient;
import com.martin.taxi.common.http.IRequest;
import com.martin.taxi.common.http.IResponse;
import com.martin.taxi.common.http.api.API;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by Administrator on 2018/3/6.
 */
public class OKHttpClientImpTest {
    IHttpClient httpClient;
    @Before
    public void setUp() throws Exception {
        httpClient = new OKHttpClientImp();
        API.Config.setDebug(false);
    }

    @Test
    public void get() throws Exception {
        IRequest request = new BaseRequest(API.Config.getDomain() + API.TEST_GET);
        request.setBody("uuid","123456");
        request.setHeader("testHeader","header");
        IResponse response = httpClient.get(request, false);

        System.out.println(response.getCode());
        System.out.println(response.getData());
    }

    @Test
    public void post() throws Exception {
    }

}