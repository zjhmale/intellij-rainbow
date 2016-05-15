## Rainbow plugin

https://plugins.jetbrains.com/plugin/8214

* Rainbow highlighting identifiers based on their names. Each identifier gets a color based on a hash of its name.
* Rainbow highlighting delimiters such as parentheses, brackets or braces according to their depth. not just in Lisp.

## Screenshots

* for dark themes

![](./pics/dark.png)

* for light themes

![](./pics/light.png)

## Configuration

![](./pics/settings.png)

## Support Languages

* C family (C/C++/ObjectiveC)
* Java
* JavaScript (IDEA Ultimate and integrate with JavaScript Support plugin)
* Kotlin (IDEA 14+)
* Clojure ([Cursive](https://cursive-ide.com/) already support Rainbow Delimiter)
* Python
* Haskell
* Agda
* Rust (hack it use plugin sdk with JDK8 and IDEA 15+ instead of JDK6 and IDEA 14+ as usual)
* Erlang
* Scala
* Go
* Groovy
* Ruby (IDEA Ultimate and integrate with Ruby Support plugin)
* Elixir

## Development

* `./gradlew runIdea`
* `./gradlew buildPlugin`
* `./gradlew test`

## License

Copyright © 2016 zjhmale

Released under the terms of the MIT License
