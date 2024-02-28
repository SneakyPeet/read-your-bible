# Shadow CLJS Firebase Start Project

WORK IN PROGRESS

https://clojurescript.org/guides/promise-interop#using-promises-with-core-async

# firebase

## emulators

remember -add project-id

# Shaddow CLJS

## compile a build once and exit
$ npx shadow-cljs compile app

## compile and watch
$ npx shadow-cljs watch app

## connect to REPL for the build (available while watch is running)
$ npx shadow-cljs cljs-repl app

## connect to standalone node repl
$ npx shadow-cljs node-repl

## Running a release build optimized for production use
$ npx shadow-cljs release app

## Release debugging commands.
$ shadow-cljs check app
$ shadow-cljs release app --debug
