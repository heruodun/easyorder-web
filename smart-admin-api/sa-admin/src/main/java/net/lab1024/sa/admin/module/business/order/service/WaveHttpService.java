package net.lab1024.sa.admin.module.business.order.service;



import com.alibaba.fastjson2.*;
import javafx.beans.binding.ObjectExpression;
import okhttp3.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class WaveHttpService {

    private static final MediaType JSON_TYPE = MediaType.get("application/json; charset=utf-8");


    public static Map<Integer, Object> get(int[] waveIds) {
        final String SERVICE_URL1 = "http://localhost:5000/wave/orders";
        OkHttpClient client = new OkHttpClient();
        String jsonRequestBody = JSON.toJSONString(waveIds);
        RequestBody body = RequestBody.create(jsonRequestBody, JSON_TYPE);

        Request request = new Request.Builder()
                .url(SERVICE_URL1)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                System.out.println("Response JSON: " + responseBody);

                // 直接解析整个结构，注意这里的路径“waves”需要根据实际的JSON结构进行调整
                JSONObject jsonResponse = JSON.parseObject(responseBody);
                JSONObject waves = jsonResponse.getJSONObject("waves");
                Map<Integer, Object> resultMap = new HashMap<>();

                for (String waveId : waves.keySet()) {
                    JSONObject waveDetails = waves.getJSONObject(waveId);
                    resultMap.put(Integer.parseInt(waveId), waveDetails);
                }

                return resultMap;
            } else {
                System.err.println("Error: " + response.code() + ", Message: " + response.message());
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Boolean ship(int waveId, String operator) {
        final String SERVICE_URL = "http://localhost:5000/order/operation";
        OkHttpClient client = new OkHttpClient();
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("wave_id", waveId);
        requestMap.put("operator", operator);
        String jsonRequestBody = JSON.toJSONString(requestMap);
        RequestBody body = RequestBody.create(jsonRequestBody, JSON_TYPE);

        Request request = new Request.Builder()
                .url(SERVICE_URL)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                System.out.println("Response JSON: " + responseBody);

                // 直接解析整个结构，注意这里的路径“waves”需要根据实际的JSON结构进行调整
                JSONObject jsonResponse = JSON.parseObject(responseBody);
                System.out.println(jsonResponse);

                return true;
            } else {
                System.err.println("Error: " + response.code() + ", Message: " + response.message());
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    public static JSONObject getOrder(Long orderId) {

        final String SERVICE_URL2 =  "http://localhost:5000/order?order_id=" + orderId;

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(SERVICE_URL2)
                .build(); // 构建请求

        try {
            Response response = client.newCall(request).execute(); // 同步调用, 获取响应

            // 确保HTTP请求成功
            if (response.isSuccessful() && response.body() != null) {
                ResponseBody responseBody = response.body();
                String responseBodyStr = responseBody.string(); // 获取响应体的字符串
                // 打印或者根据需要处理响应体的内容
                System.out.println("Response Body: " + responseBodyStr);
                responseBody.close(); // 关闭响应体，释放资源

                // 此处简单地返回响应体的字符串作为示例
                // 实际应用中应根据需要对该字符串进行解析（例如转换为JSON对象）
                JSONObject jsonResponse = JSON.parseObject(responseBodyStr);
                return jsonResponse;
            }

        } catch (IOException e) { // 处理网络或其他IO错误
            e.printStackTrace();
        }
        return null; // 出错或响应码非成功时返回null
    }
}