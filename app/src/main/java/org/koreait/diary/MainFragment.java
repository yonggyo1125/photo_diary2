package org.koreait.diary;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import org.koreait.diary.commons.AppMenus;
import org.koreait.diary.member.LoginSession;

public class MainFragment extends Fragment {

    private MainActivity mainActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_main, container, false);

        mainActivity = (MainActivity) getActivity();

        checkAcess();

        RecyclerView recyclerView = viewGroup.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mainActivity, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        DiaryAdapter adapter = new DiaryAdapter();


        // 리싸이클러뷰에 어댑터 설정하기
        recyclerView.setAdapter(adapter);
        return viewGroup;
    }

    @Override
    public void onResume() {
        super.onResume();

        checkAcess();
    }

    private void checkAcess() {
        mainActivity.updateBottomMenu();

        if (!LoginSession.isLogin()) { // 로그인이 안되어 있는 경우는 로그인 페이지로 이동

            mainActivity.onFragmentChange(AppMenus.MEMBER_LOGIN);

        }
    }
}