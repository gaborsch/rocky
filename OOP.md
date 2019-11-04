# Object Oriented Programming. 

It sounds a big scary thing for Rockstar, but the basic building blocks are already in there. Because we recently clarified and implemented the _nested functions_ feature, we can define named blocks within named blocks, with local scope variables. The OOP concept described here is not too different from that, and we can fare quite long with these features. The OOP described below is not a full-fledged OOP, but encapsulation, inheritance, abstraction and polymorphism is covered (the latter two is partially). 

Let me show you a small example (this uses the "parameterless method" syntax I proposed earlier):

```
Sequence looks like nothing                   (class declaration, inherits nothing)
Let the counter be 0                          (field declaration works like any local variable declaration)
current takes nothing                         (method declaration works like any function declaration)
give back the counter                         (read access to field)
                                              (end of method "next")
next takes nothing                            (another method declaration)
Build the counter up                          (write access to field)
give back the counter                         (read access to field)
                                              (end of method "next")
                                              (end of class "Sequence")
ID wants to be Sequence                       (instantiation)
say next on ID                                (call the parameterless method on instance ID, output: "1")
say next on ID                                (output: "2")
say 2 times next on ID                        (output: "6")
say current on ID                             (output: "3")
```

With classes we can write "system functions" library (collection of classes in a `system.rock` file or similar), that could be supplied with each interpreter/transpiler. These functions could be the core of the system, any Rockstar developer could easily contribute. I've already written an indexed linked list implementation that can be used as an alternative for native array implementation. I think that if we implement the OOP feature, we don't need to extend the language any more (the only exception could be the string handling, that can be implemented also with arrays, but would be much more effective natively).

## Features
I summarized the main characterisics of the OOP feature below. How it works, what will be the user experience, what's implemented and what's not. I tried to cover the most relevant OOP questions as well.

### Object type:

A variable (and a value) can be an `object` type. An object can have methods, fields, and possibly a constructor. There are no operations defined for an object type value other than the method call. Object fields cannot be accessed using dereferencing.

### Class declaration: 

`<class> looks like <superclass>`
Alias: `look like`. The class is terminated by an empty line (just like other blocks). A class name should follow the rules of a variable names (simple, common or proper name currently), however it is recommended to use proper names (i.e. starting with capital letters) Superclass name can be `nothing` (or any other aliases for `null`) meaning that there is no superclass.

### Inheritance:

A class can inherit methods and fields from one class only (or from `nothing` or other aliases for `null` - meaning that there is no superclass). There are neither multiple superclasses nor traits.

### Field declaration in a class:

Any variable declared in the scope of the class will be treated as a local field. All fields should be declared.

### Method declaration in a class:

Any function declared in the scope of the class will be treated as a method on the class.

### Constructor declaration: 

`<classname> takes <constructor parameter list>`
The function named exactly as the class name will be used as constructor. The parameter list can be `nothing` or othes aliases of `null`, if there are no parameters. There can be only one constructor for each class.

### Special references:

The object name `parent` in a method call refers to the method accessible from the superclass (that can be declared in the parent class, grandparent class, or even higher). Aliases to `parent`: `father`, `mother`, `papa`, `mama`. 
The method name `parent` refers to the parent object constructor.

The variable name `self` refers to the innermost object context (also known as "this"). Aliases: `myself`, `yourself`, `himself`, `herself`, `itself`, `ourselves`, `yourselves`, `themselves`.

### Instantiation:

`<variable> wants to be <class> [taking <constructor parameter list>]` (also `want to be`, `wanna be`, `will be`, `would be`). The instantiation always comes with an assignment. The `taking` part (with constructor parameters) is optional.
When instantiating, all fields are initialized and all methods are declared, in order of declaration (superclass first, then the subclass). Finally the constructor is called with the provided parameter list (or with empty list, if missing).

### Method call:

`<method> on <object-value> [taking <parameter-list>]`. Aliases for `on`: `by`, `in`, `at`, `to`, `from`, `for` or `near`. There are no multiple method calls (like `method2 on method1 on Object`).
The precedence of the method call is the highest, higher than the currently highest operator (function call).

In an object context, the instance methods and inherited methods can be called without qualifying the object (implicit `this` reference). Also, in an object context, `parent` (and its aliases) refer to the parent constructor.

### Visibility:

All declared methods are public, there are no private or protected methods.
All fields of a class are protected (not visible from outside, but a subclass method can read and write them). 

### Override:

A subclass method declaration will override (and hide) a superclass method declaration, if the same name is defined. The superclass method is still accessible from the subclass using the `<method> on parent` special object reference.
A field declared in a subclass does not override the superclass field, instead, it uses that field from the superclass (only overwrites its value).

### Overload:

Just like with the functions, it is not possible to overload a method, because only the method name is checked.

### Static fields, static methods:

There are no static fields of methods in Rockstar. However, after defining a class, it is possible to create an instance as a global variable. By convention, the name of the global variable should be equal to the class name. This instance can be used as "static instance".
If a method in an ordinary instance wants to access the static methods, it can use the `on <classname>` clause - but the static field values are not shared among instances.

### Abstract methods:
A method with an empty body is considered abstract. A non-abstract method must have at least a `Give back nothing` statement.

### Abstract classes, interfaces:
A class that has abstract methods is considered and abstract class. It is not possible to instantiate an abstract class.

There are no interfaces in Rockstar, however we can define classes that have abstract methods only - an absolutely abstract class. These have no special meanings, as long as there is no multiple inheritance.

### Runtime instance check:

The `<instance> is a kind of <classname>` operation can be used to check if the instance implements a certain class. Alias: `<instance> is like <classname>`. Inherited classes also count.

### Inner classes:

All created classes are of global scope. It is possible to create a class within a method or a class body, but these will be accessible outside of their defining scope as well. (This may change in the future.)

### Anonymous classes:

There are no anonymous classes.

### Garbage collection, destructors, finalizers:

There is no garbage collection and there are no destructors. However, an object pool can be implemented.

## Possible future extensions:

### Interface implementation, multiple inheritance:
It is possible to inherit methods from multiple classes or interfaces, by having a list expression for class or interface names. The first one is the strongest, it overrides all methods of the rest of the classes, the last ones should be the interfaces (if any). We could make a rule that the first one must be a class (or `nothing`), the others must be interfaces, to make it undertandable and implementable.

### Inner classes
Proper visibility for the inner classes would make them more useful.

# Example code
--- 

Chain/Array
```
Chain Link looks like nothing                 (class declaration, inherits nothing)
  the morning is nowhere                      (reference to previous)
  the evening is gone                         (reference to next)
  the load is nothing                         (the payload)
  Chain Link takes the burden                 (constructor with the payload)
    put the burden into the load              
                                              (end of method)
  the ray takes the burden                    ("set")
    put the burden into the load              
    give back the burden                      
                                              (eom)
  the sunrise takes the sun                   (attach to previous)
    let the morning be the sun                
    if the sun is not gone                    (call "attach to next" on the previous)
      the sunset on the sun taking self       (method call, "self" access)

    give back the sun
                                              (eom)
  the sunset takes the sun                    (attach to next)
    let the evening be the sun                 
    give back the sun
                                              (eom)
  the eclipse takes nothing                   (clearNext)
    let tonight be the evening
    let the evening be nowhere                
    give back tonight
                                              (end of method)
  tomorrow takes nothing                      (getNext)
    give back the morning                     
                                              (eom)
  the look takes nothing                      (getPrevoius)
    give back the load                        
                                              (eom)
                                              (end of class Chain Link)

Chain looks like nothing                                          (class declaration, inherits nothing, no constructor)
  the brave is nowhere                                            
  the coward is gone                                              
  the army is invincible                                          
  add takes the sword                                             (add to the list)
    the warrior wants to be Chain Link taking the sword           (initialize a container with the vale)
    if the coward is not gone                                     
      the sunrise on the coward taking the warrior                (append to the tail element)
                                                                  (end if)
    let the coward be the warrior                                 (this is the tail)
    if the brave is nowhere                                       
      let the brave be the warrior                                (set the list head, if not set)
                                                                  (end if)
    build the army up                                             (increase size)
    give back the burden                                          
                                                                  (eom)
  remove takes nothing                                            (remove the last, give back its value)
    if the coward is gone                                         (if the list is empty)
      give back nothing
                                                                  (end if)
    let the burden be the look on the coward                      (get the value from the last)
    let the coward be the eclipse on the coward                   (remove the last)
    knock the army down                                           (decrease size)
    give back the burden
                                                                  (eom)
  size takes nothing                                              (size)
    give back the army                                            
                                                                  (eom)
  first takes nothing                                             (first value)
    give back the look on the brave                               
                                                                  (eom)
  last takes nothing                                              (last value)
    give back the look on the coward                              
                                                                  ((eom))
  peek takes the enemy                                            (get a value at given index)
    if nothing is as weak as the enemy and the enemy is weaker than the army    (check index bounds)
      let the warrior be the brave                                (iterate through the containers)
      while the enemy is stronger than nothing
        let the warrior be tomorrow on the warrior                (next container)
        knock the enemy down
                                                                  (end while)
      give back the look on the warrior                           (return the value)
                                                                  (end if)
    give back mysterious
                                                                  (eom)
                                                                  (end of class Chain)
Array looks like Chain
  set takes the enemy, the sword                                  (set: index, value)
    if nothing is as weak as the enemy                            (if the index is non-negative)
      let the ghost be the enemy
      build the ghost up
      if the ghost is stronger than the army                      (we need to expand)
        while the ghost is stronger than the army                 (expand until we have the limit)
           add taking mysterious                                  (initialize the skipped indexes)
                                                                  (end while)
        let the warrior be the coward
      otherwise                                                   (we need to find the proper element)
        let the warrior be the brave
        while the enemy is stronger than nothing
          let the warrior be tomorrow on the warrior              (next)
          knock the enemy down
                                                                  (end while)
                                                                  (end if)
      the ray on the warrior taking the sword                     (set the value for the current)
      give back the sword
    otherwise
      give back nothing
                                                                  (end if)
                                                                  (eom)
  get takes the enemy                                             (get function)
    give back peek taking the enemy
                                                                  (eom)
  description takes nothing                                       (formatted output)
    let the show be "["
    let the warrior be the brave
    while the warrior is not gone
      let the show be with the look on the warrior
      if the warrior is not the coward
        let the show be with ", "
      (end if)
      let the warrior be tomorrow on the warrior
    (end while)
    give back the show with "]"
                                                                  (eom)
                                                                  (end of class Array)
```

Some demo for the Chain / Array
```
the rainbow would be Chain                (instantiation)

add on the rainbow taking "Red"            (method calls)
add on the rainbow taking "Orange"
add on the rainbow taking "Green"
add on the rainbow taking "Blue"
add on the rainbow taking "Violet"

shout last on the rainbow (Violet)
shout peek on the rainbow taking 2 (Green - index starts at 0)
shout peek on the rainbow taking 5 (mysterious - no such index)
say remove on the rainbow (Violet) (remove all elements, one by one)
say remove on the rainbow (Blue)
say size on the rainbow (3)        (3 elements left)
say remove on the rainbow (Green)
say remove on the rainbow (Orange)
say remove on the rainbow (Red)
say size on the rainbow (0)        (all elements removed)

x will be Array
set on x taking 3,"d"
say description on x
set on x taking 0,"a"
say description on x
set on x taking 6,"g"
set on x taking 3, mysterious
say description on x
```



















