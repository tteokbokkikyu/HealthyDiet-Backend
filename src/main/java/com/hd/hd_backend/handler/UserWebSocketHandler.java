package com.hd.hd_backend.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hd.hd_backend.dto.*;
import com.hd.hd_backend.entity.*;
import com.hd.hd_backend.mapper.*;
import com.hd.hd_backend.service.*;
import com.hd.hd_backend.utils.JsonUtils;
import com.hd.hd_backend.utils.WebSocketCode;
import com.hd.hd_backend.utils.WebSocketSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Component
public class UserWebSocketHandler extends TextWebSocketHandler {
    @Autowired
    private UserService userService;
    @Autowired
    private FoodService foodService;
    @Autowired
    private WeightService weightService;
    // 错误映射
    private static final Map<String, String> errorMapping = new HashMap<>();
    static {
        errorMapping.put("手机号不能为空", "{\"error_code\":400,\"error_message\":\"手机号不能为空\"}");
        errorMapping.put("密码不能为空", "{\"error_code\":400,\"error_message\":\"密码不能为空\"}");
        errorMapping.put("用户不存在", "{\"error_code\":404,\"error_message\":\"用户不存在\"}");
        errorMapping.put("密码错误", "{\"error_code\":401,\"error_message\":\"密码错误\"}");
        errorMapping.put("账号已被封禁", "{\"error_code\":403,\"error_message\":\"账号已被封禁\"}");
    }
    private final ObjectMapper objectMapper = new ObjectMapper(); // 创建 ObjectMapper 实例

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        System.out.println("文本消息"); // 输出解析后的用户信息
        String payload = message.getPayload();
        // 解析消息并执行相应的操作
        // 假设消息格式为 "action:payload"
        String[] parts = payload.split(":", 2); // 限制分割为 2 部分
        String action = parts[0];
        UserDTO userDTO;

        switch (action) {
            case "register":
                userDTO = parseUserDTO(parts[1]);
                if (userDTO != null) {
                    System.out.println("解析后的用户信息: " + userDTO); // 输出解析后的用户信息
                    try {
                        NormalUser user = userService.register(userDTO);



                        String successMessage = String.format(
                                "{\"status\":200,\"message\":\"注册成功!\",\"user\":%s}",
                                UserToJson(user)
                        );
                        session.sendMessage(new TextMessage(successMessage));

                        WebSocketSessionManager.addSession(user.getUserId(), session);
                        session.getAttributes().put("userId", user.getUserId());
                        System.out.println(user.getUserId()); // 输出
                    } catch (Exception e) {
                        switch (e.getMessage()){
                            case "用户名已存在":
                                session.sendMessage(new TextMessage("{\"error_code\":\"1\",\"error_message\":\""+e.getMessage()+"\"}"));
                                break;
                            default:
                                session.sendMessage(new TextMessage("{\"error_code\":\"100\",\"error_message\":\""+e.getMessage()+"\"}"));



                        }
                    }

                } else {
                    session.sendMessage(new TextMessage("解析用户信息失败"));
                }
                break;
            case "login":
                userDTO = parseUserDTO(parts[1]);
                if (userDTO != null) {
                    System.out.println("解析后的用户信息: " + userDTO); // 输出解析后的用户信息
                    try {
                        NormalUser user = userService.login(userDTO);
                        session.sendMessage(new TextMessage(UserToJson(user)) );
                        WebSocketSessionManager.addSession(user.getUserId(), session);
                        session.getAttributes().put("userId", user.getUserId());
                        System.out.println(user.getUserId()); // 输出
                    }catch (Exception e) {
                        // 根据异常消息返回相应的错误码和错误信息
                        String errorResponse = handleLoginError(e.getMessage());
                        session.sendMessage(new TextMessage(errorResponse));
                    }
                } else {
                    session.sendMessage(new TextMessage("{\"error_code\":\"1\",\"error_message\":\"数据格式错误\"}"));
                }
                break;
            case "getAllFood":
                List<FoodItem> allFood = foodService.getAllFoodItems();
                session.sendMessage(new TextMessage(JsonUtils.toJsonMsg(WebSocketCode.FOOD_LIST_SUCCESS.ordinal(),allFood,"data")) );
                break;
            case "getFoodByName":
                if (parts.length > 1) {
                    FoodItem food = foodService.getFoodItemByName(parts[1]);
                    if (food != null) {
                        session.sendMessage(new TextMessage(JsonUtils.toJsonMsg(WebSocketCode.FOOD_ITEM_GET_SUCCESS.ordinal(),food,"data")) );
                    } else {
                        session.sendMessage(new TextMessage(JsonUtils.toJsonMsg(WebSocketCode.FOOD_ITEM_GET_FAIL.ordinal(),"食物未找到","error_message")) );
                    }
                }
                break;
            case "getAllFoodRecord":
                if (parts.length > 1) {
                    int userId = Integer.parseInt(parts[1]);
                    try {
                        List<FoodRecordDTO> foodRecords = foodService.getFoodRecordsByUserId(userId);
                        if (foodRecords != null && !foodRecords.isEmpty()) {

                            session.sendMessage(new TextMessage(JsonUtils.toJsonMsg(WebSocketCode.FOOD_RECORD_GET_SUCCESS.ordinal(),foodRecords,"data")) );
                        } else {
                            session.sendMessage(new TextMessage(JsonUtils.toJsonMsg(WebSocketCode.FOOD_RECORD_GET_FAIL.ordinal(),"未找到食物记录","error_message")));
                        }
                    } catch (Exception e) {
                        session.sendMessage(new TextMessage(JsonUtils.toJsonMsg(WebSocketCode.FOOD_RECORD_GET_FAIL.ordinal(),"获取食物记录失败","error_message")));
                        System.out.println(e.getMessage());
                    }
                } else {
                    session.sendMessage(new TextMessage(JsonUtils.toJsonMsg(WebSocketCode.FOOD_RECORD_GET_FAIL.ordinal(),"缺少参数","error_message")));
                }
                break;
            case "getFoodRecord":
                if (parts.length > 1) {
                    int foodRecordId = Integer.parseInt(parts[1]);
                    try {
                        FoodRecordDTO foodRecord = foodService.getFoodRecordById(foodRecordId);
                        if (foodRecord != null) {
                            session.sendMessage(new TextMessage(JsonUtils.toJsonMsg(WebSocketCode.FOOD_RECORD_GET_SUCCESS.ordinal(),foodRecord,"data")) );
                        } else {
                            session.sendMessage(new TextMessage(JsonUtils.toJsonMsg(WebSocketCode.FOOD_RECORD_GET_FAIL.ordinal(),"未找到食物记录","error_message")));
                        }
                    } catch (Exception e) {
                        session.sendMessage(new TextMessage(JsonUtils.toJsonMsg(WebSocketCode.FOOD_RECORD_GET_FAIL.ordinal(),"获取食物记录失败","error_message")));
                        System.out.println(e.getMessage());
                    }
                } else {
                    session.sendMessage(new TextMessage(JsonUtils.toJsonMsg(WebSocketCode.FOOD_RECORD_GET_FAIL.ordinal(),"缺少参数","error_message")));
                }
                break;
            case "addFoodRecord":
                if (parts.length > 1) {
                    try {
                        System.out.println("Received data: " + parts[1]);
                        FoodRecord foodRecord = objectMapper.readValue(parts[1], FoodRecord.class);
                        foodService.addFoodRecord(foodRecord);
                        session.sendMessage(new TextMessage(JsonUtils.toJsonMsg(WebSocketCode.FOOD_RECORD_ADD_SUCCESS.ordinal(),"食物记录添加成功","message")) );
                    } catch (Exception e) {
                        session.sendMessage(new TextMessage(JsonUtils.toJsonMsg(WebSocketCode.FOOD_RECORD_ADD_FAIL.ordinal(),e.getMessage(),"error_message")) );
                    }
                } else {
                    session.sendMessage(new TextMessage(JsonUtils.toJsonMsg(WebSocketCode.FOOD_RECORD_ADD_FAIL.ordinal(),"缺少食物记录数据","error_message")) );
                }
                break;
            case "updateFoodRecord":
                if (parts.length > 1) {
                    try {
                        FoodRecord foodRecord = objectMapper.readValue(parts[1], FoodRecord.class);
                        foodService.updateFoodRecord(foodRecord);
                        session.sendMessage(new TextMessage(JsonUtils.toJsonMsg(WebSocketCode.FOOD_RECORD_UPDATE_SUCCESS.ordinal(),"食物记录更新成功","message")) );
                    } catch (Exception e) {
                        session.sendMessage(new TextMessage(JsonUtils.toJsonMsg(WebSocketCode.FOOD_RECORD_UPDATE_FAIL.ordinal(),e.getMessage(),"error_message")) );
                    }
                } else {
                    session.sendMessage(new TextMessage(JsonUtils.toJsonMsg(WebSocketCode.FOOD_RECORD_UPDATE_FAIL.ordinal(),"缺少食物记录数据","error_message")) );
                }
                break;
            case "deleteFoodRecord":
                if (parts.length > 1) {
                    try {
                        int foodRecordId = Integer.parseInt(parts[1]);
                        foodService.deleteFoodRecord(foodRecordId);
                        session.sendMessage(new TextMessage(JsonUtils.toJsonMsg(WebSocketCode.FOOD_RECORD_DELETE_SUCCESS.ordinal(),"食物记录删除成功","message")) );
                    } catch (Exception e) {
                        session.sendMessage(new TextMessage(JsonUtils.toJsonMsg(WebSocketCode.FOOD_RECORD_DELETE_FAIL.ordinal(), e.getMessage(),"error_message")) );
                    }
                } else {
                    session.sendMessage(new TextMessage(JsonUtils.toJsonMsg(WebSocketCode.FOOD_RECORD_DELETE_FAIL.ordinal(), "缺少食物记录ID","error_message")) );
                }
                break;
            case "updateUser":
                if (!session.getAttributes().containsKey("userId")) {
                    session.sendMessage(new TextMessage(JsonUtils.toJsonMsg(WebSocketCode.UPDATE_USER_FAIL.ordinal(), "用户未登录","error_message")) );
                    break;
                }

                try {
                    // 直接使用NormalUser接收更新信息
                    NormalUser updateInfo = objectMapper.readValue(parts[1], NormalUser.class);
                    int userId = (Integer) session.getAttributes().get("userId");
                    updateInfo.setUserId(userId); // 设置userId确保更新正确的用户

                    userService.updateUser(userId, updateInfo);

                    // 获取更新后的用户信息
                    NormalUser updatedUser = userService.getUserById(userId);  // 使用service而不是直接访问mapper

                    session.sendMessage(new TextMessage("{\"code\":"+String.valueOf(WebSocketCode.UPDATE_USER_SUCCESS.ordinal())+",\"status\":200,\"message\":\"用户信息更新成功\",\"data\":" + JsonUtils.toJson(updatedUser) + "}"));
                } catch (Exception e) {
                    session.sendMessage(new TextMessage(JsonUtils.toJsonMsg(WebSocketCode.UPDATE_USER_FAIL.ordinal(), e.getMessage(),"error_message")) );
                    e.printStackTrace();
                }
                break;
            case "addFoodItem":
                if (!session.getAttributes().containsKey("userId")) {
                    session.sendMessage(new TextMessage(JsonUtils.toJsonMsg(WebSocketCode.FOOD_ITEM_ADD_FAIL.ordinal(), "用户未登录","error_message")) );
                    break;
                }

                try {
                    FoodItem newFoodItem = objectMapper.readValue(parts[1], FoodItem.class);
                    foodService.addFoodItem(newFoodItem);
                    session.sendMessage(new TextMessage(JsonUtils.toJsonMsg(WebSocketCode.FOOD_ITEM_ADD_SUCCESS.ordinal(), "食物添加成功","message")) );
                } catch (Exception e) {
                    session.sendMessage(new TextMessage(JsonUtils.toJsonMsg(WebSocketCode.FOOD_ITEM_ADD_FAIL.ordinal(), e.getMessage(),"error_message")) );
                }
                break;

            case "updateFoodItem":
                if (!session.getAttributes().containsKey("userId")) {
                    session.sendMessage(new TextMessage(JsonUtils.toJsonMsg(WebSocketCode.FOOD_ITEM_UPDATE_FAIL.ordinal(), "用户未登录","error_message")) );
                    break;
                }

                try {
                    FoodItem updateFoodItem = objectMapper.readValue(parts[1], FoodItem.class);
                    foodService.updateFoodItem(updateFoodItem);
                    session.sendMessage(new TextMessage(JsonUtils.toJsonMsg(WebSocketCode.FOOD_ITEM_UPDATE_SUCCESS.ordinal(), "食物更新成功","message")) );
                } catch (Exception e) {
                    session.sendMessage(new TextMessage(JsonUtils.toJsonMsg(WebSocketCode.FOOD_ITEM_UPDATE_FAIL.ordinal(), e.getMessage(),"error_message")) );

                }
                break;

            case "deleteFoodItem":
                if (!session.getAttributes().containsKey("userId")) {
                    session.sendMessage(new TextMessage(JsonUtils.toJsonMsg(WebSocketCode.FOOD_ITEM_DELETE_FAIL.ordinal(), "用户未登录","error_message")) );

                    break;
                }

                try {
                    Integer foodId = Integer.parseInt(parts[1]);
                    foodService.deleteFoodItem(foodId);
                    session.sendMessage(new TextMessage(JsonUtils.toJsonMsg(WebSocketCode.FOOD_ITEM_DELETE_SUCCESS.ordinal(), "食物删除成功","message")) );
                } catch (Exception e) {
                    session.sendMessage(new TextMessage(JsonUtils.toJsonMsg(WebSocketCode.FOOD_ITEM_DELETE_FAIL.ordinal(), e.getMessage(),"error_message")) );

                }
                break;
            case "addWeight":
                if (!session.getAttributes().containsKey("userId")) {
                    session.sendMessage(new TextMessage(JsonUtils.toJsonMsg(
                            WebSocketCode.WEIGHT_ADD_FAIL.ordinal(),
                            "用户未登录",
                            "error_message"
                    )));
                    break;
                }
                try {
                    // 创建Weight对象并设置属性
                    Weight weight = new Weight();
                    weight.setUserId((Integer) session.getAttributes().get("userId"));
                    weight.setWeight(Double.parseDouble(parts[1]));

                    weightService.addWeight(weight);
                    session.sendMessage(new TextMessage(JsonUtils.toJsonMsg(
                            WebSocketCode.WEIGHT_ADD_SUCCESS.ordinal(),
                            "体重记录添加成功",
                            "message"
                    )));
                } catch (Exception e) {
                    session.sendMessage(new TextMessage(JsonUtils.toJsonMsg(
                            WebSocketCode.WEIGHT_ADD_FAIL.ordinal(),
                            e.getMessage(),
                            "error_message"
                    )));
                }
                break;
            case "deleteWeight":
                if (!session.getAttributes().containsKey("userId")) {
                    session.sendMessage(new TextMessage(JsonUtils.toJsonMsg(WebSocketCode.WEIGHT_DELETE_FAIL.ordinal(), "用户未登录","error_message")));
                    break;
                }
                try {
                    Integer userId = (Integer) session.getAttributes().get("userId");
                    String time = parts[1];
                    weightService.deleteWeight(userId, time);
                    session.sendMessage(new TextMessage(JsonUtils.toJsonMsg(WebSocketCode.WEIGHT_DELETE_SUCCESS.ordinal(), "体重记录删除成功","message")));
                } catch (Exception e) {
                    session.sendMessage(new TextMessage(JsonUtils.toJsonMsg(WebSocketCode.WEIGHT_DELETE_FAIL.ordinal(), e.getMessage(),"error_message")));
                }
                break;
            case "updateWeight":
                if (!session.getAttributes().containsKey("userId")) {
                    session.sendMessage(new TextMessage(JsonUtils.toJsonMsg(WebSocketCode.WEIGHT_UPDATE_FAIL.ordinal(), "用户未登录","error_message")));
                    break;
                }
                try {
                    Weight weight = objectMapper.readValue(parts[1], Weight.class);
                    weight.setUserId((Integer) session.getAttributes().get("userId"));
                    weightService.updateWeight(weight);
                    session.sendMessage(new TextMessage(JsonUtils.toJsonMsg(WebSocketCode.WEIGHT_UPDATE_SUCCESS.ordinal(), "体重记录更新成功","message")));
                } catch (Exception e) {
                    session.sendMessage(new TextMessage(JsonUtils.toJsonMsg(WebSocketCode.WEIGHT_UPDATE_FAIL.ordinal(), e.getMessage(),"error_message")));
                }
                break;
            case "getUserWeights":
                if (!session.getAttributes().containsKey("userId")) {
                    session.sendMessage(new TextMessage(JsonUtils.toJsonMsg(WebSocketCode.WEIGHT_GET_FAIL.ordinal(), "用户未登录","error_message")));
                    break;
                }
                try {
                    Integer userId = (Integer) session.getAttributes().get("userId");
                    List<Weight> weights = weightService.getUserWeights(userId);
                    session.sendMessage(new TextMessage(JsonUtils.toJsonMsg(WebSocketCode.WEIGHT_GET_SUCCESS.ordinal(), weights,"data")));
                } catch (Exception e) {
                    session.sendMessage(new TextMessage(JsonUtils.toJsonMsg(WebSocketCode.WEIGHT_GET_FAIL.ordinal(), e.getMessage(),"error_message")));
                }
                break;
            case "getLatestWeight":
                if (!session.getAttributes().containsKey("userId")) {
                    session.sendMessage(new TextMessage(JsonUtils.toJsonMsg(WebSocketCode.WEIGHT_LATEST_GET_FAIL.ordinal(), "用户未登录","error_message")));
                    break;
                }
                try {
                    Integer userId = (Integer) session.getAttributes().get("userId");
                    Weight weight = weightService.getLatestWeight(userId);
                    session.sendMessage(new TextMessage(JsonUtils.toJsonMsg(WebSocketCode.WEIGHT_LATEST_GET_SUCCESS.ordinal(), weight,"data")));
                } catch (Exception e) {
                    session.sendMessage(new TextMessage(JsonUtils.toJsonMsg(WebSocketCode.WEIGHT_LATEST_GET_FAIL.ordinal(), e.getMessage(),"error_message")));
                }
                break;
            default:
                session.sendMessage(new TextMessage("{\"error_code\":\"400\",\"error_message\":\"格式错误\"}"));
        }
    }
    private UserDTO parseUserDTO(String data) {
        try {
            UserDTO userDTO = objectMapper.readValue(data, UserDTO.class); // 使用 ObjectMapper 解析 JSON
            System.out.println("解析后的 JSON: " + objectMapper.writeValueAsString(userDTO)); // 输出解析后的 JSON
            return userDTO;
        } catch (Exception e) {
            // 处理解析异常
            e.printStackTrace(); // 输出异常信息
            return null; // 或者抛出自定义异常
        }
    }

    private String UserToJson(NormalUser user) {
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(user);
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    private String handleLoginError(String errorMessage) {
        // 从映射中获取错误响应
        return errorMapping.getOrDefault(errorMessage, "{\"error_code\":500,\"error_message\":\"登录失败\"}");
    }

}