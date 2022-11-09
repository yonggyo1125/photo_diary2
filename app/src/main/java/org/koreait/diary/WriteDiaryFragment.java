package org.koreait.diary;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;


public class WriteDiaryFragment extends Fragment {
    private EditText diaryTitle;
    private FrameLayout previewFrame;
    private EditText diaryContent;
    private Button writeDiaryBtn;
    private CameraSurfaceView cameraView;

    private MainActivity mainActivity;
    private RequestQueue requestQueue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_write_diary, container, false);
        mainActivity = (MainActivity) getActivity();
        diaryTitle = viewGroup.findViewById(R.id.diaryTitle);
        diaryContent = viewGroup.findViewById(R.id.diaryContent);
        writeDiaryBtn = viewGroup.findViewById(R.id.writeDiaryBtn);
        previewFrame = viewGroup.findViewById(R.id.previewFrame);
        cameraView = new CameraSurfaceView(mainActivity);
        previewFrame.addView(cameraView);


        requestQueue = Volley.newRequestQueue(mainActivity);

        writeDiaryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture();
            }
        });

        return viewGroup;
    }

    private void processWrite(String imageData) {
        String url = ApiURLs.URL + "diary/write";
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ObjectMapper om = new ObjectMapper();
                        om.registerModule(new JavaTimeModule());
                        try {
                            ApiResults<Diary> results = om.readValue(response, new TypeReference<ApiResults<Diary>>() {
                            });
                            if (!results.isSuccess()) {
                                throw new RuntimeException(results.getMessage());
                            }
                            diaryTitle.setText("");
                            diaryContent.setText("");
                            if (previewFrame != null) {
                                previewFrame.removeAllViews();
                            }
                            Log.d("WRITE_DATA", results.toString());
                            Toast.makeText(mainActivity, "작성하였습니다..", Toast.LENGTH_LONG).show();
                            mainActivity.onFragmentChange(AppMenus.APP_MAIN);
                        } catch (Exception e) {
                            Toast.makeText(mainActivity, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("WRITE_DATA", error.toString());
                    }
                }) {

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                Member member = LoginSession.getMember();
                params.put("title", diaryTitle.getText().toString());
                params.put("content", diaryContent.getText().toString());
                params.put("memId", member.getMemId());
                if (imageData != null) {
                    params.put("imageData", imageData);
                }
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

    /** 사진 찍기 */
    private void takePicture() {
        // CameraSurfaceView의 capture 메서드 호출하기
        cameraView.capture(new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                try {
                    // 전달받은 바이트 배열을 Bitmap 객체로 만들기
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    String outUriStr = MediaStore.Images.Media.insertImage(mainActivity.getContentResolver(), bitmap, "Captured Image", "Captured Image using Camera.");

                    if (outUriStr == null) {
                        Log.d("SampleCapture", "Image insert failed.");
                        return;
                    } else {
                        Uri outUri = Uri.parse(outUriStr);
                        mainActivity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, outUri));
                        Log.d("INSERTED_IMAGES", outUri.toString());
                        String imageData = null;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            imageData =  Base64.getEncoder().encodeToString(data).trim();
                        } else {
                           imageData =  android.util.Base64.encodeToString(data, android.util.Base64.DEFAULT).trim();
                        }

                        processWrite(imageData);
                    }

                    camera.startPreview();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}