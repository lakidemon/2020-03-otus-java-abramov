package ru.otus.core.model;

import com.google.common.collect.Iterables;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private int age;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    private Address address;
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_id")
    private List<Phone> phones;

    public User(String name, int age, Address address, List<Phone> phones) {
        this(0L, name, age, address, new ArrayList<>(phones));
    }

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (object == null || getClass() != object.getClass())
            return false;

        User user = (User) object;

        if (id != user.id)
            return false;
        if (age != user.age)
            return false;
        if (!name.equals(user.name))
            return false;
        if (!address.equals(user.address))
            return false;
        return Iterables.elementsEqual(phones, user.phones);
    }

}

