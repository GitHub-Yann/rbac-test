package xyz.yann.rbac.demo.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import xyz.yann.rbac.demo.controller.mapper.DtoMapper;
import xyz.yann.rbac.demo.domain.MemberRoleEntity;
import xyz.yann.rbac.demo.dto.MemberRoleResponse;
import xyz.yann.rbac.demo.service.MemberRoleService;

@RestController
@RequestMapping("/api/v1/member-roles")
public class MemberRoleQueryController {

    private final MemberRoleService memberRoleService;

    public MemberRoleQueryController(MemberRoleService memberRoleService) {
        this.memberRoleService = memberRoleService;
    }

    @GetMapping
    public List<MemberRoleResponse> listMemberRoles(@RequestParam(value = "memberId", required = false) Long memberId) {
        List<MemberRoleEntity> entities = memberId == null
                ? memberRoleService.findAllAssignments()
                : memberRoleService.findByMemberId(memberId);
        return entities.stream().map(DtoMapper::toMemberRoleResponse).toList();
    }
}
