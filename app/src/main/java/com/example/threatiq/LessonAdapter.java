package com.example.threatiq;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class LessonAdapter extends RecyclerView.Adapter<LessonAdapter.LessonViewHolder> {

    private Context context;
    private ArrayList<Lesson> lessonList;

    public LessonAdapter(Context context, ArrayList<Lesson> lessonList) {
        this.context = context;
        this.lessonList = lessonList;
    }

    @NonNull
    @Override
    public LessonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.lesson_row_item, parent, false);
        return new LessonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LessonViewHolder holder, int position) {
        Lesson lesson = lessonList.get(position);
        holder.lessonTitle.setText(lesson.getTitle());
        holder.lessonImage.setImageResource(lesson.getImageResourceId());


        // --- THIS IS THE NEW PART ---
        // Set a click listener on the entire row view
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // When clicked, open the detail activity
                openLessonDetail(lesson.getTitle(), lesson.getImageResourceId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return lessonList.size();
    }

    // --- ADD THIS HELPER METHOD ---
    private void openLessonDetail(String title, int imageResId) {
        Intent intent = new Intent(context, LessonDetailActivity.class);
        intent.putExtra("LESSON_TITLE", title);
        intent.putExtra("LESSON_IMAGE_RES_ID", imageResId);
        context.startActivity(intent);
    }

    public static class LessonViewHolder extends RecyclerView.ViewHolder {
        ImageView lessonImage;
        TextView lessonTitle;

        public LessonViewHolder(@NonNull View itemView) {
            super(itemView);
            lessonImage = itemView.findViewById(R.id.lesson_image);
            lessonTitle = itemView.findViewById(R.id.lesson_title);
        }
    }
}
