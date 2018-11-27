package com.example.ang.dineanddate.box;

public class box {
    // Needed to connect to DB
    private String userId;
    private String name;
    private String profileImageUrl;
    private String foodChoice1;
    private String foodChoice2;
    private String foodChoice3;

    // Function to populate the box or "card"
    public box (String userId, String name, String profileImageUrl) {
        this.userId = userId;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
    }

    // Functions to get the values and change the values
    public String getUserId(){
        return userId;
    }
    // Changes userID
    public void setUserId(String userId) {
        this.userId = userId;
    }
    // Functions to get the values and change the values
    public String getName(){
        return name;
    }
    // Changes name
    public void setName(String name) {
        this.name = name;
    }

    public String getProfileImageUrl(){
        return profileImageUrl;
    }
    // Changes name
    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    //Food
    public String getFoodChoice1(){
        return foodChoice1;
    }
    // Changes name
    public void setFoodChoice1(String foodChoice1) {
        this.foodChoice1 = foodChoice1;
    }

    public String getFoodChoice2(){
        return foodChoice2;
    }
    // Changes name
    public void setFoodChoice2(String foodChoice2) {
        this.foodChoice2 = foodChoice2;
    }

    public String getFoodChoice3(){
        return foodChoice3;
    }
    // Changes name
    public void setFoodChoice3(String foodChoice3) {
        this.foodChoice3 = foodChoice3;
    }
}
