package dev.ilankal.taskmaster.ui.Models;

import dev.ilankal.taskmaster.Enum.Category;
import dev.ilankal.taskmaster.Enum.Type;

public class Task {
    private String id = "";
    private String description = "";
    private String date = "";
    private Category category = Category.OTHER;
    private Type type = Type.OPTIONAL;
    private Boolean isCompleted = Boolean.FALSE;

    public Task(){

    }

    public Task(String description, String date, Category category, Type type, Boolean isCompleted) {
        this.description = description;
        this.date = date;
        this.category = category;
        this.type = type;
        this.isCompleted = isCompleted;
    }

    // Getters and setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    // Method to convert enum to specific string representation
    public String getTypeString() {
        if (type != null) {
            switch (type) {
                case IMPORTANT:
                    return "Important";
                case URGENT:
                    return "Urgent";
                case OPTIONAL:
                    return "Optional";
                default:
                    return "Unknown";
            }
        }
        return "Unknown";
    }

    // Method to convert string representation to enum
    public Type getStringType(String typeStr) {
        if (typeStr != null) {
            switch (typeStr.toLowerCase()) {
                case "important":
                    return Type.IMPORTANT;
                case "urgent":
                    return Type.URGENT;
                case "optional":
                    return Type.OPTIONAL;
                default:
                    return null;
            }
        }
        return null;
    }

    // Method to convert enum to specific string representation
    public String getCategoryString() {
        if (category != null) {
            switch (category) {
                case WORK:
                    return "Work";
                case PERSONAL:
                    return "Personal";
                case HOME:
                    return "Home";
                case FITNESS:
                    return "Fitness";
                case OTHER:
                    return "Other";
                default:
                    return "Unknown";
            }
        }
        return "Unknown";
    }

    // Method to convert string representation to enum
    public Category getStringCategory(String categoryStr) {
        if (categoryStr != null) {
            switch (categoryStr.toLowerCase()) {
                case "work":
                    return Category.WORK;
                case "personal":
                    return Category.PERSONAL;
                case "home":
                    return Category.HOME;
                case "fitness":
                    return Category.FITNESS;
                case "other":
                    return Category.OTHER;
                default:
                    return null;
            }
        }
        return null;
    }



    @Override
    public String toString() {
        return "Task{" +
                "id='" + id + '\'' +
                ", description='" + description + '\'' +
                ", date='" + date + '\'' +
                ", category=" + category +
                ", type=" + type +
                ", isCompleted=" + isCompleted +
                '}';
    }
}


