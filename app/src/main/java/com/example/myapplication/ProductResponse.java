package com.example.myapplication;

import com.google.gson.annotations.SerializedName;

public class ProductResponse {

    // product라는 필드를 Gson에서 매핑할 수 있게 SerializedName을 사용
    @SerializedName("product")
    public Product product;

    public static class Product {

        @SerializedName("product_name")
        public String product_name;

        @SerializedName("brands")
        public String brands;

        @SerializedName("image_url")
        public String image_url;
    }
}
