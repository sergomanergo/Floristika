package ru.kazachkov.florist.data;


import android.provider.BaseColumns;

public final class Contract {
    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "florist.db";
    private static final String TYPE_TEXT = " TEXT";
    private static final String TYPE_INTEGER = " INTEGER";
    private static final String TYPE_REAL = " REAL";

    private static final String COMMA_SEP = ",";
    private static final String DOT_SEP = ".";
    private static final String PRIMARY_KEY = "PRIMARY KEY";
    private static final String SPACE = "  ";
    private static final String SELECT_ALL_FROM = "SELECT * FROM ";


    public static abstract class Storage implements BaseColumns {
        public static final String TABLE_NAME = "storagies";
        public static final String COLUMN_ID = "storagies_id";
        public static final String COLUMN_NAME = "storagies_name";

        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                COLUMN_ID + " TEXT PRIMARY KEY," +
                COLUMN_NAME + TYPE_TEXT
                + " )";
    }

    public static abstract class Stock implements BaseColumns {
        public static final String TABLE_NAME = "stock";
        public static final String COLUMN_STORAGE_ID = "stock_storage_id";
        public static final String COLUMN_FLOWER_ID = "stock_flower_id";
        public static final String COLUMN_ITEMS = "stock_items";
        public static final String COLUMN_IN_STOCK = "stock_in_stock";


        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                COLUMN_STORAGE_ID + TYPE_TEXT + COMMA_SEP +
                COLUMN_FLOWER_ID + TYPE_TEXT + COMMA_SEP +
                COLUMN_ITEMS + TYPE_REAL + COMMA_SEP +
                COLUMN_IN_STOCK + TYPE_INTEGER +
                PRIMARY_KEY + " (" + COLUMN_STORAGE_ID + COMMA_SEP + COLUMN_FLOWER_ID
                + " )"
                + " )";
    }

    public static abstract class SalePnts implements BaseColumns {
        public static final String TABLE_NAME = "sale_pnts";
        public static final String COLUMN_ID = "sale_pnts_storage_id";
        public static final String COLUMN_NAME = "sale_pnts_storage_name";
        public static final String COLUMN_IS_MANAGER = "sale_pnts_is_manager";

        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                COLUMN_ID + " TEXT PRIMARY KEY," +
                COLUMN_NAME + TYPE_TEXT + COMMA_SEP +
                COLUMN_IS_MANAGER + TYPE_TEXT
                + " )";
    }

    public static abstract class Photos implements BaseColumns {
        public static final String TABLE_NAME = "photos";
        public static final String COLUMN_ITEM_ID = "photos_item_id";
        public static final String COLUMN_PATH = "phptps_path";


        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                COLUMN_PATH + TYPE_TEXT + COMMA_SEP +
                COLUMN_ITEM_ID + TYPE_TEXT +
                PRIMARY_KEY + " (" + COLUMN_PATH + COMMA_SEP + COLUMN_ITEM_ID
                + " )"
                + " )";
    }

    public static abstract class ItemPrices implements BaseColumns {
        public static final String TABLE_NAME = "item_prices";
        public static final String COLUMN_STORAGE_ID = "item_prices_storage_id";
        public static final String COLUMN_FLOWER_ID = "item_prices_flower_id";
        public static final String COLUMN_REGULAR_PRICE = "item_prices_regular_price";
        public static final String COLUMN_MIN_PRICE = "item_prices_min_price";

        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                COLUMN_STORAGE_ID + TYPE_TEXT + COMMA_SEP +
                COLUMN_FLOWER_ID + TYPE_TEXT + COMMA_SEP +
                COLUMN_REGULAR_PRICE + TYPE_REAL + COMMA_SEP +
                COLUMN_MIN_PRICE + TYPE_REAL +

                PRIMARY_KEY + " (" + COLUMN_STORAGE_ID + COMMA_SEP + COLUMN_FLOWER_ID
                + " )"
                + " )";
    }

    public static abstract class ItemCategories implements BaseColumns {
        public static final String TABLE_NAME = "item_cat";
        public static final String COLUMN_CAT_ID = "item_cat_cat_id";
        public static final String COLUMN_ITEM_ID = "item_cat_item_id";


        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                COLUMN_CAT_ID + TYPE_TEXT + COMMA_SEP +
                COLUMN_ITEM_ID + TYPE_TEXT +
                PRIMARY_KEY + " (" + COLUMN_CAT_ID + COMMA_SEP + COLUMN_ITEM_ID
                + " )"
                + " )";
    }


    public static abstract class Item implements BaseColumns {
        public static final String TABLE_NAME = "items";
        public static final String COLUMN_ID = "items_id";
        public static final String COLUMN_NAME = "items_name";
        public static final String COLUMN_ARTICLE = "items_article";
        public static final String COLUMN_UNITS_ID = "items_units_id";
        public static final String COLUMN_IS_LOCKED = "items_is_locked";
        public static final String COLUMN_IS_ZERO_STOCK = "items_is_zero_stock";

        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                COLUMN_ID + " TEXT PRIMARY KEY," +
                COLUMN_ARTICLE + TYPE_TEXT + COMMA_SEP +
                COLUMN_NAME + TYPE_TEXT + COMMA_SEP +
                COLUMN_UNITS_ID + TYPE_TEXT + COMMA_SEP +
                COLUMN_IS_LOCKED + TYPE_INTEGER + COMMA_SEP +
                COLUMN_IS_ZERO_STOCK + TYPE_INTEGER
                + " )";
    }


    public static abstract class Categories implements BaseColumns {
        public static final String TABLE_NAME = "categories";
        public static final String COLUMN_ID = "categories_id";
        public static final String COLUMN_NAME = "cat_name";

        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                COLUMN_ID + " TEXT PRIMARY KEY," +
                COLUMN_NAME + TYPE_TEXT
                + " )";
    }


    public static abstract class Units implements BaseColumns {
        public static final String TABLE_NAME = "units";
        public static final String COLUMN_ID = "units_id";
        public static final String COLUMN_NAME = "units_name";
        public static final String COLUMN_MIN_VALUE = "units_min_value";

        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                COLUMN_ID + " TEXT PRIMARY KEY," +
                COLUMN_MIN_VALUE + TYPE_REAL + COMMA_SEP +
                COLUMN_NAME + TYPE_TEXT
                + " )";
    }

}
