package com.javarush.telegram;

public class UserInfo {
    public String name;
    public String sex;
    public String age;
    public String city;
    public String occupation;
    public String hobby;
    public String handsome;
    public String wealth;
    public String annoys;
    public String goals;

    private String fieldToString(String str, String description) {
        if (str != null && !str.isEmpty())
            return description + ": " + str + "\n";
        else
            return "";
    }

    @Override
    public String toString() {
        String result = "";

        result += fieldToString(name, "Name");
        result += fieldToString(sex, "Sex");
        result += fieldToString(age, "Age");
        result += fieldToString(city, "City");
        result += fieldToString(occupation, "Occupation");
        result += fieldToString(hobby, "Hobby");
        result += fieldToString(handsome, "Handsome (max 10)");
        result += fieldToString(wealth, "Wealth");
        result += fieldToString(annoys, "Annoys");
        result += fieldToString(goals, "Goals");

        return result;
    }
}
