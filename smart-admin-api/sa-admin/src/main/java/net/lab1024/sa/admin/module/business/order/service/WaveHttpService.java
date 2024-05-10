package net.lab1024.sa.admin.module.business.order.service;



import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import okhttp3.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class WaveHttpService {

    private static final MediaType JSON_TYPE = MediaType.get("application/json; charset=utf-8");
    private static final String SERVICE_URL = "http://localhost:5000/wave/orders";

    public static Map<Integer, Object> get(int[] waveIds) {
        OkHttpClient client = new OkHttpClient();
        String jsonRequestBody = JSON.toJSONString(waveIds);
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
}