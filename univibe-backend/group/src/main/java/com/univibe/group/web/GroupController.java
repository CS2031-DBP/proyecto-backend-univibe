package com.univibe.group.web;

import com.univibe.group.model.Group;
import com.univibe.group.repo.GroupRepository;
import com.univibe.user.model.User;
import com.univibe.user.repo.UserRepository;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    public GroupController(GroupRepository groupRepository, UserRepository userRepository) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
    }

    @PostMapping
    public Group create(@RequestParam @NotBlank String name, @RequestParam Long ownerId) {
        User owner = userRepository.findById(ownerId).orElseThrow();
        Group g = new Group();
        g.setName(name);
        g.setOwner(owner);
        g.getMembers().add(owner);
        return groupRepository.save(g);
    }

    @PostMapping("/{groupId}/join")
    public Map<String, Object> join(@PathVariable Long groupId, @RequestParam Long userId) {
        Group g = groupRepository.findById(groupId).orElseThrow();
        User u = userRepository.findById(userId).orElseThrow();
        g.getMembers().add(u);
        groupRepository.save(g);
        return Map.of("members", g.getMembers().size());
    }
}
