package org.koreait.diary;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.koreait.diary.commons.ApiResults;
import org.koreait.diary.commons.ApiURLs;
import org.koreait.diary.commons.AppMenus;
import org.koreait.diary.member.LoginSession;
import org.koreait.diary.member.Member;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainFragment extends Fragment {
    private RequestQueue requestQueue;
    private MainActivity mainActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_main, container, false);


        mainActivity = (MainActivity) getActivity();

        checkAcess();

        requestQueue = Volley.newRequestQueue(mainActivity);

        RecyclerView recyclerView = viewGroup.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mainActivity, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);


        Member member = LoginSession.getMember();
        String url = ApiURLs.URL + "diaries/" + member.getMemId();

        StringRequest request = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ObjectMapper om = new ObjectMapper();
                        om.registerModule(new JavaTimeModule());
                        try {
                            ApiResults<List<Diary>> results = om.readValue(response, new TypeReference<ApiResults<List<Diary>>>() {
                            });
                            List<Diary> diaries = results.getData();
                            DiaryAdapter adapter = new DiaryAdapter();
                            adapter.setItems(diaries);
                            // 리싸이클러뷰에 어댑터 설정하기
                            recyclerView.setAdapter(adapter);

                        } catch (Exception e) {
                            Toast.makeText(mainActivity, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("DIARY_DATA", error.toString());
                    }
                }
        ) {

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                return params;
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                try {
                    String utf8String = new String(response.data, "UTF-8");
                    return Response.success(utf8String, HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    // log error
                    return Response.error(new ParseError(e));
                } catch (Exception e) {
                    // log error
                    return Response.error(new ParseError(e));
                }
            }
        };

        request.setShouldCache(false);
        requestQueue.add(request);


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