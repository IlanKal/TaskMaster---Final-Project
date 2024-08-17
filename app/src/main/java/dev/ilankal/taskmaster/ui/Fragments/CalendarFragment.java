package dev.ilankal.taskmaster.ui.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import dev.ilankal.taskmaster.Adapter.TaskAdapter;
import dev.ilankal.taskmaster.Data.DataManager;
import dev.ilankal.taskmaster.Interfaces.TasksByDateCallback;
import dev.ilankal.taskmaster.R;
import dev.ilankal.taskmaster.TouchHelper;
import dev.ilankal.taskmaster.databinding.FragmentCalendarBinding;
import dev.ilankal.taskmaster.ui.Models.Task;

public class CalendarFragment extends Fragment {
    private CalendarView calendarView;
    private RecyclerView recyclerView;
    private FirebaseUser currentUser;
    private TaskAdapter taskAdapter;
    private Calendar calendar;
    private FragmentCalendarBinding binding;
    private String currentDate;
    private List<Task> taskList;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCalendarBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize currentUser
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        findViews(root);
        initViews();

        return root;
    }

    private void findViews(View root) {
        calendarView = root.findViewById(R.id.calendarView);
        calendar = Calendar.getInstance();
        recyclerView = root.findViewById(R.id.RecyclerViewCalender);
    }

    private void initViews() {
        taskList = new ArrayList<>();

        // give the data at the first time before click
        long selectedDateInMillis = calendarView.getDate();
        Date currentDateDate = new Date(selectedDateInMillis);
        SimpleDateFormat dateFormat = new SimpleDateFormat("d/M/yyyy", Locale.getDefault());
        currentDate = dateFormat.format(currentDateDate);
        loadTasksByDate();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        taskAdapter = new TaskAdapter(taskList, currentUser, getChildFragmentManager(), getContext());
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new TouchHelper(taskAdapter, getContext()));
        itemTouchHelper.attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(taskAdapter);

        currentDate();
        calendarView.setOnDateChangeListener((view, year, month, day) -> {
            currentDate = dateView(day, month, year);
            loadTasksByDate();
        });
    }

    private String dateView(int day, int month, int year) {
        return day + "/" + (month + 1) + "/" + year;
    }

    private void currentDate() {
        calendarView.setDate(System.currentTimeMillis(), false, true);
    }

    private void loadTasksByDate() {
        if (currentUser == null) return;

        DataManager.getInstance().getTasksByDate(currentUser, currentDate, new TasksByDateCallback() {
            @Override
            public void onTasksRetrieved(List<Task> tasks) {
                taskList.clear();
                taskList.addAll(tasks);
                taskAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Exception e) {
                // Handle error
            }
        });
    }

}
