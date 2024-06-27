# turfgame-resources

Utilities to download Turf-related statistics from different resources

## To Download Feeds

- Check out version to build (`git log --oneline` to find tag, `git checkout v1.1.0` to checkout actual tag).
- Build package (`mvn clean install`)
- Update feed startup scripts (`bin/feedsv4_startup.bat` and `bin/feedsv5_startup.bat`) to point to correct paths and
  resources.
- Install startup scripts in Windows startup-folder (Win-R `shell:startup`).
