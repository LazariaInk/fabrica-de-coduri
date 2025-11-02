package com.lazar.fabrica_de_coduri.service;

import com.lazar.fabrica_de_coduri.model.Course;
import com.lazar.fabrica_de_coduri.repository.CourseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseService {
    private final CourseRepository repo;
    public CourseService(CourseRepository repo) { this.repo = repo; }

    //public List<Course> listFeatured() { return repo.findTop8ByIsFeaturedTrueOrderByIdDesc(); }
    public List<Course> listAll()      { return repo.findAllByOrderByIdDesc(); }
    public Course getBySlug(String slug){
        return repo.findBySlug(slug).orElseThrow(() -> new RuntimeException("Cursul nu există"));
    }
}
