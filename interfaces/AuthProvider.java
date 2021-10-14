package ru.kazachkov.florist.interfaces;

public interface AuthProvider {
    boolean isContainsSessionId();

    boolean isContainsPwd();

    boolean isNeedAuth();

    String getSessionId();

    String getPassword();

    void setPassword(String password);

    void setSessionId(String sessinoId);

    void logout();
}
