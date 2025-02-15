package org.acme;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.util.UUID;

@Entity
@Table(name = "test")
public class TestEntity {

    @Id
    String id = UUID.randomUUID().toString();
    String name;
    @Version
    private Integer version;

    protected TestEntity() {
    }

    public TestEntity(
        String id,
        String name
    ) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
