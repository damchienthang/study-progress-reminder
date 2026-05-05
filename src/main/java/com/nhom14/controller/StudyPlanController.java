package com.nhom14.controller;

import com.nhom14.model.StudyPlan;
import com.nhom14.model.Task;
import com.nhom14.model.User;
import com.nhom14.service.CourseService;
import com.nhom14.service.StudyPlanService;
import com.nhom14.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/plans")
public class StudyPlanController {

    @Autowired private StudyPlanService planService;
    @Autowired private TaskService      taskService;
    @Autowired private CourseService    courseService;

    private User currentUser(HttpSession s) { return (User) s.getAttribute("user"); }

    @GetMapping
    public String list(HttpSession session, Model model) {
        User u = currentUser(session);
        if (u == null) return "redirect:/login";
        model.addAttribute("plans", planService.findByUser(u.getUserId()));
        return "plans/list";
    }

    @GetMapping("/new")
    public String newForm(HttpSession session, Model model) {
        User u = currentUser(session);
        if (u == null) return "redirect:/login";
        model.addAttribute("courses", courseService.findByUser(u.getUserId()));
        return "plans/form";
    }

    @PostMapping("/new")
    public String create(@RequestParam String planName,
                         @RequestParam(required = false) Integer courseId,
                         @RequestParam String startDate,
                         @RequestParam String endDate,
                         HttpSession session, Model model) {
        User u = currentUser(session);
        if (u == null) return "redirect:/login";
        StudyPlan p = new StudyPlan(u.getUserId(), planName, startDate, endDate);
        p.setCourseId(courseId);
        String err = planService.create(p);
        if (err != null) {
            model.addAttribute("error", err);
            model.addAttribute("courses", courseService.findByUser(u.getUserId()));
            return "plans/form";
        }
        return "redirect:/plans?msg=created";
    }

    @GetMapping("/{planId}/edit")
    public String editForm(@PathVariable int planId, HttpSession session, Model model) {
        User u = currentUser(session);
        if (u == null) return "redirect:/login";
        StudyPlan p = planService.findById(planId, u.getUserId());
        if (p == null) return "redirect:/plans";
        model.addAttribute("plan", p);
        model.addAttribute("courses", courseService.findByUser(u.getUserId()));
        return "plans/form";
    }

    @PostMapping("/{planId}/edit")
    public String update(@PathVariable int planId,
                         @RequestParam String planName,
                         @RequestParam(required = false) Integer courseId,
                         @RequestParam String startDate,
                         @RequestParam String endDate,
                         HttpSession session, Model model) {
        User u = currentUser(session);
        if (u == null) return "redirect:/login";
        StudyPlan p = new StudyPlan(u.getUserId(), planName, startDate, endDate);
        p.setPlanId(planId);
        p.setCourseId(courseId);
        String err = planService.update(p);
        if (err != null) {
            model.addAttribute("error", err);
            model.addAttribute("plan", p);
            model.addAttribute("courses", courseService.findByUser(u.getUserId()));
            return "plans/form";
        }
        return "redirect:/plans?msg=updated";
    }

    @GetMapping("/{planId}")
    public String detail(@PathVariable int planId, HttpSession session, Model model) {
        User u = currentUser(session);
        if (u == null) return "redirect:/login";
        StudyPlan p = planService.findById(planId, u.getUserId());
        if (p == null) return "redirect:/plans";
        model.addAttribute("plan", p);
        model.addAttribute("tasks", taskService.findByPlan(planId));
        model.addAttribute("courses", courseService.findByUser(u.getUserId()));
        return "plans/detail";
    }

    @PostMapping("/{planId}/tasks/new")
    public String addTask(@PathVariable int planId,
                          @RequestParam String taskName,
                          @RequestParam(required = false) String description,
                          @RequestParam(required = false) Integer courseId,
                          @RequestParam String deadline,
                          @RequestParam(defaultValue = "MEDIUM") String priority,
                          HttpSession session, Model model) {
        User u = currentUser(session);
        if (u == null) return "redirect:/login";
        Task t = new Task();
        t.setPlanId(planId);
        t.setCourseId(courseId);
        t.setTaskName(taskName);
        t.setDescription(description);
        t.setDeadline(deadline);
        t.setPriority(priority);
        String err = taskService.create(t, u.getUserId());
        if (err != null) {
            StudyPlan p = planService.findById(planId, u.getUserId());
            model.addAttribute("plan", p);
            model.addAttribute("tasks", taskService.findByPlan(planId));
            model.addAttribute("courses", courseService.findByUser(u.getUserId()));
            model.addAttribute("error", err);
            return "plans/detail";
        }
        return "redirect:/plans/" + planId + "?msg=taskAdded";
    }

    @PostMapping("/{planId}/delete")
    public String delete(@PathVariable int planId, HttpSession session) {
        User u = currentUser(session);
        if (u == null) return "redirect:/login";
        planService.delete(planId, u.getUserId());
        return "redirect:/plans?msg=deleted";
    }
}
