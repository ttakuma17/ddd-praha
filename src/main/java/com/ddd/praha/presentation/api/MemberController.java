package com.ddd.praha.presentation.api;

import com.ddd.praha.application.service.usecase.MemberService;
import com.ddd.praha.domain.Email;
import com.ddd.praha.domain.EnrollmentStatus;
import com.ddd.praha.domain.Member;
import com.ddd.praha.domain.MemberId;
import com.ddd.praha.domain.MemberName;
import com.ddd.praha.presentation.exception.ResourceNotFoundException;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 参加者コントローラー
 */
@RestController
@RequestMapping("/api/members")
public class MemberController {
    private final MemberService memberService;
    
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }
    
    /**
     * 全ての参加者を取得する
     * @return 参加者のリスト
     */
    @GetMapping
    public List<MemberResponse> getAllMembers() {
        List<Member> members = memberService.getAll();
        return members.stream()
                .map(MemberResponse::from)
                .collect(Collectors.toList());
    }
    
    /**
     * IDで参加者を検索する
     * @param id 参加者ID
     * @return 参加者情報
     */
    @GetMapping("/{id}")
    public MemberResponse findMemberById(@PathVariable String id) {
        return memberService.findById(new MemberId(id))
            .map(MemberResponse::from)
            .orElseThrow(() -> new ResourceNotFoundException("Member not found: " + id));
    }
    
    /**
     * 新しい参加者を追加する
     * @param request 参加者作成リクエスト
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createMember(@RequestBody MemberCreateRequest request) {
        memberService.addMember(
                new MemberName(request.getName()),
                new Email(request.getEmail()),
                EnrollmentStatus.valueOf(request.getStatus())
        );
    }
    
    /**
     * 参加者の在籍ステータスを更新する
     * @param id 参加者ID
     * @param request ステータス更新リクエスト
     */
    @PutMapping("/{id}/status")
    @ResponseStatus(HttpStatus.OK)
    public void updateMemberStatus(@PathVariable String id, @RequestBody MemberStatusUpdateRequest request) {
        memberService.updateMemberStatus(
                new MemberId(id),
                EnrollmentStatus.valueOf(request.getStatus())
        );
    }
}