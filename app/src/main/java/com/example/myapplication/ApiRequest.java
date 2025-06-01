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
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.StringReader;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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

    // 장바구니 항목 추가
    public void addShoppingItem(String name, int quantity, String unit, final ApiCallback callback) {
        String url = "http://yju407.dothome.co.kr/add_shopping_cart.php"; // 장바구니 추가 PHP 경로

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d("ApiRequest", "장바구니 추가 응답: " + response);
                    Toast.makeText(context, "장바구니에 추가되었습니다.", Toast.LENGTH_SHORT).show();
                    if (callback != null) callback.onSuccess(response);
                },
                error -> {
                    Log.e("ApiRequest", "장바구니 추가 오류: " + error.toString());
                    Toast.makeText(context, "장바구니 추가 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                    if (callback != null) callback.onError(error);
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("name", name);
                params.put("quantity", String.valueOf(quantity));
                params.put("unit", unit);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    // 장바구니 항목 목록 가져오기
    public void fetchShoppingItems(final ShoppingFetchListener listener) {
        String url = "http://yju407.dothome.co.kr/get_shopping_cart.php"; // 장바구니 목록 PHP 경로

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    List<ShoppingItem> shoppingItems = parseShoppingItems(response);
                    listener.onFetchSuccess(shoppingItems);
                },
                error -> {
                    Log.e("ApiRequest", "장바구니 불러오기 오류", error);
                    listener.onFetchError(error);
                });

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonArrayRequest);
    }

    // 장바구니 항목 삭제
    public void deleteShoppingItem(int id, final ApiCallback callback) {
        String url = "http://yju407.dothome.co.kr/delete_shopping_cart.php"; // 장바구니 삭제 PHP 경로

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d("ApiRequest", "장바구니 삭제 응답: " + response);
                    Toast.makeText(context, "장바구니에서 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                    if (callback != null) callback.onSuccess(response);
                },
                error -> {
                    Log.e("ApiRequest", "장바구니 삭제 오류: " + error.toString());
                    Toast.makeText(context, "장바구니 삭제 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                    if (callback != null) callback.onError(error);
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(id)); // 삭제할 항목 id
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }


    // 장바구니 JSON 파싱 메서드
    private List<ShoppingItem> parseShoppingItems(JSONArray jsonArray) {
        List<ShoppingItem> shoppingItems = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                int id = jsonObject.getInt("id");
                String name = jsonObject.getString("name");
                int quantity = jsonObject.getInt("quantity");
                String unit = jsonObject.getString("unit");

                shoppingItems.add(new ShoppingItem(id, name, quantity, unit));
            }
        } catch (JSONException e) {
            Log.e("ApiRequest", "장바구니 파싱 오류", e);
        }
        return shoppingItems;
    }


    // 콜백 인터페이스 - 성공/실패 처리용
    public interface ApiCallback {
        void onSuccess(String response);
        void onError(VolleyError error);
    }

    // 장바구니 항목 불러오기 콜백
    public interface ShoppingFetchListener {
        void onFetchSuccess(List<ShoppingItem> shoppingItems);
        void onFetchError(VolleyError error);
    }

    // 재료 추가 메서드
    public void addIngredient(String itemName, int quantity, String unit, String intakeDate, String expirationDate, String storageLocation, int image) {
        String url = "http://yju407.dothome.co.kr/add_ingredient.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d("Response", response);
                    Toast.makeText(context, "재료가 추가되었습니다.", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    Log.e("Error", error.toString());
                    Toast.makeText(context, "오류 발생: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("name", itemName);
                params.put("quantity", String.valueOf(quantity));
                params.put("unit", unit); // ✅ 추가된 부분
                params.put("intakeDate", intakeDate);
                params.put("expiration_date", expirationDate);
                params.put("storageLocation", storageLocation);
                params.put("image", String.valueOf(image));
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }



    public void fetchIngredients(final IngredientFetchListener listener) {
        String url = "http://yju407.dothome.co.kr/get_ingredients.php"; // 재료를 가져오는 API URL

        // 서버로 보낼 파라미터가 필요한 경우, 예를 들어 재료 필터링이나 검색 등을 위한 인코딩을 추가할 수 있음
        // 예시로 ingredientsParam에 URL 인코딩된 문자열을 추가할 수 있음.
        StringBuilder ingredientsParam = new StringBuilder();
        try {
            // 예시로 'ingredient' 값을 URL 인코딩
            ingredientsParam.append(URLEncoder.encode("ingredient", "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            Log.e("ApiRequest", "Error encoding ingredients parameter", e);
        }

        // URL에 파라미터를 추가하는 예시 (만약 서버에서 파라미터를 요구한다면)
        String finalUrl = url + "?" + ingredientsParam.toString();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, finalUrl, null,
                response -> {
                    List<Ingredient> ingredients = parseIngredients(response);
                    listener.onFetchSuccess(ingredients);
                },
                error -> {
                    Log.e("ApiRequest", "Error fetching ingredients", error);
                    listener.onFetchError(error);
                }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=UTF-8";  // 응답 인코딩을 UTF-8로 설정
            }
        };

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
                String unit = jsonObject.getString(("unit"));
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

                ingredients.add(new Ingredient(name, quantity, unit, intakeDate, expirationDate, storageLocation, image)); // storageLocation 추가
            }
        } catch (Exception e) {
            Log.e("ApiRequest", "Error parsing ingredients", e);
        }
        return ingredients;
    }

    // 재료 목록을 기반으로 레시피를 가져오는 메서드
    public void fetchRecipesByIngredients(List<String> ingredients, final RecipeFetchListener listener) {
        String url = "http://yju407.dothome.co.kr/get_recipes.php";

        // 서버로 보낼 파라미터 형성 (재료 목록)
        StringBuilder ingredientsParam = new StringBuilder();
        for (String ingredient : ingredients) {
            try {
                // 각 재료를 URL 인코딩하여 파라미터에 추가
                ingredientsParam.append(URLEncoder.encode(ingredient, "UTF-8")).append(",");
            } catch (UnsupportedEncodingException e) {
                Log.e("ApiRequest", "Error encoding ingredient: " + ingredient, e);
            }
        }
        // 마지막 쉼표 제거
        if (ingredientsParam.length() > 0) {
            ingredientsParam.deleteCharAt(ingredientsParam.length() - 1);
        }

        // 서버 요청 (재료 목록을 쿼리 파라미터로 전달)
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url + "?ingredients=" + ingredientsParam.toString(),
                response -> {
                    // 서버로부터 받은 JSON 응답을 파싱
                    List<Recipe> recipes = parseRecipes(response);
                    listener.onFetchSuccess(recipes);
                },
                error -> {
                    Log.e("ApiRequest", "Error fetching recipes", error);
                    listener.onFetchError(error);
                }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=UTF-8";  // 응답 인코딩 설정
            }
        };

        // 요청 큐에 추가
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    // 레시피 목록을 파싱하는 메서드
    private List<Recipe> parseRecipes(String response) {
        List<Recipe> recipeList = new ArrayList<>();
        try {
            // JSON 응답을 JSONObject로 변환
            JSONObject jsonResponse = new JSONObject(response);

            // "recipes" 배열 추출
            if (jsonResponse.getBoolean("success")) {
                JSONArray recipesArray = jsonResponse.getJSONArray("recipes");

                // 배열에서 각 레시피 객체를 추출하여 Recipe 객체로 변환
                for (int i = 0; i < recipesArray.length(); i++) {
                    JSONObject recipeObject = recipesArray.getJSONObject(i);
                    String name = recipeObject.getString("name");
                    String imageUrl = recipeObject.getString("image_url");
                    String ingredients = recipeObject.getString("ingredients");
                    String instructions = recipeObject.getString("instructions");

                    // Recipe 객체를 리스트에 추가
                    recipeList.add(new Recipe(name, imageUrl, ingredients, instructions));
                }
            }
        } catch (JSONException e) {
            Log.e("ApiRequest", "Error parsing recipes", e);
        }
        return recipeList;
    }
    //  식약처 기반 레시피 불러오기
    public void fetchRecipesFromXMLAPI(final RecipeFetchListener listener) {
        String url = "http://openapi.foodsafetykorea.go.kr/api/cf37f4688166446c8e5e/COOKRCP01/xml/1/100";

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        List<Recipe> recipes = parseXML(response);
                        listener.onFetchSuccess(recipes);
                    } catch (Exception e) {
                        Log.e("ApiRequest", "XML 파싱 실패", e);
                        listener.onFetchError(null);
                    }
                },
                error -> {
                    Log.e("ApiRequest", "공공 API(XML) 요청 실패", error);
                    listener.onFetchError(error);
                });

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
    }
    private List<Recipe> parseXML(String xml) throws Exception {
        List<Recipe> recipes = new ArrayList<>();
        Recipe currentRecipe = null;

        XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
        parser.setInput(new StringReader(xml));
        int eventType = parser.getEventType();

        while (eventType != XmlPullParser.END_DOCUMENT) {
            String tag = parser.getName();

            switch (eventType) {
                case XmlPullParser.START_TAG:
                    if ("row".equals(tag)) {
                        currentRecipe = new Recipe();
                    } else if (currentRecipe != null) {
                        if ("RCP_NM".equals(tag)) {
                            currentRecipe.setName(parser.nextText());
                        } else if ("ATT_FILE_NO_MAIN".equals(tag)) {
                            currentRecipe.setImageUrl(parser.nextText());
                        } else if ("RCP_PARTS_DTLS".equals(tag)) {
                            currentRecipe.setIngredients(parser.nextText());
                        } else if (tag.startsWith("MANUAL")) {
                            String step = parser.nextText();
                            if (!step.trim().isEmpty()) {
                                String inst = currentRecipe.getInstructions() != null ? currentRecipe.getInstructions() : "";
                                currentRecipe.setInstructions(inst + step + "\n");
                            }
                        }
                    }
                    break;

                case XmlPullParser.END_TAG:
                    if ("row".equals(tag) && currentRecipe != null) {
                        recipes.add(currentRecipe);
                        currentRecipe = null;
                    }
                    break;
            }

            eventType = parser.next();
        }

        return recipes;
    }


    // 재료를 삭제하는 메서드 (이미지 리소스 ID로 삭제)
    public void deleteIngredientByImage(String name, final ApiDeleteListener listener) {
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
                params.put("name", String.valueOf(name)); // 삭제할 재료의 ID
                return params;
            }
        };

        // 요청 큐에 추가
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }


    public void updateIngredient(String name, int quantity, String unit, String intakeDate, String expirationDate, String storageLocation, int imageResId, final ApiUpdateListener listener) {
        String url = "http://yju407.dothome.co.kr/update_ingredient.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    listener.onUpdateSuccess();
                },
                error -> {
                    Log.e("Error", error.toString());
                    listener.onUpdateError();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("name", name);
                params.put("quantity", String.valueOf(quantity));
                params.put("unit", unit); // ✅ unit 추가!
                params.put("intakeDate", intakeDate);
                params.put("expiration_date", expirationDate);
                params.put("storageLocation", storageLocation);
                params.put("image", String.valueOf(imageResId));
                return params;
            }
        };

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


    // 레시피 가져오기 성공/실패 리스너 인터페이스
    public interface RecipeFetchListener {
        void onFetchSuccess(List<Recipe> recipes);
        void onFetchError(VolleyError error);
    }

    // 재료 목록을 가져오는 리스너 인터페이스
    public interface IngredientFetchListener {
        void onFetchSuccess(List<Ingredient> ingredients);
        void onFetchError(VolleyError error);
    }
}