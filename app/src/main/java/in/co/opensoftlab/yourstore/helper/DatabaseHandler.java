package in.co.opensoftlab.yourstore.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dewangankisslove on 07-03-2016.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    private Context context;

    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "autoroom.db";

    // Contacts table name
    private static final String TABLE_CARS = "cars";
    private static final String TABLE_BIKES = "bikes";
    private static final String TABLE_SPECIFICATION = "specification";
    private static final String TABLE_FEATURE = "feature";
    private static final String TABLE_MSG = "msg";
    private static final String TABLE_OFFERING = "offering";
    private static final String TABLE_ORDER_HISTORY = "order_history";

    // CARS Table Columns names
    private static final String PRODUCT_ID = "_id";
    private static final String PRODUCT_NAME = "name";
    private static final String PRODUCT_URL = "url";
    private static final String BODY_TYPE = "body_type";
    private static final String ENGINE = "engine";
    private static final String BRAND = "brand";
    private static final String MODEL_NAME = "model_name";
    private static final String MODEL_YEAR = "model_year";
    private static final String PRICE = "price";
    private static final String FUEL_TYPE = "fuel_type";
    private static final String SEATING = "seating";
    private static final String DISTANCE_DRIVEN = "driven_distance";
    private static final String COLOR = "color";
    private static final String TRANSMISSION = "transmission";
    private static final String SELLER_TYPE = "seller_type";
    private static final String MILEAGE = "mileage";
    private static final String TANK_CAPACITY = "tank_capacity";
    private static final String OWNER_NO = "owner_no";
    private static final String CAR_CONDITION = "car_condition";
    private static final String SELLER_NAME = "seller_name";
    private static final String SELLER_ID = "seller_id";
    private static final String SELLER_URL = "seller_url";
    private static final String PROFILE_ID = "profile_id";
    private static final String GEO_LOCATION = "geo_location";
    private static final String ADDRESS = "address";
    private static final String UPLOAD_DATE = "upload_date";
    private static final String ASSURED_PRODUCT = "assured";

    // CARS Table Columns names
    private static final String BIKE_ID = "_id";
    private static final String BIKE_NAME = "bike_name";
    private static final String BIKE_URL = "bike_url";
    private static final String BIKE_ENGINE = "bike_engine";
    private static final String BIKE_BRAND = "bike_brand";
    private static final String BIKE_MODEL = "bike_model";
    private static final String BIKE_MODEL_YEAR = "bike_year";
    private static final String BIKE_PRICE = "bike_price";
    private static final String BIKE_DISTANCE_DRIVEN = "driven_distance";
    private static final String BIKE_WEIGHT = "bike_weight";
    private static final String BIKE_COLOR = "bike_color";
    private static final String ELECTRIC_START = "electric_start";
    private static final String BIKE_MILEAGE = "bike_mileage";
    private static final String BIKE_TANK_CAPACITY = "tank_capacity";
    private static final String TOP_SPEED = "top_speed";
    //    private static final String SELLER_TYPE = "seller_type";
//    private static final String OWNER_NO = "owner_no";
    private static final String BIKE_CONDITION = "bike_condition";
//    private static final String SELLER_NAME = "seller_name";
//    private static final String SELLER_ID = "seller_id";
//    private static final String SELLER_URL = "seller_url";
//    private static final String PROFILE_ID = "profile_id";
//    private static final String GEO_LOCATION = "geo_location";
//    private static final String ADDRESS = "address";
//    private static final String UPLOAD_DATE = "upload_date";
//    private static final String ASSURED_PRODUCT = "assured";

    // SPECIFICATIONS Table Columns names
    private static final String ATTR_ID = "id";
    private static final String ATTR_NAME = "name";
    private static final String ATTR_VALUE = "value";

    //unReadMsg Table Columns names
    private static final String CHAT_ID = "id";
    private static final String NO_UNREAD_MSG = "no_msg";

    //offering Table Columns names
    private static final String O_TYPE = "id";
    private static final String O_VALUE = "no_msg";

    //orderHistory Table Columns names
    private static final String ORDER_ID = "order_id";
    private static final String ORDER_TIME = "time";

    private static final String CREATE_TABLE_PRODUCTS = "CREATE TABLE " + TABLE_CARS + "(" + PRODUCT_ID + " TEXT PRIMARY KEY," +
            PRODUCT_NAME + " TEXT," + PRODUCT_URL + " TEXT," + BODY_TYPE + " TEXT," + ENGINE + " TEXT," + BRAND + " TEXT," +
            MODEL_NAME + " TEXT," + MODEL_YEAR + " INT, " + PRICE + " INT, " + FUEL_TYPE + " TEXT, " + SEATING + " INT," +
            DISTANCE_DRIVEN + " INT," + COLOR + " TEXT," + TRANSMISSION + " TEXT," + SELLER_TYPE + " TEXT," + MILEAGE + " TEXT," +
            TANK_CAPACITY + " INT," + OWNER_NO + " TEXT," + CAR_CONDITION + " TEXT," + SELLER_NAME + " TEXT," + SELLER_ID + " TEXT," +
            SELLER_URL + " TEXT," + PROFILE_ID + " TEXT," + GEO_LOCATION + " TEXT," + ADDRESS + " TEXT," + UPLOAD_DATE + " TEXT," +
            ASSURED_PRODUCT + " INT" + ")";
    private static final String CREATE_TABLE_BIKES = "CREATE TABLE " + TABLE_BIKES + "(" + BIKE_ID + " TEXT PRIMARY KEY," +
            BIKE_NAME + " TEXT," + BIKE_URL + " TEXT," + BIKE_ENGINE + " TEXT," + BIKE_BRAND + " TEXT," + BIKE_MODEL + " TEXT," +
            BIKE_MODEL_YEAR + " INT, " + BIKE_PRICE + " INT, " + BIKE_DISTANCE_DRIVEN + " TEXT, " + BIKE_WEIGHT + " INT, " +
            BIKE_COLOR + " TEXT," + ELECTRIC_START + " TEXT," + BIKE_MILEAGE + " TEXT," + BIKE_TANK_CAPACITY + " INT," +
            TOP_SPEED + " INT," + SELLER_TYPE + " TEXT," + OWNER_NO + " TEXT," + BIKE_CONDITION + " TEXT," + SELLER_NAME + " TEXT," +
            SELLER_ID + " TEXT," + SELLER_URL + " TEXT," + PROFILE_ID + " TEXT," + GEO_LOCATION + " TEXT," + ADDRESS + " TEXT," +
            UPLOAD_DATE + " TEXT," + ASSURED_PRODUCT + " INT" + ")";
    private static final String CREATE_TABLE_SPECIFICATION = "CREATE TABLE " + TABLE_SPECIFICATION + "(" + ATTR_ID + " TEXT," + ATTR_NAME + " TEXT," + ATTR_VALUE + " TEXT" + ")";
    private static final String CREATE_TABLE_FEATURE = "CREATE TABLE " + TABLE_FEATURE + "(" + ATTR_ID + " TEXT," +
            ATTR_NAME + " TEXT," + ATTR_VALUE + " TEXT" + ")";
    private static final String CREATE_TABLE_UNREAD_MSG = "CREATE TABLE " + TABLE_MSG + "(" + CHAT_ID + " TEXT PRIMARY KEY," + NO_UNREAD_MSG + " INT" + ")";
    private static final String CREATE_TABLE_OFFERING = "CREATE TABLE " + TABLE_OFFERING + "(" + O_TYPE + " TEXT," + O_VALUE + " TEXT" + ")";
    private static final String CREATE_TABLE_ORDER_HISTORY = "CREATE TABLE " + TABLE_ORDER_HISTORY + "(" + ORDER_ID + " TEXT PRIMARY KEY," + PRODUCT_ID + " TEXT," + ORDER_TIME + " TEXT" + ")";


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_PRODUCTS);
        db.execSQL(CREATE_TABLE_BIKES);
        db.execSQL(CREATE_TABLE_SPECIFICATION);
        db.execSQL(CREATE_TABLE_FEATURE);
        db.execSQL(CREATE_TABLE_UNREAD_MSG);
        db.execSQL(CREATE_TABLE_OFFERING);
        db.execSQL(CREATE_TABLE_ORDER_HISTORY);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CARS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BIKES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SPECIFICATION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FEATURE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MSG);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OFFERING);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDER_HISTORY);

        // Create tables again
        //onCreate(db);
        db.execSQL(CREATE_TABLE_PRODUCTS);
        db.execSQL(CREATE_TABLE_BIKES);
        db.execSQL(CREATE_TABLE_SPECIFICATION);
        db.execSQL(CREATE_TABLE_FEATURE);
        db.execSQL(CREATE_TABLE_UNREAD_MSG);
        db.execSQL(CREATE_TABLE_OFFERING);
        db.execSQL(CREATE_TABLE_ORDER_HISTORY);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    public void addOrderHistory(String id, String productId, String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(ORDER_ID, id);
        values.put(PRODUCT_ID, productId);
        values.put(ORDER_TIME, time);
        db.insert(TABLE_ORDER_HISTORY, null, values);

        db.close(); // Closing database connection
    }

    public String getOrderHistory(String id) {
        String result = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;
        cursor = db.query(TABLE_ORDER_HISTORY, null, PRODUCT_ID + "=?", new String[]{id}, null, null, null);
        try {
            if (cursor.moveToLast()) {
                result = cursor.getString(cursor.getColumnIndex(ORDER_TIME));
            }
        } finally {
            cursor.close();
        }
        return result;
    }

    public void addOffering(String type, String value) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(O_TYPE, type);
        values.put(O_VALUE, value);
        db.insert(TABLE_OFFERING, null, values);

        db.close(); // Closing database connection
    }

    public void addChat(String id, int no_msg) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(CHAT_ID, id);
        values.put(NO_UNREAD_MSG, no_msg);
        db.insert(TABLE_MSG, null, values);

        db.close(); // Closing database connection
    }

    public void addSpecification(String id, String name, String value) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(ATTR_ID, id);
        values.put(ATTR_NAME, name);
        values.put(ATTR_VALUE, value);
        db.insert(TABLE_SPECIFICATION, null, values);

        db.close(); // Closing database connection
    }

    public void addFeature(String id, String name, String value) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(ATTR_ID, id);
        values.put(ATTR_NAME, name);
        values.put(ATTR_VALUE, value);
        db.insert(TABLE_FEATURE, null, values);

        db.close(); // Closing database connection
    }

    //Adding data in cars table
    public void addProducts(String productId, String productName, String productUrl, String bodyType, String engine, String brand, String modelName, int modelYear, int price, String fuelType, int seating, int distanceDriven, String color, String transmission, String sellerType, String mileage, int tankCapacity, String ownerNo, String carCondition, String sellerName, String sellerId, String sellerUrl, String profileId, String geoLocation, String address, String uploadDate, int assuredProduct) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(PRODUCT_ID, productId);
        values.put(PRODUCT_NAME, productName);
        values.put(PRODUCT_URL, productUrl);
        values.put(BODY_TYPE, bodyType);
        values.put(ENGINE, engine);
        values.put(BRAND, brand);
        values.put(MODEL_NAME, modelName);
        values.put(MODEL_YEAR, modelYear);
        values.put(PRICE, price);
        values.put(FUEL_TYPE, fuelType);
        values.put(SEATING, seating);
        values.put(DISTANCE_DRIVEN, distanceDriven);
        values.put(COLOR, color);
        values.put(TRANSMISSION, transmission);
        values.put(SELLER_TYPE, sellerType);
        values.put(MILEAGE, mileage);
        values.put(TANK_CAPACITY, tankCapacity);
        values.put(OWNER_NO, ownerNo);
        values.put(CAR_CONDITION, carCondition);
        values.put(SELLER_NAME, sellerName);
        values.put(SELLER_ID, sellerId);
        values.put(SELLER_URL, sellerUrl);
        values.put(PROFILE_ID, profileId);
        values.put(GEO_LOCATION, geoLocation);
        values.put(ADDRESS, address);
        values.put(UPLOAD_DATE, uploadDate);
        values.put(ASSURED_PRODUCT, assuredProduct);
        db.insert(TABLE_CARS, null, values);

        db.close(); // Closing database connection
    }

    //Adding data in bikes table
    public void addBikes(String productId, String productName, String productUrl, String engine,
                         String brand, String modelName, int modelYear, int price, int distanceDriven, int weight,
                         String color, String electricStart, String sellerType, String mileage, int tankCapacity,
                         int topSpeed, String ownerNo, String bikeCondition, String sellerName, String sellerId, String sellerUrl, String profileId, String geoLocation, String address, String uploadDate, int assuredProduct) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(BIKE_ID, productId);
        values.put(BIKE_NAME, productName);
        values.put(BIKE_URL, productUrl);
        values.put(BIKE_ENGINE, engine);
        values.put(BIKE_BRAND, brand);
        values.put(BIKE_MODEL, modelName);
        values.put(BIKE_MODEL_YEAR, modelYear);
        values.put(BIKE_PRICE, price);
        values.put(BIKE_DISTANCE_DRIVEN, distanceDriven);
        values.put(BIKE_WEIGHT, weight);
        values.put(BIKE_COLOR, color);
        values.put(ELECTRIC_START, electricStart);
        values.put(BIKE_MILEAGE, mileage);
        values.put(BIKE_TANK_CAPACITY, tankCapacity);
        values.put(TOP_SPEED, topSpeed);
        values.put(SELLER_TYPE, sellerType);
        values.put(OWNER_NO, ownerNo);
        values.put(BIKE_CONDITION, bikeCondition);
        values.put(SELLER_NAME, sellerName);
        values.put(SELLER_ID, sellerId);
        values.put(SELLER_URL, sellerUrl);
        values.put(PROFILE_ID, profileId);
        values.put(GEO_LOCATION, geoLocation);
        values.put(ADDRESS, address);
        values.put(UPLOAD_DATE, uploadDate);
        values.put(ASSURED_PRODUCT, assuredProduct);
        db.insert(TABLE_BIKES, null, values);

        db.close(); // Closing database connection
    }

    public void updateChat(String id, int no) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NO_UNREAD_MSG, no);
        db.update(TABLE_MSG, values, "id = '" + id + "'", null);
    }

    public boolean checkOrder(String id) {
        boolean result = false;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;
        cursor = db.query(TABLE_ORDER_HISTORY, null, ORDER_ID + "=?", new String[]{id}, null, null, null);
        try {
            if (cursor.moveToFirst()) {
                result = true;
            }
        } finally {
            cursor.close();
        }
        return result;
    }

    public boolean checkOrderProduct(String id) {
        boolean result = false;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;
        cursor = db.query(TABLE_ORDER_HISTORY, null, PRODUCT_ID + "=?", new String[]{id}, null, null, null);
        try {
            if (cursor.moveToFirst()) {
                result = true;
            }
        } finally {
            cursor.close();
        }
        return result;
    }

    public boolean checkOffering(String type, String value) {
        boolean result = false;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;
        cursor = db.query(TABLE_OFFERING, null, O_TYPE + "=? AND " + O_VALUE + "=?", new String[]{type, value}, null, null, null);
        try {
            if (cursor.moveToFirst()) {
                result = true;
            }
        } finally {
            cursor.close();
        }
        return result;
    }

    public boolean checkFeature(String id, String name) {
        boolean result = false;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;
        cursor = db.query(TABLE_FEATURE, null, ATTR_ID + "=? AND " + ATTR_NAME + "=?",
                new String[]{id, name}, null, null, null);
        try {
            if (cursor.moveToFirst()) {
                result = true;
            }
        } finally {
            cursor.close();
        }
        return result;
    }

    public boolean checkChat(String id) {
        boolean result = false;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;
        cursor = db.query(TABLE_MSG, null, CHAT_ID + "=?", new String[]{id}, null, null, null);
        try {
            if (cursor.moveToFirst()) {
                result = true;
            }
        } finally {
            cursor.close();
        }
        return result;
    }

    public int getNoUnreadMsg(String id) {
        int result = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;
        cursor = db.query(TABLE_MSG, null, CHAT_ID + "=?", new String[]{id}, null, null, null);
        try {
            if (cursor.moveToFirst()) {
                result = cursor.getInt(cursor.getColumnIndex(NO_UNREAD_MSG));
            }
        } finally {
            cursor.close();
        }
        return result;
    }

    public boolean checkProductExist(String no) {
        boolean result = false;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;
        cursor = db.query(TABLE_CARS, null, PRODUCT_ID + "=?", new String[]{no}, null, null, null);
        try {
            if (cursor.moveToFirst()) {
                result = true;
            }
        } finally {
            cursor.close();
        }
        return result;
    }

    public boolean checkBikeExist(String no) {
        boolean result = false;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;
        cursor = db.query(TABLE_BIKES, null, BIKE_ID + "=?", new String[]{no}, null, null, null);
        try {
            if (cursor.moveToFirst()) {
                result = true;
            }
        } finally {
            cursor.close();
        }
        return result;
    }

    public void removeAllSpecification() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SPECIFICATION, null, null);
        db.close();
    }

    public void removeAllFeatures() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FEATURE, null, null);
        db.close();
    }

    public void removeFeatures(String id, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FEATURE, ATTR_ID + "=? AND " + ATTR_NAME + "=?", new String[]{id, name});
        db.close();
    }

    public void removeAllProducts() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CARS, null, null);
        db.close();
    }

    public void removeAllBikes() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_BIKES, null, null);
        db.close();
    }

    public List<String[]> getSpecification(String id) {
        List<String[]> result = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_SPECIFICATION, null, ATTR_ID + "=?", new String[]{id}, null, null, null);
        Log.d("cursorP", String.valueOf(cursor.getCount()));
        try {
            if (cursor.moveToFirst()) {
                do {
                    String[] object = new String[2];
                    object[0] = cursor.getString(1); //NAME
                    object[1] = cursor.getString(2);//value
                    result.add(object);
                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
        }

        return result;
    }

    public String getFeatureVal(String id, String name) {
        String result = new String();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_FEATURE, null, ATTR_ID + "=? AND " + ATTR_NAME + "=?", new String[]{id, name}, null, null, null);
        Log.d("cursorP", String.valueOf(cursor.getCount()));
        try {
            if (cursor.moveToFirst()) {
                result = cursor.getString(2);
            }
        } finally {
            cursor.close();
        }

        return result;
    }

    public List<String[]> getFeatures(String id) {
        List<String[]> result = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_FEATURE, null, ATTR_ID + "=?", new String[]{id}, null, null, null);
        Log.d("cursorP", String.valueOf(cursor.getCount()));
        try {
            if (cursor.moveToFirst()) {
                do {
                    String[] object = new String[2];
                    object[0] = cursor.getString(1); //NAME
                    object[1] = cursor.getString(2); //VAL
                    result.add(object);

                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
        }

        return result;
    }


    public List<String[]> getProducts(String brandName, String modelName, String bodyType,
                                      String lowPrice, String highPrice, String orderBy, String order) {
        List<String[]> result = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        if (orderBy.isEmpty() || order.isEmpty()) {
            if (!brandName.isEmpty() && !modelName.isEmpty() && !bodyType.isEmpty() && !lowPrice.isEmpty() && !highPrice.isEmpty()
                    ) {
                cursor = db.query(TABLE_CARS, null, BRAND + "=? AND " + MODEL_NAME + "=? AND " + BODY_TYPE + "=? AND (" +
                                PRICE + " BETWEEN ? AND ?)",
                        new String[]{brandName, modelName, bodyType, lowPrice, highPrice}, null, null, null);
            } else if (!brandName.isEmpty() && !modelName.isEmpty() && bodyType.isEmpty() && !lowPrice.isEmpty() && !highPrice.isEmpty()
                    ) {
                cursor = db.query(TABLE_CARS, null, BRAND + "=? AND " + MODEL_NAME + "=? AND (" +
                                PRICE + " BETWEEN ? AND ?)",
                        new String[]{brandName, modelName, lowPrice, highPrice}, null, null, null);
            } else if (!brandName.isEmpty() && modelName.isEmpty() && !bodyType.isEmpty() && !lowPrice.isEmpty() && !highPrice.isEmpty()
                    ) {
                cursor = db.query(TABLE_CARS, null, BRAND + "=? AND " + BODY_TYPE + "=? AND (" +
                                PRICE + " BETWEEN ? AND ?)",
                        new String[]{brandName, bodyType, lowPrice, highPrice}, null, null, null);
            } else if (brandName.isEmpty() && modelName.isEmpty() && !bodyType.isEmpty() && !lowPrice.isEmpty() && !highPrice.isEmpty()
                    ) {
                cursor = db.query(TABLE_CARS, null, BODY_TYPE + "=? AND (" +
                                PRICE + " BETWEEN ? AND ?)",
                        new String[]{bodyType, lowPrice, highPrice}, null, null, null);
            } else if (!brandName.isEmpty() && modelName.isEmpty() && bodyType.isEmpty() && !lowPrice.isEmpty() && !highPrice.isEmpty()
                    ) {
                cursor = db.query(TABLE_CARS, null, BRAND + "=? AND (" +
                                PRICE + " BETWEEN ? AND ?)",
                        new String[]{brandName, lowPrice, highPrice}, null, null, null);
            } else if (brandName.isEmpty() && modelName.isEmpty() && bodyType.isEmpty() && !lowPrice.isEmpty() && !highPrice.isEmpty()
                    ) {
                cursor = db.query(TABLE_CARS, null, PRICE + " BETWEEN ? AND ? ",
                        new String[]{lowPrice, highPrice}, null, null, null);
            }
        } else {
            if (!brandName.isEmpty() && !modelName.isEmpty() && !bodyType.isEmpty() && !lowPrice.isEmpty() && !highPrice.isEmpty()
                    && !orderBy.isEmpty() && !order.isEmpty()) {
                cursor = db.query(TABLE_CARS, null, BRAND + "=? AND " + MODEL_NAME + "=? AND " + BODY_TYPE + "=? AND (" +
                                PRICE + " BETWEEN ? AND ?)",
                        new String[]{brandName, modelName, bodyType, lowPrice, highPrice}, null, null, orderBy + " " + order);
            } else if (!brandName.isEmpty() && !modelName.isEmpty() && bodyType.isEmpty() && !lowPrice.isEmpty() && !highPrice.isEmpty()
                    && !orderBy.isEmpty() && !order.isEmpty()) {
                cursor = db.query(TABLE_CARS, null, BRAND + "=? AND " + MODEL_NAME + "=? AND (" +
                                PRICE + " BETWEEN ? AND ?)",
                        new String[]{brandName, modelName, lowPrice, highPrice}, null, null, orderBy + " " + order);
            } else if (!brandName.isEmpty() && modelName.isEmpty() && !bodyType.isEmpty() && !lowPrice.isEmpty() && !highPrice.isEmpty()
                    && !orderBy.isEmpty() && !order.isEmpty()) {
                cursor = db.query(TABLE_CARS, null, BRAND + "=? AND " + BODY_TYPE + "=? AND (" +
                                PRICE + " BETWEEN ? AND ?)",
                        new String[]{brandName, bodyType, lowPrice, highPrice}, null, null, orderBy + " " + order);
            } else if (brandName.isEmpty() && modelName.isEmpty() && !bodyType.isEmpty() && !lowPrice.isEmpty() && !highPrice.isEmpty()
                    && !orderBy.isEmpty() && !order.isEmpty()) {
                cursor = db.query(TABLE_CARS, null, BODY_TYPE + "=? AND (" +
                                PRICE + " BETWEEN ? AND ?)",
                        new String[]{bodyType, lowPrice, highPrice}, null, null, orderBy + " " + order);
            } else if (!brandName.isEmpty() && modelName.isEmpty() && bodyType.isEmpty() && !lowPrice.isEmpty() && !highPrice.isEmpty()
                    && !orderBy.isEmpty() && !order.isEmpty()) {
                cursor = db.query(TABLE_CARS, null, BRAND + "=? AND (" +
                                PRICE + " BETWEEN ? AND ?)",
                        new String[]{brandName, lowPrice, highPrice}, null, null, orderBy + " " + order);
            } else if (brandName.isEmpty() && modelName.isEmpty() && bodyType.isEmpty() && !lowPrice.isEmpty() && !highPrice.isEmpty()
                    && !orderBy.isEmpty() && !order.isEmpty()) {
                cursor = db.query(TABLE_CARS, null, PRICE + " BETWEEN ? AND ? ",
                        new String[]{lowPrice, highPrice}, null, null, orderBy + " " + order);
            }
        }
        Log.d("cursorP", String.valueOf(cursor.getCount()));
        try {
            if (cursor.moveToFirst()) {
                do {
                    String[] object = new String[27];
                    object[0] = cursor.getString(0); //id
                    object[1] = cursor.getString(1); //NAME
                    object[2] = cursor.getString(2); //URL
                    object[3] = cursor.getString(3); //bodyType
                    object[4] = cursor.getString(4); //engine
                    object[5] = cursor.getString(5); //brand
                    object[6] = cursor.getString(6); //modelName
                    object[7] = String.valueOf(cursor.getInt(7)); //model Year
                    object[8] = String.valueOf(cursor.getInt(8)); //price
                    object[9] = cursor.getString(9); //fuelType
                    object[10] = String.valueOf(cursor.getInt(10)); //seating
                    object[11] = String.valueOf(cursor.getInt(11)); //distanceDriven
                    object[12] = cursor.getString(12); //color
                    object[13] = cursor.getString(13); //transmission
                    object[14] = cursor.getString(14); //sellerType
                    object[15] = cursor.getString(15); //mileage
                    object[16] = String.valueOf(cursor.getInt(16)); //tankCapacity
                    object[17] = cursor.getString(17); //ownerNo
                    object[18] = cursor.getString(18); //car condition
                    object[19] = cursor.getString(19); //sellerName
                    object[20] = cursor.getString(20); //sellerId
                    object[21] = cursor.getString(21); //sellerUrl
                    object[22] = cursor.getString(22); //profileId
                    object[23] = cursor.getString(23); //geoLocation
                    object[24] = cursor.getString(24);
                    object[25] = cursor.getString(25);
                    object[26] = String.valueOf(cursor.getInt(26));
                    result.add(object);

                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
        }

        return result;
    }

    public List<String[]> getBikes(String brandName, String modelName, String lowPrice,
                                   String highPrice, String orderBy, String order) {
        List<String[]> result = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        if (orderBy.isEmpty() || order.isEmpty()) {
            if (!brandName.isEmpty() && !modelName.isEmpty() && !lowPrice.isEmpty() && !highPrice.isEmpty()
                    ) {
                cursor = db.query(TABLE_BIKES, null, BIKE_BRAND + "=? AND " + BIKE_MODEL + "=? AND (" +
                                BIKE_PRICE + " BETWEEN ? AND ?)",
                        new String[]{brandName, modelName, lowPrice, highPrice}, null, null, null);
            } else if (!brandName.isEmpty() && modelName.isEmpty() && !lowPrice.isEmpty() && !highPrice.isEmpty()
                    ) {
                cursor = db.query(TABLE_BIKES, null, BIKE_BRAND + "=? AND (" +
                                BIKE_PRICE + " BETWEEN ? AND ?)",
                        new String[]{brandName, lowPrice, highPrice}, null, null, null);
            } else if (brandName.isEmpty() && modelName.isEmpty() && !lowPrice.isEmpty() && !highPrice.isEmpty()
                    ) {
                cursor = db.query(TABLE_BIKES, null, "(" +
                                BIKE_PRICE + " BETWEEN ? AND ?)",
                        new String[]{lowPrice, highPrice}, null, null, null);
            } else if (brandName.isEmpty() && modelName.isEmpty() && !lowPrice.isEmpty() && !highPrice.isEmpty()
                    ) {
                cursor = db.query(TABLE_BIKES, null, BIKE_PRICE + " BETWEEN ? AND ? ",
                        new String[]{lowPrice, highPrice}, null, null, null);
            }
        } else {
            if (!brandName.isEmpty() && !modelName.isEmpty() && !lowPrice.isEmpty() && !highPrice.isEmpty()
                    && !orderBy.isEmpty() && !order.isEmpty()) {
                cursor = db.query(TABLE_BIKES, null, BIKE_BRAND + "=? AND " + BIKE_MODEL + "=? AND (" +
                                BIKE_PRICE + " BETWEEN ? AND ?)",
                        new String[]{brandName, modelName, lowPrice, highPrice}, null, null, orderBy + " " + order);
            } else if (!brandName.isEmpty() && modelName.isEmpty() && !lowPrice.isEmpty() && !highPrice.isEmpty()
                    && !orderBy.isEmpty() && !order.isEmpty()) {
                cursor = db.query(TABLE_BIKES, null, BIKE_BRAND + "=? AND (" +
                                BIKE_PRICE + " BETWEEN ? AND ?)",
                        new String[]{brandName, lowPrice, highPrice}, null, null, orderBy + " " + order);
            } else if (brandName.isEmpty() && modelName.isEmpty() && !lowPrice.isEmpty() && !highPrice.isEmpty()
                    && !orderBy.isEmpty() && !order.isEmpty()) {
                cursor = db.query(TABLE_BIKES, null, "(" +
                                BIKE_PRICE + " BETWEEN ? AND ?)",
                        new String[]{lowPrice, highPrice}, null, null, orderBy + " " + order);
            } else if (brandName.isEmpty() && modelName.isEmpty() && !lowPrice.isEmpty() && !highPrice.isEmpty()
                    && !orderBy.isEmpty() && !order.isEmpty()) {
                cursor = db.query(TABLE_BIKES, null, BIKE_PRICE + " BETWEEN ? AND ? ",
                        new String[]{lowPrice, highPrice}, null, null, orderBy + " " + order);
            }
        }
        Log.d("cursorP", String.valueOf(cursor.getCount()));
        try {
            if (cursor.moveToFirst()) {
                do {
                    String[] object = new String[26];
                    object[0] = cursor.getString(0); //id
                    object[1] = cursor.getString(1); //NAME
                    object[2] = cursor.getString(2); //URL
                    object[3] = cursor.getString(3); //engine
                    object[4] = cursor.getString(4); //brand
                    object[5] = cursor.getString(5); //modelName
                    object[6] = String.valueOf(cursor.getInt(6)); //model Year
                    object[7] = String.valueOf(cursor.getInt(7)); //price
                    object[8] = String.valueOf(cursor.getInt(8)); //distanceDriven
                    object[9] = String.valueOf(cursor.getInt(9)); //weight
                    object[10] = cursor.getString(10); //color
                    object[11] = cursor.getString(11); //electric start
                    object[12] = cursor.getString(12); //mileage
                    object[13] = String.valueOf(cursor.getInt(13)); //tankCapacity
                    object[14] = String.valueOf(cursor.getInt(14)); //top speed
                    object[15] = cursor.getString(15); //sellerType
                    object[16] = cursor.getString(16); //ownerNo
                    object[17] = cursor.getString(17); //bike condition
                    object[18] = cursor.getString(18); //sellerName
                    object[19] = cursor.getString(19); //sellerId
                    object[20] = cursor.getString(20); //sellerUrl
                    object[21] = cursor.getString(21); //profileId
                    object[22] = cursor.getString(22); //geoLocation
                    object[23] = cursor.getString(23);
                    object[24] = cursor.getString(24);
                    object[25] = String.valueOf(cursor.getInt(25));
                    result.add(object);

                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
        }

        return result;
    }

}
