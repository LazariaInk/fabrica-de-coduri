package com.lazar.fabrica_de_coduri.dto;

import java.util.List;

public class CoursesPageDTO {
    public List<CourseCardDTO> items;
    public int page;
    public int pageSize;
    public long total;
    public int totalPages;

    public CoursesPageDTO(List<CourseCardDTO> items, int page, int pageSize, long total, int totalPages) {
        this.items = items; this.page = page; this.pageSize = pageSize; this.total = total; this.totalPages = totalPages;
    }
}
