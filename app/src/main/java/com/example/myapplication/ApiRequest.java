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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApiRequest {

    private Context context;

    public ApiRequest(Context context) {
        this.context = context;
    }

    public void addIngredient(String itemName, int quantity, String intakeDate, String expirationDate, int image) {
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
                params.put("image", String.valueOf(image));


                return params;
            }
        };

        // 요청 큐에 추가
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

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

    private List<Ingredient> parseIngredients(JSONArray jsonArray) {
        List<Ingredient> ingredients = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String name = jsonObject.getString("name");
                int quantity = jsonObject.getInt("quantity");
                String intakeDate = jsonObject.getString("intakeDate");
                String expirationDate = jsonObject.getString("expiration_date");
                int image = jsonObject.getInt("image");

                ingredients.add(new Ingredient(name, quantity, intakeDate, expirationDate, image));
            }
        } catch (Exception e) {
            Log.e("ApiRequest", "Error parsing ingredients", e);
        }
        return ingredients;
    }

    public interface IngredientFetchListener {
        void onFetchSuccess(List<Ingredient> ingredients);
        void onFetchError(VolleyError error);
    }
}
