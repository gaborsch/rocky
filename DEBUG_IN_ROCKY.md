# How to develop in Rocky?

What makes Rocky Rockstar interpreter unique is the features how it supports development. These include

- Play around with Rockstar interactively (REPL mode - Read-Eval-Print loop)
- List and interpret your program without running it
- Interactively debug your program, almost like in your IDE

## REPL - play interactively

If you are curious what's going to happen if you write a certain syntax, try it with the REPL mode. 
The `-x` option instructs Rockstar to explain every instruction you enter.

```
$ ./rockstar - -x
Rockstar Java by gaborsch, Version 0.99
---------------------------------------
Type 'exit' to quit, 'show' to get more info.
Tommy was a lovestruck ladykiller                   <-- Enter your Rockstar code, press Enter
tommy := 100                                        <-- with -x option every command is explained
Jenny was loyal                                     <-- Enter another line 
jenny := 5                                          <-- 
show vars                                           <-- the 'show vars' command lists all defined variables
Variables:                                          <-- 
tommy = 100                                         <-- Variable 1
jenny = 5                                           <-- Variable 2
let Tommy be with Jenny                             <-- OK, try something more complex
tommy := (tommy + jenny)                            <-- expressions are explained, too
say Tommy                                           <-- print command
print tommy                                         <-- explained
105                                                 <-- and the result is also printed
exit                                                <-- exiting REPL mode
```

## Finding compile errors

OK, you have a program, the famous FizzBuzz developed. Here it is:
```
$ cat programs/fizzbuzz_minimalist.rock
Modulus takes Number and Divisor
While Number is as high as Divisor
Put Number minus Divisor into Number

Give back Number

Limit is 100
Counter is 0
Fizz is 3
Buzz is 5
Until Counter is Limit
Build Counter up
If Modulus taking Counter, Fizz is  and Modulus taking Counter, Buzz is 0
Say "FizzBuzz!"
Continue

If Modulus taking Counter and Fizz is 0
Say "Fizz!"
Continue

If Modulus taking Counter and Buzz is 0
Say "Buzz!"
Continue

Say Counter 
```


