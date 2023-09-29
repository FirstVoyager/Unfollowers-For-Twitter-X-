package limitless.android.unfollowtita.Adapter;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;

import limitless.android.unfollowtita.Activity.MainActivity;
import limitless.android.unfollowtita.Dialog.UpgradeBottomSheet;
import limitless.android.unfollowtita.Model.UserAction;
import limitless.android.unfollowtita.Other.Listener;
import limitless.android.unfollowtita.Model.Type;
import limitless.android.unfollowtita.Model.UserModel;
import limitless.android.unfollowtita.Other.AdManager;
import limitless.android.unfollowtita.Other.Constant;
import limitless.android.unfollowtita.Other.SQLiteUnfollow;
import limitless.android.unfollowtita.Other.SharePref;
import limitless.android.unfollowtita.Other.Utils;
import limitless.android.unfollowtita.Other.UnfollowTiTa;
import limitless.android.unfollowtita.R;
import twitter4j.TwitterException;
import twitter4j.User;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private Context context;
    private List<UserModel> users;
    private FragmentManager fm;
    private Listener<UserModel> onScrollEndListener;
    private UnfollowTiTa unfollowTiTa;
    private SQLiteUnfollow sqlite;
    private boolean isFollowers, isMuted,
            isBlocked, isFans, isMutualFriend,
            isNotBack, isFollowing, isWhiteList, isBlackList;
    private SharePref sharePref;

    public UserAdapter(Context context, List<UserModel> users, FragmentManager fm, boolean isBlocked, boolean isFollowers, boolean isMuted, boolean isFans, boolean isMutualFriend, boolean isNotBack, boolean isFollowing, boolean isWhiteList, boolean isBlackList) {
        this.context = context;
        this.users = users;
        this.fm = fm;
        this.isFollowers = isFollowers;
        this.unfollowTiTa = new UnfollowTiTa(context);
        this.sqlite = new SQLiteUnfollow(context, new SQLiteUnfollow(context, 0).getMainAccount().id);
        this.isMuted = isMuted;
        this.isBlocked = isBlocked;
        this.isFans = isFans;
        this.isMutualFriend = isMutualFriend;
        this.isNotBack = isNotBack;
        this.isFollowing = isFollowing;
        this.isWhiteList = isWhiteList;
        this.isBlackList = isBlackList;
        this.sharePref = new SharePref(context);
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new UserViewHolder(LayoutInflater.from(context).inflate(
                R.layout.item_user, viewGroup, false
        ));
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder userViewHolder, int i) {
        userViewHolder.bindView(users.get(i));
    }

    @Override
    public int getItemCount() {
        try {
            return users.size();
        }catch (Exception e){
            Utils.error(e);
            return 0;
        }
    }

    public int getUserSize(){
        try {
            return users.size();
        }catch (Exception e){
            return 0;
        }
    }

    public void deleteUser(long id){
        try {
            moveItem(id);
            if (isBlocked){
                sqlite.deleteUser(id, Type.BLOCKED);
            }else if (isFans){
                sqlite.deleteUser(id, Type.FANS);
                sqlite.insertUser(id, Type.FOLLOWING);
            }else if (isMuted){
                sqlite.deleteUser(id, Type.MUTED);
            }else if (isMutualFriend){
                sqlite.deleteUser(id, Type.FOLLOWING);
                sqlite.deleteUser(id, Type.MUTUAL_FRIENDS);
            }else if (isNotBack){
                sqlite.deleteUser(id, Type.FOLLOWING);
                sqlite.deleteUser(id, Type.NOT_FOLLOWING_BACK);
            }else if (isFollowing){
                sqlite.deleteUser(id, Type.FOLLOWING);
            }else if (isWhiteList){
                sqlite.insertUser(id, Type.NOT_FOLLOWING_BACK);
                sqlite.deleteUser(id, Type.WHITE_LIST);
            }else if (isBlackList){
                sqlite.insertUser(id, Type.FANS);
                sqlite.deleteUser(id, Type.BLACK_LIST);
            }
        }catch (Exception e){
            Utils.error(e);
        }
    }

    private void moveItem(long id) {
        int n = 0;
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).user.getId() == id){
                n = i;
                break;
            }
        }
        users.remove(n);
        notifyItemRemoved(n);
    }

    public void insertUsers(@Nullable List<UserModel> users){
        if (users == null || users.size() <= 0)
            return;
        if (this.users == null)
            this.users = new ArrayList<>();
        this.users.addAll(users);
        notifyDataSetChanged();
    }

    public void setOnEndScrollListener(Listener<UserModel> onEndScrollListener){
        this.onScrollEndListener = onEndScrollListener;
    }

    public void clearAll() {
        if (users != null){
            users.clear();
            notifyDataSetChanged();
        }
    }


    class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView civAvatar;
        private MaterialTextView tvName, tvScreenName;
        private MaterialButton btnAction;
        private ProgressBar progressBar;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            civAvatar = itemView.findViewById(R.id.imageView_avatar);
            tvName = itemView.findViewById(R.id.textView_name);
            tvScreenName = itemView.findViewById(R.id.text_view_screen);
            btnAction = itemView.findViewById(R.id.imageButton_action);
            progressBar = itemView.findViewById(R.id.progressBar);
            itemView.findViewById(R.id.imageButton_more).setOnClickListener(this);
            itemView.setOnClickListener(this);
            btnAction.setOnClickListener(this);
            if (isFollowers){
                btnAction.setVisibility(View.GONE);
            }else if (isMuted){
                btnAction.setText(context.getString(R.string.t_unmute));
            }else if (isBlocked){
                btnAction.setText(context.getString(R.string.t_unblock));
            }else if (isFans){
                btnAction.setText(context.getString(R.string.t_follow));
            }else if (isNotBack || isFollowing || isMutualFriend){
                btnAction.setText(context.getString(R.string.t_unfollow));
            }else if (isWhiteList || isBlackList){
                btnAction.setText(context.getString(R.string.remove));
            }
        }

        void bindView(UserModel userModel){
            Utils.loadPhoto(context, userModel.user.getBiggerProfileImageURL(), civAvatar, true);
            tvName.setText(userModel.user.getName());
            tvScreenName.setText(userModel.user.getScreenName());
            progressBar.setVisibility(View.INVISIBLE);

            if (userModel.isProgressing){
                btnAction.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
            }else {
                btnAction.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
            }
            if (isFollowers){
                btnAction.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
            }
            if (getAdapterPosition() == users.size() - 1){
                if (onScrollEndListener != null){
                    onScrollEndListener.data(users.get(getAdapterPosition()));
                }
            }
        }

        @Override
        public void onClick(View v) {
            try {
                if (v.getId() == R.id.imageButton_action){
                    int i = getAdapterPosition();
                    users.get(i).isProgressing = true;
                    actionButtonClicked(progressBar, btnAction, getCurrentUser(i));
                }else if (v.getId() == R.id.cardView_user){
                    openUserInfoDialog(getCurrentUser(getAdapterPosition()));
                }else if (v.getId() == R.id.imageButton_more){
                    moreButton(v, getAdapterPosition());
                }
            }catch (Exception e){
                Utils.error(e);
            }
        }
    }

    private void moreButton(View v, int position) {
        PopupMenu popupMenu = new PopupMenu(context, v);
        popupMenu.inflate(R.menu.menu_item_user);
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()){
                case R.id.user_blackList:
                    sqlite.deleteUser(users.get(position).user.getId(), Type.FANS);
                    if (sqlite.insertUser(users.get(position).user.getId(), Type.BLACK_LIST)){
                        Utils.toast(context, context.getString(R.string.added_to_, context.getString(R.string.black_list)));
                    }else {
                        Utils.toast(context, "Exist in black list");
                    }
                    moveItem(users.get(position).user.getId());
                    break;
                case R.id.user_whiteList:
                    sqlite.deleteUser(users.get(position).user.getId(), Type.NOT_FOLLOWING_BACK);
                    if (sqlite.insertUser(users.get(position).user.getId(), Type.WHITE_LIST)){
                        Utils.toast(context, context.getString(R.string.added_to_, context.getString(R.string.white_list)));
                    }else {
                        Utils.toast(context, R.string.exist_in_black_list);
                    }
                    moveItem(users.get(position).user.getId());
                    break;
                case R.id.user_block:
                    blockUser(position);
                    break;
                case R.id.user_mute:
                    muteUser(position);
                    break;
                case R.id.user_share:
                    Utils.shareText(context, Utils.profileUrl(users.get(position).user.getScreenName()));
                    break;
                case R.id.user_seeOnTwitter:
                    Utils.openUrl(context, Utils.profileUrl(users.get(position).user.getScreenName()));
                    break;
            }
            return false;
        });
        if (isMuted){
            popupMenu.getMenu().removeItem(R.id.user_mute);
            popupMenu.getMenu().removeItem(R.id.user_whiteList);
            popupMenu.getMenu().removeItem(R.id.user_blackList);
        }else if (isBlocked){
            popupMenu.getMenu().removeItem(R.id.user_block);
            popupMenu.getMenu().removeItem(R.id.user_whiteList);
            popupMenu.getMenu().removeItem(R.id.user_blackList);
        }else if (isWhiteList || isBlackList){
            popupMenu.getMenu().removeItem(R.id.user_whiteList);
            popupMenu.getMenu().removeItem(R.id.user_whiteList);
            popupMenu.getMenu().removeItem(R.id.user_blackList);
        }else if (isMutualFriend){
            popupMenu.getMenu().removeItem(R.id.user_whiteList);
            popupMenu.getMenu().removeItem(R.id.user_blackList);
        }else if (isFans){
            popupMenu.getMenu().removeItem(R.id.user_whiteList);
        }else if (isNotBack){
            popupMenu.getMenu().removeItem(R.id.user_blackList);
        }else if (isFollowing || isFollowers){
            popupMenu.getMenu().removeItem(R.id.user_whiteList);
            popupMenu.getMenu().removeItem(R.id.user_blackList);
        }
        popupMenu.show();
    }

    private void blockUser(int position) {
        unfollowTiTa.blockUser(users.get(position).user.getId(),
                user -> {
                    if (user != null){
                        Utils.toast(context, user.getName() + "  blocked");
                        sqlite.insertUser(user.getId(), Type.BLOCKED);
                    }
                });
    }

    private void muteUser(int position) {
        unfollowTiTa.muteUser(
                users.get(position).user.getId(),
                user -> {
                    if (user != null){
                        if (sqlite.insertUser(user.getId(), Type.MUTED)){
                            Utils.toast(context, R.string.add_to_muted);
                        }else {
                            Utils.toast(context, R.string.cant_add_to_muted);
                        }
                    }
                });
    }

    private twitter4j.User getCurrentUser(int i) {
        try {
            return users.get(i).user;
        }catch (Exception e){
            Utils.error(e);
            return null;
        }
    }

    private void openUserInfoDialog(twitter4j.User user) {
        if (user == null)
            return;
        Utils.showUserDialog(user.getId(), fm);
    }

    private void actionButtonClicked(final ProgressBar progressBar, final AppCompatButton ibtnAction, twitter4j.User user) {
        if (user == null)
            return;
        progressBar.setVisibility(View.VISIBLE);
        ibtnAction.setVisibility(View.INVISIBLE);
        Listener<User> listener = user1 -> {
            if (user1 != null){
                deleteUser(user1.getId());
            }else {
                progressBar.setVisibility(View.INVISIBLE);
                ibtnAction.setVisibility(View.VISIBLE);
                for (int i = 0; i < users.size(); i++) {
                    if (users.get(i).user.getId() == user1.getId()){
                        users.get(i).isProgressing = false;
                        notifyItemChanged(i);
                    }
                }
            }
        };
        if (isMuted){
            unfollowTiTa.unMuteUser(user.getId(), listener);
            sendBroadCast(UserAction.MUTE, user.getId());
        }else if (isBlocked){
            unfollowTiTa.unBlockUser(user.getId(), listener);
            sendBroadCast(UserAction.BLOCK, user.getId());
        }else if (isFans) {
            if (sharePref.get(SharePref.PRO_VERSION, false)) {
                unfollowTiTa.followUser(user.getId(), listener);
            }else {
                new UpgradeBottomSheet().show(fm, null);
            }
            sendBroadCast(UserAction.FOLLOW, user.getId());
        } else if (isBlackList) {
            deleteUser(user.getId());
            progressBar.setVisibility(View.INVISIBLE);
            ibtnAction.setVisibility(View.VISIBLE);
        }else if (isWhiteList){
            deleteUser(user.getId());
            progressBar.setVisibility(View.INVISIBLE);
            ibtnAction.setVisibility(View.VISIBLE);
        } else if (isMutualFriend || isFollowing || isNotBack) {
            sharePref.put(SharePref.unfollowCount, sharePref.getInt(SharePref.unfollowCount, Constant.unfollowCountForShowAd) - 1);
            if (sharePref.getInt(SharePref.unfollowCount, Constant.unfollowCountForShowAd) <= 0){
                new AdManager(context).loadAndShowInterstitial();
                sharePref.put(SharePref.unfollowCount, Constant.unfollowCountForShowAd);
            }
            unfollowTiTa.unfollowUser(user.getId(), listener);
            sendBroadCast(UserAction.UNFOLLOW, user.getId());
        }
    }

    /**
     * Send broadcast to change data
     * @param action Action type
     * @param userID user id
     */
    private void sendBroadCast(UserAction action, long userID) {
        Intent intent = new Intent(MainActivity.CHANGE_DATA_ACTION);
        intent.putExtra(Intent.EXTRA_TEXT, action.name());
        intent.putExtra(SharePref.userId, userID);
        context.sendBroadcast(intent);
    }

}
