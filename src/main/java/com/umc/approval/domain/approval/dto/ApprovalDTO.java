package com.umc.approval.domain.approval.dto;

import com.umc.approval.domain.approval.entity.Approval;
import com.umc.approval.domain.document.entity.Document;
import com.umc.approval.domain.user.entity.User;
import lombok.Data;

public class ApprovalDTO {

    // 타 게시글 승인/반려 request
    @Data
    public static class PostOtherApprovalRequest{
        Boolean isApprove;

        public Approval toEntity(User user, Document document){
            return Approval.builder()
                    .user(user)
                    .document(document)
                    .isApprove(isApprove)
                    .build();
        }
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
