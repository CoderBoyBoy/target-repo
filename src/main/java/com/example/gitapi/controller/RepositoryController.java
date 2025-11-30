package com.example.gitapi.controller;

import com.example.gitapi.model.BranchInfo;
import com.example.gitapi.model.CommitInfo;
import com.example.gitapi.model.Repository;
import com.example.gitapi.service.RepositoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST API controller for Git repository operations.
 */
@RestController
@RequestMapping("/api/repositories")
public class RepositoryController {

    private final RepositoryService repositoryService;

    public RepositoryController(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }

    /**
     * Initialize a new Git repository.
     *
     * @param request the request containing repository name
     * @return the created repository
     */
    @PostMapping("/init")
    public ResponseEntity<Repository> initRepository(@RequestBody Map<String, String> request) {
        String name = request.get("name");
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Repository name is required");
        }
        Repository repository = repositoryService.initRepository(name);
        return ResponseEntity.status(HttpStatus.CREATED).body(repository);
    }

    /**
     * Clone a remote Git repository.
     *
     * @param request the request containing remote URL and repository name
     * @return the cloned repository
     */
    @PostMapping("/clone")
    public ResponseEntity<Repository> cloneRepository(@RequestBody Map<String, String> request) {
        String remoteUrl = request.get("url");
        String name = request.get("name");

        if (remoteUrl == null || remoteUrl.isBlank()) {
            throw new IllegalArgumentException("Remote URL is required");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Repository name is required");
        }

        Repository repository = repositoryService.cloneRepository(remoteUrl, name);
        return ResponseEntity.status(HttpStatus.CREATED).body(repository);
    }

    /**
     * Get repository information.
     *
     * @param name the repository name
     * @return the repository information
     */
    @GetMapping("/{name}")
    public ResponseEntity<Repository> getRepository(@PathVariable String name) {
        Repository repository = repositoryService.getRepository(name);
        return ResponseEntity.ok(repository);
    }

    /**
     * List all branches in a repository.
     *
     * @param name the repository name
     * @return list of branches
     */
    @GetMapping("/{name}/branches")
    public ResponseEntity<List<BranchInfo>> listBranches(@PathVariable String name) {
        List<BranchInfo> branches = repositoryService.listBranches(name);
        return ResponseEntity.ok(branches);
    }

    /**
     * Create a new branch.
     *
     * @param name    the repository name
     * @param request the request containing branch name
     * @return the created branch
     */
    @PostMapping("/{name}/branches")
    public ResponseEntity<BranchInfo> createBranch(@PathVariable String name, @RequestBody Map<String, String> request) {
        String branchName = request.get("branchName");
        if (branchName == null || branchName.isBlank()) {
            throw new IllegalArgumentException("Branch name is required");
        }
        BranchInfo branch = repositoryService.createBranch(name, branchName);
        return ResponseEntity.status(HttpStatus.CREATED).body(branch);
    }

    /**
     * List commits in a repository.
     *
     * @param name  the repository name
     * @param limit maximum number of commits to return
     * @return list of commits
     */
    @GetMapping("/{name}/commits")
    public ResponseEntity<List<CommitInfo>> listCommits(
            @PathVariable String name,
            @RequestParam(defaultValue = "10") int limit) {
        List<CommitInfo> commits = repositoryService.listCommits(name, limit);
        return ResponseEntity.ok(commits);
    }

    /**
     * Add files to the staging area.
     *
     * @param name    the repository name
     * @param request the request containing file pattern
     * @return success message
     */
    @PostMapping("/{name}/add")
    public ResponseEntity<Map<String, String>> addFiles(@PathVariable String name, @RequestBody Map<String, String> request) {
        String filePattern = request.getOrDefault("pattern", ".");
        repositoryService.addFiles(name, filePattern);
        return ResponseEntity.ok(Map.of("message", "Files added successfully"));
    }

    /**
     * Commit staged changes.
     *
     * @param name    the repository name
     * @param request the request containing commit details
     * @return the created commit
     */
    @PostMapping("/{name}/commit")
    public ResponseEntity<CommitInfo> commit(@PathVariable String name, @RequestBody Map<String, String> request) {
        String message = request.get("message");
        String author = request.get("author");
        String email = request.get("email");

        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Commit message is required");
        }
        if (author == null || author.isBlank()) {
            throw new IllegalArgumentException("Author name is required");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Author email is required");
        }

        CommitInfo commit = repositoryService.commit(name, message, author, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(commit);
    }

    /**
     * Checkout a branch.
     *
     * @param name    the repository name
     * @param request the request containing branch name
     * @return success message
     */
    @PostMapping("/{name}/checkout")
    public ResponseEntity<Map<String, String>> checkout(@PathVariable String name, @RequestBody Map<String, String> request) {
        String branchName = request.get("branchName");
        if (branchName == null || branchName.isBlank()) {
            throw new IllegalArgumentException("Branch name is required");
        }
        repositoryService.checkout(name, branchName);
        return ResponseEntity.ok(Map.of("message", "Checked out branch: " + branchName));
    }
}
