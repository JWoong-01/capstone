package com.example.myapplication;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApiRequest {

    private Context context;

    public ApiRequest(Context context) {
        this.context = context;
    }

    // 재료 추가 메서드
    public void addIngredient(String itemName, int quantity, String intakeDate, String expirationDate, String storageLocation, int image) {
        String url = "http://yju407.dothome.co.kr/add_ingredient.php"; // 서버 URL

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    // 성공적으로 응답을 받은 경우
                    Log.d("Response", response);
                    Toast.makeText(context, "재료가 추가되었습니다.", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    // 오류가 발생한 경우
                    Log.e("Error", error.toString());
                    Toast.makeText(context, "오류 발생: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("name", itemName);
                params.put("quantity", String.valueOf(quantity));
                params.put("intakeDate", intakeDate);
                params.put("expiration_date", expirationDate);
                params.put("storageLocation",storageLocation);
                params.put("image", String.valueOf(image));
                return params;
            }
        };

        // 요청 큐에 추가
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    // 재료 목록을 가져오는 메서드
    public void fetchIngredients(final IngredientFetchListener listener) {
        String url = "http://yju407.dothome.co.kr/get_ingredients.php"; // 재료를 가져오는 API URL

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    List<Ingredient> ingredients = parseIngredients(response);
                    listener.onFetchSuccess(ingredients);
                },
                error -> {
                    Log.e("ApiRequest", "Error fetching ingredients", error);
                    listener.onFetchError(error);
                });

        // 요청 큐에 추가
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonArrayRequest);
    }

    // 재료 목록을 파싱하는 메서드
    private List<Ingredient> parseIngredients(JSONArray jsonArray) {
        List<Ingredient> ingredients = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String name = jsonObject.getString("name");
                int quantity = jsonObject.getInt("quantity");
                String intakeDate = jsonObject.getString("intakeDate");
                String expirationDateString = jsonObject.getString("expiration_date");
                String storageLocation = jsonObject.optString("storageLocation", "냉동"); // 기본값 설정

                // expiration_date를 Calendar 객체로 변환(날짜 관리)
                Calendar expirationDate = Calendar.getInstance();
                String[] dateParts = expirationDateString.split("-");
                int year = Integer.parseInt(dateParts[0]);
                int month = Integer.parseInt(dateParts[1]) - 1; // 월은 0부터 시작
                int day = Integer.parseInt(dateParts[2]);
                expirationDate.set(year, month, day);

                int image = jsonObject.getInt("image");

                ingredients.add(new Ingredient(name, quantity, intakeDate, expirationDate, storageLocation, image)); // storageLocation 추가
            }
        } catch (Exception e) {
            Log.e("ApiRequest", "Error parsing ingredients", e);
        }
        return ingredients;
    }

    // 재료를 삭제하는 메서드 (이미지 리소스 ID로 삭제)
    public void deleteIngredientByImage(int imageResId, final ApiDeleteListener listener) {
        String url = "http://yju407.dothome.co.kr/delete_ingredient.php"; // 서버 URL

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    // 삭제 성공
                    listener.onDeleteSuccess();
                },
                error -> {
                    // 오류 발생
                    Log.e("Error", error.toString());
                    listener.onDeleteError();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("image", String.valueOf(imageResId)); // 삭제할 재료의 이미지 리소스 ID
                return params;
            }
        };

        // 요청 큐에 추가
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }


    // 재료를 수정하는 메서드
    public void updateIngredient(String name, int quantity, String intakeDate, String expirationDate, String storageLocation, int imageResId, final ApiUpdateListener listener) {
        String url = "http://yju407.dothome.co.kr/update_ingredient.php"; // 서버 URL

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    // 수정 성공
                    listener.onUpdateSuccess();
                },
                error -> {
                    // 오류 발생
                    Log.e("Error", error.toString());
                    listener.onUpdateError();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("name", name);
                params.put("quantity", String.valueOf(quantity));
                params.put("intakeDate", intakeDate);
                params.put("expiration_date", expirationDate);
                params.put("storageLocation", storageLocation); // storageLocation 추가
                params.put("image", String.valueOf(imageResId)); // 이미지 리소스 ID
                return params;
            }
        };

        // 요청 큐에 추가
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    // 삭제 성공/실패 리스너 인터페이스
    public interface ApiDeleteListener {
        void onDeleteSuccess();
        void onDeleteError();
    }

    // 수정 성공/실패 리스너 인터페이스
    public interface ApiUpdateListener {
        void onUpdateSuccess();
        void onUpdateError();
    }

    // 재료 목록을 가져오는 리스너 인터페이스
    public interface IngredientFetchListener {
        void onFetchSuccess(List<Ingredient> ingredients);
        void onFetchError(VolleyError error);
    }
}
