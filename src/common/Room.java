package common;

import java.io.Serializable;
import java.util.List;

public class Room implements Serializable {
    private String id;
    private String name;
    private String description;
    private List<String> members; // lưu username

    public Room() {}

    public Room(String id, String name, String description, List<String> members) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.members = members;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }
}
