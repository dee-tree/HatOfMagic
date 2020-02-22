package com.codemitry.hatofmagic;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.DisplayCutout;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    final static String ENABLED_AUDIO_PREF = "Audio";
    final static String LANGUAGE_PREF = "Language";
    MediaPlayer mediaPlayer;
    Locale myLocale;
    LinearLayout creatorsLayout;
    ConstraintLayout menuLayout;
    TextView svetlana, vladislav, dmitry;

    private int cutOutheight = 0;

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        // Для устройств с челкой
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            DisplayCutout displayCutout = getWindow().getDecorView().getRootWindowInsets().getDisplayCutout();
            if (displayCutout != null) {
                cutOutheight = Math.abs(displayCutout.getSafeInsetRight() - displayCutout.getSafeInsetLeft());
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        creatorsLayout = findViewById(R.id.creatorsLayout);
        menuLayout = findViewById(R.id.menuLayout);

        svetlana = findViewById(R.id.svetlana);
        vladislav = findViewById(R.id.vladislav);
        dmitry = findViewById(R.id.dmitriy);


        mediaPlayer = MediaPlayer.create(this, R.raw.background_menu_sound);
        if (isEnabledAudio(this))
            mediaPlayer.setVolume(0, 1);
        else
            mediaPlayer.setVolume(0, 0);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mediaPlayer.start();
            }
        });

        svetlana.setText(Html.fromHtml("<a href=" + getString(R.string.svetlana_link) + ">" + getString(R.string.svetlana) + "</a>"));
        svetlana.setMovementMethod(LinkMovementMethod.getInstance());
        svetlana.setLinkTextColor(ContextCompat.getColor(this, R.color.menu_text));

        vladislav.setText(Html.fromHtml("<a href=" + getString(R.string.v31r_link) + ">" + getString(R.string.v31r) + "</a>"));
        vladislav.setMovementMethod(LinkMovementMethod.getInstance());
        vladislav.setLinkTextColor(ContextCompat.getColor(this, R.color.menu_text));

        dmitry.setText(Html.fromHtml("<a href=" + getString(R.string.codemitry_link) + ">" + getString(R.string.codemitry) + "</a>"));
        dmitry.setMovementMethod(LinkMovementMethod.getInstance());
        dmitry.setLinkTextColor(ContextCompat.getColor(this, R.color.menu_text));

//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN );

        changeLang(getLanguage(this));
        updateTexts();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer.isPlaying())
            mediaPlayer.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(R.anim.to_top, android.R.anim.fade_out);
        mediaPlayer.start();
//        if (isEnabledAudio(this)) {
//            mediaPlayer.setVolume(0, 1);
//        } else
//            mediaPlayer.setVolume(0, 0);
        updateAudioButton();
        updateLanguageButton();
    }

    private void updateAudioButton() {
        if (isEnabledAudio(this)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ((ImageButton) findViewById(R.id.audioButton)).setImageDrawable(getDrawable(android.R.drawable.ic_lock_silent_mode_off));
//                findViewById(R.id.audioButton).setBackground(getDrawable(android.R.drawable.ic_lock_silent_mode_off));
            } else
                ((ImageButton) findViewById(R.id.audioButton)).setImageDrawable(ContextCompat.getDrawable(this, android.R.drawable.ic_lock_silent_mode_off));
//                findViewById(R.id.audioButton).setBackground(ContextCompat.getDrawable(this, android.R.drawable.ic_lock_silent_mode_off));
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                ((ImageButton) findViewById(R.id.audioButton)).setImageDrawable(getDrawable(android.R.drawable.ic_lock_silent_mode));
//                findViewById(R.id.audioButton).setBackground(getDrawable(android.R.drawable.ic_lock_silent_mode));
            else
                ((ImageButton) findViewById(R.id.audioButton)).setImageDrawable(ContextCompat.getDrawable(this, android.R.drawable.ic_lock_silent_mode));
//            findViewById(R.id.audioButton).setBackground(ContextCompat.getDrawable(this, android.R.drawable.ic_lock_silent_mode_off));
        }
    }

    private void updateLanguageButton() {
        String currLang = getLanguage(this);
        if (currLang.equals("en")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ((ImageButton) findViewById(R.id.languageButton)).setImageDrawable(getDrawable(R.drawable.united_states));
            } else
                ((ImageButton) findViewById(R.id.languageButton)).setImageDrawable(ContextCompat.getDrawable(this, R.drawable.united_states));
        } else if (currLang.equals("ru")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ((ImageButton) findViewById(R.id.languageButton)).setImageDrawable(getDrawable(R.drawable.russia));
            } else
                ((ImageButton) findViewById(R.id.languageButton)).setImageDrawable(ContextCompat.getDrawable(this, R.drawable.russia));
        }
    }

    void updateTexts() {
        ((Button) findViewById(R.id.start_button)).setText(getString(R.string.start));
        ((Button) findViewById(R.id.exit_button)).setText(getString(R.string.exit));

        ((TextView) findViewById(R.id.creator_text)).setText(getString(R.string.creators));
        svetlana.setText(Html.fromHtml("<a href=" + getString(R.string.svetlana_link) + ">" + getString(R.string.svetlana) + "</a>"));
        vladislav.setText(Html.fromHtml("<a href=" + getString(R.string.v31r_link) + ">" + getString(R.string.v31r) + "</a>"));
        dmitry.setText(Html.fromHtml("<a href=" + getString(R.string.codemitry_link) + ">" + getString(R.string.codemitry) + "</a>"));
    }


    public void onLanguageButtonClick(View v) {
        String currLang = getLanguage(this);
        if (currLang.equals("en")) {
            changeLang("ru");
        } else if (currLang.equals("ru")) {
            changeLang("en");
        } else {
            changeLang("en");
        }
        updateLanguageButton();
        updateTexts();
    }

    public void onAudioButtonClick(View v) {
        if (isEnabledAudio(this)) {
            saveEnabledAudio(this, false);
            mediaPlayer.setVolume(0, 0);
        } else {
            saveEnabledAudio(this, true);
            mediaPlayer.setVolume(0, 1);
        }
        updateAudioButton();
    }

    public void onStartClick(View v) {
        mediaPlayer.pause();
        mediaPlayer.seekTo(0);
        Intent gameIntent = new Intent(this, GameActivity.class);
        gameIntent.putExtra("cutout", cutOutheight);
        startActivity(gameIntent);
        overridePendingTransition(android.R.anim.fade_in, R.anim.to_bottom);
    }


    public void onExitClick(View v) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAndRemoveTask();
        } else finish();

    }

    static boolean isEnabledAudio(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        return prefs.getBoolean(ENABLED_AUDIO_PREF, true);
    }

    static void saveEnabledAudio(Context context, boolean enabled) {
        SharedPreferences prefs = context.getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(ENABLED_AUDIO_PREF, enabled);
        editor.apply();
    }


    static String getLanguage(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        return prefs.getString(LANGUAGE_PREF, "en");
    }

    static void saveLanguage(Context context, String lang) {
        SharedPreferences prefs = context.getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(LANGUAGE_PREF, lang);
        editor.apply();
    }

    public void changeLang(String lang) {
        if (lang.equalsIgnoreCase("")) return;
        myLocale = new Locale(lang);
        saveLanguage(this, lang);
        Locale.setDefault(myLocale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = myLocale;
        getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

    }

    @Override
    public void onBackPressed() {
        if (creatorsLayout.getVisibility() == View.VISIBLE) {
            menuLayout.setEnabled(true);
            menuLayout.setVisibility(View.VISIBLE);
            hideCreators();
        } else {
            super.onBackPressed();
        }
    }

    void showCreators() {
        findViewById(R.id.creators_button).setEnabled(false);
        creatorsLayout.setAlpha(0);
        creatorsLayout.setY(-1000);
        creatorsLayout.setVisibility(View.VISIBLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AnimatedVectorDrawable drawable = (AnimatedVectorDrawable) this.getDrawable(R.drawable.show_creators_button);
            ((ImageButton) findViewById(R.id.creators_button)).setImageDrawable(drawable);
            drawable.start();
        }
        creatorsLayout.animate().translationY(0).alpha(1).setDuration(500).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                findViewById(R.id.creators_button).setEnabled(true);
                menuLayout.setEnabled(false);
                menuLayout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    void hideCreators() {
        findViewById(R.id.creators_button).setEnabled(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AnimatedVectorDrawable drawable = (AnimatedVectorDrawable) this.getDrawable(R.drawable.hide_creators_button);
            ((ImageButton) findViewById(R.id.creators_button)).setImageDrawable(drawable);
            drawable.start();
        }
        creatorsLayout.animate().translationY(-1000).alpha(0).setDuration(500).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                creatorsLayout.setVisibility(View.GONE);
                findViewById(R.id.creators_button).setEnabled(true);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    public void onCreatorsButtonClick(View v) {
        if (creatorsLayout.getVisibility() == View.GONE) {
            showCreators();
        } else {
            menuLayout.setEnabled(true);
            menuLayout.setVisibility(View.VISIBLE);
            hideCreators();
        }
    }

}
