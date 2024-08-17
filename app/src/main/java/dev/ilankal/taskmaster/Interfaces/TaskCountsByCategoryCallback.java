package dev.ilankal.taskmaster.Interfaces;

public interface TaskCountsByCategoryCallback {
    void onTaskCountsByCategoryUpdated(int workTasks, int personalTasks, int homeTasks, int fitnessTasks, int otherTasks);
    void onError(Exception e);
}
