package com.test.requestjsontest.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.test.requestjsontest.R;
import com.test.requestjsontest.db.JsonData;
import com.test.requestjsontest.util.HttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

public class FirstActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
    }

    @Override
    public void onResume() {
        super.onResume();

        requestResult("https://cdn.heweather.com/china-city-list.json");

        onCreateDialog();
    }


    /**
     * 获取数据后将数据存储在数据库中
     * @param address 获取数据的地址URI
     */
    private void requestResult(String address) {
        HttpUtil.sendHttpRequest(address, new okhttp3.Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                //parseData(subString(responseData));
                parseData(responseData);
            }

            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(FirstActivity.this, "数据获取失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 去除网站返回数据的说明部分
     * @param data 网站的返回数据
     * @return 去除说明部分之后的json数据
     */
    public static String subString(String data) {
        String returnData = "";
        int start = data.indexOf("[");
        if (start != -1) {
            returnData =  data.substring(start);
        }
        return returnData;
    }

    /**
     * 解析json数据
     * @param data json数据
     */
    private void parseData(final String data) {
        try {
            JSONArray jsonArray = new JSONArray(data);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                JsonData jsonData = new JsonData();

                jsonData.setCode(jsonObject.getString("id"));
                jsonData.setCity(jsonObject.getString("cityZh"));
                jsonData.setProvince(jsonObject.getString("provinceZh"));
                jsonData.setLeader(jsonObject.getString("leaderZh"));
                jsonData.save();

                updateProgressDialog(i/3181*100);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        finally {

            //数据加载完毕之后，关闭进度对话框，并且回到MainActivity
            closeProgressDialog();
            Intent backIntent = new Intent(FirstActivity.this, MainActivity.class);
            startActivity(backIntent);
            //销毁当前活动
            finish();
        }
    }

    /**
     * 创建进度条
     */
    private void onCreateDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(FirstActivity.this);
            progressDialog.setMessage("正在加载数据,请稍后...");
            progressDialog.setCanceledOnTouchOutside(false);

            //修改ProgressDialog的风格，让其显示出进度条
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setProgress(0);
            progressDialog.setMax(100);
        }
        progressDialog.show();
    }

    /**
     * 在主线程中更新进度条
     */
    private void updateProgressDialog(final int progress) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog.setProgress(progress);
            }
        });

    }

    /**
     * 关闭进度对话框
     */
    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
