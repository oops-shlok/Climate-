package com.example.hackathondelta;

import com.google.gson.annotations.SerializedName;

public class DataClass {
    @SerializedName("main")
    Main main;

    public Main getMain() {
        return main;
    }

    public void setMain(Main main) {
        this.main = main;
    }

}
