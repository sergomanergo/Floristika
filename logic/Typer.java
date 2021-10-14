package ru.kazachkov.florist.logic;


import lombok.Getter;

public class Typer {
    public static final int SALE = 146;
    public static final int PREPAYMENT = 211;
    public static final int SHOWCASE = 413;
    public static final int DELETE = 124;
    public static final int COMEBACK = 954;


    @Getter
    private int currentType = SALE;

    private boolean savedOrder;

    public Typer(boolean savedOrder) {
        this.savedOrder = savedOrder;
        if (savedOrder) currentType = PREPAYMENT;
    }

    public int onNextType() {
        if (!savedOrder) {
            switch (currentType) {
                case SALE:
                    currentType = PREPAYMENT;
                    break;
                case PREPAYMENT:
                    currentType = SHOWCASE;
                    break;
                case SHOWCASE:
                    currentType = SALE;
                    break;
            }
        } else {
            switch (currentType) {
                case PREPAYMENT:
                    currentType = DELETE;
                    break;
                case DELETE:
                    currentType = COMEBACK;
                    break;
                case COMEBACK:
                    currentType = PREPAYMENT;
                    break;
            }
        }

        return currentType;
    }
}
