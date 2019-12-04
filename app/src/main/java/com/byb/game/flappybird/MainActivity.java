package com.byb.game.flappybird;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.biwan.platform.sdk.BYBHandler;
import com.biwan.platform.sdk.BYBSDK;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity implements HttpHandler {
    private TextView txtOpenid;
    private TextView txtNick;
    private String prepayid;
    private String nonce;
    private String timestamp;
    private String sign;
    private JSONObject token;

    private static String Code;
    private static Context _context;
    private final static String appid = "2";


    private static void dialogTip(Context context, final String content) {
        Toast ts = Toast.makeText(context, content, Toast.LENGTH_LONG);
        ts.show();
    }

    private static JSONObject formdata2obj(String data) {
        JSONObject obj = new JSONObject();
        String[] t = data.split("&");
        try {
            for (String s : t) {
                String[] d = s.split("=");
                obj.put(d[0], d[1]);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    static BYBHandler sdkListener = new BYBHandler() {
        @Override
        public void handleBack(final String data) {
            JSONObject obj = formdata2obj(data);
            try {
                if (obj.getString("ReqType").equals("auth")) {
                    String code = obj.getString("code");
                    if (code.equals("0")) {
                        Code = obj.getString("Code");
                        dialogTip(_context, "授权成功");
                    } else {
                        dialogTip(_context, "授权失败");
                    }
                }
                else if (obj.getString("ReqType").equals("pay")) {
                    if (obj.getString("code").equals("0")) {
                        dialogTip(_context, "支付成功");
                    } else {
                        dialogTip(_context, "支付失败");
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _context = this;
        txtOpenid = findViewById(R.id.openid);
        txtNick = findViewById(R.id.nick);
        txtOpenid.setText("");
        txtNick.setText("");

        BYBSDK.init(this, appid, "bybflappybird", sdkListener);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        BYBSDK.openUrlListener(intent);
    }

    @Override
    public void handleAuth(JSONObject data) {
        try {
            Integer code = data.getInt("code");

            if (code == 0) {
                JSONObject t = data.getJSONObject("data");
                txtOpenid.setText("OpenID: " + t.getString("OpenID"));
                txtNick.setText("Nick: " + t.getString("nick"));
                token = t.getJSONObject("AccessToken");
            } else {
                String msg = data.getString("msg");
                txtOpenid.setText(msg);
                txtNick.setText("");
            }
        } catch (JSONException e) {
            txtOpenid.setText("解析出错");
            txtNick.setText("");
        }
    }

    @Override
    public void handlePayOrder(JSONObject data) {
        try {
            Integer code = data.getInt("code");

            if (code == 0) {
                JSONObject t = data.getJSONObject("data");
                txtOpenid.setText("AppID: " + t.getString("AppID"));
                txtNick.setText("PrepayID: " + t.getString("PrepayID"));

                prepayid = t.getString("PrepayID");
                nonce = t.getString("Nonce");
                timestamp = t.getString("TimeStamp");
                sign = t.getString("sign");
            } else {
                String msg = data.getString("msg");
                txtOpenid.setText(msg);
                txtNick.setText("");
            }
        } catch (JSONException e) {
            txtOpenid.setText("解析出错");
            txtNick.setText("");
        }
    }

    public void onClickGetTestPayOrder(View view) {
        try {
            JSONObject tmpToken = new JSONObject();
            tmpToken.put("EncryptStr", encodeURIComponent(token.getString("EncryptStr")));
            tmpToken.put("EncryptKey", encodeURIComponent(token.getString("EncryptKey")));
            BiwanRequest.testOrderReq(this, appid, tmpToken, "1000游戏币", 10.0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onClickGetTestUserInfo(View view) {
        if (Code == null) {
            dialogTip(this, "未授权");
            return;
        }
        try {
            BiwanRequest.testUserinfoReq(this, Code);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onClickAuthorise(View view) {
        if (BYBSDK.isInstalled())
            BYBSDK.authorise();
        else
            dialogTip(this, "请先安装币游宝APP");
    }

    public void onClickPay(View v) {
        BYBSDK.pay(prepayid, nonce, timestamp, sign);
    }

    public static String encodeURIComponent(String s) {
        String result = null;

        try {
            result = URLEncoder.encode(s, "UTF-8")
                    .replaceAll("\\+", "%20")
                    .replaceAll("\\%21", "!")
                    .replaceAll("\\%27", "'")
                    .replaceAll("\\%28", "(")
                    .replaceAll("\\%29", ")")
                    .replaceAll("\\%7E", "~");
        }

        // This exception should never occur.
        catch (UnsupportedEncodingException e) {
            result = s;
        }

        return result;
    }

}
