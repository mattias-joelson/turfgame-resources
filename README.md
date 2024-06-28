# turfgame-resources

Utilities to download Turf-related statistics from different resources

## To Download Feeds

- Check out version to build (`git log --oneline` to find tag, `git checkout v1.2.0` to checkout actual tag).
- Build package (`mvn clean install`)
- Update feed downloader script (`bin/feedsv_downloader.bat`) to point to correct paths and resources.
- Install startup scripts in Windows startup-folder (Win-R `shell:startup`).
- Start script manually (`bin/feedsv_downloader.bat`) or restart computer (to verify setup).
