package org.koreait.diary.member;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

import org.json.JSONObject;
import org.koreait.diary.MainActivity;
import org.koreait.diary.R;
import org.koreait.diary.commons.ApiResults;
import org.koreait.diary.commons.ApiURLs;
import org.koreait.diary.commons.AppMenus;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class LoginFragment extends Fragment {

    private Button findUserId;
    private Button findUserPass;
    private Button joinUser;

    private EditText loginUserId;
    private EditText loginUserPass;
    private Button loginBtn;

    private Resources res;

    private MainActivity mainActivity;

    private RequestQueue requestQueue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_login, container, false);

        findUserId = viewGroup.findViewById(R.id.findUserId);
        findUserPass = viewGroup.findViewById(R.id.findUserPass);
        joinUser = viewGroup.findViewById(R.id.joinUser);

        mainActivity = (MainActivity) getActivity();

        requestQueue = Volley.newRequestQueue(mainActivity);

        /** 로그인 처리 S */
        loginUserId = viewGroup.findViewById(R.id.loginUserId);
        loginUserPass = viewGroup.findViewById(R.id.loginUserPass);
        loginBtn = viewGroup.findViewById(R.id.loginBtn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    // 필수 데이터 - 아이디, 비밀번호
                    String userId = loginUserId.getText().toString().trim();
                    String userPass = loginUserPass.getText().toString().trim();
                    res = getResources();
                    if (userId.isEmpty()) {
                        throw new RuntimeException(res.getString(R.string.requiredUserId));
                    }

                    if (userPass.isEmpty()) {
                        throw new RuntimeException(res.getString(R.string.requiredUserPass));
                    }


                    processLogin(userId, userPass);


                } catch (RuntimeException e) {
                    Toast.makeText(mainActivity, e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        });

        /** 로그인 처리 E */

        // 아이디 찾기 페이지 이동
        findUserId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.onFragmentChange(AppMenus.MEMBER_FIND_ID);
            }
        });

        // 비밀번호 찾기 페이지 이동
        findUserPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.onFragmentChange(AppMenus.MEMBER_FIND_PASS);
            }
        });

        // 회원 가입 페이지 이동
        joinUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.onFragmentChange(AppMenus.MEMBER_JOIN);
            }
        });

        return viewGroup;
    }

    /**
     * 로그인 처리
     *
     * @param userId
     * @param userPass
     */
    private void processLogin(String userId, String userPass) {

        // 192.168.2.101:3000
        String url = ApiURLs.URL + "member/login";

        StringRequest request = new StringRequest(
                    Request.Method.POST,
                    url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            ObjectMapper om = new ObjectMapper();
                            om.registerModule(new JavaTimeModule());
                            try {
                                ApiResults<Member> results = om.readValue(response, new TypeReference<ApiResults<Member>>() {
                                });
                                if (!results.isSuccess()) {
                                    throw new RuntimeException(results.getMessage());
                                }

                                Member member = results.getData();

                                LoginSession.setMember(member);

                                SharedPreferences pref = mainActivity.getSharedPreferences("pref", Activity.MODE_PRIVATE);
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putString("memId", userId);
                                editor.commit();

                                loginUserId.setText("");
                                loginUserPass.setText("");

                                mainActivity.onFragmentChange(AppMenus.APP_MAIN);

                            } catch (Exception e) {
                                Toast.makeText(mainActivity, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("LOGIN_DATA", error.toString());
                        }
                    }
                ) {

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("memId", userId);
                params.put("memPw", userPass);
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
    }
}