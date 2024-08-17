package dev.ilankal.taskmaster.ui.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.firebase.ui.auth.AuthUI;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import dev.ilankal.taskmaster.Data.DataManager;
import dev.ilankal.taskmaster.Interfaces.TaskCountsByCategoryCallback;
import dev.ilankal.taskmaster.Interfaces.TaskCountsByTypeCallback;
import dev.ilankal.taskmaster.Interfaces.TaskCountsByUserCallback;
import dev.ilankal.taskmaster.LoginActivity;
import dev.ilankal.taskmaster.R;
import dev.ilankal.taskmaster.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private ImageView logoImage;
    private MaterialButton logoutButton;
    private TextView completedTasksText;
    private TextView pendingTasksText;
    private PieChart pieChart;
    private BarChart barChart;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        findViews(root);
        initViews();

        return root;
    }

    private void findViews(View root) {
        logoImage = root.findViewById(R.id.logo_image);
        logoutButton = root.findViewById(R.id.logout_button);
        completedTasksText = root.findViewById(R.id.completed_tasks_text);
        pendingTasksText = root.findViewById(R.id.pending_tasks_text);
        pieChart = root.findViewById(R.id.pieChart);
        barChart = root.findViewById(R.id.barChart);
    }

    private void initViews() {
        if (getContext() != null) {
            logoutButton.setOnClickListener(view -> {
                AuthUI.getInstance()
                        .signOut(getContext())
                        .addOnCompleteListener(task -> {
                            // Redirect to login screen after successful sign-out
                            Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
                            startActivity(loginIntent);
                            getActivity().finish();
                        });
            });
        }

        // Initialize or update UI components here
        updateTaskCounts();
        setupBarChart();
        updatePieChart();
    }

    private void setupBarChart() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DataManager.getInstance().getTaskCountsByCategory(user, new TaskCountsByCategoryCallback() {
                @Override
                public void onTaskCountsByCategoryUpdated(int workTasks, int personalTasks, int homeTasks, int fitnessTasks, int otherTasks) {
                    ArrayList<BarEntry> barEntries = new ArrayList<>();
                    barEntries.add(new BarEntry(0f, workTasks)); // Work
                    barEntries.add(new BarEntry(1f, personalTasks)); // Personal
                    barEntries.add(new BarEntry(2f, homeTasks)); // Home
                    barEntries.add(new BarEntry(3f, fitnessTasks)); // Fitness
                    barEntries.add(new BarEntry(4f, otherTasks)); // Other

                    BarDataSet barDataSet = new BarDataSet(barEntries, "Tasks by Category");

                    int purple700 = ContextCompat.getColor(requireContext(), R.color.purple_700);
                    int cornflowerBlue = ContextCompat.getColor(requireContext(), R.color.cornflower_blue);
                    int malibu = ContextCompat.getColor(requireContext(), R.color.malibu);

                    barDataSet.setColors(purple700, cornflowerBlue, malibu);

                    BarData barData = new BarData(barDataSet);
                    barData.setBarWidth(0.9f); // set custom bar width

                    barChart.setData(barData);
                    barChart.setFitBars(true); // make the x-axis fit exactly all bars

                    // X-axis setup
                    XAxis xAxis = barChart.getXAxis();
                    xAxis.setGranularity(1f);
                    xAxis.setGranularityEnabled(true);
                    xAxis.setLabelCount(5);
                    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                    xAxis.setValueFormatter(new IndexAxisValueFormatter(new String[]{"Work", "Personal", "Home", "Fitness", "Other"}));

                    // Y-axis setup
                    YAxis leftAxis = barChart.getAxisLeft();
                    leftAxis.setGranularity(1f);
                    leftAxis.setAxisMinimum(0f); // Start Y-axis at 0
                    leftAxis.setAxisMaximum(10f); // Set Y-axis maximum to 10

                    YAxis rightAxis = barChart.getAxisRight();
                    rightAxis.setEnabled(false); // Disable right Y-axis

                    barChart.getDescription().setEnabled(false); // Disable chart description
                    barChart.animateY(1000); // Animate Y-axis
                    barChart.invalidate(); // Refresh the chart
                }
                @Override
                public void onError(Exception e) {
                    // Handle error
                }
            });
        }
    }

    private void updateTaskCounts() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DataManager.getInstance().getCompletedAndPendingTasks(user, new TaskCountsByUserCallback() {
                @Override
                public void onTaskCountsByUserUpdated(int completedTasks, int pendingTasks) {
                    completedTasksText.setText(completedTasks + "\nCompleted Tasks");
                    pendingTasksText.setText(pendingTasks + "\nPending Tasks");
                }
                @Override
                public void onError(Exception e) {
                    // Handle error
                }
            });
        }
    }

    private void updatePieChart() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DataManager.getInstance().getTaskCountsByType(user, new TaskCountsByTypeCallback() {
                @Override
                public void onTaskCountsByTypeUpdated(int importantTasks, int urgentTasks, int optionalTasks) {
                    ArrayList<PieEntry> entries = new ArrayList<>();
                    entries.add(new PieEntry(importantTasks, "Important"));
                    entries.add(new PieEntry(urgentTasks, "Urgent"));
                    entries.add(new PieEntry(optionalTasks, "Optional"));

                    PieDataSet pieDataSet = new PieDataSet(entries, "Tasks by Types");

                    int purple700 = ContextCompat.getColor(requireContext(), R.color.purple_700);
                    int cornflowerBlue = ContextCompat.getColor(requireContext(), R.color.cornflower_blue);
                    int malibu = ContextCompat.getColor(requireContext(), R.color.malibu);

                    pieDataSet.setColors(purple700, cornflowerBlue, malibu);

                    PieData pieData = new PieData(pieDataSet);
                    pieChart.setData(pieData);
                    pieChart.getDescription().setEnabled(false);
                    pieChart.animateY(1000);
                    pieChart.invalidate(); // Refresh the chart
                }

                @Override
                public void onError(Exception e) {
                    // Handle error
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
