# SystemDesignProject-Team2

## contributing

# git workflow

The Gitflow Workflow defines a strict branching model designed around the project release. This workflow doesn’t add any new concepts or commands beyond what’s required for the Feature Branch Workflow. Instead, it assigns very specific roles to different branches and defines how and when they should interact.

Te key idea is that all feature development should take place in dedicated branches instead of the master branch. This encapsulation makes it easy for multiple developers to work on a particular feature without disturbing the main codebase.
It also means the master branch will never contain broken code, which is a huge advantage for continuous integration environments.

# cloning
First, clone this repository on your computer. To check for any updates, use GitHub Desktop or, using the terminal, 
```
git fetch
git pull 
```
(only `git pull` if there are no conflicts with your local repository!)

# start with the master branch

All feature branches are created off the latest code state of a project. The latest code is maintained and updated in the master branch.

```
git checkout master
git fetch origin
git reset --hard origin/master

```

This switches the repo to the master branch, pulls the latest commits and resets the repo's local copy of master to match the latest version.

# creating a new development branch
The first time: 

After cloning the repository, list all of the branches in your repository by running the following command.
```
git branch

```
This should result in: `*master` (* indicates that you are currently on master) along with any other branches

Use a separate branch for each feature or issue you work on. After creating a branch, check it out locally so that any changes you make will be on that branch.

The following simultaneously creates and checks out <new-branch>.
  
```
git branch -b <new-branch> (you may name your branch in the following way: your-initials/what's on the branch)

```

However, if you want to check out to an exisiting branch, simply run this command:

```
git checkout <branchname>
```

# update, add, commit and push changes

The git status command displays the state of the working directory and the staging area. It lets you see which changes have been staged, which haven't, and which files aren't being tracked by Git. 
```
git status

```
A Git log is a running record of commits. A full log has the following pieces: A commit hash (SHA1 40 character checksum of the commits contents). Because it is generated based on the commit contents it is unique.

```
git log

```

Add files to the staging area 

```
git add <some-file>

```
Commit files to the local repository using the following command.
```
git commit -m <message>

```

# push feature branch to remote

It’s a good idea to push the feature branch up to the central repository. This serves as a convenient backup, when collaborating with other developers, this would give them access to view commits to the new branch.

```
git push -u origin <new-feature-branch-name>

```

# Resolve feedback

Now teammates comment and approve the pushed commits. Resolve their comments locally, commit, and push the suggested changes to GitHub. Your updates appear in the pull request.

# Merge your pull request

Before you merge, you may have to resolve merge conflicts if others have made changes to the repo. When your pull request is approved and conflict-free, you can add your code to the master branch.

# Resolving merge conflicts

While your feature branch with merge conflicts, run this command:

```
git pull origin master

```

It will result in merge conflicts, I personally like using p4mergetool (https://gist.github.com/dgoguerra/8258007) to deal with my merge conflicts.

Once the conflicts resolved, commit and push the changes.


Good luck!

