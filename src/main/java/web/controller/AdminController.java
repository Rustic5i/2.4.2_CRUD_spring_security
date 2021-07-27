package web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import web.dao.DAO;
import web.model.Role;
import web.model.User;
import web.myExcetion.SaveObjectException;
import web.service.IUserService;

import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/")
public class AdminController {

    private IUserService userService;

    @Autowired
    public AdminController(IUserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "hello", method = RequestMethod.GET)
    public String printWelcome(ModelMap model) {
        List<String> messages = new ArrayList<>();
        messages.add("Hello!");
        messages.add("I'm Spring MVC-SECURITY application");
        messages.add("5.2.0 version by sep'19 ");
        model.addAttribute("messages", messages);
        return "hello";
    }

    @RequestMapping(value = "login", method = RequestMethod.GET)
    public String loginPage() {
        return "login";
    }
    ////*******************************************************************

    @ModelAttribute("newUser")
    public User getPerson() {
        return new User();
    }

    @GetMapping("/admin")
    public String index(Model model) {
        model.addAttribute("people", userService.getAllUsers());
        return "view/index";
    }

    @PostMapping("/admin")
    public String creat(@ModelAttribute("newUser") @Valid User user,
                        BindingResult bindingResult, Model model,
                        @RequestParam(name = "listRoles[]", required = false) String... roles) {
         model.addAttribute("people", userService.getAllUsers());
        if (bindingResult.hasErrors()) {
            return "view/index";
        }
        try {
            Set<Role> roleSet = userService.getSetRoles(roles);
            user.setRoles(roleSet);
            userService.registrationUser(user);
        } catch (SaveObjectException e) {
            e.getMessage();
            bindingResult.rejectValue("username", "SaveObjectException",
                    "Exception: The user with the name " + user.getUsername() + " already exists");
            return "view/index";
        }
        return "redirect:/admin";
    }

    @DeleteMapping("/admin/{id}")
    public String deletePerson(@PathVariable("id") Long id) {
        userService.removeUserById(id);
        return "redirect:/admin";
    }

    @GetMapping("/admin/{id}/edit")
    public String edit(@ModelAttribute("id") Long id, Model model) {
        model.addAttribute("user", userService.getUserById(id));
        return "view/edit";
    }

    @PatchMapping("/admin/{id}")
    public String updatePerson(@ModelAttribute("user") @Valid User updateuser,
                               BindingResult bindingResult,
                               @RequestParam(name = "listRoles[]", required = false) String... roles) {
        if (bindingResult.hasErrors()) {
            return "view/edit";
        }
        try {
            Set<Role> roleSet = userService.getSetRoles(roles);
            updateuser.setRoles(roleSet);
            userService.updateUser(updateuser);
        } catch (SaveObjectException e) {
            e.getMessage();
            bindingResult.rejectValue("username", "SaveObjectException",
                    "Exception: The user with the name " + updateuser.getUsername() + " already exists");
            return "view/edit";
        }
        return "redirect:/admin";
    }
}