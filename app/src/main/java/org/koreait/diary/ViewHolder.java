package org.koreait.diary;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.koreait.diary.commons.ApiURLs;
import org.koreait.diary.commons.Utils;

public class ViewHolder extends RecyclerView.ViewHolder {
    private ImageView mainDiaryPhoto;
    private TextView mainDiaryTitle;
    private TextView mainDiaryContent;
    private View itemView;

    public ViewHolder(@NonNull View itemView) {
        super(itemView);
        // 뷰 객체에 들어 있는 텍스트뷰 참조하기
        mainDiaryPhoto = itemView.findViewById(R.id.mainDiaryPhoto);
        mainDiaryTitle = itemView.findViewById(R.id.mainDiaryTitle);
        mainDiaryContent = itemView.findViewById(R.id.mainDiaryContent);
        this.itemView = itemView;
    }

    public void setItem(Diary item) {
        mainDiaryTitle.setText(item.getTitle());
        mainDiaryContent.setText(item.getContent());

        String photoUrl = item.getPhotoUrl();
        if (photoUrl == null) {
            return;
        }

        String imageSrc = ApiURLs.URL +  photoUrl;
        Picasso.get().load(imageSrc).into(mainDiaryPhoto);
        /**
        handler.post(() -> {

            Bitmap bitmap = Utils.getBitmapFromURL(imageSrc);
            mainDiaryPhoto.setImageBitmap(bitmap);
        });
         */
    }
}
