package com.gjscut.waterplantswatcher;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.gjscut.waterplantswatcher.model.Token;

import java.util.Date;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * A login screen that offers login via username/password.
 */
public class LoginActivity extends AppCompatActivity {
    // UI references.
    private EditText mUsernameView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    SharedPreferences.Editor editor;
    SharedPreferences read;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        editor = getSharedPreferences("water_plants", Context.MODE_PRIVATE).edit();
        read = getSharedPreferences("water_plants", Context.MODE_PRIVATE);
        long date = read.getLong("loginTime", 0L);
        String access_token = read.getString("accessToken", "");
        String token_type = read.getString("tokenType", "");
        String refresh_token = read.getString("refreshToken", "");
        int expires_in = read.getInt("expiresIn", 0);
        Token token = new Token(token_type, access_token, refresh_token, expires_in);
        Constant.putToken(token);
        if (!token.access_token.isEmpty() && new Date().getTime() - date < 1000 * token.expires_in * 24) {
            Intent intent = new Intent(LoginActivity.this, ProcessesActivity.class);
            startActivity(intent);
            finish();
        }
        setContentView(R.layout.activity_login);
        this.setTitle("Login");
        // Set up the login form.
        mUsernameView = (EditText) findViewById(R.id.username);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mUsernameSignInButton = (Button) findViewById(R.id.username_sign_in_button);
        mUsernameSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private void attemptLogin() {
        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String username = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid username address.
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        } else if (!isUsernameValid(username)) {
            mUsernameView.setError(getString(R.string.error_invalid_username));
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            login(username, password);
        }
    }

    private boolean isUsernameValid(String username) {
        return username.length() >= 4;
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 4;
    }

    private void login(final String username, final String password) {
        showProgress(true);
        NetHelper.api(this).login(username, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onNext(String value) {
                        Constant.username = username;
                        accessToken(username, password);
                    }

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        showProgress(false);
                        mPasswordView.setError(getString(R.string.error_incorrect_password));
                        mPasswordView.requestFocus();
                    }
                });
    }

    private void accessToken(final String username, final String password) {
        NetHelper.api(this).accessTokenByPassword("password", Constant.clientId, Constant.clientSecret, username, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Token>() {

                    @Override
                    public void onNext(Token value) {
                        Constant.token = value;
                        editor.putLong("loginTime", new Date().getTime());
                        editor.putString("accessToken", Constant.token.access_token);
                        editor.putString("tokenType", Constant.token.token_type);
                        editor.putString("refreshToken", Constant.token.refresh_token);
                        editor.putInt("expiresIn", Constant.token.expires_in);
                        editor.apply();
                    }

                    @Override
                    public void onError(Throwable e) {
                        showProgress(false);
                        mPasswordView.setError(getString(R.string.error_incorrect_password));
                        mPasswordView.requestFocus();
                    }

                    @Override
                    public void onCompleted() {
                        Intent intent = new Intent(LoginActivity.this, ProcessesActivity.class);
                        startActivity(intent);
                        showProgress(false);
                        finish();
                    }
                });
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

}

