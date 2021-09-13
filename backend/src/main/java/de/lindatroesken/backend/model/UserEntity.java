package de.lindatroesken.backend.model;

import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "app_user")
@Setter
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "userEntity", fetch = FetchType.EAGER)
    private Set<MangelEntity> mangelList;

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "user_name", nullable = false, unique = true)
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "user_role")
    private String role;

    public void addMangel(MangelEntity mangelEntity) {
        mangelList.add(mangelEntity);
        mangelEntity.setUserEntity(this);
    }

    public void removeMangel(MangelEntity mangelEntity) {
        mangelList.remove(mangelEntity);
        mangelEntity.setUserEntity(null);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity that = (UserEntity) o;
        return username.equals(that.username);
    }

    @Override
    public int hashCode() {
        return getId() == null ? getUsername().hashCode() : getId().hashCode();
    }
}
