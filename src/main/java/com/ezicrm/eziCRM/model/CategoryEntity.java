package com.ezicrm.eziCRM.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.Objects;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "category", schema = "test_db", catalog = "")
public class CategoryEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "cat_id", nullable = false)
    private long catId;

    public long getCatId() {
        return catId;
    }

    public void setCatId(long catId) {
        this.catId = catId;
    }

    @Basic
    @Column(name = "category_name", nullable = false, length = 100, unique = true)
    private String categoryName;

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @Basic
    @Column(name = "updated")
    @UpdateTimestamp
    private Timestamp updated;

    public Timestamp getUpdated() {
        return updated;
    }

    public void setUpdated(Timestamp updated) {
        this.updated = updated;
    }

    @Basic
    @Column(name = "created", nullable = true)
    @CreationTimestamp
    private Timestamp created;

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CategoryEntity that = (CategoryEntity) o;

        if (catId != that.catId) return false;
        if (!Objects.equals(categoryName, that.categoryName)) return false;
        if (!Objects.equals(updated, that.updated)) return false;
        if (!Objects.equals(created, that.created)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (catId ^ (catId >>> 32));
        result = 31 * result + (categoryName != null ? categoryName.hashCode() : 0);
        result = 31 * result + (updated != null ? updated.hashCode() : 0);
        result = 31 * result + (created != null ? created.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CategoryEntity{" +
                "catId=" + catId +
                ", categoryName='" + categoryName + '\'' +
                ", updated=" + updated +
                ", created=" + created +
                '}';
    }

    @ManyToMany
    @JoinTable(name = "rel_cus_cat",
            joinColumns = @JoinColumn(name = "cat_id"),
            inverseJoinColumns = @JoinColumn(name = "cus_id"))
    private Set<CustomerEntity> assignedCustomers = new HashSet<>();

    public Set<CustomerEntity> getAssCustomers() {
        return assignedCustomers;
    }

    public void setAssCustomers(Set<CustomerEntity> assCustomers) {
        this.assignedCustomers = assCustomers;
    }
}
