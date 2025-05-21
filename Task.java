import java.time.LocalDate;

public class Task implements Comparable<Task> {
    private String title;
    private LocalDate dueDate;
    private String priority;
    private boolean isDone;
    private int reminderDays;
    private boolean reminderSent;
    private String userEmail; // Optional: can also store globally

    public Task(String title, LocalDate dueDate, String priority, boolean isDone,
    int reminderDays, boolean reminderSent, String userEmail) {
        this.title = title;
        this.dueDate = dueDate;
        this.priority = priority;
        this.isDone = isDone;
        this.reminderDays = reminderDays;
        this.reminderSent = reminderSent;
        this.userEmail = userEmail;
    }

    public String getTitle() { return title; }
    public LocalDate getDueDate() { return dueDate; }
    public String getPriority() { return priority; }
    public boolean isDone() { return isDone; }

    public int getReminderDays() { return reminderDays; }

    public boolean isReminderSent() { return reminderSent; }

    public String getUserEmail() { return userEmail; }

    public void markDone() { this.isDone = true; }

    public void setReminderSent(boolean reminderSent) { this.reminderSent = reminderSent; }

    @Override
    public String toString() {
        return (isDone ? "[Done] " : "") +
               title + " | Due: " + dueDate +
               " | Priority: " + priority +
               " | Reminder: " + reminderDays + " day(s) before";
    }

    // Sort: by dueDate, then by priority
    @Override
    public int compareTo(Task o) {
        int dateCmp = this.dueDate.compareTo(o.dueDate);
        if (dateCmp != 0) return dateCmp;

        return priorityValue(this.priority) - priorityValue(o.priority);
    }

    private int priorityValue(String p) {
        return switch (p.toLowerCase()) {
            case "high" -> 1;
            case "medium" -> 2;
            case "low" -> 3;
            default -> 4;
        };
    }

    public String serialize() {
        return title + ";" +
               dueDate + ";" +
               priority + ";" +
               isDone + ";" +
               reminderDays + ";" +
               reminderSent + ";" +
               userEmail;
    }

    public static Task deserialize(String line) {
        String[] parts = line.split(";", -1); // allows empty fields

        // Default values for reminderDays and email if not present
        int reminderDays = parts.length > 4 ? Integer.parseInt(parts[4]) : 0;
        boolean reminderSent = parts.length > 5 && Boolean.parseBoolean(parts[5]);
        String email = parts.length > 6 ? parts[6] : "";

        return new Task(
            parts[0],
            LocalDate.parse(parts[1]),
            parts[2],
            Boolean.parseBoolean(parts[3]),
            reminderDays,
            reminderSent,
            email
        );
    }
}
