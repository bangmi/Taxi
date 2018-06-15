package com.martin.taxi.activity;

import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.martin.taxi.MyTaxiApplication;
import com.martin.taxi.R;
import com.martin.taxi.account.PhoneInputDialog;
import com.martin.taxi.account.response.UserAccount;
import com.martin.taxi.account.response.LoginResponse;
import com.martin.taxi.common.http.IHttpClient;
import com.martin.taxi.common.http.IRequest;
import com.martin.taxi.common.http.IResponse;
import com.martin.taxi.common.http.api.API;
import com.martin.taxi.common.http.biz.BaseBizResponse;
import com.martin.taxi.common.http.impl.BaseRequest;
import com.martin.taxi.common.http.impl.BaseResponse;
import com.martin.taxi.common.http.impl.OKHttpClientImp;
import com.martin.taxi.common.storage.SharedPreferencesDao;
import com.martin.taxi.common.util.ToastUtil;

public class HomeActivity extends BaseActivity {

    private IHttpClient mHttpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mHttpClient = new OKHttpClientImp();
        checkLoginState();

    }

    /**
     * 检查用户是否登录
     */
    private void checkLoginState() {

        // 获取本地登录信息

        SharedPreferencesDao dao = new SharedPreferencesDao(MyTaxiApplication.getInstance(), SharedPreferencesDao.FILE_ACCOUNT);
        final UserAccount account = (UserAccount) dao.get(SharedPreferencesDao.KEY_ACCOUNT, UserAccount.class);

        // 登录是否过期
        boolean tokenValid = false;

        // 检查token是否过期
        if (account != null) {
            if (account.getExpired() > System.currentTimeMillis()) {
                // token 有效
                tokenValid = true;
            }
        }
        if (!tokenValid) {
            showPhoneInputDialog();
        } else {
            // 请求网络，完成自动登录
            new Thread() {
                @Override
                public void run() {
                    String url = API.Config.getDomain() + API.LOGIN_BY_TOKEN;
                    IRequest request = new BaseRequest(url);
                    request.setBody("token", account.getToken());
                    IResponse response = mHttpClient.post(request, false);
                    Log.d(TAG, response.getData());
                    if (response.getCode() == BaseResponse.STATE_OK) {
                        LoginResponse bizRes = new Gson().fromJson(response.getData(), LoginResponse.class);
                        if (bizRes.getCode() == BaseBizResponse.STATE_OK) {
                            // Token 登录成功 保存登录信息
                            UserAccount account = bizRes.getData();
                            // todo: 加密存储
                            SharedPreferencesDao dao = new SharedPreferencesDao(MyTaxiApplication.getInstance(), SharedPreferencesDao.FILE_ACCOUNT);
                            dao.save(SharedPreferencesDao.KEY_ACCOUNT, account);

                            // 通知 UI
                            HomeActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtil.show(HomeActivity.this, getString(R.string.login_suc));
                                }
                            });
                        }
                        if (bizRes.getCode() == BaseBizResponse.STATE_TOKEN_INVALID) {
                            //Token无效，重新使用密码登录
                            HomeActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showPhoneInputDialog();
                                }
                            });
                        }
                    } else {
                        HomeActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.show(HomeActivity.this, getString(R.string.error_server));
                            }
                        });
                    }
                }
            }.start();
        }
    }

    /**
     * 显示手机输入框
     */
    private void showPhoneInputDialog() {
        PhoneInputDialog dialog = new PhoneInputDialog(this);
        dialog.show();
    }
}
