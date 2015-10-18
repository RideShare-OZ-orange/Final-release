package com.swen900014.orange.rideshareoz.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;


/**
 * Created by Sangzhuoyang Yu on 9/11/15.
 * Encapsulate user data, user can be either a
 * driver or passenger depending on whether the
 * ride is offed by them
 */
public class User implements Serializable
{
    private String name;
    private String email;
    private String phone;
    private String credit;
    private String about;
    private String licence;

    private static User currentUser;

    public User()
    {
        name = "";
        email = "";
        phone = "";
        credit = "";
    }

    public User(String name, String email, String phone, String credit)
    {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.credit = credit;
    }

    public User(String username)
    {
        this.name = username;
    }

    public String getUsername()
    {
        return name;
    }

    public String getEmail()
    {
        return email;
    }

    public String getPhone()
    {
        return phone;
    }

    public String getCredit()
    {
        return credit;
    }

    public static void setCurrentUser(User currentUser)
    {
        User.currentUser = currentUser;
    }

    public static User getCurrentUser()
    {
        return currentUser;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public void setCredit(String credit)
    {
        this.credit = credit;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public String getAbout()
    {
        return about;
    }

    public String getLicence()
    {
        return licence;
    }

    public void setAbout(String about)
    {
        this.about = about;
    }

    public void setLicence(String licence)
    {
        this.licence = licence;
    }

    public void storeProfile(String profile)
    {
        try
        {
            JSONObject profileJson = new JSONObject(profile);
            about = profileJson.getString("note");
            phone = profileJson.getString("phone");
            licence = profileJson.getString("driver_license");
        } catch (JSONException e)
        {
            e.printStackTrace();
        }

    }
}
