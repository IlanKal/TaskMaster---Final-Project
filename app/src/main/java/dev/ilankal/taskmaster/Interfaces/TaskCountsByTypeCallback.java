package dev.ilankal.taskmaster.Interfaces;

public interface TaskCountsByTypeCallback {
    void onTaskCountsByTypeUpdated(int importantTasks, int urgentTasks, int optionalTasks);
    void onError(Exception e);
}