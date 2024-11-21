// Directory.java
import java.util.*;

public class Directory extends FileEntity {
    private Map<String, FileEntity> children;

    public Directory(String name) {
        super(name);
        this.children = new HashMap<>();
    }

    public boolean addEntity(FileEntity entity) {
        if (children.containsKey(entity.getName())) {
            return false; // Name conflict
        }
        children.put(entity.getName(), entity);
        return true;
    }

    public FileEntity getEntity(String name) {
        return children.get(name);
    }

    public boolean removeEntity(String name) {
        return children.remove(name) != null;
    }

    public List<FileEntity> listEntities() {
        return new ArrayList<>(children.values());
    }

    @Override
    public boolean isDirectory() {
        return true;
    }
}
