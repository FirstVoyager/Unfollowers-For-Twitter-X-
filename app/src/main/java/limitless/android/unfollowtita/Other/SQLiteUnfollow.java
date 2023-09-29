package limitless.android.unfollowtita.Other;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import limitless.android.unfollowtita.Model.Account;
import limitless.android.unfollowtita.Model.Type;
import limitless.android.unfollowtita.Model.UserModel;

public class SQLiteUnfollow extends SQLiteOpenHelper {

    private static final String name = "unfollowtita.sql";
    private static final int version = 4;

    // Account table
    private String tAccount = "account";
    private String tId = "id";
    private String tName = "name";
    private String tScreen = "screenName";
    private String tBio = "Bio";
    private String tProfile = "profileUrl";
    private String tHeader = "headerUrl";
    private String tAccessToken = "accessToken";
    private String tAccessSecret = "accessSecret";
    private String tConsumerKey = "consumerKey";
    private String tConsumerSecret = "consumerSecret";
    private String tIsMain = "isMain";
    private String tFollowingLoaded = "followingLoaded";
    private String tFollowersLoaded = "followersLoaded";
    private String tBlockedLoaded = "blockedLoaded";
    private String tMutedLoaded = "mutedLoaded";
    private String tAccountCode = "CREATE TABLE IF NOT EXISTS " + tAccount + " (" +
            tId + " INTEGER, " +
            tName + " TEXT, " +
            tScreen + " TEXT, " +
            tBio + " TEXT, " +
            tProfile + " TEXT, " +
            tHeader + " TEXT, " +
            tAccessToken + " TEXT, " +
            tAccessSecret + " TEXT, " +
            tConsumerKey + " TEXT, " +
            tConsumerSecret + " TEXT, " +
            tIsMain + " INTEGER , " +
            tFollowingLoaded + " INTEGER, " +
            tFollowersLoaded + " INTEGER, " +
            tBlockedLoaded + " INTEGER, " +
            tMutedLoaded + " INTEGER" +
            ");";
    // users table
    private String uTable = "users";
    private String uId = "id";
    private String uUserId = "userID";
    private String uAccount = "account";
    private String uType = "type";
    private String tUserCode = "CREATE TABLE IF NOT EXISTS " + uTable + " (" +
            uId + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            uUserId + " INTEGER, " +
            uAccount + " INTEGER, " +
            uType + " TEXT " +
            ")";

    private long accountId;
    private SQLiteDatabase database;

    public SQLiteUnfollow(Context context, long accountId) {
        super(context, name, null, version);
        this.accountId = accountId;
    }

    public SQLiteUnfollow(Context context){
        super(context, name, null, version);
        this.accountId = 0;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(tAccountCode);
        db.execSQL(tUserCode);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            if (4 == version){
                db.execSQL(tAccountCode);
                db.execSQL(tUserCode);
            }
        }catch (Exception e){
            Utils.error(e);
        }
    }

    private void read(){
        try {
            database = getReadableDatabase();
        } catch (Exception e) {
            Utils.error(e);
        }
    }

    private void write(){
        try {
            database = getWritableDatabase();
        } catch (Exception e) {
            Utils.error(e);
        }
    }

    public void setAccountId(long id){
        this.accountId = id;
    }

    // Account table
    public boolean insertAccount(@Nullable Account a){
        if (a == null)
            return false;
        ContentValues cv = new ContentValues();
        cv.put(tId, a.id);
        cv.put(tName, a.name);
        cv.put(tScreen, a.screenName);
        cv.put(tBio, a.bio);
        cv.put(tProfile, a.profileUrl);
        cv.put(tHeader, a.headerUrl);
        cv.put(tAccessToken, a.accessToken);
        cv.put(tAccessSecret, a.accessSecret);
        cv.put(tConsumerKey, a.consumerToken);
        cv.put(tConsumerSecret, a.consumerSecret);
        cv.put(tIsMain, a.isMain);
        cv.put(tFollowingLoaded, a.followingLoaded);
        cv.put(tFollowersLoaded, a.followersLoaded);
        cv.put(tBlockedLoaded, a.blockedLoaded);
        cv.put(tMutedLoaded, a.mutedLoaded);
        write();
        return database.insert(tAccount, null, cv) > 0;
    }

    public boolean deleteAccount(long id){
        write();
        return database.delete(tAccount, tId + "=?", new String[]{String.valueOf(id)}) > 0;
    }

    private Account getAccount(long id){
        return null;
    }

    public int accountCount(){
        read();
        Cursor cursor = database.rawQuery("SELECT * FROM " + tAccount, null);
        if (cursor == null)
            return 0;
        int n = cursor.getCount();
        cursor.close();
        return n;
    }

    public Account getMainAccount(){
        read();
        Cursor cursor = database.rawQuery("SELECT * FROM " + tAccount + " WHERE " + tIsMain + "=1", null);
        Account a = new Account();
        if (cursor == null)
            return a;
        if (cursor.getCount() <= 0){
            cursor.close();
            return a;
        }
        cursor.moveToFirst();
        a.name = cursor.getString(cursor.getColumnIndex(tName));
        a.screenName = cursor.getString(cursor.getColumnIndex(tScreen));
        a.bio = cursor.getString(cursor.getColumnIndex(tBio));
        a.id = cursor.getLong(cursor.getColumnIndex(tId));
        a.profileUrl = cursor.getString(cursor.getColumnIndex(tProfile));
        a.headerUrl = cursor.getString(cursor.getColumnIndex(tHeader));
        a.isMain = true;
        a.accessToken = cursor.getString(cursor.getColumnIndex(tAccessToken));
        a.accessSecret = cursor.getString(cursor.getColumnIndex(tAccessSecret));
        a.consumerToken = cursor.getString(cursor.getColumnIndex(tConsumerKey));
        a.consumerSecret = cursor.getString(cursor.getColumnIndex(tConsumerSecret));
        a.followingLoaded = cursor.getInt(cursor.getColumnIndex(tFollowingLoaded)) == 1;
        a.followersLoaded = cursor.getInt(cursor.getColumnIndex(tFollowersLoaded)) == 1;
        a.blockedLoaded = cursor.getInt(cursor.getColumnIndex(tBlockedLoaded)) == 1;
        a.mutedLoaded = cursor.getInt(cursor.getColumnIndex(tMutedLoaded)) == 1;
        return a;
    }

    /**
     * Set current account id
     * @param id
     */
    public void setCurrentAccount(long id) {
        accountId = id;
    }

    public void setMainAccount(long id){
        write();
        String query = "UPDATE " + tAccount + " SET " + tIsMain + "=0" + " WHERE " + tIsMain + "=1";
        database.execSQL(query);
        query = "UPDATE " + tAccount + " SET " + tIsMain + "=1" + " WHERE " + tId + "=" + id;
        database.execSQL(query);
    }

    public List<Account> getAccounts(){
        read();
        Cursor cursor = database.rawQuery("SELECT * FROM " + tAccount, null);
        if (cursor == null)
            return null;
        if (cursor.getCount() <= 0 || ! cursor.moveToFirst()){
            cursor.close();
            return null;
        }
        List<Account> accounts = new ArrayList<>();
        int name = cursor.getColumnIndex(tName);
        int screen = cursor.getColumnIndex(tScreen);
        int id = cursor.getColumnIndex(tId);
        int bio = cursor.getColumnIndex(tBio);
        int profile = cursor.getColumnIndex(tProfile);
        int header = cursor.getColumnIndex(tHeader);
        int main = cursor.getColumnIndex(tIsMain);
        int access = cursor.getColumnIndex(tAccessToken);
        int secret = cursor.getColumnIndex(tAccessSecret);
        int consumerKey = cursor.getColumnIndex(tConsumerKey);
        int consumerSecret = cursor.getColumnIndex(tConsumerSecret);
        int followingLoaded = cursor.getColumnIndex(tFollowingLoaded);
        int followersLoaded = cursor.getColumnIndex(tFollowersLoaded);
        int blockedLoaded = cursor.getColumnIndex(tBlockedLoaded);
        int mutedLoaded = cursor.getColumnIndex(tMutedLoaded);
        do {
            accounts.add(new Account(
                    cursor.getLong(id),
                    cursor.getString(name),
                    cursor.getString(screen),
                    cursor.getString(bio),
                    cursor.getString(profile),
                    cursor.getString(header),
                    cursor.getString(access),
                    cursor.getString(secret),
                    cursor.getString(consumerKey),
                    cursor.getString(consumerSecret),
                    cursor.getInt(main) == 1,
                    cursor.getInt(followingLoaded) == 1,
                    cursor.getInt(followersLoaded) == 1,
                    cursor.getInt(blockedLoaded) == 1,
                    cursor.getInt(mutedLoaded) == 1
            ));
        }while (cursor.moveToNext());
        cursor.close();
        return accounts;
    }

    public boolean checkAccount(long id) {
        read();
        Cursor cursor = database.rawQuery("SELECT * FROM " + tAccount + " WHERE " + tId + "=" + id + " LIMIT 1", null);
        if (cursor == null)
            return false;
        if (cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        return true;
    }

    public boolean updateAccount(@Nullable Account account) {
        if (account == null)
            return false;
        ContentValues cv = new ContentValues();
        cv.put(tName, account.name);
        cv.put(tProfile, account.profileUrl);
        cv.put(tHeader, account.headerUrl);
        cv.put(tBio, account.bio);
        cv.put(tScreen, account.screenName);
        cv.put(tFollowingLoaded, account.followingLoaded);
        cv.put(tFollowersLoaded, account.followersLoaded);
        cv.put(tBlockedLoaded, account.blockedLoaded);
        cv.put(tMutedLoaded, account.mutedLoaded);
        write();
        return database.update(tAccount, cv, tId + "=?", new String[]{String.valueOf(account.id)}) > 0;
    }

    // users
    public boolean insertUser(UserModel userModel){
        write();
        ContentValues cv = new ContentValues();
        cv.put(uUserId, userModel.userId);
        cv.put(uAccount, userModel.accountId);
        cv.put(uType, userModel.type);
        return database.insert(uTable, null, cv) > 0;
    }

    public void insertUser(@Nullable List<UserModel> userModels){
        if (userModels == null || userModels.size() <= 0)
            return;
        write();
        ContentValues cv;
        for (UserModel u : userModels) {
            cv = new ContentValues();
            cv.put(uUserId, u.userId);
            cv.put(uAccount, u.accountId);
            cv.put(uType, u.type);
            database.insert(uTable, null, cv);
        }
    }

    public boolean insertUser(long id, @Nullable Type type){
        if (type == null)
            return false;
        ContentValues cv = new ContentValues();
        cv.put(uUserId, id);
        cv.put(uType, type.name());
        cv.put(uAccount, accountId);
        write();
        return database.insert(uTable, null, cv) > 0;

    }

    public void insertUser(@Nullable long[] ids, @Nullable Type t){
        if (ids == null || ids.length <= 0 || t == null)
            return;
        write();
        ContentValues cv;
        for (long id: ids) {
            cv = new ContentValues();
            cv.put(uUserId, id);
            cv.put(uAccount, accountId);
            cv.put(uType, t.name());
            database.insert(uTable, null, cv);
        }
    }

    public boolean deleteUser(long id, @Nullable Type type){
        if (type == null)
            return false;
        write();
        return database.delete(uTable, uUserId + "=? AND " + uAccount + "=? AND " + uType + "=?", new String[]{String.valueOf(id), String.valueOf(accountId), type.name()}) > 0;
    }

    public void deleteUsers(@Nullable List<UserModel> userModels){
        if (userModels == null || userModels.size() <= 0)
            return;
        write();
        for (UserModel u: userModels) {
            database.delete(uTable, uUserId + "=? AND " + uAccount + "=?", new String[]{String.valueOf(u.userId), String.valueOf(accountId)});
        }
    }

    /**
     * Get users from database with custom type
     * @param type User type
     * @param listener
     */
    public void getUsers(Type type, Listener<List<UserModel>> listener) {
        new GetUsers(listener).execute(type);
    }

    public List<UserModel> getUsers(Type type){
        read();
        String typeName = type.name();
        Cursor cursor = database.rawQuery("SELECT * FROM " + uTable + " WHERE " + uType + "='" + typeName + "' AND " + uAccount + "=" + accountId, null);
        if (cursor == null)
            return new ArrayList<>();
        if (cursor.getCount() <= 0 || ! cursor.moveToFirst()){
            cursor.close();
            return new ArrayList<>();
        }
        int id = cursor.getColumnIndex(uId);
        int user = cursor.getColumnIndex(uUserId);
        int account = cursor.getColumnIndex(uAccount);
        cursor.moveToFirst();
        List<UserModel> userModels = new ArrayList<>();
        do {
            userModels.add(new UserModel(
                    cursor.getInt(id),
                    cursor.getLong(user),
                    cursor.getLong(account),
                    null,
                    typeName
            ));
        }while (cursor.moveToNext());
        return userModels;
    }

    /**
     * Get users size in custom category
     * @param type Category
     * @return
     */
    public int userSize(@Nullable Type type){
        if (type == null)
            return 0;
        read();
        Cursor cursor = database.rawQuery("SELECT * FROM " + uTable + " WHERE " + uType + "='" + type.name() + "' AND " + uAccount + "=" + accountId, null);
        if (cursor == null)
            return 0;
        int n = cursor.getCount();
        cursor.close();
        return n;
    }

    public boolean deleteUsers(@Nullable Type type) {
        if (type == null)
            return false;
        write();
        return database.delete(uTable, uType + "=? AND " + uAccount + "=?", new String[]{type.name(), String.valueOf(accountId)}) > 0;
    }

    public void insertUsers(@Nullable long[] ids, @Nullable Type type, @Nullable Listener<Boolean> listener){
        if (ids == null || ids.length <= 0 || type == null)
            return;
        new InsertUsers(ids, type, listener).execute();
    }

    private class InsertUsers extends AsyncTask<Void, Void, Boolean>{
        private long[] ids;
        private Type type;
        private Listener<Boolean> listener;
        public InsertUsers(long[] ids, Type type, Listener<Boolean> listener) {
            this.ids = ids;
            this.type = type;
            this.listener = listener;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                insertUser(ids, type);
                return true;
            } catch (Exception e) {
                Utils.error(e);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (listener != null)
                listener.data(aBoolean);
        }
    }

    /**
     * Get users in background
     */
    private class GetUsers extends AsyncTask<Type, Void, List<UserModel>> {

        private Listener<List<UserModel>> listener;

        private GetUsers(Listener<List<UserModel>> listener) {
            this.listener = listener;
        }

        @Override
        protected List<UserModel> doInBackground(Type... types) {
            return getUsers(types[0]);
        }

        @Override
        protected void onPostExecute(List<UserModel> userModels) {
            super.onPostExecute(userModels);
            listener.data(userModels);
        }
    }

}
