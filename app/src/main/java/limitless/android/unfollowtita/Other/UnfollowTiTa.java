package limitless.android.unfollowtita.Other;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

import limitless.android.unfollowtita.Model.Account;
import limitless.android.unfollowtita.Model.TopFriends;
import twitter4j.IDs;
import twitter4j.PagableResponseList;
import twitter4j.Paging;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Relationship;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;

public class UnfollowTiTa {

    private Twitter twitter;
    private SharePref sharePref;
    private SQLiteUnfollow sqLite;

    public UnfollowTiTa(Context context) {
        this.sharePref = new SharePref(context);
        this.sqLite = new SQLiteUnfollow(context, 0);
        Account account = this.sqLite.getMainAccount();
        if (account == null)
            return;
        sqLite.setAccountId(account.id);
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.setOAuthConsumerKey(account.consumerToken);
        builder.setOAuthConsumerSecret(account.consumerSecret);
        builder.setOAuthAccessToken(account.accessToken);
        builder.setOAuthAccessTokenSecret(account.accessSecret);

        this.twitter = new TwitterFactory(builder.build()).getInstance();
    }

    public static TopFriends getRealFriend(Status s) {
        TopFriends model = new TopFriends();
        if (s.isRetweet()){
            model.user = s.getRetweetedStatus().getUser();
            model.screenName = s.getRetweetedStatus().getUser().getScreenName();
            model.id = s.getRetweetedStatus().getId();
            model.count = 0;
        }else if (s.getText().startsWith("@")){
            model.screenName = s.getInReplyToScreenName();
            model.user = null;
            model.id = 0;
            model.count = 0;
        }else {
            model.user = s.getUser();
            model.screenName = s.getUser().getScreenName();
            model.id = s.getUser().getId();
            model.count = 0;
        }
        return model;
    }

    public static String tweetUrl(String screenName, long id) {
        return "https://twitter.com/" + screenName + "/status/" + id;
    }

    public Twitter getTwitter(){
        return twitter;
    }

    /**
     * Get following users
     * @param id For a other users following
     * @param cursor Next page id
     * @param listener
     */
    public void getFollowing(String id, long cursor, Listener<PagableResponseList<User>> listener){
        new GetFollowing(id, cursor, listener).execute();
    }

    /**
     * Unfollow user
     * @param id User id in long
     * @param listener
     */
    public void unfollowUser(long id, Listener<User> listener) {
        new UnfollowUser(id, listener).execute();
    }

    /**
     * Get followers
     * @param id Account id
     * @param cursor Next page id
     * @param listener
     */
    public void getFollowers(String id, long cursor, Listener<PagableResponseList<User>> listener) {
        new GetFollowers(id, cursor, listener).execute();
    }

    /**
     * Show user information
     * @param id User id in long
     * @param idS User screen name
     * @param listener
     */
    public void showUser(long id, String idS, Listener<User> listener) {
        new ShowUser(id, idS, listener).execute();
    }

    /**
     * Get users details from ids
     * @param ids User ids
     * @param listener
     */
    public void lookupUsers(long[] ids, Listener<ResponseList<User>> listener){
            new LookupUsers(ids, listener).execute();
    }

    public void blockUser(long id, Listener<User> listener) {
        new GetBlockUser(id, listener).execute();
    }

    public void muteUser(long id, Listener<User> listener) {
        new GetMuteUser(id, listener).execute();
    }

    public void followUser(long id, Listener<User> listener) {
        new GetFollowUser(id, listener).execute();
    }

    /**
     * Follow user with string id
     * @param screenName User screen name
     * @param listener
     */
    public void followUser(String screenName, Listener<User> listener){
        new  GetFollowUserS(listener).execute(screenName);
    }

    /**
     * Get following ids
     * @param cursor Next page ids
     * @param listener
     */
    public void getFollowingIDs(long cursor, Listener<IDs> listener) {
        new LoadAllFollowing(cursor, listener).execute();
    }

    /**
     * Get followers ids
     * @param cursor Next page id
     * @param listener
     */
    public void getFollowersIDs(long cursor, Listener<IDs> listener) {
        new LoadAllFollowers(listener).execute(cursor);
    }

    /**
     * Get all blocked users ids
     * @param listener
     */
    public void getAllBlockedIds(Listener<IDs> listener){
        new GetAllBlockedIds(listener).execute();
    }

    /**
     * Get muted user ids
     * @param cursor Next page id
     * @param listener
     */
    public void getMutedIds(long cursor, Listener<IDs> listener) {
        new GetAllMutedIds(listener).execute(cursor);
    }

    /**
     * Get muted users
     * @param l Next cursor id
     * @param listener
     */
    public void getMutedUsers(long l, Listener<PagableResponseList<User>> listener) {
        new GetMutedUsers(listener).execute(l);
    }

    public void unMuteUser(long id, Listener<User> listener) {
        new UnMuteUser(listener).execute(id);
    }

    public void unBlockUser(long id, Listener<User> listener) {
        new UnBlockUser(listener).execute(id);
    }

    /**
     * Get blocked users
     * @param cursor Next page id
     * @param listener
     */
    public void getBlockedUser(long cursor, Listener<PagableResponseList<User>> listener) {
        new GetBlockedUser(listener).execute(cursor);
    }

    /**
     * Send your new tweet
     * @param statusUpdate Your new status
     * @param listener Listener for result
     */
    public void updateStatus(StatusUpdate statusUpdate, Listener<Status> listener) {
        new StatusUpdateT(listener).execute(statusUpdate);
    }

    public void searchUser(String query, int i, Listener<ResponseList<User>> listener){
        new SearchUser(listener, query).execute(i);
    }

    public void getUserFollowing(String id, long cursor, Listener<PagableResponseList<User>> listener){
        new GetUserFollowing(cursor, listener).execute(id);
    }

    public void getUserFollowers(String id, long cursor, Listener<PagableResponseList<User>> listener) {
        new GetUserFollowers(cursor, listener).execute(id);
    }

    public void getHomeTimeLine(Paging paging, Listener<List<Status>> listener) {
        new GetHomeTimeLine(listener).execute(paging);
    }

    public void getFirstTweet(Query query, Listener<List<Status>> listener) {
        new GetFirstStatus(listener).execute(query);
    }

    public void searchTweets(Query query, Listener<QueryResult> listener) {
        new SearchTweets(listener).execute(query);
    }

    /**
     * Get main user home time line
     * @param page Page of tweets
     * @param listener
     */
    public void getUserTimeLine(Paging page, Listener<List<Status>> listener) {
        new GetUserTimeLine(page, listener).execute();
    }

    public void getTweets(Paging paging, long id, Listener<ResponseList<Status>> listListener) {
        new GetTweets(paging, id, listListener).execute();
    }

    private class GetTweets extends AsyncTask<Void, Void, ResponseList<Status>>{
        private Paging paging;
        private long id;
        private Listener<ResponseList<twitter4j.Status>> listListener;

        public GetTweets(Paging paging, long id, Listener<ResponseList<twitter4j.Status>> listListener) {
            this.paging = paging;
            this.id = id;
            this.listListener = listListener;
        }

        @Override
        protected ResponseList<twitter4j.Status> doInBackground(Void... voids) {
            try {
                return twitter.getUserTimeline(id, paging);
            } catch (TwitterException e) {
                Utils.error(e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(ResponseList<twitter4j.Status> statuses) {
            super.onPostExecute(statuses);
            if (listListener != null)
                listListener.data(statuses);
        }
    }

    /**
     * Get main user home tile line in background
     */
    private class GetUserTimeLine extends AsyncTask<Void, Void, List<Status>>{

        private Paging paging;
        private Listener<List<twitter4j.Status>> listener;

        public GetUserTimeLine(Paging paging, Listener<List<twitter4j.Status>> listener) {
            this.paging = paging;
            this.listener = listener;
        }

        @Override
        protected List<twitter4j.Status> doInBackground(Void... voids) {
            try {
                return twitter.getUserTimeline(paging);
            } catch (TwitterException e) {
                Utils.error(e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<twitter4j.Status> list) {
            super.onPostExecute(list);
            listener.data(list);
        }
    }

    private class SearchTweets extends AsyncTask<Query, Void, QueryResult>{

        private Listener<QueryResult> listener;

        public SearchTweets(Listener<QueryResult> listener) {
            this.listener = listener;
        }

        @Override
        protected QueryResult doInBackground(Query... queries) {
            try {
                return twitter.search(queries[0]);
            } catch (TwitterException e) {
                Utils.error(e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(QueryResult queryResult) {
            super.onPostExecute(queryResult);
            listener.data(queryResult);
        }
    }

    private class GetFirstStatus extends AsyncTask<Query, Void, List<Status>>{

        private Listener<List<twitter4j.Status>> listener;

        public GetFirstStatus(Listener<List<twitter4j.Status>> listener) {
            this.listener = listener;
        }

        @Override
        protected List<twitter4j.Status> doInBackground(Query... search) {
            try {
                QueryResult queryResult = twitter.search(search[0]);
                return queryResult.getTweets();
            }catch (TwitterException e){
                Utils.error(e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<twitter4j.Status> statuses) {
            super.onPostExecute(statuses);
            listener.data(statuses);
        }
    }

    private class GetHomeTimeLine extends AsyncTask<Paging, Void, ResponseList<Status>>{

        private Listener<List<twitter4j.Status>> listener;

        public GetHomeTimeLine(Listener<List<twitter4j.Status>> listener) {
            this.listener = listener;
        }

        @Override
        protected ResponseList<twitter4j.Status> doInBackground(Paging... pagings) {
            try {
                return twitter.getHomeTimeline(pagings[0]);
            } catch (TwitterException e) {
                Utils.error(e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(ResponseList<twitter4j.Status> statuses) {
            super.onPostExecute(statuses);
            listener.data(statuses);
        }
    }

    private class GetUserFollowers extends AsyncTask<String, Void, PagableResponseList<User>>{

        private long cursor;
        private Listener<PagableResponseList<User>> listener;

        public GetUserFollowers(long cursor, Listener<PagableResponseList<User>> listener) {
            this.cursor = cursor;
            this.listener = listener;
        }

        @Override
        protected PagableResponseList<User> doInBackground(String... strings) {
            try {
                return twitter.getFollowersList(strings[0], cursor);
            } catch (TwitterException e) {
                Utils.error(e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(PagableResponseList<User> users) {
            super.onPostExecute(users);
            listener.data(users);
        }
    }

    private class GetUserFollowing extends AsyncTask<String, Void, PagableResponseList<User>>{

        private long cursor;
        private Listener<PagableResponseList<User>> listener;

        public GetUserFollowing(long cursor, Listener<PagableResponseList<User>> listener) {
            this.cursor = cursor;
            this.listener = listener;
        }

        @Override
        protected PagableResponseList<User> doInBackground(String... strings) {
            try {
                return twitter.getFriendsList(strings[0], cursor);
            } catch (TwitterException e) {
                Utils.error(e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(PagableResponseList<User> users) {
            super.onPostExecute(users);
            listener.data(users);
        }
    }

    private class SearchUser extends AsyncTask<Integer, Void, ResponseList<User>>{

        private Listener<ResponseList<User>> listener;
        private String query;

        public SearchUser(Listener<ResponseList<User>> listener, String query) {
            this.listener = listener;
            this.query = query;
        }

        @Override
        protected ResponseList<User> doInBackground(Integer... integers) {
            try {
                return twitter.searchUsers(query, integers[0]);
            } catch (TwitterException e) {
                Utils.error(e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(ResponseList<User> users) {
            super.onPostExecute(users);
            listener.data(users);
        }
    }

    /**
     * Update status in background
     */
    private class StatusUpdateT extends AsyncTask<StatusUpdate, Void, Status>{

        private Listener<twitter4j.Status> listener;

        private StatusUpdateT(Listener<twitter4j.Status> listener) {
            this.listener = listener;
        }

        @Override
        protected twitter4j.Status doInBackground(StatusUpdate... statusUpdates) {
            try {
                return twitter.updateStatus(statusUpdates[0]);
            } catch (TwitterException e) {
                Utils.error(e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(twitter4j.Status status) {
            super.onPostExecute(status);
            listener.data(status);
        }
    }

    /**
     * Follow user in background
     */
    private class GetFollowUserS extends AsyncTask<String, Void, User>{

        private Listener<User> listener;

        public GetFollowUserS(Listener<User> listener) {
            this.listener = listener;
        }

        @Override
        protected User doInBackground(String... strings) {
            try {
                return twitter.createFriendship(strings[0]);
            } catch (TwitterException e) {
                Utils.error(e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(User user) {
            super.onPostExecute(user);
            listener.data(user);
        }
    }

    /**
     * Get blocked users in background
     */
    private class GetBlockedUser extends AsyncTask<Long, Void, PagableResponseList<User>>{

        private Listener<PagableResponseList<User>> listener;

        public GetBlockedUser(Listener<PagableResponseList<User>> listener) {
            this.listener = listener;
        }

        @Override
        protected PagableResponseList<User> doInBackground(Long... longs) {
            try {
                return twitter.getBlocksList(longs[0]);
            } catch (TwitterException e) {
                Utils.error(e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(PagableResponseList<User> users) {
            super.onPostExecute(users);
            listener.data(users);
        }
    }

    private class UnBlockUser extends AsyncTask<Long, Void, User>{

        private Listener<User> listener;

        public UnBlockUser(Listener<User> listener) {
            this.listener = listener;
        }

        @Override
        protected User doInBackground(Long... longs) {
            try {
                return twitter.destroyBlock(longs[0]);
            } catch (TwitterException e) {
                Utils.error(e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(User user) {
            super.onPostExecute(user);
            listener.data(user);
        }
    }

    private class UnMuteUser extends AsyncTask<Long, Void, User>{

        private Listener<User> listener;

        public UnMuteUser(Listener<User> listener) {
            this.listener = listener;
        }

        @Override
        protected User doInBackground(Long... longs) {
            try {
                return twitter.destroyMute(longs[0]);
            } catch (TwitterException e) {
                Utils.error(e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(User user) {
            super.onPostExecute(user);
            listener.data(user);
        }
    }

    /**
     * Get muted users in background
     */
    private class GetMutedUsers extends AsyncTask<Long, Void, PagableResponseList<User>>{

        private Listener<PagableResponseList<User>> listener;

        public GetMutedUsers(Listener<PagableResponseList<User>> listener) {
            this.listener = listener;
        }

        @Override
        protected PagableResponseList<User> doInBackground(Long... longs) {
            try {
                return twitter.getMutesList(longs[0]);
            } catch (TwitterException e) {
                Utils.error(e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(PagableResponseList<User> users) {
            super.onPostExecute(users);
            listener.data(users);
        }
    }

    /**
     * Get muted users in background
     */
    private class GetAllMutedIds extends AsyncTask<Long, Void, IDs>{

        private Listener<IDs> listener;

        public GetAllMutedIds(Listener<IDs> listener) {
            this.listener = listener;
        }

        @Override
        protected IDs doInBackground(Long... longs) {
            try {
                return twitter.getMutesIDs(longs[0]);
            } catch (TwitterException e) {
                Utils.error(e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(IDs iDs) {
            super.onPostExecute(iDs);
            listener.data(iDs);
        }
    }

    /**
     * Get blocked user ids in background
     */
    private class GetAllBlockedIds extends AsyncTask<Void, Void, IDs>{

        private Listener<IDs> listener;

        public GetAllBlockedIds(Listener<IDs> listener) {
            this.listener = listener;
        }

        @Override
        protected IDs doInBackground(Void... voids) {
            try {
                return twitter.getBlocksIDs();
            } catch (TwitterException e) {
                Utils.error(e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(IDs iDs) {
            super.onPostExecute(iDs);
            listener.data(iDs);
        }
    }

    /**
     * Get followers ids in next page
     */
    private class LoadAllFollowers extends AsyncTask<Long, Void, IDs>{

        private Listener<IDs> listener;

        public LoadAllFollowers(Listener<IDs> listener) {
            this.listener = listener;
        }

        @Override
        protected IDs doInBackground(Long... longs) {
            try {
                return twitter.getFollowersIDs(longs[0]);
            } catch (TwitterException e) {
                Utils.error(e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(IDs iDs) {
            super.onPostExecute(iDs);
            listener.data(iDs);
        }
    }

    /**
     * Get following ids in background
     */
    private class LoadAllFollowing extends AsyncTask<Void, Void, IDs>{

        private long cursor;
        private Listener<IDs> listener;

        public LoadAllFollowing(long cursor, Listener<IDs> listener) {
            this.cursor = cursor;
            this.listener = listener;
        }

        @Override
        protected IDs doInBackground(Void... voids) {
            try {
                return twitter.getFriendsIDs(cursor);
            } catch (TwitterException e) {
                Utils.error(e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(IDs iDs) {
            super.onPostExecute(iDs);
            listener.data(iDs);
        }
    }

    private class GetFollowUser extends AsyncTask<Void, Void, User>{

        private long id;
        private Listener<User> listener;

        public GetFollowUser(long id, Listener<User> listener) {
            this.id = id;
            this.listener = listener;
        }

        @Override
        protected User doInBackground(Void... voids) {
            try {
                return twitter.createFriendship(id);
            } catch (TwitterException e) {
                Utils.error(e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(User user) {
            super.onPostExecute(user);
            listener.data(user);
        }
    }

    private class GetMuteUser extends AsyncTask<Void, Void, User>{

        private long id;
        private Listener<User> listener;

        public GetMuteUser(long id, Listener<User> listener) {
            this.id = id;
            this.listener = listener;
        }

        @Override
        protected User doInBackground(Void... voids) {
            try {
                return twitter.createMute(id);
            } catch (TwitterException e) {
                Utils.error(e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(User user) {
            super.onPostExecute(user);
            listener.data(user);
        }
    }

    private class GetBlockUser extends AsyncTask<Void, Void, User>{

        private long id;
        private Listener<User> listener;

        public GetBlockUser(long id, Listener<User> listener) {
            this.id = id;
            this.listener = listener;
        }

        @Override
        protected User doInBackground(Void... voids) {
            try {
                return twitter.createBlock(id);
            } catch (TwitterException e) {
                Utils.error(e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(User user) {
            super.onPostExecute(user);
            listener.data(user);
        }
    }

    private class GetLookRelationship extends AsyncTask<Void, Void, Relationship>{

        private long id;
        private Listener<Relationship> listener;

        public GetLookRelationship(long id, Listener<Relationship> listener) {
            this.id = id;
            this.listener = listener;
        }

        @Override
        protected Relationship doInBackground(Void... voids) {
            try {
                return twitter.showFriendship(sharePref.getLong(SharePref.userId, 0), id);
            } catch (TwitterException e) {
                Utils.error(e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(Relationship friendship) {
            super.onPostExecute(friendship);
            listener.data(friendship);
        }
    }

    private class GetFollowingIDs extends AsyncTask<Void, Void, IDs>{

        private long id, cursor;
        private Listener<IDs> listener;

        public GetFollowingIDs(long id, long cursor, Listener<IDs> listener) {
            this.id = id;
            this.cursor = cursor;
            this.listener = listener;
        }

        @Override
        protected IDs doInBackground(Void... voids) {
            try {
                return twitter.getFriendsIDs(id, cursor, 100);
            } catch (TwitterException e) {
                Utils.error(e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(IDs iDs) {
            super.onPostExecute(iDs);
            listener.data(iDs);
        }
    }

    /**
     * Get user information in background
     */
    private class ShowUser extends AsyncTask<Void, Void, User>{

        private long id;
        private String idS;
        private Listener<User> listener;

        public ShowUser(long id, String idS, Listener<User> listener) {
            this.id = id;
            this.idS = idS;
            this.listener = listener;
        }

        @Override
        protected User doInBackground(Void... voids) {
            try {
                if (idS == null){
                    return twitter.showUser(id);
                }else {
                    return twitter.showUser(idS);
                }
            } catch (TwitterException e) {
                Utils.error(e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(User user) {
            super.onPostExecute(user);
            listener.data(user);
        }
    }

    private class GetFollowers extends AsyncTask<Void, Void, PagableResponseList<User>>{

        private String id;
        private long cursor;
        private Listener<PagableResponseList<User>> listener;

        public GetFollowers(String id, long cursor, Listener<PagableResponseList<User>> listener) {
            this.id = id;
            this.cursor = cursor;
            this.listener = listener;
        }

        @Override
        protected PagableResponseList<User> doInBackground(Void... voids) {
            try {
                if (id == null)
                    id = sqLite.getMainAccount().screenName;
                return twitter.getFollowersList(id, cursor, 200);
            }catch (TwitterException e){
                Utils.error(e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(PagableResponseList<User> users) {
            super.onPostExecute(users);
            listener.data(users);
        }
    }

    private class UnfollowUser extends AsyncTask<Void, Void, User>{

        private long id;
        private Listener<User> listener;

        public UnfollowUser(long id, Listener<User> listener) {
            this.id = id;
            this.listener = listener;
        }

        @Override
        protected User doInBackground(Void... voids) {
            try {
                return twitter.destroyFriendship(id);
            } catch (TwitterException e) {
                Utils.error(e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(User user) {
            super.onPostExecute(user);
            listener.data(user);
        }
    }

    private class GetFollowing extends AsyncTask<Void, Void, PagableResponseList<User>>{

        private String id;
        private long cursor;
        private Listener<PagableResponseList<User>> listener;

        public GetFollowing(String id, long cursor, Listener<PagableResponseList<User>> listener) {
            this.id = id;
            this.cursor = cursor;
            this.listener = listener;
        }

        @Override
        protected PagableResponseList<User> doInBackground(Void... voids) {
            try {
                if (id == null)
                    id = sqLite.getMainAccount().screenName;
                return twitter.getFriendsList(id, cursor, 200);
            } catch (TwitterException e) {
                Utils.error(e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(PagableResponseList<User> users) {
            super.onPostExecute(users);
            listener.data(users);
        }
    }

    /**
     * Get users in background
     */
    private class LookupUsers extends AsyncTask<Void, Void, ResponseList<User>>{

        private long[] iDs;
        private Listener<ResponseList<User>> listener;

        public LookupUsers(long[] iDs, Listener<ResponseList<User>> listener) {
            this.iDs = iDs;
            this.listener = listener;
        }

        @Override
        protected ResponseList<User> doInBackground(Void... voids) {
            try {
                if (iDs == null || iDs.length <= 0)
                    return null;
                return twitter.lookupUsers(iDs);
            } catch (TwitterException e) {
                Utils.error(e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(ResponseList<User> users) {
            super.onPostExecute(users);
            listener.data(users);
        }
    }

}
