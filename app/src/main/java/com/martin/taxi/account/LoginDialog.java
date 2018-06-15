package com.martin.taxi.account;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.martin.taxi.MyTaxiApplication;
import com.martin.taxi.R;
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

import java.lang.ref.SoftReference;


/**
 * 登录框
 */

public class LoginDialog extends Dialog {

    private static final String TAG = "LoginDialog";
    private static final int LOGIN_SUC = 1;
    private static final int SERVER_FAIL = 2;
    private static final int PW_ERR = 4;
    private TextView mPhone;
    private EditText mPw;
    private Button mBtnConfirm;
    private View mLoading;
    private TextView mTips;
    private String mPhoneStr;
    private IHttpClient mHttpClient;
    private MyHandler mHandler;

    /**
     * 接收子线程消息的 Handler
     */
    static class MyHandler extends Handler {
        // 软引用
        SoftReference<LoginDialog> dialogRef;

        public MyHandler(LoginDialog dialog) {
            dialogRef = new SoftReference<LoginDialog>(dialog);
        }

        @Override
        public void handleMessage(Message msg) {
            LoginDialog dialog = dialogRef.get();
            if (dialog == null) {
                return;
            }
            // 处理UI 变化
            switch (msg.what) {

                case LOGIN_SUC:
                    dialog.showLoginSuc();
                    break;
                case PW_ERR:
                    dialog.showPasswordError();
                    break;
                case SERVER_FAIL:
                    dialog.showServerError();
                    break;
            }
        }
    }

    public LoginDialog(Context context, String phone) {
        this(context, R.style.Dialog);
        mPhoneStr = phone;
        mHttpClient = new OKHttpClientImp();
        mHandler = new MyHandler(this);
    }

    public LoginDialog(Context context, int theme) {
        super(context, theme);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View root = inflater.inflate(R.layout.dialog_login_input, null);
        setContentView(root);
        initViews();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    private void initViews() {
        mPhone = findViewById(R.id.phone);
        mPw = findViewById(R.id.password);
        mBtnConfirm = findViewById(R.id.btn_confirm);
        mLoading = findViewById(R.id.loading);
        mTips = findViewById(R.id.tips);
        findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mBtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
        mPhone.setText(mPhoneStr);
    }

    /**
     * 提交登录
     */
    private void submit() {
        String password = mPw.getText().toString();
        //  网络请求登录
        new Thread() {
            @Override
            public void run() {
                String url = API.Config.getDomain() + API.LOGIN;
                IRequest request = new BaseRequest(url);
                request.setBody("phone", mPhoneStr);
                String password = mPw.getText().toString();
                request.setBody("password", password);
                IResponse response = mHttpClient.post(request, false);
                Log.d(TAG, response.getData());
                if (response.getCode() == BaseResponse.STATE_OK) {
                    LoginResponse bizRes =
                            new Gson().fromJson(response.getData(), LoginResponse.class);
                    if (bizRes.getCode() == BaseBizResponse.STATE_OK) {
                        // 保存登录信息
                        UserAccount account = bizRes.getData();
                        // todo: 加密存储
                        SharedPreferencesDao dao =
                                new SharedPreferencesDao(MyTaxiApplication.getInstance(),
                                        SharedPreferencesDao.FILE_ACCOUNT);
                        dao.save(SharedPreferencesDao.KEY_ACCOUNT, account);
                        // 通知 UI
                        mHandler.sendEmptyMessage(LOGIN_SUC);
                    }
                    if (bizRes.getCode() == BaseBizResponse.STATE_PW_ERR) {
                        mHandler.sendEmptyMessage(PW_ERR);
                    } else {
                        mHandler.sendEmptyMessage(SERVER_FAIL);
                    }
                } else {
                    mHandler.sendEmptyMessage(SERVER_FAIL);
                }
            }
        }.start();
    }

    /**
     * 显示／隐藏 loading
     *
     * @param show
     */
    public void showOrHideLoading(boolean show) {
        if (show) {
            mLoading.setVisibility(View.VISIBLE);
            mBtnConfirm.setVisibility(View.GONE);
        } else {
            mLoading.setVisibility(View.GONE);
            mBtnConfirm.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 处理登录成功 UI
     */
    public void showLoginSuc() {
        mLoading.setVisibility(View.GONE);
        mBtnConfirm.setVisibility(View.GONE);
        mTips.setVisibility(View.VISIBLE);
        mTips.setTextColor(getContext().getResources().getColor(R.color.color_text_normal));
        mTips.setText(getContext().getString(R.string.login_suc));
        ToastUtil.show(getContext(), getContext().getString(R.string.login_suc));
        dismiss();
    }

    /**
     * 显示服服务器出错
     */
    public void showServerError() {
        showOrHideLoading(false);
        mTips.setVisibility(View.VISIBLE);
        mTips.setTextColor(getContext().getResources().getColor(R.color.error_red));
        mTips.setText(getContext().getString(R.string.error_server));
    }

    /**
     * 密码错误
     */
    public void showPasswordError() {
        showOrHideLoading(false);
        mTips.setVisibility(View.VISIBLE);
        mTips.setTextColor(getContext().getResources().getColor(R.color.error_red));
        mTips.setText(getContext().getString(R.string.password_error));
    }
}
