package com.lazar.fabrica_de_coduri.utils;

import java.text.Normalizer;

public class SlugUtils {
    public static String toSlug(String input) {
        if (input == null) return "";

        String noNumberPrefix = input.replaceFirst("^\\d+\\.*\\s*", "")
                .replace("C++", "Cpp")
                .replace("c++", "cpp")
                .replace("C#", "C sharp")
                .replace("c#", "c sharp");

        return Normalizer.normalize(noNumberPrefix, Normalizer.Form.NFD)
                .replaceAll("[\\p{InCombiningDiacriticalMarks}]", "")
                .replaceAll("[^a-zA-Z0-9]", "-")
                .replaceAll("-{2,}", "-")
                .replaceAll("^-|-$", "")
                .toLowerCase();
    }
}
