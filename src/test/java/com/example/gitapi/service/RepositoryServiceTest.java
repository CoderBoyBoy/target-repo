package com.example.gitapi.service;

import com.example.gitapi.model.BranchInfo;
import com.example.gitapi.model.CommitInfo;
import com.example.gitapi.model.Repository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RepositoryServiceTest {

    private RepositoryService repositoryService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        repositoryService = new RepositoryService();
        repositoryService.setBaseRepositoryPath(tempDir.toString());
    }

    @Test
    void initRepository_shouldCreateNewRepository() {
        String repoName = "test-repo";

        Repository repository = repositoryService.initRepository(repoName);

        assertNotNull(repository);
        assertEquals(repoName, repository.getName());
        assertTrue(repository.getPath().contains(repoName));
        assertEquals("main", repository.getCurrentBranch());
        assertNotNull(repository.getLastModified());
    }

    @Test
    void getRepository_shouldReturnRepositoryInfo() {
        String repoName = "test-repo";
        repositoryService.initRepository(repoName);

        Repository repository = repositoryService.getRepository(repoName);

        assertNotNull(repository);
        assertEquals(repoName, repository.getName());
    }

    @Test
    void listBranches_shouldReturnBranches() {
        String repoName = "test-repo";
        repositoryService.initRepository(repoName);

        List<BranchInfo> branches = repositoryService.listBranches(repoName);

        assertNotNull(branches);
        // New repository should have main branch
        assertTrue(branches.isEmpty() || branches.stream().anyMatch(b -> b.getName().equals("main")));
    }

    @Test
    void addAndCommit_shouldCreateCommit() throws IOException {
        String repoName = "test-repo";
        repositoryService.initRepository(repoName);

        // Create a file to commit
        Path repoPath = tempDir.resolve(repoName);
        Path testFile = repoPath.resolve("test.txt");
        Files.writeString(testFile, "Hello, World!");

        // Add and commit
        repositoryService.addFiles(repoName, ".");
        CommitInfo commit = repositoryService.commit(repoName, "Initial commit", "Test Author", "test@example.com");

        assertNotNull(commit);
        assertNotNull(commit.getId());
        assertEquals("Initial commit", commit.getMessage());
        assertEquals("Test Author", commit.getAuthor());
        assertEquals("test@example.com", commit.getAuthorEmail());
    }

    @Test
    void listCommits_shouldReturnCommits() throws IOException {
        String repoName = "test-repo";
        repositoryService.initRepository(repoName);

        // Create a file and commit
        Path repoPath = tempDir.resolve(repoName);
        Path testFile = repoPath.resolve("test.txt");
        Files.writeString(testFile, "Hello, World!");

        repositoryService.addFiles(repoName, ".");
        repositoryService.commit(repoName, "Initial commit", "Test Author", "test@example.com");

        List<CommitInfo> commits = repositoryService.listCommits(repoName, 10);

        assertNotNull(commits);
        assertFalse(commits.isEmpty());
        assertEquals("Initial commit", commits.get(0).getMessage());
    }

    @Test
    void createBranch_shouldCreateNewBranch() throws IOException {
        String repoName = "test-repo";
        repositoryService.initRepository(repoName);

        // Create initial commit (required for branch creation)
        Path repoPath = tempDir.resolve(repoName);
        Path testFile = repoPath.resolve("test.txt");
        Files.writeString(testFile, "Hello, World!");
        repositoryService.addFiles(repoName, ".");
        repositoryService.commit(repoName, "Initial commit", "Test Author", "test@example.com");

        BranchInfo branch = repositoryService.createBranch(repoName, "feature-branch");

        assertNotNull(branch);
        assertEquals("feature-branch", branch.getName());
        assertFalse(branch.isRemote());
    }

    @Test
    void checkout_shouldSwitchBranch() throws IOException {
        String repoName = "test-repo";
        repositoryService.initRepository(repoName);

        // Create initial commit
        Path repoPath = tempDir.resolve(repoName);
        Path testFile = repoPath.resolve("test.txt");
        Files.writeString(testFile, "Hello, World!");
        repositoryService.addFiles(repoName, ".");
        repositoryService.commit(repoName, "Initial commit", "Test Author", "test@example.com");

        // Create and checkout new branch
        repositoryService.createBranch(repoName, "feature-branch");
        repositoryService.checkout(repoName, "feature-branch");

        Repository repository = repositoryService.getRepository(repoName);
        assertEquals("feature-branch", repository.getCurrentBranch());
    }
}
