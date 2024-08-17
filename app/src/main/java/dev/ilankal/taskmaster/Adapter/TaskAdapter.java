
package dev.ilankal.taskmaster.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import dev.ilankal.taskmaster.Data.DataManager;
import dev.ilankal.taskmaster.Interfaces.RequestDb;
import dev.ilankal.taskmaster.R;
import dev.ilankal.taskmaster.ui.Fragments.AddNewTask;
import dev.ilankal.taskmaster.ui.Models.Task;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.MyViewHolder> {
    private List<Task> taskList;
    private FirebaseUser currentUser;
    private DatabaseReference databaseReference;
    private FragmentManager fragmentManager;
    private Context context;


    public TaskAdapter(List<Task> taskList, FirebaseUser currentUser, FragmentManager fragmentManager, Context context) {
        this.taskList = taskList;
        this.currentUser = currentUser;
        this.fragmentManager = fragmentManager;
        this.context = context;
        this.databaseReference = FirebaseDatabase.getInstance().getReference("users")
                .child(currentUser.getUid()).child("allTasks");
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_box, parent, false);
        return new MyViewHolder(view);
    }

    public void deleteTask(int pos) {
        if (pos >= 0 && pos < taskList.size()) {
            Task task = taskList.get(pos);

            Log.d("TaskAdapter", "Attempting to delete task at position: " + pos + " with ID: " + task.getId());
            Log.d("TaskAdapter", "Current task list size before deletion: " + taskList.size());

            taskList.remove(pos);
            notifyItemRemoved(pos);

            DataManager.getInstance().deleteTaskByUser(currentUser, task.getId(), new RequestDb() {
                @Override
                public void onSuccess() {
                    Log.d("TaskAdapter", "Task deleted successfully.");
                }

                @Override
                public void onFailure(Exception e) {
                    Log.d("TaskAdapter", "Failed to delete task.", e);

                }
            });
        } else {
            Log.e("TaskAdapter", "Invalid index for deletion: " + pos + ". Task list size: " + taskList.size());
        }
    }
    public void editTask(int pos) {
        Task task = taskList.get(pos);
        Bundle bundle = new Bundle();
        bundle.putString("id", task.getId());
        bundle.putString("task", task.getDescription());
        bundle.putString("date", task.getDate());
        bundle.putString("category", task.getCategory().toString());
        bundle.putString("type", task.getType().toString());

        AddNewTask addNewTask = new AddNewTask();
        addNewTask.setArguments(bundle);
        addNewTask.show(fragmentManager, AddNewTask.TAG);
    }
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Task task = taskList.get(position);

        holder.mcheckbox.setOnCheckedChangeListener(null);
        holder.mcheckbox.setText(task.getDescription());
        holder.date_tv.setText(task.getDate());
        holder.category_tv.setText("Category: " + task.getCategoryString());
        holder.type_tv.setText("Type: " + task.getTypeString());
        holder.mcheckbox.setChecked(task.isCompleted());

        // Reattach listener
        holder.mcheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {

            task.setCompleted(isChecked);

            // Update the task in the database
            DataManager.getInstance().updateTaskCompletionStatus(currentUser, task.getId(), isChecked, new RequestDb() {
                @Override
                public void onSuccess() {
                    Log.d("Adapter", "Task completion status updated successfully.");
                }

                @Override
                public void onFailure(Exception e) {
                    Log.d("Adapter", "Failed to update task completion status.", e);
                    Toast.makeText(context, "Error updating task status", Toast.LENGTH_SHORT).show();
                }
            });
        });

        holder.shareButton.setOnClickListener(v -> {
            String shareText = "Task: " + task.getDescription() + "\n" +
                    "Date: " + task.getDate() + "\n" +
                    "Category: " + task.getCategoryString() + "\n" +
                    "Type: " + task.getTypeString() + "\n\n" +
                    "Shared via TaskMaster: Manage your tasks efficiently and effectively";

            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, shareText);
            intent.setType("text/plain");

            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private MaterialCheckBox mcheckbox;
        private TextView date_tv;
        private TextView category_tv;
        private TextView type_tv;
        private ImageView shareButton;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mcheckbox = itemView.findViewById(R.id.mcheckbox);
            date_tv = itemView.findViewById(R.id.date_tv);
            category_tv = itemView.findViewById(R.id.category_tv);
            type_tv = itemView.findViewById(R.id.type_tv);
            shareButton = itemView.findViewById(R.id.shareButton);
        }
    }
}

