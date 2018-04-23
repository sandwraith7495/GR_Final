package com.example.sandwraith8.gr_final.repository;

import com.example.sandwraith8.gr_final.model.Person;

/**
 * Created by sandwraith8 on 14/04/2018.
 */

public interface PersonRepository {
    void add(Person person);

    void edit(Person person);

    Person find(String name);
}
