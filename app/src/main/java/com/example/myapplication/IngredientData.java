package com.example.myapplication;




public class IngredientData {

    public static final String[] FRUITS = {
            "사과", "apple", "딸기", "strawberry", "오렌지", "orange", "복숭아", "peach",
            "포도", "grape", "배", "pear", "바나나", "banana", "체리", "cherry",
            "망고", "mango", "키위", "kiwi", "파인애플", "pineapple", "수박", "watermelon"
    };


    public static final String[] DAIRY = {
            "우유", "milk", "요거트", "yogurt", "치즈", "cheese", "두유", "soy milk",
            "마가린", "margarine", "버터", "butter", "아몬드 우유", "almond milk", "그릭 요거트", "greek yogurt"
    };


    public static final String[] MEAT = {
            "소고기", "beef", "돼지고기", "pork", "닭고기", "chicken", "양고기", "lamb",
            "햄", "ham", "소시지", "sausage", "오리고기", "duck", "양념고기", "marinated meat",
            "닭다리", "drumstick", "닭가슴살", "chicken breast", "베이컨", "bacon"
    };

    public static final String[] ETC = {
            "계란", "egg", "어묵", "fish cake", "떡볶이 떡", "rice cake", "떡국떡", "sliced rice cake",
            "만두", "dumpling", "돈까스", "pork cutlet", "식빵", "bread", "스파게티 면", "spaghetti",
            "소면", "thin noodles", "김치", "kimchi"
    };


    public static final String[] CONDIMENTS = {
            "케첩", "ketchup", "마요네즈", "mayonnaise", "머스타드", "mustard", "바베큐 소스", "bbq sauce",
            "핫소스", "hot sauce", "간장", "soy sauce", "참기름", "sesame oil", "소금", "salt",
            "설탕", "sugar", "고추장", "red pepper paste", "굴소스", "  oyster sauce", "고춧가루", "chili powder",
            "된장", "soybean paste", "쌈장", "ssamjang", "카레 가루", "curry powder", "샐러드 드레싱", "salad dressing",
            "피시 소스", "fish sauce", "치킨 스톡", "chicken stock", "토마토 소스", "tomato sauce"
    };


    public static final String[] SEAFOOD = {
            "고등어", "mackerel", "오징어", "squid", "새우", "shrimp", "게", "crab",
            "문어", "octopus", "낙지", "webfoot octopus", "연어", "salmon", "조개", "clam",
            "방어", "amberjack", "성게", "sea urchin", "굴", "oyster", "홍합", "mussel",
            "전복", "abalone", "미역", "seaweed", "다시마", "kelp", "동태", "pollock",
            "멸치", "anchovy", "갈치", "cutlassfish", "꽁치", "saury"
    };

    public static final String[] VEGETABLES = {
            "당근", "carrot", "피망", "paprika", "가지", "eggplant", "양파", "onion",
            "감자", "potato", "고구마", "sweet potato", "브로콜리", "broccoli", "대파", "green onion",
            "마늘", "garlic", "애호박", "zucchini", "양배추", "cabbage",
            "청경채", "bok choy", "무", "radish", "오이", "cucumber", "상추", "lettuce", "버섯", "mushroom"
    };

    public static String getMatchedKoreanName(String productName) {
        productName = productName.toLowerCase();

        // 각 카테고리 배열 다 검사
        String match = matchName(productName, DAIRY);
        if (match != null) return match;
        match = matchName(productName, MEAT);
        if (match != null) return match;
        match = matchName(productName, FRUITS);
        if (match != null) return match;
        match = matchName(productName, VEGETABLES);
        if (match != null) return match;
        match = matchName(productName, SEAFOOD);
        if (match != null) return match;
        match = matchName(productName, CONDIMENTS);
        if (match != null) return match;
        match = matchName(productName, ETC);
        if (match != null) return match;

        return productName; // 못 찾으면 원문 그대로
    }

    // 내부 도우미 함수
    private static String matchName(String name, String[] pairs) {
        for (int i = 0; i < pairs.length - 1; i += 2) {
            String korean = pairs[i];
            String english = pairs[i + 1];
            if (name.contains(korean.toLowerCase()) || name.contains(english.toLowerCase())) {
                return korean; // 한글 이름만 반환
            }
        }
        return null;
    }

    public static int getImageResource(String name) {
        switch (name) {
            //  유제품
            case "우유": return R.drawable.it_milk;
            case "요거트": return R.drawable.it_yogurt;
            case "치즈": return R.drawable.it_cheese;
            case "두유": return R.drawable.it_soymilk;

            //  기타
            case "계란": return R.drawable.it_egg;
            case "어묵": return R.drawable.it_fishcake;
            case "만두": return R.drawable.it_dumpling;
            case "식빵": return R.drawable.it_bread;

            //  과일
            case "사과": return R.drawable.it_apple;
            case "딸기": return R.drawable.it_strawberry;
            case "오렌지": return R.drawable.it_orange;
            case "포도": return R.drawable.it_grape;
            case "바나나": return R.drawable.it_banana;
            case "수박": return R.drawable.it_watermelon;
            case "체리": return R.drawable.it_cherry;
            case "망고": return R.drawable.it_mango;
            case "키위": return R.drawable.it_kiwi;
            case "복숭아": return R.drawable.it_peach;
            case "파인애플": return R.drawable.it_pineapple;
            case "배": return R.drawable.it_pear;

            //  고기류
            case "소고기": return R.drawable.it_beef;
            case "돼지고기": return R.drawable.it_pork;
            case "닭고기": return R.drawable.it_chicken;
            case "양고기": return R.drawable.it_lamb;
            case "햄": return R.drawable.it_ham;
            case "소시지": return R.drawable.it_sausage;
            case "오리고기": return R.drawable.it_duck;
            case "양념고기": return R.drawable.it_marinatedmeat;
            case "닭다리": return R.drawable.it_drumsticks;
            case "닭가슴살": return R.drawable.it_breast;
            case "베이컨": return R.drawable.it_bacon;
            //  채소류
            case "당근": return R.drawable.it_carrot;
            case "피망": return R.drawable.it_paprika;
            case "가지": return R.drawable.it_eggplant;
            case "양파": return R.drawable.it_onion;
            case "감자": return R.drawable.it_potato;
            case "고구마": return R.drawable.it_sweetpotato;
            case "브로콜리": return R.drawable.it_broccoli;
            case "대파": return R.drawable.it_greenonion;
            case "마늘": return R.drawable.it_garlic;
            case "애호박": return R.drawable.it_zucchini;
            case "양배추": return R.drawable.it_cabbage;
            case "청경채": return R.drawable.it_bokchoy;
            case "무": return R.drawable.it_radish;
            case "오이": return R.drawable.it_cucumber;
            case "상추": return R.drawable.it_lettuce;
            case "버섯": return R.drawable.it_mushroom;


            //  해산물
            case "고등어": return R.drawable.it_mackerel;
            case "오징어": return R.drawable.it_squid;
            case "새우": return R.drawable.it_shrimp;
            case "게": return R.drawable.it_crab;
            case "문어": return R.drawable.it_octopus;
            case "낙지": return R.drawable.it_squid;
            case "연어": return R.drawable.it_salmon;
            case "조개": return R.drawable.it_clam;
            case "방어": return R.drawable.it_mackerel;
            case "성게": return R.drawable.it_urchin;
            case "굴": return R.drawable.it_clam;
            case "홍합": return R.drawable.it_clam;
            case "전복": return R.drawable.it_clam;
            case "미역": return R.drawable.it_seaweed;
            case "다시마": return R.drawable.it_seaweed;
            case "동태": return R.drawable.it_mackerel;
            case "멸치": return R.drawable.it_mackerel;
            case "갈치": return R.drawable.it_mackerel;
            case "꽁치": return R.drawable.it_mackerel;

            //  조미료
            case "케첩": return R.drawable.it_ketchup;
            case "마요네즈": return R.drawable.it_mayonnaise;
            case "머스타드": return R.drawable.it_mustard;
            case "바베큐 소스": return R.drawable.it_bbq;
            case "핫소스": return R.drawable.it_hot;
            case "간장": return R.drawable.it_soysauce;
            case "참기름": return R.drawable.it_sesameoil;
            case "소금": return R.drawable.it_salt;
            case "설탕": return R.drawable.it_sugar;
            case "고추장": return R.drawable.it_gochujang;
            case "굴소스": return R.drawable.it_oyster;
            case "고춧가루": return R.drawable.it_redpepperpowder;
            case "된장": return R.drawable.it_paste;
            case "쌈장": return R.drawable.it_paste;
            case "카레 가루": return R.drawable.it_currypowder;
            case "샐러드 드레싱": return R.drawable.it_dressing;
            case "피시 소스": return R.drawable.it_fishsauce;
            case "치킨 스톡": return R.drawable.it_chicken_seasoning;
            case "토마토 소스": return R.drawable.it_tomatopaste;
            default: return R.drawable.ic_pencil; // 기본 이미지
        }
    }

    private static boolean containsAny(String name, String[] keywords) {
        for (String keyword : keywords) {
            if (name.contains(keyword.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}
