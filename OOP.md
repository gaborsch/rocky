TODO:

Parent constructor

Chain/Array (plain OOP)
```
A Chain Link looks like nothing               (class declaration, inherits nothing)
  the morning is nowhere                      (field declaration)
  the evening is gone                         (field declaration)
  the load is nothing                         (field declaration)
  A Chain Link takes the burden               (function name equals to the class name is a constructor, one parameter here)
    put the burden into the load              (write to field)
                                              (end of constructor)
  sunray takes the burden                     (method declaration)
    put the burden into the load              (write to field)
    give back the burden                      (access to field)
                                              (end of method)
  the sunrise takes the sun                   (method declaration)
    let the morning be the sun                (write to field)
    if the sun is not gone
      the sunset on the sun taking self       (method call, "self" access)
    give back the sun
                                              (end of method)
  the sunset takes the sun                    (method declaration)
    let the evening be the sun                (write to field)
    give back the sun
                                              (end of method)
  the eclipse takes nothing                   (method declaration)
    the sunset on the evening taking nowhere  (method call, access to field)
    give back the evening
                                              (end of method)
  tomorrow takes nothing                      (method declaration)
    give back the morning                     (access to field)
                                              (end of method)
  the look takes nothing                      (method declaration)
    give back the load                        (access to field)
                                              (end of method)
                                              (end of class A Chain Link)
A Chain looks like nothing                                        (class declaration, inherits nothing, no constructor)
  the brave is nowhere                                            (field declaration)
  the coward is gone                                              (field declaration)
  the army is invincible                                          (field declaration)
  add takes the sword                                             (method declaration)
    the warrior wants to be the A Chain Link taking the sword     (call constructor with one parameter)
    if the coward is not gone                                     (access to field)
      the sunrise on the coward taking the warrior                (call method on instance with one parameter)
                                                                  (end if)
    let the coward be the warrior                                 (write to field)
    if the brave is nowhere                                       (access to field)
      let the brave be the warrior                                (write to field)
                                                                  (end if)
    build the army up                                             (write to field)
    give back the burden                                          (access to field)
                                                                  (end of method)
  remove takes nothing                                            (method declaration)
    if the coward is gone                                         (access to field)
      give back nothing
                                                                  (end if)
    let the burden be the look on the coward                      (call method on instance)
    let the coward be the eclipse on the coward                   (call method on instance)
    knock the army down                                           (write to field)
    give back the burden
                                                                  (end of method)
  size takes nothing                                              (method declaration)
    give back the army                                            (access to field)
                                                                  (end of method)
  first takes nothing                                             (method declaration)
    give back the look on the brave                               (access to field)
                                                                  (end of method)
  last takes nothing                                              (method declaration)
    give back the look on the coward                              (access to field)
                                                                  (end of method)
  peek takes the enemy                                            (method declaration)
    if nothing is as weak as the enemy and the enemy is weaker than the army
      let the warrior be the brave                                (object assignment)
      while the enemy is stronger than nothing
        let the warrior be the tomorrow on the warrior            (call method on instance)
knock the enemy down
                                                                  (end while)
      give back the look on the warrior                           (call method on instance)
                                                                  (end if)
    give back nothing
                                                                  (end of method)
                                                                  (end of class Chain)
Array looks like Chain
  set takes the enemy, the sword
    if nothing is as weak as the enemy 
      let New Wave be the enemy + 1
 if New Wave is stronger than the army                       (access to super class field)
         let the warrior be expand taking New Wave
      otherwise
        let the warrior be the brave
        while the enemy is stronger than nothing
          let the warrior be the tomorrow on the warrior          (call method on instance)
 knock the enemy down
                                                                  (end while)
                                                                  (end if)
 sunray on the warrior taking the sword                      (call method on instance)
 give back the sword
    otherwise
      give back nothing
                                                                  (end if)
                                                                  (end of method)
  get takes the enemy                                             (method declaration)
    give back peek taking the enemy
                                                                  (end of method)
                                                                  (end of class Chain)




the rainbow would be A Chain                (instantiation)
the flowers want to be A Chain              (instantiation, also "wants to be")
the rope wanna be A Chain                   (instantiation)

add on the rainbow taking "Red"            (method calls)
add on the rainbow taking "Orange"
add on the rainbow taking "Green"
add on the rainbow taking "Blue"
add on the rainbow taking "Violet"


shout last on the rainbow taking nothing (Violet)
shout peek on the rainbow taking 2 (Green - index starts at 0)
say remove on the rainbow taking nothing (Violet)
say remove on the rainbow taking nothing (Blue)
say size on the rainbow taking nothing (3)
``

Sequence
```
Sequence looks like nothing
Let the counter be 0                          	(field declaration)
next takes nothing                            	(parameterless method declaration)
Build the counter up                          	(write access to field)
give back the counter                         (read access to field)
(end of method next)
(end of class Sequence)
ID wants to be Sequence
say next on ID                    (output: "1")
say next on ID                    (output: "2")
say 2 of next on ID               (output: "6")
```


Object Pool
```
Pool looks like nothing
  the spring is nowhere	(instance used to create new instances, e.g. static instance)
  Pool takes the beginning	(constructor)
    let the spring be the beginning
let the hope be the conception at the beginning	(initialize the pool with one instance)
death on the hope!	(created instance put to the pool)

  the faith wants to be A Chain
  fetch takes nothing
    let remove from the faith be the hope
    if the hope is nothing
 give back conception in the spring

give back the hope
(eom)
  recycle takes the delusion
    death on the delusion
give back the delusion
(eom)
(eoc)
Pool Object looks like nothing
  my pool is nothing
  conception takes the water
    put the water into my pool 
    give back instantiate	(method should be defined in subclasses - "abstract method")
(eom)
  death takes nothing
    remove from my pool
    let my pool be empty
(eom)
(eoc)
```

Object Pool with factory
```
Pool looks like nothing
  the spring is nowhere	(instance used to create new instances, e.g. static instance)
  Pool takes the beginning	(constructor)
    let the spring be the beginning
let the hope be the conception at the beginning	(initialize the pool with one instance)
death on the hope!	(created instance put to the pool)

  the faith wants to be A Chain
  fetch takes nothing
    let remove from the faith be the hope
    if the hope is nothing
 give back conception in the spring

give back the hope
(eom)
  recycle takes the delusion
    death on the delusion
give back the delusion
(eom)
(eoc)
Factory looks like nothing
  my pool is nothing
  conception takes the water
    put the water into my pool 
    give back instantiate	(method should be defined in subclasses - "abstract method")
(eom)
  death takes nothing
    remove from my pool
    let my pool be empty
(eom)
(eoc)

Client looks like nothing
name is nobody
location is nowhere
username is nothing
password is nothing
(eoc)
Client Factory is like Factory
  instantiate takes nothing
     Tom wants to be Client
     give back Tom
(eom)
(eoc)
Client Pool is like Pool
  Client Pool takes nothing
    factory wants to be Client Factory
    Pool in parent taking factory
(eom)
(eoc)
```

Pool with factory (2)
```
Pool looks like nothing
  Pool takes nothing    	(constructor)
let the hope be the conception from myself    	(initialize the pool with one instance)
a return taking the hope	(created instance put to the pool)
(eom)	
  the faith wants to be A Chain
  a new takes nothing
    let remove from the faith be the hope
    if the hope is nothing
 let the hope be conception from myself	

give back the hope
(eom)
  a return takes the water
    add to the faith taking the water
give back the water
(eom)
  instantiate takes nothing                             (method should be defined in subclasses - "abstract method")
give back nothing
(eom)
(eoc)


Client looks like nothing	(methods not expressed here)
name is nobody
location is nowhere
username is nothing
password is nothing
(eoc)

Client Pool is like Pool
  instantiate takes nothing
     Tom wants to be Client
     give back Tom
(eom)
(eoc)
Clients will be Client Pool

let Tommy be a new from Clients
let Jane be a new from Clients

a return to Clients taking Tommy
```
----
So, Object Oriented Programming. 

It sounds a big scary thing for Rockstar, but the basic building blocks are already in there. Since we recently clarified and implemented the nested functions feature, we can define named blocks within named blocks, with local scope variables. The OOP concept described here is not too different from that, yet we can fare quite long with these features. It's not a full-fledge OOP (abstraction and polymorphism is not covered), but encapsulation and inheritance _is_ covered. Later we could refine the structure, if we really need them, it wouldn't take too much, but I prefer adding new concepts slowly.

Let me show you a small example (this uses the "parameterless method" syntax I proposed earlier):

```
Sequence looks like nothing                   (class declaration, inherits nothing)
Let the counter be 0                          (field declaration works like any local variable declaration)
current takes nothing                         (method declaration works like any function declaration)
give back the counter                         (read access to field)
                                              (end of method "next")
next takes nothing                            (method declaration )
Build the counter up                          (write access to field)
give back the counter                         (read access to field)
                                              (end of method "next")
                                              (end of class "Sequence")
ID wants to be Sequence                       (instantiation)
say next on ID                                (call the parameterless method on instance ID, output: "1")
say next on ID                                (output: "2")
say 2 times next on ID                        (output: "6")
say the counter on ID                         (attempt to access a field, but forbidden, output: "mysterious")
say current on ID                             (output: "3")
```

With classes we can write "system functions" library (collection of classes in a `system.rock` file or similar), that could be supplied with each interpreter/transpiler. These functions could be the core of the system, any Rockstar developer could easily contribute. I've already written an indexed linked list implementation that can be used as an alternative for native array implementation. I think that if we implement the OOP feature, we don't need to extend the language any more (the only exception could be the string handling, that can be implemented also with arrays, but would be much more effective natively).

I summarized the main characterisics of the OOP feature below. How it works, what will be the user experience, what's implemented and what's not. I tried to cover the most relevant OOP questions as well.

Object type:
A variable (and a value) can be an `object` type. An object has methods, fields, and possibly a constructor. There are no operations defined for an object type value other than the method calls, object fields cannot be accessed using dereferencing. 

Class declaration: 
`<class> looks like <superclass>`
Alias: `look like`. The class is terminated by an empty line (just like other blocks). A class name should follow the rules of a variable names (simple, common or proper name currently), however it is recommended to use proper names (i.e. starting with capital letters) Superclass name can be `nothing` meaning that there is no superclass.

Inheritance:
A class can inherit methods and fields from one class only (or from `nothing` or other aliases for `null` - meaning that there is no superclass). There are no multiple super classes nor traits.

Instantiation:
`<variable> wants to be <class>` (also `want to be`, `wanna be`, `will be`, `would be`). An instantiation always comes with an assignment.

Field declaration in a class:
Any field declared in the scope of the class will be treated as a local field. 

Method declaration in a class:
Any function declared in the scope of the class will be treated as a method on the class. 

Constructor declaration: 
`<classname> takes <constructor parameter list>`
The function named exactly as the class name will be used as constructor. The parameter list can be `nothing` or othes aliases of `null`, if there are no parameters.

Special object references:
`self` refers to the class itself. Aliases: `myself`, `yourself`, `himself`, `herself`, `itself`, `ourselves`, `yourselves`, `themselves`. Can be used within a class body.
`parent` refers to the superclass. Aliases: `father`, `mother`, `papa`, `mama`. Can be used within a subclass body. 

Method call:
`<method> on <object-value> [taking <parameter-list>]`. Aliases for `on`: `by`, `in`, `at`, `to`, `for`, `from` or `near`. There are no multiple method calls (like `method2 on method1 on Object`), so in this case the `<object-value>` should be interpreted as a variable reference.
The precedence of the method call is the highest, higher than the currently highest operator (function call).

Visibility:
All declared methods are public, there are no private or protected methods.
All fields of a class are protected (not visible from outside, but a subclass method can read and write them). 

Override:
A field declared in a subclass does not override the superclass field, instead, it uses that field from the superclass (overwrite its value).
A subclass method can override the superclass method, even change the signature.

Overload:
A subclass method declaration will overload (and hide) a superclass method declaration, if the same name is defined. The superclass method is still accessible from the subclass using the `<method> on parent` special object reference.

Static fields, static methods:
There are no static fields of methods in Rockstar. However, after defining a class, it is possible to create an instance as a global variable. By convention, the name of the global variable must be equal to the class name. This instance can be used as "static instance".
If a method in an ordinary instance wants to access the static methods, it must use the `on <classname>` clause.

Class type check:
There is no strong type check, only runtime checks. All object instances are treated equally, the referred method (function) is determined runtime, from the instance.

Abstraction, interfaces:
There are no abstract methods and abstract classes or interfaces. 

Anonymous classes:
There are no anonymous classes.

Garbage collection, destructors:
There is no garbage collection and there are no destructors. However, an object pool can be implemented























