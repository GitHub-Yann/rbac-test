package xyz.yann.rbac.demo.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import xyz.yann.rbac.demo.controller.mapper.DtoMapper;
import xyz.yann.rbac.demo.dto.MemberRoleAssignmentRequest;
import xyz.yann.rbac.demo.dto.MemberRoleResponse;
import xyz.yann.rbac.demo.service.MemberRoleService;

@RestController
@RequestMapping("/api/v1/members/{memberId}/roles/{roleId}")
public class MemberRoleController {

    private final MemberRoleService memberRoleService;

    public MemberRoleController(MemberRoleService memberRoleService) {
        this.memberRoleService = memberRoleService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MemberRoleResponse assignRole(@PathVariable Long memberId,
                                         @PathVariable Long roleId,
                                         @RequestBody(required = false) @Valid MemberRoleAssignmentRequest request) {
        MemberRoleAssignmentRequest payload = request == null ? new MemberRoleAssignmentRequest() : request;
        return DtoMapper.toMemberRoleResponse(memberRoleService.assignRole(memberId, roleId, payload));
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void revokeRole(@PathVariable Long memberId, @PathVariable Long roleId) {
        memberRoleService.revokeRole(memberId, roleId);
    }
}
