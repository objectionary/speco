<img alt="logo" src="https://www.objectionary.com/cactus.svg" height="100px" />

[![EO principles respected here](https://www.elegantobjects.org/badge.svg)](https://www.elegantobjects.org)
[![DevOps By Rultor.com](http://www.rultor.com/b/objectionary/speco)](http://www.rultor.com/p/objectionary/speco)
[![We recommend IntelliJ IDEA](https://www.elegantobjects.org/intellij-idea.svg)](https://www.jetbrains.com/idea/)

[![mvn](https://github.com/objectionary/speco/actions/workflows/mvn.yml/badge.svg?branch=master)](https://github.com/objectionary/speco/actions/workflows/mvn.yml)
[![PDD status](http://www.0pdd.com/svg?name=objectionary/speco)](http://www.0pdd.com/p?name=objectionary/speco)
[![codecov](https://codecov.io/gh/objectionary/speco/branch/master/graph/badge.svg)](https://codecov.io/gh/objectionary/speco)
[![Maven Central](https://img.shields.io/maven-central/v/org.eolang/speco.svg)](https://maven-badges.herokuapp.com/maven-central/org.eolang/speco)
[![Hits-of-Code](https://hitsofcode.com/github/objectionary/speco)](https://hitsofcode.com/view/github/objectionary/speco)
![Lines of code](https://img.shields.io/tokei/lines/github/objectionary/speco)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](https://github.com/objectionary/speco/blob/master/LICENSE.txt)

Specialization of objects in EO programs.

SPECO is a tool that is aimed to be launched on the EO program converted to a collection of .xmir files,
which has undergone modifications performed by [AOI](https://github.com/objectionary/aoi) tool.

Consider the following EO program:

```
[] > cat
  [] > talk
    QQ.io.stdout > @
      "Meow!"

[] > dog
  [] > talk
    QQ.io.stdout > @
      "Woof!"
  [] > eat
    QQ.io.stdout > @
      "I am eating"

[x] > pet1
  x.talk > @

[x] > pet2
  seq > @
    x.talk
    x.eat
```

The following block in the .xmir file of this program will be generated after AOI launch:

```
<aoi>
    <obj fqn="pet1.x">
       <inferred>
          <obj fqn="cat"/>
          <obj fqn="dog"/>
       </inferred>
    </obj>
    <obj fqn="pet2.x">
       <inferred>
          <obj fqn="dog"/>
       </inferred>
    </obj>
</aoi>
```

As we can see, object `x` from `pet1` is only used with its `talk` attribute, therefore it can either be
an instance of `cat` or `dog`. Whereas `x` located `pet2` is used with both `talk` and `eat`, which
lets us determine that `x` can only be an instance of `dog`.

Right now object `pet1` has only one implementation, which looks like below in xmir format.
It does not give any hints on what object `x` may be in this context.

```
<o abstract="" line="20" name="pet1" pos="0">
   <o line="20" name="x" pos="1"/>
   <o base="x" line="21" pos="2"/>
   <o base=".talk" line="21" name="@" pos="3"/>
</o>
```

SPECO makes it obvious what `x` is in the provided context. For example, it will turn object `pet1`
into these two declarations of objects `pet1_spec_x=cat` and `pet1_spec_x=dog`, which are specific
for `cat` and `dog` correspondingly.

```
<o abstract="" line="20" name="pet1_spec_x=cat" pos="0">
   <o line="20" name="x" pos="1" spec="cat"/>
   <o base="x" line="21" pos="2"/>
   <o base=".talk" line="21" name="@" pos="3"/>
</o>
```

```
<o abstract="" line="20" name="pet1_spec_x=dog" pos="0">
   <o line="20" name="x" pos="1" spec="dog"/>
   <o base="x" line="21" pos="2"/>
   <o base=".talk" line="21" name="@" pos="3"/>
</o>
```

It will generate a collection of modified .xmir files as an output.

## How to Contribute

Fork repository, make changes, send us a pull request.
We will review your changes and apply them to the `master` branch shortly,
provided they don't violate our quality standards. To avoid frustration,
before sending us your pull request please run full Maven build:

```bash
$ mvn clean install -Pqulice
```

You will need Maven 3.3+ and Java 8+.
