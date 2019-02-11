# rocky

This is my RockStar interpreter in Java, have fun and play with it.

Features include:
* Fully compliant Rockstar implementation to date (all test are OK)
* Test run mode (specify a test directory and run all tests within)
* Interactive terminal mode (REPL - Read - Eval - Print Loop)
* List command (parse a file without running it)

It requires at least Java8 JRE to run. The `rockstar.bat` and `rockstar` wrappers makes it easy to execute on Windows and Unix.

### Install

To install and run, you only need the `dist/rocky.jar` file, and one of the wrappers (`rockstar.bat` or `rockstar`). Everythin else is for development.

### Usage
```
$ ./rockstar
Rockstar Java by gaborsch, Version 0.99
---------------------------------------
Usage:
rockstar <filename> ...
rockstar run [--options ...] <filename> ...
    Execute a program. Input is taken from standard input, output is printed to standard output.
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
    Print help about the command.
```

Each command has a more detailed help with options, so try `rockstar help run`, `rockstar help test`, `rockstar help repl`, etc.


### Test results:
```
$ ./rockstar test programs/tests
CORRECT tests in programs\tests\correct
   [ OK ] apostrophesIgnored.rock
   [ OK ] factorial.rock
   [ OK ] fibonacci.rock
   [ OK ] fizzbuzz-idiomatic.rock
   [ OK ] fizzbuzz-minimalist.rock
   [ OK ] functionCalls.rock
   [ OK ] globalVariables.rock
   [ OK ] incrementAndDecrement.rock
   [ OK ] inputTest.rock
   [ OK ] inputTest2.rock
   [ OK ] literalAliases.rock
   [ OK ] literalstrings.rock
   [ OK ] loopControl.rock
CORRECT tests in programs\tests\correct\operators
   [ OK ] addOperator.rock
   [ OK ] andTest.rock
   [ OK ] divisionOperator.rock
   [ OK ] equalityComparison.rock
   [ OK ] multiplicationOperator.rock
   [ OK ] notTest.rock
   [ OK ] orderingComparison.rock
   [ OK ] orNorTest.rock
   [ OK ] subtractOperator.rock
   [ OK ] poeticLiterals.rock
   [ OK ] poeticNumbers.rock
   [ OK ] poeticStrings.rock
   [ OK ] pronouns.rock
   [ OK ] simpleComments.rock
   [ OK ] simpleConditionals.rock
   [ OK ] simpleFunctions.rock
   [ OK ] simpleLoops.rock
   [ OK ] truthinessTest.rock
   [ OK ] umlauts.rock
PARSE_ERROR tests in programs\tests\parse-errors
   [ OK ] apostropheWithoutSpace.rock
   [ OK ] apostropheWithSpaceBefore.rock
   [ OK ] breakOutsideLoop.rock
   [ OK ] continueOutsideLoop.rock
   [ OK ] elseOutsideIf.rock
   [ OK ] invalidPoeticLiteralAssignment.rock
   [ OK ] manyErrors.rock
   [ OK ] returnOutsideFunction.rock
   [ OK ] twoElses.rock
RUNTIME_ERROR tests in programs\tests\runtime-errors
   [ OK ] nestedError.rock
RUNTIME_ERROR tests in programs\tests\runtime-errors\notCallable
   [ OK ] notCallable1.rock
   [ OK ] notCallable2.rock
   [ OK ] notCallable3.rock
   [ OK ] notCallable4.rock
   [ OK ] notCallable5.rock
   [ OK ] wrongNumberOfArguments.rock
```


