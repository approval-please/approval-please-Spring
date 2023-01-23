package com.umc.approval.domain.approval.dto;

import com.umc.approval.domain.approval.entity.Approval;
import com.umc.approval.domain.document.entity.Document;
import com.umc.approval.domain.user.entity.User;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class ApprovalDTO {

    // 타 게시글 승인/반려 request
    @Data
    public static class PostOtherApprovalRequest{
        @NotNull(message = "결재 서류에 대한 [승인/반려] 선택은 필수 값입니다.")
        Boolean isApprove;

        public Approval toEntity(User user, Document document){
            return Approval.builder()
                    .user(user)
                    .document(document)
                    .isApprove(isApprove)
                    .build();
        }
    }

    // 내 게시글 승인/반려 request
    @Data
    public static class PostMyApprovalRequest{
        @NotNull(message = "결재서류의 id는 필수 값입니다.")
        Long documentId;

        @NotNull(message = "결재서류에 대한 [승인/반려] 선택은 필수 값입니다.")
        Boolean isApprove;
    }

    // 타 게시글 승인/반려 response
    @Data
    public static class PostOtherApprovalResponse{
        Integer approveCount;
        Integer rejectCount;

        public PostOtherApprovalResponse(int approveCount, int rejectCount){
            this.approveCount = approveCount;
            this.rejectCount = rejectCount;
        }
    }

}
