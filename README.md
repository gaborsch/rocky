# Rocky - Rockstar Java interpreter

Rocky is a 100% [Rockstar](https://codewithrockstar.com/) compatible Rockstar 1.0 Interpreter, written in Java. Should you have any issues, requests, wishes or troubles, open a ticket for it.  This interpreter is _not compatible_ with Rockstar 2.0.

Rocky supports all Rockstar language features announced on 2021.10.12 and before. If you miss any feature, please open an issue for it.

**Features include:**
* Fully compliant Rockstar implementation to date (all of the tests are OK, except the reported bugs)
* Advanced Debugger mode (Step into/over/return/run, Breakpoints, watches, examine variable, trace expression evaluation!)
* Interactive mode (REPL - Read - Eval - Print Loop, with command `-`)
* IEEE754 maths (double precision, default mode), BigDecimal maths (unlimited precision, with option `--bigdecimal`) or Dec64 maths (with option `--dec64`)
* List command (parse a file without running it, `-x` option prints the Abstract Syntax Tree)
* Creating standalone executable (Java JRE 8+ still required)
* Detailed help with options explanation


**Special language features:** 
* [Object Oriented Programming](docs/OOP.md) in Rockstar! Yes, you can write OOP code in Rockstar!
* [Native Java](docs/native_java.md) binding - you can use any Java class or method (e.g. AWT graphics, File I/O, security, etc.) Also. using `FunctionalInterfaces` native Java code may call back to Rockstar!
* Aliases (substituting keywords)
* Advanced array management features

For all additional feature details, please check the [Rocky extra features page](docs/spec_ext.md).


## Prerequisites

Rocky requires at least Java8 JRE to run. Alternatively you can also use Docker to run Rocky.

## Install

The easiest way is to clone this Git repository, everything is prepared here. 

Minimalistic approach: To install and run, you only need the [`rocky.jar`](https://github.com/gaborsch/rocky/tree/master/rocky.jar)  file, and one of the wrappers ([`rockstar.bat`](https://github.com/gaborsch/rocky/tree/master/rockstar.bat) or [`rockstar`](https://github.com/gaborsch/rocky/tree/master/rockstar), depending on your OS). Make sure that `java` is on your path and you can run it immediately. The rest of the files are for development.

If you are on Linux, you can execute [`sudo install.sh`](https://github.com/gaborsch/rocky/tree/master/install.sh) - this will create a runnable binary `/usr/bin/rockstar`, so your Rockstar programs could be run like a script with `#!/usr/bin/rockstar` shebang header. 

There are some Rockstar program in the [programs](https://github.com/gaborsch/rocky/tree/master/programs) folder and its subfolders. They're mostly for test purposes, but you can peek into if you want to get inspired or check some features.

## Run

To run a program, simply add the program name(s) as parameters:

```
./rockstar programs/fizzbuzz.rock 
```

Running Rocky in Docker: Please refer the [Docker page](docs/Docker.md) for details.

## Commands

### Help

Self-explaining:

```
$ ./rockstar help
Rockstar Java by gaborsch, Version 2.1.0 (with OOP and native Java)
-------------------------------------------------------------------
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

### Run

The `run` command (may be omitted) executes one or more Rockstar programs. The files must have a `.rock` suffix, other files are skipped. For more details and options please check `rockstar help run`.

### Debug

One of the most interesting features in Rocky, enables interactive line-by-line tracing of code execution, investigating variables, etc. The full list of debug commands is below:

```
Debugger commands:
    5 or newline    Step Into
    6               Step Over (stop at line breakpoints)
    7               Step Return (stop at line breakpoints)
    8               Step Run (stop at line breakpoints)
    1 or x          Step Into Expression (print every step of the expression evaluation)
    X               Turns the Step Into mode sticky (X again will turn it off)
    a               Show accessible aliases
    b [linenum]     Add line breakpoint, default: current line
    br [linenum]    Remove line breakpoint, default: current line
    bl              List breakpoints
    s [<variable>]  Show variable (no expressions are possible). Default: show all variables
                    Showing an Object by name lists its properties
    w <variable>    Watch variable (no expressions). Watches evaluated before every statement.
    wr <variable>   Remove watch. '#1' refers to the first watch
    .               Prints the current line again (no step)
    list            Lists the current program
    exit            Exits the debugger
```

### List

List is useful to parse a file without executing it. Normally it lists the Rockstar file, with indentations:

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

With `-x` option, it lists the AST (Abstract Syntax Tree) - the result of the parsing 
### REPL mode - Read-Eval-Print-Loop

The REPL (interactive) mode enables you to try out Rockstar features. Start Rocky with `rockstar repl` or `rockstar -`:

```
$ ./rockstar -
Rockstar Java by gaborsch, Version 2.1.0 (with OOP and native Java)
-------------------------------------------------------------------
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


### Testing

Rocky has a built-in test framework, which can be used to test against the defined test cases. For tests, special rules apply, please read it through after running `rockstar help test`. The normal tests can be located under ([`programs/tests`](https://github.com/gaborsch/rocky/tree/master/programs/tests) folder.

```
$ ./rockstar test programs/tests/

============================================================
Test results for programs/tests/:
============================================================
All tests:    132
Failed tests: 0
Passed tests: 132
Pass ratio:   100.00%
============================================================
```

## Creating an executable

On Linux-like platforms (also including Git Bash, for example), it is possible to create a standalone executable. The created file includes Rocky, the `rockstar-lib` sources, and also the files you want to run. The syntax is as follows:

```
 pack.sh mainfile.rock [file.rock ...]
```

Example:

```
$ echo "Say \"Hello, Rockstar\"" >hello.rock
$ cat hello.rock
Say "Hello, Rockstar"
$ ./pack.sh hello.rock
Source file: hello.rock
Executable file 'hello' created
$ ./hello
Hello, Rockstar
```
