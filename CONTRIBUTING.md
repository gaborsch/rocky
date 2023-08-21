# Background

Originally Rocky was created as an alternative implementation for Rockstar language in Java. The original Rockstar specification included DEC64 arithmetics, but as later it was removed, this became the Rocky's first special addition. 
A few new additions came after: arrays (which later was aligned with the Rockstar 2.0 spec), and OOP. The latter is a huge topic, and only available in Rocky.
Some additions required breaking changes (e.g. the reserved keyword list is extended), this made it necessary to introduce the strict mode - so all standard Rockstar programs could be run as the language standard defined.

One of the main goals for Rocky is to help development in Rockstar. Rocky supports running, listing (parsing and explaining) and debugging Rockstar programs, and also REPL mode, to enable experimenting with Rockstar.

Rocky wants to be a superset of Rockstar language, but keeping backwards compatibilty. Any new feature can be incorporated

# How to contribute to Rocky?

If you find an incompatible feature:
* File a new issue, label as `compatibility-bug` provide a minimal example with the expected output. Also, please note the provided command line options (e.g. `-x` for enabling Rocky extensions).

If you want to add a feature:
* Open an issue, label as `enhancement`. Explain the desired feature, what's included and what's not. 

If you find an undesired feature or a bug:
* Open an issue, label as `bug`. Explain how to reproduce, what is the expected behaviour. Also, if possible, try it with different alternatives (e.g. extended mode on/off, different parameters, instructions, OSes, etc - whichever is relevant)

# Contributing

## Requirements

* JAVA JDK, at least Java8
* Maven ([Download here](https://maven.apache.org/download.cgi))

## Way of contribution to the code

For every filed issue, open a branch for the fix. When the fix is done
* run `mvn clean install` to prepare `rocky.jar` file
* make sure that all the tests are running (execute `./rockstar test -v programs/tests/`)
* if everything passes, open a Pull Request to merge to `master` branch

Should you have any questions, please contact `gaborsch` on [Rockstar Discord](https://discord.com/invite/xsQK7UU).




