// FileSystem.java
public class FileSystem {
    private Directory root;
    private Directory current;

    public FileSystem() {
        root = new Directory("/");
        current = root;
    }

    public boolean mkdir(String name) {
        return current.addEntity(new Directory(name));
    }

    public boolean touch(String name) {
        return current.addEntity(new File(name));
    }

    public String ls() {
        StringBuilder output = new StringBuilder();
        for (FileEntity entity : current.listEntities()) {
            output.append(entity.getName());
            output.append(entity.isDirectory() ? "/" : "");
            output.append(" ");
        }
        return output.toString().trim();
    }

    public boolean cd(String name) {
        if (name.equals("..")) {
            if (current.getName().equals("/")) {
                return false; // Already at root
            }
            current = (Directory) root.getEntity("/"); // Move up logic here
            return true;
        }

        FileEntity entity = current.getEntity(name);
        if (entity != null && entity.isDirectory()) {
            current = (Directory) entity;
            return true;
        }
        return false; // Invalid directory
    }

    public String pwd() {
        return current.getName();
    }

    public File getFile(String name) {
        FileEntity entity = current.getEntity(name);
        if (entity instanceof File) {
            return (File) entity;
        }
        return null;
    }

    public boolean delete(String name) {
        return current.removeEntity(name);
    }
}
