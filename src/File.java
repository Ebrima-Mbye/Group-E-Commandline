// File.java
public class File extends FileEntity {
    private StringBuilder content;

    public File(String name) {
        super(name);
        this.content = new StringBuilder();
    }

    public void writeContent(String data) {
        content.append(data);
    }

    public String readContent() {
        return content.toString();
    }

    @Override
    public boolean isDirectory() {
        return false;
    }
}
