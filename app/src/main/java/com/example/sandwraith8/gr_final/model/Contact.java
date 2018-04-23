package com.example.sandwraith8.gr_final.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by sandwraith8 on 24/03/2018.
 */

@Getter
@Setter
public class Contact {
    private String name;
    private List<String> phones = new ArrayList<>();
    private String email;
    private String image;
}
