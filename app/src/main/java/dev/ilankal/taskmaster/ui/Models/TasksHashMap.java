package dev.ilankal.taskmaster.ui.Models;

import java.util.HashMap;

public class TasksHashMap {
    private String userId = "";
    private HashMap<String, Task> allTasks = new HashMap<>();

    public TasksHashMap() {
    }
    public TasksHashMap(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public TasksHashMap setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public HashMap<String, Task> getAllTasks() {
        return allTasks;
    }

    public TasksHashMap setAllTasks(HashMap<String, Task> allTasks) {
        this.allTasks = allTasks;
        return this;
    }
    @Override
    public String toString() {
        return "TasksHashMap{" +
                "userId='" + userId + '\'' +
                ", allTasks=" + allTasks +
                '}';
    }
}
