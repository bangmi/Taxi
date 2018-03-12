package com.martin.taxi.common.http;

public interface IHttpClient {
    IResponse get(IRequest request, boolean forceCache);

    IResponse post(IRequest request, boolean forceCache);

}
