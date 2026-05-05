package com.nhom14.controller;

import com.nhom14.model.Task;
import com.nhom14.model.User;
import com.nhom14.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    @Autowired private TaskService taskService;

    private User currentUser(HttpSession s) {
        return (User) s.getAttribute("user");
    }

    @GetMapping
    public String list(@RequestParam(required = false) String status,
                       @RequestParam(required = false) String priority,
                       HttpSession session, Model model) {
        User u = currentUser(session);
        if (u == null) return "redirect:/login";

        List<Task> tasks = taskService.findByUser(u.getUserId());

        // Lọc theo status nếu có
        if (status != null && !status.isBlank() && !"ALL".equals(status)) {
            tasks = tasks.stream()
                    .filter(t -> status.equals(t.getStatus()))
                    .collect(Collectors.toList());
        }

        // Lọc theo priority nếu có
        if (priority != null && !priority.isBlank() && !"ALL".equals(priority)) {
            tasks = tasks.stream()
                    .filter(t -> priority.equals(t.getPriority()))
                    .collect(Collectors.toList());
        }

        model.addAttribute("tasks", tasks);
        model.addAttribute("status", status);
        model.addAttribute("priority", priority);
        return "tasks/list";
    }

    @GetMapping("/{taskId}/edit")
    public String editForm(@PathVariable int taskId, HttpSession session, Model model) {
        User u = currentUser(session);
        if (u == null) return "redirect:/login";
        Task t = taskService.findById(taskId);
        if (t == null) return "redirect:/tasks";
        
        model.addAttribute("task", t);
        return "tasks/form";
    }

    @PostMapping("/{taskId}/edit")
    public String update(@PathVariable int taskId,
                         @RequestParam String taskName,
                         @RequestParam(required = false) String description,
                         @RequestParam String deadline,
                         @RequestParam String priority,
                         @RequestParam(required = false, defaultValue = "/tasks") String redirectUrl,
                         HttpSession session, Model model) {
        if (currentUser(session) == null) return "redirect:/login";
        
        Task t = new Task();
        t.setTaskId(taskId);
        t.setTaskName(taskName);
        t.setDescription(description);
        t.setDeadline(deadline);
        t.setPriority(priority);
        
        String err = taskService.update(t);
        if (err != null) {
            model.addAttribute("error", err);
            model.addAttribute("task", t);
            return "tasks/form";
        }
        return "redirect:" + redirectUrl + "?msg=updated";
    }

    @PostMapping("/{taskId}/status")
    public String updateStatus(@PathVariable int taskId,
                               @RequestParam String status,
                               @RequestParam(required = false, defaultValue = "/tasks") String redirectUrl,
                               HttpSession session) {
        if (currentUser(session) == null) return "redirect:/login";
        taskService.updateStatus(taskId, status);
        return "redirect:" + redirectUrl;
    }

    @PostMapping("/{taskId}/delete")
    public String delete(@PathVariable int taskId,
                         @RequestParam(required = false, defaultValue = "/tasks") String redirectUrl,
                         HttpSession session) {
        if (currentUser(session) == null) return "redirect:/login";
        taskService.delete(taskId);
        return "redirect:" + redirectUrl + "?msg=deleted";
    }
}
