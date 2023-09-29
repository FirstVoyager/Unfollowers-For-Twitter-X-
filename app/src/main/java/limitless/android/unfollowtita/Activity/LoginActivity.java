package limitless.android.unfollowtita.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import limitless.android.unfollowtita.Dialog.AccountsBottomSheet;
import limitless.android.unfollowtita.Model.Account;
import limitless.android.unfollowtita.Other.Constant;
import limitless.android.unfollowtita.Other.Utils;
import limitless.android.unfollowtita.R;
import limitless.android.unfollowtita.databinding.ActivityLoginBinding;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;

public class LoginActivity extends BaseActivity {

    private ActivityLoginBinding binding;
    private Twitter twitter;
    private RequestToken requestToken;
    private Account account;
    private WebChromeClient webChromeClient = new WebChromeClient(){
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            binding.progressBar.setProgress(newProgress);
            if (newProgress >= 100)
                binding.progressBar.setVisibility(View.GONE);
            else
                binding.progressBar.setVisibility(View.VISIBLE);
        }
    };
    private WebViewClient webViewClient = new WebViewClient(){
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.contains(Constant.callBackUri)){
                String s = Uri.parse(url).getQueryParameter(Constant.oauthVerifier);
                if (s != null){
                    saveResulat(s);
                    return false;
                }else
                    setCancel();
            }
            return super.shouldOverrideUrlLoading(view, url);
        }
    };

    @Override
    public boolean showAds() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void init() {
        Utils.clearCookies();
        setTitle(R.string.login);
        binding.webView.getSettings().setJavaScriptEnabled(true);
        binding.webView.setWebChromeClient(webChromeClient);
        binding.webView.setWebViewClient(webViewClient);
        setUpLogin();
    }

    private void setUpLogin() {
        account = new Account();
        new Login().execute();
    }


    private class Login extends AsyncTask<Void, Void, RequestToken>{

        private TwitterException exception;

        @Override
        protected RequestToken doInBackground(Void... voids) {
            ConfigurationBuilder builder = new ConfigurationBuilder();
            builder.setOAuthConsumerKey(getString(R.string.api_key_2));
            builder.setOAuthConsumerSecret(getString(R.string.api_secret_2));
            TwitterFactory twitterFactory = new TwitterFactory(builder.build());
            twitter = twitterFactory.getInstance();
            try {
                requestToken = twitter.getOAuthRequestToken();
                return requestToken;
            } catch (TwitterException e) {
                exception = e;
                return null;
            }
        }

        @Override
        protected void onPostExecute(RequestToken requestToken) {
            if (requestToken == null){
                Toast.makeText(LoginActivity.this, exception.getErrorMessage(), Toast.LENGTH_LONG).show();
            }else {
                binding.webView.loadUrl(requestToken.getAuthenticationURL());
            }
            super.onPostExecute(requestToken);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(R.string.refresh);
        menu.add(R.string.exit);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getTitle() != null && item.getTitle().equals(getString(R.string.refresh))){
            setUpLogin();
        }else if (item.getTitle() != null && item.getTitle().equals(getString(R.string.exit))){
            setCancel();
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveResulat(String s) {
        new SaveLogin(s).execute();
    }

    private class SaveLogin extends AsyncTask<Void, Void, Account>{

        private TwitterException exception;
        private String oauth;

        public SaveLogin(String oauth) {
            this.oauth = oauth;
        }

        @Override
        protected Account doInBackground(Void... voids) {
            try {
                AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, oauth);
                account.accessToken = accessToken.getToken();
                account.accessSecret = accessToken.getTokenSecret();
                account.screenName = accessToken.getScreenName();
                account.id = accessToken.getUserId();
                account.consumerToken = getString(R.string.api_key_2);
                account.consumerSecret = getString(R.string.api_secret_2);
                account.isMain = true;
                User user = twitter.showUser(accessToken.getUserId());
                if (user != null){
                    account.name = user.getName();
                    account.profileUrl = user.getBiggerProfileImageURL();
                    account.headerUrl = user.getProfileBannerURL();
                    account.bio = user.getDescription();
                }
                account.followingLoaded = false;
                account.followersLoaded = false;
                account.blockedLoaded = false;
                account.mutedLoaded = false;
                sqLiteUnfollow.insertAccount(account);
                sqLiteUnfollow.setMainAccount(account.id);
                return account;
            } catch (TwitterException e) {
                exception = e;
                return null;
            }
        }

        @Override
        protected void onPostExecute(Account aBoolean) {
            if (account != null){
                Intent intent = new Intent();
                intent.putExtra(AccountsBottomSheet.LOGIN_ACCOUNT, account);
                setResult(RESULT_OK, intent);
                finish();
            }else {
                Utils.toast(LoginActivity.this, exception.getErrorMessage());
                setCancel();
            }
            super.onPostExecute(aBoolean);
        }
    }

    private void setCancel() {
        Utils.toast(this, R.string.error);
        setResult(RESULT_CANCELED);
        finish();
    }

}
