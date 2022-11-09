package org.koreait.diary;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import org.koreait.diary.commons.AppMenus;
import org.koreait.diary.member.LoginSession;

public class MemberButtons extends LinearLayout {

    private Button logoutBtn;
    private Button writeDiary;
    private MainActivity mainActivity;

    public MemberButtons(Context context) {
        super(context);
        init(context);
    }

    public MemberButtons(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup viewGroup = (ViewGroup)inflater.inflate(R.layout.member_buttons, this, true);

        logoutBtn = viewGroup.findViewById(R.id.logoutBtn);
        writeDiary = viewGroup.findViewById(R.id.writeDiary);

        mainActivity = (MainActivity) context;

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 * 1. LoginSession - Member member -> null
                 * 2. SharedPreferences -> memId -> clear()
                 * 3. 회원전용버튼 감추기
                 * 4. 로그인 페이지 이동
                 *
                  */

                LoginSession.setMember(null);
                SharedPreferences pref = mainActivity.getSharedPreferences("pref", MainActivity.MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.clear();
                editor.commit();

                mainActivity.updateBottomMenu(); // 회원 전용 버튼 감추기

                mainActivity.onFragmentChange(AppMenus.MEMBER_LOGIN);
            }
        });


        writeDiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mainActivity.onFragmentChange(AppMenus.WRITE_DIARY);
            }
        });
    }
}
