package com.ddd.praha.presentation.api;

import com.ddd.praha.application.service.MemberService;
import com.ddd.praha.domain.Email;
import com.ddd.praha.domain.EnrollmentStatus;
import com.ddd.praha.domain.Member;
import com.ddd.praha.domain.MemberId;
import com.ddd.praha.domain.MemberName;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<MemberResponse>> getAllMembers() {
        List<Member> members = memberService.getAllMembers();
        List<MemberResponse> response = members.stream()
                .map(MemberResponse::fromDomain)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
    
    /**
     * IDで参加者を検索する
     * @param id 参加者ID
     * @return 参加者情報
     */
    @GetMapping("/{id}")
    public ResponseEntity<MemberResponse> getMemberById(@PathVariable String id) {
        return memberService.getMemberById(new MemberId(id))
                .map(member -> ResponseEntity.ok(MemberResponse.fromDomain(member)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 新しい参加者を追加する
     * @param request 参加者作成リクエスト
     * @return 作成された参加者情報
     */
    @PostMapping
    public ResponseEntity<MemberResponse> createMember(@RequestBody MemberCreateRequest request) {
        Member member = memberService.addMember(
                new MemberName(request.getName()),
                new Email(request.getEmail()),
                EnrollmentStatus.valueOf(request.getStatus())
        );
        return new ResponseEntity<>(MemberResponse.fromDomain(member), HttpStatus.CREATED);
    }
    
    /**
     * 参加者の在籍ステータスを更新する
     * @param id 参加者ID
     * @param request ステータス更新リクエスト
     * @return 更新された参加者情報
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<MemberResponse> updateMemberStatus(
            @PathVariable String id,
            @RequestBody MemberStatusUpdateRequest request) {
        try {
            Member member = memberService.updateMemberStatus(
                    new MemberId(id),
                    EnrollmentStatus.valueOf(request.getStatus())
            );
            return ResponseEntity.ok(MemberResponse.fromDomain(member));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}