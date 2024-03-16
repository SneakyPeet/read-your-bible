# Read your bible web app

https://read-the-bible.web.app/

# firebase

## emulators

remember -add project-id

## deploy

npx firebase deploy --only firestore:rules

npx firebase deploy --only hosting --project bible-reading-plan

npx shadow-cljs release web && cd firebase && npx firebase deploy --only hosting --project bible-reading-plan && cd ..

npx shadow-cljs release functions && cd firebase && firebase deploy --only functions && cd ..

# Shaddow CLJS

## compile a build once and exit
$ npx shadow-cljs compile web

## compile and watch
$ npx shadow-cljs watch web

$ npx shadow-cljs watch functions

## connect to REPL for the build (available while watch is running)
$ npx shadow-cljs cljs-repl web

## connect to standalone node repl
$ npx shadow-cljs node-repl

## Running a release build optimized for production use
$ npx shadow-cljs release web

## Release debugging commands.
$ shadow-cljs check web
$ shadow-cljs release web --debug
