# Shadow CLJS Firebase Start Project

WORK IN PROGRESS

https://clojurescript.org/guides/promise-interop#using-promises-with-core-async

# firebase

## emulators

remember -add project-id

## deploy

npx firebase deploy --only firestore:rules

npx firebase deploy --only hosting --project bible-reading-plan

# Shaddow CLJS

## compile a build once and exit
$ npx shadow-cljs compile web

## compile and watch
$ npx shadow-cljs watch web

## connect to REPL for the build (available while watch is running)
$ npx shadow-cljs cljs-repl web

## connect to standalone node repl
$ npx shadow-cljs node-repl

## Running a release build optimized for production use
$ npx shadow-cljs release web

## Release debugging commands.
$ shadow-cljs check web
$ shadow-cljs release web --debug
