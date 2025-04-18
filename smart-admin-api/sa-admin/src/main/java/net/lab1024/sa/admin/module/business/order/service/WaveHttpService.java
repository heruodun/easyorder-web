//package net.lab1024.sa.admin.module.business.order.service;
//
//
//
//import com.alibaba.fastjson2.*;
//import lombok.extern.slf4j.Slf4j;
//import net.lab1024.sa.admin.module.business.order.constant.OrderUtil;
//import net.lab1024.sa.admin.module.business.order.sales.domain.vo.WaveAddressVO;
//import net.lab1024.sa.admin.module.business.order.sales.domain.vo.OrderSalesVO;
//import net.lab1024.sa.admin.module.business.order.sales.domain.vo.WaveDetailVO;
//import net.lab1024.sa.base.common.code.OrderErrorCode;
//import net.lab1024.sa.base.common.domain.RequestUser;
//import net.lab1024.sa.base.common.domain.ResponseDTO;
//import net.lab1024.sa.base.common.util.SmartRequestUtil;
//import okhttp3.*;
//
//import java.io.IOException;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@Slf4j
//public class WaveHttpService {
//
//    private static final MediaType JSON_TYPE = MediaType.get("application/json; charset=utf-8");
//
//
//    public static Map<Integer, Object> get(int[] waveIds) {
//        final String SERVICE_URL1 = "http://localhost:5000/wave/orders";
//        OkHttpClient client = new OkHttpClient();
//        String jsonRequestBody = JSON.toJSONString(waveIds);
//        RequestBody body = RequestBody.create(jsonRequestBody, JSON_TYPE);
//
//        Request request = new Request.Builder()
//                .url(SERVICE_URL1)
//                .post(body)
//                .build();
//
//        try (Response response = client.newCall(request).execute()) {
//            if (response.isSuccessful() && response.body() != null) {
//                String responseBody = response.body().string();
//                System.out.println("Response JSON: " + responseBody);
//
//                // 直接解析整个结构，注意这里的路径“waves”需要根据实际的JSON结构进行调整
//                JSONObject jsonResponse = JSON.parseObject(responseBody);
//                JSONObject waves = jsonResponse.getJSONObject("waves");
//                Map<Integer, Object> resultMap = new HashMap<>();
//
//                for (String waveId : waves.keySet()) {
//                    JSONObject waveDetails = waves.getJSONObject(waveId);
//                    resultMap.put(Integer.parseInt(waveId), waveDetails);
//                }
//
//                return resultMap;
//            } else {
//                System.err.println("Error: " + response.code() + ", Message: " + response.message());
//                return null;
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    public static Map<Integer, WaveDetailVO> get4New(int[] waveIds) {
//        final String SERVICE_URL1 = "http://localhost:5000/wave/orders";
//        OkHttpClient client = new OkHttpClient();
//        String jsonRequestBody = JSON.toJSONString(waveIds);
//        RequestBody body = RequestBody.create(jsonRequestBody, JSON_TYPE);
//
//        Request request = new Request.Builder()
//                .url(SERVICE_URL1)
//                .post(body)
//                .build();
//
//        try (Response response = client.newCall(request).execute()) {
//            if (response.isSuccessful() && response.body() != null) {
//                String responseBody = response.body().string();
//                System.out.println("Response JSON: " + responseBody);
//
//                // 直接解析整个结构，注意这里的路径“waves”需要根据实际的JSON结构进行调整
//                JSONObject jsonResponse = JSON.parseObject(responseBody);
//                JSONObject waves = jsonResponse.getJSONObject("waves");
//
//                Map<Integer, WaveDetailVO> resultMap = new HashMap<>();
//
//                for (String waveId : waves.keySet()) {
//                    WaveDetailVO waveDetailVO = new WaveDetailVO();
//                    JSONObject waveDetails = waves.getJSONObject(waveId);
//                    int addressCount = waveDetails.getIntValue("addressCount");
//                    int totalCount = waveDetails.getIntValue("totalCount");
//                    waveDetailVO.setAddressCount(addressCount);
//                    waveDetailVO.setTotalCount(totalCount);
//                    JSONArray addressesArray = waveDetails.getJSONArray("addresses");
//                    List<WaveAddressVO> addressList = new ArrayList<>();
//                    for (int i = 0; i < addressesArray.size(); i++) {
//                        JSONObject addressJson = addressesArray.getJSONObject(i);
//                        WaveAddressVO orderAddressedVO = new WaveAddressVO();
//                        orderAddressedVO.setAddress(addressJson.getString("address"));  // Change Integer to String due to structure
//                        orderAddressedVO.setOrderCount(addressJson.getIntValue("orderCount"));
//
//                        // Step 4: Extract orders array
//                        JSONArray ordersArray = addressJson.getJSONArray("orders");
//                        List<OrderSalesVO> orderList = new ArrayList<>();
//                        for (int j = 0; j < ordersArray.size(); j++) {
//                            JSONObject orderJson = ordersArray.getJSONObject(j);
//                            OrderSalesVO orderSalesVO = new OrderSalesVO();
//                            orderSalesVO.setId(orderJson.getLong("id"));
//                            orderSalesVO.setOrderId(orderJson.getLong("order_id"));
//                            orderSalesVO.setAddress(orderJson.getString("address"));
//                            orderSalesVO.setCurStatus(orderJson.getString("cur_status"));
//                            orderSalesVO.setCurTime(LocalDateTime.ofEpochSecond(orderJson.getLong("cur_time") / 1000, 0, java.time.ZoneOffset.UTC));
//                            orderSalesVO.setCreator(orderJson.getString("printer"));
//                            orderSalesVO.setWaveId(orderJson.getInteger("wave_id"));
//                            orderSalesVO.setDetail(orderJson.getString("content"));
//                            orderSalesVO.setCurOperator(orderJson.getString("cur_man"));
//                            orderSalesVO.setTrace(new ArrayList<>());
//                            orderSalesVO.setCreateTime(LocalDateTime.ofEpochSecond(orderJson.getLong("print_time") / 1000, 0, java.time.ZoneOffset.UTC));
//                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//                            orderSalesVO.setUpdateTime(LocalDateTime.parse(orderJson.getString("update_time"), formatter));
//
//                            // Add the orderSalesVO to the list
//                            orderList.add(orderSalesVO);
//                        }
//
//                        // Set orders in OrderAddressedVO
//                        orderAddressedVO.setOrders(orderList);
//                        // Add to address list
//                        addressList.add(orderAddressedVO);
//                        waveDetailVO.setOrders(addressList);
//                    }
//                    resultMap.put(Integer.parseInt(waveId), waveDetailVO);
//                }
//                return resultMap;
//            } else {
//                System.err.println("Error: " + response.code() + ", Message: " + response.message());
//                return null;
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    public static Boolean ship(int waveId, String operator) {
//        final String SERVICE_URL = "http://localhost:5000/order/operation";
//        OkHttpClient client = new OkHttpClient();
//        Map<String, Object> requestMap = new HashMap<>();
//        requestMap.put("wave_id", waveId);
//        requestMap.put("operator", operator);
//        String jsonRequestBody = JSON.toJSONString(requestMap);
//        RequestBody body = RequestBody.create(jsonRequestBody, JSON_TYPE);
//
//        Request request = new Request.Builder()
//                .url(SERVICE_URL)
//                .post(body)
//                .build();
//
//        try (Response response = client.newCall(request).execute()) {
//            if (response.isSuccessful() && response.body() != null) {
//                String responseBody = response.body().string();
//                System.out.println("Response JSON: " + responseBody);
//
//                // 直接解析整个结构，注意这里的路径“waves”需要根据实际的JSON结构进行调整
//                JSONObject jsonResponse = JSON.parseObject(responseBody);
//                System.out.println(jsonResponse);
//
//                return true;
//            } else {
//                log.error("Error: " + response.code() + ", Message: " + response.message());
//                return null;
//            }
//        } catch (IOException e) {
//            log.error("",e);
//            return false;
//        }
//    }
//
//    public static JSONObject getOrderById(Long id) {
//
//        final String SERVICE_URL2 =  "http://localhost:5000/order/id?id=" + id;
//
//        OkHttpClient client = new OkHttpClient();
//
//        Request request = new Request.Builder()
//                .url(SERVICE_URL2)
//                .build(); // 构建请求
//
//        try {
//            Response response = client.newCall(request).execute(); // 同步调用, 获取响应
//
//            // 确保HTTP请求成功
//            if (response.isSuccessful() && response.body() != null) {
//                ResponseBody responseBody = response.body();
//                String responseBodyStr = responseBody.string(); // 获取响应体的字符串
//                // 打印或者根据需要处理响应体的内容
//                System.out.println("Response Body: " + responseBodyStr);
//                responseBody.close(); // 关闭响应体，释放资源
//
//                // 此处简单地返回响应体的字符串作为示例
//                // 实际应用中应根据需要对该字符串进行解析（例如转换为JSON对象）
//                JSONObject jsonResponse = JSON.parseObject(responseBodyStr);
//                return jsonResponse;
//            }
//
//        } catch (IOException e) { // 处理网络或其他IO错误
//            e.printStackTrace();
//        }
//        return null; // 出错或响应码非成功时返回null
//    }
//
//
//    public static JSONObject getOrder(Long orderId) {
//
//        final String SERVICE_URL2 =  "http://localhost:5000/order?order_id=" + orderId;
//
//        OkHttpClient client = new OkHttpClient();
//
//        Request request = new Request.Builder()
//                .url(SERVICE_URL2)
//                .build(); // 构建请求
//
//        try {
//            Response response = client.newCall(request).execute(); // 同步调用, 获取响应
//
//            // 确保HTTP请求成功
//            if (response.isSuccessful() && response.body() != null) {
//                ResponseBody responseBody = response.body();
//                String responseBodyStr = responseBody.string(); // 获取响应体的字符串
//                // 打印或者根据需要处理响应体的内容
//                System.out.println("Response Body: " + responseBodyStr);
//                responseBody.close(); // 关闭响应体，释放资源
//
//                // 此处简单地返回响应体的字符串作为示例
//                // 实际应用中应根据需要对该字符串进行解析（例如转换为JSON对象）
//                JSONObject jsonResponse = JSON.parseObject(responseBodyStr);
//                return jsonResponse;
//            }
//
//        } catch (IOException e) { // 处理网络或其他IO错误
//            e.printStackTrace();
//        }
//        return null; // 出错或响应码非成功时返回null
//    }
//
//
//
//    public static JSONObject getOrdersByKeyword(String keyword, int limit, int offset, int type) {
//        String SERVICE_URL1 = "http://localhost:5000/order/search";
//        if(type == 1){
//            SERVICE_URL1 = "http://localhost:5000/order/search2";
//        }
//        OkHttpClient client = new OkHttpClient();
//
//        Map<String, Object> requestMap = new HashMap<>();
//        requestMap.put("keyword", keyword);
//        requestMap.put("limit", limit);
//        requestMap.put("offset", offset);
//        String jsonRequestBody = JSON.toJSONString(requestMap);
//        RequestBody body = RequestBody.create(jsonRequestBody, JSON_TYPE);
//
//        Request request = new Request.Builder()
//                .url(SERVICE_URL1)
//                .post(body)
//                .build();
//
//        try (Response response = client.newCall(request).execute()) {
//            if (response.isSuccessful() && response.body() != null) {
//                String responseBody = response.body().string();
//                System.out.println("Response JSON: " + responseBody);
//
//                // 直接解析整个结构，注意这里的路径“waves”需要根据实际的JSON结构进行调整
//                JSONObject jsonResponse = JSON.parseObject(responseBody);
////                JSONArray orders = jsonResponse.getJSONArray("orders");
//                return jsonResponse;
//            } else {
//                System.err.println("Error: " + response.code() + ", Message: " + response.message());
//                return null;
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    public static ResponseDTO<Boolean> operation(String orderIdQr, String operation) {
//        Long orderId = OrderUtil.parseOrderInfo(orderIdQr).getOrderId();
//        if(orderId == null){
//            return ResponseDTO.error(OrderErrorCode.ILLEGAL_ORDER_ID, "非法订单号~");
//        }
//        RequestUser requestUser = SmartRequestUtil.getRequestUser();
//        final String SERVICE_URL = "http://localhost:5000/order/operation2";
//        OkHttpClient client = new OkHttpClient();
//        Map<String, Object> requestMap = new HashMap<>();
//        requestMap.put("order_id", orderId);
//        requestMap.put("operator", requestUser.getUserName());
//        int type = 0;
//        if("peihuo".equals(operation)){
//            type = 100;
//        }
//
//        if("duijie".equals(operation)){
//            type = 200;
//        }
//
//        requestMap.put("type", type);
//        String jsonRequestBody = JSON.toJSONString(requestMap);
//        RequestBody body = RequestBody.create(jsonRequestBody, JSON_TYPE);
//
//        Request request = new Request.Builder()
//                .url(SERVICE_URL)
//                .post(body)
//                .build();
//
//        try (Response response = client.newCall(request).execute()) {
//            if (response.isSuccessful() && response.body() != null) {
//                String responseBody = response.body().string();
//                System.out.println("Response JSON: " + responseBody);
//
//                // 直接解析整个结构，注意这里的路径“waves”需要根据实际的JSON结构进行调整
//                JSONObject jsonResponse = JSON.parseObject(responseBody);
//                System.out.println(jsonResponse);
//
//                return ResponseDTO.ok();
//            } else {
//                System.err.println("Error: " + response.code() + ", Message: " + response.message());
//                return ResponseDTO.error(OrderErrorCode.FORM_REPEAT_SUBMIT, "扫码失败，请重试");
//            }
//        } catch (IOException e) {
//            log.error(e.getMessage(), e);
//            return ResponseDTO.error(OrderErrorCode.FORM_REPEAT_SUBMIT, "扫码服务异常，请重试");
//        }
//    }
//    public static Boolean operation2(Long orderId, int waveId, String operator, int operation) {
//        final String SERVICE_URL = "http://localhost:5000/wave/operation";
//        OkHttpClient client = new OkHttpClient();
//        Map<String, Object> requestMap = new HashMap<>();
//        requestMap.put("order_id", orderId);
//        requestMap.put("wave_id", waveId);
//        requestMap.put("operator", operator);
//        requestMap.put("operation", operation);
//        requestMap.put("wave_create_time", System.currentTimeMillis());
//
//        String jsonRequestBody = JSON.toJSONString(requestMap);
//        RequestBody body = RequestBody.create(jsonRequestBody, JSON_TYPE);
//
//        Request request = new Request.Builder()
//                .url(SERVICE_URL)
//                .post(body)
//                .build();
//
//        try (Response response = client.newCall(request).execute()) {
//            if (response.isSuccessful() && response.body() != null) {
//                String responseBody = response.body().string();
//                System.out.println("Response JSON: " + responseBody);
//
//                // 直接解析整个结构，注意这里的路径“waves”需要根据实际的JSON结构进行调整
//                JSONObject jsonResponse = JSON.parseObject(responseBody);
//                System.out.println(jsonResponse);
//
//                return true;
//            } else {
//                System.err.println("Error: " + response.code() + ", Message: " + response.message());
//                return false;
//            }
//        } catch (IOException e) {
//            log.error(e.getMessage(), e);
//            return false;
//        }
//    }
//
//
//}
