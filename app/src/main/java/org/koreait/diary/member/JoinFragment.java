package org.koreait.diary.member;

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

import org.koreait.diary.MainActivity;
import org.koreait.diary.R;
import org.koreait.diary.commons.ApiResults;
import org.koreait.diary.commons.ApiURLs;
import org.koreait.diary.commons.AppMenus;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * 회원 가입
 *
 */
public class JoinFragment extends Fragment {
    private EditText joinMemId;
    private EditText joinMemPw;
    private EditText joinMemPwRe;
    private EditText joinMemNm;
    private EditText joinEmail;
    private EditText joinMobile;
    private Button joinProcessBtn;

    private MainActivity mainActivity;
    private RequestQueue requestQueue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_join, container, false);

        joinMemId = viewGroup.findViewById(R.id.joinMemId);
        joinMemPw = viewGroup.findViewById(R.id.joinMemPw);
        joinMemPwRe = viewGroup.findViewById(R.id.joinMemPwRe);
        joinMemNm = viewGroup.findViewById(R.id.joinMemNm);
        joinEmail = viewGroup.findViewById(R.id.joinEmail);
        joinMobile = viewGroup.findViewById(R.id.joinMobile);
        joinProcessBtn = viewGroup.findViewById(R.id.joinProcessBtn);

        mainActivity = (MainActivity) getActivity();
        requestQueue = Volley.newRequestQueue(mainActivity);

        joinProcessBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processJoin();
            }
        });

        return viewGroup;
    }

    private void processJoin() {
        String url = ApiURLs.URL + "member/join";
        StringRequest request = new StringRequest(Request.Method.POST, url,
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

                            Toast.makeText(mainActivity, "가입하셨습니다.", Toast.LENGTH_LONG).show();
                            mainActivity.onFragmentChange(AppMenus.MEMBER_LOGIN);
                        } catch (Exception e) {
                            Toast.makeText(mainActivity, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("JOIN_DATA", error.toString());
                    }
                }) {

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("memId", joinMemId.getText().toString());
                params.put("memPw", joinMemPw.getText().toString());
                params.put("memPwRe", joinMemPwRe.getText().toString());
                params.put("memNm", joinMemNm.getText().toString());
                params.put("email", joinEmail.getText().toString());
                params.put("mobile", joinMobile.getText().toString());

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