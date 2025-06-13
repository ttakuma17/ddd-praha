package com.ddd.praha.presentation.api;

import com.ddd.praha.domain.MemberTask;
import com.ddd.praha.domain.Task;
import com.ddd.praha.domain.TaskStatus;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * 参加者課題レスポンス
 */
public class MemberTaskResponse {
    private MemberResponse owner;
    private Map<String, TaskStatusResponse> tasks;
    
    // デフォルトコンストラクタ（Jackson用）
    public MemberTaskResponse() {
    }
    
    public MemberTaskResponse(MemberResponse owner, Map<String, TaskStatusResponse> tasks) {
        this.owner = owner;
        this.tasks = tasks;
    }
    
    /**
     * ドメインオブジェクトからレスポンスオブジェクトを作成する
     * @param memberTask ドメインオブジェクト
     * @param tasks 課題のリスト
     * @return レスポンスオブジェクト
     */
    public static MemberTaskResponse from(MemberTask memberTask, Map<Task, TaskStatus> tasks) {
        MemberResponse ownerResponse = MemberResponse.from(memberTask.getOwner());
        
        Map<String, TaskStatusResponse> taskStatusMap = tasks.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().getId().value(),
                        entry -> new TaskStatusResponse(entry.getValue().name())
                ));
        
        return new MemberTaskResponse(ownerResponse, taskStatusMap);
    }
    
    public MemberResponse getOwner() {
        return owner;
    }
    
    public void setOwner(MemberResponse owner) {
        this.owner = owner;
    }
    
    public Map<String, TaskStatusResponse> getTasks() {
        return tasks;
    }
    
    public void setTasks(Map<String, TaskStatusResponse> tasks) {
        this.tasks = tasks;
    }
    
    /**
     * 課題ステータスレスポンス（内部クラス）
     */
    public static class TaskStatusResponse {
        private String status;
        
        public TaskStatusResponse() {
        }
        
        public TaskStatusResponse(String status) {
            this.status = status;
        }
        
        public String getStatus() {
            return status;
        }
        
        public void setStatus(String status) {
            this.status = status;
        }
    }
}