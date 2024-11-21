// FileEntity.java
public abstract class FileEntity {
    protected String name;

    public FileEntity(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract boolean isDirectory();
}
