# TeaBot Spice
Teabot is a small helper bot that has one purpose in life: Tea serving. Teabot keeps track of important daily times (tea orders), and notifies you when your order is ready
### Important
Teabot is currently unstable, her code requires maintentence and may break. Please use her with care
### Dependencies
Linux - No compatibility with Windows, mac OS untested

Notify-send - To send messages to the desktop

Openjdk8 & JFX - Compiling and running

Maven - Compiling

### Installation
Allow the compileAndRun script to be executed:
```
$ chmod + x compileAndRun
```
Or simply compile:
```
$ mvn clean compile assembly:single
```
And finally run:
```
$ java -cp target/TeaBot-Spice-0.0.1-SNAPSHOT-jar-with-dependencies.jar spiced.tea.cup.time.Main
```
Optionally, you can run it as a background process
```
$ nohup java -cp target/TeaBot-Spice-0.0.1-SNAPSHOT-jar-with-dependencies.jar spiced.tea.cup.time.Main &
```


