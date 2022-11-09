package org.koreait.diary;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ViewHolder extends RecyclerView.ViewHolder {
    private TextView mainDiaryTitle;
    private TextView mainDiaryContent;

    public ViewHolder(@NonNull View itemView) {
        super(itemView);
        // 뷰 객체에 들어 있는 텍스트뷰 참조하기
        mainDiaryTitle = itemView.findViewById(R.id.mainDiaryTitle);
        mainDiaryContent = itemView.findViewById(R.id.mainDiaryContent);
    }

    public void setItem(Diary item) {
        mainDiaryTitle.setText(item.getTitle());
        mainDiaryContent.setText(item.getContent());
    }
}
