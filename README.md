# rocky

This is my RockStar interpreter in Java, have fun and play with it. 
I like to receive feedbacks, should you have any issues, requests, wishes or troubles, open a ticket for it and I'll help you to resolve. Contributors are also welcome!

Features include:
* (Almost) fully compliant Rockstar implementation to date (89% of the new test are OK)
* Debugger mode (Step into/over/return/run, Breakpoints, watches, examine variable)
* Interactive terminal mode (REPL - Read - Eval - Print Loop)
* List command (parse a file without running it)
* Detailed help with options explanation

It requires at least Java8 JRE to run. The `rockstar.bat` and `rockstar` wrappers make it easy to execute on Windows and Unix.

### Install

To install and run, you only need the `./rocky.jar` file, and one of the wrappers (`rockstar.bat` or `rockstar`, depending on your OS). Everything else is for development.

### Usage
```
$ ./rockstar
Rockstar Java by gaborsch, Version 0.99
---------------------------------------
Usage:
rockstar <filename> ...
rockstar run [--options ...] <filename> ...
    Execute a program. Input is taken from standard input, output is printed to standard output.
rockstar debug [--options ...] <filename> ...
    Debug a program interactively. Stop at breakpoints, lines, display and watch variables.
rockstar list [--options ...] <filename> ...
    Parse and list a program. Useful for syntax checking.
rockstar - [<filename> ...]
rockstar repl [<filename> ...]
    Start an interactive session (Read-Evaluate-Print Loop). Enter commands and execute them immediately.
    The specified programs are pre-run (e.g. defining functions, etc). Special commands are available.
rockstar test [--options ...] <filename> ...
rockstar test --testdir <directoryname>
    Execute unit tests. Special rules apply, check `rockstar help test` for details
rockstar [-h|--help]
rockstar help
    Print this help.
rockstar help <command>
    Print more detailed help about the given command.
```

Each command has a more detailed help with options, so try `rockstar help run`, `rockstar help debug`, `rockstar help repl`, etc.



