package org.koreait.diary;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import org.koreait.diary.commons.AppMenus;
import org.koreait.diary.member.LoginSession;

public class BottomMenuFragment extends Fragment {

    private Button homeBtn;
    private Button moreBtn;
    private MainActivity mainActivity;

    private FrameLayout memberOnlyButtons;
    private MemberButtons memberButtons;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup viewGroup = (ViewGroup)inflater.inflate(R.layout.fragment_bottom_menu, container, false);

        homeBtn = viewGroup.findViewById(R.id.home_btn);
        moreBtn = viewGroup.findViewById(R.id.more_btn);

        mainActivity = (MainActivity) getActivity();

        memberOnlyButtons = viewGroup.findViewById(R.id.memberOnlyButtons);
        memberButtons = new MemberButtons(mainActivity);

        // 메인 화면 이동, 로그인이 안되어 있으면? 로그인 화면
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.onFragmentChange(AppMenus.APP_MAIN);
            }
        });

        // 더보기 메뉴 열기
        moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.slideMenuOpen();
            }
        });

        addMemberOnlyButtons();

        return viewGroup;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void addMemberOnlyButtons() {
        if (LoginSession.isLogin()) {
           memberOnlyButtons.setVisibility(View.VISIBLE);
        } else {
            memberOnlyButtons.setVisibility(View.GONE);
        }
    }
}