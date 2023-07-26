# Rocky Extension to The Rockstar Language Specification

Rockstars need freedom. Sometimes for necessities, sometimes just for fun, but a rocker always revolting against the rules or at least breaking some boundaries. So, Rocky offers some features that are not available in [the supported Rockstar specification](spec.md).

By default, Rocky recognises whether strict or extension mode should be allowed (from the presence of `import` or `album` statements). To explicitly enable Rocky features, add `-X` or `--rocky` as a command-line parameter. 

## Object Oriented Programming extension

There is a separate page to introduce [Rockstar Object-Oriented Programming](OOP.md).

## Native Java support

There is a separate page about [Native Java support](native_java.md) in Rocky.

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

The built-in function `sorted` creates a new array, that contains all the elements of the original array, ordered. Can be used in prefix or postfix format. 

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

As per the original Rockstar specification, Rocky fully supports DEC64 arithmetic with `--dec64` command line option (including rounding functions).

## BigDecimal Arithmetics

Rocky supports infinite-precision arithmetics using Java BigDecimal type with `--bigdecimal` command line option (including rounding functions).

## Aliasing

If you want to express yourself as you want, you can redefine many language elements using the `means` keyword:
```
Growl means Say
growl "Hello there!"
```

Aliasing operates on whole keyword level only. For example `throw back means give back` works, but `throw means give` does not.
All keywords in statements can be aliased. From expressions, it only works for the keyword `taking` currently (but may be extended). 

It's possible to define the same alias for different keywords, if their lexical position is different. For example:
```
telling means say
telling means taking
telling "Hello!" ("say")
Desire telling my love ("taking")
```

It is also allowed to redefine keywords (though not recommended), but it's not possible to redefine other aliases.


