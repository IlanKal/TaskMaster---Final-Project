package dev.ilankal.taskmaster.ui.Fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

import dev.ilankal.taskmaster.Data.DataManager;
import dev.ilankal.taskmaster.Enum.Category;
import dev.ilankal.taskmaster.Enum.Type;
import dev.ilankal.taskmaster.Interfaces.RequestDb;
import dev.ilankal.taskmaster.R;
import dev.ilankal.taskmaster.SplashActivity;
import dev.ilankal.taskmaster.Utilities.SoundPlayer;
import dev.ilankal.taskmaster.ui.Models.Task;

public class AddNewTask extends BottomSheetDialogFragment {
    private SoundPlayer soundPlayer;
    public static final String TAG = "AddNewTask";
    private EditText taskDescription;
    private Button pickDateButton;
    private Spinner categorySpinner;
    private Spinner typeSpinner;
    private Button saveButton;
    private Context context;
    private DatabaseReference databaseReference;
    private String id = "";
    private String dueDateUpdate = "";
    private Task currentTask;

    public static AddNewTask newInstance() {
        return new AddNewTask();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_add_new_task, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);

        soundPlayer = new SoundPlayer(getContext());

        databaseReference = FirebaseDatabase.getInstance().getReference("tasks");
        boolean isUpdate = false;

        final Bundle bundle = getArguments();
        if (bundle != null) {
            isUpdate = true;
            String task = bundle.getString("task");
            id = bundle.getString("id");
            dueDateUpdate = bundle.getString("date");
            String category = bundle.getString("category");
            String type = bundle.getString("type");

            taskDescription.setText(task);
            pickDateButton.setText(dueDateUpdate);

            // Set the spinners to the correct values
            categorySpinner.setSelection(((ArrayAdapter<Category>) categorySpinner.getAdapter()).getPosition(Category.valueOf(category)));
            typeSpinner.setSelection(((ArrayAdapter<Type>) typeSpinner.getAdapter()).getPosition(Type.valueOf(type)));

            saveButton.setEnabled(true);  // Enable save button for editing
            saveButton.setBackgroundColor(getResources().getColor(R.color.cornflower_blue));  // Set to enabled color

            taskDescription.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    saveButton.setEnabled(!s.toString().trim().isEmpty());
                    saveButton.setBackgroundColor(s.toString().trim().isEmpty() ? Color.GRAY : getResources().getColor(R.color.cornflower_blue));
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }

        pickDateButton.setOnClickListener(v -> showDatePickerDialog());
        boolean finalIsUpdate = isUpdate;
        saveButton.setOnClickListener(v -> {
            saveTask(finalIsUpdate);
            soundPlayer.playSound(R.raw.task_save, false);
        });
    }

    private void findViews(View view) {
        taskDescription = view.findViewById(R.id.taskDescription);
        pickDateButton = view.findViewById(R.id.pickDateButton);
        categorySpinner = view.findViewById(R.id.categorySpinner);
        typeSpinner = view.findViewById(R.id.typeSpinner);
        saveButton = view.findViewById(R.id.saveButton);

        // Set up the Category spinner
        ArrayAdapter<Category> categoryAdapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item,
                Category.values()
        );
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        // Set up the Type spinner
        ArrayAdapter<Type> typeAdapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item,
                Type.values()
        );
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(typeAdapter);
    }
    private void setCurrentTask(Task task){
        this.currentTask = task;
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                (view, year1, month1, dayOfMonth) -> {
                    month1 = month1 + 1;
                    String date = dayOfMonth + "/" + month1 + "/" + year1;
                    pickDateButton.setText(date);
                },
                year, month, day);
        datePickerDialog.show();
    }

    private void saveTask(boolean isUpdate) {
        String description = taskDescription.getText().toString().trim();
        String date = pickDateButton.getText().toString().trim();
        Category category = (Category) categorySpinner.getSelectedItem();
        Type type = (Type) typeSpinner.getSelectedItem();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        if (user == null) {
            Toast.makeText(getContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        if (description.isEmpty() || date.equals("Pick Date") || category == null || type == null) {
            Toast.makeText(getContext(), "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isUpdate) {
            // Prepare the updated task object
            Task updatedTask = new Task(description, date, category, type, false);
            updatedTask.setId(id);  // Ensure the task ID is set for the update

            DataManager.getInstance().updateTaskByUser(user, updatedTask, new RequestDb() {
                @Override
                public void onSuccess() {
                    Toast.makeText(getContext(), "Task Updated", Toast.LENGTH_SHORT).show();
                    dismiss();
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(getContext(), "Update Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Create a Task object for a new task
            Task task = new Task(description, date, category, type, false);
            DataManager.getInstance().addNewTaskByUser(user, new RequestDb() {
                @Override
                public void onSuccess() {
                    Toast.makeText(getContext(), "Task saved", Toast.LENGTH_SHORT).show();
                    dismiss();
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(getContext(), "Error saving task: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }, task);
        }
    }



}
