package ru.kazachkov.florist.app;


import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import lombok.Getter;
import lombok.Setter;
import ru.kazachkov.florist.api.model.UserAuthRole;
import ru.kazachkov.florist.interfaces.AuthProvider;
import ru.kazachkov.florist.tools.Const;

import static ru.kazachkov.florist.tools.Preconditions.checkNotNull;

public class AppPreferences implements AuthProvider {

    private SharedPreferences preferences;
    private static final String SESSION_ID = "session_id";
    private static final String PWD = "pwd";
    private static final String MODE_PURCHASE = "mode_purchase";
    private static final String CURRENT_SLAE_PNT_ID = "sale_pnt_id";
    private static final String CURRENT_SLAE_PNT_NAME = "sale_pnt_name";


    private String sessionId;

    private @Getter
    String password;
    private @Getter
    boolean modePurchase;
    private @Getter
    String salePntId;
    private @Getter
    String salePntName;


    private static AppPreferences INSTANCE;

    public static AppPreferences getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new AppPreferences(context);
        }
        return INSTANCE;
    }

    public AppPreferences(Context context) {
        preferences = context.getSharedPreferences(Const.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        sessionId = preferences.getString(SESSION_ID, "");
        password = preferences.getString(PWD, "");
        salePntId = preferences.getString(CURRENT_SLAE_PNT_ID, "");
        salePntName = preferences.getString(CURRENT_SLAE_PNT_NAME, "");

    }

    @Override
    public boolean isContainsSessionId() {
        return !TextUtils.isEmpty(sessionId);
    }

    @Override
    public boolean isContainsPwd() {
        return !TextUtils.isEmpty(password);
    }

    @Override
    public boolean isNeedAuth() {
        return !isContainsPwd() || !isContainsSessionId();
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
        preferences.edit().putString(PWD, password).apply();
    }

    public void setModePurchase(boolean modePurchase) {
        this.modePurchase = modePurchase;
        preferences.edit().putBoolean(MODE_PURCHASE, modePurchase).apply();
    }

    @Override
    public String getSessionId() {
        //Log.d("nev", "мне плохо..." + sessionId);
        return sessionId;
    }

    @Override
    public void setSessionId(String sessinoId) {
        this.sessionId = sessinoId;
        preferences.edit().putString(SESSION_ID, sessinoId).apply();

    }

    public void setSalePnt(UserAuthRole.SalePnt salePnt) {
        checkNotNull(salePntId);

        this.salePntId = salePnt.getStorage1CId();
        preferences.edit().putString(CURRENT_SLAE_PNT_ID, salePntId).apply();

        this.salePntName = salePnt.getStorageName();
        preferences.edit().putString(CURRENT_SLAE_PNT_ID, salePntName).apply();
    }


    @Override
    public void logout() {
        this.sessionId = "";
        this.password = "";
        preferences.edit().putString(SESSION_ID, "").apply();
        preferences.edit().putString(PWD, "").apply();
    }


}
