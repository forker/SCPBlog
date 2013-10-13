# SCP Blog

## Standalone web blog server with publishing over SCP

### Start

`mvn install exec:java -Dexec.mainClass=sshblog.Main`

### Publish

`scp -P 2222 about_me_page.md me@localhost:about_me`

password is `4u`

### Check it out

point your browser at `http://localhost:4567/about_me`

## Why?

Sounded like fun..

## Known issues:

* Insufficient error handling
* Only in-memory storage

## Maybe TODO:

* Off the code launch configuration
* Configurable page template
* Page title wiring from the first header in markdown text
