import javax.swing.*;
import java.awt.*;
//import java.awt.event.*;
import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class ToDoListApp extends JFrame {
    private DefaultListModel<Task> listModel;
    private JList<Task> taskList;
    private JTextField taskInput;
    private JComboBox<String> priorityInput;
    private JSpinner dateSpinner;
    private JTextField reminderDaysField;
    private JTextField emailField;

    private final String FILE_NAME = "tasks.txt";

    public ToDoListApp() {
        setTitle("To-Do List Manager with Email Reminders");
        setSize(700, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Task input
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        add(new JLabel("Task:"), gbc);

        taskInput = new JTextField(30);
        gbc.gridx = 1;
        add(taskInput, gbc);

        // Due Date
        gbc.gridy = 1; gbc.gridx = 0;
        add(new JLabel("Due Date:"), gbc);

        dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(dateEditor);
        gbc.gridx = 1;
        add(dateSpinner, gbc);

        // Priority
        gbc.gridy = 2; gbc.gridx = 0;
        add(new JLabel("Priority:"), gbc);

        String[] priorities = {"High", "Medium", "Low"};
        priorityInput = new JComboBox<>(priorities);
        gbc.gridx = 1;
        add(priorityInput, gbc);

        // Reminder Days
        gbc.gridy = 3; gbc.gridx = 0;
        add(new JLabel("Remind me (days before):"), gbc);

        reminderDaysField = new JTextField(5);
        gbc.gridx = 1;
        add(reminderDaysField, gbc);

        // Email Address
        gbc.gridy = 4; gbc.gridx = 0;
        add(new JLabel("Your Email:"), gbc);

        emailField = new JTextField(25);
        gbc.gridx = 1;
        add(emailField, gbc);

        // Buttons
        JButton addButton = new JButton("Add Task");
        JButton deleteButton = new JButton("Delete Task");
        JButton markDoneButton = new JButton("Mark Done");
        JButton saveButton = new JButton("Save Tasks");

        gbc.gridy = 5; gbc.gridx = 0;
        add(addButton, gbc);
        gbc.gridx = 1;
        add(deleteButton, gbc);
        gbc.gridy = 6; gbc.gridx = 0;
        add(markDoneButton, gbc);
        gbc.gridx = 1;
        add(saveButton, gbc);

        // Task List
        listModel = new DefaultListModel<>();
        loadTasks();

        taskList = new JList<>(listModel);
        taskList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(taskList);
        scrollPane.setPreferredSize(new Dimension(650, 250));

        gbc.gridy = 7; gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 0, 0, 0);
        add(scrollPane, gbc);

        // Actions
        addButton.addActionListener(e -> addTask());
        deleteButton.addActionListener(e -> deleteTask());
        markDoneButton.addActionListener(e -> markTaskDone());
        saveButton.addActionListener(e -> saveTasks());

        // Background thread to check reminders daily
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(24 * 60 * 60 * 1000); // once a day
                    SwingUtilities.invokeLater(this::checkReminders);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void addTask() {
        String title = taskInput.getText().trim();
        if (title.isEmpty()) return;

        Date date = (Date) dateSpinner.getValue();
        LocalDate dueDate = new java.sql.Date(date.getTime()).toLocalDate();
        String priority = (String) priorityInput.getSelectedItem();

        int reminderDays;
        try {
            reminderDays = Integer.parseInt(reminderDaysField.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid number for reminder days.");
            return;
        }

        String email = emailField.getText().trim();
        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your email.");
            return;
        }

        Task task = new Task(title, dueDate, priority, false, reminderDays, false, email);
        listModel.addElement(task);
        sortTasks();
        taskInput.setText("");
        reminderDaysField.setText("");
        emailField.setText("");
    }

    private void deleteTask() {
        int index = taskList.getSelectedIndex();
        if (index != -1) {
            listModel.remove(index);
        }
    }

    private void markTaskDone() {
        int index = taskList.getSelectedIndex();
        if (index != -1) {
            Task t = listModel.get(index);
            t.markDone();
            listModel.set(index, t);
            sortTasks();
        }
    }

    private void saveTasks() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (int i = 0; i < listModel.size(); i++) {
                writer.write(listModel.get(i).serialize());
                writer.newLine();
            }
            JOptionPane.showMessageDialog(this, "Tasks saved.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to save tasks.");
        }
    }

    private void loadTasks() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                listModel.addElement(Task.deserialize(line));
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to load tasks.");
        }
        sortTasks();
    }

    private void sortTasks() {
        java.util.List<Task> tasks = Collections.list(listModel.elements());
        Collections.sort(tasks);
        listModel.clear();
        for (Task t : tasks) {
            listModel.addElement(t);
        }
    }

    private void checkReminders() {
        LocalDate today = LocalDate.now();
        for (int i = 0; i < listModel.size(); i++) {
            Task task = listModel.get(i);
            if (!task.isDone() && !task.isReminderSent()) {
                long daysUntilDue = java.time.temporal.ChronoUnit.DAYS.between(today, task.getDueDate());
                if (daysUntilDue == task.getReminderDays()) {
                    String subject = "Reminder: Task \"" + task.getTitle() + "\" due soon!";
                    String body = "Task: " + task.getTitle() +
                                  "\nDue Date: " + task.getDueDate() +
                                  "\nPriority: " + task.getPriority();
                    EmailUtil.sendEmail(task.getUserEmail(), subject, body);
                    task.setReminderSent(true);
                    listModel.set(i, task);
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ToDoListApp().setVisible(true));
    }
}
