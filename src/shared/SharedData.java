package com.bchilakalapudi.rtrconstruction.shared;

import com.bchilakalapudi.rtrconstruction.controller.FirebaseDatabaseHandler;

public class SharedData {
    public static String loginuserId;
    public static String logincompanyId;
    public static String selectedCompanyId;
    public static String account;
    public static FirebaseDatabaseHandler dbhandler;

    public static String getLoginuserId() {
        return loginuserId;
    }

    public static void setLoginuserId(String loginuserId) {
        SharedData.loginuserId = loginuserId;
    }

    public static FirebaseDatabaseHandler getDbhandler() {
        return dbhandler;
    }

    public static void setDbhandler(FirebaseDatabaseHandler dbhandler) {
        SharedData.dbhandler = dbhandler;
    }

    public static String getLogincompanyId() {
        return logincompanyId;
    }

    public static void setLogincompanyId(String logincompanyId) {
        SharedData.logincompanyId = logincompanyId;
    }

    public static String getAccount() {
        return account;
    }

    public static void setAccount(String account) {
        SharedData.account = account;
    }
}
