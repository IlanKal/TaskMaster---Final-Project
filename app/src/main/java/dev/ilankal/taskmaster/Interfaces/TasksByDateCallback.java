package dev.ilankal.taskmaster.Interfaces;

import java.util.List;

import dev.ilankal.taskmaster.ui.Models.Task;

public interface TasksByDateCallback {
    void onTasksRetrieved(List<Task> tasks);
    void onError(Exception e);
}