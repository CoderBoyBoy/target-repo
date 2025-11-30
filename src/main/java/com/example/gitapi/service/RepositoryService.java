package com.example.gitapi.service;

import com.example.gitapi.exception.GitOperationException;
import com.example.gitapi.model.BranchInfo;
import com.example.gitapi.model.CommitInfo;
import com.example.gitapi.model.Repository;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for performing Git operations using JGit.
 */
@Service
public class RepositoryService {

    @Value("${git.repositories.base-path:/tmp/repositories}")
    private String baseRepositoryPath;

    /**
     * Initialize a new Git repository.
     *
     * @param name the name of the repository
     * @return the created Repository
     */
    public Repository initRepository(String name) {
        Path repoPath = Path.of(baseRepositoryPath, name);

        try {
            Files.createDirectories(repoPath);
            Git.init().setDirectory(repoPath.toFile()).setInitialBranch("main").call();

            Repository repository = new Repository(name, repoPath.toString());
            repository.setCurrentBranch("main");
            repository.setLastModified(LocalDateTime.now());

            return repository;
        } catch (IOException | GitAPIException e) {
            throw new GitOperationException("Failed to initialize repository: " + name, e);
        }
    }

    /**
     * Clone a remote Git repository.
     *
     * @param remoteUrl the URL of the remote repository
     * @param name      the name for the local repository
     * @return the cloned Repository
     */
    public Repository cloneRepository(String remoteUrl, String name) {
        Path repoPath = Path.of(baseRepositoryPath, name);

        try {
            Files.createDirectories(repoPath);
            Git git = Git.cloneRepository()
                    .setURI(remoteUrl)
                    .setDirectory(repoPath.toFile())
                    .call();

            String branch = git.getRepository().getBranch();
            git.close();

            Repository repository = new Repository(name, repoPath.toString());
            repository.setCurrentBranch(branch);
            repository.setLastModified(LocalDateTime.now());

            return repository;
        } catch (IOException | GitAPIException e) {
            throw new GitOperationException("Failed to clone repository: " + remoteUrl, e);
        }
    }

    /**
     * Get repository information.
     *
     * @param name the name of the repository
     * @return the Repository information
     */
    public Repository getRepository(String name) {
        Path repoPath = Path.of(baseRepositoryPath, name);
        File repoDir = repoPath.toFile();

        if (!repoDir.exists()) {
            throw new GitOperationException("Repository not found: " + name);
        }

        try (Git git = Git.open(repoDir)) {
            String branch = git.getRepository().getBranch();

            Repository repository = new Repository(name, repoPath.toString());
            repository.setCurrentBranch(branch);
            repository.setLastModified(LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(repoDir.lastModified()), ZoneId.systemDefault()));

            return repository;
        } catch (IOException e) {
            throw new GitOperationException("Failed to open repository: " + name, e);
        }
    }

    /**
     * List all branches in a repository.
     *
     * @param name the name of the repository
     * @return list of branches
     */
    public List<BranchInfo> listBranches(String name) {
        Path repoPath = Path.of(baseRepositoryPath, name);

        try (Git git = Git.open(repoPath.toFile())) {
            List<BranchInfo> branches = new ArrayList<>();

            // Local branches
            List<Ref> localBranches = git.branchList().call();
            for (Ref ref : localBranches) {
                String branchName = ref.getName().replace("refs/heads/", "");
                String commitId = ref.getObjectId() != null ? ref.getObjectId().getName() : "";
                branches.add(new BranchInfo(branchName, commitId, false));
            }

            return branches;
        } catch (IOException | GitAPIException e) {
            throw new GitOperationException("Failed to list branches for repository: " + name, e);
        }
    }

    /**
     * Create a new branch in a repository.
     *
     * @param name       the name of the repository
     * @param branchName the name of the new branch
     * @return the created BranchInfo
     */
    public BranchInfo createBranch(String name, String branchName) {
        Path repoPath = Path.of(baseRepositoryPath, name);

        try (Git git = Git.open(repoPath.toFile())) {
            Ref ref = git.branchCreate().setName(branchName).call();
            String commitId = ref.getObjectId() != null ? ref.getObjectId().getName() : "";

            return new BranchInfo(branchName, commitId, false);
        } catch (IOException | GitAPIException e) {
            throw new GitOperationException("Failed to create branch: " + branchName, e);
        }
    }

    /**
     * List commits in a repository.
     *
     * @param name  the name of the repository
     * @param limit maximum number of commits to return
     * @return list of commits
     */
    public List<CommitInfo> listCommits(String name, int limit) {
        Path repoPath = Path.of(baseRepositoryPath, name);

        try (Git git = Git.open(repoPath.toFile())) {
            List<CommitInfo> commits = new ArrayList<>();

            Iterable<RevCommit> log = git.log().setMaxCount(limit).call();
            for (RevCommit commit : log) {
                LocalDateTime timestamp = LocalDateTime.ofInstant(
                        Instant.ofEpochSecond(commit.getCommitTime()), ZoneId.systemDefault());

                commits.add(new CommitInfo(
                        commit.getName(),
                        commit.getFullMessage(),
                        commit.getAuthorIdent().getName(),
                        commit.getAuthorIdent().getEmailAddress(),
                        timestamp));
            }

            return commits;
        } catch (IOException | GitAPIException e) {
            throw new GitOperationException("Failed to list commits for repository: " + name, e);
        }
    }

    /**
     * Add files to the staging area.
     *
     * @param name        the name of the repository
     * @param filePattern the file pattern to add (e.g., "." for all files)
     */
    public void addFiles(String name, String filePattern) {
        Path repoPath = Path.of(baseRepositoryPath, name);

        try (Git git = Git.open(repoPath.toFile())) {
            git.add().addFilepattern(filePattern).call();
        } catch (IOException | GitAPIException e) {
            throw new GitOperationException("Failed to add files to repository: " + name, e);
        }
    }

    /**
     * Commit staged changes.
     *
     * @param name    the name of the repository
     * @param message the commit message
     * @param author  the author name
     * @param email   the author email
     * @return the created CommitInfo
     */
    public CommitInfo commit(String name, String message, String author, String email) {
        Path repoPath = Path.of(baseRepositoryPath, name);

        try (Git git = Git.open(repoPath.toFile())) {
            RevCommit commit = git.commit()
                    .setMessage(message)
                    .setAuthor(author, email)
                    .call();

            LocalDateTime timestamp = LocalDateTime.ofInstant(
                    Instant.ofEpochSecond(commit.getCommitTime()), ZoneId.systemDefault());

            return new CommitInfo(
                    commit.getName(),
                    commit.getFullMessage(),
                    commit.getAuthorIdent().getName(),
                    commit.getAuthorIdent().getEmailAddress(),
                    timestamp);
        } catch (IOException | GitAPIException e) {
            throw new GitOperationException("Failed to commit changes to repository: " + name, e);
        }
    }

    /**
     * Checkout a branch.
     *
     * @param name       the name of the repository
     * @param branchName the name of the branch to checkout
     */
    public void checkout(String name, String branchName) {
        Path repoPath = Path.of(baseRepositoryPath, name);

        try (Git git = Git.open(repoPath.toFile())) {
            git.checkout().setName(branchName).call();
        } catch (IOException | GitAPIException e) {
            throw new GitOperationException("Failed to checkout branch: " + branchName, e);
        }
    }

    /**
     * Get the base repository path.
     *
     * @return the base repository path
     */
    public String getBaseRepositoryPath() {
        return baseRepositoryPath;
    }

    /**
     * Set the base repository path.
     *
     * @param baseRepositoryPath the base repository path
     */
    public void setBaseRepositoryPath(String baseRepositoryPath) {
        this.baseRepositoryPath = baseRepositoryPath;
    }
}
