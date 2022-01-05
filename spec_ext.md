# Rocky Extension to The Rockstar Language Specification

Rockstars need freedom. Sometimes for necessities, sometimes just for fun, but a rocker always revolting against the rules or at least breaking some boundaries. So, Rocky offers some features that are not available in [the supported Rockstar specification](spec.md).

By default, Rocky recognises whether strict or extension mode should be allowed (from the presence of `import` or `album` statements). To explicitly enable Rocky features, add `-X` or `--rocky` as a command-line parameter. 

## Object Oriented Programming extension

There is a [separate page](OOP.md) to introduce Rockstar OOP.

## Native Java support

Rocky has a Rockstar-to-Java binding, that is using the [Rocky OOP](OOP.md) syntax, but the underlying classes are provided by Java. The Java wrapping is using a new value type - NATIVE - to represent Java classes and instances. Only `public` methods and properties can be accessed.

Scalar values (number, String, boolean, null) are implicitly converted to Java and from Java, as well. Array values may be converted to Java implicitly, too, but it is possible to specify the exact Java representation with casting.

Importing (`from java, util play ArrayList`) creates a new global variable with the class name (`ArrayList` in this case) that allows access to static methods and properties. 

Instantiation (`l will be ArrayList [taking Parameter]`) creates a new variable (`l`) using a previously imported class (`ArrayList`), the constructor parameters (if any) should follow the `taking` keyword. 

Method calling and property access uses similar syntax: `method on object [taking parameter, ...]` and `property on object` respectively. If there are multiple method signatures, the first matching method is used. Method and property names are not case sensitive.

Return values are retained as-is (and wrapped into a Rockstar NATIVE), unless a primitive value is returned (number, String, null). `Void` is converted to `mysterious`. 

Type check (`l is like List`) is possible, including super classes and implemented interfaces. Even two classes or two instances can be checked against each other (`Linkedlist is like List` or `l is like l2`). Negation is also allowed (`l is not like l2`).

### Conversion of types

It is possible to cast a Rockstar array to a native object with a specific type. 

```
from java, lang play Long, Double, String, Short
from java, util play List, LinkedList, Arrays, Map

Let V be 12.3
Cast V with Short (V will be a native object of type Short)

rock X with 1, 2, 3
cast X into ArrayListOfLongs with List, Long              (ArrayList<Long>)
cast X into LinkedListOfDoubles with LinkedList, Double   (LinkedList<Double>)
cast X into ArrayOfStrings with Arrays, String            (String[])

let M at "one" be 1
let M at "two" be 2
cast M into HashMapOfStringAndLong with Map, String, Long (HashMap<String,Long>)
```
The above statements cast Rockstar Arrays into native Java objects. `List` defaults to `ArrayList`, `Map` defaults to `HashMap`. Converting to a Java array is done with casting to `Arrays`. It is even possible to cast to complex types, e.g. `cast X with List, Map, List, String, List, Short` results a value with `ArrayList<HashMap<ArrayList<String>,ArrayList<Short>>>`

Similarly, returned non-primitive values must be converted back if we want to access them within Rockstar. These types include `BigDecimal`, `BigInteger`, all `List`, `Map` and array types.

```
cast V (V is cast back - note that the value is 12 now)
cast ArrayListOfLongs into X2        (X2 is a Rockstar array)
cast ArrayOfStrings into X3          (X2 is a Rockstar array)
cast HashMapOfStringAndLong into M2  (X2 is a Rockstar associative array)
```

## Array extensions

### Iteration

Array iteration is the extension of the `while` loop. There are two similar statements for list iteration (`while ... alike ...`) and associative array iteration (`while ... alike ... for ...`).
Aliases: `while ... as ...` and `while ... as ... for ...`

```
('the army' is an integer-indexed array)
While the army alike my warrior
  ('my warrior' is the current value)
```
The cycle traverses through all array elements in increasing index order.

```
('the members' is an associative array)
While the members alike the man for the role
  ('the role' is the key, 'the man' is the value)
```
The cycle traverses through all key-value elements in increasing key order. Keys are compared according to the default comparison rules (e.g. string are alphabetically increasing, numbers are arithmetically increasing, etc).

### Array sorting

The built-in function `sorted` creates a new array, and adds the elements of the original array in that order. Can be used in prefix or postfix format. 

The following lines are identical:
```
('the colors' is an integer-indexed array)
Let the rainbow be sorted the colors
Let the rainbow be the colors sorted
```

### Array size

The built-in function `count of` returns the number of elements in an array. Aliases: `length of`, `height of`.

```
Say height of the rainbow
```

### Last element of an array ("peek")

The built-in function `last of` returns the last element in an array.
```
Say last of the colors
```

## DEC64 Arithmetics

As per the original Rockstar specification, Rocky fully supports DEC64 arithmetic with the `--dec64` command line option (including rounding functions).

## Aliasing

So, you want to express yourself as you want? You can redefine many language elements using the `means` keyword:
```
Growl means Say
growl "Hello there!"
```

Aliasing operates on whole keyword lists level only. For example `throw back means give back` works, but `throw means give` does not.
All keywords in statements can be aliased. From expressions, it only works for the keyword `taking` currently (but may be extended). 

It's possible to define the same alias for different keywords, if their lexical position is different. For example:
```
telling means say
telling means taking
telling "Hello!" ("say")
Desire telling my love ("taking")
```

It is also allowed to redefine keywords (though not recommended), but it's not possible to redefine other aliases.


