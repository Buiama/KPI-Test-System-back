package com.kpi.authservice.services.implementations;

import org.springframework.stereotype.Service;

import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class EmailValidatorService implements Predicate<String> {
    public boolean test(String email) {
        String regex = "^[a-zA-Z0-9]+(\\.[a-zA-Z0-9]+)*@(lll\\.)?kpi\\.ua$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
