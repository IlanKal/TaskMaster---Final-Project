package dev.ilankal.taskmaster.Interfaces;

public interface TaskCountsByUserCallback {
    void onTaskCountsByUserUpdated(int completedTasks, int pendingTasks);
    void onError(Exception e);
}