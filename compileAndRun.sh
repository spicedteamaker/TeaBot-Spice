#!/bin/bash

mvn clean compile assembly:single
java -cp target/TeaBot-Spice-0.0.1-SNAPSHOT-jar-with-dependencies.jar spiced.tea.cup.time.Main
