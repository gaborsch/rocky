# Object Oriented Programming 

## Preface

Writing Object Oriented programs in Rockstar is quite simple. You can define classes, constructors, private fields, public methods, method overrides. 
You can instantiate a class with the constructor and call member methods. You can extend other classes and even define abstract methods to be implemented in child classes.
Within the member methods you can access the parent class methods and the object instance itself. If you have a variable, you can check its runtime class. 
All these are of course within the Rockstar limitations: no strong typing, no method overload, no null hassle (every declaration comes with assignment).

The approach is very much like a traditional OOP, without the interfaces. Of the OOP principles, all encapsulation, inheritance, abstraction and polymorphism are covered (the latter two are partially). 

## Let's start!

This small example uses the "parameterless method" syntax. The explanations are on the right side.

This sample class implements a simple sequence, holding a counter value and return the next value each time it is called. 

```
a sequence looks like nothing                 (class declaration, inherits nothing)
Let the counter be 0                          (field declaration works like any local variable declaration)
current takes nothing                         (method declaration works like any function declaration)
give back the counter                         (read access to field)
                                              (end of method "current")
next takes nothing                            (another method declaration)
Build the counter up                          (write access to field)
give back the counter                         (read access to field)
                                              (end of method "next")
                                              (end of class "a sequence")
ID will be a sequence                         (instantiation)
say next from ID                              (call the parameterless method on instance ID, output: "1")
say next from ID                              (output: "2")
say 2 times next from ID                      (output: "6")
say current from ID                           (output: "3")
```

Rocky has a few built-in classes provided under the `src/main/resources/rockstar-lib` folder, including an indexed linked list implementation that can be used as an alternative for native array implementation. Feel free to contribute your own classes!

## Features
The main characteristics of the OOP feature are below. How it works, what will be the user experience, what's covered and what's not. I tried to highlight the most relevant OOP questions as well.

### Object type:

A variable (and a value) can be of an `object` type. An object can have methods, fields, and possibly a constructor. The `object` type has the following operations:
* method call
* equality check
* dynamic type testing

There are no other operations defined. Specifically, object fields cannot be accessed using dereferencing.

### Package:

Each file may have its package declaration. The syntax is simple: `Album: core, and utils`. Actually the colon is optinal, as Rockstar skips it. In general the syntax is `Album: <list-of-identifiers>`. The `list-of-identifiers` is a list expression that contains Rockstar identifiers, that defines the package. Alternatively, an identifier may be a string constant, e.g. `Album: "core", "utils"` (also slash/separated, like `Album: "core/utils"`). Also, for convenience, an `and` expression is allowed, like `Album: core and utils` (note, that the comma is missing.)

The folder names are the identifier names (or string parts), lowercased, all non-alpha characters replaced with underscore (`_`). Similar rules apply to class names (see below).

### Importing, class loading:

To import a class, we can use the following syntax: `[[From/off] <package-name>] play <class-names>`. The `from/off` part is optional, and it denotes that the imported classes should be in the same package as the importing class.

```
from Core, and Utils play an array, a comparator
off Core play Maths
play a chain
```

It imports the classes identified by `<class-names>` from the defined package. There is a folder called `rockstar-libs` under `src/main/resources` that is the root of the system-defined classes, otherwise the local working directory is used to locate a class. When the class is imported first time, its file is located, and the body is run. The defined classes, functions and variables will be accessible globally. For example, it is possible to create a `Maths` class that defines a `Maths` globally available object instance that can be accessed for maths functions - a static instance.

### Class declaration: 

`<class-name> looks like <superclass>`
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

The method name `describe` should return the string representation of an object (if implemented), just like `toString()` in many languages.

### Instantiation:

`<variable> wants to be <class> [taking <constructor parameter list>]` (also `want to be`, `wanna be`, `will be`, `would be`). The instantiation always comes with an assignment. The `taking` part (with constructor parameters) is optional.
When instantiating, all fields are initialized and all methods are declared, in order of declaration (superclass first, then the subclass). Finally the constructor is called with the provided parameter list (or with empty list, if missing).

### Method call:

`<method> on <object-value> [taking <parameter-list>]`. Aliases for `on`: `by`, `in`, `to`, `from`, `for` or `near`. There are no multiple method calls (like `method2 on method1 on Object`).
The precedence of the method call is the highest, higher than the currently highest operator (function call).

In an object context, the instance methods and inherited methods can be called without qualifying the object (implicit `this` reference). Also, in an object context, `parent` (and its aliases) refer to the parent constructor.

Note, that `at` is reserved for array access in core Rockstar, thus cannot be used.

### Visibility:

All declared methods are public, there are no private or protected methods.
All fields of a class are protected (not visible from outside, but a subclass method can read and write them). 

### Override:

A subclass method declaration will override (and hide) a superclass method declaration, if the same name is defined. The superclass method is still accessible from the subclass using the `<method> on parent` special object reference.
A field declared in a subclass does not override the superclass field, instead, it uses that field from the superclass (only overwrites its value).

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

### Protected methods
Not all methods are useful for the user of the class, and it would also increase the readibility if only the public methods would be callable from outside. There's no need for private, package private, friend or whatever other visibility rules.

# Example code

* https://github.com/gaborsch/rocky/blob/master/src/main/resources/rockstar-lib/core/utils/an_array.rock
* https://github.com/gaborsch/rocky/blob/master/src/main/resources/rockstar-lib/core/utils/a_chain.rock
* https://github.com/gaborsch/rocky/blob/master/src/main/resources/rockstar-lib/core/utils/a_chainlink.rock

Some demo for the Chain / Array

```
from core, utils play an array, a chain

the rainbow would be a chain              (instantiation)

add to the rainbow taking "Red"            (method calls)
add to the rainbow taking "Orange"
add to the rainbow taking "Green"
add to the rainbow taking "Blue"
add to the rainbow taking "Violet"

shout last on the rainbow (Violet)
shout peek to the rainbow taking 2 (Green - index starts at 0)
shout peek to the rainbow taking 5 (mysterious - no such index)
say remove from the rainbow (Violet) (remove all elements, one by one)
say remove from the rainbow (Blue)
say size for the rainbow (3)        (3 elements left)
say remove from the rainbow (Green)
say remove from the rainbow (Orange)
say remove from the rainbow (Red)
say size for the rainbow (0)        (all elements removed)

x will be an array
set to x taking 3,"d"
say description on x
set to x taking 0,"a"
say description on x
set to x taking 6,"g"
set to x taking 3, mysterious
say description on x
```



















