package com.nhom14.controller;

import com.nhom14.model.Course;
import com.nhom14.model.User;
import com.nhom14.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/courses")
public class CourseController {

    @Autowired private CourseService courseService;

    private User currentUser(HttpSession s) { return (User) s.getAttribute("user"); }

    @GetMapping
    public String list(@RequestParam(required = false) String semester,
                       @RequestParam(required = false) String search,
                       HttpSession session, Model model) {
        User u = currentUser(session);
        if (u == null) return "redirect:/login";

        java.util.List<Course> courses;
        boolean hasSearch   = search   != null && !search.isBlank();
        boolean hasSemester = semester != null && !semester.isBlank();

        if (hasSearch && hasSemester) {
            courses = courseService.findByUserSemesterAndSearch(u.getUserId(), semester, search);
        } else if (hasSearch) {
            courses = courseService.findByUserAndSearch(u.getUserId(), search);
        } else if (hasSemester) {
            courses = courseService.findByUserAndSemester(u.getUserId(), semester);
        } else {
            courses = courseService.findByUser(u.getUserId());
        }

        // Lấy danh sách học kỳ duy nhất để hiển thị dropdown filter
        java.util.List<String> semesters = courseService.findByUser(u.getUserId()).stream()
                .map(Course::getSemester)
                .filter(s -> s != null && !s.isBlank())
                .distinct()
                .sorted()
                .toList();

        model.addAttribute("courses", courses);
        model.addAttribute("semester",  semester);
        model.addAttribute("search",    search);
        model.addAttribute("semesters", semesters);
        return "courses/list";
    }

    @GetMapping("/new")
    public String newForm(HttpSession session) {
        if (currentUser(session) == null) return "redirect:/login";
        return "courses/form";
    }

    @PostMapping("/new")
    public String create(@RequestParam String courseName,
                         @RequestParam(required = false) String courseCode,
                         @RequestParam(required = false) String lecturer,
                         @RequestParam(defaultValue = "0") int credits,
                         @RequestParam(required = false) String semester,
                         HttpSession session, Model model) {
        User u = currentUser(session);
        if (u == null) return "redirect:/login";
        Course c = new Course(u.getUserId(), courseName, courseCode, lecturer, credits, semester);
        String err = courseService.create(c);
        if (err != null) { model.addAttribute("error", err); return "courses/form"; }
        return "redirect:/courses?msg=created";
    }

    @GetMapping("/{courseId}/edit")
    public String editForm(@PathVariable int courseId, HttpSession session, Model model) {
        User u = currentUser(session);
        if (u == null) return "redirect:/login";
        model.addAttribute("course", courseService.findById(courseId, u.getUserId()));
        return "courses/form";
    }

    @PostMapping("/{courseId}/edit")
    public String update(@PathVariable int courseId,
                         @RequestParam String courseName,
                         @RequestParam(required = false) String courseCode,
                         @RequestParam(required = false) String lecturer,
                         @RequestParam(defaultValue = "0") int credits,
                         @RequestParam(required = false) String semester,
                         HttpSession session, Model model) {
        User u = currentUser(session);
        if (u == null) return "redirect:/login";
        Course c = new Course(u.getUserId(), courseName, courseCode, lecturer, credits, semester);
        c.setCourseId(courseId);
        String err = courseService.update(c);
        if (err != null) { model.addAttribute("error", err); model.addAttribute("course", c); return "courses/form"; }
        return "redirect:/courses?msg=updated";
    }

    @PostMapping("/{courseId}/delete")
    public String delete(@PathVariable int courseId, HttpSession session) {
        User u = currentUser(session);
        if (u == null) return "redirect:/login";
        courseService.delete(courseId, u.getUserId());
        return "redirect:/courses?msg=deleted";
    }
}
