import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Arrays;

public class FileSystemSimulatorGUI extends JFrame {
    private JTextArea terminalArea;
    private java.io.File currentDirectory;  // Using java.io.File here

    public FileSystemSimulatorGUI() {
        currentDirectory = new java.io.File(System.getProperty("user.home"));

        setTitle("Group E Command Prompt");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        terminalArea = new JTextArea();
        terminalArea.setEditable(true);
        terminalArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        terminalArea.setBackground(Color.BLACK);
        terminalArea.setForeground(Color.WHITE);
        terminalArea.setCaretColor(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(terminalArea);
        add(scrollPane, BorderLayout.CENTER);

        terminalArea.setText("Welcome to the Group E File System Simulator!\n"
                             + "Current Directory: " + currentDirectory.getAbsolutePath() + "\n"
                             + "Type 'help' for a list of commands.\n");
        
        terminalArea.setCaretPosition(terminalArea.getDocument().getLength());

        terminalArea.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent e) {
                // Prevent deleting the '>' prompt
                String text = terminalArea.getText();
                if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE || e.getKeyCode() == KeyEvent.VK_DELETE) {
                    // Check if last character is the prompt ">"
                    int caretPos = terminalArea.getCaretPosition();
                    if (caretPos > 2 && text.substring(caretPos - 2, caretPos).equals("> ")) {
                        e.consume();  // Prevent delete if it's the '>' prompt
                    }
                }

                // Handle Enter key press (process the command)
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String command = text.substring(text.lastIndexOf(">") + 1).trim();
                    if (!command.isBlank()) {
                        processCommand(command);
                    }
                    terminalArea.append("\n> ");
                    e.consume();  // Prevents adding a new line after pressing Enter
                }
            }
        });

        terminalArea.append("> ");  // Initial prompt

        setVisible(true);
    }

    private void processCommand(String command) {
        String[] parts = command.trim().split(" ", 3); // Split into command and up to two arguments
        String cmd = parts[0];
        String argument1 = parts.length > 1 ? parts[1] : "";
        String argument2 = parts.length > 2 ? parts[2] : "";

        switch (cmd) {
            case "pwd":
                appendOutput("Current Directory: " + currentDirectory.getAbsolutePath());
                break;

            case "ls":
                java.io.File[] files = currentDirectory.listFiles();
                if (files != null) {
                    appendOutput("Directory Contents:");
                    Arrays.stream(files)
                            .forEach(file -> appendOutput((file.isDirectory() ? "[DIR] " : "[FILE] ") + file.getName()));
                } else {
                    appendOutput("Error listing directory contents.");
                }
                break;

            case "mkdir":
                if (!argument1.isBlank()) {
                    java.io.File newDir = new java.io.File(currentDirectory, argument1);
                    if (newDir.mkdir()) {
                        appendOutput("Directory created: " + newDir.getAbsolutePath());
                    } else {
                        appendOutput("Error creating directory: " + argument1);
                    }
                } else {
                    appendOutput("Usage: mkdir <directory_name>");
                }
                break;

            case "touch":
                if (!argument1.isBlank()) {
                    java.io.File newFile = new java.io.File(currentDirectory, argument1);
                    try {
                        if (newFile.createNewFile()) {
                            appendOutput("File created: " + newFile.getAbsolutePath());
                        } else {
                            appendOutput("Error creating file: " + argument1);
                        }
                    } catch (IOException ex) {
                        appendOutput("IOException: " + ex.getMessage());
                    }
                } else {
                    appendOutput("Usage: touch <file_name>");
                }
                break;

            case "cd":
                if (!argument1.isBlank()) {
                    java.io.File newDir = new java.io.File(argument1);
                    if (!newDir.isAbsolute()) {
                        newDir = new java.io.File(currentDirectory, argument1);
                    }
                    if (newDir.exists() && newDir.isDirectory()) {
                        currentDirectory = newDir;
                        appendOutput("Changed directory to: " + currentDirectory.getAbsolutePath());
                    } else {
                        appendOutput("Error: Directory does not exist: " + argument1);
                    }
                } else {
                    appendOutput("Usage: cd <directory_path>");
                }
                break;

            case "edit":
                if (!argument1.isBlank()) {
                    java.io.File fileToEdit = new java.io.File(currentDirectory, argument1);
                    if (fileToEdit.exists() && fileToEdit.isFile()) {
                        try {
                            String content = JOptionPane.showInputDialog(this, "Enter content to write to the file:");
                            if (content != null) {
                                FileWriter writer = new FileWriter(fileToEdit, true); // Append mode
                                writer.write(content + System.lineSeparator());
                                writer.close();
                                appendOutput("Content added to file: " + argument1);
                            }
                        } catch (IOException ex) {
                            appendOutput("Error editing file: " + ex.getMessage());
                        }
                    } else {
                        appendOutput("Error: File does not exist: " + argument1);
                    }
                } else {
                    appendOutput("Usage: edit <file_name>");
                }
                break;

            case "rename":
                if (!argument1.isBlank() && !argument2.isBlank()) {
                    java.io.File oldFile = new java.io.File(currentDirectory, argument1);
                    java.io.File newFile = new java.io.File(currentDirectory, argument2);
                    if (oldFile.exists()) {
                        if (oldFile.renameTo(newFile)) {
                            appendOutput("Renamed: " + argument1 + " to " + argument2);
                        } else {
                            appendOutput("Error renaming: " + argument1);
                        }
                    } else {
                        appendOutput("Error: File or directory does not exist: " + argument1);
                    }
                } else {
                    appendOutput("Usage: rename <old_name> <new_name>");
                }
                break;

            case "rm":
                if (!argument1.isBlank()) {
                    java.io.File fileToDelete = new java.io.File(currentDirectory, argument1);
                    if (fileToDelete.exists()) {
                        if (fileToDelete.isDirectory()) {
                            if (fileToDelete.delete()) {
                                appendOutput("Directory deleted: " + argument1);
                            } else {
                                appendOutput("Error deleting directory (make sure it's empty): " + argument1);
                            }
                        } else {
                            if (fileToDelete.delete()) {
                                appendOutput("File deleted: " + argument1);
                            } else {
                                appendOutput("Error deleting file: " + argument1);
                            }
                        }
                    } else {
                        appendOutput("Error: File or directory does not exist: " + argument1);
                    }
                } else {
                    appendOutput("Usage: rm <name>");
                }
                break;

            case "help":
                appendOutput("Available commands:");
                appendOutput("pwd - Show current directory");
                appendOutput("ls - List contents of the current directory");
                appendOutput("mkdir <name> - Create a directory");
                appendOutput("touch <name> - Create a file");
                appendOutput("edit <name> - Edit a file's content");
                appendOutput("rename <old_name> <new_name> - Rename a file or directory");
                appendOutput("rm <name> - Delete a file or directory");
                appendOutput("cd <path> - Change directory");
                appendOutput("exit - Exit the simulator");
                break;

            case "exit":
                appendOutput("Exiting File System Simulator. Goodbye!");
                System.exit(0);
                break;

            default:
                appendOutput("Unknown command: " + cmd);
                appendOutput("Type 'help' for a list of commands.");
                break;
        }
    }

    private void appendOutput(String message) {
        terminalArea.append("\n" + message);
        terminalArea.setCaretPosition(terminalArea.getDocument().getLength());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FileSystemSimulatorGUI());
    }
}
