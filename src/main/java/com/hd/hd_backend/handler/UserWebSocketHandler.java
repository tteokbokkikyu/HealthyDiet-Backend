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

    private final ObjectMapper objectMapper = new ObjectMapper(); // 创建 ObjectMapper 实例

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        System.out.println("文本消息"); // 输出解析后的用户信息
        String payload = message.getPayload();
        // 解析消息并执行相应的操作
        // 假设消息格式为 "action:payload"
        String[] parts = payload.split(":", 2); // 限制分割为 2 部分
        String action = parts[0];



        switch (action) {
            case "register":{
                //userDTO = parseUserDTO(parts[1]);
                NormalUser normalUser =JsonUtils.fromJson(parts[1], NormalUser.class);
                if (normalUser != null) {
                    System.out.println("解析后的用户信息: " + normalUser ); // 输出解析后的用户信息
                    try {
                        NormalUser user = userService.register(normalUser );




                        session.sendMessage(new TextMessage(JsonUtils.toJsonMsg(WebSocketCode.REGISTER_SUCCESS.ordinal(),user,"user")) );

                        WebSocketSessionManager.addSession(user.getId(), session);
                        session.getAttributes().put("userId", user.getId());
                        System.out.println(user.getId()); // 输出
                    } catch (Exception e) {
                        e.printStackTrace();
                        session.sendMessage(new TextMessage(JsonUtils.toJsonMsg(WebSocketCode.REGISTER_FAIL.ordinal(),e.getMessage(),"error_message")));

                    }

                } else {
                    session.sendMessage(new TextMessage("解析用户信息失败"));
                }
                break;}
            case "login":
                User User =JsonUtils.fromJson(parts[1], User.class);
                if (User != null) {
                    System.out.println("解析后的用户信息: " + User); // 输出解析后的用户信息
                    try {

                        User user = userService.login( User);
                        session.sendMessage(new TextMessage(JsonUtils.toJsonMsg(WebSocketCode.LOGIN_SUCCESS.ordinal(),user,"data")) );
                        WebSocketSessionManager.addSession(user.getId(), session);
                        if(user.getIsAdmin()==0)
                            session.getAttributes().put("userId", user.getId());
                        else
                            session.getAttributes().put("adminId", user.getId());
                        System.out.println(user.getId()); // 输出
                    }catch (Exception e) {
                        e.printStackTrace();
                        // 根据异常消息返回相应的错误码和错误信息
                        session.sendMessage(new TextMessage(JsonUtils.toJsonMsg(WebSocketCode.LOGIN_FAIL.ordinal(),e.getMessage(),"error_message")));
                    }
                } else {
                    session.sendMessage(new TextMessage(JsonUtils.toJsonMsg(WebSocketCode.LOGIN_FAIL.ordinal(),"数据格式错误","error_message")));
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
                    updateInfo.setId(userId); // 设置userId确保更新正确的用户

                    userService.updateUser(userId, updateInfo);

                    // 获取更新后的用户信息
                    NormalUser updatedUser = userService.getUserById(userId);  // 使用service而不是直接访问mapper

                    session.sendMessage(new TextMessage("{\"code\":"+String.valueOf(WebSocketCode.UPDATE_USER_SUCCESS.ordinal())+",\"status\":200,\"message\":\"用户信息更新成功\",\"data\":" + JsonUtils.toJson(updatedUser) + "}"));
                } catch (Exception e) {
                    session.sendMessage(new TextMessage(JsonUtils.toJsonMsg(WebSocketCode.UPDATE_USER_FAIL.ordinal(), e.getMessage(),"error_message")) );
                    e.printStackTrace();
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
                session.sendMessage(new TextMessage("未知操作"));
        }
    }

}