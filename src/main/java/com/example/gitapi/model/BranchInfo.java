package com.example.gitapi.model;

/**
 * Domain model representing a Git branch.
 */
public class BranchInfo {

    private String name;
    private String lastCommitId;
    private boolean isRemote;

    public BranchInfo() {
    }

    public BranchInfo(String name, String lastCommitId, boolean isRemote) {
        this.name = name;
        this.lastCommitId = lastCommitId;
        this.isRemote = isRemote;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastCommitId() {
        return lastCommitId;
    }

    public void setLastCommitId(String lastCommitId) {
        this.lastCommitId = lastCommitId;
    }

    public boolean isRemote() {
        return isRemote;
    }

    public void setRemote(boolean remote) {
        isRemote = remote;
    }
}
