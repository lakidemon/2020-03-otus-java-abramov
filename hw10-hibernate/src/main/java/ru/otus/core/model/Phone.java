package ru.otus.core.model;

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
public class Phone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String number;

    public Phone(String number) {
        this.id = 0;
        this.number = number;
    }

    public static List<Phone> single(String phone) {
        return new ArrayList<Phone>() {
            {
                add(new Phone(phone));
            }};
    }
}
