package org.koreait.diary;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import static org.koreait.diary.commons.AppMenus.*;

import org.koreait.diary.commons.AppPermission;
import org.koreait.diary.member.FindUserIdFragment;
import org.koreait.diary.member.FindUserPassFragment;
import org.koreait.diary.member.JoinFragment;
import org.koreait.diary.member.LoginFragment;
import org.koreait.diary.member.LoginSession;

public class MainActivity extends AppCompatActivity {

    private LoginFragment loginFragment;
    private JoinFragment joinFragment;
    private FindUserIdFragment findUserIdFragment;
    private FindUserPassFragment findUserPassFragment;
    private MainFragment mainFragment;
    private WriteDiaryFragment writeDiaryFragment;

    private LinearLayout slideMenu; // 더보기 메뉴
    private FrameLayout slideMenuBg; // 더보기 메뉴 배경 레이어

    private Animation slideOpenAnim;
    private Animation slideCloseAnim;

    private boolean isSlideOpen = false;
    private boolean isSlideMenuBgClicked = false;

    private FrameLayout bottomContatiner;
    private BottomMenuFragment bottomMenuFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_Diary);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //loginFragment = (LoginFragment) getSupportFragmentManager().findFragmentById(R.id.loginFragment);
        loginFragment = new LoginFragment();
        joinFragment = new JoinFragment();
        findUserIdFragment = new FindUserIdFragment();
        findUserPassFragment = new FindUserPassFragment();

        mainFragment = new MainFragment(); // 메인화면(로그인 이후)
        writeDiaryFragment = new WriteDiaryFragment(); // 일기쓰기 화면(로그인 이후)
        
        bottomContatiner = findViewById(R.id.bottom_container);

       checkAcess();

        /** 더보기 메뉴 관련 S */
        slideMenu = findViewById(R.id.slideMenu);
        slideMenuBg = findViewById(R.id.slideMenuBg);

        slideOpenAnim = AnimationUtils.loadAnimation(this, R.anim.slide_open);
        slideCloseAnim = AnimationUtils.loadAnimation(this, R.anim.slide_close);

        slideOpenAnim.setAnimationListener(new SlideMenuAnimationListener());
        slideCloseAnim.setAnimationListener(new SlideMenuAnimationListener());

        slideMenuBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isSlideMenuBgClicked) {
                    isSlideMenuBgClicked = true;
                    slideMenuClose();

                }
            }
        });

        slideMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slideCloseAnim.cancel();
            }
        });

        /** 더보기 메뉴 관련 E */

        AppPermission.check(this);
    }

    public void onFragmentChange(int menu) {
        FragmentManager manager = getSupportFragmentManager();
        switch (menu) {
            case MEMBER_LOGIN: // 로그인
                manager.beginTransaction().replace(R.id.main_container, loginFragment).commit();
                break;
            case MEMBER_FIND_ID:  // 아이디 찾기
                manager.beginTransaction().replace(R.id.main_container, findUserIdFragment).commit();
                break;
            case MEMBER_FIND_PASS: // 비밀번호 찾기
                manager.beginTransaction().replace(R.id.main_container, findUserPassFragment).commit();
                break;
            case MEMBER_JOIN: // 회원 가입
                manager.beginTransaction().replace(R.id.main_container, joinFragment).commit();
                break;
            case APP_MAIN : // 메인 화면
                manager.beginTransaction().replace(R.id.main_container, mainFragment).commit();
                break;
            case WRITE_DIARY: // 일기 쓰기
                manager.beginTransaction().replace(R.id.main_container, writeDiaryFragment).commit();
                break;
        }
    }

    // 더보기 메뉴 열기
    public void slideMenuOpen() {
        if (!isSlideOpen) { // 열리지 않은 상태
            slideMenu.startAnimation(slideOpenAnim);
        }
    }

    // 더보기 메뉴 닫기
    public void slideMenuClose() {

        if (isSlideOpen) { // 열려 있는 경우
            slideMenu.startAnimation(slideCloseAnim);
        }
    }

    private class SlideMenuAnimationListener implements Animation.AnimationListener {
        @Override
        public void onAnimationStart(Animation animation) {
            if (!isSlideOpen) {
                slideMenuBg.setVisibility(View.VISIBLE);
                slideMenu.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onAnimationEnd(Animation animation) {
                if (isSlideOpen) {
                    isSlideOpen = false;

                    slideMenu.setVisibility(View.INVISIBLE);
                    slideMenuBg.setVisibility(View.INVISIBLE);
                    isSlideMenuBgClicked = false;
                } else {
                    isSlideOpen = true;
                }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        checkAcess();
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences pref = this.getSharedPreferences("pref", Activity.MODE_PRIVATE);
        if (pref != null) {
            String memId = pref.getString("memId", null);
            LoginSession.updateMember(memId);
        }

        checkAcess();
    }

    public void updateBottomMenu() {
        bottomMenuFragment = new BottomMenuFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.bottom_container, bottomMenuFragment).commit();
    }

    private void checkAcess() {
        if (LoginSession.isLogin()) { // 로그인 시 -> 메인 화면
            onFragmentChange(APP_MAIN);
        } else { // 미 로그인 시 -> 로그인 화면
            onFragmentChange(MEMBER_LOGIN);
        }

        updateBottomMenu();
    }
}