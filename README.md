# Rocky - Rockstar Java interpreter

Rocky is a 100% [Rockstar](https://codewithrockstar.com/) compatible RockStar Interpreter, written in Java. Have fun and play with it! I like to receive feedbacks, should you have any issues, requests, wishes or troubles, open a ticket for it and I'll help you to resolve. 

Features include:
* Fully compliant Rockstar implementation to date (all of the new test are OK, except the reported bugs)
* Advanced Debugger mode (Step into/over/return/run, Breakpoints, watches, examine variable, trace expression evaluation!)
* Interactive terminal mode (REPL - Read - Eval - Print Loop)
* List command (parse a file without running it)
* Detailed help with options explanation
* IEEE754 maths (double precision), or optionally Dec64 (with option `--dec64`)
* **NEW!!!** Rockstar [Object Oriented Programming](OOP.md)! Yes, you can write OOP code in Rockstar!

It requires at least Java8 JRE to run. The `rockstar.bat` and `rockstar` wrappers make it easy to execute on Windows and Unix.

### Install

To install and run, you only need the `./rocky.jar` file, and one of the wrappers (`rockstar.bat` or `rockstar`, depending on your OS). Everything else is for development.

There are some Rockstar program in the `programs` folder and its subfolders, you may want to check them, too. They're mostly for test purposes, though.

#### Docker Support

If you don't have Java on your local machine, you can create a docker image using the following command:
```
docker build -t rockstar .
``` 
Once created, you can use the container to run rocky. Here are some sample commands:
* Get help: `docker run --rm rockstar help`
* Run a program: `docker run --rm -v ${pwd}:/local rockstar /local/programs/gameoflife.rock`
* Run a program (with input): `docker run --rm -v ${pwd}:/local --interactive --tty rockstar /local/programs/modulus.rock`

### Usage

To run a program, it's simple:
```
$ ./rockstar programs/modulus.rock 
Enter Dividend:
100
Enter Divisor:
7
The modulus is 2
```
To list (and parse) a program, try `list` (note, that it is the parsed version, with indentations, to help better understanding. Whitespaces do not count in Rockstar)
```
$ ./rockstar list programs/modulus.rock

  Modulus takes Number and Divisor
    Put Divisor into Big Divisor
    While Big Divisor is less than Number
      Put Big Divisor times 2 into Big Divisor

    While Big Divisor is as high as Divisor
      If Number is as high as Big Divisor
        Put Number minus Big Divisor into Number

      Put Big Divisor over 2 into Big Divisor

    Give back Number

  say "Enter Dividend:"
  Listen to X
  say "Enter Divisor:"
  Listen to Y
  say "The modulus is " with Modulus taking X and Y
```

Time to get acquainted with Rockstar? Try the REPL (interactive) mode, start Rockstar with `-` option:

```
$ ./rockstar -
Rockstar Java by gaborsch, Version 0.99
---------------------------------------
Type 'exit' to quit, 'show' to get more info.
> My name is Gabor
> What's your name?
> Say my name
5
> show var
Variables:
what = 44
my name = 5
> exit
```
There are other possibilities as well, like debug mode (detailed description coming soon) or help for each command:
```
$ ./rockstar help
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
rockstar test [--options ...] <file-or-dirname> ...
    Execute unit tests. Special rules apply, check `rockstar help test` for details
rockstar [-h|--help]
rockstar help
    Print this help.
rockstar help <command>
    Print more detailed help about the given command.
```

Each command has a more detailed help with options, so try `rockstar help run`, `rockstar help debug`, `rockstar help repl`, etc.

### Test results

*100%* of the tests have passed! With some buggy test cases fixed locally ( https://github.com/RockstarLang/rockstar/issues/202 and https://github.com/RockstarLang/rockstar/issues/203 ), everything works!

Also, I included OOP tests that do not pass on other Rockstar implementations that don't have OOP feature.

```
$ ./rockstar test -v programs/tests/
PARSE_ERROR tests in programs/tests/failures
   [ OK ] invalid_comments.rock
CORRECT tests in programs/tests/fixtures/assignment
   [ OK ] compound_assignments.rock
   [ OK ] lets.rock
CORRECT tests in programs/tests/fixtures/comments
   [ OK ] complex_comments.rock
   [ OK ] simpleComments.rock
   [ OK ] simple_comment.rock
CORRECT tests in programs/tests/fixtures/conditionals
   [ OK ] empty_if.rock
   [ OK ] simpleConditionals.rock
   [ OK ] truthinessTest.rock
CORRECT tests in programs/tests/fixtures/constants
   [ OK ] constants.rock
CORRECT tests in programs/tests/fixtures/control-flow
   [ OK ] nested_loops.rock
   [ OK ] simpleLoops.rock
CORRECT tests in programs/tests/fixtures/equality
   [ OK ] booleans.rock
   [ OK ] equalityComparison.rock
   [ OK ] mysterious.rock
   [ OK ] negation.rock
   [ OK ] nothing.rock
   [ OK ] null.rock
   [ OK ] numbers.rock
   [ OK ] strings.rock
CORRECT tests in programs/tests/fixtures/examples
   [ OK ] 99_beers.rock
   [ OK ] factorial.rock
   [ OK ] fibonacci.rock
   [ OK ] fizzbuzz-idiomatic.rock
   [ OK ] fizzbuzz-minimalist.rock
CORRECT tests in programs/tests/fixtures/functions
   [ OK ] functionCalls.rock
   [ OK ] nested_functions.rock
   [ OK ] nested_function_scopes.rock
   [ OK ] recursion.rock
   [ OK ] simpleFunctions.rock
CORRECT tests in programs/tests/fixtures/io
   [ OK ] hello_number.rock
   [ OK ] hello_world.rock
   [ OK ] inputTest.rock
   [ OK ] inputTest2.rock
CORRECT tests in programs/tests/fixtures/literals
   [ OK ] literalAliases.rock
   [ OK ] literalstrings.rock
   [ OK ] poeticLiterals.rock
   [ OK ] poeticNumbers.rock
CORRECT tests in programs/tests/fixtures/math
   [ OK ] operators.rock
   [ OK ] operator_aliases.rock
   [ OK ] operator_precedence.rock
CORRECT tests in programs/tests/fixtures/oop
   [ OK ] abstract_method.rock
   [ OK ] class_declaration.rock
   [ OK ] constructor.rock
   [ OK ] field_declaration.rock
   [ OK ] field_visibility.rock
   [ OK ] instance_check.rock
   [ OK ] instantiation.rock
   [ OK ] method.rock
   [ OK ] method_override.rock
   [ OK ] parameterless_method.rock
   [ OK ] parent_constructor.rock
   [ OK ] parent_ref.rock
CORRECT tests in programs/tests/fixtures/operators
   [ OK ] addOperator.rock
   [ OK ] andTest.rock
   [ OK ] booleans.rock
   [ OK ] divisionOperator.rock
   [ OK ] incrementAndDecrement.rock
   [ OK ] list_expressions_arithmetic.rock
   [ OK ] multiplicationOperator.rock
   [ OK ] notTest.rock
   [ OK ] orderingComparison.rock
   [ OK ] orNorTest.rock
   [ OK ] subtractOperator.rock
CORRECT tests in programs/tests/fixtures/variables
   [ OK ] common_variables.rock
   [ OK ] globalVariables.rock
   [ OK ] poeticStrings.rock
   [ OK ] pronouns.rock
   [ OK ] proper_variables.rock
   [ OK ] simple_pronouns.rock
   [ OK ] simple_variables.rock
   [ OK ] umlauts.rock
CORRECT tests in programs/tests/fixtures/whitespace
   [ OK ] apostrophesIgnored.rock
   [ OK ] leading_blank_lines.rock
   [ OK ] leading_empty_lines.rock
   [ OK ] leading_whitespace.rock
   [ OK ] no_newline_at_eof.rock
   [ OK ] trailing_blank_lines.rock
   [ OK ] trailing_empty_lines.rock
RUNTIME_ERROR tests in programs/tests/runtime-errors
   [ OK ] abstract_instantiation.rock
   [ OK ] field_access_outside.rock
   [ OK ] undefined_method.rock
   [ OK ] undefined_method_from_object.rock

============================================================
Test results for programs/tests/:
============================================================
All tests:    83
Failed tests: 0
Passed tests: 83
Pass ratio:   100.00%
============================================================

```

